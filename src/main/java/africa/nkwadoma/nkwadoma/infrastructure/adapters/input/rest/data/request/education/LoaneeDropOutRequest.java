package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.education;


import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class LoaneeDropOutRequest {

    private String loaneeId;
    private String cohortId;
    private String reasonForDropOut;
}
