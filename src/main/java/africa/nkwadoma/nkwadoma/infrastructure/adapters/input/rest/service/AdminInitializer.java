package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.service;


import africa.nkwadoma.nkwadoma.application.ports.input.loanmanagement.loancalculation.CalculationEngineUseCase;
import africa.nkwadoma.nkwadoma.application.ports.input.notification.SendColleagueEmailUseCase;
import africa.nkwadoma.nkwadoma.application.ports.input.loanmanagement.LoanMetricsUseCase;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.*;
import africa.nkwadoma.nkwadoma.application.ports.output.meedlportfolio.DemographyOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.meedlportfolio.PortfolioOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.constants.MeedlConstants;
import africa.nkwadoma.nkwadoma.domain.enums.identity.ActivationStatus;
import africa.nkwadoma.nkwadoma.domain.enums.Industry;
import africa.nkwadoma.nkwadoma.domain.enums.identity.IdentityRole;
import africa.nkwadoma.nkwadoma.domain.enums.identity.OrganizationType;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.education.ServiceOffering;
import africa.nkwadoma.nkwadoma.domain.model.identity.OrganizationEmployeeIdentity;
import africa.nkwadoma.nkwadoma.domain.model.identity.OrganizationIdentity;
import africa.nkwadoma.nkwadoma.domain.model.identity.OrganizationLoanDetail;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import africa.nkwadoma.nkwadoma.domain.model.meedlPortfolio.Demography;
import africa.nkwadoma.nkwadoma.domain.model.meedlPortfolio.Portfolio;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.organization.*;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;


@Component
@RequiredArgsConstructor
@Slf4j
public class AdminInitializer {
    private final SendColleagueEmailUseCase sendEmail;
    private final UserIdentityOutputPort userIdentityOutputPort;
    private final IdentityManagerOutputPort identityManagerOutPutPort;
    private final OrganizationIdentityOutputPort organizationIdentityOutputPort;
    private final OrganizationEmployeeIdentityOutputPort organizationEmployeeIdentityOutputPort;
    private final LoanMetricsUseCase loanMetricsUseCase;
    private final PortfolioOutputPort portfolioOutputPort;
    private final CalculationEngineUseCase calculationEngineUseCase;
    private final OrganizationLoanDetailOutputPort organizationLoanDetailOutputPort;
    private final DemographyOutputPort demographyOutputPort;

    @Value("${superAdmin.email}")
    private String SUPER_ADMIN_EMAIL ;

    @Value("${superAdmin.firstName}")
    private String SUPER_ADMIN_FIRST_NAME ;

    @Value("${superAdmin.lastName}")
    private String SUPER_ADMIN_LAST_NAME ;

