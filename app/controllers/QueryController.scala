package controllers


import javax.inject._
import play.api._
import play.api.mvc._
import play.api.libs.json._

import services.CountryQuerier

/**
  * Created by tom on 7/16/16.
  */
@Singleton
class QueryController @Inject() (countryQuerier: CountryQuerier)  extends Controller {

  def index = Action {
    Ok(views.html.query("Query Countries"))
  }

  def airports(countryCode:String) = Action { request =>
    val offset = Integer.parseInt(request.queryString.get("offset").flatMap(_.headOption).getOrElse("0"))
    val limit  = Integer.parseInt(request.queryString.get("limit").flatMap(_.headOption).getOrElse("50"))
    val (airports,totalCount) = countryQuerier.getAirportsForCountryCode(countryCode, limit, offset)
    val message = "List of Airports for " + countryCode + " (" + offset + " of " + totalCount + ")"
    Ok(views.html.airports(message,countryCode,airports,countryQuerier,limit, offset, totalCount ))
  }


  def searchCountries = Action { request =>
    val query = request.queryString.get("q").flatMap(_.headOption).getOrElse("")
    Ok(Json.toJson(countryQuerier.getCountryDataForString(query))) as JSON
  }

  def getAirportsForCountryCode(countryCode:String) = Action { request =>
    val offset = Integer.parseInt(request.queryString.get("offset").flatMap(_.headOption).getOrElse("0"))
    val limit  = Integer.parseInt(request.queryString.get("limit").flatMap(_.headOption).getOrElse("50"))
    Ok(Json.toJson(countryQuerier.getAirportsForCountryCode(countryCode, limit, offset)._1)) as JSON
  }


}
