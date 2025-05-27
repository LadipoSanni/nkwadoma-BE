package africa.nkwadoma.nkwadoma.domain.model.loan.loanBook;

import africa.nkwadoma.nkwadoma.domain.model.education.Cohort;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.File;

@Getter
@Setter
@Builder
public class RepaymentRecordBook {
    private String absoluteFilePath;
    private File file;
    private Cohort cohort;
}
