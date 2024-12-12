package africa.nkwadoma.nkwadoma.domain.enums.constants;


import lombok.Getter;

@Getter
public enum CohortMessages {
    COHORT_EXIST("cohort exist"),
    INPUT_CANNOT_BE_NULL("Input cannot be empty or null"),
    COHORT_DOES_NOT_EXIST("Cohort does not exist"),
    COHORT_WITH_LOAN_DETAILS_CANNOT_BE_EDITED("Cohort with loan details cannot be edited"),
    COHORT_TUITION_DETAILS_MUST_HAVE_BEEN_UPDATED("Cohort tuition details must have been updated"),
    INITIAL_DEPOSIT_CANNOT_BE_GREATER_THAT_TOTAL_COHORT_FEE("Loanee initial deposit cannot be greater than total cohortFee"),
    AMOUNT_REQUESTED_CANNOT_BE_GREATER_THAT_TOTAL_COHORT_FEE("Amount requested can't be greater than total cohortFee"),
    CREATEDBY_NOT_EXIST_IN_ORGANIZATION("Created-By doesnt exist in organization"),
    COHORT_NAME_REQUIRED("Cohort name is required"),
    COHORT_WITH_NAME_EXIST("Cohort with name xeist");

    private final String message;

    CohortMessages(String message) {
        this.message = message;
    }
}
