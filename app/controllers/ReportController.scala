package controllers


import javax.inject._

import dao.CountriesDAO
import play.api.mvc._

import scala.concurrent.Await
import scala.concurrent.duration._

/**
  * Created by tom on 7/17/16.
  */
class ReportController @Inject() (cDao: CountriesDAO) extends Controller {

  def index = Action {
    val topTen = Await.result(cDao.getTopTenAirportCountries, 1 minute).map { tt =>
      (tt._1, tt._2, tt._3,Await.result(cDao.getRunwayTypesPerCountry(tt._1), 1 minute),
        Await.result(cDao.getMostCommonRunwayIdents(tt._1,10), 1 minute))
    }
    val bottomTen = Await.result(cDao.getBottomTenAirportCountries, 1 minute).map { bt =>
      (bt._1, bt._2, bt._3,Await.result(cDao.getRunwayTypesPerCountry(bt._1), 1 minute),
        Await.result(cDao.getMostCommonRunwayIdents(bt._1,10), 1 minute))
    }

    Ok(views.html.report("Behold! Your Report!", topTen, bottomTen))
  }



}