    private UserIdentity getUserIdentity() {
        return UserIdentity.builder()
                .email(SUPER_ADMIN_EMAIL)
                .firstName(SUPER_ADMIN_FIRST_NAME)
                .lastName(SUPER_ADMIN_LAST_NAME)
                .role(IdentityRole.MEEDL_SUPER_ADMIN)
                .createdBy(UUID.randomUUID().toString())
                .build();
    }
    private OrganizationIdentity getOrganizationIdentity(UserIdentity userIdentity) {
        return OrganizationIdentity.builder()
                .name(MeedlConstants.MEEDL)
                .email("meedl@meedl.com")
                .tin("kwadoma2189")
                .rcNumber("RC2892832")
                .phoneNumber("0908965321")
                .organizationType(OrganizationType.MEEDL)
                .activationStatus(ActivationStatus.ACTIVE)
                .requestedInvitationDate(LocalDateTime.now())
                .organizationEmployees(List.of(OrganizationEmployeeIdentity
                        .builder()
                        .meedlUser(userIdentity)
                        .createdBy(userIdentity.getId())
                        .build()))
                .serviceOfferings(List.of(ServiceOffering
                        .builder()
                        .industry(Industry.EDUCATION)
                        .name("TRAINING")
                        .build()))
                .build();
    }
    private OrganizationIdentity createFirstOrganizationIdentity(OrganizationIdentity organizationIdentity) throws MeedlException {
        organizationIdentity.setEnabled(Boolean.TRUE);
        organizationIdentity.setInvitedDate(LocalDateTime.now().toString());
        organizationIdentity.setActivationStatus(ActivationStatus.ACTIVE);
        Optional<OrganizationIdentity> foundOrganization = organizationIdentityOutputPort.findByRcNumber(organizationIdentity.getRcNumber());
        organizationIdentity = getKeycloakOrganizationIdentity(organizationIdentity, foundOrganization);
        OrganizationIdentity savedOrganizationIdentity;
        try {
            log.info("Creating first organization identity {}", organizationIdentity);
            if(foundOrganization.isEmpty()) {
                savedOrganizationIdentity = organizationIdentityOutputPort.save(organizationIdentity);
                log.info("Saved organization identity {}", savedOrganizationIdentity);
            }
            else savedOrganizationIdentity = organizationIdentityOutputPort.findByEmail(organizationIdentity.getEmail());
            log.info("Saving organization identity {}", savedOrganizationIdentity);
        } catch (MeedlException exception) {
            log.warn("Failed to create organization identity on db for first organization {}", exception.getMessage());
            savedOrganizationIdentity = organizationIdentityOutputPort.findByEmail(organizationIdentity.getEmail());
        }
        OrganizationLoanDetail organizationLoanDetail = organizationLoanDetailOutputPort.findByOrganizationId(organizationIdentity.getId());
        if (ObjectUtils.isEmpty(organizationLoanDetail)){
            log.info("No organization loan details was found for this first organization. \n------> Saving a new organization loan detail.");
            organizationLoanDetail = buildOrganizationLoanDetail(organizationIdentity);
            organizationLoanDetailOutputPort.save(organizationLoanDetail);
        }
        OrganizationEmployeeIdentity employeeIdentity = organizationIdentity.getOrganizationEmployees().get(0);
        employeeIdentity.setOrganization(organizationIdentity.getId());
        employeeIdentity.setActivationStatus(ActivationStatus.ACTIVE);
        log.info("Organization employee identity {}", employeeIdentity.getMeedlUser());

        try{
            Optional<OrganizationEmployeeIdentity> foundOptionalOrganizationEmployee = organizationEmployeeIdentityOutputPort.findByMeedlUserId(employeeIdentity.getMeedlUser().getId());
            if (foundOptionalOrganizationEmployee.isEmpty()){
                log.info("Saving first employee to db with id: {}, email {} ", employeeIdentity.getMeedlUser().getId(),  employeeIdentity.getMeedlUser().getEmail());
                employeeIdentity = organizationEmployeeIdentityOutputPort.save(employeeIdentity);
            }else {
                employeeIdentity = foundOptionalOrganizationEmployee.get();
                log.info("First employee was previously created and exist with id {}", employeeIdentity.getId());
            }
        } catch(DataIntegrityViolationException dataIntegrityViolationException){
            log.warn("Employee for first organization {} already exists wth error message: {}", organizationIdentity.getId(), dataIntegrityViolationException.getMessage() );
        }
        savedOrganizationIdentity.setOrganizationEmployees(List.of(employeeIdentity));

        log.info("Created organization identity: {} , employee is : {}", organizationIdentity, savedOrganizationIdentity.getOrganizationEmployees().get(0));
        return savedOrganizationIdentity;
    }

    private OrganizationIdentity getKeycloakOrganizationIdentity(OrganizationIdentity organizationIdentity, Optional<OrganizationIdentity> foundOrganization) throws MeedlException {
        try {
            if (foundOrganization.isEmpty()) {
                log.info("Creating first organization identity");
                organizationIdentity = identityManagerOutPutPort.createKeycloakClient(organizationIdentity);
            }else {
                organizationIdentity.setId(foundOrganization.get().getId());
            }
        } catch (MeedlException exception) {
            log.warn("Failed to create organization identity's client representation for first organization {}", exception.getMessage());
            ClientRepresentation foundClient = identityManagerOutPutPort.getClientRepresentationByClientId(organizationIdentity.getName());
            organizationIdentity.setId(foundClient.getId());
        }
        return organizationIdentity;
    }
    private OrganizationLoanDetail buildOrganizationLoanDetail(OrganizationIdentity organizationIdentity) {
        return OrganizationLoanDetail.builder()
                .organization(organizationIdentity).amountReceived(BigDecimal.valueOf(0))
                .amountRequested(BigDecimal.valueOf(0)).amountRepaid(BigDecimal.valueOf(0))
                .interestIncurred(BigDecimal.ZERO)
                .outstandingAmount(BigDecimal.valueOf(0)).build();
    }

