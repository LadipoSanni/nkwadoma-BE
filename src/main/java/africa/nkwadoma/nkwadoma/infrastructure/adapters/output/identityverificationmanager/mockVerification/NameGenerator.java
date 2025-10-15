package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.identityverificationmanager.mockVerification;

import java.util.Random;

public class NameGenerator {

    private static final char[] VOWELS = {'A', 'E', 'I', 'O', 'U'};
    private static final char[] CONSONANTS = {
            'B', 'C', 'D', 'F', 'G', 'H', 'J', 'K',
            'L', 'M', 'N', 'P', 'Q', 'R', 'S', 'T',
            'V', 'W', 'X', 'Y', 'Z'
    };

    private static final Random RANDOM = new Random();

    public static String generateName(int length) {
        if (length <= 0) {
            throw new IllegalArgumentException("Length must be greater than zero");
        }

        StringBuilder word = new StringBuilder();

        for (int i = 0; i < length; i++) {
            if (i % 2 == 0) {
                // Even index → Consonant
                char consonant = CONSONANTS[RANDOM.nextInt(CONSONANTS.length)];
                word.append(consonant);
            } else {
                // Odd index → Vowel
                char vowel = VOWELS[RANDOM.nextInt(VOWELS.length)];
                word.append(vowel);
            }
        }

        return word.toString();
    }
}

