package blackjack;

import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

class ActionStandTest {

  @Test
  void terminatesHand() {
    assertNull(new ActionStand().execute());
  }

  @Test
  void repeatIsIdempotent() {
    ActionStand action = new ActionStand();
    action.execute();
    assertNull(action.execute());
  }
}
