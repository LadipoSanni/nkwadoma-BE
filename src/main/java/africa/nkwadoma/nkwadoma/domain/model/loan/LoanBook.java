package africa.nkwadoma.nkwadoma.domain.model.loan;

import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.education.Cohort;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;

@Getter
@Setter
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class LoanBook {
    private String absoluteFilePath;
    private File file;
    private Cohort cohort;
    private String loanProductId;
    private List<Loanee> loanees;

    public void validate() throws MeedlException {
        MeedlValidator.validateObjectInstance(this.getCohort(), "Cohort details can not be empty.");
        MeedlValidator.validateUUID(this.getCohort().getId(), "Cohort id cannot be null.");
        MeedlValidator.validateObjectInstance(this.getFile(), "Please Provide file to upload");
        MeedlValidator.validateUUID(this.loanProductId, "Loan product id is required.");
    }
}
