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

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import uk.gov.hmrc.http.{HeaderCarrier, HttpPost, HttpReads, HttpResponse}
import uk.gov.hmrc.http.logging.Authorization
import uk.gov.hmrc.play.http.logging.MdcLoggingExecutionContext

trait DesConnector extends ServicesConfig {

  val httpPost:HttpPost = WSHttp
  lazy val desUrl = baseUrl("des")
  lazy val subscriptionUrl = s"$desUrl/lifetime-isa/manager"
  lazy val registrationUrl = s"$desUrl/registration/organisation"

  val httpReads: HttpReads[HttpResponse] = new HttpReads[HttpResponse] {
    override def read(method: String, url: String, response: HttpResponse) = response
  }

  private def updateHeaderCarrier(headerCarrier: HeaderCarrier) =
    headerCarrier.copy(extraHeaders = Seq(("Environment" -> AppContext.desUrlHeaderEnv)),
      authorization = Some(Authorization(s"Bearer ${AppContext.desAuthToken}")))

  def subscribe(lisaManager: String, payload: JsValue)(implicit hc: HeaderCarrier): Future[HttpResponse] = {
    val uri = s"$subscriptionUrl/$lisaManager/subscription"
    Logger.info(s"DES Connector post subscription ${uri}")
    httpPost.POST(uri, payload)(implicitly, httpReads, updateHeaderCarrier(hc), MdcLoggingExecutionContext.fromLoggingDetails(hc)) map { response =>
      response
    } recover {
      // $COVERAGE-OFF$
      case e: Exception => Logger.error(s"Error in Desconnector subscribe: ${e.getMessage}")
        throw e
      // $COVERAGE-ON$
    }
  }


  def register(utr: String, payload: JsValue)(implicit hc: HeaderCarrier): Future[HttpResponse] = {
    val uri = s"$registrationUrl/utr/$utr"
    Logger.info(s"DES Connector post registerOnce ${uri}")
    httpPost.POST(uri, payload)(implicitly, httpReads, updateHeaderCarrier(hc), MdcLoggingExecutionContext.fromLoggingDetails(hc)) map { response =>
      response
    } recover {
      // $COVERAGE-OFF$
      case e: Exception => Logger.error(s"Error in Desconnector register : ${e.getMessage}")
        throw e
      // $COVERAGE-ON$
    }
  }


}

object DesConnector extends DesConnector
