package dao

import scala.concurrent.Future
import javax.inject.Inject

import models._
import play.api.db.slick.DatabaseConfigProvider
import play.api.db.slick.HasDatabaseConfigProvider
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import slick.driver.SQLiteDriver


class RunwaysDAO @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) extends HasDatabaseConfigProvider[SQLiteDriver] {
  import driver.api._

  val Runways = TableQuery[RunwaysTable]

  def all(): Future[Seq[Runway]] = db.run(Runways.result)

  def insert(runway: Runway): Future[Unit] = db.run(Runways += runway).map { _ => () }

  def getRunwaysForAirportIdent(ident:String):Future[Seq[Runway]] = {
    db.run(Runways.filter(_.airport_ident === ident).result)
  }

  class RunwaysTable(tag: Tag) extends Table[Runway](tag, "runways") {

    def airport_ident = column[String]("airport_ident", O.PrimaryKey)
    def surface = column[String]("surface")
    def lighted = column[Boolean]("lighted")
    def closed = column[Boolean]("closed")
    def le_ident = column[Option[String]]("le_ident")
    def he_ident = column[Option[String]]("he_ident")
    def length_ft = column[Int]("length_ft")
    def width_ft = column[Int]("width_ft")

    def * = (airport_ident, surface, lighted, closed, length_ft, width_ft, le_ident, he_ident ) <> (Runway.tupled, Runway.unapply _)
  }
}