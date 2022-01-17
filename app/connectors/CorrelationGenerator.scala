/*
 * Copyright 2022 HM Revenue & Customs
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

import uk.gov.hmrc.http.HeaderCarrier

import java.util.UUID.randomUUID

trait CorrelationGenerator {

  def generateRandomUUID: String = randomUUID.toString

  def addCorrelationId(hc: HeaderCarrier): HeaderCarrier = {

    val CorrelationIdPattern = """.*([A-Za-z0-9]{8}-[A-Za-z0-9]{4}-[A-Za-z0-9]{4}-[A-Za-z0-9]{4}).*""".r
    val correlationId = hc.requestId match {
      case Some(requestId) => requestId.value match {
        case CorrelationIdPattern(prefix) => prefix + "-" + generateRandomUUID.substring(24)
        case _ => generateRandomUUID
      }
      case _ => generateRandomUUID
    }
    hc.withExtraHeaders("CorrelationId" -> correlationId)
  }

}
