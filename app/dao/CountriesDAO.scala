package dao

import javax.inject.Inject

import models._
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import slick.driver.SQLiteDriver

import scala.concurrent.Future

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

  def getTopTenAirportCountries: Future[Seq[(String,String,Int)]] = {
    db.run(sql"""select c.code, c.name, count(a.ident) as cnt
      FROM countries c inner join airports a on c.code = a.iso_country
      GROUP BY c.code ORDER BY cnt DESC LIMIT 10""".as[(String,String,Int)])
  }

  def getBottomTenAirportCountries: Future[Seq[(String,String,Int)]] = {
    db.run(sql"""select c.code, c.name, count(a.ident) as cnt
      FROM countries c inner join airports a on c.code = a.iso_country
      GROUP BY c.code ORDER BY cnt ASC LIMIT 10""".as[(String,String,Int)])
  }

  def getRunwayTypesPerCountry(code:String): Future[Seq[String]] = {
    db.run(sql"""SELECT DISTINCT(r.surface)
      FROM runways r INNER JOIN airports a ON r.airport_ident = a.ident
      WHERE r.surface IS NOT NULL AND r.surface != '' AND a.iso_country = $code""".as[String])
  }

  private class CountriesTable(tag: Tag) extends Table[Country](tag, "countries") {

    def code = column[String]("code", O.PrimaryKey)
    def name = column[String]("name")

    def * = (code, name) <> (Country.tupled, Country.unapply _)
  }
}