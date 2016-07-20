package dao

import javax.inject.Inject

import models._
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import slick.driver.SQLiteDriver

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

class AirportsDAO @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) extends HasDatabaseConfigProvider[SQLiteDriver] {
  import driver.api._

  def Airports = TableQuery[AirportsTable]

  def all(): Future[Seq[Airport]] = db.run(Airports.result)



  def airportsForCountryCode(code:String) = Airports.filter(_.iso_country === code)

  def airportsByCountryCodeQuery(code:String) = Airports.filter(_.iso_country === code)

  def airportsForCountryCode(code:String, offset:Int, limit: Int): (Seq[Airport], Int) =  {
    val ap = Await.result(db.run(airportsByCountryCodeQuery(code).drop(offset).take(limit).result), 1 second)
    val tc = Await.result(db.run(airportsForCountryCode(code).length.result), 1 second)
    (ap,tc)
  }

  def insert(airport: Airport): Future[Unit] = db.run(Airports += airport).map { _ => () }

  class AirportsTable(tag: Tag) extends Table[Airport](tag, "airports") {

    def ident = column[String]("ident", O.PrimaryKey)
    def airport_type = column[String]("type")
    def name = column[String]("name")
    def iso_country = column[String]("iso_country")

    def * = (ident, airport_type,name,iso_country) <> (Airport.tupled, Airport.unapply _)
  }
}