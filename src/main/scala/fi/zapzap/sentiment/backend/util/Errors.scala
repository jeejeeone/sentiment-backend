package fi.zapzap.sentiment.backend.util

import fi.zapzap.sentiment.backend.response.GeneralErrorResponse.generalErrorResponse
import zhttp.http.{Request, UResponse}
import zio.URIO
import zio.logging.Logging
import zio.logging.Logging.warn

object Errors {
  def handleError(request: Request,
                  exception: Throwable,
                  msg: String): URIO[Logging, UResponse] =
  //TODO: Stacktrace
    warn(
      s"Failed to process request: " + s"path='${request.url.path.asString}', msg='$msg', exception=$exception"
    ) *> generalErrorResponse(msg)

  def handleError(request: Request,
                  exception: Throwable): URIO[Logging, UResponse] =
    handleError(request, exception, exception.getMessage)
}
