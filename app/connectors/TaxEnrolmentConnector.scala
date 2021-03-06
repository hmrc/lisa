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

package connectors

import config.AppConfig
import javax.inject.Inject
import play.api.Logger
import play.api.libs.json.JsValue
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse}
import uk.gov.hmrc.http.HttpClient
import uk.gov.hmrc.http.HttpReads.Implicits.readRaw

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class TaxEnrolmentConnector @Inject() (config: AppConfig, httpClient: HttpClient) {

  lazy val taxEnrolmentUrl: String = config.taxEnrolmentUrl

  def enrolmentStatus(groupId: String)(implicit hc: HeaderCarrier): Future[HttpResponse] = {
    val uri = s"$taxEnrolmentUrl/groups/$groupId/subscriptions"
    Logger.logger.info(s"Tax Enrolment connector get subscriptions $uri")
    httpClient.GET(uri)
  }

  def subscribe(subscriptionId: String, body: JsValue)(implicit hc: HeaderCarrier): Future[HttpResponse] = {
    val uri = s"$taxEnrolmentUrl/subscriptions/$subscriptionId/subscriber"
    Logger.logger.info(s"Tax Enrolment connector put subscribe $uri")
    httpClient.PUT(uri, body)
  }

}