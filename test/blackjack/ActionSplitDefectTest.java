package blackjack;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.Collection;
import java.util.List;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

class ActionSplitDefectTest {

  @Test
  @Tag("known-bug")
  void BUG001_splitAcesMustReceiveExactlyOneAdditionalCardEach() {
    PlayerHuman p = new PlayerHuman(100, 1, new BufferedReader(new StringReader("")));
    Dealer d = new Dealer(21);
    Card a = new Card(Rank.ACE, Suit.HEARTS), b = new Card(Rank.ACE, Suit.CLUBS);
    d.dealOneCard(p, a, Face.UP);
    d.dealOneCard(p, b, Face.UP);
    d.placeBet(p, p.getHand(), 10);
    Collection<Hand> hands = new ActionSplit(d, d, p.getHand(), b).execute();
    assertEquals(List.of(2, 2), hands.stream().map(Hand::countElements).toList());
  }

  @Test
  @Tag("known-bug")
  void BUG002_rejectedSplitMustNotLoseOriginalCard() {
    PlayerHuman p = new PlayerHuman(100, 1, new BufferedReader(new StringReader("")));
    Dealer d = new Dealer(21);
    Card a = new Card(Rank.EIGHT, Suit.HEARTS), b = new Card(Rank.EIGHT, Suit.CLUBS);
    d.dealOneCard(p, a, Face.UP);
    d.dealOneCard(p, b, Face.UP);
    d.placeBet(p, p.getHand(), 10);
    p.nextHand();
    p.nextHand();
    p.nextHand();
    new ActionSplit(d, d, p.getHand(), b).execute();
    assertEquals(2, p.getHand().countElements());
  }
}
