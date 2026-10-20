package blackjack;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
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
