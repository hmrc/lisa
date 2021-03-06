
import play.core.PlayVersion
import scoverage.ScoverageKeys
import uk.gov.hmrc.DefaultBuildSettings.{defaultSettings, scalaSettings}
import uk.gov.hmrc.sbtdistributables.SbtDistributablesPlugin.publishingSettings

val appName = "lisa"
name                      := "lisa"
PlayKeys.playDefaultPort  := 8886
majorVersion              := 1
retrieveManaged           := true

lazy val lisa = Project(appName, file("."))
enablePlugins(PlayScala, SbtDistributablesPlugin)

scalaSettings
publishingSettings
defaultSettings()

scalaVersion := "2.12.12"

val silencerVersion = "1.7.1"

libraryDependencies ++= Seq(
  ws,
  "uk.gov.hmrc"             %% "bootstrap-backend-play-27" % "5.3.0",
  "uk.gov.hmrc"             %% "domain"                    % "5.11.0-play-27",
  "org.scalatest"           %% "scalatest"                 % "3.0.9"             % Test,
  "org.pegdown"             %  "pegdown"                   % "1.6.0"             % Test,
  "org.mockito"             %  "mockito-core"              % "3.10.0"            % Test,
  "com.typesafe.play"       %% "play-test"                 % PlayVersion.current % Test,
  "org.scalatestplus.play"  %% "scalatestplus-play"        % "4.0.3"             % Test,
  compilerPlugin("com.github.ghik" % "silencer-plugin" % silencerVersion cross CrossVersion.full),
  "com.github.ghik" % "silencer-lib" % silencerVersion % Provided cross CrossVersion.full
)

Compile / unmanagedResourceDirectories += baseDirectory.value / "resources"

Test / fork                            := true

ScoverageKeys.coverageExcludedPackages := "<empty>;testOnlyDoNotUseInAppConf.*;config.*;.metrics.*;prod.*;app.*;MicroService*;uk.gov.hmrc.BuildInfo"
ScoverageKeys.coverageMinimum          := 100
ScoverageKeys.coverageFailOnMinimum    := false
ScoverageKeys.coverageHighlighting     := true

scalacOptions ++= Seq("-P:silencer:pathFilters=views;routes")

