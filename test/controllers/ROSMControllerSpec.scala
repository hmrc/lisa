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

import com.sun.scenario.effect.impl.sw.java.JSWBlend_EXCLUSIONPeer
import connectors.DesConnector
import org.scalatest.{BeforeAndAfter, BeforeAndAfterEach}
import org.scalatest.mock.MockitoSugar
import org.scalatestplus.play.{OneAppPerSuite, PlaySpec}
import org.mockito.Mockito._

import scala.concurrent.Future
import play.api.test.Helpers._
import play.api.test._
import org.mockito.Matchers._
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{AnyContentAsJson, Result}
import uk.gov.hmrc.play.http.{HeaderCarrier, HttpResponse}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.io.Source


class ROSMControllerSpec extends PlaySpec
  with MockitoSugar
  with OneAppPerSuite with BeforeAndAfterEach {

  implicit val hc:HeaderCarrier = HeaderCarrier()

  override def beforeEach(): Unit = {
    reset(mockDesConnector)
  }

  val regPayload: String = Source.fromInputStream(getClass().getResourceAsStream("/json/registration_example.json")).mkString
  val regErrorJson: String = Source.fromInputStream(getClass().getResourceAsStream("/json/utr_error.json")).mkString
  val subscribePayload: String = Source.fromInputStream(getClass().getResourceAsStream("/json/subscription_example.json")).mkString

  "The ROSMController " should {
    "should register" when {
      "called with a valid JSON payload" in {
        val jsVal: JsValue = Json.parse(regPayload)
        val utr: String = "1234567890"
        when(mockDesConnector.register(any(),any())(any())).thenReturn(Future.successful(HttpResponse(OK,Some(jsVal))))

        doRegister(utr, regPayload) { res =>

          status(res) mustBe(OK)
          (contentAsJson(res) \ "regime").as[String] mustBe ("LISA")
        }

      }
    }

    "subscribe" when {
      "called with a valid JSON payload" in {
        val jsVal: JsValue = Json.parse(subscribePayload)
        val utr: String = "1234567890"
        when(mockDesConnector.subscribe(any(),any())(any())).thenReturn(Future.successful(HttpResponse(OK,Some(Json.parse(s"""{"subscriptionId": "928282776"}""")))))

        doSubscribe(utr, subscribePayload) { res =>

          status(res) mustBe(OK)
          (contentAsJson(res) \ "subscriptionId").as[String] mustBe ("928282776")
        }

      }
    }

    "400 Response with a Invalid UTR as code" when {
      "called with a valid JSON payload" in {
        println("Json is " + regErrorJson)
        val regJson: JsValue = Json.parse(regErrorJson)
        val utr: String = "1234567890"

        when(mockDesConnector.register(any(),any())(any())).thenReturn(Future.successful(HttpResponse(BAD_REQUEST,Some(regJson))))

        doRegister(utr, regPayload) { res =>

          status(res) mustBe(BAD_REQUEST)
          (contentAsJson(res) \ "code").as[String] mustBe ("INVALID_UTR")

        }

      }
    }

    "Return 500 error thrown on connector" when {
      "called with a valid JSON Payload" in {
        println("Json is " + regErrorJson)
        val regJson: JsValue = Json.parse(regErrorJson)
        val utr: String = "1234567890"

        when(mockDesConnector.register(any(),any())(any())).thenReturn(Future.failed(new Exception("Error")))

        doRegister(utr, regPayload) { res =>

          status(res) mustBe(INTERNAL_SERVER_ERROR)

        }

      }
    }
  }

  def doRegister(utr: String, payload: String)(callback: (Future[Result]) => Unit) {
    val res = await(SUT.register(utr).apply(FakeRequest(Helpers.PUT, "/").withBody(AnyContentAsJson(Json.toJson(payload)))))

    callback(Future(res))
  }

  def doSubscribe(utr: String, payload: String)(callback: (Future[Result]) => Unit) {
    val res = await(SUT.submitSubscription(utr).apply(FakeRequest(Helpers.PUT, "/").withBody(AnyContentAsJson(Json.toJson(payload)))))

    callback(Future(res))
  }

  val mockDesConnector = mock[DesConnector]
  val SUT = new ROSMController {
    override val connector: DesConnector = mockDesConnector
  }
}