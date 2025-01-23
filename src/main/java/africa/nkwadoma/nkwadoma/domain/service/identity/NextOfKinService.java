package africa.nkwadoma.nkwadoma.domain.service.identity;

import africa.nkwadoma.nkwadoma.application.ports.input.identity.*;
import africa.nkwadoma.nkwadoma.application.ports.output.education.*;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.*;
import africa.nkwadoma.nkwadoma.domain.enums.constants.*;
import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import africa.nkwadoma.nkwadoma.domain.model.loan.*;
import africa.nkwadoma.nkwadoma.domain.validation.*;
import lombok.*;
import org.apache.commons.lang3.*;
import org.springframework.stereotype.*;

import java.util.*;

@Service
@RequiredArgsConstructor
public class NextOfKinService implements CreateNextOfKinUseCase {
    private final NextOfKinIdentityOutputPort nextOfKinIdentityOutputPort;
    private final LoaneeOutputPort loaneeOutputPort;

    @Override
    public NextOfKin saveAdditionalDetails(NextOfKin nextOfKin) throws MeedlException {
        MeedlValidator.validateObjectInstance(nextOfKin);
        nextOfKin.validate();
        trimSpaceForUserIdentity(nextOfKin.getLoanee());
        Loanee foundLoanee = loaneeOutputPort.findByUserId(nextOfKin.getLoanee().getUserIdentity().getId()).
                orElseThrow(()-> new MeedlException(IdentityMessages.LOANEE_NOT_FOUND.getMessage()));
        Optional<NextOfKin> foundNextOfKin = nextOfKinIdentityOutputPort.findByLoaneeId(foundLoanee.getId());
        if (foundNextOfKin.isPresent()) {
            throw new MeedlException(IdentityMessages.LOANEE_HAS_NEXT_OF_KIN.getMessage());
        }
        nextOfKin.setLoanee(foundLoanee);
        return nextOfKinIdentityOutputPort.save(nextOfKin);
    }

    private void trimSpaceForUserIdentity(Loanee loanee) {
        if (ObjectUtils.isNotEmpty(loanee)) {
            loanee.getUserIdentity().setAlternateContactAddress(loanee.getUserIdentity().getAlternateContactAddress().trim());
            loanee.getUserIdentity().setAlternatePhoneNumber(loanee.getUserIdentity().getAlternatePhoneNumber().trim());
            loanee.getUserIdentity().setAlternateEmail(loanee.getUserIdentity().getAlternateEmail().trim());
        }
    }
}
