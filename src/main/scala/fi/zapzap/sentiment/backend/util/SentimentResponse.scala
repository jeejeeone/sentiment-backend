package fi.zapzap.sentiment.backend.util
import fi.zapzap.sentiment.backend.Env.SentimentBackend
import zhttp.http.{Request, Response, UResponse}
import zio.ZIO
import zio.json.{EncoderOps, JsonEncoder}

object SentimentResponse {
  def toJsonResponse[A](data: A)
                       (implicit encoder: JsonEncoder[A]): UResponse =
    Response.jsonString(data.toJson)

  def withDefaultErrorHandling[A](request: Request)
                                 (effect: ZIO[SentimentBackend, Throwable, UResponse]) =
    effect.catchAll(exc => Errors.handleError(request, exc))
}
