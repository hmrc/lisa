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

package config

import javax.inject.Inject

import play.api.{Configuration, Environment}
import uk.gov.hmrc.auth.core.PlayAuthConnector
import uk.gov.hmrc.http._
import uk.gov.hmrc.play.audit.http.connector.AuditConnector
import uk.gov.hmrc.play.config.{AppName, RunMode, ServicesConfig}
import uk.gov.hmrc.play.http.ws._
import uk.gov.hmrc.http.hooks.HttpHook
import uk.gov.hmrc.play.bootstrap.config.LoadAuditingConfig

class WSHttp @Inject()(override val appNameConfiguration: Configuration)
  extends HttpGet
  with WSGet
  with HttpPut
  with WSPut
  with HttpPost
  with WSPost
  with HttpDelete
  with WSDelete
  with HttpPatch
  with WSPatch
  with AppName {

  override val hooks: Seq[HttpHook] = NoneRequired
}

class MicroserviceAuditConnector @Inject()(override val runModeConfiguration: Configuration, environment: Environment)
  extends AuditConnector
  with RunMode {

  override protected def mode = environment.mode
  override lazy val auditingConfig = LoadAuditingConfig(runModeConfiguration, mode, s"auditing")
}

class LisaAuthConnector @Inject()(override val runModeConfiguration: Configuration, environment: Environment, val http: WSHttp)
  extends PlayAuthConnector
  with ServicesConfig {

  override protected def mode = environment.mode

  val serviceUrl = baseUrl("auth")
}