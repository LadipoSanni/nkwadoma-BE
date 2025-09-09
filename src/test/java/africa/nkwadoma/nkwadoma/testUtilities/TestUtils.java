package africa.nkwadoma.nkwadoma.testUtilities;



import africa.nkwadoma.nkwadoma.domain.enums.loanee.ModeOfPayment;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

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

    public static List<String> generateRandomLoanBookCSV(String  absoluteCSVFilePathAndName, int numberOfRows) throws IOException {
        Path filePath = Path.of(absoluteCSVFilePathAndName);
        List<String> lines = new ArrayList<>();
        List<String> emails = new ArrayList<>();

        lines.add("firstName,lastName,email,phoneNumber,DON,initialDeposit,amountRequested,amountReceived,amountApproved");

        for (int i = 0; i < numberOfRows; i++) {
            String firstName = generateName(4);
            String lastName = generateName(5);
            String email = generateEmail(6);
            emails.add(email);
            String phoneNumber = "080" + String.format("%08d", (int)(Math.random() * 100000000));
            String don = "2024-" + String.format("%02d", (i % 12) + 1) + "-" + String.format("%02d", (i % 28) + 1);
            int initialDeposit = 5000 + (i * 1000);
            int amountRequested = 30000 + (i * 2000);
            int amountReceived = amountRequested - (i % 5) * 1000;
            int amountApproved = amountRequested - (i % 5) * 1000;

            lines.add(String.join(",", firstName, lastName, email, phoneNumber, don,
                    String.valueOf(initialDeposit),
                    String.valueOf(amountRequested),
                    String.valueOf(amountReceived),
                    String.valueOf(amountApproved)));
        }

        Files.write(filePath, lines);
        return emails;
    }

    public static long generateRandomNumber(int digits) {
        if (digits <= 0) {
            throw new IllegalArgumentException("Number of digits must be greater than 0");
        }

        Random random = new Random();

        // smallest number with N digits
        long min = (long) Math.pow(10, digits - 1);

        // largest number with N digits
        long max = (long) Math.pow(10, digits) - 1;

        // generate random number in range [min, max]
        return min + ((long) (random.nextDouble() * (max - min + 1)));
    }
    public static void generateRandomRepaymentRecordCSV(List<String> emails, String absoluteCSVFilePathAndName) throws IOException {
        Path filePath = Path.of(absoluteCSVFilePathAndName);
        List<String> lines = new ArrayList<>();

        // Updated header
        lines.add("firstName,lastName,email,paymentDate,amountPaid,modeOfPayment");

        for (int i = 0; i < emails.size(); i++) {
            String firstName = generateName(4);
            String lastName = generateName(5);
            String modeOfPayment = ModeOfPayment.CASH.name();
            String email = emails.get(i);
            String paymentDate = "2024-" + String.format("%02d", (i % 12) + 1) + "-" + String.format("%02d", (i % 28) + 1);

            int amountPaid = 5000 + (i * 500);

            lines.add(String.join(",",
                    firstName,
                    lastName,
                    email,
                    paymentDate,
                    String.valueOf(amountPaid),
                    modeOfPayment));
        }

        Files.write(filePath, lines);
    }
    public static String generateRandomUUID() {
        return UUID.randomUUID().toString();
    }
}
