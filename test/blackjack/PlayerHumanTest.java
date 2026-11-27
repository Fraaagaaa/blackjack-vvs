package blackjack;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.MockedConstruction;

class PlayerHumanTest {

  BufferedReader input = mock(BufferedReader.class);

  @ParameterizedTest
  @CsvSource({"q,true", "Q,true", "'',false"})
  void quitCommands(String line, boolean expected) throws Exception {
    when(input.readLine()).thenReturn(line);
    assertEquals(expected, new PlayerHuman(100, 1, input).decideIfQuit());
  }

  @Test
  void retriesInvalidQuit() throws Exception {
    when(input.readLine()).thenReturn("wrong", "q");
    assertTrue(new PlayerHuman(100, 1, input).decideIfQuit());
  }

  @Test
  void brokePlayerQuits() {
    assertTrue(new PlayerHuman(0, 1, input).decideIfQuit());
  }

  @Test
  void quitReadFailure() throws Exception {
    when(input.readLine()).thenThrow(new IOException("test"));
    assertThrows(RuntimeException.class, () -> new PlayerHuman(100, 1, input).decideIfQuit());
  }

  @ParameterizedTest
  @CsvSource({"'',1", "1,1", "99,99", "100,100"})
  void validBet(String line, int amount) throws Exception {
    when(input.readLine()).thenReturn(line);
    IBanker b = mock(IBanker.class);
    PlayerHuman p = new PlayerHuman(100, 1, input);
    p.bet(b);
    verify(b).placeBet(p, p.getHand(), amount);
  }

  @ParameterizedTest
  @ValueSource(strings = {"0", "-1", "101", "abc", "2147483648"})
  void invalidBetIsRetried(String invalid) throws Exception {
    when(input.readLine()).thenReturn(invalid, "5");
    IBanker b = mock(IBanker.class);
    PlayerHuman p = new PlayerHuman(100, 1, input);
    p.bet(b);
    verify(b).placeBet(p, p.getHand(), 5);
  }

  @Test
  void exactMinimumDoesNotPrompt() {
    PlayerHuman p = new PlayerHuman(1, 1, input);
    p.bet(mock(IBanker.class));
    verifyNoInteractions(input);
  }

  @Test
  void betReadFailure() throws Exception {
    when(input.readLine()).thenThrow(new IOException("test"));
    assertThrows(
        RuntimeException.class, () -> new PlayerHuman(100, 1, input).bet(mock(IBanker.class)));
  }

  @Test
  void initialSplitCount() {
    assertEquals(0, new PlayerHuman(100, 1, input).countSplitHands());
  }

  @Test
  void allocatesThreeDistinctHands() {
    PlayerHuman p = new PlayerHuman(100, 1, input);
    assertEquals(3, Set.of(p.nextHand(), p.nextHand(), p.nextHand()).size());
  }

  @Test
  void refusesFourthSplit() {
    PlayerHuman p = new PlayerHuman(100, 1, input);
    p.nextHand();
    p.nextHand();
    p.nextHand();
    assertNull(p.nextHand());
  }

  @Test
  void resetReleasesSplitSlots() {
    PlayerHuman p = new PlayerHuman(100, 1, input);
    p.nextHand();
    p.nextHand();
    p.returnAllCards();
    assertEquals(0, p.countSplitHands());
  }

  @Test
  void resetClearsEachOccupiedHand() {
    try (MockedConstruction<Hand> hands =
        mockConstruction(Hand.class, (h, c) -> when(h.countElements()).thenReturn(1))) {
      PlayerHuman p = new PlayerHuman(100, 1, input);
      p.returnAllCards();
      verify(hands.constructed().get(3)).clear();
    }
  }

  @Test
  void playsStandWithMockedAction() throws Exception {
    when(input.readLine()).thenReturn("");
    try (MockedConstruction<Hand> hands = mockConstruction(Hand.class);
        MockedConstruction<ActionStand> actions = mockConstruction(ActionStand.class)) {
      PlayerHuman p = new PlayerHuman(100, 1, input);
      assertEquals(List.of(p.getHand()), p.play(mock(IDealer.class), mock(IBanker.class)));
    }
  }

