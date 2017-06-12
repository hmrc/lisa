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

import org.mockito.Matchers.any
import org.mockito.Mockito.when
import org.scalatest.mock.MockitoSugar
import org.scalatestplus.play.{OneAppPerTest, PlaySpec}
import play.api.libs.json.{JsValue, Json}
import play.api.test.Helpers.ACCEPTED
import play.api.test.Helpers.BAD_REQUEST
import uk.gov.hmrc.play.http.{HeaderCarrier, HttpPost, HttpResponse}

import scala.concurrent.{Await, Future}
import scala.concurrent.duration.Duration
import scala.io.Source

class TaxEnrolmentConnectorSpec extends PlaySpec with MockitoSugar with OneAppPerTest{

  "TaxEnrolment endpoint" should {
    "Return status 200" when {
      "Valid json is posted" in {
        when(mockHttpPost.POSTEmpty[HttpResponse](any())(any(), any()))
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
        }
      }
      }
    "Return status 400" when {
      "When error returned from tax enrolment" in {
        when(mockHttpPost.POSTEmpty[HttpResponse](any())(any(), any()))
          .thenReturn(
            Future.successful(
              HttpResponse(
                responseStatus = BAD_REQUEST,
                responseJson = Some(Json.parse(s"""{"code": "INTERNAL_ERROR"}"""))
              )
            )
          )

        doEnrolmentStatus { response =>
          response.status must be (BAD_REQUEST)
        }
      }
      }
    }


  private def doEnrolmentStatus(callback: HttpResponse => Unit) = {
    val response = Await.result(SUT.enrolmentStatus("Z019283"), Duration.Inf)

    callback(response)
  }

  val mockHttpPost = mock[HttpPost]

  implicit val hc = HeaderCarrier()

  object SUT extends TaxEnrolmentConnector {
    override val httpPost = mockHttpPost
  }

}
