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

package connectors

import helpers.BaseTestSpec
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import play.api.libs.json.{JsValue, Json}
import play.api.test.Helpers._
import uk.gov.hmrc.http.HttpResponse

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
import scala.io.Source

class DesConnectorSpec extends BaseTestSpec {

  val desConnector = new DesConnector(mockAppConfig, mockHttpClient)

  "Subscription endpoint" should {
    "Return a status 202" when {
      "Valid json posted" in {
        when(mockHttpClient.POST[JsValue, HttpResponse](any(), any(), any())(any(), any(), any(), any()))
          .thenReturn(
            Future.successful(
              HttpResponse(
                responseStatus = ACCEPTED,
                responseJson = Some(Json.parse(s"""{"SubscriptionID": "928282776"}"""))
              )
            )
          )

        doSubcribe { response =>
          response.status must be(ACCEPTED)
        }
      }
    }
    "Return a status 503" when {
      "invalid json posted" in {
        when(mockHttpClient.POST[JsValue, HttpResponse](any(), any(), any())(any(), any(), any(), any()))
          .thenReturn(
            Future.successful(
              HttpResponse(
                responseStatus = SERVICE_UNAVAILABLE,
                responseJson = Some(Json.parse(
                  s"""
                {
                  "code": "SERVICE_UNAVAILABLE",
                  "reason": "Dependent systems are currently not responding."
                }
                """))
              )
            )
          )

        doInvalidSubscribe { response =>
          response.status must be(SERVICE_UNAVAILABLE)
        }
      }
    }
  }

  "Registration endpoint" should {
    "Return a status 200" when {
      "Valid json posted" in {
        when(mockHttpClient.POST[JsValue, HttpResponse](any(), any(), any())(any(), any(), any(), any()))
          .thenReturn(
            Future.successful(
              HttpResponse(
                responseStatus = OK,
                responseJson = Some(Json.parse(
                  s"""{
                        "safeId": "XE0001234567890",
                        "agentReferenceNumber": "AARN1234567",
                        "isEditable": true,
                        "isAnAgent": false,
                        "isAnASAgent": false,
                        "isAnIndividual": true,
                        "individual": {
                          "firstName": "Stephen",
                          "lastName": "Wood",
                          "dateOfBirth": "1990-04-03"
                        },
                        "address": {
                          "addressLine1": "100 SuttonStreet",
                          "addressLine2": "Wokingham",
                          "addressLine3": "Surrey",
                          "addressLine4": "London",
                          "postalCode": "DH14EJ",
                          "countryCode": "GB"
                        },
                        "contactDetails": {
                          "primaryPhoneNumber": "01332752856",
                          "secondaryPhoneNumber": "07782565326",
                          "faxNumber": "01332754256",
                          "emailAddress": "stephen@manncorpone.co.uk"
                        }
                      }""".stripMargin))
              )
            )
          )

        doRegister { response =>
          response.status must be(OK)
        }
      }
    }
    "Return a status 503" when {
      "invalid json posted" in {
        when(mockHttpClient.POST[JsValue, HttpResponse](any(), any(), any())(any(), any(), any(), any()))
          .thenReturn(
            Future.successful(
              HttpResponse(
                responseStatus = SERVICE_UNAVAILABLE,
                responseJson = Some(Json.parse(
                  s"""
                {
                  "code": "SERVICE_UNAVAILABLE",
                  "reason": "Dependent systems are currently not responding."
                }
                """))
              )
            )
          )

        doInvalidRegister { response =>
          response.status must be(SERVICE_UNAVAILABLE)
        }
      }
    }
  }

  private def doSubcribe(callback: HttpResponse => Unit): Unit = {
    val jsVal: JsValue = Json.toJson(Source.fromInputStream(getClass().getResourceAsStream("/json/subscription_example.json")).mkString)
    val response = Await.result(desConnector.subscribe("Z019283", jsVal), Duration.Inf)

    callback(response)
  }


  private def doRegister(callback: HttpResponse => Unit): Unit = {
    val jsVal: JsValue = Json.toJson(Source.fromInputStream(getClass().getResourceAsStream("/json/registration_example.json")).mkString)
    val response = Await.result(desConnector.register("Z019283", jsVal), Duration.Inf)

    callback(response)
  }

  private def doInvalidSubscribe(callback: HttpResponse => Unit): Unit = {
    val jsVal: JsValue = Json.toJson(Source.fromInputStream(getClass().getResourceAsStream("/json/subscription_example.json")).mkString.replace("utr", "otr"))
    val response = Await.result(desConnector.subscribe("Z019283", jsVal), Duration.Inf)

    callback(response)
  }

  private def doInvalidRegister(callback: HttpResponse => Unit): Unit = {
    val jsVal: JsValue = Json.toJson(Source.fromInputStream(getClass().getResourceAsStream("/json/registration_example.json")).mkString.replace("utr", "otr"))
    val response = Await.result(desConnector.register("Z019283", jsVal), Duration.Inf)

    callback(response)
  }
}
