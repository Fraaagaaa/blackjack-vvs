package blackjack;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;

class ActionSurrenderTest {

  @Test
  void terminatesHand() {
    assertNull(new ActionSurrender(mock(Hand.class)).execute());
  }

  @Test
  void requestsSurrender() {
    Hand h = mock(Hand.class);
    new ActionSurrender(h).execute();
    verify(h).setSurrendered(true);
  }
}
