package blackjack;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;

class CardTest {

  @ParameterizedTest
  @EnumSource(Rank.class)
  void retainsRank(Rank rank) {
    assertEquals(rank, new Card(rank, Suit.HEARTS).getRank());
  }

  @ParameterizedTest
  @EnumSource(Suit.class)
  void retainsSuit(Suit suit) {
    assertEquals(suit, new Card(Rank.ACE, suit).getSuit());
  }

  @Test
  void initiallyHidden() {
    assertEquals(Face.DOWN, new Card(Rank.ACE, Suit.CLUBS).getFace());
  }

  @Test
  void hidesIdentity() {
    assertEquals("X", new Card(Rank.ACE, Suit.CLUBS).toString());
  }

  @ParameterizedTest
  @EnumSource(Face.class)
  void changesFace(Face face) {
    Card card = new Card(Rank.ACE, Suit.CLUBS);
    card.setFace(face);
    assertEquals(face, card.getFace());
  }

  @ParameterizedTest
  @CsvSource({"ACE,SPADES,As", "TEN,HEARTS,10h", "QUEEN,DIAMONDS,Qd", "TWO,CLUBS,2c"})
  void rendersVisibleCard(Rank rank, Suit suit, String expected) {
    Card card = new Card(rank, suit);
    card.setFace(Face.UP);
    assertEquals(expected, card.toString());
  }

  @Test
  void canHideAgain() {
    Card card = new Card(Rank.ACE, Suit.CLUBS);
    card.setFace(Face.UP);
    card.setFace(Face.DOWN);
    assertEquals("X", card.toString());
  }
}
