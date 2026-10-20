package blackjack;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Set;
import org.junit.jupiter.api.Test;

class FaceTest {

  @Test
  void hasUpAndDown() {
    assertEquals(Set.of("UP", "DOWN"), Set.of(Face.UP.name(), Face.DOWN.name()));
  }
}
