package africa.nkwadoma.nkwadoma.testUtilities;

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
}
