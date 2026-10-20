package blackjack;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class OutcomeTest {

  @ParameterizedTest
  @CsvSource({"WIN_BJ,1.25", "WIN,1.0", "TIE,0.5", "LOSE,0.0", "SURRENDER,0.25"})
  void potMultiplier(Outcome outcome, double expected) {
    assertEquals(expected, outcome.getMultiplier());
  }

  @ParameterizedTest
  @CsvSource({
    "WIN_BJ,PLAYER WINS 3:2",
    "WIN,PLAYER WINS",
    "TIE,PLAYER TIES",
    "LOSE,PLAYER LOSES",
    "SURRENDER,PLAYER SURRENDERS"
  })
  void outcomeText(Outcome outcome, String expected) {
    assertEquals(expected, outcome.toString());
  }
}
