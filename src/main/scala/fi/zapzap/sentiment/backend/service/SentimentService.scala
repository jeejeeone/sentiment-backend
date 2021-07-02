package fi.zapzap.sentiment.backend.service

import fi.zapzap.sentiment.backend.model.IntervalValue.IntervalValue
import fi.zapzap.sentiment.backend.model.{TotalMentionCount, TickerMentionCount}
import zio.Task

trait SentimentService {
  def totalMentionCount(interval: IntervalValue): Task[Seq[TotalMentionCount]]
  def tickerMentions(ticker: String): Task[Seq[TickerMentionCount]]
}