package africa.nkwadoma.nkwadoma.application.ports.input.notification;

import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.financier.Financier;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import africa.nkwadoma.nkwadoma.domain.model.investmentvehicle.InvestmentVehicle;

public interface FinancierEmailUseCase {
    void inviteIndividualFinancierToPlatform(UserIdentity userIdentity) throws MeedlException;

    void inviteCooperateFinancierToPlatform(Financier financier) throws MeedlException;

    void inviteIndividualFinancierToVehicle(UserIdentity userIdentity, InvestmentVehicle investmentVehicle) throws MeedlException;

    void inviteCooperateFinancierToVehicle(Financier financier, InvestmentVehicle investmentVehicle) throws MeedlException;
}
