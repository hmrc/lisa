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

import play.core.PlayVersion
import scoverage.ScoverageKeys
import uk.gov.hmrc.DefaultBuildSettings.{defaultSettings, integrationTestSettings, scalaSettings}
import uk.gov.hmrc.sbtdistributables.SbtDistributablesPlugin.publishingSettings

name                      := "lisa"
PlayKeys.playDefaultPort  := 8886
majorVersion              := 1
retrieveManaged           := true

lazy val lisa = (project in file("."))
  .configs(IntegrationTest)

scalaSettings
publishingSettings
defaultSettings()
integrationTestSettings()

scalaVersion := "2.12.10"

val testScope = "test"

libraryDependencies ++= Seq(
  ws,
  "uk.gov.hmrc"             %% "bootstrap-backend-play-27" % "4.2.0",
  "uk.gov.hmrc"             %% "auth-client"        % "5.2.0-play-27",
  "uk.gov.hmrc"             %% "domain"             % "5.11.0-play-27",
  "org.scalatest"           %% "scalatest"          % "3.0.8"             % testScope,
  "org.pegdown"             %  "pegdown"            % "1.6.0"             % testScope,
  "org.mockito"             %  "mockito-core"       % "3.3.0"             % testScope,
  "com.typesafe.play"       %% "play-test"          % PlayVersion.current % testScope,
  "org.scalatestplus.play"  %% "scalatestplus-play" % "3.1.3"             % testScope
)
unmanagedResourceDirectories in Compile += baseDirectory.value / "resources"

evictionWarningOptions in update        := EvictionWarningOptions.default.withWarnScalaVersionEviction(false)

fork in Test                            := true

resolvers ++= Seq(
  Resolver.bintrayRepo("hmrc", "releases"),
  Resolver.jcenterRepo
)

enablePlugins(PlayScala, SbtAutoBuildPlugin, SbtGitVersioning, SbtDistributablesPlugin, SbtArtifactory)

ScoverageKeys.coverageExcludedPackages  := "<empty>;testOnlyDoNotUseInAppConf.*;config.*;.metrics.*;prod.*;app.*;MicroService*;uk.gov.hmrc.BuildInfo"
ScoverageKeys.coverageMinimum           := 100
ScoverageKeys.coverageFailOnMinimum     := false
ScoverageKeys.coverageHighlighting      := true
parallelExecution in Test               := false