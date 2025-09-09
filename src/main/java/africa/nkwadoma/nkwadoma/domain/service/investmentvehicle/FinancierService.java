package africa.nkwadoma.nkwadoma.domain.service.investmentvehicle;

import africa.nkwadoma.nkwadoma.application.ports.output.identity.OrganizationEmployeeIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.OrganizationIdentityOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.identity.OrganizationType;
import africa.nkwadoma.nkwadoma.domain.exceptions.IdentityException;
import africa.nkwadoma.nkwadoma.application.ports.input.investmentvehicle.FinancierUseCase;
import africa.nkwadoma.nkwadoma.application.ports.input.meedlnotification.MeedlNotificationUsecase;
import africa.nkwadoma.nkwadoma.application.ports.output.financier.*;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.IdentityManagerOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.UserIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.investmentvehicle.InvestmentVehicleFinancierOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.investmentvehicle.InvestmentVehicleOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.meedlportfolio.PortfolioOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.notification.email.AsynchronousMailingOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.notification.meedlNotification.AsynchronousNotificationOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.AccreditationStatus;
import africa.nkwadoma.nkwadoma.domain.enums.identity.ActivationStatus;
import africa.nkwadoma.nkwadoma.domain.enums.identity.IdentityRole;
import africa.nkwadoma.nkwadoma.domain.enums.NotificationFlag;
import africa.nkwadoma.nkwadoma.domain.enums.constants.*;
import africa.nkwadoma.nkwadoma.domain.enums.constants.identity.IdentityMessages;
import africa.nkwadoma.nkwadoma.domain.enums.constants.InvestmentVehicleMessages;
import africa.nkwadoma.nkwadoma.domain.enums.constants.MeedlMessages;
import africa.nkwadoma.nkwadoma.domain.enums.constants.identity.UserMessages;
import africa.nkwadoma.nkwadoma.domain.enums.constants.investmentVehicle.FinancierMessages;
import africa.nkwadoma.nkwadoma.domain.enums.investmentvehicle.FinancierType;
import africa.nkwadoma.nkwadoma.domain.enums.investmentvehicle.InvestmentVehicleVisibility;
import africa.nkwadoma.nkwadoma.domain.exceptions.InvestmentException;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.financier.*;
import africa.nkwadoma.nkwadoma.domain.model.identity.OrganizationEmployeeIdentity;
import africa.nkwadoma.nkwadoma.domain.model.identity.OrganizationIdentity;
import africa.nkwadoma.nkwadoma.domain.model.meedlPortfolio.Portfolio;
import africa.nkwadoma.nkwadoma.domain.model.notification.MeedlNotification;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import africa.nkwadoma.nkwadoma.domain.model.investmentvehicle.*;
import africa.nkwadoma.nkwadoma.domain.model.investmentvehicle.InvestmentVehicle;
import africa.nkwadoma.nkwadoma.domain.model.investmentvehicle.InvestmentVehicleFinancier;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.financier.FinancierMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.investmentvehicle.InvestmentVehicleMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.organization.OrganizationEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.mapper.OrganizationIdentityMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static africa.nkwadoma.nkwadoma.domain.enums.investmentvehicle.FinancierType.COOPERATE;
import static africa.nkwadoma.nkwadoma.domain.enums.investmentvehicle.FinancierType.INDIVIDUAL;

@AllArgsConstructor
@Service
@EnableAsync
@Slf4j
public class FinancierService implements FinancierUseCase {
    private final FinancierOutputPort financierOutputPort;
    private final UserIdentityOutputPort userIdentityOutputPort;
    private final IdentityManagerOutputPort identityManagerOutputPort;
    private final InvestmentVehicleOutputPort investmentVehicleOutputPort;
    private final InvestmentVehicleFinancierOutputPort investmentVehicleFinancierOutputPort;
    private final MeedlNotificationUsecase meedlNotificationUsecase;
    private final BeneficialOwnerOutputPort beneficialOwnerOutputPort;
    private final FinancierBeneficialOwnerOutputPort financierBeneficialOwnerOutputPort;
    private final AsynchronousMailingOutputPort asynchronousMailingOutputPort;
    private final AsynchronousNotificationOutputPort asynchronousNotificationOutputPort;
    private final PoliticallyExposedPersonOutputPort politicallyExposedPersonOutputPort;
    private final FinancierPoliticallyExposedPersonOutputPort financierPoliticallyExposedPersonOutputPort;
    private final OrganizationIdentityMapper organizationIdentityMapper;
    private List<Financier> financiersToMail;
    private final InvestmentVehicleMapper investmentVehicleMapper;
    private final FinancierMapper financierMapper;
    private final PortfolioOutputPort portfolioOutputPort;
    private final OrganizationIdentityOutputPort organizationIdentityOutputPort;
    private final OrganizationEmployeeIdentityOutputPort organizationEmployeeIdentityOutputPort;

    @Override
    public String inviteFinancier(List<Financier> financiers, String investmentVehicleId) throws MeedlException {
        financiersToMail = new ArrayList<>();
        MeedlValidator.validateCollection(financiers, FinancierMessages.EMPTY_FINANCIER_PROVIDED.getMessage());
        InvestmentVehicle investmentVehicle = fetchInvestmentVehicleIfProvided(investmentVehicleId);

        UserIdentity actor = getActorPerformingAction(financiers);

        String response = null;
        if (financiers.size() == BigInteger.ONE.intValue()) {
            response =  inviteSingleFinancier(financiers.get(0), investmentVehicle,actor);
        }else {
            response = inviteMultipleFinancier(financiers, investmentVehicle,actor);
        }
        if (actor.getRole().isMeedlSuperAdmin()) {
            asynchronousMailingOutputPort.sendFinancierEmail(financiersToMail, investmentVehicle);
            asynchronousNotificationOutputPort.notifyPortfolioManagerOfNewFinancier(financiersToMail, investmentVehicle, actor);
            updateNumberOfFinancierOnPortfolio(financiers);
        }else {
            UserIdentity meedlSuperAdmin = userIdentityOutputPort.findMeedlSuperAdmin();
            asynchronousNotificationOutputPort.sendFinancierInvitationNotificationToSuperAdmin(financiersToMail,actor,meedlSuperAdmin);
        }
        return response;
    }
    @Override
    public String makePrivacyPolicyDecision(Financier financier) throws MeedlException {
        MeedlValidator.validateObjectInstance(financier, FinancierMessages.EMPTY_FINANCIER_PROVIDED.getMessage());
        MeedlValidator.validateUUID(financier.getUserIdentity().getId(), UserMessages.INVALID_USER_ID.getMessage());

        Financier foundFinancier = financierOutputPort.findFinancierByUserId(financier.getUserIdentity().getId());
        foundFinancier.setPrivacyPolicyAccepted(financier.isPrivacyPolicyAccepted());
        financierOutputPort.save(foundFinancier);
        if (financier.isPrivacyPolicyAccepted()){
            return "Privacy policy accepted";
        }
        return "Privacy policy declined";
    }

