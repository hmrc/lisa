
import play.core.PlayVersion
import scoverage.ScoverageKeys
import uk.gov.hmrc.DefaultBuildSettings.{defaultSettings, scalaSettings}

val appName = "lisa"
name                      := "lisa"
PlayKeys.playDefaultPort  := 8886
majorVersion              := 1
retrieveManaged           := true

lazy val lisa = Project(appName, file("."))
enablePlugins(PlayScala, SbtDistributablesPlugin)

scalaSettings
defaultSettings()
disablePlugins(JUnitXmlReportPlugin) //Required to prevent https://github.com/scalatest/scalatest/issues/1427

scalaVersion := "2.13.15"

libraryDependencies ++= AppDependencies()

Compile / unmanagedResourceDirectories += baseDirectory.value / "resources"

Test / fork                            := true

ScoverageKeys.coverageExcludedPackages := "<empty>;testOnlyDoNotUseInAppConf.*;config.*;.metrics.*;prod.*;app.*;MicroService*;uk.gov.hmrc.BuildInfo"
ScoverageKeys.coverageFailOnMinimum    := false
ScoverageKeys.coverageHighlighting     := true

scalacOptions += "-Wconf:src=routes/.*:s"

addCommandAlias("scalastyleAll", "all scalastyle Test/scalastyle")
