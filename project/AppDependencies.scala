import play.sbt.PlayImport.ws
import sbt.*

object AppDependencies {

  val bootstrapPlayVersion = "9.19.0"

 private val compile = Seq(
    ws,
    "uk.gov.hmrc"             %% "bootstrap-backend-play-30" % bootstrapPlayVersion,
    "uk.gov.hmrc"             %% "domain-play-30"            % "11.0.0"
   )

    private val test = Seq(
    "uk.gov.hmrc"             %% "bootstrap-test-play-30"    % bootstrapPlayVersion
  ).map(_ % Test)

 def apply(): Seq[ModuleID] = compile ++ test

}