    private void updateNumberOfFinancierOnPortfolio(List<Financier> financiers) throws MeedlException {
        Portfolio portfolio = Portfolio.builder().portfolioName("Meedl").build();
        portfolio = portfolioOutputPort.findPortfolio(portfolio);
        for (Financier financier : financiers) {
            if (financier.getFinancierType() == null){
                continue;
            }
            if (financier.getFinancierType().equals(COOPERATE)){
                portfolio.setTotalNumberOfInstitutionalFinancier(portfolio.getTotalNumberOfInstitutionalFinancier() + 1);
            }else {
                portfolio.setTotalNumberOfIndividualFinancier(portfolio.getTotalNumberOfIndividualFinancier() + 1);
            }
            portfolio.setTotalNumberOfFinancier(portfolio.getTotalNumberOfFinancier() + 1);
            portfolioOutputPort.save(portfolio);
        }
    }

    private UserIdentity getActorPerformingAction(List<Financier> financiers) throws MeedlException {
        try {
            log.info("created by == {}",financiers.get(0).getUserIdentity().getCreatedBy());
            return userIdentityOutputPort.findById(financiers.get(0).getUserIdentity().getCreatedBy());
        } catch (InvestmentException e) {
            if (e.getMessage().equals(IdentityMessages.USER_NOT_FOUND.getMessage())){
                throw new InvestmentException("Actor performing this action (i.e invite financier) is unknown. Please contact admin.");
            }
            throw new InvestmentException(e.getMessage());
        }
    }

    private String inviteMultipleFinancier(List<Financier> financiers, InvestmentVehicle investmentVehicle,UserIdentity actor) throws MeedlException {
        financiers
                .forEach(financier -> {
                    try {
                        MeedlValidator.validateObjectInstance(financier, FinancierMessages.EMPTY_FINANCIER_PROVIDED.getMessage());
                        confirmFinancierHasType(financier);
                        inviteFinancier(financier, investmentVehicle,actor);
                    } catch (MeedlException e) {
                        log.error("Multiple invite flow. Financier details {}", financier ,e);
                    }
                });
        return getMessageForMultipleFinanciers(investmentVehicle);
    }

    private String inviteSingleFinancier(Financier financier, InvestmentVehicle investmentVehicle,UserIdentity actor) throws MeedlException {
        MeedlValidator.validateObjectInstance(financier, FinancierMessages.EMPTY_FINANCIER_PROVIDED.getMessage());
        try{
            confirmFinancierHasType(financier);
            inviteFinancier(financier, investmentVehicle,actor);
        }catch (MeedlException e){
            log.error("Single invite flow. financier details {}", financier ,e);
            throw new MeedlException(e.getMessage());
        }
        return getMessageForSingleFinancier(investmentVehicle);
    }

