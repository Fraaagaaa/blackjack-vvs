package blackjack;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class RankTest {

  @ParameterizedTest
  @CsvSource({"TWO,0", "TEN,8", "JACK,9", "QUEEN,10", "KING,11", "ACE,12"})
  void valueTableIndex(Rank rank, int expected) {
    assertEquals(expected, rank.getIndex());
  }

  @ParameterizedTest
  @CsvSource({"TWO,2", "TEN,10", "JACK,J", "QUEEN,Q", "KING,K", "ACE,A"})
  void notation(Rank rank, String expected) {
    assertEquals(expected, rank.getName());
  }
}
