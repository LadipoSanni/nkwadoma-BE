package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.identity;

import africa.nkwadoma.nkwadoma.domain.enums.loanee.LevelOfEducation;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UpdateLoaneeProfileRequest {

    public String stateOfResidence;
    private LevelOfEducation levelOfEducation;
}
