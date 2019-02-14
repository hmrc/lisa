import sbt._
import play.sbt.PlayImport._
import play.core.PlayVersion
import uk.gov.hmrc.SbtAutoBuildPlugin
import uk.gov.hmrc.sbtdistributables.SbtDistributablesPlugin
import uk.gov.hmrc.versioning.SbtGitVersioning

object AppDependencies {

  val compile = Seq(
    ws,
    "uk.gov.hmrc" %% "bootstrap-play-25" % "4.8.0",
    "uk.gov.hmrc" %% "auth-client" % "2.19.0-play-25",
    "uk.gov.hmrc" %% "domain" % "5.3.0"
  )

  val test = Seq(
    "org.scalatest" %% "scalatest" % "2.2.6",
    "org.pegdown" % "pegdown" % "1.6.0",
    "org.mockito" % "mockito-core" % "1.9.0",
    "com.typesafe.play" %% "play-test" % PlayVersion.current,
    "org.scalatestplus.play" %% "scalatestplus-play" % "2.0.0"
  )

  def apply() = compile ++ test
}
