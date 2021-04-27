import play.core.PlayVersion
import scoverage.ScoverageKeys
import uk.gov.hmrc.DefaultBuildSettings.{defaultSettings, integrationTestSettings, scalaSettings}
import uk.gov.hmrc.sbtdistributables.SbtDistributablesPlugin.publishingSettings

val appName = "lisa"
name                      := "lisa"
PlayKeys.playDefaultPort  := 8886
majorVersion              := 1
retrieveManaged           := true

lazy val lisa = Project(appName, file("."))
enablePlugins(PlayScala, SbtAutoBuildPlugin, SbtGitVersioning, SbtDistributablesPlugin)

scalaSettings
publishingSettings
defaultSettings()
integrationTestSettings()
configs(IntegrationTest)

scalaVersion := "2.12.12"

val testScope = "test"

libraryDependencies ++= Seq(
  ws,
  "uk.gov.hmrc"             %% "bootstrap-backend-play-27" % "4.2.0",
  "uk.gov.hmrc"             %% "auth-client"               % "5.2.0-play-27",
  "uk.gov.hmrc"             %% "domain"                    % "5.11.0-play-27",
  "org.scalatest"           %% "scalatest"                 % "3.0.8"             % testScope,
  "org.pegdown"             %  "pegdown"                   % "1.6.0"             % testScope,
  "org.mockito"             %  "mockito-core"              % "3.3.0"             % testScope,
  "com.typesafe.play"       %% "play-test"                 % PlayVersion.current % testScope,
  "org.scalatestplus.play"  %% "scalatestplus-play"        % "3.1.3"             % testScope
)
unmanagedResourceDirectories in Compile += baseDirectory.value / "resources"

evictionWarningOptions in update        := EvictionWarningOptions.default.withWarnScalaVersionEviction(false)

fork in Test                            := true

ScoverageKeys.coverageExcludedPackages  := "<empty>;testOnlyDoNotUseInAppConf.*;config.*;.metrics.*;prod.*;app.*;MicroService*;uk.gov.hmrc.BuildInfo"
ScoverageKeys.coverageMinimum           := 100
ScoverageKeys.coverageFailOnMinimum     := false
ScoverageKeys.coverageHighlighting      := true
parallelExecution in Test               := false