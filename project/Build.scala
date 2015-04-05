import sbt._, Keys._
import sbtrelease._
import xerial.sbt.Sonatype._
import ReleaseStateTransformations._
import com.typesafe.sbt.pgp.PgpKeys

object build extends Build {

  private def gitHash: String = scala.util.Try(
    sys.process.Process("git rev-parse HEAD").lines_!.head
  ).getOrElse("master")

  private val msgpack4zJava06Name = "msgpack4z-java06"

  val modules = msgpack4zJava06Name :: Nil

  lazy val msgpack4zJava06 = Project("msgpack4z-java06", file(".")).settings(
    ReleasePlugin.releaseSettings ++ ReleasePlugin.extraReleaseCommands ++ sonatypeSettings: _*
  ).settings(
    autoScalaLibrary := false,
    crossPaths := false,
    name := msgpack4zJava06Name,
    javacOptions in compile ++= Seq("-target", "7", "-source", "7"),
    javacOptions in (Compile, doc) ++= Seq("-locale", "en_US"),
    commands += Command.command("updateReadme")(UpdateReadme.updateReadmeTask),
    libraryDependencies ++= (
      ("org.msgpack" % "msgpack" % "0.6.11") ::
      ("com.github.xuwei-k" % "msgpack4z-api" % "0.1.0") ::
      Nil
    ),
    ReleasePlugin.ReleaseKeys.releaseProcess := Seq[ReleaseStep](
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
    ),
    credentials ++= PartialFunction.condOpt(sys.env.get("SONATYPE_USER") -> sys.env.get("SONATYPE_PASS")){
      case (Some(user), Some(pass)) =>
        Credentials("Sonatype Nexus Repository Manager", "oss.sonatype.org", user, pass)
    }.toList,
    organization := "com.github.xuwei-k",
    homepage := Some(url("https://github.com/msgpack4z")),
    licenses := Seq("MIT License" -> url("http://www.opensource.org/licenses/mit-license.php")),
    scalacOptions ++= (
      "-deprecation" ::
      "-unchecked" ::
      "-Xlint" ::
      "-language:existentials" ::
      "-language:higherKinds" ::
      "-language:implicitConversions" ::
      Nil
    ),
    scalacOptions in compile ++= (
      "-Ywarn-unused" ::
      "-Ywarn-unused-import" ::
      Nil
    ),
    scalaVersion := "2.11.5",
    crossScalaVersions := scalaVersion.value :: Nil,
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
    ,
    description := "msgpack4z"
  )

}