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
import org.mockito.Matchers._
import org.mockito.Mockito._
import org.scalatest.BeforeAndAfterEach
import org.scalatest.mock.MockitoSugar
import org.scalatestplus.play.{OneAppPerSuite, PlaySpec}
import play.api.libs.json.Json
import play.api.mvc.AnyContentAsJson
import play.api.test.Helpers._
import play.api.test._
import uk.gov.hmrc.play.http.{HeaderCarrier, HttpResponse, Upstream4xxResponse}

import scala.concurrent.Future


class TaxEnrolmentControllerSpec extends PlaySpec
  with MockitoSugar
  with OneAppPerSuite with BeforeAndAfterEach {

  implicit val hc:HeaderCarrier = HeaderCarrier()

  override def beforeEach(): Unit = {
    reset(mockConnector)
  }

  "Tax Enrolment Controller" should {
    "return 204 NO CONTENT" when {
      "204 is returned from the connector" in {
        when(mockConnector.subscribe(any(), any())(any())).thenReturn(Future.successful(HttpResponse(NO_CONTENT)))

        val res = doSubscribe()

        status(res) mustBe NO_CONTENT
      }
    }
    "return 500 INTERNAL SERVER ERROR" when {
      "any other status is returned from the connector" in {
        when(mockConnector.subscribe(any(), any())(any())).thenReturn(Future.successful(HttpResponse(OK)))

        val res = doSubscribe()

        status(res) mustBe INTERNAL_SERVER_ERROR
      }
      "any exceptions occur in the connector" in {
        when(mockConnector.subscribe(any(), any())(any())).thenReturn(Future.failed(Upstream4xxResponse("fail", BAD_REQUEST, BAD_REQUEST)))

        val res = doSubscribe()

        status(res) mustBe INTERNAL_SERVER_ERROR
      }
    }
  }

  private def doSubscribe() = {
    SUT.subscribe("1234567890").apply(FakeRequest(Helpers.PUT, "/").withBody(AnyContentAsJson(Json.toJson("{}"))))
  }

  val mockConnector: TaxEnrolmentConnector = mock[TaxEnrolmentConnector]
  val SUT = new TaxEnrolmentController {
    override val connector: TaxEnrolmentConnector = mockConnector
  }
}