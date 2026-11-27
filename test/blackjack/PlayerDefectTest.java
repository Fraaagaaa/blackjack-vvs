package blackjack;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

class PlayerDefectTest {

  static class ConstructorDispatchProbe extends PlayerCPU {
    int setterCalls;

    ConstructorDispatchProbe() {
      super(100, 1);
    }

    @Override
    public void setAccount(int account) {
      setterCalls++;
    }

    @Override
    public void setMinimalBet(int minimalBet) {
      setterCalls++;
    }
  }

  @Test
  @Tag("known-bug")
  void BUG007_constructorMustNotDispatchToOverridableSetters() {
    assertEquals(0, new ConstructorDispatchProbe().setterCalls);
  }
}
