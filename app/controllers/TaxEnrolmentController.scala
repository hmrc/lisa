/*
 * Copyright 2021 HM Revenue & Customs
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
import javax.inject.Inject
import play.api.Logger
import play.api.mvc._
import uk.gov.hmrc.auth.core.AuthProvider.GovernmentGateway
import uk.gov.hmrc.auth.core.{AffinityGroup, AuthConnector, AuthProviders, AuthorisedFunctions}
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

import scala.concurrent.ExecutionContext.Implicits.global

class TaxEnrolmentController @Inject() (override val authConnector: AuthConnector,
                                        connector: TaxEnrolmentConnector,
                                        val cc: ControllerComponents) extends BackendController(cc: ControllerComponents) with AuthorisedFunctions {

  def getSubscriptionsForGroupId(groupId: String): Action[AnyContent] = Action.async { implicit request =>
    authorised(AffinityGroup.Organisation and AuthProviders(GovernmentGateway)) {
      connector.enrolmentStatus(groupId)(hc).map {
        response =>
          Logger.logger.info(s"The connector has returned ${response.status} for $groupId")
          Results.Status(response.status)(response.body)
      } recover {
        case _ => InternalServerError("""{"code":"INTERNAL_SERVER_ERROR","reason":"Dependent systems are currently not responding"}""")
      }
    } recover {
      case _ => Unauthorized
    }
  }

}