package africa.nkwadoma.nkwadoma.domain.enums.constants;


import lombok.Getter;

@Getter
public enum CohortMessages {
    COHORT_EXIST("cohort exist");

    private final String message;

    CohortMessages(String message) {
        this.message = message;
    }
}
