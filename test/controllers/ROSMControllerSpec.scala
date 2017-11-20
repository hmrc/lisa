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

import config.LisaAuthConnector
import connectors.{DesConnector, TaxEnrolmentConnector}
import org.scalatest.BeforeAndAfterEach
import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import org.mockito.Mockito._

import scala.concurrent.Future
import play.api.test.Helpers._
import play.api.test._
import org.mockito.Matchers._
import play.api.libs.json.Json
import play.api.mvc.{AnyContentAsJson, Result}
import uk.gov.hmrc.auth.core.BearerTokenExpired

import scala.concurrent.ExecutionContext.Implicits.global
import scala.io.Source
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse, Upstream4xxResponse}


class ROSMControllerSpec extends PlaySpec
  with MockitoSugar
  with GuiceOneAppPerSuite with BeforeAndAfterEach {

  implicit val hc:HeaderCarrier = HeaderCarrier()

  override def beforeEach(): Unit = {
    reset(mockDesConnector)
    when(mockAuthCon.authorise[Unit](any(), any())(any(), any())).thenReturn(Future.successful(()))
  }

  val regPayload: String = Source.fromInputStream(getClass().getResourceAsStream("/json/registration_example.json")).mkString
  val regErrorJson: String = Source.fromInputStream(getClass().getResourceAsStream("/json/utr_error.json")).mkString
  val subscribePayload: String = Source.fromInputStream(getClass().getResourceAsStream("/json/subscription_example.json")).mkString

  "Register endpoint" should {

    "return a 200 ok response" when {
      "everything is valid and no errors are thrown" in {

        when(mockDesConnector.register(any(),any())(any())).thenReturn(Future.successful(HttpResponse(OK,Some(Json.parse("{}")))))

        doRegister() { res =>
          status(res) mustBe OK
        }
      }
    }

    "return a 400 error response with Invalid UTR as the response code" when {
      "the connector returns a 400 response" in {
        when(mockDesConnector.register(any(),any())(any())).thenReturn(Future.successful(HttpResponse(BAD_REQUEST,Some(Json.parse(regErrorJson)))))

        doRegister() { res =>
          status(res) mustBe BAD_REQUEST
          (contentAsJson(res) \ "code").as[String] mustBe "INVALID_UTR"
        }
      }
    }

    "return a 500 error response" when {
      "the connector returns an error" in {
        when(mockDesConnector.register(any(),any())(any())).thenReturn(Future.failed(new Exception("Error")))

        doRegister() { res =>
          status(res) mustBe INTERNAL_SERVER_ERROR
          (contentAsJson(res) \ "code").as[String] mustBe "INTERNAL_SERVER_ERROR"
        }
      }
    }

    "return unauthorised" when {
      "the auth connector does not return successfully" in {
        when(mockAuthCon.authorise[Unit](any(), any())(any(), any())).thenReturn(Future.failed(BearerTokenExpired("Unauthorised")))

        doRegister() { res =>
          status(res) mustBe UNAUTHORIZED
        }
      }
    }

  }

  "Subscribe endpoint" should {

    "return a 200 ok response with the subscriptionId" when {
      "everything is valid and no errors are thrown" in {


        when(mockEnrolmentConnector.subscribe(any(), any())(any())).
          thenReturn(Future.successful(HttpResponse(NO_CONTENT)))

        when(mockDesConnector.subscribe(any(), any())(any())).
          thenReturn(Future.successful(HttpResponse(ACCEPTED, Some(Json.parse(s"""{"subscriptionId": "928282776"}""")))))

        doSubscribe() { res =>
          status(res) mustBe (ACCEPTED)
          (contentAsJson(res) \ "subscriptionId").as[String] mustBe "928282776"
        }
      }
    }

    "return a 500 internal server error response" when {

      "the call to des fails" in {
        when(mockEnrolmentConnector.subscribe(any(), any())(any())).
          thenReturn(Future.successful(HttpResponse(NO_CONTENT)))

        when(mockDesConnector.subscribe(any(), any())(any())).
          thenReturn(Future.failed(Upstream4xxResponse("Bad Request", BAD_REQUEST, BAD_REQUEST)))

        doSubscribe() { res =>
          status(res) mustBe INTERNAL_SERVER_ERROR
          (contentAsJson(res) \ "code").as[String] mustBe "INTERNAL_SERVER_ERROR"
        }
      }

      "the call to des returns an unexpected status code" in {
        when(mockEnrolmentConnector.subscribe(any(), any())(any())).
          thenReturn(Future.successful(HttpResponse(NO_CONTENT)))

        when(mockDesConnector.subscribe(any(), any())(any())).
          thenReturn(Future.successful(HttpResponse(NO_CONTENT, Some(Json.parse(s"""{"subscriptionId": "928282776"}""")))))

        doSubscribe() { res =>
          status(res) mustBe INTERNAL_SERVER_ERROR
          (contentAsJson(res) \ "code").as[String] mustBe "INTERNAL_SERVER_ERROR"
        }
      }

      "the call to tax enrolments fails" in {
        when(mockEnrolmentConnector.subscribe(any(), any())(any())).
          thenReturn(Future.failed(Upstream4xxResponse("Bad Request", BAD_REQUEST, BAD_REQUEST)))

        when(mockDesConnector.subscribe(any(), any())(any())).
          thenReturn(Future.successful(HttpResponse(OK, Some(Json.parse(s"""{"subscriptionId": "928282776"}""")))))

        doSubscribe() { res =>
          status(res) mustBe INTERNAL_SERVER_ERROR
          (contentAsJson(res) \ "code").as[String] mustBe "INTERNAL_SERVER_ERROR"
        }
      }

      "the call to tax enrolments returns an unexpected status code" in {
        when(mockEnrolmentConnector.subscribe(any(), any())(any())).
          thenReturn(Future.successful(HttpResponse(OK, responseString = Some("A 204 (No Content) the only valid response"))))

        when(mockDesConnector.subscribe(any(), any())(any())).
          thenReturn(Future.successful(HttpResponse(OK, Some(Json.parse(s"""{"subscriptionId": "928282776"}""")))))

        doSubscribe() { res =>
          status(res) mustBe INTERNAL_SERVER_ERROR
          (contentAsJson(res) \ "code").as[String] mustBe "INTERNAL_SERVER_ERROR"
        }
      }

      "the response from des does not contain a subscriptionId" in {
        when(mockEnrolmentConnector.subscribe(any(), any())(any())).
          thenReturn(Future.successful(HttpResponse(NO_CONTENT)))

        when(mockDesConnector.subscribe(any(), any())(any())).
          thenReturn(Future.successful(HttpResponse(OK, Some(Json.parse(s"""{}""")))))

        doSubscribe() { res =>
          status(res) mustBe INTERNAL_SERVER_ERROR
          (contentAsJson(res) \ "code").as[String] mustBe "INTERNAL_SERVER_ERROR"
        }
      }

    }

    "return unauthorised" when {
      "the auth connector does not return successfully" in {
        when(mockAuthCon.authorise[Unit](any(), any())(any(), any())).thenReturn(Future.failed(BearerTokenExpired("Unauthorised")))

        doSubscribe() { res =>
          status(res) mustBe UNAUTHORIZED
        }
      }
    }

  }

  def doRegister()(callback: (Future[Result]) => Unit) {
    val res = await(SUT.register("1234567890").apply(FakeRequest(Helpers.PUT, "/").withBody(AnyContentAsJson(Json.parse(regPayload)))))

    callback(Future(res))
  }

  def doSubscribe()(callback: (Future[Result]) => Unit) {
    val res = await(SUT.submitSubscription("1234567890", "Z1234").apply(FakeRequest(Helpers.PUT, "/").withBody(AnyContentAsJson(Json.parse(subscribePayload)))))

    callback(Future(res))
  }

  private val mockDesConnector = mock[DesConnector]
  private val mockEnrolmentConnector = mock[TaxEnrolmentConnector]
  private val mockAuthCon = mock[LisaAuthConnector]

  object SUT extends ROSMController {
    override val connector: DesConnector = mockDesConnector
    override val enrolmentConnector: TaxEnrolmentConnector = mockEnrolmentConnector
    override val authConnector : LisaAuthConnector = mockAuthCon

  }
}