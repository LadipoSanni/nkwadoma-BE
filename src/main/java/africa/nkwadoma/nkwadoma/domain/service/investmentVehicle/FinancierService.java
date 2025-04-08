package africa.nkwadoma.nkwadoma.domain.service.investmentVehicle;

import africa.nkwadoma.nkwadoma.application.ports.input.email.FinancierEmailUseCase;
import africa.nkwadoma.nkwadoma.application.ports.input.identity.NextOfKinUseCase;
import africa.nkwadoma.nkwadoma.application.ports.input.investmentVehicle.FinancierUseCase;
import africa.nkwadoma.nkwadoma.application.ports.input.meedlNotification.MeedlNotificationUsecase;
import africa.nkwadoma.nkwadoma.application.ports.output.bankDetail.BankDetailOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.IdentityManagerOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.UserIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.investmentVehicle.CooperationOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.investmentVehicle.FinancierOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.investmentVehicle.InvestmentVehicleFinancierOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.investmentVehicle.InvestmentVehicleOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.meedlNotification.AsynchronousMailingOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.AccreditationStatus;
import africa.nkwadoma.nkwadoma.domain.enums.ActivationStatus;
import africa.nkwadoma.nkwadoma.domain.enums.IdentityRole;
import africa.nkwadoma.nkwadoma.domain.enums.constants.InvestmentVehicleMessages;
import africa.nkwadoma.nkwadoma.domain.enums.constants.MeedlMessages;
import africa.nkwadoma.nkwadoma.domain.enums.constants.investmentVehicle.FinancierMessages;
import africa.nkwadoma.nkwadoma.domain.enums.investmentVehicle.FinancierType;
import africa.nkwadoma.nkwadoma.domain.enums.investmentVehicle.InvestmentVehicleVisibility;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.MeedlNotification;
import africa.nkwadoma.nkwadoma.domain.model.bankDetail.BankDetail;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import africa.nkwadoma.nkwadoma.domain.model.investmentVehicle.Cooperation;
import africa.nkwadoma.nkwadoma.domain.model.investmentVehicle.Financier;
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
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
    private final MeedlNotificationUsecase meedlNotificationUsecase;
    private final FinancierEmailUseCase financierEmailUseCase;
    private final NextOfKinUseCase nextOfKinUseCase;
    private final BankDetailOutputPort bankDetailOutputPort;
    private final CooperationOutputPort cooperationOutputPort;
    private final AsynchronousMailingOutputPort asynchronousMailingOutputPort;
    private List<Financier> financiersToMail;

    @Override
    public String inviteFinancier(List<Financier> financiers, String investmentVehicleId) throws MeedlException {
        financiersToMail = new ArrayList<>();
        InvestmentVehicle investmentVehicle = null;
        MeedlValidator.validateCollection(financiers, FinancierMessages.EMPTY_FINANCIER_PROVIDED.getMessage());
        investmentVehicle = fetchInvestmentVehicleIfProvided(investmentVehicleId, investmentVehicle);
        String response = null;
        if (financiers.size() == 1) {
            response =  inviteSingleFinancier(financiers.get(0), investmentVehicle);
        }else {
            response = inviteMultipleFinancier(financiers, investmentVehicle);
        }
        asynchronousMailingOutputPort.sendFinancierEmail(financiersToMail, investmentVehicle);
        return response;
    }

    private String inviteMultipleFinancier(List<Financier> financiers, InvestmentVehicle investmentVehicle) {
        financiers
                .forEach(financier -> {
                    try {
                        MeedlValidator.validateObjectInstance(financier, FinancierMessages.EMPTY_FINANCIER_PROVIDED.getMessage());
                        confirmFinancierHasType(financier);
                        inviteFinancier(financier, investmentVehicle);
                    } catch (MeedlException e) {
                        log.error("financier details {}", financier ,e);
                        //TODO notify financier on failure
                        throw new RuntimeException(e);
                    }
                });
        return getMessageForMultipleFinanciers(investmentVehicle);
    }

    private String inviteSingleFinancier(Financier financier, InvestmentVehicle investmentVehicle) throws MeedlException {
        MeedlValidator.validateObjectInstance(financier, FinancierMessages.EMPTY_FINANCIER_PROVIDED.getMessage());
        try{
            confirmFinancierHasType(financier);
            inviteFinancier(financier, investmentVehicle);
        }catch (MeedlException e){
            log.error("financier details {}", financier ,e);
            //TODO notify financier on failure
            throw new MeedlException(e);
        }
        return getMessageForSingleFinancier(investmentVehicle);
    }

    private InvestmentVehicle fetchInvestmentVehicleIfProvided(String investmentVehicleId, InvestmentVehicle investmentVehicle) throws MeedlException {
        if (StringUtils.isNotEmpty(investmentVehicleId) && StringUtils.isNotBlank(investmentVehicleId)){
            MeedlValidator.validateUUID(investmentVehicleId, InvestmentVehicleMessages.INVALID_INVESTMENT_VEHICLE_ID.getMessage());
            log.info("Fetching investment vehicle with id {}", investmentVehicleId);
            investmentVehicle = investmentVehicleOutputPort.findById(investmentVehicleId);
            log.info("Investment vehicle found with id {}", investmentVehicle.getId());
        }
        log.info("is the vehicle id presence : {}", StringUtils.isNotEmpty(investmentVehicleId) || StringUtils.isNotBlank(investmentVehicleId));
        return investmentVehicle;
    }


    private static String getMessageForSingleFinancier(InvestmentVehicle investmentVehicle) {
        if (ObjectUtils.isEmpty(investmentVehicle)){
            return "Financier have been invited to the platform";
        }else {
            return "Financier has been added to investment vehicle";
        }
    }
    private static String getMessageForMultipleFinanciers(InvestmentVehicle investmentVehicle) {
        if (ObjectUtils.isEmpty(investmentVehicle)){
            return "Financier(s) has been added to investment vehicle";
        }else {
            return "Financier(s) have been invited to the platform";
        }
    }

    private void inviteFinancier(Financier financier, InvestmentVehicle investmentVehicle) throws MeedlException {
        if (ObjectUtils.isEmpty(investmentVehicle)){
            log.info("Financier invited without an investment vehicle id, therefore financier is invited to the platform.");
            inviteFinancierToPlatform(financier);
        }else {
            log.info("Inviting financier into an investment vehicle with id {}", financier.getInvestmentVehicleId());
            inviteFinancierToInvestmentVehicle(financier, investmentVehicle);
        }
    }

    private static void confirmFinancierHasType(Financier financier) throws MeedlException {
        log.info("Financier type before saving is {}", financier.getFinancierType());
        if(financier.getFinancierType() == null && MeedlValidator.isNotValidId(financier.getId())){
            log.error("Financier does not have a valid type when inviting");
            //TODO Notify admin on failure to invite financier
            throw new MeedlException(FinancierMessages.INVALID_FINANCIER_TYPE.getMessage());
        }
        log.info("Financier either has a type or has an id {} {}", financier.getFinancierType(), financier.getId());
    }

    private void inviteFinancierToPlatform(Financier financier) throws MeedlException {
        financier.validate();
        if (financier.getFinancierType() == FinancierType.INDIVIDUAL) {
            inviteIndividualFinancierToPlatform(financier);
        }else {
            inviteCooperateFinancierToPlatform(financier);
        }
    }

    private Financier inviteCooperateFinancierToPlatform(Financier financier) throws MeedlException {
        log.info("Financier invited into the platform before getCooperateFinancierByUserIdentity is called.");
        try {
            financier = getCooperateFinancierByUserIdentity(financier);
            log.info("cooperate financier found on the platform ");
        } catch (MeedlException e) {
            log.warn("Failed to find user on application. Financier not yet onboarded.");
            log.info("Inviting a new financier to the platform {} ",e.getMessage());
            financier = saveNonExistingCooperateFinancier(financier);
            financiersToMail.add(financier);
            log.info("Financier with email {} added for email sending.", financier.getUserIdentity().getEmail());
        }
        return financier;
    }

    private Financier saveNonExistingCooperateFinancier(Financier financier) throws MeedlException {
        log.info("Saving cooperate financier user identity to platform {} ",financier);
        UserIdentity userIdentity = identityManagerOutputPort.createUser(financier.getUserIdentity());
        userIdentity = userIdentityOutputPort.save(userIdentity);
        financier.setUserIdentity(userIdentity);
        Cooperation cooperation = cooperationOutputPort.save(financier.getCooperation());
        financier.setCooperation(cooperation);
        financier = financierOutputPort.save(financier);
        return financier;
    }

    private Financier getCooperateFinancierByUserIdentity(Financier financier) throws MeedlException {
        UserIdentity userIdentity = findFinancierUserIdentityByEmail(financier.getUserIdentity().getEmail());
        try {
            Financier existingFinancier = financierOutputPort.findFinancierByUserId(userIdentity.getId());
            log.info("Financier found on the platform you the user id: {} email is {}", existingFinancier.getId(), existingFinancier.getUserIdentity().getEmail());
        }catch (MeedlException e){
            log.warn("User is not previously a financier but exists on the platform");
            log.info("Creating a new cooperation financier for user with this email : {}", userIdentity.getEmail());
            Cooperation cooperation = cooperationOutputPort.save(financier.getCooperation());
            financier.setCooperation(cooperation);
            financier.setUserIdentity(userIdentity);
            Financier savedFinancier = financierOutputPort.save(financier);
            log.info("Cooperate financier saved successfully");
            log.info("User previously existing has now been made a financier");
            notifyExistingFinancier(financier);
        }
        return financier;
    }

    private void inviteIndividualFinancierToPlatform(Financier financier){
        try {
            financier = getFinancierByUserIdentity(financier);
        } catch (MeedlException e) {
            financier = saveNonExistingFinancier(financier, e.getMessage());
            financiersToMail.add(financier);
            log.info("Financier with email {} added for email sending.", financier.getUserIdentity().getEmail());
        }
    }
    private void inviteFinancierToInvestmentVehicle(Financier financier, InvestmentVehicle investmentVehicle) throws MeedlException {
        financier.validate();
        financier.validateFinancierDesignation();
        addFinancierToInvestmentVehicle(financier, investmentVehicle);
    }


    private void addFinancierToInvestmentVehicle(Financier financier, InvestmentVehicle investmentVehicle) throws MeedlException {
        if (MeedlValidator.isValidId(financier.getId())){
            financier = financierOutputPort.findFinancierByFinancierId(financier.getId());
        }else {
            try {
                financier = getFinancierByUserIdentity(financier);
            } catch (MeedlException e) {
                financier = saveNonExistingFinancier(financier, e.getMessage());
            }
        }
        addAndNotifyFinancier(financier, investmentVehicle);
    }

    private void addAndNotifyFinancier(Financier financier, InvestmentVehicle investmentVehicle) throws MeedlException {
        Optional<InvestmentVehicleFinancier> optionalInvestmentVehicleFinancier = investmentVehicleFinancierOutputPort.findByInvestmentVehicleIdAndFinancierId(investmentVehicle.getId(), financier.getId());
        if (optionalInvestmentVehicleFinancier.isEmpty()) {
            InvestmentVehicleFinancier investmentVehicleFinancier =  assignDesignation(financier, investmentVehicle);
            investmentVehicleFinancierOutputPort.save(investmentVehicleFinancier);
            notifyExistingFinancier(financier, investmentVehicle);
            log.info("Financier {} added to investment vehicle {}.", financier.getUserIdentity().getEmail(), investmentVehicle.getName());
        }{
            log.warn("Attempted to add financier with email {} to the same investment vehicle twice with name {} and id {}", financier.getUserIdentity().getEmail(), investmentVehicle.getName(), investmentVehicle.getId());
            //TODO notify the admin that the financier has been added to the investment vehicle previously.
        }
    }
    private Financier saveNonExistingFinancier(Financier financier, String message) {
        log.warn("Failed to find user on application. Financier not yet onboarded.");
        log.info("Inviting a new financier to the platform {} ",message);
        log.warn("Started saving non existing financier {}", financier.getUserIdentity().getEmail());
        Financier savedFinancier;
        try {
            savedFinancier = saveFinancier(financier);
            log.info("Saved non-existing financier with email : {}", savedFinancier.getUserIdentity().getEmail());
            financier = updateFinancierDetails(financier, savedFinancier);
        } catch (MeedlException ex) {
            log.error("",ex);
            throw new RuntimeException(ex);
        }
        financiersToMail.add(financier);
        return financier;
    }

    private Financier getFinancierByUserIdentity(Financier financier) throws MeedlException {

        UserIdentity userIdentity = findFinancierUserIdentityByEmail(financier.getUserIdentity().getEmail());
//        UserIdentity userIdentity = userIdentityOutputPort.findByEmail(financier.getUserIdentity().getEmail());
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
            log.info("Individual financier saved successfully");
            log.info("User previously existing has now been made a financier");
            notifyExistingFinancier(financier);
            return updateFinancierDetails(financier, savedFinancier);
        }
    }

    private UserIdentity findFinancierUserIdentityByEmail(String financierEmail) throws MeedlException {
        UserIdentity userIdentity = userIdentityOutputPort.findByEmail(financierEmail);
        log.info("User identity found by email {} ,when inviting financier ", userIdentity.getEmail());
        if (userIdentity.getRole() != IdentityRole.FINANCIER) {
            //TODO Add new role to user.
        }
        return userIdentity;
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
    public Page<Financier> viewAllFinancier(Financier financier) throws MeedlException {
        MeedlValidator.validateObjectInstance(financier, FinancierMessages.EMPTY_FINANCIER_PROVIDED.getMessage());
        return financierOutputPort.viewAllFinancier(financier);
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

    @Override
    public Financier investInVehicle(Financier financier) throws MeedlException {
        MeedlValidator.validateObjectInstance(financier, FinancierMessages.EMPTY_FINANCIER_PROVIDED.getMessage());
        Financier foundFinancier = financierOutputPort.findFinancierByUserId(financier.getUserIdentity().getId());
        financier.setId(foundFinancier.getId());
        InvestmentVehicle foundInvestmentVehicle = investmentVehicleOutputPort.findById(financier.getInvestmentVehicleId());
        InvestmentVehicle investmentVehicle = null;
        InvestmentVehicleFinancier investmentVehicleFinancier = null;
        log.info("Investment vehicle found is {}" ,foundInvestmentVehicle.getInvestmentVehicleVisibility());
        if (foundInvestmentVehicle.getInvestmentVehicleVisibility().equals(InvestmentVehicleVisibility.PUBLIC)) {
            log.info("Initiating investment into a public vehicle");
            investInPublicVehicle(financier, foundFinancier, foundInvestmentVehicle);
            investmentVehicle = foundInvestmentVehicle;
        } else {
            investmentVehicleFinancier = investmentVehicleFinancierOutputPort.findByInvestmentVehicleIdAndFinancierId(financier.getInvestmentVehicleId(), financier.getId())
                    .orElseThrow(() -> new MeedlException("You will needs to be part of the investment vehicle you want to finance "));
            investmentVehicle = investmentVehicleFinancier.getInvestmentVehicle();
            updateInvestmentVehicleAvailableAmount(financier, investmentVehicle);
            updateInvestmentVehicleFinancierAmount(investmentVehicleFinancier, financier);
            log.info("Updated the investment vehicle available amount for {}", investmentVehicle);
            log.info("New amount after adding to the investment vehicle... {}", investmentVehicle.getTotalAvailableAmount());
        }

        investmentVehicleOutputPort.save(investmentVehicle);

        return financier;
    }

    private InvestmentVehicleFinancier investInPublicVehicle(Financier financier, Financier foundFinancier, InvestmentVehicle investmentVehicle) throws MeedlException {
        if (foundFinancier.getActivationStatus().equals(ActivationStatus.ACTIVE)) {
            log.info("User is active {}", foundFinancier.getActivationStatus());
            updateInvestmentVehicleAvailableAmount(financier, investmentVehicle);
            return updateInvestmentVehicleFinancierAmountInvested(investmentVehicle, financier);
        }else {
            log.error("Financier is not active. Financier status is {}", foundFinancier.getActivationStatus());
            throw new MeedlException("Financier is not active on the platform");
        }
    }

    private InvestmentVehicleFinancier updateInvestmentVehicleFinancierAmountInvested(InvestmentVehicle investmentVehicle, Financier financier) throws MeedlException {
        InvestmentVehicleFinancier investmentVehicleFinancier;
        Optional<InvestmentVehicleFinancier> optionalInvestmentVehicleFinancier = investmentVehicleFinancierOutputPort
                .findByInvestmentVehicleIdAndFinancierId(investmentVehicle.getId(), financier.getId());
        log.info("Updating investment vehicle financier ");
        if (optionalInvestmentVehicleFinancier.isPresent()) {
            investmentVehicleFinancier = optionalInvestmentVehicleFinancier.get();
            BigDecimal newAmount = investmentVehicleFinancier.getAmountInvested().add(financier.getAmountToInvest());
            investmentVehicleFinancier.setAmountInvested(newAmount);
            log.info("Updated the amount invested in the investment vehicle financier for {}... amount invested {}", newAmount, financier.getAmountToInvest());
        }else {
            log.info("First time financier is in vesting in this vehicle. Amount {}", financier.getAmountToInvest());
            investmentVehicleFinancier = InvestmentVehicleFinancier.builder()
                    .investmentVehicle(investmentVehicle)
                    .financier(financier)
                    .amountInvested(financier.getAmountToInvest())
                    .build();
        }
        return investmentVehicleFinancierOutputPort.save(investmentVehicleFinancier);
    }

    private void updateInvestmentVehicleFinancierAmount(InvestmentVehicleFinancier investmentVehicleFinancier, Financier financier) throws MeedlException {
        if (investmentVehicleFinancier.getAmountInvested() == null) {
            log.info("Investment vehicle financier amount invested is null. Changing it to zero.");
            investmentVehicleFinancier.setAmountInvested(BigDecimal.ZERO);
        }
        BigDecimal currentAmount = investmentVehicleFinancier.getAmountInvested();
        BigDecimal newAmount = currentAmount.add(financier.getAmountToInvest());
        investmentVehicleFinancier.setAmountInvested(newAmount);
        log.info("Updating investment vehicle financier amount invested to {}",investmentVehicleFinancier.getAmountInvested());
        investmentVehicleFinancierOutputPort.save(investmentVehicleFinancier);

    }

    private static BigDecimal updateInvestmentVehicleAvailableAmount(Financier financier, InvestmentVehicle investmentVehicle) {
        if (investmentVehicle.getTotalAvailableAmount() == null) {
            log.info("Investment vehicle have no total available amount. Setting it up to zero. {}", investmentVehicle.getId());
            investmentVehicle.setTotalAvailableAmount(BigDecimal.ZERO);
        }
        BigDecimal currentAmount = investmentVehicle.getTotalAvailableAmount();
        BigDecimal newAmount = currentAmount.add(financier.getAmountToInvest());
        investmentVehicle.setTotalAvailableAmount(newAmount);
        log.info("Total amount available for this investment vehicle has been updated. {}", newAmount);
        return newAmount;
    }

    @Override
    @Transactional
    public Financier completeKyc(Financier financier) throws MeedlException {
        MeedlValidator.validateObjectInstance(financier, "Kyc request cannot be empty");
        financier.validateKyc();
        Financier foundFinancier = financierOutputPort.findFinancierByUserId(financier.getUserIdentity().getId());
        if (foundFinancier.getUserIdentity().getBankDetail() == null){
            log.info("Financier details in service to use in completing kyc {}", financier);
            log.info("Bank details in financier service to use in completing kyc {}", financier.getUserIdentity().getBankDetail());
            BankDetail bankDetail = bankDetailOutputPort.save(financier.getUserIdentity().getBankDetail());
            log.info("Bank details in financier service after been saved in bank detail adapter. {}", bankDetail);
            mapKycFinancierUpdatedValues(financier, foundFinancier, bankDetail);

            userIdentityOutputPort.save(foundFinancier.getUserIdentity());
            log.info("updated user details for kyc");
            return financierOutputPort.completeKyc(financier);
        }else {
            log.info("Financier {} has already completed kyc.", foundFinancier);
            throw new MeedlException("Kyc already done.");
        }
    }

    private static void mapKycFinancierUpdatedValues(Financier financier, Financier foundFinancier, BankDetail bankDetail) {
        UserIdentity userIdentity = foundFinancier.getUserIdentity();
        log.info("updating user details in kyc {}", userIdentity.getId());

        userIdentity.setNin(financier.getUserIdentity().getNin());
        userIdentity.setTaxId(financier.getUserIdentity().getTaxId());
        userIdentity.setBvn(financier.getUserIdentity().getBvn());
        userIdentity.setBankDetail(bankDetail);
        userIdentity.setPhoneNumber(financier.getUserIdentity().getPhoneNumber());

        foundFinancier.setUserIdentity(userIdentity);

        financier.setFinancierType(foundFinancier.getFinancierType());
        financier.setUserIdentity(userIdentity);
        financier.setCooperation(foundFinancier.getCooperation());
        financier.setId(foundFinancier.getId());

    }

    @Override
    public Financier findFinancierByCooperationId(String cooperationId) throws MeedlException {
        return null;
    }

    private Financier saveFinancier(Financier financier) throws MeedlException {
        if (financier.getFinancierType() == FinancierType.INDIVIDUAL) {
            financier.setActivationStatus(ActivationStatus.INVITED);
            UserIdentity userIdentity = financier.getUserIdentity();
            financier.setAccreditationStatus(AccreditationStatus.UNVERIFIED);
            log.info("User {} does not exist on platform and cannot be added to investment vehicle.", userIdentity.getEmail());
            userIdentity.setRole(IdentityRole.FINANCIER);
            userIdentity.setCreatedAt(LocalDateTime.now());
            userIdentity = identityManagerOutputPort.createUser(userIdentity);
            userIdentity = userIdentityOutputPort.save(userIdentity);
            financier.setUserIdentity(userIdentity);
            return financierOutputPort.save(financier);
        }else {
            return inviteCooperateFinancierToPlatform(financier);
        }
    }
}
