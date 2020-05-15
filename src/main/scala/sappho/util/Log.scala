package sappho.util

import java.net.URL

import com.typesafe.scalalogging.Logger
import net.ruippeixotog.scalascraper.browser.Browser
import net.ruippeixotog.scalascraper.model.Document

object Log {
  implicit class LoggingBrowser(val browser: Browser) extends AnyVal {
    def get(url: URL, logger: Logger): Document = get(url.toString, logger)

    def get(url: String, logger: Logger): Document = {
      logger.info("GET " + url)
      browser.get(url)
    }
  }
}
