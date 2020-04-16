package sappho.ao3

trait Chapters extends sappho.Chapters with (Int => Chapter)  {
  def apply(i: Int): Chapter
}
