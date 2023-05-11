
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

scalaVersion := "2.13.10"
val bootstrapPlay28 = "7.15.0"

ThisBuild / libraryDependencySchemes ++= Seq(
  "org.scala-lang.modules" %% "scala-xml" % VersionScheme.Always
)

libraryDependencies ++= Seq(
  ws,
  "uk.gov.hmrc"             %% "bootstrap-backend-play-28" % bootstrapPlay28,
  "uk.gov.hmrc"             %% "domain"                    % "8.3.0-play-28",
  "org.pegdown"             %  "pegdown"                   % "1.6.0"             % Test,
  "com.typesafe.play"       %% "play-test"                 % PlayVersion.current % Test,
  "org.scalatest"           %% "scalatest"                 % "3.2.15"            % Test,
  "org.scalatestplus.play"  %% "scalatestplus-play"        % "5.1.0"             % Test,
  "org.scalatestplus"       %% "mockito-3-4"               % "3.2.10.0"          % Test,
  "com.vladsch.flexmark"    % "flexmark-all"               % "0.64.4"            % Test,
  "uk.gov.hmrc"             %% "bootstrap-test-play-28"    % bootstrapPlay28     % Test
)

Compile / unmanagedResourceDirectories += baseDirectory.value / "resources"

Test / fork                            := true

ScoverageKeys.coverageExcludedPackages := "<empty>;testOnlyDoNotUseInAppConf.*;config.*;.metrics.*;prod.*;app.*;MicroService*;uk.gov.hmrc.BuildInfo"
ScoverageKeys.coverageFailOnMinimum    := false
ScoverageKeys.coverageHighlighting     := true

scalacOptions += "-Wconf:src=routes/.*:s"

addCommandAlias("scalastyleAll", "all scalastyle test:scalastyle")
