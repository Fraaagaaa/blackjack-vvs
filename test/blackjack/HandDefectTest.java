package blackjack;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.Iterator;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

class HandDefectTest {

  static class ConstructorDispatchProbe extends Hand {
    int setterCalls;

    ConstructorDispatchProbe() {
      super(null, 0, true);
    }

    @Override
    public void setAllowedHit(boolean allowed) {
      setterCalls++;
    }

    @Override
    public void setAllowedSplit(boolean allowed) {
      setterCalls++;
    }

    @Override
    public void setAllowedDouble(boolean allowed) {
      setterCalls++;
    }

    @Override
    public void setAllowedSurrender(boolean allowed) {
      setterCalls++;
    }
  }

  @Test
  @Tag("known-bug")
  void BUG003_iteratorRemovalMustKeepAceScoreConsistent() {
    Hand h = TestFixtures.hand(Rank.ACE, Rank.TWO);
    Iterator<ICard> iterator = h.iterator();
    iterator.next();
    iterator.remove();
    assertEquals(2, h.getOptimalValue());
  }

  @Test
  @Tag("known-bug")
  void BUG004_removingLastHiddenCardMustRevealRemainingHand() {
    ICard hidden = TestFixtures.card(Rank.ACE);
    when(hidden.getFace()).thenReturn(Face.DOWN);
    Hand h = TestFixtures.hand(Rank.TWO);
    h.addElement(hidden);
    h.remove(hidden);
    assertTrue(h.isRevealed());
  }

  @Test
  @Tag("known-bug")
  void BUG006_constructorMustNotDispatchToOverridableSetters() {
    assertEquals(0, new ConstructorDispatchProbe().setterCalls);
  }
}
