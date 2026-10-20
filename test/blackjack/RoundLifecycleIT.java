package blackjack;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.Collection;
import java.util.List;
import org.junit.jupiter.api.Test;

class RoundLifecycleIT {

  PlayerHuman human(String script) {
    return new PlayerHuman(100, 1, new BufferedReader(new StringReader(script)));
  }

  @Test
  void splitThenResetRestoresPrimaryHand() {
    PlayerHuman p = human("");
    Dealer d = new Dealer(21);
    Hand h = p.getHand();
    Card a = new Card(Rank.EIGHT, Suit.HEARTS), b = new Card(Rank.EIGHT, Suit.CLUBS);
    d.dealOneCard(h, a, Face.UP);
    d.dealOneCard(h, b, Face.UP);
    d.placeBet(p, h, 10);
    new ActionSplit(d, d, h, b).execute();
    p.returnAllCards();
    assertTrue(h.isAllowedSurrender());
  }

  @Test
  void splitPreservesTwoCardsPerHand() {
    PlayerHuman p = human("");
    Dealer d = new Dealer(21);
    Hand h = p.getHand();
    Card b = new Card(Rank.EIGHT, Suit.CLUBS);
    d.dealOneCard(h, new Card(Rank.EIGHT, Suit.HEARTS), Face.UP);
    d.dealOneCard(h, b, Face.UP);
    d.placeBet(p, h, 10);
    Collection<Hand> result = new ActionSplit(d, d, h, b).execute();
    assertTrue(result.stream().allMatch(x -> x.countElements() == 2));
  }

  @Test
  void humanHitThenStand() {
    PlayerHuman p = human("1\n\n");
    Dealer d = new Dealer(21);
    d.placeBet(p, p.getHand(), 1);
    d.dealOneCard(p, new Card(Rank.TWO, Suit.HEARTS), Face.UP);
    d.dealOneCard(p, new Card(Rank.THREE, Suit.CLUBS), Face.UP);
    p.play(d, d);
    assertEquals(3, p.getHand().countElements());
  }

  @Test
  void humanDouble() {
    PlayerHuman p = human("2\n");
    Dealer d = new Dealer(21);
    d.placeBet(p, p.getHand(), 10);
    d.dealOneCard(p, new Card(Rank.TWO, Suit.HEARTS), Face.UP);
    d.dealOneCard(p, new Card(Rank.THREE, Suit.CLUBS), Face.UP);
    p.play(d, d);
    assertEquals(20, d.getBet(p.getHand()));
  }

  @Test
  void humanSurrender() {
    PlayerHuman p = human("3\n");
    Dealer d = new Dealer(21);
    d.placeBet(p, p.getHand(), 10);
    d.dealOneCard(p, new Card(Rank.TWO, Suit.HEARTS), Face.UP);
    d.dealOneCard(p, new Card(Rank.THREE, Suit.CLUBS), Face.UP);
    p.play(d, d);
    assertTrue(p.getHand().isSurrendered());
  }

  @Test
  void humanSplitThenStandBothHands() {
    PlayerHuman p = human("2\n\n\n");
    Dealer d = new Dealer(21);
    d.placeBet(p, p.getHand(), 10);
    d.dealOneCard(p, new Card(Rank.EIGHT, Suit.HEARTS), Face.UP);
    d.dealOneCard(p, new Card(Rank.EIGHT, Suit.CLUBS), Face.UP);
    assertEquals(2, p.play(d, d).size());
  }

  @Test
  void invalidActionIsRetried() {
    PlayerHuman p = human("garbage\n999\n\n");
    Dealer d = new Dealer(21);
    d.placeBet(p, p.getHand(), 1);
    assertEquals(List.of(p.getHand()), p.play(d, d));
  }

  @Test
  void surrenderedHandResetsForNextRound() {
    PlayerHuman p = human("");
    Hand h = p.getHand();
    new ActionSurrender(h).execute();
    p.returnAllCards();
    org.junit.jupiter.api.Assertions.assertFalse(h.isSurrendered());
  }

  @Test
  void repeatedHitKeepsTurnActive() {
    PlayerHuman p = human("");
    Dealer d = new Dealer(21);
    new ActionHit(d, p.getHand(), 21).execute();
    assertEquals(List.of(p.getHand()), new ActionHit(d, p.getHand(), 21).execute());
  }

  @Test
  void splitHandCanSplitAgainWhenNewCardMatches() {
    PlayerHuman p = human("");
    Dealer d = new Dealer(21);
    Hand h = p.getHand();
    Card duplicate = new Card(Rank.TWO, Suit.CLUBS);
    d.dealOneCard(h, new Card(Rank.TWO, Suit.SPADES), Face.UP);
    d.dealOneCard(h, duplicate, Face.UP);
    d.placeBet(p, h, 10);
    Hand split = new ActionSplit(d, d, h, duplicate).execute().iterator().next();
    // Unshuffled dealer starts with TWO then THREE: split now contains TWO + TWO.
    assertEquals(
        2, new ActionSplit(d, d, split, split.getDuplicatedCards().get(0)).execute().size());
  }
}