    public UserIdentity inviteFirstUser(UserIdentity userIdentity) throws MeedlException {
        userIdentity.setCreatedAt(LocalDateTime.now());
        saveUserToKeycloak(userIdentity);
        savedSuperAdminToDb(userIdentity);
        removeDuplicateSuperAdmin(userIdentity);
        return userIdentity;
    }

    private void savedSuperAdminToDb(UserIdentity userIdentity) throws MeedlException {
        UserIdentity foundUserIdentity = null;
        try {
            foundUserIdentity = userIdentityOutputPort.findByEmail(userIdentity.getEmail());
            foundUserIdentity.setCreatedBy(foundUserIdentity.getId());
        } catch (MeedlException e) {
            log.warn("First user not found, creating first user: {}", e.getMessage());
        } finally {
            log.info("First user after finding, before saving to db: {}", foundUserIdentity);
            if (ObjectUtils.isEmpty(foundUserIdentity)) {
                userIdentity = saveUserToDB(userIdentity);
            }else {
                userIdentity = foundUserIdentity;
                log.info("First user already exists in db {}", foundUserIdentity);
            }
            if (!IdentityRole.MEEDL_SUPER_ADMIN.equals(userIdentity.getRole())){
                log.info("Changing the first user role to meedl super admin. User previous role was {}", userIdentity.getRole());
                userIdentityOutputPort.changeUserRole(userIdentity.getId(), IdentityRole.MEEDL_SUPER_ADMIN);
            }
        }
    }

    private void removeDuplicateSuperAdmin(UserIdentity userIdentity) {
        try {
            removeDuplicateSuperAdmins(userIdentity);
            log.info("No duplicate roles exist after this check. All either removed or does not exist.");
        } catch (MeedlException e) {
            log.error("Error finding {} by role to make change update",userIdentity.getRole().name(), e);
        }
    }

    private void removeDuplicateSuperAdmins(UserIdentity userIdentity) throws MeedlException {
        List<UserIdentity> superAdminsOnKeycloak = identityManagerOutPutPort.getUsersByRole(IdentityRole.MEEDL_SUPER_ADMIN.name());
        List<UserIdentity> superAdminsOnDb = userIdentityOutputPort.findAllByRole(IdentityRole.MEEDL_SUPER_ADMIN);
        log.info("Role being searched for at admin initializer {}", userIdentity.getRole());
        if (superAdminsOnKeycloak.isEmpty() && superAdminsOnDb.isEmpty()) {
            log.info("No users found with role {}", userIdentity.getRole());
            return;
        }

        boolean emailExistsOnKeycloak = superAdminsOnKeycloak.stream()
                .anyMatch(u -> u.getEmail().equalsIgnoreCase(userIdentity.getEmail()));

        if (emailExistsOnKeycloak) {
            for (UserIdentity user : superAdminsOnKeycloak) {
                if (!user.getEmail().equalsIgnoreCase(userIdentity.getEmail())) {
                    log.info("Changing role of user on keycloak {} to {}", user.getEmail(), IdentityRole.MEEDL_ADMIN.name());
                    identityManagerOutPutPort.changeUserRole(user, IdentityRole.MEEDL_ADMIN.name());
                }
            }
        }
            for (UserIdentity user : superAdminsOnDb) {
                if (!user.getEmail().equalsIgnoreCase(userIdentity.getEmail())) {
                    log.info("Changing role of user on db {} to {}", user.getEmail(), IdentityRole.MEEDL_ADMIN.name());
                    userIdentityOutputPort.changeUserRole(user.getId(), IdentityRole.MEEDL_ADMIN);
                }
            }

    }

    private UserIdentity saveUserToDB(UserIdentity userIdentity) throws MeedlException {
            try {
                return userIdentityOutputPort.save(userIdentity);
            } catch (MeedlException e) {
                log.error("Unable to save user to identity manager, error : {}", e.getMessage());
                throw new MeedlException("Unable to save user to data base, error : " + e.getMessage());
            }
    }

