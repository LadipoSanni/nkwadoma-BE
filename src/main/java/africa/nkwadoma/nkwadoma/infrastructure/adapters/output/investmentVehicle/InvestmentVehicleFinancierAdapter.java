package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.investmentVehicle;

import africa.nkwadoma.nkwadoma.application.ports.output.investmentVehicle.InvestmentVehicleFinancierOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.ActivationStatus;
import africa.nkwadoma.nkwadoma.domain.enums.constants.InvestmentVehicleMessages;
import africa.nkwadoma.nkwadoma.domain.enums.constants.investmentVehicle.FinancierMessages;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.investmentVehicle.Financier;
import africa.nkwadoma.nkwadoma.domain.model.investmentVehicle.InvestmentVehicleFinancier;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.investmentVehicle.FinancierMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.investmentVehicle.InvestmentVehicleFinancierMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.investmentVehicle.FinancierEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.investmentVehicle.InvestmentVehicleFinancierEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.investmentVehicle.InvestmentVehicleFinancierRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class InvestmentVehicleFinancierAdapter implements InvestmentVehicleFinancierOutputPort {
    private final InvestmentVehicleFinancierRepository investmentVehicleFinancierRepository;
    private final InvestmentVehicleFinancierMapper investmentVehicleFinancierMapper;
    private final FinancierMapper financierMapper;
    @Override
    public InvestmentVehicleFinancier save(InvestmentVehicleFinancier investmentVehicleFinancier) throws MeedlException {
        MeedlValidator.validateObjectInstance(investmentVehicleFinancier, "Investment vehicle financier can not be empty.");
        investmentVehicleFinancier.validate();
        InvestmentVehicleFinancierEntity investmentVehicleFinancierEntity =
                investmentVehicleFinancierMapper.toInvestmentVehicleFinancierEntity(investmentVehicleFinancier);
        log.info("The vehicle financier entity mapped {}", investmentVehicleFinancierEntity);
        InvestmentVehicleFinancierEntity savedInvestmentVehicleFinancierEntity = investmentVehicleFinancierRepository.save(investmentVehicleFinancierEntity);
        return investmentVehicleFinancierMapper.toInvestmentVehicleFinancier(savedInvestmentVehicleFinancierEntity);

    }

    @Override
    public Optional<InvestmentVehicleFinancier> findByInvestmentVehicleIdAndFinancierId(String investmentVehicleId, String financierId) throws MeedlException {
        MeedlValidator.validateUUID(investmentVehicleId, InvestmentVehicleMessages.INVALID_INVESTMENT_VEHICLE_ID.getMessage());
        MeedlValidator.validateUUID(financierId, "Invalid financier id provided");
        log.info("Validated id for view InvestmentVehicleFinancier by vehicle id and financier id is {} ----- {}", investmentVehicleId, financierId);
        Optional<InvestmentVehicleFinancierEntity> optionalInvestmentVehicleFinancierEntity = investmentVehicleFinancierRepository.findByInvestmentVehicleIdAndFinancierId(investmentVehicleId, financierId);

        if (optionalInvestmentVehicleFinancierEntity.isEmpty()){
            return Optional.empty();
        }
        log.info("Investment vehicle financier found {}", optionalInvestmentVehicleFinancierEntity.get().getId());
        return Optional.of(investmentVehicleFinancierMapper.toInvestmentVehicleFinancier(optionalInvestmentVehicleFinancierEntity.get()));
    }

    @Override
    public void deleteInvestmentVehicleFinancier(String id) throws MeedlException {
        MeedlValidator.validateUUID(id, "Invalid investment vehicle financier Id provided");
        investmentVehicleFinancierRepository.deleteById(id);
    }

    @Override
    public Page<Financier> viewAllFinancierInAnInvestmentVehicle(String investmentVehicleId, Pageable pageRequest) throws MeedlException {
        MeedlValidator.validateUUID(investmentVehicleId, InvestmentVehicleMessages.INVALID_INVESTMENT_VEHICLE_ID.getMessage());
        Page<FinancierEntity> financiers = investmentVehicleFinancierRepository.findFinanciersByInvestmentVehicleId(investmentVehicleId, pageRequest);
        return financiers.map(financierMapper::map);
    }
    @Override
    public Page<Financier> viewAllFinancierInAnInvestmentVehicle(String investmentVehicleId, ActivationStatus activationStatus, Pageable pageRequest) throws MeedlException {
        MeedlValidator.validateUUID(investmentVehicleId, InvestmentVehicleMessages.INVALID_INVESTMENT_VEHICLE_ID.getMessage());
        MeedlValidator.validateObjectInstance(activationStatus, "Please provide a valid activation status to find by.");
        Page<FinancierEntity> financiers = investmentVehicleFinancierRepository.findFinanciersByInvestmentVehicleIdAndStatus(investmentVehicleId, activationStatus, pageRequest);
        return financiers.map(financierMapper::map);
    }


    @Transactional
    @Override
    public void deleteByInvestmentVehicleIdAndFinancierId(String investmentId, String financierId) throws MeedlException {
        MeedlValidator.validateUUID(investmentId, InvestmentVehicleMessages.INVALID_INVESTMENT_VEHICLE_ID.getMessage());
        MeedlValidator.validateUUID(financierId, "Invalid financier id provided");
        investmentVehicleFinancierRepository.deleteByInvestmentVehicleIdAndFinancierId(investmentId, financierId);
    }

    @Override
    public List<InvestmentVehicleFinancier> findAllInvestmentVehicleFinancierInvestedIn(String financierId) throws MeedlException {
        MeedlValidator.validateUUID(financierId, FinancierMessages.INVALID_FINANCIER_ID.getMessage());
        List<InvestmentVehicleFinancierEntity> investmentVehicleFinanciers = investmentVehicleFinancierRepository.findAllInvestmentVehicleFinancierInvestedIn(financierId);
        return investmentVehicleFinancierMapper.toInvestmentVehicleFinancier(investmentVehicleFinanciers);
    }

    @Override
    public List<InvestmentVehicleFinancier> findByAll(String investmentVehicleId, String financierId) {
        List<InvestmentVehicleFinancierEntity> investmentVehicleFinanciers = investmentVehicleFinancierRepository.findAllByInvestmentVehicle_IdAndFinancier_Id(investmentVehicleId, financierId);
        return investmentVehicleFinancierMapper.toInvestmentVehicleFinancier(investmentVehicleFinanciers);
    }

    @Override
    public boolean checkIfAnyFinancierHaveInvestedInVehicle(String investmentVehicleId) throws MeedlException {
        MeedlValidator.validateUUID(investmentVehicleId, InvestmentVehicleMessages.INVALID_INVESTMENT_VEHICLE_ID.getMessage());
        return investmentVehicleFinancierRepository.checkIfAnyFinancierAlreadyInvestedInVehicle(investmentVehicleId);
    }

    @Override
    public void removeFinancierAssociationWithInvestmentVehicle(String investmentVehicleId) throws MeedlException {
        MeedlValidator.validateUUID(investmentVehicleId, InvestmentVehicleMessages.INVALID_INVESTMENT_VEHICLE_ID.getMessage());
        investmentVehicleFinancierRepository.deleteByInvestmentVehicleId(investmentVehicleId);
    }
}
