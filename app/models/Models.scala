package models

import play.api.libs.json._

case class Country(code:String, name:String) {
  def json:JsObject = {
    Json.obj(
      "code" -> code,
      "name" -> name
    )
  }
}

case class Airport(ident:String, airport_type:String, name:String, iso_country:String) {
  def json:JsObject = {
    Json.obj(
      "ident" -> ident,
      "airport_type" -> airport_type,
      "name" -> name,
      "iso_country" -> iso_country
    )
  }
}

case class Runway(airport_ident:String, surface:String, lighted:Boolean, closed:Boolean, length_ft:Int, width_ft:Int, le_ident:Option[String], he_ident:Option[String]) {
  def json:JsObject = {
    Json.obj(
      "airport_ident" -> airport_ident,
      "surface" -> surface,
      "lighted" -> lighted,
      "closed" -> closed,
      "length_ft" -> length_ft,
      "width_ft" -> width_ft,
      "le_ident" -> le_ident,
      "he_ident" -> he_ident
    )
  }
}