package dao

import scala.concurrent.Future

import javax.inject.Inject
import models._
import play.api.db.slick.DatabaseConfigProvider
import play.api.db.slick.HasDatabaseConfigProvider
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import slick.driver.SQLiteDriver

class AirportsDAO @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) extends HasDatabaseConfigProvider[SQLiteDriver] {
  import driver.api._

  val Airports = TableQuery[AirportsTable]

  def all(): Future[Seq[Airport]] = db.run(Airports.result)



  def airportsForCountryCode(code:String) = Airports.filter(_.iso_country === code)

  private def airportsByCountryCodeQuery(code:String) = Airports.filter(_.iso_country === code)

  def airportsForCountryCode(code:String, offset:Int, limit: Int): Future[(Seq[Airport], Int)] =  {
      val ap = db.run(airportsByCountryCodeQuery(code).drop(offset).take(limit).result)
      val tc = db.run(airportsForCountryCode(code).length.result)
      for {
        a <- ap
        c <- tc
      } yield (a,c)
  }

  def insert(airport: Airport): Future[Unit] = db.run(Airports += airport).map { _ => () }

  class AirportsTable(tag: Tag) extends Table[Airport](tag, "AIRPORT") {

    def ident = column[String]("ident", O.PrimaryKey)
    def airport_type = column[String]("type")
    def name = column[String]("name")
    def iso_country = column[String]("iso_country")

    def * = (ident, airport_type,name,iso_country) <> (Airport.tupled, Airport.unapply _)
  }
}