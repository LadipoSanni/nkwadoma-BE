package africa.nkwadoma.nkwadoma.domain.enums.constants;


import lombok.Getter;

@Getter
public enum CohortMessages {
    COHORT_EXIST("cohort exist"),
    INPUT_CANNOT_BE_NULL("input cannot be empty or null"),
    COHORT_DOES_NOT_EXIST("cohort does not exist"),
    COHORT_WITH_LOAN_DETAILS_CANNOT_BE_EDITED("Cohort With Loan Details Cannot Be Edited"),
    COHORT_TUITION_DETAILS_MUST_HAVE_BEEN_UPDATED("Cohort Tuition Details Must Have Been Updated"),
    INITIAL_DEPOSIT_CANNOT_BE_GREATER_THAT_TOTAL_COHORT_FEE("Loanee initial deposit cannot be greater than total cohortFee"),
    AMOUNT_REQUESTED_CANNOT_BE_GREATER_THAT_TOTAL_COHORT_FEE("Amount Requested Can't be greater than total cohortFee"),
    CREATEDBY_NOT_EXIST_IN_ORGANIZATION("Created-By Doesnt Exist In Organization");

    private final String message;

    CohortMessages(String message) {
        this.message = message;
    }
}
