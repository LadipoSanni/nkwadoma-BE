package africa.nkwadoma.nkwadoma.domain.model.identity;


import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class IdentityVerification {

    private String identityId;
    private String identityImage;
}
