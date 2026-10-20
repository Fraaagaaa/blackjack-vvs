package blackjack;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class CardHandIT {

  @ParameterizedTest
  @CsvSource({"ACE,TEN,21", "ACE,ACE,12", "TEN,KING,20", "TWO,THREE,5"})
  void realCardsDetermineHandScore(Rank a, Rank b, int expected) {
    Hand hand = new Hand(new PlayerCPU(100, 1), 0, true);
    hand.addElement(new Card(a, Suit.HEARTS));
    hand.addElement(new Card(b, Suit.CLUBS));
    assertEquals(expected, hand.getOptimalValue());
  }

  @Test
  void revealExposesRealCard() {
    Card card = new Card(Rank.ACE, Suit.SPADES);
    Hand hand = new Hand(new PlayerCPU(100, 1), 0, true);
    hand.addElement(card);
    hand.reveal();
    assertEquals("As", card.toString());
  }

  @Test
  void realFaceCardsCanSplit() {
    Hand hand = new Hand(new PlayerCPU(100, 1), 0, true);
    hand.addElement(new Card(Rank.TEN, Suit.HEARTS));
    hand.addElement(new Card(Rank.KING, Suit.CLUBS));
    assertEquals(1, hand.getDuplicatedCards().size());
  }
}
