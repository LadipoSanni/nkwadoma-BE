package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.education;


import africa.nkwadoma.nkwadoma.domain.enums.EmploymentStatus;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class EmploymentStatusResponse {

    private String cohortId;
    private String loaneeId;
    private EmploymentStatus employmentStatus;
}
