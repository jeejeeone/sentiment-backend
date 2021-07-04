package fi.zapzap.sentiment.backend.response

import zhttp.http.HttpError.InternalServerError
import zhttp.http.{Response, UResponse}
import zio.{UIO, ZIO}

object GeneralErrorResponse {
  def generalErrorResponse(msg: String): UIO[UResponse] =
    ZIO.succeed(Response.fromHttpError(InternalServerError(s"""{"error": "$msg"}""")))
}
