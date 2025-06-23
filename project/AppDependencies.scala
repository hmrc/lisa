import play.core.PlayVersion
import play.sbt.PlayImport.ws
import sbt._

object AppDependencies {

  val bootstrapPlayVersion = "9.13.0"

 private val compile = Seq(
    ws,
    "uk.gov.hmrc"             %% "bootstrap-backend-play-30" % bootstrapPlayVersion,
    "uk.gov.hmrc"             %% "domain-play-30"            % "11.0.0"
   )

    private val test = Seq(
    "uk.gov.hmrc"             %% "bootstrap-test-play-30"    % bootstrapPlayVersion,
    "org.pegdown"             %  "pegdown"                   % "1.6.0",
    "org.playframework"       %% "play-test"                 % PlayVersion.current,
    "org.scalatest"           %% "scalatest"                 % "3.2.19",
    "org.scalatestplus.play"  %% "scalatestplus-play"        % "7.0.1",
    "org.scalatestplus"       %% "mockito-3-4"               % "3.2.10.0",
    "com.vladsch.flexmark"    % "flexmark-all"               % "0.64.8"
  ).map(_ % Test)

 def apply(): Seq[ModuleID] = compile ++ test

}
