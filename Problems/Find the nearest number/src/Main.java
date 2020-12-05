import java.util.*;

public class Main {
    public static void main(String[] args) {
        // write your code here
        Scanner scanner = new Scanner(System.in);
        String[] lineOfNumbers = scanner.nextLine().split("\\s+");
        int pivot = Integer.parseInt(scanner.nextLine());
        List<Integer> numbers = new ArrayList<>();
        for (String number : lineOfNumbers) {
            numbers.add(Integer.parseInt(number));
        }
        numbers.sort(Integer::compare);
        List<Integer> distances = new ArrayList<>();
        for (Integer number : numbers) {
            distances.add(Math.abs(number - pivot));
        }
        int minDistance = distances.get(0);
        for (Integer distance : distances) {
            minDistance = Math.min(distance, minDistance);
        }
        for (int i = 0; i < distances.size(); i++) {
            if (distances.get(i) == minDistance) {
                System.out.print(numbers.get(i) + " ");
            }
        }
        System.out.println();
    }
}