package africa.nkwadoma.nkwadoma.domain.model.loan;

import lombok.*;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NextOfKin {
    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String nextOfKinRelationship;
    private String contactAddress;
    private Loanee loanee;

}