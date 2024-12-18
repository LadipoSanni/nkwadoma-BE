package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.loanManagement;

import africa.nkwadoma.nkwadoma.domain.enums.loanEnums.LoanOfferResponse;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class LoanOfferAcceptRequest {

    @NotBlank(message = "LoanOffer Id Is Required")
    private String loanOfferId;
    @NotNull(message = "Offer Response Is Is Required")
    private LoanOfferResponse loaneeResponse;
}
