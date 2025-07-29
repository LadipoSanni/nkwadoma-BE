package africa.nkwadoma.nkwadoma.domain.model.loan.loanBook;

import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.education.Cohort;
import africa.nkwadoma.nkwadoma.domain.model.education.CohortLoanee;
import africa.nkwadoma.nkwadoma.domain.model.notification.MeedlNotification;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import lombok.*;

import java.io.File;
import java.util.List;

@Getter
@Setter
@ToString
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class LoanBook {
    private String absoluteFilePath;
    private File file;
    private Cohort cohort;
    private String loanProductId;
    private String actorId;
    private MeedlNotification meedlNotification;
    private List<RepaymentHistory> repaymentHistories;
    private List<CohortLoanee> cohortLoanees;

    public void validateLoanBook() throws MeedlException {
        MeedlValidator.validateObjectInstance(this.getCohort(), "Cohort details can not be empty.");
        MeedlValidator.validateUUID(this.getCohort().getId(), "Cohort id cannot be null.");
        MeedlValidator.validateObjectInstance(this.file, "Please provide file to upload");

    }
}
