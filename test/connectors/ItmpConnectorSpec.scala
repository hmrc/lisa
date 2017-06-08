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

import org.joda.time.DateTime
import org.mockito.Matchers
import org.mockito.Matchers.any
import org.mockito.Mockito.when
import org.scalatest.mock.MockitoSugar
import org.scalatestplus.play.{OneAppPerSuite, PlaySpec}
import play.api.libs.json.{JsValue, Json}
import play.api.test.Helpers._
import uk.gov.hmrc.play.http.{HeaderCarrier, HttpGet, HttpPost, HttpResponse}

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

class ItmpConnectorSpec extends PlaySpec
  with MockitoSugar
  with OneAppPerSuite {


  val jsVal: JsValue = Json.obj("field" -> "value").as[JsValue]
  val jsFailure: JsValue = Json.obj("field" -> "value").as[JsValue]
  val lisaRef: String = "Z123456"

  "Subscription endpoint" should {
    "Return a status 202" when {
      "Valid json posted" in {
        when(mockHttpPost.POST[JsValue, HttpResponse](any(), any(), any())(any(), any(), any()))
          .thenReturn(
            Future.successful(
              HttpResponse(
                responseStatus = ACCEPTED,
                responseJson = Some(Json.parse(s"""{"SubscriptionID": "928282776"}"""))
              )
            )
          )

        doSubcribe { response =>
          response.status must be (OK)
        }
      }
      }
    }

  private def doSubcribe(callback: HttpResponse => Unit) = {
    val jsVal: JsValue = Json.obj("field" -> "value").as[JsValue]
    val response = Await.result(SUT.subscribe("Z019283", jsVal), Duration.Inf)

    callback(response)
  }


  val mockHttpPost = mock[HttpPost]

  implicit val hc = HeaderCarrier()

  object SUT extends ItmpConnector {
    override val httpPost = mockHttpPost
  }



}
