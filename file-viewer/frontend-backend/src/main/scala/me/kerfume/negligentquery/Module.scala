package me.kerfume.negligentquery

import Outside._

class Module {
  val parseAndBuild: OutsideF[Unit] = for {
    query <- getNegligentQuery()
    // parse query
    // buildSql
    sql: String = ???
    _ <- printSQL(sql)
  } yield ()
}
