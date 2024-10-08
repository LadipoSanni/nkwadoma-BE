package africa.nkwadoma.nkwadoma.application.ports.output.investmentVehicle;

import africa.nkwadoma.nkwadoma.domain.exceptions.MiddlException;
import africa.nkwadoma.nkwadoma.domain.model.investmentVehicle.InvestmentVehicleIdentity;

import java.util.List;

public interface InvestmentVehicleIdentityOutputPort {
    InvestmentVehicleIdentity save(InvestmentVehicleIdentity capitalGrowth) throws MiddlException;

    InvestmentVehicleIdentity findById(String id) throws MiddlException;

    List<InvestmentVehicleIdentity> findAll();
}
