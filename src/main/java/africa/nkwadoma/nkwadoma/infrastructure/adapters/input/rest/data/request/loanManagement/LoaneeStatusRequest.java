package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.loanManagement;

import africa.nkwadoma.nkwadoma.domain.enums.loanee.LoaneeStatus;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class LoaneeStatusRequest {

    private List<String> loaneeIds;
    private LoaneeStatus loaneeStatus;
}
