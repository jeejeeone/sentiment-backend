package fi.zapzap.sentiment.backend.service

import zio.Task

trait DatabaseService {
  def connectDb(): Task[Unit]
}
