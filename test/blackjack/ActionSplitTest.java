package blackjack;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.Test;

class ActionSplitTest {

  Hand original = mock(Hand.class), split = mock(Hand.class);
  Player owner = mock(Player.class);
  IDealer dealer = mock(IDealer.class);
  IBanker banker = mock(IBanker.class);
  ICard card = TestFixtures.card(Rank.EIGHT);

  void ready() {
    when(original.isAllowedSplit()).thenReturn(true);
    when(original.getOwner()).thenReturn(owner);
    when(owner.getAccountBalance()).thenReturn(10);
    when(banker.getBet(original)).thenReturn(10);
    when(original.remove(card)).thenReturn(true);
    when(owner.nextHand()).thenReturn(split);
  }

  @Test
  void forksIntoTwoHands() {
    ready();
    assertEquals(
        List.of(split, original), new ActionSplit(dealer, banker, original, card).execute());
  }

  @Test
  void placesIndependentBet() {
    ready();
    new ActionSplit(dealer, banker, original, card).execute();
    verify(banker).placeBet(owner, split, 10);
  }

  @Test
  void transfersDuplicate() {
    ready();
    new ActionSplit(dealer, banker, original, card).execute();
    verify(dealer).dealOneCard(split, card, Face.UP);
  }

  @Test
  void dealsOriginalReplacement() {
    ready();
    new ActionSplit(dealer, banker, original, card).execute();
    verify(dealer).dealOneCard(original, Face.UP);
  }

  @Test
  void dealsSplitReplacement() {
    ready();
    new ActionSplit(dealer, banker, original, card).execute();
    verify(dealer).dealOneCard(split, Face.UP);
  }

  @Test
  void disablesSurrender() {
    ready();
    new ActionSplit(dealer, banker, original, card).execute();
    verify(split).setAllowedSurrender(false);
  }

  @Test
  void deniedSplit() {
    assertTrue(new ActionSplit(dealer, banker, original, card).execute().isEmpty());
  }

  @Test
  void insufficientFunds() {
    ready();
    when(owner.getAccountBalance()).thenReturn(9);
    assertTrue(new ActionSplit(dealer, banker, original, card).execute().isEmpty());
  }

  @Test
  void absentDuplicate() {
    ready();
    when(original.remove(card)).thenReturn(false);
    assertTrue(new ActionSplit(dealer, banker, original, card).execute().isEmpty());
  }

  @Test
  void exhaustedHands() {
    ready();
    when(owner.nextHand()).thenReturn(null);
    assertTrue(new ActionSplit(dealer, banker, original, card).execute().isEmpty());
  }
}
