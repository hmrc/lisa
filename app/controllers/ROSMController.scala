/*
 * Copyright 2018 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package controllers

import config.AppConfig
import javax.inject.Inject
import connectors.{DesConnector, TaxEnrolmentConnector}
import play.api.Logger
import play.api.libs.json.{JsValue, Json}
import play.api.mvc._
import uk.gov.hmrc.auth.core.AuthProvider.GovernmentGateway
import uk.gov.hmrc.auth.core.{AffinityGroup, AuthConnector, AuthProviders, AuthorisedFunctions}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.bootstrap.controller.BaseController

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.control.NonFatal

class ROSMController @Inject() (override val authConnector: AuthConnector,
                                connector: DesConnector,
                                enrolmentConnector: TaxEnrolmentConnector,
                                appConfig: AppConfig) extends BaseController with AuthorisedFunctions {

  def register(utr: String): Action[AnyContent] = Action.async { implicit request =>
    authorised(AffinityGroup.Organisation and AuthProviders(GovernmentGateway)){
      performRegister(utr)(request)
    } recover {
      case _ => Unauthorized
    }
  }

  private def performRegister(utr: String)(implicit request:Request[AnyContent]): Future[Result] = {
    connector.register(utr, request.body.asJson.get).map { response =>
      Logger.info(s"The connector has returned ${response.status} for $utr")

      Results.Status(response.status)(response.body)
    } recover {
      case NonFatal(ex: Throwable) => {
        Logger.warn(s"performRegister: Failed - ${ex.getMessage}")
        InternalServerError("""{"code":"INTERNAL_SERVER_ERROR","reason":"Dependent systems are currently not responding"}""")
      }
    }
  }

  def submitSubscription(utr: String, lisaManagerRef:String): Action[AnyContent] = Action.async { implicit request =>
    authorised(AffinityGroup.Organisation and AuthProviders(GovernmentGateway)) {
      val requestJson: JsValue = request.body.asJson.get
      connector.subscribe(lisaManagerRef, requestJson).flatMap { response =>
        Logger.info(s"submitSubscription: Response from Connector ${response.status} for $utr")

        response.status match {
          case ACCEPTED => {
            val success = Results.Status(response.status)(response.body)
            val safeId = (requestJson \ "safeId").as[String]
            val subscriptionId = (response.json \ "subscriptionId").as[String]

            Logger.info(s"submitSubscription: calling Tax Enrolments with subscriptionId $subscriptionId and safeId $safeId")
            submitTaxEnrolmentSubscription(subscriptionId, safeId, success)
          }
          case _ => throw new RuntimeException(s"ROSM subscription failed. Returned a response status of ${response.status} for zref $lisaManagerRef")
        }
      } recover {
        case NonFatal(ex: Throwable) => {
          Logger.warn(s"submitSubscription: Failed - ${ex.getMessage}")
          InternalServerError("""{"code":"INTERNAL_SERVER_ERROR","reason":"Dependent systems are currently not responding"}""")
        }
      }
    } recover {
      case _ => Unauthorized
    }
  }

  private def submitTaxEnrolmentSubscription(subscriptionId: String, safeId: String, success: Result)(implicit hc: HeaderCarrier): Future[Result] = {
    val enrolmentRequest = Json.obj("serviceName" -> "HMRC-LISA-ORG", "callback" -> appConfig.rosmCallbackUrl, "etmpId" -> safeId)

    enrolmentConnector.subscribe(subscriptionId, enrolmentRequest)(hc).map { enrolRes =>
      Logger.info(s"submitSubscription: Tax Enrolments: Response from Connector ${enrolRes.status} for $subscriptionId")

      enrolRes.status match {
        case NO_CONTENT => success
        case _ => throw new RuntimeException(s"Tax Enrolment subscription failed. Returned a response status of ${enrolRes.status} for subscriptionId $subscriptionId and safeId $safeId")
      }
    }
  }

  def subscriptionCallback: Action[AnyContent] = Action.async { request =>
    val logDetails =
      s"method = ${request.method}" +
      request.getQueryString("subscriptionId").map(id => s", subscriptionId = ${id}").getOrElse("") +
      request.body.asJson.map(js => s", json = ${js.toString}").getOrElse("")

    Logger.warn(s"Received ROSM subscription callback: $logDetails")

    Future.successful(NoContent)
  }

}