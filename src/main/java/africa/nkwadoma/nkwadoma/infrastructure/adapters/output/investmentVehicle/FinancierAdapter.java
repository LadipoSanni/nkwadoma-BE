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

@Slf4j
@RequiredArgsConstructor
@Component
public class FinancierAdapter implements FinancierOutputPort {
    private final UserIdentityOutputPort userIdentityOutputPort;
    private final IdentityManagerOutputPort identityManagerOutputPort;
    private final FinancierRepository financierRepository;
    private final FinancierMapper financierMapper;
    private final InvestmentVehicleOutputPort investmentVehicleOutputPort;
    private final InvestmentVehicleFinancierOutputPort investmentVehicleFinancierOutputPort;


    @Override
    public Financier saveFinancier(Financier financier) throws MeedlException {
        MeedlValidator.validateObjectInstance(financier, "Financier can not be empty.");
        financier.validate();
        InvestmentVehicle investmentVehicle = investmentVehicleOutputPort.findById(financier.getInvestmentVehicleId());
        addFinancierToVehicle(financier, investmentVehicle);
        FinancierEntity financierEntity = financierMapper.map(financier);
        FinancierEntity savedFinancierEntity = financierRepository.save(financierEntity);
        log.info("Financier saved to db: {}", savedFinancierEntity);

        return financierMapper.map(savedFinancierEntity);
    }
    @Override
    public Financier findFinancierById(String financierId) throws MeedlException {
        MeedlValidator.validateUUID(financierId, FinancierMessages.INVALID_FINANCIER_ID.getMessage());
        FinancierEntity financierEntity = financierRepository.findById(financierId)
                .orElseThrow(()-> new MeedlException("Financier not found"));
        return financierMapper.map(financierEntity);
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

    @Override
    public Page<Financier> viewAllFinanciersInInvestmentVehicle(Financier financier) throws MeedlException {
        MeedlValidator.validateObjectInstance(financier);
        MeedlValidator.validatePageSize(financier.getPageSize());
        MeedlValidator.validatePageNumber(financier.getPageNumber());
        MeedlValidator.validateUUID(financier.getInvestmentVehicleId(), InvestmentVehicleMessages.INVALID_INVESTMENT_VEHICLE_ID.getMessage());

        Pageable pageRequest = PageRequest.of(financier.getPageNumber(), financier.getPageSize());
        log.info("View all financiers in a vehicle. Page number: {}, page size: {}", financier.getPageNumber(), financier.getPageSize());
        Page<Financier> foundFinanciers = investmentVehicleFinancierOutputPort.viewAllFinancierInAnInvestmentVehicle(financier.getInvestmentVehicleId(), pageRequest);
        log.info("Found financiers in db: {}", foundFinanciers);
        return foundFinanciers;
    }

    private void addFinancierToVehicle(Financier investmentVehicleFinancier, InvestmentVehicle investmentVehicle) {
        UserIdentity investor = investmentVehicleFinancier.getIndividual();
            try {
                UserIdentity foundInvestor = userIdentityOutputPort.findByEmail(investor.getEmail());
                log.info("User {} exists on platform and can be added to investment vehicle.",investor.getEmail());
                createInvestmentVehicleFinancier(investmentVehicle, foundInvestor);
            } catch (MeedlException e) {
                try {
                    UserIdentity userIdentity = saveFinancier(investmentVehicleFinancier, investor);
                } catch (MeedlException ex) {
                    throw new RuntimeException(ex);
                }
            }

    }

    private void createInvestmentVehicleFinancier(InvestmentVehicle investmentVehicle, UserIdentity financial) throws MeedlException {
        investmentVehicleFinancierOutputPort.save(InvestmentVehicleFinancier.builder()
                        .financier(financial)
                        .investmentVehicle(investmentVehicle)
                        .build());
        log.info("Financier {} added to investment vehicle {}.", financial.getEmail(), investmentVehicle.getName());
    }

    private UserIdentity saveFinancier(Financier financier, UserIdentity investor) throws MeedlException {
        log.info("User {} does not exist on platform and cannot be added to investment vehicle.", investor.getEmail());
        investor.setRole(IdentityRole.FINANCIER);
        investor.setCreatedBy(financier.getInvitedBy());
        UserIdentity userIdentity = identityManagerOutputPort.createUser(investor);
        userIdentity.setCreatedAt(LocalDateTime.now());
        return userIdentityOutputPort.save(userIdentity);
    }
}
