package blackjack;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

class HandTest {

  @Test
  void preservesOwner() {
    Player owner = mock(Player.class);
    assertSame(owner, new Hand(owner, 0, true).getOwner());
  }

  @ParameterizedTest
  @ValueSource(ints = {0, 1, 3})
  void preservesIndex(int index) {
    assertEquals(index, new Hand(mock(Player.class), index, false).getIndex());
  }

  @Test
  void startsEmpty() {
    assertEquals(0, TestFixtures.hand().countElements());
  }

  @Test
  void emptyValue() {
    assertEquals(0, TestFixtures.hand().getOptimalValue());
  }

  @Test
  void addsCard() {
    assertEquals(1, TestFixtures.hand(Rank.ACE).countElements());
  }

  @ParameterizedTest
  @CsvSource({"TWO,2", "TEN,10", "JACK,10", "QUEEN,10", "KING,10", "ACE,1"})
  void minimumCardValues(Rank rank, int value) {
    assertEquals(value, TestFixtures.hand(rank).getValue());
  }

  @ParameterizedTest
  @CsvSource({"NINE,20", "TEN,21", "JACK,21", "ACE,12"})
  void promotesOnlyOneAce(Rank rank, int expected) {
    assertEquals(expected, TestFixtures.hand(Rank.ACE, rank).getOptimalValue());
  }

  @Test
  void aceDemotesAfterHit() {
    assertEquals(12, TestFixtures.hand(Rank.ACE, Rank.TEN, Rank.ACE).getOptimalValue());
  }

  @Test
  void bustRemainsBust() {
    assertEquals(22, TestFixtures.hand(Rank.KING, Rank.QUEEN, Rank.TWO).getOptimalValue());
  }

  @Test
  void clearReturnsOriginalCards() {
    ICard card = TestFixtures.card(Rank.ACE);
    Hand hand = TestFixtures.hand();
    hand.addElement(card);
    assertEquals(List.of(card), hand.clear());
  }

  @Test
  void clearResetsAceCount() {
    Hand hand = TestFixtures.hand(Rank.ACE);
    hand.clear();
    hand.addElement(TestFixtures.card(Rank.TWO));
    assertEquals(2, hand.getOptimalValue());
  }

  @Test
  void clearEmptiesHand() {
    Hand hand = TestFixtures.hand(Rank.TWO);
    hand.clear();
    assertEquals(0, hand.countElements());
  }

  @Test
  void iteratesInInsertionOrder() {
    ICard card = TestFixtures.card(Rank.ACE);
    Hand hand = TestFixtures.hand();
    hand.addElement(card);
    assertSame(card, hand.iterator().next());
  }

  @Test
  void removesExistingCard() {
    ICard card = TestFixtures.card(Rank.ACE);
    Hand hand = TestFixtures.hand();
    hand.addElement(card);
    assertTrue(hand.remove(card));
  }

  @Test
  void removingAbsentCardDoesNothing() {
    assertFalse(TestFixtures.hand().remove(TestFixtures.card(Rank.ACE)));
  }

  @Test
  void removingAceUpdatesOptimalValue() {
    ICard card = TestFixtures.card(Rank.ACE);
    Hand hand = TestFixtures.hand(Rank.TWO);
    hand.addElement(card);
    hand.remove(card);
    assertEquals(2, hand.getOptimalValue());
  }

  @Test
  void hiddenCardHidesHand() {
    ICard card = TestFixtures.card(Rank.ACE);
    when(card.getFace()).thenReturn(Face.DOWN);
    Hand hand = TestFixtures.hand();
    hand.addElement(card);
    assertFalse(hand.isRevealed());
  }

  @Test
  void revealChangesVisibility() {
    ICard card = TestFixtures.card(Rank.ACE);
    when(card.getFace()).thenReturn(Face.DOWN);
    Hand hand = TestFixtures.hand();
    hand.addElement(card);
    hand.reveal();
    assertTrue(hand.isRevealed());
  }

  @Test
  void revealForwardsToCard() {
    ICard card = TestFixtures.card(Rank.ACE);
    Hand hand = TestFixtures.hand();
    hand.addElement(card);
    hand.reveal();
    verify(card).setFace(Face.UP);
  }

  @Test
  void clearResetsVisibility() {
    ICard card = TestFixtures.card(Rank.ACE);
    when(card.getFace()).thenReturn(Face.DOWN);
    Hand hand = TestFixtures.hand();
    hand.addElement(card);
    hand.clear();
    assertTrue(hand.isRevealed());
  }

  @Test
  void equalValuesWithDifferentRanksAreDuplicates() {
    ICard king = TestFixtures.card(Rank.KING);
    Hand hand = TestFixtures.hand(Rank.TEN);
    hand.addElement(king);
    assertEquals(List.of(king), hand.getDuplicatedCards());
  }

  @Test
  void distinctValuesHaveNoDuplicates() {
    assertTrue(TestFixtures.hand(Rank.TWO, Rank.ACE).getDuplicatedCards().isEmpty());
  }

  @Test
  void rendersEmptyPrimaryHand() {
    assertEquals("empty", TestFixtures.hand().toString());
  }

  @Test
  void labelsSplitHand() {
    assertEquals("(Split Hand 2):empty", new Hand(mock(Player.class), 2, false).toString());
  }

  @Test
  void rendersCardText() {
    ICard card = TestFixtures.card(Rank.ACE);
    when(card.toString()).thenReturn("Ah");
    Hand hand = TestFixtures.hand();
    hand.addElement(card);
    assertEquals("Ah", hand.toString());
  }

  @ParameterizedTest
  @ValueSource(booleans = {true, false})
  void hitPrivilege(boolean allowed) {
    Hand hand = TestFixtures.hand();
    hand.setAllowedHit(allowed);
    assertEquals(allowed, hand.isAllowedHit());
  }

  @ParameterizedTest
  @ValueSource(booleans = {true, false})
  void splitPrivilege(boolean allowed) {
    Hand hand = TestFixtures.hand();
    hand.setAllowedSplit(allowed);
    assertEquals(allowed, hand.isAllowedSplit());
  }

  @ParameterizedTest
  @ValueSource(booleans = {true, false})
  void doublePrivilege(boolean allowed) {
    Hand hand = TestFixtures.hand();
    hand.setAllowedDouble(allowed);
    assertEquals(allowed, hand.isAllowedDouble());
  }

  @ParameterizedTest
  @ValueSource(booleans = {true, false})
  void surrenderPrivilege(boolean allowed) {
    Hand hand = TestFixtures.hand();
    hand.setAllowedSurrender(allowed);
    assertEquals(allowed, hand.isAllowedSurrender());
  }

  @ParameterizedTest
  @ValueSource(booleans = {true, false})
  void surrenderState(boolean value) {
    Hand hand = TestFixtures.hand();
    hand.setSurrendered(value);
    assertEquals(value, hand.isSurrendered());
  }

  @Test
  void surrenderDenied() {
    Hand hand = TestFixtures.hand();
    hand.setAllowedSurrender(false);
    hand.setSurrendered(true);
    assertFalse(hand.isSurrendered());
  }
}
