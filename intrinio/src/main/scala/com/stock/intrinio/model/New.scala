package com.stock.intrinio.model

import akka.NotUsed
import akka.stream.scaladsl.Source
import com.stock.intrinio.sentiment.Sentiment

final case class New(id: String,
               title: String,
               publication_date: String,
               summary: String)

final case class NewSentiment(item: New, sentiment: Sentiment)

final case class Company(id: String,
                   ticker: String,
                   name: String,
                   lei: String,
                   cik: String)

final case class Item(name: String, id: Long)

final case class NewsFile(news: Source[New, NotUsed],
                          company: Company,
                          next_page: String)
