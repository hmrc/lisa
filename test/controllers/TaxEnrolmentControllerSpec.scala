/*
 * Copyright 2020 HM Revenue & Customs
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
import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.{OneAppPerSuite, PlaySpec}
import play.api.libs.json.Json
import play.api.mvc.ControllerComponents
import play.api.test.Helpers._
import play.api.test._
import uk.gov.hmrc.auth.core.{AuthConnector, BearerTokenExpired}

import scala.concurrent.Future
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse, Upstream4xxResponse}

class TaxEnrolmentControllerSpec extends PlaySpec
  with MockitoSugar with OneAppPerSuite with BeforeAndAfterEach with Injecting {

  implicit val hc:HeaderCarrier = HeaderCarrier()

  override def beforeEach(): Unit = {
    reset(mockConnector)
    when(mockAuthCon.authorise[Unit](any(), any())(any(), any())).thenReturn(Future.successful(()))
  }

  "Get Enrolments for Group ID" should {
    "return the status and body as returned from the connector" when {
      "no errors occur" in {
        when(mockConnector.enrolmentStatus(any())(any())).thenReturn(Future.successful(HttpResponse(OK, responseString = Some("test"))))

        val res = doGetSubscriptionsForGroupId()

        status(res) mustBe OK
        contentAsString(res) mustBe "test"
      }
    }
    "return appropriate 500 internal server error response" when {
      "any errors occur" in {
        when(mockConnector.enrolmentStatus(any())(any())).thenReturn(Future.failed(Upstream4xxResponse("fail", BAD_REQUEST, BAD_REQUEST)))

        val res = doGetSubscriptionsForGroupId()

        status(res) mustBe INTERNAL_SERVER_ERROR
        contentAsJson(res) mustBe Json.parse("""{"code":"INTERNAL_SERVER_ERROR","reason":"Dependent systems are currently not responding"}""")
      }
    }
    "return unauthorised" when {
      "the auth connector doesnt return successfully" in {
        when(mockAuthCon.authorise[Unit](any(), any())(any(), any())).thenReturn(Future.failed(BearerTokenExpired("unauthorised")))

        val res = doGetSubscriptionsForGroupId()

        status(res) mustBe UNAUTHORIZED
      }
    }

  }

  private def doGetSubscriptionsForGroupId() = {
    SUT.getSubscriptionsForGroupId("1234567890").apply(FakeRequest(Helpers.GET, "/"))
  }

  val mockConnector: TaxEnrolmentConnector = mock[TaxEnrolmentConnector]
  val mockAuthCon: AuthConnector = mock[AuthConnector]
  private lazy val controllerComponents = inject[ControllerComponents]

  lazy val SUT = new TaxEnrolmentController(mockAuthCon, mockConnector, controllerComponents)

}