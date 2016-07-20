package controllers


import javax.inject._

import dao.{AirportsDAO, CountriesDAO, RunwaysDAO}
import models._
import play.api.libs.json._
import play.api.mvc.{Action, Controller}

import scala.concurrent.Await
import scala.concurrent.duration._

/**
  * Created by tom on 7/16/16.
  */
@Singleton
class QueryController @Inject() (cDao: CountriesDAO, rDao: RunwaysDAO, aDao: AirportsDAO)  extends Controller {

  def index = Action {
    Ok(views.html.query("Query Countries"))
  }

  def airports(countryCode:String) = Action { request =>
    val offset = Integer.parseInt(request.queryString.get("offset").flatMap(_.headOption).getOrElse("0"))
    val limit  = Integer.parseInt(request.queryString.get("limit").flatMap(_.headOption).getOrElse("50"))
    val (airports,totalCount) = aDao.airportsForCountryCode(countryCode, offset, limit)
    val airportsWithRunways:Seq[(Airport,Seq[Runway])] = airports.map { ap =>
      val runways = Await.result(rDao.getRunwaysForAirportIdent(ap.ident), 1 second)
      (ap, runways)
    }
    val message = "List of Airports for " + countryCode + " (" + offset + " of " + totalCount + ")"
    Ok(views.html.airports(message,countryCode,airportsWithRunways, rDao, limit, offset, totalCount))
  }

  def searchCountries = Action { request =>
    val query = request.queryString.get("q").flatMap(_.headOption).getOrElse("")
    val res = cDao.getCountryDataForString(query)
    Ok(Json.toJson(res.map(_.json)) ) as JSON
  }



}
