package blackjack;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

class PlayerTest {

  // A minimal concrete shell tests the abstract base without CPU/Human logic.
  static class Subject extends Player {
    Subject(Hand hand) {
      super(100, 5);
      hands.add(hand);
    }

    public List<Hand> play(IDealer d, IBanker b) {
      throw new UnsupportedOperationException();
    }

    public boolean decideIfQuit() {
      throw new UnsupportedOperationException();
    }

    public Hand nextHand() {
      throw new UnsupportedOperationException();
    }

    public Collection<ICard> returnAllCards() {
      throw new UnsupportedOperationException();
    }
  }

  @Test
  void initialBalance() {
    assertEquals(100, new Subject(mock(Hand.class)).getAccountBalance());
  }

  @ParameterizedTest
  @ValueSource(ints = {0, 1, 100})
  void setAccount(int amount) {
    Subject p = new Subject(mock(Hand.class));
    p.setAccount(amount);
    assertEquals(amount, p.getAccount());
  }

  @ParameterizedTest
  @ValueSource(ints = {1, 5, 100})
  void minimumBet(int amount) {
    Subject p = new Subject(mock(Hand.class));
    p.setMinimalBet(amount);
    assertEquals(amount, p.getMinimalBet());
  }

  @ParameterizedTest
  @ValueSource(ints = {0, 1, 100})
  void awardsMoney(int amount) {
    Subject p = new Subject(mock(Hand.class));
    p.awardMoney(amount);
    assertEquals(100 + amount, p.getAccountBalance());
  }

  @ParameterizedTest
  @CsvSource({"0,0", "1,1", "99,99", "100,100", "101,100"})
  void forfeitCappedByBalance(int amount, int expected) {
    assertEquals(expected, new Subject(mock(Hand.class)).forfeitMoney(amount));
  }

  @ParameterizedTest
  @CsvSource({"0,100", "1,99", "100,0", "101,0"})
  void debitUpdatesBalance(int amount, int expected) {
    Subject p = new Subject(mock(Hand.class));
    p.forfeitMoney(amount);
    assertEquals(expected, p.getAccountBalance());
  }

  @Test
  void primaryHand() {
    Hand h = mock(Hand.class);
    assertSame(h, new Subject(h).getHand());
  }

  @Test
  void delegatesCard() {
    Hand h = mock(Hand.class);
    ICard c = mock(ICard.class);
    new Subject(h).giveOneCard(c);
    verify(h).addElement(c);
  }

  @Test
  void delegatesMinimum() {
    Hand h = mock(Hand.class);
    when(h.getValue()).thenReturn(11);
    assertEquals(11, new Subject(h).minValueInHand());
  }

  @Test
  void delegatesOptimum() {
    Hand h = mock(Hand.class);
    when(h.getOptimalValue()).thenReturn(21);
    assertEquals(21, new Subject(h).getOptimalValueInHand());
  }

  @Test
  void delegatesReveal() {
    Hand h = mock(Hand.class);
    new Subject(h).revealHand();
    verify(h).reveal();
  }

  @Test
  void awardRestoresExhaustedBalance() {
    Subject p = new Subject(mock(Hand.class));
    p.forfeitMoney(100);
    p.awardMoney(10);
    assertEquals(10, p.getAccountBalance());
  }
}
