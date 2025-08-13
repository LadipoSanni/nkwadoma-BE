package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.identity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MFARequest {
    private String MFAPhoneNumber;
    private boolean enablePhoneNumberMFA;
    private boolean enableEmailMFA;
    private boolean disableMFA;
}
