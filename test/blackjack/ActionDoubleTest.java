package blackjack;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;

class ActionDoubleTest {

  @Test
  void terminatesHand() {
    assertNull(
        new ActionDouble(mock(IDealer.class), mock(IBanker.class), mock(Hand.class)).execute());
  }

  @Test
  void doublesOriginalBet() {
    Hand h = mock(Hand.class);
    Player p = mock(Player.class);
    IBanker b = mock(IBanker.class);
    when(h.isAllowedDouble()).thenReturn(true);
    when(h.getOwner()).thenReturn(p);
    when(b.getBet(h)).thenReturn(10);
    new ActionDouble(mock(IDealer.class), b, h).execute();
    verify(b).placeBet(p, h, 10);
  }

  @Test
  void dealsOneCard() {
    Hand h = mock(Hand.class);
    when(h.isAllowedDouble()).thenReturn(true);
    IDealer d = mock(IDealer.class);
    new ActionDouble(d, mock(IBanker.class), h).execute();
    verify(d).dealOneCard(h, Face.UP);
  }

  @Test
  void deniedDoubleDoesNotDeal() {
    IDealer d = mock(IDealer.class);
    new ActionDouble(d, mock(IBanker.class), mock(Hand.class)).execute();
    verifyNoInteractions(d);
  }

  @Test
  void disablesHit() {
    Hand h = mock(Hand.class);
    new ActionDouble(mock(IDealer.class), mock(IBanker.class), h).execute();
    verify(h).setAllowedHit(false);
  }

  @Test
  void disablesSplit() {
    Hand h = mock(Hand.class);
    new ActionDouble(mock(IDealer.class), mock(IBanker.class), h).execute();
    verify(h).setAllowedSplit(false);
  }

  @Test
  void disablesDouble() {
    Hand h = mock(Hand.class);
    new ActionDouble(mock(IDealer.class), mock(IBanker.class), h).execute();
    verify(h).setAllowedDouble(false);
  }

  @Test
  void disablesSurrender() {
    Hand h = mock(Hand.class);
    new ActionDouble(mock(IDealer.class), mock(IBanker.class), h).execute();
    verify(h).setAllowedSurrender(false);
  }
}
