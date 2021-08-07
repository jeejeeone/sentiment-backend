package fi.zapzap.sentiment.backend.service.live

import fi.zapzap.sentiment.backend.model.IntervalValue.IntervalValue
import fi.zapzap.sentiment.backend.model.{TickerMentionCount, TotalMentionCount}
import fi.zapzap.sentiment.backend.service.SentimentService
import fi.zapzap.sentiment.backend.util.SafeSQLSyntax.safeSQLSyntax
import scalikejdbc._
import zio.blocking.Blocking
import zio.logging.{Logger, Logging}
import zio.{Has, Task, URLayer, ZLayer}

case class SentimentServiceLive(blockingService: Blocking.Service,
                                logger: Logger[String])
  extends SentimentService {

  override def totalMentionCount(interval: IntervalValue): Task[Seq[TotalMentionCount]] =
    blockingService.effectBlocking {
      DB readOnly { implicit session =>
        sql"""
             SELECT
               mention, COUNT(*), data -> 'Sector' AS sector, data -> 'Industry' AS industry
             FROM
               mentions, ticker_details
             WHERE
               time > NOW() - interval '${safeSQLSyntax(interval.toString)}'
               AND mentions.mention = ticker_details.ticker
             GROUP BY mention, ticker
             ORDER BY count DESC;
           """
          .map(rs =>
            TotalMentionCount(
              rs.string("mention"),
              // Can be null
              Option(rs.string("sector"))
                .map(_.replace("\"", ""))
                .getOrElse(""),
              // Can be null
              Option(rs.string("industry"))
                .map(_.replace("\"", ""))
                .getOrElse(""),
              rs.int("count")
            )
          ).list().apply()
      }
    }

  override def tickerMentions(ticker: String, dayInterval: Int): Task[Seq[TickerMentionCount]] =
    blockingService.effectBlocking {
      DB readOnly { implicit session =>
        sql"""
             SELECT
               time_bucket('1 day', time) AS date, mention, COUNT(*)
             FROM
               mentions
             WHERE
               time > NOW() - interval '${safeSQLSyntax(s"${dayInterval}days")}'
             AND
               mention = $ticker
             GROUP BY date, mention
             ORDER BY date, count DESC;
           """
          .map(rs =>
            TickerMentionCount(
              rs.localDate("date"),
              rs.int("count")
            )
          ).list().apply()
      }
    }
}

object SentimentServiceLive {
  val layer: URLayer[Logging with Has[Blocking.Service], Has[SentimentService]] =
    ZLayer.fromServices[Blocking.Service, Logger[String], SentimentService] {
      (blockingService, logger) => SentimentServiceLive(blockingService, logger)
    }
}