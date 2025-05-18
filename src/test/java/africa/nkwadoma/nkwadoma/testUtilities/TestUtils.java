package africa.nkwadoma.nkwadoma.testUtilities;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TestUtils {

    public static String generateEmail(int emailLength){
        return generateName(emailLength) + "@grr.la";
    }
    public static String generateEmail( String name, int emailLength){
        return String.format(name+ "%s@grr.la", generateName(emailLength) );
    }
    public static String generateName(String actualName, int length) {
        return String.format(actualName+"%s", generateName(length));
    }
    public static String generateName(int length) {
        if (length <= 0) {
            throw new IllegalArgumentException("Length must be positive");
        }

        Random random = new Random();
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < length; i++) {
            boolean upper = random.nextBoolean();
            int ascii = upper ? 65 + random.nextInt(26) : 97 + random.nextInt(26);
            result.append((char) ascii);
        }

        return result.toString();
    }

    public static void generateRandomCSV(String  absoluteCSVFilePathAndName, int numberOfRows) throws IOException {
        Path filePath = Path.of(absoluteCSVFilePathAndName);
        List<String> lines = new ArrayList<>();

        lines.add("firstName,lastName,email,phoneNumber,DON,initialDeposit,amountRequested,amountReceived");

        for (int i = 0; i < numberOfRows; i++) {
            String firstName = generateName(4);
            String lastName = generateName(5);
            String email = generateEmail(6);
            String phoneNumber = "080" + String.format("%08d", (int)(Math.random() * 100000000));
            String don = "2024-" + String.format("%02d", (i % 12) + 1) + "-" + String.format("%02d", (i % 28) + 1);
            int initialDeposit = 5000 + (i * 1000);
            int amountRequested = 30000 + (i * 2000);
            int amountReceived = amountRequested - (i % 5) * 1000;

            lines.add(String.join(",", firstName, lastName, email, phoneNumber, don,
                    String.valueOf(initialDeposit),
                    String.valueOf(amountRequested),
                    String.valueOf(amountReceived)));
        }

        Files.write(filePath, lines);
    }

}
