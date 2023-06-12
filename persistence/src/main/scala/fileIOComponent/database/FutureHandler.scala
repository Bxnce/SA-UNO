package fileIOComponent.database

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}
import concurrent.duration.DurationInt
import scala.util.{Failure, Success}

class FutureHandler {
  implicit val context: scala.concurrent.ExecutionContext = scala.concurrent.ExecutionContext.global

  def resolveBlockingOnFuture[T](futureToResolve: Future[T], duration: Duration = Duration.Inf): T =
    Await.result(futureToResolve, duration)

  def resolveNonBlockingOnFuture[T](futureToResolve: Future[T], numOfRetries: Int = 3): Future[T] = {
    retry(numOfRetries, FutureExceptionList(Vector.empty)) {
      futureToResolve
    }
  }

  private def retry[T](numOfRetries: Int, exceptionList: FutureExceptionList)(operation: => Future[T]): Future[T] = {
    if (numOfRetries == 0)
      println(s"Retry failed finally with exception: [${exceptionList.list.toString()}]")
      Future.failed(exceptionList)
    else
      operation transformWith {
        case Success(value) => Future(value)
        case Failure(exception) =>
          val delay = 10.milliseconds
          Thread.sleep(delay.toMillis)
          println(s"Error occurred on request, with try number[$numOfRetries], Retry after [$delay] [ms]")
          retry(numOfRetries - 1, exceptionList.copy(exceptionList.list :+ (numOfRetries, exception))) {
            operation
          }
      }
  }

  private case class FutureExceptionList(list: Vector[(Int, Throwable)]) extends Exception {
    override def toString: String = {
      val sb = new StringBuilder
      sb.append("FutureExceptionList(")
      list.foreach { case (numOfRetries, exception) =>
        sb.append(s"Retry number [$numOfRetries] failed with exception: [$exception]")
      }
      sb.append(")")
      sb.toString()
    }
  }
}
