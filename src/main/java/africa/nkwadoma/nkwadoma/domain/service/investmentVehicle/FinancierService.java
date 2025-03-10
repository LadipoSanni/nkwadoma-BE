package africa.nkwadoma.nkwadoma.domain.service.investmentVehicle;

import africa.nkwadoma.nkwadoma.application.ports.input.investmentVehicle.FinancierUseCase;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.IdentityManagerOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.UserIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.investmentVehicle.FinancierOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.investmentVehicle.InvestmentVehicleFinancierOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.investmentVehicle.InvestmentVehicleOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.meedlNotification.MeedlNotificationOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.IdentityRole;
import africa.nkwadoma.nkwadoma.domain.enums.constants.InvestmentVehicleMessages;
import africa.nkwadoma.nkwadoma.domain.enums.constants.MeedlMessages;
import africa.nkwadoma.nkwadoma.domain.enums.constants.investmentVehicle.FinancierMessages;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.MeedlNotification;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import africa.nkwadoma.nkwadoma.domain.model.investmentVehicle.Financier;
import africa.nkwadoma.nkwadoma.domain.model.investmentVehicle.InvestmentVehicle;
import africa.nkwadoma.nkwadoma.domain.model.investmentVehicle.InvestmentVehicleFinancier;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Service
@Slf4j
public class FinancierService implements FinancierUseCase {
    private final FinancierOutputPort financierOutputPort;
    private final UserIdentityOutputPort userIdentityOutputPort;
    private final IdentityManagerOutputPort identityManagerOutputPort;
    private final InvestmentVehicleOutputPort investmentVehicleOutputPort;
    private final InvestmentVehicleFinancierOutputPort investmentVehicleFinancierOutputPort;
    private final MeedlNotificationOutputPort meedlNotificationOutputPort;


    @Override
    public String inviteFinancier(Financier financier) throws MeedlException {
        inviteFinancierValidation(financier);
        try {
            financier = getFinancierByUserIdentity(financier);
        } catch (MeedlException e) {
            financier = saveNonExistingFinancier(financier);
        }
        InvestmentVehicle investmentVehicle = investmentVehicleOutputPort.findById(financier.getInvestmentVehicleId());
        addFinancierToVehicle(financier, investmentVehicle);
        notifyExistingFinancier(financier, investmentVehicle);
        return "Financier added to investment vehicle";
    }

    private static void inviteFinancierValidation(Financier financier) throws MeedlException {
        MeedlValidator.validateObjectInstance(financier, FinancierMessages.EMPTY_FINANCIER_PROVIDED.getMessage());
        MeedlValidator.validateUUID(financier.getInvestmentVehicleId(), InvestmentVehicleMessages.INVALID_INVESTMENT_VEHICLE_ID.getMessage());
        financier.validate();
    }

    private Financier saveNonExistingFinancier(Financier financier) {
        Financier existingFinancier;
        log.warn("Failed to find user on application. Financier not yet onboarded.");
        try {
            existingFinancier = saveFinancier(financier);
            log.info("Saved non-existing financier {}", existingFinancier.getId());
            financier = updateFinancierDetails(financier, existingFinancier);
        } catch (MeedlException ex) {
            throw new RuntimeException(ex);
        }
        return financier;
    }

    private Financier getFinancierByUserIdentity(Financier financier) throws MeedlException {

        UserIdentity userIdentity = userIdentityOutputPort.findByEmail(financier.getIndividual().getEmail());
        log.info("User identity found by email {} ,when inviting financier ", userIdentity.getEmail());
        if (userIdentity.getRole() != IdentityRole.FINANCIER) {
            //TODO Add new role to user.
        }
        try {
            Financier existingFinancier = financierOutputPort.findFinancierByUserId(userIdentity.getId());
            log.info("Financier found by user identity id {}", userIdentity.getId());
            return updateFinancierDetails(financier, existingFinancier);
            
        }catch (MeedlException e){
            log.warn("User is not previously a financier but exists on the platform");
            log.info("Creating a new financier for user with email : {}", userIdentity.getEmail());
            financier.setIndividual(userIdentity);
            Financier savedFinancier = financierOutputPort.saveFinancier(financier);
            return updateFinancierDetails(financier, savedFinancier);
        }
    }

