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
import uk.gov.hmrc.play.http.HttpResponse

import scala.io.Source


class RegistrationControllerSpec extends PlaySpec
  with MockitoSugar
  with OneAppPerSuite with BeforeAndAfterEach {

  override def beforeEach(): Unit = {
    reset(mockDesConnector)
  }

  val regPayload: String = Source.fromInputStream(getClass().getResourceAsStream("/json/registration_example.json/.json")).mkString

  "The register controller" should {
    "Return 200 and valid payload" when {
      "called with a valid resquest" in {
        val jsVal: JsValue = Json.toJson(regPayload)
        val utr: String = "1234567890"
        when(mockDesConnector.register(any(),any())).thenReturn(Future.successful(HttpResponse(OK,Some(jsVal))))

        doRegister(utr, regPayload) { res =>

          status(res) mustBe(OK)
        }

      }
    }
  }

  def doRegister(utr: String, payload: String)(callback: (Future[Result]) => Unit) {
    val res = await(SUT.register(utr).apply(FakeRequest(Helpers.PUT, "/").withBody(AnyContentAsJson(Json.parse(payload)))))

    callback(res)
  }

  val mockDesConnector = mock[DesConnector]
  val SUT = new RegistrationController {
    override val connector: DesConnector = mockDesConnector
  }
}