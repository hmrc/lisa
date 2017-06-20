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

import connectors.DesConnector
import play.api.Logger
import play.api.mvc._
import uk.gov.hmrc.play.http.HeaderCarrier
import uk.gov.hmrc.play.microservice.controller.BaseController

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ROSMController extends BaseController {

  val connector: DesConnector = DesConnector

  def register(utr: String) = Action.async { implicit request =>
    performRegister(utr)(request)

  }

  def performRegister(utr: String)(implicit request:Request[AnyContent]): Future[Result] = {
    connector.register(utr, request.body.asJson.get).map {
      response =>
        Logger.info(s"The connector has returned ${response.status} for ${utr}")
        Results.Status(response.status)(response.body)
    }
  } recover {
    case _ => InternalServerError("""{"code":"INTERNAL_SERVER_ERROR","reason":"Dependent systems are currently not responding"}""") }


  def submitSubscription(utr: String,lisaManagerRef:String)  = Action.async { implicit request =>
    connector.subscribe(lisaManagerRef, request.body.asJson.get).map {
      response =>
        Logger.info(s"submitSubscription : Response from Connector ${response.status} for ${utr}")
        Results.Status(response.status)(response.body)
    }recover {
      case _ => InternalServerError("""{"code":"INTERNAL_SERVER_ERROR","reason":"Dependent systems are currently not responding"}""") }
  }


}

//object ROSMController extends ROSMController
