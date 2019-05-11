package com.stock.intrinio.model

final case class New(id: String,
               title: String,
               publication_date: String,
               summary: String)

final case class Company(id: String,
                   ticker: String,
                   name: String,
                   lei: String,
                   cik: String)

final case class Item(name: String, id: Long)
