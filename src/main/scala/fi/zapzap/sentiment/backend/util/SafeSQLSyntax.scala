package fi.zapzap.sentiment.backend.util

import org.postgresql.core.Utils
import scalikejdbc.SQLSyntax

object SafeSQLSyntax {
  def safeSQLSyntax(value: String): SQLSyntax = {
    val escapedLiteral = Utils.escapeLiteral(null, value, true)
    SQLSyntax.createUnsafely(escapedLiteral.toString())
  }
}
