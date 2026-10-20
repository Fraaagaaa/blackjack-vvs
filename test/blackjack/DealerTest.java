package blackjack;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.MockedConstruction;

class DealerTest {

  @ParameterizedTest
  @CsvSource({"20,19,WIN", "19,20,LOSE", "21,21,TIE", "21,22,WIN", "22,21,LOSE", "22,22,LOSE"})
  void comparesScores(int human, int casino, Outcome expected) {
    Hand h = mock(Hand.class), c = mock(Hand.class);
    when(h.getOptimalValue()).thenReturn(human);
    when(c.getOptimalValue()).thenReturn(casino);
    assertEquals(expected, new Dealer(21).compareHands(h, c));
  }

  @Test
  void surrenderTakesPriority() {
    Hand h = mock(Hand.class);
    when(h.isSurrendered()).thenReturn(true);
    assertEquals(Outcome.SURRENDER, new Dealer(21).compareHands(h, mock(Hand.class)));
  }

  @Test
  void recordsActualDebit() {
    Player p = mock(Player.class);
    Hand h = mock(Hand.class);
    when(p.forfeitMoney(10)).thenReturn(7);
    Dealer d = new Dealer(21);
    d.placeBet(p, h, 10);
    assertEquals(7, d.getBet(h));
  }

  @Test
  void accumulatesBet() {
    Player p = mock(Player.class);
    Hand h = mock(Hand.class);
    when(p.forfeitMoney(10)).thenReturn(10);
    Dealer d = new Dealer(21);
    d.placeBet(p, h, 10);
    d.placeBet(p, h, 10);
    assertEquals(20, d.getBet(h));
  }

  @ParameterizedTest
  @CsvSource({"WIN,20", "LOSE,0", "TIE,10", "SURRENDER,5", "WIN_BJ,25"})
  void settlesPlayerShare(Outcome outcome, int expected) {
    Player p = mock(Player.class), c = mock(Player.class);
    Hand h = mock(Hand.class);
    Dealer d = new Dealer(21);
    when(p.forfeitMoney(10)).thenReturn(10);
    when(c.forfeitMoney(anyInt())).thenAnswer(i -> i.getArgument(0));
    d.placeBet(p, h, 10);
    d.resolveBet(p, c, h, outcome);
    verify(p).awardMoney(expected);
  }

  @Test
  void settlementIsIdempotent() {
    Player p = mock(Player.class), c = mock(Player.class);
    Hand h = mock(Hand.class);
    Dealer d = new Dealer(21);
    when(p.forfeitMoney(10)).thenReturn(10);
    when(c.forfeitMoney(10)).thenReturn(10);
    d.placeBet(p, h, 10);
    d.resolveBet(p, c, h, Outcome.WIN);
    d.resolveBet(p, c, h, Outcome.WIN);
    verify(p).awardMoney(20);
  }

  @Test
  void noBetHasNoPayout() {
    Player p = mock(Player.class);
    new Dealer(21).resolveBet(p, mock(Player.class), mock(Hand.class), Outcome.WIN);
    verifyNoInteractions(p);
  }

  @Test
  void explicitCardToHand() {
    IHand<ICard> h = mock(Hand.class);
    ICard c = mock(ICard.class);
    new Dealer(21).dealOneCard(h, c, Face.UP);
    verify(h).addElement(c);
  }

  @Test
  void explicitCardToPlayer() {
    Player p = mock(Player.class);
    ICard c = mock(ICard.class);
    new Dealer(21).dealOneCard(p, c, Face.DOWN);
    verify(p).giveOneCard(c);
  }

  @Test
  void explicitCardFace() {
    ICard c = mock(ICard.class);
    new Dealer(21).dealOneCard(mock(Hand.class), c, Face.UP);
    verify(c).setFace(Face.UP);
  }

  @Test
  void collectionHidesCard() {
    ICard c = mock(ICard.class);
    new Dealer(21).collectCards(List.of(c));
    verify(c).setFace(Face.DOWN);
  }

  @Test
  void shuffleDelegatesToDeck() {
    try (MockedConstruction<Deck> decks = mockConstruction(Deck.class)) {
      Dealer d = new Dealer(21);
      d.shuffle();
      verify(decks.constructed().get(0)).shuffle();
    }
  }

  @Test
  void deckCardToHand() {
    ICard card = mock(ICard.class);
    Hand h = mock(Hand.class);
    try (MockedConstruction<Deck> decks =
        mockConstruction(
            Deck.class,
            (d, c) -> {
              when(d.countElements()).thenReturn(1);
              when(d.takeOneFromTop()).thenReturn(card);
            })) {
      new Dealer(21).dealOneCard(h, Face.UP);
      verify(h).addElement(card);
    }
  }

  @Test
  void deckCardToPlayer() {
    ICard card = mock(ICard.class);
    Player p = mock(Player.class);
    Hand h = mock(Hand.class);
    when(p.getHand()).thenReturn(h);
    try (MockedConstruction<Deck> decks =
        mockConstruction(
            Deck.class,
            (d, c) -> {
              when(d.countElements()).thenReturn(1);
              when(d.takeOneFromTop()).thenReturn(card);
            })) {
      new Dealer(21).dealOneCard(p, Face.UP);
      verify(h).addElement(card);
    }
  }

  @Test
  void emptyDeckDoesNotDeal() {
    Hand h = mock(Hand.class);
    try (MockedConstruction<Deck> decks = mockConstruction(Deck.class)) {
      new Dealer(21).dealOneCard(h, Face.UP);
      verifyNoInteractions(h);
    }
  }
}
