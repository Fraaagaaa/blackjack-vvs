package blackjack;

import static org.junit.jupiter.api.Assertions.assertTimeout;

import java.time.Duration;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("performance")
class DeckPerformanceTest {
  @Test
  void unitDeckHandlesOneHundredThousandRoundTripsWithinFiveSeconds() {
    // Engineering smoke budget, not a supplied product requirement or benchmark.
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
  void integratedDealerRecyclesTenThousandDecksWithinTenSeconds() {
    assertTimeout(
        Duration.ofSeconds(10),
        () -> {
          Dealer dealer = new Dealer(21);
          PlayerCPU player = new PlayerCPU(100, 1);
          for (int round = 0; round < 10000; round++) {
            for (int card = 0; card < 52; card++) {
              dealer.dealOneCard(player, Face.UP);
            }
            dealer.collectCards(player.returnAllCards());
          }
        });
  }
}
