package africa.nkwadoma.nkwadoma.domain.enums.constants;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MeedlConstants {

    public static final int ONE = 1;
    public static final String MEEDL = "Meedl";
    public static final String MALE = "Male";
    public static final String FEMALE = "Female";
    public static final List<String> SOUTH_EAST = Arrays.asList("Anambra", "Enugu", "Imo", "Abia", "Ebonyi");
    public static final List<String> SOUTH_WEST = Arrays.asList("Lagos", "Ogun", "Oyo", "Osun", "Ondo", "Ekiti");
    public static final List<String> SOUTH_SOUTH = Arrays.asList("Akwa Ibom", "Bayelsa", "Cross River", "Delta", "Edo", "Rivers");
    public static final List<String> NORTH_EAST = Arrays.asList("Adamawa", "Bauchi", "Borno", "Gombe", "Taraba", "Yobe");
    public static final List<String> NORTH_WEST = Arrays.asList("Jigawa", "Kaduna", "Kano", "Katsina", "Kebbi", "Sokoto", "Zamfara");
    public static final List<String> NORTH_CENTRAL = Arrays.asList("Benue", "Kogi", "Kwara", "Nasarawa", "Niger", "Plateau", "FCT");
    public static final String NIGERIA = "Nigeria";
    public static final Map<String, String> GENDER_TO_FULL = new HashMap<>();
    static {
        GENDER_TO_FULL.put("m", MALE);
        GENDER_TO_FULL.put("f", FEMALE);
        GENDER_TO_FULL.put("male", MALE);
        GENDER_TO_FULL.put("female", FEMALE);
    }
}
