package flashcards;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
    static {
        Logger.redirectSystemOut();
    }
    private static final LogScanner scanner = Logger.getScanner();
    private static final FlashCards cards = new FlashCards();
    private static final Statistics statistics = new Statistics();
    private static File exportOnExitFile = null;

    public static void main(String[] args) {
        processStartupOptions(args);
        while (true) {
            System.out.format("\nInput the action (add, remove, import, export, ask, exit, log, hardest card, reset stats):\n");
            String command = scanner.nextLine();
            switch (command) {
                case "add":
                    addCard();
                    break;
                case "remove":
                    removeCard();
                    break;
                case "import":
                    importFromFile();
                    break;
                case "export":
                    exportToFile();
                    break;
                case "ask":
                    ask();
                    break;
                case "log":
                    saveSessionToLog();
                    break;
                case "hardest card":
                    showStatistics();
                    break;
                case "reset stats":
                    resetStatistics();
                    break;
                case "exit":
                    if (exportOnExitFile != null) {
                        exportToFile(exportOnExitFile);
                    }
                    System.out.println("Bye bye!");
                    return;
            }
        }
    }

    private static void setExportOnExitFile(File file) {
        exportOnExitFile = file;
    }

    private static void processStartupOptions(String[] args) {
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-import")) {
                if (i + 1 < args.length) {
                    File file = new File(args[i + 1]);
                    importFromFile(file);
                }
                i++;

            } else if (args[i].equals("-export")) {
                if (i + 1 < args.length) {
                    setExportOnExitFile(new File(args[i + 1]));
                }
                i++;
            }
        }
    }

    public static void addCard() {
        System.out.println("The card:");
        String term = scanner.nextLine();
        if (cards.containsTerm(term)) {
            System.out.printf("The card \"%s\" already exists.\n", term);
            return;
        }
        System.out.println("The definition of the card:");
        String definition = scanner.nextLine();
        if (cards.containsDefinition(definition)) {
            System.out.printf("The definition \"%s\" already exists.\n", definition);
            return;
        }
        cards.addCard(term, definition);
        System.out.printf("The pair (\"%s\":\"%s\") has been added.\n", term, definition);
    }

    public static void removeCard() {
        System.out.println("Which card?");
        String term = scanner.nextLine();
        if (cards.removeCard(term)) {
            System.out.println("The card has been removed.");
        } else {
            System.out.printf("Can't remove \"%s\": there is no such card.\n", term);
        }
    }

    public static void importFromFile() {
        System.out.println("File name:");
        File file = new File(scanner.nextLine());
        importFromFile(file);
    }

    public static void importFromFile(File file) {
        try (Scanner in = new Scanner(file)) {
            importFromFile(in);
        } catch (FileNotFoundException e) {
            System.out.println("File not found.");
        }
    }

    private static void importFromFile(Scanner in) {
        Pattern pattern = Pattern.compile("\"([^\"]*)\":\"([^\"]*)\":([^\"]*)");
        int loaded = 0;
        while (in.hasNextLine()) {
            Matcher matcher = pattern.matcher(in.nextLine());
            if (matcher.matches()) {
                String term = matcher.group(1);
                String definition = matcher.group(2);
                int mistakes = Integer.parseInt(matcher.group(3));
                cards.updateCard(term, definition);
                statistics.setMistakeCount(term, mistakes);
                loaded++;
            } else {
                throw new RuntimeException("Bad file format");
            }
        }
        System.out.printf("%d cards have been loaded.\n", loaded);
    }

    public static void exportToFile() {
        System.out.println("File name:");
        File file = new File(scanner.nextLine());
        exportToFile(file);
    }

    public static void exportToFile(File file) {
        try (PrintStream out = new PrintStream(file)) {
            exportToFile(out);
        } catch (FileNotFoundException e) {
            System.out.println("File not found.");
        }
    }

    private static void exportToFile(PrintStream out) {
        cards.allTerms().forEach(term -> {
            String definition = cards.getDefinitionByTerm(term);
            out.printf("\"%s\":\"%s\":%d\n", term, definition, statistics.mistakes(term));
        });
        System.out.printf("%d cards have been saved.\n", cards.count());
    }

    private static void ask() {
        System.out.println("How many times to ask?");
        int timesToAsk = Integer.parseInt(scanner.nextLine());
        for (int i = 1; i <= timesToAsk; i++) {
            String term = cards.getRandomTerm();
            System.out.printf("Print the definition of \"%s\":\n", term);
            String answer = scanner.nextLine();
            String definition = cards.getDefinitionByTerm(term);
            if (Objects.equals(answer, definition)) {
                System.out.println("Correct!");
                continue;
            }
            statistics.accountMistake(term);

            System.out.printf("Wrong. The right answer is \"%s\"", definition);
            if (cards.containsDefinition(answer)) {
                System.out.printf(", but your definition is correct for \"%s\"",
                cards.getTermByDefinition(answer));
            }
            System.out.println(".");
        }
    }

    private static void saveSessionToLog() {
        System.out.println("File name:");
        String filename = scanner.nextLine();
        Logger.saveToFile(filename);
        System.out.println("The log has been saved.");
    }

    private static void showStatistics() {
        System.out.print(statistics.getStatistics());
    }

    private static void resetStatistics() {
        statistics.reset();
        System.out.println("Card statistics have been reset.");
    }
}
