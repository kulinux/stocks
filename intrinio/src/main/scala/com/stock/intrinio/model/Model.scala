package com.stock.intrinio.model

import akka.NotUsed
import akka.stream.scaladsl.Source
import com.stock.intrinio.sentiment.Sentiment


//News model
final case class New(id: String,
                     title: String,
                     publication_date: String,
                     summary: String)

final case class NewSentiment(item: New,
                              sentiment: Sentiment)


//Stock price model

final case class StockPrice( date: String,
                             intraperiod: Boolean,
                             frequency: String,
                             open: Double,
                             high: Double,
                             low: Double,
                             close: Double,
                             volume: Double,
                             adj_open: Double,
                             adj_high: Double,
                             adj_low: Double,
                             adj_close: Double,
                             adj_volume: Double )


