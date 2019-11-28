import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._

class SpringLambdaLoadTest extends Simulation {

  val httpProtocol = http
    .baseUrl("https://7w4r8n19oi.execute-api.eu-central-1.amazonaws.com/Prod")
    .acceptHeader("application/json")
    .doNotTrackHeader("1")

  val scn = scenario("SpringLambdaLoadTest")
    .exec(
      http("add_pet")
        .post("/pet")
        .header("Content-Type", "application/json")
        .body(StringBody("""{"name": "Max", "type": "dog", "birthday": "2010-11-03", "medicalRecord": "bla bla bla"}""")).asJson
    )

  setUp(
    scn.inject(
      rampUsersPerSec(1) to 20 during (60 seconds)
    ).protocols(httpProtocol)
  )
}