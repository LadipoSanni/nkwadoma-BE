package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.loanManagement.loanee;


import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class LoaneeProfileResponse {


    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private int creditScore;



}
