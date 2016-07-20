package dao

import javax.inject.Inject

import models._
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.Logger
import slick.driver.SQLiteDriver

import scala.concurrent.{Await, Future}
import scala.concurrent.duration._

class CountriesDAO @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) extends HasDatabaseConfigProvider[SQLiteDriver] {
  import driver.api._

  private val Countries = TableQuery[CountriesTable]

  def all(): Future[Seq[Country]] = db.run(Countries.result)

  def insert(country: Country): Future[Unit] = db.run(Countries += country).map { _ => () }

  def getCountryByCode(code:String): Future[Seq[Country]] = {
    db.run(Countries.filter {c => c.code.toLowerCase === code.toLowerCase()}.result)
  }

  def getCountryDataForString(string:String): Seq[Country] = {
    val cbc = Await.result(getCountryByCode(string), 1 second)
    if (cbc.length > 0) {
      Logger.debug("cbc: " + cbc)
      cbc
    } else {
      val q = Countries.filter { c => c.name.toLowerCase like "%" + string.toLowerCase + "%" }.result
      val cbn = Await.result(db.run(q), 1 second)
      cbn
    }
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

  def getMostCommonRunwayIdents(countryCode:String, numNeeded:Int = 10) : Future[Seq[String]] =  {
    db.run(sql"""SELECT r.le_ident, count(le_ident) as cnt
      FROM runways r inner join airports a ON r.airport_ident = a.ident
      WHERE a.iso_country = $countryCode
      GROUP BY le_ident ORDER BY cnt DESC LIMIT $numNeeded""".as[String])
  }

  private class CountriesTable(tag: Tag) extends Table[Country](tag, "countries") {

    def code = column[String]("code", O.PrimaryKey)
    def name = column[String]("name")

    def * = (code, name) <> (Country.tupled, Country.unapply _)
  }
}