    private InvestmentVehicle fetchInvestmentVehicleIfProvided(String investmentVehicleId) throws MeedlException {
        InvestmentVehicle investmentVehicle = null;
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
            return "Financier has been invited to the platform";
        }else {
            return "Financier has been added to an investment vehicle";
        }
    }
    private static String getMessageForMultipleFinanciers(InvestmentVehicle investmentVehicle) {
        if (ObjectUtils.isEmpty(investmentVehicle)){
            return "Financiers have been added to an investment vehicle";
        }else {
            return "Financiers have been invited to the platform";
        }
    }

    private void inviteFinancier(Financier financier, InvestmentVehicle investmentVehicle,UserIdentity actor) throws MeedlException {
        if (ObjectUtils.isEmpty(investmentVehicle)){
            log.info("Financier invited without an investment vehicle id, therefore financier is invited to the platform.");
            inviteFinancierToPlatform(financier);
        }else {
            log.info("Inviting financier into an investment vehicle with id {}", investmentVehicle.getId());
            inviteFinancierToInvestmentVehicle(financier, investmentVehicle,actor);
        }
    }

    private static void confirmFinancierHasType(Financier financier) throws InvestmentException {
        log.info("Financier type before saving is {}", financier.getFinancierType());
        if(financier.getFinancierType() == null && MeedlValidator.isNotValidId(financier.getId())){
            log.error("Financier does not have a valid type when inviting");
            //TODO Notify admin on failure to invite financier
            throw new InvestmentException(FinancierMessages.INVALID_FINANCIER_TYPE.getMessage());
        }
        log.info("Financier either has a type {} or has an id {}", financier.getFinancierType(), financier.getId());
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
        log.info("Cooperate Financier invitation about to start ");
        validateInput(financier);
        validateCooperationDoesNotExist(financier,financier.getUserIdentity().getEmail());
        log.info("Done with validation for cooperate financier invite to platform");
        OrganizationIdentity organizationIdentity = OrganizationIdentity.builder().name(financier.getName())
                .email(financier.getEmail()).createdBy(financier.getUserIdentity().getCreatedBy())
                .requestedInvitationDate(LocalDateTime.now()).build();

        OrganizationIdentity savedCooperate = saveCooperation(organizationIdentity);

        log.info("Done saving cooperate organization with id {}", savedCooperate.getId());
        UserIdentity savedUserIdentity = saveUserIdentity(financier.getUserIdentity());

        UserIdentity actor = userIdentityOutputPort.findById(savedUserIdentity.getCreatedBy());

        Financier buildFinancier = buildFinancier(financier,savedCooperate.getId(),actor);

        Financier savedFinancier = financierOutputPort.save(buildFinancier);
        financier.setId(savedFinancier.getId());
        financier.setUserIdentity(savedUserIdentity);

        saveCooperateFinancier(organizationIdentity,savedUserIdentity,actor);

        savedFinancier.setUserIdentity(savedUserIdentity);
        financiersToMail.add(financier);
        log.info("Financier with email {} added for email sending. ", savedFinancier.getUserIdentity().getEmail());
        return financier;
    }


    private void saveCooperateFinancier(OrganizationIdentity organizationIdentity, UserIdentity employee,UserIdentity actor) throws MeedlException {
        OrganizationEmployeeIdentity organizationEmployeeIdentity = OrganizationEmployeeIdentity.builder()
                .meedlUser(employee)
                .organization(organizationIdentity.getId())
                .createdBy(organizationIdentity.getCreatedBy())
                .activationStatus(actor.getRole().isMeedlSuperAdmin()
                        ? ActivationStatus.INVITED
                        : ActivationStatus.PENDING_APPROVAL)
                .build();
        log.info("Saving cooperate employee representation: {}", organizationEmployeeIdentity);
        organizationEmployeeIdentityOutputPort.save(organizationEmployeeIdentity);
    }

    private Financier buildFinancier(Financier financier,String organizationId,UserIdentity actor) {
        financier.setIdentity(organizationId);
        financier.setCreatedAt(LocalDateTime.now());
        financier.setFinancierType(COOPERATE);
        financier.setAccreditationStatus(AccreditationStatus.UNVERIFIED);
        financier.setActivationStatus(actor.getRole().isMeedlSuperAdmin()
                ? ActivationStatus.INVITED
                : ActivationStatus.PENDING_APPROVAL);
        return financier;
    }

    private UserIdentity saveUserIdentity(UserIdentity userIdentity) throws MeedlException {
        log.info("Saving user identity for email: {}", userIdentity.getEmail());
        userIdentity.setRole(IdentityRole.COOPERATE_FINANCIER_SUPER_ADMIN);
        userIdentity = identityManagerOutputPort.createUser(userIdentity);
        return userIdentityOutputPort.save(userIdentity);
    }

    private OrganizationIdentity saveCooperation(OrganizationIdentity organizationIdentity) throws MeedlException {
        organizationIdentity.setOrganizationType(OrganizationType.COOPERATE);
        organizationIdentity.setId(UUID.randomUUID().toString());
        log.info("Saving new cooperation: {}", organizationIdentity.getName());
        return organizationIdentityOutputPort.save(organizationIdentity);
    }

    private void validateCooperationDoesNotExist(Financier financier,String email) throws MeedlException {
        if (organizationIdentityOutputPort.existByEmail(financier.getEmail())) {
            log.error("Cooperation financier with email already exists {}", financier.getEmail());
            throw new InvestmentException("Cooperation with email already exists");
        }
        if (ObjectUtils.isNotEmpty(organizationIdentityOutputPort.findOrganizationByName(financier.getName()))) {
            log.error("Cooperation financier with name already exists {}", financier.getName());
            throw new InvestmentException("Cooperation with name already exists");
        }
        if (userIdentityOutputPort.checkIfUserExistByEmail(email)) {
            log.error("Cooperate financier with email already exists, {}", email);
            throw new InvestmentException("User with email already exists");
        }
        if (identityManagerOutputPort.userExistByEmail(email)) {
            log.error("Cooperate financier with email already exists on keycloak {}", email);
            throw new InvestmentException("User with email already exists. Contact admin");
        }
        if (identityManagerOutputPort.clientExistByName(financier.getName())){
            log.error("Financier cooperation with name already exists on keycloak {}", financier.getName());
            throw new InvestmentException("Cooperation with name already exists");
        }
    }

    private static void validateInput(Financier financier) throws MeedlException {
        MeedlValidator.validateObjectInstance(financier,"Financier cannot be empty ");
        MeedlValidator.validateObjectInstance(financier.getUserIdentity(),"UserIdentity cannot be empty ");
        MeedlValidator.validateObjectName(financier.getName(),"Cooperate name cannot be emopty","Cooperate");
        MeedlValidator.validateEmail(financier.getEmail());
    }


    private void inviteIndividualFinancierToPlatform(Financier financier) throws InvestmentException {
        try {
            financier = getFinancierByUserIdentity(financier);
        } catch (MeedlException e) {
            financier = saveNonExistingFinancier(financier, e.getMessage());
            log.info("Financier with id {} added for email sending.", financier.getIdentity());
        }
    }
    private void inviteFinancierToInvestmentVehicle(Financier financier, InvestmentVehicle investmentVehicle,UserIdentity actor) throws MeedlException {
        financier.validate();
        financier.validateFinancierDesignation();
        log.info("Done validating financier details for a financier invite and added to a vehicle");
        addFinancierToInvestmentVehicle(financier, investmentVehicle,actor);
    }

    private void addFinancierToInvestmentVehicle(Financier financier, InvestmentVehicle investmentVehicle,UserIdentity actor) throws MeedlException {
        BigDecimal amountToInvest = financier.getAmountToInvest();
        if (MeedlValidator.isValidId(financier.getId())){
            log.info("The financier has a valid id {} therefore the financier is only being added to the vehicle", financier.getId());
            financier = financierOutputPort.findById(financier.getId());
        }else {
            try {
                financier = getFinancierByUserIdentity(financier);
            } catch (MeedlException e) {
                log.warn("",e);
                financier = saveNonExistingFinancier(financier, e.getMessage());
            }
        }
        financier.setAmountToInvest(amountToInvest);
        addAndNotifyFinancier(financier, investmentVehicle,actor);
    }

    private void addAndNotifyFinancier(Financier financier, InvestmentVehicle investmentVehicle,UserIdentity actor) throws MeedlException {
        List<InvestmentVehicleFinancier> optionalInvestmentVehicleFinancier = investmentVehicleFinancierOutputPort.findByAll(investmentVehicle.getId(), financier.getId());
        if (optionalInvestmentVehicleFinancier.isEmpty()) {
            InvestmentVehicleFinancier investmentVehicleFinancier = assignDesignation(financier, investmentVehicle);
            if (isFinancierInvesting(financier)){
                log.info("Financier with email {} is investing {}", financier.getUserIdentity().getEmail(), financier.getAmountToInvest());
                updateInvestmentVehicleFinancierAmountInvested(investmentVehicle, financier);
                updateInvestmentVehicleAvailableAmount(financier, investmentVehicle);
                notifyExistingFinancier(financier, NotificationFlag.INVESTMENT_VEHICLE, investmentVehicle,actor);
            }else {
                log.info("Financier is not investing, therefore, saving investment vehicle financier. ");
                investmentVehicleFinancierOutputPort.save(investmentVehicleFinancier);
                notifyExistingFinancier(financier, NotificationFlag.INVESTMENT_VEHICLE, investmentVehicle,actor);
            }
            log.info("Financier {} added to investment vehicle {}. Investment vehicle financier was not found. ", financier.getIdentity(), investmentVehicle.getName());
        }{
            log.warn("Attempted to add financier with email {} to the same investment vehicle twice with name {} and id {}", financier.getIdentity(), investmentVehicle.getName(), investmentVehicle.getId());
            //TODO notify the admin that the financier has been added to the investment vehicle previously.
        }
    }

    private boolean isFinancierInvesting(Financier financier) {
        log.info("Would financier invest {}: {}", (financier.getAmountToInvest() != null && financier.getAmountToInvest().compareTo(BigDecimal.ZERO) >= 0), financier.getAmountToInvest());
        return financier.getAmountToInvest() != null && financier.getAmountToInvest().compareTo(BigDecimal.ZERO) >= 0;
    }

    private Financier saveNonExistingFinancier(Financier financier, String message) throws InvestmentException {
        log.warn("Failed to find user on application. Financier not yet onboarded.");
        log.info("Inviting a new financier to the platform {} ",message);
        log.warn("Started saving non existing financier {}", financier.getUserIdentity().getEmail());
        Financier savedFinancier;
        try {
            savedFinancier = saveFinancier(financier);
            log.info("Saved non-existing financier with identity : {}", savedFinancier.getIdentity());
            financier = updateFinancierDetails(financier, savedFinancier);
        } catch (MeedlException ex) {
            log.error("Error saving none existing financier ",ex);
            throw new InvestmentException(ex.getMessage());
        }
        financiersToMail.add(financier);
        log.info("Added financier for email sending .");
        return financier;
    }

    private Financier getFinancierByUserIdentity(Financier financier) throws MeedlException {

        UserIdentity userIdentity = findFinancierUserIdentityByEmail(financier.getUserIdentity().getEmail());
        log.info("User identity found by email {} ,when inviting financier ", userIdentity.getEmail());
        if (userIdentity.getRole() != IdentityRole.FINANCIER) {
            //TODO Add new role to user.
        }
        try {
            Financier existingFinancier = financierOutputPort.findFinancierByUserId(userIdentity.getId());
            financier.getUserIdentity().setId(userIdentity.getId());
            log.info("Financier found by user identity id {}", userIdentity.getId());
            return updateFinancierDetails(financier, existingFinancier);

        }catch (MeedlException e){
            log.warn("User is not previously a financier but exists on the platform");
            log.info("Creating a new financier for user with email : {}", userIdentity.getEmail());
            financier.setUserIdentity(userIdentity);
            Financier savedFinancier = financierOutputPort.save(financier);
            log.info("Individual financier saved successfully");
            log.info("User previously existing has now been made a financier");
            notifyExistingFinancier(financier, NotificationFlag.INVITE_FINANCIER);
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



    private static Financier updateFinancierDetails(Financier financier, Financier existingFinancier) {
        existingFinancier.setInvestmentVehicleId(financier.getInvestmentVehicleId());
        existingFinancier.setInvestmentVehicleDesignation(financier.getInvestmentVehicleDesignation());
        existingFinancier.setUserIdentity(financier.getUserIdentity());
        existingFinancier.setName(financier.getName());
        financier = existingFinancier;
        log.info("Updated the financier details without saving {}", financier);
        return financier;
    }

    @Override
    public Financier viewFinancierDetail(String userId, String financierId) throws MeedlException {
        Financier financier = null;
        UserIdentity userIdentity = userIdentityOutputPort.findById(userId);
        if (userIdentity.getRole().isFinancier()) {
            log.info("User is a financier.");
            if (userIdentity.getRole().isCooperateStaff()) {
                log.info("User is a cooperate staff");
                financier = financierOutputPort.findFinancierByCooperateStaffUserId(userIdentity.getId());
            }else {
                log.info("User is an individual financier with user id  {}", userIdentity.getId());
                financier = financierOutputPort.findByIdentity(userIdentity.getId());
            }
        } else {
            log.info("User is not a financier");
            MeedlValidator.validateUUID(financierId, FinancierMessages.INVALID_FINANCIER_ID.getMessage());
            financier = financierOutputPort.findById(financierId);
            if (financier.getFinancierType().equals(FinancierType.COOPERATE)) {
                Financier cooperateFinancier = financierOutputPort.findCooperateFinancierById(financier.getId());
                financier.setCooperateAdminEmail(cooperateFinancier.getCooperateAdminEmail());
                financier.setCooperateAdminName(cooperateFinancier.getCooperateAdminName());
                log.info("cooperate super admin mail {} ==== super admin name {}",financier.getCooperateAdminEmail(),financier.getCooperateAdminName());
            }
            log.info("found financier {}", financier);
        }
        log.info("found financier before updating values : {}", financier);
        return updateFinancierDetail(financier);
    }

    private boolean isFinancier(String userId) throws MeedlException {
        IdentityRole role = userIdentityOutputPort.findById(userId).getRole();
        log.info("Is user a financier {} {}", role == IdentityRole.FINANCIER, role);
        return role == IdentityRole.FINANCIER;
    }

    private Financier updateFinancierDetail(Financier financier) throws MeedlException {
        List<InvestmentVehicleFinancier> financierInvestmentVehicle = investmentVehicleFinancierOutputPort.findAllInvestmentVehicleFinancierInvestedIn(financier.getId());

        log.info("all investment financier made ---> {}",financierInvestmentVehicle.size());

        List<InvestmentVehicle> investmentVehicles =
                investmentVehicleOutputPort.findListOfInvestmentVehicleFinancierWasAddedTo(financier.getId());

        log.info("non duplicate investment vehicle ----> {}",investmentVehicles.size());

        financier.setTotalNumberOfInvestment(financierInvestmentVehicle.size());
        financier.setInvestmentVehicles(investmentVehicles);
        List<BeneficialOwner> beneficialOwners = financierBeneficialOwnerOutputPort.findAllBeneficialOwner(financier.getId());
        financier.setBeneficialOwners(beneficialOwners);
        return financier;
    }

    private InvestmentVehicleFinancier assignDesignation(Financier financier, InvestmentVehicle investmentVehicle) {
       return InvestmentVehicleFinancier.builder()
                .financier(financier)
                .investmentVehicleDesignation(financier.getInvestmentVehicleDesignation())
                .investmentVehicle(investmentVehicle)
                .build();
    }

    @Override
    public Page<Financier> viewAllFinancier(Financier financier) throws MeedlException {
        MeedlValidator.validateObjectInstance(financier, FinancierMessages.EMPTY_FINANCIER_PROVIDED.getMessage());
        log.info("View all financiers with type {}. Page number: {}, page size: {} and activation status {}",
                financier.getFinancierType(), financier.getPageNumber(), financier.getPageSize(), financier.getActivationStatus());
        if (financier.getInvestmentVehicleId() != null){
            return viewAllFinancierInInvestmentVehicle(financier);
        }
        return financierOutputPort.viewAllFinancier(financier);
    }

    @Override
    public Page<Financier> viewAllFinancierInInvestmentVehicle(Financier financier) throws MeedlException {
        viewAllFinancierInVehicleValidation(financier);
        Pageable pageRequest = PageRequest.of(financier.getPageNumber(), financier.getPageSize());
        log.info("View all financiers in a vehicle with id {}. Page number: {}, page size: {} and activation status {}",
                financier.getInvestmentVehicleId(), financier.getPageNumber(), financier.getPageSize(), financier.getActivationStatus());
        Page<Financier> foundFinanciers = investmentVehicleFinancierOutputPort.viewAllFinancierInAnInvestmentVehicle(
                financier.getInvestmentVehicleId(), financier.getActivationStatus(), pageRequest);
        log.info("Found financiers with activation status {} in db: {}",financier.getActivationStatus(), foundFinanciers.get());
        return foundFinanciers;
    }

    private static void viewAllFinancierInVehicleValidation(Financier financier) throws MeedlException {
        MeedlValidator.validateObjectInstance(financier, FinancierMessages.EMPTY_FINANCIER_PROVIDED.getMessage());
        MeedlValidator.validatePageSize(financier.getPageSize());
        MeedlValidator.validatePageNumber(financier.getPageNumber());
        MeedlValidator.validateUUID(financier.getInvestmentVehicleId(), InvestmentVehicleMessages.INVALID_INVESTMENT_VEHICLE_ID.getMessage());
    }

    @Override
    public Page<Financier> search(String name, Financier financier) throws MeedlException {
        MeedlValidator.validateObjectInstance(financier, FinancierMessages.EMPTY_FINANCIER_PROVIDED.getMessage());
        MeedlValidator.validateDataElement(name, MeedlMessages.INVALID_SEARCH_PARAMETER.getMessage());
        MeedlValidator.validatePageSize(financier.getPageSize());
        MeedlValidator.validatePageNumber(financier.getPageNumber());
        return financierOutputPort.search(name, financier);
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
        financier.validateInvestInVehicleDetails();
        Financier foundFinancier = financierOutputPort.findFinancierByUserId(financier.getUserIdentity().getId());
        financier.setId(foundFinancier.getId());
        InvestmentVehicle foundInvestmentVehicle = investmentVehicleOutputPort.findById(financier.getInvestmentVehicleId());
        log.info("Investment vehicle found is {}" ,foundInvestmentVehicle);
        if (foundInvestmentVehicle.getInvestmentVehicleVisibility() == null){
            log.error("The investment vehicle found has a null visibility. id : {} name : {}", foundInvestmentVehicle.getId(), foundInvestmentVehicle.getName());
            throw new InvestmentException("Found vehicle visibility is not defined.");
        }
        if (foundInvestmentVehicle.getInvestmentVehicleVisibility().equals(InvestmentVehicleVisibility.PUBLIC)) {
            log.info("Initiating investment into a public vehicle");
            validateAmountToInvest(financier, foundInvestmentVehicle);
            investInPublicVehicle(financier, foundFinancier, foundInvestmentVehicle);
        } else {
            List<InvestmentVehicleFinancier> investmentVehicleFinancierList = investmentVehicleFinancierOutputPort.findByAll(financier.getInvestmentVehicleId(), financier.getId());
            if (investmentVehicleFinancierList.isEmpty()){
                throw  new InvestmentException("You will needs to be part of the investment vehicle you want to finance ");
            }
            InvestmentVehicleFinancier investmentVehicleFinancier = investmentVehicleFinancierList.get(0);
            InvestmentVehicle investmentVehicle = investmentVehicleFinancier.getInvestmentVehicle();
            validateAmountToInvest(financier, investmentVehicle);

            updateInvestmentVehicleAvailableAmount(financier, investmentVehicle);
            financier.setInvestmentVehicleDesignation(investmentVehicleFinancier.getInvestmentVehicleDesignation());
            investmentVehicleFinancier = assignDesignation(financier, investmentVehicle);
//            updateInvestmentVehicleFinancierAmount(investmentVehicleFinancier, financier);
            updateInvestmentVehicleFinancierAmountInvested(investmentVehicle, financier);

            log.info("Amount weh financier wan put ----> "+ financier.getAmountToInvest());
            updateFinancierTotalAmountInvested(financier);
            log.info("Updated the investment vehicle available amount for {}", investmentVehicle);
            log.info("New amount after adding to the investment vehicle... {}", investmentVehicle.getTotalAvailableAmount());
        }
        return financier;
    }

    private void validateAmountToInvest(Financier financier, InvestmentVehicle foundInvestmentVehicle) throws MeedlException {
        if (!(financier.getAmountToInvest().compareTo(foundInvestmentVehicle.getMinimumInvestmentAmount()) >= 0)) {
            log.error("Amount you are investing {} is below the minimum investment amount stated {}. Financier id {}", financier.getAmountToInvest(), foundInvestmentVehicle.getMinimumInvestmentAmount(),financier.getId());
            throw new InvestmentException("Amount you are investing "+ financier.getAmountToInvest() +" is below the minimum investment amount stated " + foundInvestmentVehicle.getMinimumInvestmentAmount());
        }
    }

    private InvestmentVehicleFinancier investInPublicVehicle(Financier financier, Financier foundFinancier, InvestmentVehicle investmentVehicle) throws MeedlException {
        if (isFinancierActive(foundFinancier)) {
            log.info("User is active {}", foundFinancier.getActivationStatus());
            updateInvestmentVehicleAvailableAmount(financier, investmentVehicle);
            updateFinancierTotalAmountInvested(financier);
            return updateInvestmentVehicleFinancierAmountInvested(investmentVehicle, financier);
        }else {
            log.error("Financier is not active. Financier status is {}", foundFinancier.getActivationStatus());
            throw new InvestmentException("Financier is not active on the platform");
        }
    }
    private BigDecimal updateInvestmentVehicleAvailableAmount(Financier financier, InvestmentVehicle investmentVehicle) throws MeedlException {
        if (investmentVehicle.getTotalAvailableAmount() == null) {
            log.info("Investment vehicle have no total available amount. Setting it up to zero. {}", investmentVehicle.getId());
            investmentVehicle.setTotalAvailableAmount(BigDecimal.ZERO);
        }
        BigDecimal currentAmount = investmentVehicle.getTotalAvailableAmount();
        BigDecimal newAmount = currentAmount.add(financier.getAmountToInvest());
        investmentVehicle.setTotalAvailableAmount(newAmount);
        log.info("Total amount available for this investment vehicle has been updated. {}", newAmount);
        investmentVehicleOutputPort.save(investmentVehicle);
        return newAmount;
    }

    private void updateFinancierTotalAmountInvested(Financier financier) throws MeedlException {
        log.info("Update financier total amount invested ...");
        Financier financierToBeUpdate = financierOutputPort.findByFinancierId(financier.getId());
        BigDecimal currentTotalAmountInvested = financierToBeUpdate.getTotalAmountInvested();
        if (currentTotalAmountInvested == null) {
            currentTotalAmountInvested = BigDecimal.ZERO;
        }
        BigDecimal newTotalAmountInvested = currentTotalAmountInvested.add(financier.getAmountToInvest());
        financierToBeUpdate.setTotalAmountInvested(newTotalAmountInvested);
        financierOutputPort.save(financierToBeUpdate);
    }


    private static boolean isFinancierActive(Financier foundFinancier) {
        return foundFinancier.getActivationStatus().equals(ActivationStatus.ACTIVE);
    }

    private InvestmentVehicleFinancier updateInvestmentVehicleFinancierAmountInvested(InvestmentVehicle investmentVehicle, Financier financier) throws MeedlException {
        InvestmentVehicleFinancier investmentVehicleFinancier;;
        log.info("Updating investment vehicle financier ");
        List<InvestmentVehicleFinancier> investmentVehicleFinanciers = investmentVehicleFinancierOutputPort.findByAll(investmentVehicle.getId(), financier.getId());

        if (investmentVehicleFinanciers.size() == BigInteger.ONE.intValue() &&
                (investmentVehicleFinanciers.get(0).getAmountInvested() == null ||
                investmentVehicleFinanciers.get(0).getAmountInvested().compareTo(BigDecimal.ZERO) == BigInteger.ZERO.intValue())) {

            InvestmentVehicleFinancier foundInvestmentVehicleFinancier = investmentVehicleFinanciers.get(0);
            log.info("Financier was only added to the vehicle. {}", investmentVehicle.getName());
            foundInvestmentVehicleFinancier.setAmountInvested(financier.getAmountToInvest());
            log.info("Updated the amount invested in the investment vehicle financier for {} ", foundInvestmentVehicleFinancier.getAmountInvested());
            foundInvestmentVehicleFinancier.setDateInvested(LocalDate.now());
            investmentVehicleFinancier = foundInvestmentVehicleFinancier;

        }else {
                log.info("Financier is investing in this vehicle. Amount {} role {}", financier.getAmountToInvest(), financier.getInvestmentVehicleDesignation());
                investmentVehicleFinancier = InvestmentVehicleFinancier.builder()
                        .investmentVehicle(investmentVehicle)
                        .financier(financier)
                        .investmentVehicleDesignation(financier.getInvestmentVehicleDesignation())
                        .amountInvested(financier.getAmountToInvest())
                        .dateInvested(LocalDate.now())
                        .build();
            }

        return investmentVehicleFinancierOutputPort.save(investmentVehicleFinancier);
    }

    @Override
    public Financier completeKyc(Financier financier) throws MeedlException {
        MeedlValidator.validateObjectInstance(financier, "Kyc request cannot be empty");
        MeedlValidator.validateObjectInstance(financier.getUserIdentity(), UserMessages.NULL_ACTOR_USER_IDENTITY.getMessage());
        MeedlValidator.validateObjectInstance(financier.getUserIdentity().getId(), "Identification for user performing this action is unknown.");

        Financier foundFinancier ;
        UserIdentity userIdentity = userIdentityOutputPort.findById(financier.getUserIdentity().getId());
        if (userIdentity.getRole().isCooperateStaff()){
            foundFinancier = financierOutputPort.findFinancierByCooperateStaffUserId(userIdentity.getId());
        }else {
            foundFinancier = financierOutputPort.findFinancierByUserId(userIdentity.getId());
        }
        log.info("THE found financier on kyc ---======> {}", foundFinancier);
        if (foundFinancier.getAccreditationStatus() != null &&
                foundFinancier.getAccreditationStatus().equals(AccreditationStatus.UNVERIFIED)){
            log.info("Validating for kyc financier service {}", financier);
            financier.validateKyc(foundFinancier.getFinancierType());
            validateCooperationDoesNotExistWithRcNumber(financier);
            log.info("Financier details in service to use in completing kyc {}", financier);
            mapKycFinancierUpdatedValues(financier, foundFinancier);
            if (financier.getBeneficialOwners() != null){
                log.info("Saving financier beneficial owners.");
                saveFinancierBeneficialOwners(financier);
            }
            if (financier.isPoliticallyExposed()) {
                saveFinancierPoliticallyExposedPeople(financier);
            }

            if (foundFinancier.getFinancierType().equals(INDIVIDUAL)) {
                userIdentityOutputPort.save(foundFinancier.getUserIdentity());
                identityManagerOutputPort.updateUserData(foundFinancier.getUserIdentity());
                log.info("updated user details for kyc");
            }
                Financier savedFinancier = financierOutputPort.completeKyc(financier);
                savedFinancier.setBeneficialOwners(financier.getBeneficialOwners());
                return savedFinancier;
        }else {
            log.info("Financier {} has already completed kyc.", foundFinancier);
            throw new InvestmentException("Kyc already done.");
        }
    }

    private void validateCooperationDoesNotExistWithRcNumber(Financier financier) throws MeedlException {
        Optional<OrganizationIdentity> optionalOrganizationIdentity = organizationIdentityOutputPort.findByRcNumber(financier.getRcNumber());
        if (optionalOrganizationIdentity.isPresent()){
            log.error("Organization with provided Rc number already exist on complete kyc");
            throw new MeedlException("Cooperation with provided Rc number already exist.");
        }
    }

    private void saveFinancierPoliticallyExposedPeople(Financier financier) throws MeedlException {
        List<PoliticallyExposedPerson> politicallyExposedPeople = new ArrayList<>();
        log.info("Started saving politically exposed person.");
        for (PoliticallyExposedPerson politicallyExposedPerson : financier.getPoliticallyExposedPeople()) {
            PoliticallyExposedPerson savedPoliticallyExposedPerson = politicallyExposedPersonOutputPort.save(politicallyExposedPerson);
            politicallyExposedPeople.add(savedPoliticallyExposedPerson);
            log.info("Financier saved with politically exposed person : {}", savedPoliticallyExposedPerson);
        }
        financier.setPoliticallyExposedPeople(politicallyExposedPeople);
        log.info("Saving financier politically exposed person...");
        List<FinancierPoliticallyExposedPerson> financierPoliticallyExposedPeople =
                financier.getPoliticallyExposedPeople().stream()
                        .map(politicallyExposedPerson ->
                                FinancierPoliticallyExposedPerson.builder()
                                        .politicallyExposedPerson(politicallyExposedPerson)
                                        .financier(financier)
                                        .build()
                        ).map(financierPoliticallyExposedPerson -> {
                            try {
                                return financierPoliticallyExposedPersonOutputPort.save(financierPoliticallyExposedPerson);
                            } catch (MeedlException e) {
                                throw new RuntimeException(e);
                            }
                        })
                        .toList();
        log.info("Saved... financier politically exposed people... {}",financierPoliticallyExposedPeople);
    }

    private void saveFinancierBeneficialOwners(Financier financier) throws MeedlException {
        List<BeneficialOwner> beneficialOwners = new ArrayList<>();
        log.info("Started saving beneficial owner.");
        for (BeneficialOwner beneficialOwner : financier.getBeneficialOwners()) {
            BeneficialOwner savedBeneficialOwner = beneficialOwnerOutputPort.save(beneficialOwner);
            beneficialOwners.add(savedBeneficialOwner);
            log.info("Financier saved with beneficial owner : {}", savedBeneficialOwner);
        }
        financier.setBeneficialOwners(beneficialOwners);
        log.info("Saving financier beneficial owners...");
        List<FinancierBeneficialOwner> financierBeneficialOwners =
                financier.getBeneficialOwners().stream()
                .map(beneficialOwner ->
                    FinancierBeneficialOwner.builder()
                            .beneficialOwner(beneficialOwner)
                            .financier(financier)
                            .build()
                ).map(financierBeneficialOwner -> {
                            try {
                                return financierBeneficialOwnerOutputPort.save(financierBeneficialOwner);
                            } catch (MeedlException e) {
                                throw new RuntimeException(e);
                            }
                        })
                        .toList();
        log.info("Saved... financier beneficial owners... {}",financierBeneficialOwners);

    }

    private  void mapKycFinancierUpdatedValues(Financier financier, Financier foundFinancier) throws MeedlException {
        if (foundFinancier.getFinancierType().equals(INDIVIDUAL)) {
            log.info("Individual financier mapping kyc details ");
            mapKycUserIdentityDataIndividualFinancier(financier, foundFinancier);
            mapKycFinancierPreviousData(financier, foundFinancier);
        }else {
            log.info("Cooperate financier mapping kyc details");
            OrganizationIdentity organizationIdentity = organizationIdentityOutputPort.findById(foundFinancier.getIdentity());
            organizationIdentityMapper.mapCooperateDetailToOrganization(organizationIdentity,financier);
            organizationIdentity.setOrganizationType(OrganizationType.COOPERATE);
            organizationIdentity =organizationIdentityOutputPort.save(organizationIdentity);
            financier.setOrganizationIdentity(organizationIdentity);
            financier.setId(foundFinancier.getId());
            financier.setName(foundFinancier.getName());
            financier.setFinancierType(foundFinancier.getFinancierType());
            financier.setCreatedAt(foundFinancier.getCreatedAt());
            financier.setTotalAmountInvested(financier.getTotalAmountInvested());
            financier.setActivationStatus(foundFinancier.getActivationStatus());
            financier.setIdentity(foundFinancier.getIdentity());
        }
    }

    private void mapKycFinancierPreviousData(Financier financier, Financier foundFinancier) {
        financier.setFinancierType(foundFinancier.getFinancierType());
        financier.setActivationStatus(foundFinancier.getActivationStatus());
        financier.setAmountInvested(foundFinancier.getAmountInvested());
        financier.setTotalNumberOfInvestment(foundFinancier.getTotalNumberOfInvestment());
        financier.setTotalAmountInvested(foundFinancier.getTotalAmountInvested());
        financier.setTotalIncomeEarned(foundFinancier.getTotalIncomeEarned());
        financier.setCreatedAt(foundFinancier.getCreatedAt());
        financier.setId(foundFinancier.getId());
        financier.setIdentity(foundFinancier.getIdentity());
    }

    private void mapKycUserIdentityDataIndividualFinancier(Financier financier, Financier foundFinancier) throws MeedlException {
        UserIdentity userIdentity = userIdentityOutputPort.findById(foundFinancier.getIdentity());
        log.info("updating user details in kyc service for user : {}", userIdentity);

        userIdentity.setNin(financier.getUserIdentity().getNin());
        userIdentity.setTaxId(financier.getUserIdentity().getTaxId());
        userIdentity.setBvn(financier.getUserIdentity().getBvn());
        userIdentity.setPhoneNumber(financier.getUserIdentity().getPhoneNumber());
        userIdentity.setEmailVerified(Boolean.TRUE);
        userIdentity.setEnabled(Boolean.TRUE);

        if (foundFinancier.getFinancierType() == null){
            log.error("Financier does not have type ");
            throw new MeedlException("Financier does not have type");
        } else{
            if (foundFinancier.getFinancierType() == COOPERATE){
                log.info("Found financier user for cooperation first name {} ", foundFinancier.getCooperation().getName());
                userIdentity.setFirstName(foundFinancier.getCooperation().getName());
                userIdentity.setLastName(foundFinancier.getCooperation().getName());
            }
        }

        foundFinancier.setUserIdentity(userIdentity);
        financier.setUserIdentity(userIdentity);

        log.info("Mapped user in financier {}", foundFinancier.getUserIdentity());
    }

    @Override
    public FinancierVehicleDetail viewInvestmentDetailOfFinancier(String financierId, String userId) throws MeedlException {
        Financier financier = resolveFinancier(financierId, userId);
        List<InvestmentVehicleFinancier> financierInvestmentVehicles = investmentVehicleFinancierOutputPort.findAllInvestmentVehicleFinancierInvestedIn(financier.getId());
        int numberOfInvestment = financierInvestmentVehicles.size();
        BigDecimal totalInvestmentAmount = financier.getTotalAmountInvested();
        List<InvestmentSummary> investmentSummaries = getInvestmentVehicle(financierInvestmentVehicles);

        return FinancierVehicleDetail.builder()
                .numberOfInvestment(numberOfInvestment)
                .totalAmountInvested(totalInvestmentAmount)
                .investmentSummaries(investmentSummaries)
                .portfolioValue(financier.getPortfolioValue())
                .build();
    }

    @Override
    public InvestmentSummary viewInvestmentDetailOfFinancier(String financierId, String investmentVehicleFinancierId, String userId) throws MeedlException {
        Financier financier = resolveFinancier(financierId, userId);
        InvestmentVehicleFinancier investmentVehicleFinancier =
                investmentVehicleFinancierOutputPort.findByFinancierIdAndInvestmentVehicleFinancierId(financier.getId(), investmentVehicleFinancierId);
        InvestmentVehicle investmentVehicle = investmentVehicleFinancier.getInvestmentVehicle();
        investmentVehicle.setAmountFinancierInvested(investmentVehicleFinancier.getAmountInvested());
        investmentVehicle.setDateInvested(investmentVehicleFinancier.getDateInvested());
        investmentVehicle.setDesignations(investmentVehicleFinancier.getInvestmentVehicleDesignation());
        return investmentVehicleMapper.toInvestmentSummary(investmentVehicle);
    }


    private CooperateFinancier buildCooperateFinancierIdentity(CooperateFinancier inviter, Financier newColleague) {
       CooperateFinancier newCooperateFinancier = CooperateFinancier.builder().cooperate(inviter.getCooperate())
                .financier(newColleague).build();
       newCooperateFinancier.setActivationStatus(
               inviter.getFinancier().getUserIdentity().getRole().isSuperAdmin()
                       ? ActivationStatus.INVITED
                       : ActivationStatus.PENDING_APPROVAL
       );
       return newCooperateFinancier;
    }


    private void validateRolePermissions(IdentityRole inviterRole, IdentityRole colleagueRole) throws IdentityException {
        Map<IdentityRole, Set<IdentityRole>> allowedRoles = Map.of(
                IdentityRole.COOPERATE_FINANCIER_SUPER_ADMIN,Set.of(IdentityRole.COOPERATE_FINANCIER_ADMIN),
                IdentityRole.COOPERATE_FINANCIER_ADMIN,Set.of(IdentityRole.COOPERATE_FINANCIER_ADMIN)
        );
        if (!allowedRoles.getOrDefault(inviterRole, Set.of()).contains(colleagueRole)) {
            throw new IdentityException(String.format("Role %s cannot invite colleague with role %s", inviterRole, colleagueRole));
        }
    }




    private void decisionMustEitherBeApprovedOrDeclined(ActivationStatus activationStatus) throws africa.nkwadoma.nkwadoma.domain.exceptions.IdentityException {
        if (! activationStatus.equals(ActivationStatus.APPROVED) && !activationStatus.equals(ActivationStatus.DECLINED)) {
            throw new IdentityException(OrganizationMessages.DECISION_CAN_EITHER_BE_APPROVED_OR_DECLINED.getMessage());
        }
    }

    @Override
    public Page<Financier> viewAllFinancierInvestment(String actorId, String finanacierId, int pageSize, int pageNumber) throws MeedlException {
        UserIdentity userIdentity = userIdentityOutputPort.findById(actorId);
        if (userIdentity.getRole().equals(IdentityRole.FINANCIER)){
            Page<InvestmentVehicleFinancier> investmentVehicleFinanciers =
                    investmentVehicleFinancierOutputPort.findAllInvestmentVehicleFinancierInvestedIntoByUserId(userIdentity.getId(),pageSize,pageNumber);
            return investmentVehicleFinanciers.map(financierMapper::mapToFinancierInvestment);
        }
        MeedlValidator.validateUUID(finanacierId, FinancierMessages.INVALID_FINANCIER_ID.getMessage());
        Page<InvestmentVehicleFinancier> investmentVehicleFinanciers =
                investmentVehicleFinancierOutputPort.findAllInvestmentVehicleFinancierInvestedIntoByFinancierId(finanacierId,pageSize,pageNumber);
        return investmentVehicleFinanciers.map(financierMapper::mapToFinancierInvestment);
    }

    @Override
    public Page<Financier> searchFinancierInvestment(Financier financier) throws MeedlException {
        UserIdentity userIdentity = userIdentityOutputPort.findById(financier.getActorId());
        if (userIdentity.getRole().equals(IdentityRole.FINANCIER)){
            Page<InvestmentVehicleFinancier> investmentVehicleFinanciers =
                    investmentVehicleFinancierOutputPort.searchFinancierInvestmentByInvestmentVehicleNameAndUserId
                            (financier.getInvestmentVehicleName(),userIdentity.getId(),financier.getPageSize(),financier.getPageNumber());
            return investmentVehicleFinanciers.map(financierMapper::mapToFinancierInvestment);
        }
        MeedlValidator.validateUUID(financier.getId(), FinancierMessages.INVALID_FINANCIER_ID.getMessage());
        Page<InvestmentVehicleFinancier> investmentVehicleFinanciers =
                investmentVehicleFinancierOutputPort.searchFinancierInvestmentByInvestmentVehicleNameAndFinancierId
                        (financier.getInvestmentVehicleName(),financier.getId(),financier.getPageSize(),financier.getPageNumber());
        return investmentVehicleFinanciers.map(financierMapper::mapToFinancierInvestment);
    }


    public Financier resolveFinancier(String financierId, String userId) throws MeedlException {
        Financier foundFinancier = null;
        if (isFinancier(userId)) {
            foundFinancier = financierOutputPort.findFinancierByUserId(userId);
        } else {
            MeedlValidator.validateUUID(financierId, FinancierMessages.INVALID_FINANCIER_ID.getMessage());
            foundFinancier = financierOutputPort.findById(financierId);
        }
        return foundFinancier;
    }


    private List<InvestmentSummary> getInvestmentVehicle(List<InvestmentVehicleFinancier> financierInvestmentVehicles) {
        return financierInvestmentVehicles.stream()
                .map(investmentVehicleFinancier -> {
                    InvestmentVehicle investmentVehicle = investmentVehicleFinancier.getInvestmentVehicle();
                    InvestmentSummary investmentSummary = investmentVehicleMapper.toInvestmentSummary(investmentVehicle);
                    investmentSummary.setDateInvested(investmentVehicleFinancier.getDateInvested());
                    investmentSummary.setDesignations(investmentVehicleFinancier.getInvestmentVehicleDesignation());
                    investmentSummary.setAmountInvested(investmentVehicleFinancier.getAmountInvested());
                    return investmentSummary;
                })
                .toList();
    }


    private Financier saveFinancier(Financier financier) throws MeedlException {
        financier.setActivationStatus(ActivationStatus.INVITED);
        financier.setAccreditationStatus(AccreditationStatus.UNVERIFIED);
        if (financier.getFinancierType() == FinancierType.INDIVIDUAL) {
            UserIdentity userIdentity = financier.getUserIdentity();
            log.info("User {} does not exist on platform and cannot be added to investment vehicle.", userIdentity.getEmail());
            userIdentity.setCreatedAt(LocalDateTime.now());
            userIdentity.setRole(IdentityRole.FINANCIER);
            userIdentity = identityManagerOutputPort.createUser(userIdentity);
            userIdentity = userIdentityOutputPort.save(userIdentity);
            financier.setUserIdentity(userIdentity);
            financier.setIdentity(userIdentity.getId());
            financier.setCreatedAt(LocalDateTime.now());
            financier = financierOutputPort.save(financier);
            financier.setUserIdentity(userIdentity);
            return financier;
        }else {
            return inviteCooperateFinancierToPlatform(financier);
        }
    }

    private void notifyExistingFinancier(Financier financier, NotificationFlag notificationFlag) throws MeedlException {
        log.info("Started in app notification for existing financier");
        MeedlNotification meedlNotification = MeedlNotification.builder()
                .user(financier.getUserIdentity())
                .timestamp(LocalDateTime.now())
                .contentId(financier.getId())
                .senderMail(financier.getUserIdentity().getEmail())
                .senderFullName(financier.getUserIdentity().getFirstName())
                .title("You have now been made a financier on the platform.")
                .notificationFlag(notificationFlag)
                .build();
        meedlNotificationUsecase.sendNotification(meedlNotification);
    }

    private void notifyExistingFinancier(Financier financier,  NotificationFlag notificationFlag, InvestmentVehicle investmentVehicle,UserIdentity actor) throws MeedlException {
        log.info("Started in app notification for invite financier  == {}",financier);
        UserIdentity userIdentity = userIdentityOutputPort.findById(financier.getUserIdentity().getId());
        MeedlNotification meedlNotification = MeedlNotification.builder()
                .user(userIdentity)
                .timestamp(LocalDateTime.now())
                .contentId(investmentVehicle.getId())
                .senderMail(actor.getEmail())
                .senderFullName(actor.getFirstName()+" "+actor.getLastName())
                .title("Added to "+ investmentVehicle.getName()+" investment vehicle")
                .notificationFlag(notificationFlag)
                .build();
        meedlNotificationUsecase.sendNotification(meedlNotification);
    }
}
