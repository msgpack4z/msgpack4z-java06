import sbtrelease._
import xerial.sbt.Sonatype._
import ReleaseStateTransformations._
import build._

ReleasePlugin.extraReleaseCommands

sonatypeSettings

autoScalaLibrary := false

crossPaths := false

name := msgpack4zJava06Name

javacOptions in compile ++= Seq("-target", "7", "-source", "7")

javacOptions in (Compile, doc) ++= Seq("-locale", "en_US")

commands += Command.command("updateReadme")(UpdateReadme.updateReadmeTask)

libraryDependencies ++= (
  ("org.msgpack" % "msgpack" % "0.6.12") ::
  ("com.github.xuwei-k" % "msgpack4z-api" % "0.2.0") ::
  Nil
)

releaseProcess := Seq[ReleaseStep](
  checkSnapshotDependencies,
  inquireVersions,
  runClean,
  runTest,
  setReleaseVersion,
  commitReleaseVersion,
  UpdateReadme.updateReadmeProcess,
  tagRelease,
  ReleaseStep(state => Project.extract(state).runTask(PgpKeys.publishSigned, state)._1),
  setNextVersion,
  commitNextVersion,
  UpdateReadme.updateReadmeProcess,
  pushChanges
)

credentials ++= PartialFunction.condOpt(sys.env.get("SONATYPE_USER") -> sys.env.get("SONATYPE_PASS")){
  case (Some(user), Some(pass)) =>
    Credentials("Sonatype Nexus Repository Manager", "oss.sonatype.org", user, pass)
}.toList

organization := "com.github.xuwei-k"

homepage := Some(url("https://github.com/msgpack4z"))

licenses := Seq("MIT License" -> url("http://www.opensource.org/licenses/mit-license.php"))

scalacOptions ++= (
  "-deprecation" ::
  "-unchecked" ::
  "-Xlint" ::
  "-language:existentials" ::
  "-language:higherKinds" ::
  "-language:implicitConversions" ::
  Nil
) ::: unusedWarnings

scalaVersion := "2.11.8"

crossScalaVersions := scalaVersion.value :: Nil

pomExtra :=
  <developers>
    <developer>
      <id>xuwei-k</id>
      <name>Kenji Yoshida</name>
      <url>https://github.com/xuwei-k</url>
    </developer>
  </developers>
  <scm>
    <url>git@github.com:msgpack4z/msgpack4z-java06.git</url>
    <connection>scm:git:git@github.com:msgpack4z/msgpack4z-java06.git</connection>
    <tag>{if(isSnapshot.value) gitHash else { "v" + version.value }}</tag>
  </scm>


description := "msgpack4z"

Seq(Compile, Test).flatMap(c =>
  scalacOptions in (c, console) ~= {_.filterNot(unusedWarnings.toSet)}
)
