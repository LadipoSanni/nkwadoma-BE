package africa.nkwadoma.nkwadoma.domain.service.investmentVehicle;

import africa.nkwadoma.nkwadoma.application.ports.input.email.FinancierEmailUseCase;
import africa.nkwadoma.nkwadoma.application.ports.input.investmentVehicle.FinancierUseCase;
import africa.nkwadoma.nkwadoma.application.ports.input.meedlNotification.MeedlNotificationUsecase;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.IdentityManagerOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.UserIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.investmentVehicle.FinancierOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.investmentVehicle.InvestmentVehicleFinancierOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.investmentVehicle.InvestmentVehicleOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.ActivationStatus;
import africa.nkwadoma.nkwadoma.domain.enums.IdentityRole;
import africa.nkwadoma.nkwadoma.domain.enums.constants.InvestmentVehicleMessages;
import africa.nkwadoma.nkwadoma.domain.enums.constants.MeedlMessages;
import africa.nkwadoma.nkwadoma.domain.enums.constants.investmentVehicle.FinancierMessages;
import africa.nkwadoma.nkwadoma.domain.enums.investmentVehicle.FinancierType;
import africa.nkwadoma.nkwadoma.domain.enums.investmentVehicle.InvestmentVehicleDesignation;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.MeedlNotification;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import africa.nkwadoma.nkwadoma.domain.model.investmentVehicle.Financier;
import africa.nkwadoma.nkwadoma.domain.model.investmentVehicle.FinancierDetails;
import africa.nkwadoma.nkwadoma.domain.model.investmentVehicle.InvestmentVehicle;
import africa.nkwadoma.nkwadoma.domain.model.investmentVehicle.InvestmentVehicleFinancier;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.time.LocalDateTime;
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
    private final MeedlNotificationUsecase meedlNotificationUsecase;
    private final FinancierEmailUseCase FinancierEmailUseCase;


    @Override
    public String inviteFinancier(Financier financier) throws MeedlException {
        MeedlValidator.validateObjectInstance(financier, FinancierMessages.EMPTY_FINANCIER_PROVIDED.getMessage());
        if (StringUtils.isEmpty(financier.getInvestmentVehicleId())){
            return inviteFinancierToPlatform(financier);
        }
        return inviteFinancierToInvestmentVehicle(financier);
    }

    private String inviteFinancierToPlatform(Financier financier) throws MeedlException {
        financier.validate();
        if (financier.getFinancierType() == FinancierType.INDIVIDUAL) {
            inviteIndividualFinancierToPlatform(financier);
        }else {
            inviteCooperateFinancierToPlatform(financier);
        }
        return "Financier has been invited to the platform";
    }

    private void inviteCooperateFinancierToPlatform(Financier financier) {
        //TODO invite cooperation to platform
    }

    private void inviteIndividualFinancierToPlatform(Financier financier) throws MeedlException {
        try {
            financier = getFinancierByUserIdentity(financier);
        } catch (MeedlException e) {
            log.warn("Failed to find user on application. Financier not yet onboarded.");
            log.info("Inviting a new financier to the platform {} ",e.getMessage());
            financier = saveNonExistingFinancier(financier);
            emailInviteNonExistingFinancierToPlatform(financier);
        }
    }

    private void emailInviteNonExistingFinancierToPlatform(Financier financier) throws MeedlException {
        FinancierEmailUseCase.inviteFinancierToPlatform(financier.getUserIdentity());
    }

    private String inviteFinancierToInvestmentVehicle(Financier financier) throws MeedlException {
        financier.validate();
        validateFinancierDesignation(financier);
        MeedlValidator.validateUUID(financier.getInvestmentVehicleId(), InvestmentVehicleMessages.INVALID_INVESTMENT_VEHICLE_ID.getMessage());
        if (financier.getFinancierType() == FinancierType.INDIVIDUAL){
            inviteIndividualFinancierToInvestmentVehicle(financier);
        }else {
            inviteCooperateFinancierToInvestmentVehicle(financier);
        }
        return "Financier added to investment vehicle";
    }

    private void inviteCooperateFinancierToInvestmentVehicle(Financier financier) {
        //TODO invite cooperation to vehicle
    }

    private void inviteIndividualFinancierToInvestmentVehicle(Financier financier) throws MeedlException {
        InvestmentVehicle investmentVehicle = investmentVehicleOutputPort.findById(financier.getInvestmentVehicleId());
        try {
            financier = getFinancierByUserIdentity(financier);
            Optional<InvestmentVehicleFinancier> optionalInvestmentVehicleFinancier = addFinancierToVehicle(financier, investmentVehicle);
            if (optionalInvestmentVehicleFinancier.isEmpty()) {
                notifyExistingFinancier(financier, investmentVehicle);
            }
        } catch (MeedlException e) {
            log.warn("Failed to find user on application. Financier not yet onboarded.");
            log.info("Inviting a new financier to the platform {} ",e.getMessage());
            financier = saveNonExistingFinancier(financier);
            Optional<InvestmentVehicleFinancier> optionalInvestmentVehicleFinancier = addFinancierToVehicle(financier, investmentVehicle);
            if (optionalInvestmentVehicleFinancier.isEmpty()) {
                emailInviteNonExistingFinancierToVehicle(financier, investmentVehicle);
            }
        }
    }

    private static void validateFinancierDesignation(Financier financier) throws MeedlException {
        MeedlValidator.validateObjectInstance(financier.getInvestmentVehicleDesignation(), FinancierMessages.FINANCIER_DESIGNATION_REQUIRED.getMessage());
        if (financier.getInvestmentVehicleDesignation().isEmpty()){
            throw new MeedlException(FinancierMessages.FINANCIER_DESIGNATION_REQUIRED.getMessage());
        }
        if (financier.getInvestmentVehicleDesignation().contains(InvestmentVehicleDesignation.DONOR) ||
                financier.getInvestmentVehicleDesignation().contains(InvestmentVehicleDesignation.ENDOWER) ||
                financier.getInvestmentVehicleDesignation().contains(InvestmentVehicleDesignation.INVESTOR) &&
                financier.getInvestmentVehicleDesignation().size() > BigInteger.ONE.intValue()
        ){
            throw new MeedlException("Financier can only be a signed a single role.");
        }

    }

    private void emailInviteNonExistingFinancierToVehicle(Financier financier, InvestmentVehicle investmentVehicle) throws MeedlException {
        FinancierEmailUseCase.inviteFinancierToVehicle(financier.getUserIdentity(), investmentVehicle);
    }

    private Financier saveNonExistingFinancier(Financier financier) {
        log.warn("Started saving non existing financier {}", financier.getUserIdentity().getEmail());
        Financier savedFinancier;
        try {
            savedFinancier = saveFinancier(financier);
            log.info("Saved non-existing financier with email : {}", savedFinancier.getId());
            financier = updateFinancierDetails(financier, savedFinancier);
        } catch (MeedlException ex) {
            throw new RuntimeException(ex);
        }
        return financier;
    }

    private Financier getFinancierByUserIdentity(Financier financier) throws MeedlException {

        UserIdentity userIdentity = userIdentityOutputPort.findByEmail(financier.getUserIdentity().getEmail());
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
            financier.setUserIdentity(userIdentity);
            Financier savedFinancier = financierOutputPort.save(financier);
            log.info("Financier saved successfully");
            log.info("User previously existing has now been made a financier");
            notifyExistingFinancier(financier);
            return updateFinancierDetails(financier, savedFinancier);
        }
    }

    private void notifyExistingFinancier(Financier financier) throws MeedlException {
        log.info("Started in app notification for existing financier");
        MeedlNotification meedlNotification = MeedlNotification.builder()
                .user(financier.getUserIdentity())
                .timestamp(LocalDateTime.now())
                .contentId(financier.getId())
                .senderMail(financier.getUserIdentity().getEmail())
                .senderFullName(financier.getUserIdentity().getFirstName())
                .title("You have now been made a financier on the platform.")
                .build();
        meedlNotificationUsecase.sendNotification(meedlNotification);
    }

    private void notifyExistingFinancier(Financier financier, InvestmentVehicle investmentVehicle) throws MeedlException {
        log.info("Started in app notification for invite financier");
        MeedlNotification meedlNotification = MeedlNotification.builder()
                .user(financier.getUserIdentity())
                .timestamp(LocalDateTime.now())
                .contentId(investmentVehicle.getId())
                .senderMail(financier.getUserIdentity().getEmail())
                .senderFullName(financier.getUserIdentity().getFirstName())
                .title("Added to "+ investmentVehicle.getName()+" investment vehicle")
                .build();
        meedlNotificationUsecase.sendNotification(meedlNotification);
    }

    private static Financier updateFinancierDetails(Financier financier, Financier existingFinancier) {
        existingFinancier.setInvestmentVehicleId(financier.getInvestmentVehicleId());
        existingFinancier.setInvestmentVehicleDesignation(financier.getInvestmentVehicleDesignation());
        financier = existingFinancier;
        return financier;
    }

    @Override
    public Financier viewFinancierDetail(String financierId) throws MeedlException {
        MeedlValidator.validateUUID(financierId, FinancierMessages.INVALID_FINANCIER_ID.getMessage());
        return financierOutputPort.findFinancierByFinancierId(financierId);
    }
    @Override
    public FinancierDetails viewFinancierDetailByFinancierId(String financierId) throws MeedlException {
        MeedlValidator.validateUUID(financierId, FinancierMessages.INVALID_FINANCIER_ID.getMessage());
        return financierOutputPort.findFinancierDetailsByFinancierId(financierId);
    }

    @Override
    public Page<Financier> viewAllFinancier(Financier financier) throws MeedlException {
        MeedlValidator.validateObjectInstance(financier, FinancierMessages.EMPTY_FINANCIER_PROVIDED.getMessage());
        return financierOutputPort.viewAllFinancier(financier);
    }
    private Optional<InvestmentVehicleFinancier> addFinancierToVehicle(Financier financier, InvestmentVehicle investmentVehicle) throws MeedlException {
        Optional<InvestmentVehicleFinancier> optionalInvestmentVehicleFinancier = investmentVehicleFinancierOutputPort.findByInvestmentVehicleIdAndFinancierId(investmentVehicle.getId(), financier.getId());
        if (optionalInvestmentVehicleFinancier.isEmpty()) {
            InvestmentVehicleFinancier investmentVehicleFinancier =  assignDesignation(financier, investmentVehicle);
            investmentVehicleFinancierOutputPort.save(investmentVehicleFinancier);
            log.info("Financier {} added to investment vehicle {}.", financier.getUserIdentity().getEmail(), investmentVehicle.getName());
        }
        return optionalInvestmentVehicleFinancier;
       }

    private InvestmentVehicleFinancier assignDesignation(Financier financier, InvestmentVehicle investmentVehicle) {
       return InvestmentVehicleFinancier.builder()
                .financier(financier)
                .investmentVehicleDesignation(financier.getInvestmentVehicleDesignation())
                .investmentVehicle(investmentVehicle)
                .build();
    }

    @Override
    public Page<Financier> viewAllFinancierInInvestmentVehicle(Financier financier) throws MeedlException {
        viewAllFinancierInVehicleValidation(financier);

        Pageable pageRequest = PageRequest.of(financier.getPageNumber(), financier.getPageSize());
        log.info("View all financiers in a vehicle with id {}. Page number: {}, page size: {}",financier.getInvestmentVehicleId(), financier.getPageNumber(), financier.getPageSize());
        Page<Financier> foundFinanciers = investmentVehicleFinancierOutputPort.viewAllFinancierInAnInvestmentVehicle(financier.getInvestmentVehicleId(), pageRequest);
        log.info("Found financiers in db: {}", foundFinanciers);
        return foundFinanciers;
    }
    @Override
    public Page<Financier> viewAllFinancierInInvestmentVehicleByActivationStatus(Financier financier) throws MeedlException {
        viewAllFinancierInVehicleValidation(financier);
        MeedlValidator.validateObjectInstance(financier.getActivationStatus(), "Please provide a valid activation status.");

        Pageable pageRequest = PageRequest.of(financier.getPageNumber(), financier.getPageSize());
        log.info("View all financiers in a vehicle with id {}. Page number: {}, page size: {} and activation status {}",
                financier.getInvestmentVehicleId(), financier.getPageNumber(), financier.getPageSize(), financier.getActivationStatus());
        Page<Financier> foundFinanciers = investmentVehicleFinancierOutputPort.viewAllFinancierInAnInvestmentVehicle(
                financier.getInvestmentVehicleId(), financier.getActivationStatus(), pageRequest);
        log.info("Found financiers with activation status {} in db: {}",financier.getActivationStatus(), foundFinanciers);
        return foundFinanciers;
    }

    private static void viewAllFinancierInVehicleValidation(Financier financier) throws MeedlException {
        MeedlValidator.validateObjectInstance(financier, FinancierMessages.EMPTY_FINANCIER_PROVIDED.getMessage());
        MeedlValidator.validatePageSize(financier.getPageSize());
        MeedlValidator.validatePageNumber(financier.getPageNumber());
        MeedlValidator.validateUUID(financier.getInvestmentVehicleId(), InvestmentVehicleMessages.INVALID_INVESTMENT_VEHICLE_ID.getMessage());
    }

    @Override
    public Page<Financier> search(String name, int pageNumber, int pageSize) throws MeedlException {
        MeedlValidator.validateDataElement(name, MeedlMessages.INVALID_SEARCH_PARAMETER.getMessage());
        MeedlValidator.validatePageSize(pageSize);
        MeedlValidator.validatePageNumber(pageNumber);
        return financierOutputPort.search(name, pageNumber, pageSize);
    }

    @Override
    public void updateFinancierStatus(Financier financier) {
        if(ObjectUtils.isNotEmpty(financier) && ObjectUtils.isNotEmpty(financier.getUserIdentity())
            && ObjectUtils.isNotEmpty(financier.getUserIdentity().getRole())
            && financier.getUserIdentity().getRole() == IdentityRole.FINANCIER){
            try {
                Financier foundFinancier = financierOutputPort.findFinancierByUserId(financier.getUserIdentity().getId());
                foundFinancier.setActivationStatus(ActivationStatus.ACTIVE);
                financierOutputPort.save(foundFinancier);
            } catch (MeedlException e) {
                log.error("Failed to find Financier when attempting to update Financier status for user with id {}", financier.getUserIdentity().getId(), e);
            }
        }
    }

    private Financier saveFinancier(Financier financier) throws MeedlException {
        financier.setActivationStatus(ActivationStatus.INVITED);
        UserIdentity userIdentity = financier.getUserIdentity();
        log.info("User {} does not exist on platform and cannot be added to investment vehicle.", userIdentity.getEmail());
        userIdentity.setCreatedBy(financier.getInvitedBy());
        userIdentity.setRole(IdentityRole.FINANCIER);
        userIdentity.setCreatedAt(LocalDateTime.now());
        userIdentity = identityManagerOutputPort.createUser(userIdentity);
        userIdentity = userIdentityOutputPort.save(userIdentity);
        financier.setUserIdentity(userIdentity);
        return financierOutputPort.save(financier);
    }
}
