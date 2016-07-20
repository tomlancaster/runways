import org.scalatestplus.play._
import play.api.test._
import play.api.test.Helpers._

/**
 * Add your spec here.
 * You can mock out a whole application including requests, plugins etc.
 * For more information, consult the wiki.
 */
class ApplicationSpec extends PlaySpec with OneAppPerTest {

  "Routes" should {

    "send 404 on a bad request" in  {
      route(app, FakeRequest(GET, "/boum")).map(status(_)) mustBe Some(NOT_FOUND)
    }

  }

  "HomeController" should {

    "render the index page" in {
      val home = route(app, FakeRequest(GET, "/")).get

      status(home) mustBe OK
      contentType(home) mustBe Some("text/html")
      contentAsString(home) must include ("Query and Report on Airports and Runways By Selecting an Option Above")
    }

  }

  "QueryController" should {
    "render the country query page" in {
      val query = route(app, FakeRequest(GET, "/query")).get

      status(query) mustBe OK
      contentType(query) mustBe Some("text/html")
      contentAsString(query) must include ("Query Countries")
    }

    "render the US page" in {
      val us = route(app, FakeRequest(GET, "/query/airports/US")).get

      status(us) mustBe OK
      contentType(us) mustBe Some("text/html")
      contentAsString(us) must include ("Total Rf Heliport")
    }
  }

  "ReportController" should {
    "render the reports page" in {
      val query = route(app, FakeRequest(GET, "/report")).get

      status(query) mustBe OK
      contentType(query) mustBe Some("text/html")
      contentAsString(query) must include ("21501")
    }
  }

}
