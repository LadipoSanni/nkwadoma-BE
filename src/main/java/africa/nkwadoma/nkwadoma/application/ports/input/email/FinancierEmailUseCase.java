package africa.nkwadoma.nkwadoma.application.ports.input.email;

import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import africa.nkwadoma.nkwadoma.domain.model.investmentVehicle.Financier;
import africa.nkwadoma.nkwadoma.domain.model.investmentVehicle.InvestmentVehicle;

public interface FinancierEmailUseCase {
    void inviteFinancierToPlatform(UserIdentity userIdentity) throws MeedlException;

    void inviteFinancierToVehicle(UserIdentity userIdentity, InvestmentVehicle investmentVehicle) throws MeedlException;
}
