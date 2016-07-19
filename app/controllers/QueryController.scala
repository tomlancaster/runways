package controllers


import javax.inject._

import dao.{AirportsDAO, CountriesDAO, RunwaysDAO}
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json._
import play.api.mvc.{Action, Controller}

/**
  * Created by tom on 7/16/16.
  */
@Singleton
class QueryController @Inject() (cDao: CountriesDAO, rDao: RunwaysDAO, aDao: AirportsDAO)  extends Controller {

  def index = Action {
    Ok(views.html.query("Query Countries"))
  }

  def airports(countryCode:String) = Action.async { request =>
    val offset = Integer.parseInt(request.queryString.get("offset").flatMap(_.headOption).getOrElse("0"))
    val limit  = Integer.parseInt(request.queryString.get("limit").flatMap(_.headOption).getOrElse("50"))
    val airportsReturn = aDao.airportsForCountryCode(countryCode, offset, limit)
    airportsReturn.map { ap =>
      val message = "List of Airports for " + countryCode + " (" + offset + " of " + ap._2 + ")"
      Ok(views.html.airports(message,countryCode,ap._1, rDao, limit, offset, ap._2))
    }

  }

  def searchCountries = Action.async { request =>
    import scala.concurrent.ExecutionContext.Implicits.global
    val query = request.queryString.get("q").flatMap(_.headOption).getOrElse("")
    val res = cDao.getCountryDataForString(query)
    res.map { r =>
      Ok(Json.toJson(r.map(_.json)) ) as JSON
    }
  }



}
