package africa.nkwadoma.nkwadoma.testUtilities;

import java.util.Random;

public class TestUtils {

    public static String generateEmail(int emailLength){
        return generateName(emailLength) + "@grr.la";
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
}
