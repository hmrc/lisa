/*
 * Copyright 2022 HM Revenue & Customs
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
import play.api.Logging
import play.api.libs.json.JsValue
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient, HttpResponse}

import javax.inject.Inject
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class DesConnector @Inject() (config: AppConfig, httpClient: HttpClient) extends RawResponseReads with Logging with CorrelationGenerator {

  lazy val desUrl = config.desUrl
  lazy val subscriptionUrl = s"$desUrl/lifetime-isa/manager"
  lazy val registrationUrl = s"$desUrl/registration/organisation"

  private val desHeaders: Seq[(String, String)] = Seq(
    "Environment" -> config.desUrlHeaderEnv,
    "Authorization" -> s"Bearer ${config.desAuthToken}"
  )

  def subscribe(lisaManager: String, payload: JsValue)(implicit hc: HeaderCarrier): Future[HttpResponse] = {
    val uri = s"$subscriptionUrl/$lisaManager/subscription"
    logger.info(s"DES Connector post subscription $uri")
    val headerCarrier = addCorrelationId(hc)
    httpClient.POST(uri, payload, desHeaders)(implicitly, httpReads, headerCarrier, implicitly) map { res => res } recover {
      case e: Exception =>
        logger.error(s"Error in DesConnector subscribe: ${e.getMessage}")
        throw e
    }
  }

  def register(utr: String, payload: JsValue)(implicit hc: HeaderCarrier): Future[HttpResponse] = {
    val uri = s"$registrationUrl/utr/$utr"
    logger.info(s"DES Connector post registerOnce $uri")
    val headerCarrier = addCorrelationId(hc)
    httpClient.POST(uri, payload, desHeaders)(implicitly, httpReads, headerCarrier, implicitly) map { res => res } recover {
      case e: Exception =>
        logger.error(s"Error in DesConnector register : ${e.getMessage}")
        throw e
    }
  }


}