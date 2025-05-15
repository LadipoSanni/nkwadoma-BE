package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.loanManagement.loanBook;

import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.education.CreateCohortRequest;
import lombok.Getter;
import lombok.Setter;

import java.io.File;

@Getter
@Setter
public class LoanBookRequest {
    private File file;
    private String absoluteFilePath;
    private CreateCohortRequest createCohortRequest;
}
