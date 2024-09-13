package africa.nkwadoma.nkwadoma.domain.model;


import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class IdentityVerification {

    private String number;
    private String image;
}
