package blackjack;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class DeckTest {

  @Test
  void emptyDeckReturnsNull() {
    assertNull(new Deck<String>().takeOneFromTop());
  }

  @Test
  void initiallyEmpty() {
    assertEquals(0, new Deck<String>().countElements());
  }

  @Test
  void returnsInFifoOrder() {
    Deck<String> deck = new Deck<>();
    deck.returnOneToBottom("A");
    deck.returnOneToBottom("B");
    assertEquals(List.of("A", "B"), List.of(deck.takeOneFromTop(), deck.takeOneFromTop()));
  }

  @Test
  void depletionAndRefill() {
    Deck<String> deck = new Deck<>();
    deck.returnOneToBottom("A");
    deck.takeOneFromTop();
    deck.returnOneToBottom("B");
    assertEquals("B", deck.takeOneFromTop());
  }

  @ParameterizedTest
  @ValueSource(ints = {0, 1, 2, 52})
  void shufflePreservesContents(int size) {
    Deck<Integer> deck = new Deck<>();
    List<Integer> expected = new ArrayList<>();
    for (int i = 0; i < size; i++) {
      deck.returnOneToBottom(i);
      expected.add(i);
    }
    deck.shuffle();
    List<Integer> actual = new ArrayList<>();
    while (deck.countElements() > 0) {
      actual.add(deck.takeOneFromTop());
    }
    Collections.sort(actual);
    assertEquals(expected, actual);
  }
}
