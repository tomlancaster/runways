package services

import javax.inject._

import play.api.Logger
import scala.concurrent.Future
import com.github.tototoshi.csv._
import play.api.Logger

import play.api.Environment


/**
  * Created by tom on 7/16/16.
  */
@Singleton
class CountryQuerier @Inject() (environment: Environment) {

  private val airports:List[Map[String,String]] = CSVReader.open(environment.getFile("conf/resources/random-repo/resources/airports.csv")).allWithHeaders()
  private val countries:List[Map[String,String]] = CSVReader.open(environment.getFile("conf/resources/random-repo/resources/countries.csv")).allWithHeaders()
  private val runways:List[Map[String,String]] = CSVReader.open(environment.getFile("conf/resources/random-repo/resources/runways.csv")).allWithHeaders()

  def getRunwaysForAirportIdent(ident:String): List[Map[String,String]] = {
    runways.filter(_("airport_ident") == ident)
  }

  def getAirportsForCountryCode(countryCode: String, limit: Int, offset: Int): (List[Map[String,String]],Int) = {
    val countryAirports = airports.filter(_("iso_country") == countryCode)
    (countryAirports.drop(offset).take(limit),countryAirports.size)
  }

  def getAirportsForCountryCode(countryCode: String: List[Map[String,String]] = {
    val countryAirports = airports.filter(_("iso_country") == countryCode)
  }

  def getCountryDataForString(s: String): List[Map[String,String]] = {
    val codeMatches = countries.filter { cMap =>
        cMap("code").toLowerCase().contains(s.toLowerCase())
    }
    if (codeMatches.size > 0) {
      codeMatches
    } else {
      countries.filter { cMap =>
        cMap("name").toLowerCase().contains(s.toLowerCase())
      }
    }
  }

  def getTopTenReport: List[(String,Int)] = {
    getCountriesWithAirportCounts.take(10)
  }

  def getBottomTenReport: List[(String,Int)] = {
    getCountriesWithAirportCounts.reverse.take(10).reverse
  }

  def getRunwayTypesPerCountry(countryCode:String): List[(String,Int)] = {
    getAirportsForCountryCode(countryCode).map { ap =>
      getRunwaysForAirportIdent(ap("airport_ident")).groupBy(_("surface")).mapValues(_.size).toList.sortBy(_._2).reverse
  }

  def getCountriesWithAirportCounts:List[(String,Int)] = {
    airports.groupBy(_("iso_country")).mapValues(_.size).toList.sortBy(_._2).reverse
  }



}
