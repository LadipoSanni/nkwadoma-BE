package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.investmentVehicle;

import africa.nkwadoma.nkwadoma.application.ports.output.investmentVehicle.InvestmentVehicleFinancierOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.constants.InvestmentVehicleMessages;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.investmentVehicle.InvestmentVehicleFinancier;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.investmentVehicle.InvestmentVehicleFinancierMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.investmentVehicle.InvestmentVehicleFinancierEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.investmentVehicle.InvestorInvestmentVehicleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class InvestmentVehicleFinancierAdapter implements InvestmentVehicleFinancierOutputPort {
    private final InvestorInvestmentVehicleRepository investorInvestmentVehicleRepository;
    private final InvestmentVehicleFinancierMapper investmentVehicleFinancierMapper;
    @Override
    public InvestmentVehicleFinancier save(InvestmentVehicleFinancier investmentVehicleFinancier) throws MeedlException {
        MeedlValidator.validateObjectInstance(investmentVehicleFinancier, "Investment vehicle financier not be empty.");
        investmentVehicleFinancier.validate();
        InvestmentVehicleFinancierEntity investmentVehicleFinancierEntity =
                investmentVehicleFinancierMapper.toInvestmentVehicleFinancierEntity(investmentVehicleFinancier);
        InvestmentVehicleFinancierEntity savedInvestmentVehicleFinancierEntity = investorInvestmentVehicleRepository.save(investmentVehicleFinancierEntity);
        return investmentVehicleFinancierMapper.toInvestmentVehicleFinancier(savedInvestmentVehicleFinancierEntity);

    }

    @Override
    public InvestmentVehicleFinancier findByInvestmentVehicleIdAndFinancierId(String investmentVehicleId, String financierId) throws MeedlException {
        MeedlValidator.validateUUID(investmentVehicleId, InvestmentVehicleMessages.INVALID_INVESTMENT_VEHICLE_ID.getMessage());
        MeedlValidator.validateUUID(financierId, "Invalid financier Id provided");
        InvestmentVehicleFinancierEntity investmentVehicleFinancierEntity = investorInvestmentVehicleRepository.findByInvestmentVehicleIdAndFinancierId(investmentVehicleId, financierId)
                .orElseThrow(()-> new MeedlException("Financier may not have been added to investment vehicle"));
        log.info("Investment vehicle financier found {}", investmentVehicleFinancierEntity.getId());
        return investmentVehicleFinancierMapper.toInvestmentVehicleFinancier(investmentVehicleFinancierEntity);
    }

    @Override
    public void deleteInvestmentVehicleFinancier(String id) throws MeedlException {
        MeedlValidator.validateUUID(id, "Invalid investment vehicle financier Id provided");
        investorInvestmentVehicleRepository.deleteById(id);
    }
}