    private void saveUserToKeycloak(UserIdentity userIdentity) {
        try {
            userIdentity = identityManagerOutPutPort.createUser(userIdentity);
            log.info("User created successfully on keycloak sending email to user");
            sendEmail.sendColleagueEmail(MeedlConstants.MEEDL,userIdentity);
        } catch (MeedlException e) {
            log.warn("Unable to create user on identity manager, error : {}", e.getMessage());
            UserRepresentation userRepresentation = null;
            try {
                userRepresentation = identityManagerOutPutPort.getUserRepresentation(userIdentity, Boolean.TRUE);
            } catch (MeedlException ex) {
                log.error("unable to get first user from keycloak although i got user already exist on keycloak {}", userIdentity);
                throw new RuntimeException(ex);
            }
            log.info("user representation email in admin initializer {} , id : {} role {}", userRepresentation.getEmail(), userRepresentation.getId() , userRepresentation.getRealmRoles());
            userIdentity.setId(userRepresentation.getId());
            try {
                IdentityRole identityRole = identityManagerOutPutPort.getUserRoles(userIdentity);
                log.info("Identity role found is {}", identityRole);
                if (!IdentityRole.MEEDL_SUPER_ADMIN.equals(identityRole)) {
                    identityManagerOutPutPort.changeUserRole(userIdentity, IdentityRole.MEEDL_SUPER_ADMIN.name());
                    log.info("The user role has been updated");
                }
            } catch (MeedlException ex) {
                log.error("Error finding user role {}", ex.getMessage());
                throw new RuntimeException(ex);
            }
        }
        log.info("First user, after saving on keycloak: {}", userIdentity);
        userIdentity.setCreatedBy(userIdentity.getId());
    }

    private Portfolio getPortfolio(){
        return Portfolio.builder().portfolioName(MeedlConstants.MEEDL).disbursedLoanAmount(BigDecimal.ZERO).build();
    }

    public Portfolio createMeedlPortfolio(Portfolio portfolio) throws MeedlException {
        Portfolio foundPortfolio = portfolioOutputPort.findPortfolio(portfolio);
        log.info("found meedl portfolio -- {}", foundPortfolio);
        if (ObjectUtils.isEmpty(foundPortfolio)) {
            log.info("Meedl portfolio created successfully -- {}", portfolio);
            return portfolioOutputPort.save(portfolio);
        }
        return foundPortfolio;
    }

    public Demography createDemography(Demography demography) throws MeedlException {
        Demography foundDemography = demographyOutputPort.findDemographyByName(MeedlConstants.MEEDL);
        log.info("found demography -- {}", foundDemography);
        if (ObjectUtils.isEmpty(foundDemography)) {
            log.info("about to create Demography -- {}", demography);
            return demographyOutputPort.save(demography);
        }
        return foundDemography;
    }

    @PostConstruct
    public void init() throws MeedlException {
        UserIdentity userIdentity = inviteFirstUser(getUserIdentity());
        OrganizationIdentity organizationIdentity = createFirstOrganizationIdentity(getOrganizationIdentity(userIdentity));
        log.info("First organization ============================> {}", organizationIdentity);
        loanMetricsUseCase.correctLoanRequestCount();
        Portfolio portfolio = createMeedlPortfolio(getPortfolio());
        log.info("Meedl portfolio process done -- {} ", portfolio);
        Demography demography = createDemography(getDemography());
        log.info("Demography process done -- {} ", demography);
        calculationEngineUseCase.scheduleDailyInterestCalculation();
        calculationEngineUseCase.scheduleMonthlyInterestCalculation();
    }

    private Demography getDemography() {
        return Demography.builder().name(MeedlConstants.MEEDL).age35To45Count(0).age25To35Count(0).age17To25Count(0)
                .totalGenderCount(0).femaleCount(0).maleCount(0).southEastCount(0).southSouthCount(0)
                .southWestCount(0).northWestCount(0).northEastCount(0).northCentralCount(0)
                .nonNigerian(0).tertiaryCount(0).oLevelCount(0).build();
    }

    private final Environment environment;
    @EventListener(ApplicationReadyEvent.class)
    public void logContextPath() {
        String port = environment.getProperty("server.port");
        String profile = environment.getProperty("spring.profiles.active");
        String version = environment.getProperty("api.version");
        String path = environment.getProperty("server.servlet.context-path");
        log.info("\nOnstart - Context path: {} \nversion ----------> {} \nprofile --------------> {}\nport ------------>{}", path, version, profile, port);
    }

}
