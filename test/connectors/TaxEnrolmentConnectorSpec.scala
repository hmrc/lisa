/*
 * Copyright 2026 HM Revenue & Customs
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

import play.api.http.Status.{ACCEPTED, INTERNAL_SERVER_ERROR, NO_CONTENT}
import play.api.libs.json.Json
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse}
import utils.ConnectorSpecHelper

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext}

class TaxEnrolmentConnectorSpec extends ConnectorSpecHelper {

  given hc: HeaderCarrier    = HeaderCarrier()
  given ec: ExecutionContext = injector.instanceOf[ExecutionContext]

  lazy val taxEnrolmentConnector: TaxEnrolmentConnector =
    injector.instanceOf[TaxEnrolmentConnector] // lazy to allow wiremock to start

  private val enrolmentStatusUrl = "/tax-enrolments/groups/Z0192/subscriptions"
  private val subscribeUrl       = "/tax-enrolments/subscriptions/1234567890/subscriber"

  "Get enrolment status" should {
    "return a success verbatim when a successful response is returned from tax enrolment" in {
      stubForGet(enrolmentStatusUrl, ACCEPTED, """{"status": "PENDING"}""")

      enrolmentStatus { response =>
        response.status             must be(ACCEPTED)
        Json.parse(response.body) mustBe Json.parse("""{"status": "PENDING"}""")
      }
    }

    "return an error verbatim when an error is returned from tax enrolment" in {
      stubForGet(enrolmentStatusUrl, INTERNAL_SERVER_ERROR, """{"code": "INTERNAL_ERROR"}""")

      enrolmentStatus { response =>
        response.status             must be(INTERNAL_SERVER_ERROR)
        Json.parse(response.body) mustBe Json.parse("""{"code": "INTERNAL_ERROR"}""")
      }
    }
  }

  "Subscribe" should {
    "return a success verbatim when a successful response is returned from tax enrolment" in {
      stubForPut(subscribeUrl, NO_CONTENT)

      subscribe { response =>
        response.status must be(NO_CONTENT)
        response.body mustBe ""
      }
    }

    "return an error verbatim when an error is returned from tax enrolment" in {
      stubForPut(subscribeUrl, INTERNAL_SERVER_ERROR, """{"code": "INTERNAL_ERROR"}""")

      subscribe { response =>
        response.status             must be(INTERNAL_SERVER_ERROR)
        Json.parse(response.body) mustBe Json.parse("""{"code": "INTERNAL_ERROR"}""")
      }
    }
  }

  private def enrolmentStatus(callback: HttpResponse => Unit): Unit = {
    val response = Await.result(taxEnrolmentConnector.enrolmentStatus("Z0192"), Duration.Inf)
    callback(response)
  }

  private def subscribe(callback: HttpResponse => Unit): Unit = {
    val response = Await.result(taxEnrolmentConnector.subscribe("1234567890", Json.parse("{}")), Duration.Inf)
    callback(response)
  }

}
