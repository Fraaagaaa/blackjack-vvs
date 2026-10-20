package blackjack;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/** Value doubles isolate Hand from the Card implementation. */
final class TestFixtures {
  private TestFixtures() {}

  static ICard card(Rank rank) {
    ICard card = mock(ICard.class);
    when(card.getRank()).thenReturn(rank);
    when(card.getFace()).thenReturn(Face.UP);
    return card;
  }

  static Hand hand(Rank... ranks) {
    Hand hand = new Hand(mock(Player.class), 0, true);
    for (Rank rank : ranks) {
      hand.addElement(card(rank));
    }
    return hand;
  }
}
