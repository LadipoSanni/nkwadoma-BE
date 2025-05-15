package africa.nkwadoma.nkwadoma.domain.model.loan;

import africa.nkwadoma.nkwadoma.domain.model.education.Cohort;
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
    private String createdBy;
    private File file;
    private Cohort cohort;
    private List<Loanee> loanees;
}