  @Test
  void playReadFailure() throws Exception {
    when(input.readLine()).thenThrow(new IOException("test"));
    try (MockedConstruction<Hand> hands =
        mockConstruction(Hand.class, (h, c) -> when(h.isAllowedHit()).thenReturn(true))) {
      PlayerHuman p = new PlayerHuman(100, 1, input);
      assertThrows(RuntimeException.class, () -> p.play(mock(IDealer.class), mock(IBanker.class)));
    }
  }

  @Test
  void bustedHandOffersOnlyStand() {
    try (MockedConstruction<Hand> hands =
        mockConstruction(
            Hand.class,
            (hand, context) -> {
              when(hand.isAllowedHit()).thenReturn(true);
              when(hand.isAllowedSplit()).thenReturn(true);
              when(hand.isAllowedDouble()).thenReturn(true);
              when(hand.isAllowedSurrender()).thenReturn(true);
              when(hand.getOptimalValue()).thenReturn(22);
            })) {
      PlayerHuman player = new PlayerHuman(100, 1, input);
      assertEquals(
          List.of(player.getHand()), player.play(mock(IDealer.class), mock(IBanker.class)));
    }
  }

  @Test
  void unaffordableActionsOfferOnlyStand() {
    IBanker banker = mock(IBanker.class);
    when(banker.getBet(any(Hand.class))).thenReturn(2);
    try (MockedConstruction<Hand> hands =
        mockConstruction(
            Hand.class,
            (hand, context) -> {
              when(hand.isAllowedSplit()).thenReturn(true);
              when(hand.isAllowedDouble()).thenReturn(true);
              when(hand.getOptimalValue()).thenReturn(20);
            })) {
      PlayerHuman player = new PlayerHuman(1, 1, input);
      assertEquals(List.of(player.getHand()), player.play(mock(IDealer.class), banker));
    }
  }

  @Test
  void splitLimitOffersOnlyStand() {
    try (MockedConstruction<Hand> hands =
        mockConstruction(
            Hand.class,
            (hand, context) -> {
              when(hand.isAllowedSplit()).thenReturn(true);
              when(hand.getOptimalValue()).thenReturn(20);
            })) {
      PlayerHuman player = new PlayerHuman(100, 1, input);
      player.nextHand();
      player.nextHand();
      player.nextHand();
      assertEquals(
          List.of(player.getHand()), player.play(mock(IDealer.class), mock(IBanker.class)));
    }
  }

  @Test
  void splitCanBeFirstOfferedAction() throws Exception {
    ICard duplicate = mock(ICard.class);
    when(input.readLine()).thenReturn("1");
    try (MockedConstruction<Hand> hands =
        mockConstruction(
            Hand.class,
            (hand, context) -> {
              when(hand.isAllowedSplit()).thenReturn(true);
              when(hand.getOptimalValue()).thenReturn(20);
              when(hand.getDuplicatedCards()).thenReturn(List.of(duplicate));
            });
        MockedConstruction<ActionSplit> actions =
            mockConstruction(
                ActionSplit.class,
                (action, context) -> when(action.execute()).thenReturn(List.of()))) {
      PlayerHuman player = new PlayerHuman(100, 1, input);
      assertEquals(
          List.of(player.getHand()), player.play(mock(IDealer.class), mock(IBanker.class)));
    }
  }

  @Test
  void doubleCanBeFirstOfferedAction() throws Exception {
    when(input.readLine()).thenReturn("1");
    try (MockedConstruction<Hand> hands =
        mockConstruction(
            Hand.class,
            (hand, context) -> {
              when(hand.isAllowedDouble()).thenReturn(true);
              when(hand.getOptimalValue()).thenReturn(20);
            })) {
      PlayerHuman player = new PlayerHuman(100, 1, input);
      assertEquals(
          List.of(player.getHand()), player.play(mock(IDealer.class), mock(IBanker.class)));
    }
  }

  @Test
  void surrenderCanBeFirstOfferedAction() throws Exception {
    when(input.readLine()).thenReturn("1");
    try (MockedConstruction<Hand> hands =
        mockConstruction(
            Hand.class,
            (hand, context) -> {
              when(hand.isAllowedSurrender()).thenReturn(true);
              when(hand.getOptimalValue()).thenReturn(20);
            })) {
      PlayerHuman player = new PlayerHuman(100, 1, input);
      assertEquals(
          List.of(player.getHand()), player.play(mock(IDealer.class), mock(IBanker.class)));
    }
  }
}
