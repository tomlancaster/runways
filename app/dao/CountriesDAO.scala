package dao

import scala.concurrent.Future

import javax.inject.Inject
import models._
import play.api.db.slick.DatabaseConfigProvider
import play.api.db.slick.HasDatabaseConfigProvider
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import slick.driver.SQLiteDriver

class CountriesDAO @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) extends HasDatabaseConfigProvider[SQLiteDriver] {
  import driver.api._

  private val Countries = TableQuery[CountriesTable]

  def all(): Future[Seq[Country]] = db.run(Countries.result)

  def insert(country: Country): Future[Unit] = db.run(Countries += country).map { _ => () }

  def count(): Future[Int] = {
    db.run(Countries.map(_.code).length.result)
  }

  def count(filter:String): Future[Int] = {
    db.run(Countries.filter { c => c.name.toLowerCase like filter.toLowerCase }.length.result)
  }

  def getCountryByCode(code:String): Future[Seq[Country]] = {
    db.run(Countries.filter {c => c.code.toLowerCase === code.toLowerCase()}.result)
  }

  def getCountryDataForString(string:String): (Future[Seq[Country]]) = {
    val cbc = getCountryByCode(string)
    cbc.map { c =>
      return (cbc)
    }
    db.run(Countries.filter { c => c.name.toLowerCase like string.toLowerCase}.result)
  }

  private class CountriesTable(tag: Tag) extends Table[Country](tag, "COUNTRY") {

    def code = column[String]("code", O.PrimaryKey)
    def name = column[String]("name")

    def * = (code, name) <> (Country.tupled, Country.unapply _)
  }
}