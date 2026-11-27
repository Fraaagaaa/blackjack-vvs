package blackjack;

import static org.junit.jupiter.api.Assertions.assertTimeout;

import java.time.Duration;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("performance")
class DeckPerformanceTest {
  @Test
  void unitDeckHandlesOneHundredThousandRoundTripsWithinFiveSeconds() {
    assertTimeout(
        Duration.ofSeconds(5),
        () -> {
          Deck<Integer> deck = new Deck<>();
          for (int i = 0; i < 100000; i++) {
            deck.returnOneToBottom(i);
            deck.takeOneFromTop();
          }
        });
  }

  @Test
  void unitDeckShufflesAndDrainsTenThousandFullDecksWithinFiveSeconds() {
    assertTimeout(
        Duration.ofSeconds(5),
        () -> {
          for (int round = 0; round < 10_000; round++) {
            Deck<Integer> deck = new Deck<>();
            for (int card = 0; card < 52; card++) {
              deck.returnOneToBottom(card);
            }
            deck.shuffle();
            for (int card = 0; card < 52; card++) {
              deck.takeOneFromTop();
            }
          }
        });
  }
}
