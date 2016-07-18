package controllers


import javax.inject._
import play.api._
import play.api.mvc._
import play.api.libs.json._

import services.CountryQuerier

/**
  * Created by tom on 7/17/16.
  */
class ReportController @Inject() (countryQuerier: CountryQuerier) extends Controller {

  def index = Action {
    val topTen = countryQuerier.getTopTenReport
    val bottomTen = countryQuerier.getBottomTenReport
    Ok(views.html.report("Behold! Your Report!", topTen, bottomTen, countryQuerier))
  }

}
