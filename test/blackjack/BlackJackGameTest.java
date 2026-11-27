package blackjack;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.MockedConstruction;

class BlackJackGameTest {

  @Test
  void zeroFundsEndsWithoutDealing() {
    try (MockedConstruction<Dealer> dealers = mockConstruction(Dealer.class);
        MockedConstruction<PlayerCPU> cpus = mockConstruction(PlayerCPU.class);
        MockedConstruction<PlayerHuman> humans = mockConstruction(PlayerHuman.class)) {
      new BlackJackGame().playGame();
      verifyNoInteractions(dealers.constructed().get(0));
    }
  }

  @Test
  void casinoBankruptcyEndsWithoutDealing() {
    try (MockedConstruction<Dealer> dealers = mockConstruction(Dealer.class);
        MockedConstruction<PlayerCPU> cpus =
            mockConstruction(
                PlayerCPU.class,
                (player, context) -> when(player.getAccountBalance()).thenReturn(0));
        MockedConstruction<PlayerHuman> humans =
            mockConstruction(
                PlayerHuman.class,
                (player, context) -> when(player.getAccountBalance()).thenReturn(100))) {
      new BlackJackGame().playGame();
      verifyNoInteractions(dealers.constructed().get(0));
    }
  }

  @Test
  void casinoBlackjackResolvesLoss() {
    Hand human = mock(Hand.class), casino = mock(Hand.class);
    when(casino.getOptimalValue()).thenReturn(21);
    when(human.getOptimalValue()).thenReturn(20);
    try (MockedConstruction<Dealer> dealers = mockConstruction(Dealer.class);
        MockedConstruction<PlayerCPU> cpus =
            mockConstruction(
                PlayerCPU.class,
                (p, c) -> {
                  when(p.getAccountBalance()).thenReturn(1000);
                  when(p.getHand()).thenReturn(casino);
                });
        MockedConstruction<PlayerHuman> humans =
            mockConstruction(
                PlayerHuman.class,
                (p, c) -> {
                  when(p.getAccountBalance()).thenReturn(100);
                  when(p.getHand()).thenReturn(human);
                  when(p.decideIfQuit()).thenReturn(true);
                })) {
      new BlackJackGame().playGame();
      verify(dealers.constructed().get(0))
          .resolveBet(humans.constructed().get(0), cpus.constructed().get(0), human, Outcome.LOSE);
    }
  }

  @ParameterizedTest
  @CsvSource({"21,21,TIE", "21,20,WIN_BJ"})
  void resolvesNaturalBlackjack(int humanValue, int casinoValue, Outcome result) {
    Hand human = mock(Hand.class), casino = mock(Hand.class);
    when(human.getOptimalValue()).thenReturn(humanValue);
    when(casino.getOptimalValue()).thenReturn(casinoValue);
    try (MockedConstruction<Dealer> dealers = mockConstruction(Dealer.class);
        MockedConstruction<PlayerCPU> cpus =
            mockConstruction(
                PlayerCPU.class,
                (p, c) -> {
                  when(p.getAccountBalance()).thenReturn(1000);
                  when(p.getHand()).thenReturn(casino);
                });
        MockedConstruction<PlayerHuman> humans =
            mockConstruction(
                PlayerHuman.class,
                (p, c) -> {
                  when(p.getAccountBalance()).thenReturn(100);
                  when(p.getHand()).thenReturn(human);
                  when(p.decideIfQuit()).thenReturn(true);
                })) {
      new BlackJackGame().playGame();
      verify(dealers.constructed().get(0))
          .resolveBet(humans.constructed().get(0), cpus.constructed().get(0), human, result);
    }
  }

  @Test
  void regularRoundSettlesPlayableHand() {
    Hand primary = mock(Hand.class), played = mock(Hand.class), casinoHand = mock(Hand.class);
    when(primary.getOptimalValue()).thenReturn(20);
    when(played.getValue()).thenReturn(20);
    when(played.getOptimalValue()).thenReturn(20);
    when(casinoHand.getOptimalValue()).thenReturn(18);
    try (MockedConstruction<Dealer> dealers =
            mockConstruction(
                Dealer.class,
                (dealer, context) ->
                    when(dealer.compareHands(played, casinoHand)).thenReturn(Outcome.WIN));
        MockedConstruction<PlayerCPU> cpus =
            mockConstruction(
                PlayerCPU.class,
                (player, context) -> {
                  when(player.getAccountBalance()).thenReturn(1000);
                  when(player.getHand()).thenReturn(casinoHand);
                  when(player.decideIfQuit()).thenReturn(true);
                });
        MockedConstruction<PlayerHuman> humans =
            mockConstruction(
                PlayerHuman.class,
                (player, context) -> {
                  when(player.getAccountBalance()).thenReturn(100);
                  when(player.getHand()).thenReturn(primary);
                  when(player.play(dealers.constructed().get(0), dealers.constructed().get(0)))
                      .thenReturn(List.of(played));
                  when(player.decideIfQuit()).thenReturn(false);
                })) {
      new BlackJackGame().playGame();
      verify(dealers.constructed().get(0))
          .resolveBet(humans.constructed().get(0), cpus.constructed().get(0), played, Outcome.WIN);
    }
  }

