package gatling

import scala.concurrent.duration._

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._

class StressTest extends Simulation {

  private val httpProtocol = http
    .baseUrl("http://localhost:8080")
    .inferHtmlResources()
    .acceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,*/*;q=0.8")
    .acceptEncodingHeader("gzip, deflate")
    .acceptLanguageHeader("de,en-US;q=0.7,en;q=0.3")
    .upgradeInsecureRequestsHeader("1")
    .userAgentHeader("Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:109.0) Gecko/20100101 Firefox/113.0")

  private val userAmount = 3000

  private val stressScenario = scenario("StressTest")
    .exec(
      http("Create Game")
        .post("/controller/newg?name1=Timo&name2=Bence")
    )
    .pause(1.seconds)
    .exec(
      http("Next Player")
        .post("/controller/next")
    )
    .pause(1.seconds)
    .exec(
      http("Take Card")
        .post("/controller/take")
    )
    .pause(1.seconds)
    .exec(
      http("Place Card")
        .get("/controller/get")
    )
    .pause(1.seconds)
    .exec(
      http("Get Game")
        .get("/controller/get")
    )

  setUp(
    stressScenario.inject(stressPeakUsers(userAmount).during(20.seconds))
  ).protocols(httpProtocol)
}