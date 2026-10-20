package blackjack;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.MockedConstruction;

class PlayerCPUTest {

  @ParameterizedTest
  @CsvSource({"0,1,true", "1,1,false", "2,1,false"})
  void quitsBelowMinimum(int balance, int minimum, boolean expected) {
    assertEquals(expected, new PlayerCPU(balance, minimum).decideIfQuit());
  }

  @Test
  void neverSplits() {
    assertNull(new PlayerCPU(100, 1).nextHand());
  }

  @ParameterizedTest
  @ValueSource(ints = {17, 18, 21, 22})
  void standsAtSeventeenOrMore(int value) {
    try (MockedConstruction<Hand> hands =
        mockConstruction(Hand.class, (h, c) -> when(h.getOptimalValue()).thenReturn(value))) {
      IDealer d = mock(IDealer.class);
      new PlayerCPU(100, 1).play(d, mock(IBanker.class));
      verifyNoInteractions(d);
    }
  }

  @Test
  void hitsUntilSeventeen() {
    try (MockedConstruction<Hand> hands =
        mockConstruction(Hand.class, (h, c) -> when(h.getOptimalValue()).thenReturn(15, 16, 17))) {
      IDealer d = mock(IDealer.class);
      PlayerCPU p = new PlayerCPU(100, 1);
      p.play(d, mock(IBanker.class));
      verify(d, times(2)).dealOneCard(p, Face.UP);
    }
  }

  @Test
  void returnsPlayedHand() {
    try (MockedConstruction<Hand> hands =
        mockConstruction(Hand.class, (h, c) -> when(h.getOptimalValue()).thenReturn(17))) {
      PlayerCPU p = new PlayerCPU(100, 1);
      assertEquals(List.of(p.getHand()), p.play(mock(IDealer.class), mock(IBanker.class)));
    }
  }

  @Test
  void returnsCardsViaHand() {
    try (MockedConstruction<Hand> hands = mockConstruction(Hand.class)) {
      PlayerCPU p = new PlayerCPU(100, 1);
      p.returnAllCards();
      verify(hands.constructed().get(0)).clear();
    }
  }
}
