package blackjack;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

class ActionHitDefectTest {

  @Test
  @Tag("known-bug")
  void BUG005_deniedHitMustNotRestoreHitPermission() {
    Hand h = TestFixtures.hand(Rank.TEN);
    h.setAllowedHit(false);
    new ActionHit(mock(IDealer.class), h, 21).execute();
    assertFalse(h.isAllowedHit());
  }
}
