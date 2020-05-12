UNDER DEVELOPMENT

# sappho

sappho is a fanfiction archive scraper written in Scala with an extensive query DSL and support for both archive-aware
and archive-agnostic data extraction.

[Documentation](https://softboot.github.io/sappho/sappho/index.html)

## Supported sites

As of May 9, 2020, sappho supports executing queries and fetching specific stories on Archive of Our Own.
Support for other sites, such as fanfiction.net, is under development.

## Examples

### Extracting story data

To analyze a specific, known story, use the `fetchStoryById` method on the corresponding `Archive` object and then
use the provided properties. For instance:

```scala
import sappho.ao3
import sappho.tags._
import net.ruippeixotog.scalascraper.browser.{Browser, JsoupBrowser}


implicit val browser: Browser = new JsoupBrowser()

val story = ao3.Archive.fetchStoryById(7094683)

//Find title
println(story.title)

//Check word count
println(story.wordCount)

//Find out when Chapter 3 was posted:
println(story.chapters(2).postedOn)

//Read first three paragraphs of the last chapter:
println(story.chapters.last.paragraphs.take(3).mkString("\n\n"))

//Analyze relationship tags:
story.relationships.foreach {
  case Relationship.Platonic(chs) => println("Friends: " + chs)
  case Relationship.Romantic(chs) => println("Romantic partners: " + chs)
  case Relationship.Other(rn) => println("Other relationship: " + rn)
}
```

### Searching for stories

In order to search for stories from a given archive, you need to specify two elements:
* a `Query` defining the conditions stories need to satisfy,
* the `Order` in which stories should appear in the sequence of results.

Both of these elements are defined in a manner completely independent from the archive used, so the same
query could be used for more than one archive.

Queries are defined using the extensive, powerful DSL provided by sappho. For instance:

```scala
import sappho.ao3
import sappho.queries.Order
import sappho.queries.Queries._

import java.time.LocalDate
import net.ruippeixotog.scalascraper.browser.{Browser, JsoupBrowser}


implicit val browser: Browser = new JsoupBrowser()

val query = (fandoms includes "Miraculous Ladybug") &&
  (150000 <= wordCount < 200000 || wordCount == 246310) &&
  updatedOn >= LocalDate.of(2018, 1, 1) &&
  isComplete

val stories = ao3.Archive.search(query, Order.byDateOfUpdate.descending)

for(story <- stories) {
  val title = story.title
  val authors = story.authors.map(_.name).mkString(", ")
  val words = story.wordCount

  println(s"'$title' by $authors ($words)")
}
```

For a complete list of available criteria, check out the [`Queries` object documentation](https://softboot.github.io/sappho/sappho/queries/Queries$.html).

### Authors
* Krzysztof Woźny - [softboot](https://github.com/softboot)

### License

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details.