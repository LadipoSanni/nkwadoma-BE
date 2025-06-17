package africa.nkwadoma.nkwadoma.application.ports.input.notification;

import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import africa.nkwadoma.nkwadoma.domain.model.investmentvehicle.InvestmentVehicle;

public interface FinancierEmailUseCase {
    void inviteFinancierToPlatform(UserIdentity userIdentity) throws MeedlException;

    void inviteFinancierToVehicle(UserIdentity userIdentity, InvestmentVehicle investmentVehicle) throws MeedlException;
}
