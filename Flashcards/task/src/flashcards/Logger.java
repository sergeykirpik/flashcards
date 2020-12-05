package flashcards;

import java.io.*;
import java.util.Scanner;

public class Logger {

    private static final PrintStream stdout = System.out;
    private static final LogOutputStream logOutputStream = new LogOutputStream();
    private static final Scanner scanner = new Scanner(System.in);
    private static final LogScanner logScanner = new LogScannerImpl();

    public static LogScanner getScanner() {
        return logScanner;
    }

    public static void saveToFile(File file) {
        try(OutputStream fileOutputStream = new FileOutputStream(file)) {
            logOutputStream.writeTo(fileOutputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveToFile(String pathname) {
        saveToFile(new File(pathname));
    }

    public static void redirectSystemOut() {
        System.setOut(new PrintStream(logOutputStream));
    }

    private static class LogOutputStream extends OutputStream {
        private final ByteArrayOutputStream baos = new ByteArrayOutputStream();

        @Override
        public void write(int b) {
            baos.write(b);
            stdout.write(b);
        }

        public void writeTo(OutputStream outputStream) throws IOException {
            baos.writeTo(outputStream);
        }

        public void appendToBuffer(String s) {
            try {
                baos.write(s.getBytes());
                baos.write('\n');
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static class LogScannerImpl implements LogScanner {
        public String nextLine() {
            String line = scanner.nextLine();
            logOutputStream.appendToBuffer(line);
            return line;
        }
    }
}

interface LogScanner {
    String nextLine();
}