  @Test
  void dealerDoesNotPlayWhenEveryHumanHandIsBusted() {
    Hand primary = mock(Hand.class), busted = mock(Hand.class), casinoHand = mock(Hand.class);
    when(primary.getOptimalValue()).thenReturn(20);
    when(busted.getValue()).thenReturn(22);
    when(casinoHand.getOptimalValue()).thenReturn(18);
    try (MockedConstruction<Dealer> dealers = mockConstruction(Dealer.class);
        MockedConstruction<PlayerCPU> cpus =
            mockConstruction(
                PlayerCPU.class,
                (player, context) -> {
                  when(player.getAccountBalance()).thenReturn(1000);
                  when(player.getHand()).thenReturn(casinoHand);
                });
        MockedConstruction<PlayerHuman> humans =
            mockConstruction(
                PlayerHuman.class,
                (player, context) -> {
                  when(player.getAccountBalance()).thenReturn(100);
                  when(player.getHand()).thenReturn(primary);
                  when(player.play(dealers.constructed().get(0), dealers.constructed().get(0)))
                      .thenReturn(List.of(busted));
                  when(player.decideIfQuit()).thenReturn(true);
                })) {
      new BlackJackGame().playGame();
      verify(cpus.constructed().get(0), never())
          .play(dealers.constructed().get(0), dealers.constructed().get(0));
    }
  }

  @Test
  void dealerDoesNotPlayWhenEveryHumanHandIsSurrendered() {
    Hand primary = mock(Hand.class);
    Hand surrendered = mock(Hand.class);
    Hand casinoHand = mock(Hand.class);
    when(primary.getOptimalValue()).thenReturn(20);
    when(surrendered.getValue()).thenReturn(20);
    when(surrendered.isSurrendered()).thenReturn(true);
    when(casinoHand.getOptimalValue()).thenReturn(18);
    try (MockedConstruction<Dealer> dealers = mockConstruction(Dealer.class);
        MockedConstruction<PlayerCPU> cpus =
            mockConstruction(
                PlayerCPU.class,
                (player, context) -> {
                  when(player.getAccountBalance()).thenReturn(1000);
                  when(player.getHand()).thenReturn(casinoHand);
                });
        MockedConstruction<PlayerHuman> humans =
            mockConstruction(
                PlayerHuman.class,
                (player, context) -> {
                  when(player.getAccountBalance()).thenReturn(100);
                  when(player.getHand()).thenReturn(primary);
                  when(player.play(dealers.constructed().get(0), dealers.constructed().get(0)))
                      .thenReturn(List.of(surrendered));
                  when(player.decideIfQuit()).thenReturn(true);
                })) {
      new BlackJackGame().playGame();
      verify(cpus.constructed().get(0), never())
          .play(dealers.constructed().get(0), dealers.constructed().get(0));
    }
  }

  @Test
  void consultsCasinoWhenHumanContinues() {
    Hand primary = mock(Hand.class);
    Hand busted = mock(Hand.class);
    Hand casinoHand = mock(Hand.class);
    when(primary.getOptimalValue()).thenReturn(20);
    when(busted.getValue()).thenReturn(22);
    when(casinoHand.getOptimalValue()).thenReturn(18);
    try (MockedConstruction<Dealer> dealers = mockConstruction(Dealer.class);
        MockedConstruction<PlayerCPU> cpus =
            mockConstruction(
                PlayerCPU.class,
                (player, context) -> {
                  when(player.getAccountBalance()).thenReturn(1000);
                  when(player.getHand()).thenReturn(casinoHand);
                  when(player.decideIfQuit()).thenReturn(false);
                });
        MockedConstruction<PlayerHuman> humans =
            mockConstruction(
                PlayerHuman.class,
                (player, context) -> {
                  when(player.getAccountBalance()).thenReturn(100, 100, 0);
                  when(player.getHand()).thenReturn(primary);
                  when(player.play(dealers.constructed().get(0), dealers.constructed().get(0)))
                      .thenReturn(List.of(busted));
                  when(player.decideIfQuit()).thenReturn(false);
                })) {
      new BlackJackGame().playGame();
      verify(cpus.constructed().get(0)).decideIfQuit();
    }
  }

  @Test
  void mainReportsUnexpectedGameFailure() {
    InputStream previousInput = System.in;
    PrintStream previousError = System.err;
    ByteArrayOutputStream error = new ByteArrayOutputStream();
    System.setIn(new ByteArrayInputStream(new byte[0]));
    System.setErr(new PrintStream(error, true, StandardCharsets.UTF_8));
    try (MockedConstruction<Dealer> dealers = mockConstruction(Dealer.class);
        MockedConstruction<PlayerCPU> cpus = mockConstruction(PlayerCPU.class);
        MockedConstruction<PlayerHuman> humans =
            mockConstruction(
                PlayerHuman.class,
                (player, context) ->
                    when(player.getAccountBalance()).thenThrow(new IllegalStateException("test")))) {
      BlackJackGame.main(new String[0]);
      assertTrue(error.toString(StandardCharsets.UTF_8).contains("Game ended unexpectedly."));
    } finally {
      System.setIn(previousInput);
      System.setErr(previousError);
    }
  }

  @Test
  void mainClosesInput() {
    class TrackedInput extends ByteArrayInputStream {
      boolean closed;

      TrackedInput() {
        super(new byte[0]);
      }

      public void close() throws IOException {
        closed = true;
        super.close();
      }
    }
    InputStream previous = System.in;
    TrackedInput input = new TrackedInput();
    System.setIn(input);
    try (MockedConstruction<Dealer> dealers = mockConstruction(Dealer.class);
        MockedConstruction<PlayerCPU> cpus = mockConstruction(PlayerCPU.class);
        MockedConstruction<PlayerHuman> humans = mockConstruction(PlayerHuman.class)) {
      BlackJackGame.main(new String[0]);
      assertTrue(input.closed);
    } finally {
      System.setIn(previous);
    }
  }
}
