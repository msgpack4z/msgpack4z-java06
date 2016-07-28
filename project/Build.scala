object build {

  def gitHash: String = scala.util.Try(
    sys.process.Process("git rev-parse HEAD").lines_!.head
  ).getOrElse("master")

  val msgpack4zJava06Name = "msgpack4z-java06"

  val modules = msgpack4zJava06Name :: Nil

  val unusedWarnings = (
    "-Ywarn-unused" ::
    "-Ywarn-unused-import" ::
    Nil
  )

}
