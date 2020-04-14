package sappho

import java.net.URL

import net.ruippeixotog.scalascraper.browser.Browser

trait Archive {
  def name: String
  def abbreviation: String
  def homepage: URL

  def fetchStoryById(storyId: Long)(implicit browser: Browser): Story
}
