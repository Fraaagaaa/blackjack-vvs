package blackjack;

import static org.junit.jupiter.api.Assertions.assertTimeout;

import java.time.Duration;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("performance")
class DealerPerformanceIT {

  @Test
  void dealerRecyclesTenThousandFullDecksWithinTenSeconds() {
    assertTimeout(
        Duration.ofSeconds(10),
        () -> {
          Dealer dealer = new Dealer(21);
          PlayerCPU player = new PlayerCPU(100, 1);
          for (int round = 0; round < 10_000; round++) {
            for (int card = 0; card < 52; card++) {
              dealer.dealOneCard(player, Face.UP);
            }
            dealer.collectCards(player.returnAllCards());
          }
        });
  }

  @Test
  void dealerSettlesOneHundredThousandBetsWithinFiveSeconds() {
    assertTimeout(
        Duration.ofSeconds(5),
        () -> {
          Dealer dealer = new Dealer(21);
          PlayerCPU player = new PlayerCPU(1, 1);
          PlayerCPU casino = new PlayerCPU(1, 1);
          for (int round = 0; round < 100_000; round++) {
            dealer.placeBet(player, player.getHand(), 1);
            dealer.resolveBet(player, casino, player.getHand(), Outcome.TIE);
          }
        });
  }
}
