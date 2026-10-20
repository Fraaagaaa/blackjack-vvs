package blackjack;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.constraints.Size;

class HandProperties {
  @Property(tries = 200, seed = "20261020")
  void optimalValueMatchesIndependentEnumeration(@ForAll @Size(max = 12) List<Rank> ranks) {
    // Enumerate every feasible total (ace=1 or 11); do not copy Hand's threshold formula.
    java.util.Set<Integer> totals = new java.util.HashSet<>();
    totals.add(0);
    for (Rank rank : ranks) {
      int value =
          switch (rank) {
            case ACE -> 1;
            case TWO -> 2;
            case THREE -> 3;
            case FOUR -> 4;
            case FIVE -> 5;
            case SIX -> 6;
            case SEVEN -> 7;
            case EIGHT -> 8;
            case NINE -> 9;
            default -> 10;
          };
      java.util.Set<Integer> next = new java.util.HashSet<>();
      for (int total : totals) {
        next.add(total + value);
        if (rank == Rank.ACE) {
          next.add(total + 11);
        }
      }
      totals = next;
    }
    int smallest = totals.stream().mapToInt(Integer::intValue).min().orElseThrow();
    int expected =
        totals.stream().mapToInt(Integer::intValue).filter(v -> v <= 21).max().orElse(smallest);
    assertEquals(expected, TestFixtures.hand(ranks.toArray(Rank[]::new)).getOptimalValue());
  }
}
