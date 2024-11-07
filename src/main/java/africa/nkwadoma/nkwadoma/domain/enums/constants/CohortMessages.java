package africa.nkwadoma.nkwadoma.domain.enums.constants;


import lombok.Getter;

@Getter
public enum CohortMessages {
    COHORT_EXIST("cohort exist"),
    INPUT_CANNOT_BE_NULL("input cannot be empty or null"),
    COHORT_DOES_NOT_EXIST("cohort does not exist"),
    COHORT_WITH_LOAN_DETAILS_CANNOT_BE_EDITED("Cohort With Loan Details Cannot Be Edited");

    private final String message;

    CohortMessages(String message) {
        this.message = message;
    }
}
