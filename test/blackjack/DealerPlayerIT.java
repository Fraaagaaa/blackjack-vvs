package blackjack;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;

class DealerPlayerIT {

  @ParameterizedTest
  @CsvSource({"WIN,110", "LOSE,90", "TIE,100", "SURRENDER,95", "WIN_BJ,115"})
  void realSettlement(Outcome outcome, int expected) {
    PlayerCPU player = new PlayerCPU(100, 1), casino = new PlayerCPU(1000, 1);
    Dealer dealer = new Dealer(21);
    dealer.placeBet(player, player.getHand(), 10);
    dealer.resolveBet(player, casino, player.getHand(), outcome);
    assertEquals(expected, player.getAccountBalance());
  }

  @ParameterizedTest
  @EnumSource(Outcome.class)
  void settlementConservesMoney(Outcome outcome) {
    PlayerCPU player = new PlayerCPU(100, 1), casino = new PlayerCPU(1000, 1);
    Dealer dealer = new Dealer(21);
    dealer.placeBet(player, player.getHand(), 10);
    dealer.resolveBet(player, casino, player.getHand(), outcome);
    assertEquals(1100, player.getAccountBalance() + casino.getAccountBalance());
  }

  @Test
  void doubleDebitsAndDeals() {
    PlayerCPU p = new PlayerCPU(100, 1);
    Dealer d = new Dealer(21);
    d.placeBet(p, p.getHand(), 10);
    new ActionDouble(d, d, p.getHand()).execute();
    assertEquals(20, d.getBet(p.getHand()));
  }

  @Test
  void hitAddsOneRealCard() {
    PlayerCPU p = new PlayerCPU(100, 1);
    Dealer d = new Dealer(21);
    new ActionHit(d, p.getHand(), 21).execute();
    assertEquals(1, p.getHand().countElements());
  }

  @Test
  void surrenderAffectsComparison() {
    PlayerCPU p = new PlayerCPU(100, 1), c = new PlayerCPU(1000, 1);
    new ActionSurrender(p.getHand()).execute();
    assertEquals(Outcome.SURRENDER, new Dealer(21).compareHands(p.getHand(), c.getHand()));
  }

  @Test
  void deckContains52UniqueCardsAndThenExhausts() {
    Dealer d = new Dealer(21);
    PlayerCPU p = new PlayerCPU(100, 1);
    for (int i = 0; i < 53; i++) {
      d.dealOneCard(p, Face.UP);
    }
    Set<String> cards = new HashSet<>();
    p.getHand().iterator().forEachRemaining(c -> cards.add(c.toString()));
    assertEquals(52, cards.size());
  }

  @Test
  void collectionAllowsReuse() {
    Dealer d = new Dealer(21);
    PlayerCPU p = new PlayerCPU(100, 1);
    for (int i = 0; i < 52; i++) {
      d.dealOneCard(p, Face.UP);
    }
    d.collectCards(p.returnAllCards());
    d.shuffle();
    d.dealOneCard(p, Face.UP);
    assertEquals(1, p.getHand().countElements());
  }

  @Test
  void cpuStandsOnSoftSeventeen() {
    PlayerCPU p = new PlayerCPU(100, 1);
    Dealer d = new Dealer(21);
    d.dealOneCard(p, new Card(Rank.ACE, Suit.HEARTS), Face.UP);
    d.dealOneCard(p, new Card(Rank.SIX, Suit.HEARTS), Face.UP);
    p.play(d, d);
    assertEquals(2, p.getHand().countElements());
  }

  @Test
  void nextRoundCanReuseSettledHand() {
    PlayerCPU p = new PlayerCPU(100, 1), c = new PlayerCPU(1000, 1);
    Dealer d = new Dealer(21);
    d.placeBet(p, p.getHand(), 10);
    d.resolveBet(p, c, p.getHand(), Outcome.TIE);
    d.placeBet(p, p.getHand(), 5);
    assertEquals(5, d.getBet(p.getHand()));
  }

  @Test
  void hitReachingBustTerminatesTurn() {
    PlayerCPU p = new PlayerCPU(100, 1);
    Dealer d = new Dealer(21);
    d.dealOneCard(p, new Card(Rank.KING, Suit.HEARTS), Face.UP);
    d.dealOneCard(p, new Card(Rank.QUEEN, Suit.CLUBS), Face.UP);
    assertEquals(0, new ActionHit(d, p.getHand(), 21).execute().size());
  }
}
