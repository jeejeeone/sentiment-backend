package fi.zapzap.sentiment.backend.service

import fi.zapzap.sentiment.backend.model.IntervalValue.IntervalValue
import fi.zapzap.sentiment.backend.model.{TickerMentionCount, TotalMentionCount}
import zio.Task

trait SentimentService {
  def totalMentionCount(interval: IntervalValue): Task[Seq[TotalMentionCount]]
  def tickerMentions(ticker: String, dayInterval: Int = 500): Task[Seq[TickerMentionCount]]
  def connectDb(): Task[Unit]
}
