package flashcards;

import java.util.*;

public class FlashCards {

    private final Map<String, String> termToDefinition = new LinkedHashMap<>();
    private final Map<String, String> definitionToTerm = new LinkedHashMap<>();
    private final Random randomGen = new Random();

    public void addCard(String term, String definition) {
        if (containsTerm(term) || containsDefinition(definition)) {
            throw new RuntimeException("Duplicate term or definition");
        }
        termToDefinition.put(term, definition);
        definitionToTerm.put(definition, term);
    }

    public void updateCard(String term, String definition) {
        termToDefinition.put(term, definition);
        definitionToTerm.put(definition, term);
    }

    public boolean removeCard(String term) {
        if (!containsTerm(term)) {
            return false;
        }
        String definition = termToDefinition.get(term);
        if (definition == null || !definitionToTerm.containsKey(definition)) {
            throw new RuntimeException("Something went wrong...");
        }
        termToDefinition.remove(term);
        definitionToTerm.remove(definition);
        return true;
    }

    public String getTermByDefinition(String definition) {
        return definitionToTerm.getOrDefault(definition, "");
    }

    public String getDefinitionByTerm(String term) {
        return termToDefinition.getOrDefault(term, "");
    }

    public boolean containsTerm(String term) {
        return termToDefinition.containsKey(term);
    }

    public boolean containsDefinition(String definition) {
        return definitionToTerm.containsKey(definition);
    }

    public Set<String> allTerms() {
        return termToDefinition.keySet();
    }

    public int count() {
        return  termToDefinition.size();
    }

    public String getRandomTerm() {
        List<String> terms = new ArrayList<>(allTerms());
        return terms.get(randomGen.nextInt(1000) % terms.size());
    }


}