package gatling

import scala.concurrent.duration._

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._

class SpikeTestPersistence extends Simulation {

  private val httpProtocol = http
    .baseUrl("http://localhost:8081")
    .inferHtmlResources()
    .acceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,*/*;q=0.8")
    .acceptEncodingHeader("gzip, deflate")
    .acceptLanguageHeader("de,en-US;q=0.7,en;q=0.3")
    .upgradeInsecureRequestsHeader("1")
    .userAgentHeader("Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:109.0) Gecko/20100101 Firefox/113.0")

  private val dbLoadScenario = scenario("DB Load Scenario")
    .exec(
      http("Load Game")
        .get("/persistence/dbload")
    )

  private val dbSaveScenario = scenario("DB Save Scenario")
    .exec(
      http("Save Game")
        .put("/persistence/dbstore")
        .body(RawFileBody("C:\\Users\\haase\\Desktop\\SA-UNO\\persistence\\src\\test_body.json"))
    )

  setUp(
    dbLoadScenario.inject(
      rampUsers(10).during(10.seconds),
      atOnceUsers(300),
      rampUsers(10).during(10.seconds)
    ),
  dbSaveScenario.inject(
    rampUsers(10).during(10.seconds),
    atOnceUsers(300),
    rampUsers(10).during(10.seconds)
  )).protocols(httpProtocol)
}