package fi.zapzap.sentiment.backend.service.live

import fi.zapzap.sentiment.backend.model.IntervalValue.IntervalValue
import fi.zapzap.sentiment.backend.model.{Ticker, TickerMentionCount, TotalMentionCount}
import fi.zapzap.sentiment.backend.service.SentimentService
import zio.{Has, Task, ULayer, ZIO, ZLayer}

import java.time.LocalDate

case class SentimentServiceLive() extends SentimentService {
  override def totalMentionCount(interval: IntervalValue): Task[Seq[TotalMentionCount]] =
    ZIO.succeed(Seq(
      TotalMentionCount(Ticker("MNMD", "Shroomstry", "Shrooms", 12123), 5)
    ))

  override def tickerMentions(ticker: String): Task[Seq[TickerMentionCount]] =
    ZIO.succeed(Seq(
      TickerMentionCount(LocalDate.now(), 2)
    ))
}

object SentimentServiceLive {
  val layer: ULayer[Has[SentimentService]] = ZLayer.succeed(SentimentServiceLive())
}