    private void notifyExistingFinancier(Financier financier, InvestmentVehicle investmentVehicle) throws MeedlException {
        log.info("Started in app notification for invite financier");
        MeedlNotification meedlNotification = MeedlNotification.builder()
                .user(financier.getIndividual())
                .timestamp(LocalDateTime.now())
                .contentId(investmentVehicle.getId())
                //to update
                .senderMail(financier.getIndividual().getEmail())
                .senderFullName(financier.getIndividual().getFirstName())
                .title("Added to "+ investmentVehicle.getName()+" investment vehicle")
                .build();
        meedlNotificationOutputPort.save(meedlNotification);
    }

    private static Financier updateFinancierDetails(Financier financier, Financier existingFinancier) {
        existingFinancier.setInvestmentVehicleId(financier.getInvestmentVehicleId());
        existingFinancier.setInvestmentVehicleRole(financier.getInvestmentVehicleRole());
        financier = existingFinancier;
        return financier;
    }

    @Override
    public Financier viewFinancierDetail(String financierId) throws MeedlException {
        MeedlValidator.validateUUID(financierId, FinancierMessages.INVALID_FINANCIER_ID.getMessage());
        return financierOutputPort.findFinancierByFinancierId(financierId);
    }

    @Override
    public Page<Financier> viewAllFinancier(Financier financier) throws MeedlException {
        MeedlValidator.validateObjectInstance(financier, FinancierMessages.EMPTY_FINANCIER_PROVIDED.getMessage());
        return financierOutputPort.viewAllFinancier(financier);
    }
    private void addFinancierToVehicle(Financier financier, InvestmentVehicle investmentVehicle) throws MeedlException {
        Optional<InvestmentVehicleFinancier>  optionalInvestmentVehicleFinancier = investmentVehicleFinancierOutputPort.findByInvestmentVehicleIdAndFinancierId(investmentVehicle.getId(), financier.getId());
        if (optionalInvestmentVehicleFinancier.isPresent()) {
            throw new MeedlException("User previously added to this investment vehicle.");
        }
        investmentVehicleFinancierOutputPort.save(InvestmentVehicleFinancier.builder()
                .financier(financier)
                .investmentVehicle(investmentVehicle)
                .build());
        log.info("Financier {} added to investment vehicle {}.", financier.getIndividual().getEmail(), investmentVehicle.getName());

    }
    @Override
    public Page<Financier> viewAllFinancierInInvestmentVehicle(Financier financier) throws MeedlException {
        MeedlValidator.validateObjectInstance(financier, FinancierMessages.EMPTY_FINANCIER_PROVIDED.getMessage());
        MeedlValidator.validatePageSize(financier.getPageSize());
        MeedlValidator.validatePageNumber(financier.getPageNumber());
        MeedlValidator.validateUUID(financier.getInvestmentVehicleId(), InvestmentVehicleMessages.INVALID_INVESTMENT_VEHICLE_ID.getMessage());

        Pageable pageRequest = PageRequest.of(financier.getPageNumber(), financier.getPageSize());
        log.info("View all financiers in a vehicle with id {}. Page number: {}, page size: {}",financier.getInvestmentVehicleId(), financier.getPageNumber(), financier.getPageSize());
        Page<Financier> foundFinanciers = investmentVehicleFinancierOutputPort.viewAllFinancierInAnInvestmentVehicle(financier.getInvestmentVehicleId(), pageRequest);
        log.info("Found financiers in db: {}", foundFinanciers);
        return foundFinanciers;
    }
    @Override
    public List<Financier> search(String name) throws MeedlException {
        MeedlValidator.validateDataElement(name, MeedlMessages.INVALID_SEARCH_PARAMETER.getMessage());
        return financierOutputPort.search(name);
    }
    private Financier saveFinancier(Financier financier) throws MeedlException {
        UserIdentity userIdentity = financier.getIndividual();
        log.info("User {} does not exist on platform and cannot be added to investment vehicle.", userIdentity.getEmail());
        userIdentity.setCreatedBy(financier.getInvitedBy());
        userIdentity.setRole(IdentityRole.FINANCIER);
        userIdentity.setCreatedAt(LocalDateTime.now());
        userIdentity = identityManagerOutputPort.createUser(userIdentity);
        userIdentity = userIdentityOutputPort.save(userIdentity);
        financier.setIndividual(userIdentity);
        return financierOutputPort.saveFinancier(financier);
    }
}
