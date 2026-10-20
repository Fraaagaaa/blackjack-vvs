package blackjack;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class SuitTest {

  @ParameterizedTest
  @CsvSource({"HEARTS,h", "DIAMONDS,d", "CLUBS,c", "SPADES,s"})
  void notation(Suit suit, String expected) {
    assertEquals(expected, suit.getName());
  }
}
