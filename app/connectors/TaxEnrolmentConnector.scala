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

package connectors

import config.{AppContext, WSHttp}
import play.api.Logger
import play.api.libs.json.JsValue
import uk.gov.hmrc.play.config.ServicesConfig
import uk.gov.hmrc.play.http.logging.Authorization
import uk.gov.hmrc.play.http._

import scala.concurrent.Future

trait TaxEnrolmentConnector extends ServicesConfig{
  val httpGet: HttpGet = WSHttp
  val httpPut: HttpPut = WSHttp

  lazy val taxEnrolmentServiceUrl: String = baseUrl("tax-enrolments") + "/tax-enrolments"

  def enrolmentStatus(groupId: String)(implicit hc: HeaderCarrier): Future[HttpResponse] = {
    val uri = s"$taxEnrolmentServiceUrl/groups/$groupId/subscriptions"
    Logger.info(s"Tax Enrolment connector get subscriptions $uri")
    httpGet.GET(uri)
  }

  def subscribe(subscriptionId: String, body: JsValue)(implicit hc: HeaderCarrier): Future[HttpResponse] = {
    val uri = s"$taxEnrolmentServiceUrl/subscriptions/$subscriptionId/subscriber"
    Logger.info(s"Tax Enrolment connector put subscribe $uri")
    httpPut.PUT(uri, body)
  }
}

object TaxEnrolmentConnector extends TaxEnrolmentConnector