package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.investmentVehicle;

import africa.nkwadoma.nkwadoma.application.ports.output.identity.IdentityManagerOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.UserIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.investmentVehicle.FinancierOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.investmentVehicle.InvestmentVehicleFinancierOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.investmentVehicle.InvestmentVehicleOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.IdentityRole;
import africa.nkwadoma.nkwadoma.domain.enums.constants.InvestmentVehicleMessages;
import africa.nkwadoma.nkwadoma.domain.enums.constants.investmentVehicle.FinancierMessages;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import africa.nkwadoma.nkwadoma.domain.model.investmentVehicle.InvestmentVehicle;
import africa.nkwadoma.nkwadoma.domain.model.investmentVehicle.Financier;
import africa.nkwadoma.nkwadoma.domain.model.investmentVehicle.InvestmentVehicleFinancier;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.investmentVehicle.FinancierMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.investmentVehicle.FinancierEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.investmentVehicle.FinancierRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Component
public class FinancierAdapter implements FinancierOutputPort {
    private final FinancierRepository financierRepository;
    private final FinancierMapper financierMapper;


    @Override
    public Financier saveFinancier(Financier financier) throws MeedlException {
        MeedlValidator.validateObjectInstance(financier, "Financier can not be empty.");
        financier.validate();
        FinancierEntity financierEntity = financierMapper.map(financier);
        FinancierEntity savedFinancierEntity = financierRepository.save(financierEntity);
        log.info("Financier saved to db: {}", savedFinancierEntity);

        return financierMapper.map(savedFinancierEntity);
    }
    @Override
    public Financier findFinancierByFinancierId(String financierId) throws MeedlException {
        MeedlValidator.validateUUID(financierId, FinancierMessages.INVALID_FINANCIER_ID.getMessage());
        FinancierEntity financierEntity = financierRepository.findById(financierId)
                .orElseThrow(()-> new MeedlException("Financier not found"));
        return financierMapper.map(financierEntity);
    }

    @Override
    public Financier findFinancierByUserId(String id) throws MeedlException {
        FinancierEntity foundFinancier = financierRepository.findByIndividual_Id(id)
                .orElseThrow(()-> new MeedlException("Financier not found") );
        return financierMapper.map(foundFinancier);
    }

    @Override
    public void delete(String financierId) throws MeedlException {
        MeedlValidator.validateUUID(financierId, FinancierMessages.INVALID_FINANCIER_ID.getMessage());
        financierRepository.deleteById(financierId);
    }

    @Override
    public Page<Financier> viewAllFinancier(Financier financier) throws MeedlException {
        log.info("Searching for all financier on the platform at adapter level.");
        MeedlValidator.validateObjectInstance(financier, FinancierMessages.EMPTY_FINANCIER_PROVIDED.getMessage());
        MeedlValidator.validatePageSize(financier.getPageSize());
        MeedlValidator.validatePageNumber(financier.getPageNumber());
        Pageable pageRequest = PageRequest.of(financier.getPageNumber(), financier.getPageSize());
        log.info("Page number: {}, page size: {}", financier.getPageNumber(), financier.getPageSize());
        Page<FinancierEntity> financierEntities = financierRepository.findAll(pageRequest);
        log.info("Found financiers in db: {}", financierEntities);
        return financierEntities.map(financierMapper::map);
    }


}
