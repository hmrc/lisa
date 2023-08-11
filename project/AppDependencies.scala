import play.core.PlayVersion
import play.sbt.PlayImport.ws
import sbt._

object AppDependencies {

  val bootstrapPlayVersion = "7.21.0"

 private val compile = Seq(
    ws,
    "uk.gov.hmrc"             %% "bootstrap-backend-play-28" % bootstrapPlayVersion,
    "uk.gov.hmrc"             %% "domain"                    % "8.3.0-play-28"
   )

    private val test = Seq(
    "uk.gov.hmrc"             %% "bootstrap-test-play-28"    % bootstrapPlayVersion,
    "org.pegdown"             %  "pegdown"                   % "1.6.0",
    "com.typesafe.play"       %% "play-test"                 % PlayVersion.current,
    "org.scalatest"           %% "scalatest"                 % "3.2.16",
    "org.scalatestplus.play"  %% "scalatestplus-play"        % "5.1.0",
    "org.scalatestplus"       %% "mockito-3-4"               % "3.2.10.0",
    "com.vladsch.flexmark"    % "flexmark-all"               % "0.64.8"
  ).map(_ % Test)

 def apply(): Seq[ModuleID] = compile ++ test

}
