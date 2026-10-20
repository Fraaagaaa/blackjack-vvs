package blackjack;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class ActionHitTest {

  @ParameterizedTest
  @CsvSource({"19,1", "20,1", "22,0"})
  void continuesOnlyPlayableHand(int score, int size) {
    Hand h = mock(Hand.class);
    when(h.isAllowedHit()).thenReturn(true);
    when(h.getValue()).thenReturn(score);
    assertEquals(size, new ActionHit(mock(IDealer.class), h, 21).execute().size());
  }

  @Test
  void requestsExactlyOneCard() {
    Hand h = mock(Hand.class);
    when(h.isAllowedHit()).thenReturn(true);
    IDealer d = mock(IDealer.class);
    new ActionHit(d, h, 21).execute();
    verify(d).dealOneCard(h, Face.UP);
  }

  @Test
  void deniedHitDoesNotDeal() {
    Hand h = mock(Hand.class);
    IDealer d = mock(IDealer.class);
    new ActionHit(d, h, 21).execute();
    verifyNoInteractions(d);
  }

  @Test
  void disablesDouble() {
    Hand h = mock(Hand.class);
    new ActionHit(mock(IDealer.class), h, 21).execute();
    verify(h).setAllowedDouble(false);
  }

  @Test
  void disablesSplit() {
    Hand h = mock(Hand.class);
    new ActionHit(mock(IDealer.class), h, 21).execute();
    verify(h).setAllowedSplit(false);
  }

  @Test
  void disablesSurrender() {
    Hand h = mock(Hand.class);
    new ActionHit(mock(IDealer.class), h, 21).execute();
    verify(h).setAllowedSurrender(false);
  }
}
