package flashcards;

import java.util.*;
import java.util.stream.Collectors;

public class Statistics {

    private final Map<String, Integer> termToMistakes = new HashMap<>();

    public int mistakes(String term) {
        return termToMistakes.getOrDefault(term, 0);
    }
    public void accountMistake(String term) {
        termToMistakes.put(term, mistakes(term) + 1);
    }

    public void setMistakeCount(String term, int mistakeCount) {
        termToMistakes.put(term, mistakeCount);
    }

    public void reset() {
        termToMistakes.clear();
    }

    public String getStatistics() {
        int mistakes = maxMistakes();
        if (mistakes == 0) {
            return "There are no cards with errors.\n";
        }
        Set<String> hardest = cardsWithMistakes(mistakes);
        String hardestStr = hardest.stream()
            .map(s -> String.format("\"%s\"", s))
            .collect(Collectors.joining(", "));

        String format;
        if (hardest.size() == 1) {
            format = "The hardest card is %s. You have %d errors answering it.\n";
        } else {
            format = "The hardest cards are %s. You have %d errors answering them.\n";
        }
        return String.format(format, hardestStr, mistakes);
    }

    private int maxMistakes() {
        int max = 0;
        for (Integer v : termToMistakes.values()) {
            max = Math.max(v, max);
        }
        return max;
    }

    private Set<String> cardsWithMistakes(int mistakes) {
        Set<String> cards = new HashSet<>();
        for (var card : termToMistakes.entrySet()) {
            if (card.getValue() == mistakes) {
                cards.add(card.getKey());
            }
        }
        return cards;
    }
}
