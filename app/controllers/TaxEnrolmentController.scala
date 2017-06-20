/*
 * Copyright 2017 HM Revenue & Customs
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

import connectors.TaxEnrolmentConnector
import play.api.Logger
import play.api.mvc._
import uk.gov.hmrc.play.http.HeaderCarrier
import uk.gov.hmrc.play.microservice.controller.BaseController

import scala.concurrent.ExecutionContext.Implicits.global

class TaxEnrolmentController extends BaseController {

  implicit val hc:HeaderCarrier = new HeaderCarrier
  val connector: TaxEnrolmentConnector = TaxEnrolmentConnector

  def getSubscriptionsForGroupId(groupId: String): Action[AnyContent] = Action.async { implicit request =>
    connector.enrolmentStatus(groupId)(hc).map {
      response =>
        Logger.info(s"The connector has returned ${response.status} for $groupId")
        Results.Status(response.status)(response.body)
    }
  }

  def subscribe(subscriptionId: String): Action[AnyContent] = Action.async { implicit request =>
    connector.subscribe(subscriptionId, request.body.asJson.get)(hc).map {
      response =>
        Logger.info(s"The connector has returned ${response.status} for $subscriptionId")

        response.status match {
          case NoContent.header.status => NoContent
          case _ => InternalServerError
        }
    } recover {
      case _ => InternalServerError
    }
  }

}