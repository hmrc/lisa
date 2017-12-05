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

import config.{ConnectorConfig, WSHttp}
import org.mockito.Matchers.any
import org.mockito.Mockito.when
import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.{OneAppPerTest, PlaySpec}
import play.api.libs.json.Json
import play.api.mvc.AnyContent
import play.api.test.Helpers.{ACCEPTED, _}

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
import uk.gov.hmrc.http.{HeaderCarrier, HttpGet, HttpPut, HttpResponse}

class TaxEnrolmentConnectorSpec extends PlaySpec with MockitoSugar with OneAppPerTest{

  "Get enrolment status" should {
    "return a success verbatim" when {
      "a successful response is returned from tax enrolment" in {
        when(mockHttpGet.GET[HttpResponse](any())(any(), any(), any()))
          .thenReturn(
            Future.successful(
              HttpResponse(
                responseStatus = ACCEPTED,
                responseJson = Some(Json.parse(s"""{"status": "PENDING"}"""))
              )
            )
          )

        doEnrolmentStatus { response =>
          response.status must be (ACCEPTED)
          Json.parse(response.body) mustBe Json.parse(s"""{"status": "PENDING"}""")
        }
      }
    }
    "return an error verbatim" when {
      "an error is returned from tax enrolment" in {
        when(mockHttpGet.GET[HttpResponse](any())(any(), any(), any()))
          .thenReturn(
            Future.successful(
              HttpResponse(
                responseStatus = INTERNAL_SERVER_ERROR,
                responseJson = Some(Json.parse(s"""{"code": "INTERNAL_ERROR"}"""))
              )
            )
          )

        doEnrolmentStatus { response =>
          response.status must be (INTERNAL_SERVER_ERROR)
          Json.parse(response.body) mustBe Json.parse(s"""{"code": "INTERNAL_ERROR"}""")
        }
      }
    }
  }

  "Subscribe" should {
    "return a success verbatim" when {
      "a successful response is returned from tax enrolment" in {
        when(mockHttpPut.PUT[AnyContent, HttpResponse](any(), any())(any(), any(), any(), any()))
          .thenReturn(
            Future.successful(
              HttpResponse(
                responseStatus = NO_CONTENT,
                responseJson = None
              )
            )
          )

        doSubscribe { response =>
          response.status must be (NO_CONTENT)
          response.body must be (null)
        }
      }
    }
    "return an error verbatim" when {
      "an error is returned from tax enrolment" in {
        when(mockHttpPut.PUT[AnyContent, HttpResponse](any(), any())(any(), any(), any(), any()))
          .thenReturn(
            Future.successful(
              HttpResponse(
                responseStatus = INTERNAL_SERVER_ERROR,
                responseJson = Some(Json.parse(s"""{"code": "INTERNAL_ERROR"}"""))
              )
            )
          )

        doSubscribe { response =>
          response.status must be (INTERNAL_SERVER_ERROR)
          Json.parse(response.body) mustBe Json.parse(s"""{"code": "INTERNAL_ERROR"}""")
        }
      }
    }
  }

  private def doEnrolmentStatus(callback: HttpResponse => Unit) = {
    val response = Await.result(SUT.enrolmentStatus("Z0192"), Duration.Inf)

    callback(response)
  }

  private def doSubscribe(callback: HttpResponse => Unit) = {
    val response = Await.result(SUT.subscribe("1234567890", Json.parse("{}")), Duration.Inf)

    callback(response)
  }

  val mockConfig: ConnectorConfig = mock[ConnectorConfig]
  val mockHttpGet: HttpGet = mock[HttpGet]
  val mockHttpPut: HttpPut = mock[HttpPut]


  implicit val hc = HeaderCarrier()

  val SUT = new TaxEnrolmentConnector(mockConfig, mockHttpGet, mockHttpPut)

}
