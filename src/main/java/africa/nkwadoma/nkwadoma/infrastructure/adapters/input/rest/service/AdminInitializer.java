package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.service;


import africa.nkwadoma.nkwadoma.application.ports.input.loanmanagement.loancalculation.CalculationEngineUseCase;
import africa.nkwadoma.nkwadoma.application.ports.input.notification.SendColleagueEmailUseCase;
import africa.nkwadoma.nkwadoma.application.ports.input.loanmanagement.LoanMetricsUseCase;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.IdentityManagerOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.OrganizationEmployeeIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.OrganizationIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.UserIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.meedlportfolio.PortfolioOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.ActivationStatus;
import africa.nkwadoma.nkwadoma.domain.enums.Industry;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.education.ServiceOffering;
import africa.nkwadoma.nkwadoma.domain.model.identity.OrganizationEmployeeIdentity;
import africa.nkwadoma.nkwadoma.domain.model.identity.OrganizationIdentity;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import africa.nkwadoma.nkwadoma.domain.model.meedlPortfolio.Portfolio;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.organization.*;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDateTime;
import java.util.*;

import static africa.nkwadoma.nkwadoma.domain.enums.IdentityRole.PORTFOLIO_MANAGER;

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

    @Value("${superAdmin.email}")
    private String SUPER_ADMIN_EMAIL ;

    @Value("${superAdmin.firstName}")
    private String SUPER_ADMIN_FIRST_NAME ;

    @Value("${superAdmin.lastName}")
    private String SUPER_ADMIN_LAST_NAME ;
    @Value("${superAdmin.createdBy}")
    private String CREATED_BY;

    private UserIdentity getUserIdentity() {
        return UserIdentity.builder()
                .email(SUPER_ADMIN_EMAIL)
                .firstName(SUPER_ADMIN_FIRST_NAME)
                .lastName(SUPER_ADMIN_LAST_NAME)
                .role(PORTFOLIO_MANAGER)
                .createdBy(CREATED_BY)
                .build();
    }
    private OrganizationIdentity getOrganizationIdentity(UserIdentity userIdentity) {
        return OrganizationIdentity.builder()
                .name("Meedl")
                .email("meedl@meedl.com")
                .tin("kwadoma2189")
                .rcNumber("RC2892832")
                .phoneNumber("0908965321")
                .status(ActivationStatus.ACTIVE)
                .organizationEmployees(List.of(OrganizationEmployeeIdentity
                        .builder()
                        .meedlUser(userIdentity)
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
        organizationIdentity.setStatus(ActivationStatus.ACTIVE);
        Optional<OrganizationEntity> foundOrganization = organizationIdentityOutputPort.findByRcNumber(organizationIdentity.getRcNumber());
        organizationIdentity = getKeycloakOrganizationIdentity(organizationIdentity, foundOrganization);
        OrganizationIdentity savedOrganizationIdentity;
        try {
            log.info("Creating first organization identity {}", organizationIdentity);
            if(foundOrganization.isEmpty()) {
                savedOrganizationIdentity = organizationIdentityOutputPort.save(organizationIdentity);
            }
            else savedOrganizationIdentity = organizationIdentityOutputPort.findByEmail(organizationIdentity.getEmail());
            log.info("Saving organization identity {}", savedOrganizationIdentity);
        } catch (MeedlException exception) {
            log.warn("Failed to create organization identity on db for first organization {}", exception.getMessage());
            savedOrganizationIdentity = organizationIdentityOutputPort.findByEmail(organizationIdentity.getEmail());
        }
        OrganizationEmployeeIdentity employeeIdentity = organizationIdentity.getOrganizationEmployees().get(0);
        employeeIdentity.setOrganization(organizationIdentity.getId());
        employeeIdentity.setStatus(ActivationStatus.ACTIVE);
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

    private OrganizationIdentity getKeycloakOrganizationIdentity(OrganizationIdentity organizationIdentity, Optional<OrganizationEntity> foundOrganization) throws MeedlException {
        try {
            if (foundOrganization.isEmpty()) {
                log.info("Creating first organization identity");
                organizationIdentity = identityManagerOutPutPort.createKeycloakClient(organizationIdentity);
            }
        } catch (MeedlException exception) {
            log.warn("Failed to create organization identity's client representation for first organization {}", exception.getMessage());
            ClientRepresentation foundClient = identityManagerOutPutPort.getClientRepresentationByClientId(organizationIdentity.getName());
            organizationIdentity.setId(foundClient.getId());
        }
        return organizationIdentity;
    }

    public UserIdentity inviteFirstUser(UserIdentity userIdentity) throws MeedlException {
        userIdentity.setCreatedAt(LocalDateTime.now());
        userIdentity = saveUserToKeycloak(userIdentity);
        UserIdentity foundUserIdentity = null;
        log.info("First user, after saving on keycloak: {}", userIdentity);
        try {
            foundUserIdentity = userIdentityOutputPort.findByEmail(userIdentity.getEmail());
        } catch (MeedlException e) {
            log.warn("First user not found, creating first user: {}", e.getMessage());
        } finally {
            log.info("First user after finding, before saving to db: {}", foundUserIdentity);
            if (ObjectUtils.isEmpty(foundUserIdentity)) {
                userIdentity = saveUserToDB(userIdentity);
            }else {
                userIdentity = foundUserIdentity;
                log.info("First user already exists");
            }
        }
        return userIdentity;
    }

    private UserIdentity saveUserToDB(UserIdentity userIdentity) throws MeedlException {
            try {
                return userIdentityOutputPort.save(userIdentity);
            } catch (MeedlException e) {
                log.error("Unable to save user to identity manager, error : {}", e.getMessage());
                throw new MeedlException("Unable to save user to data base, error : " + e.getMessage());
            }
    }

    private UserIdentity saveUserToKeycloak(UserIdentity userIdentity) throws MeedlException {
        try {
            userIdentity = identityManagerOutPutPort.createUser(userIdentity);
            log.info("User created successfully on keycloak sending email to user");
            sendEmail.sendColleagueEmail("MEEDL",userIdentity);
        } catch (MeedlException e) {
            log.warn("Unable to create user on identity manager, error : {}", e.getMessage());
            UserRepresentation userRepresentation = identityManagerOutPutPort.getUserRepresentation(userIdentity, Boolean.TRUE);
            log.info("user representation email {} , id : {}", userRepresentation.getEmail(), userRepresentation.getId() );
            userIdentity.setId(userRepresentation.getId());
        }
        return userIdentity;
    }

    private Portfolio getPortfolio(){
        return Portfolio.builder().portfolioName("Meedl").build();
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

    @PostConstruct
    public void init() throws MeedlException {
        UserIdentity userIdentity = inviteFirstUser(getUserIdentity());
        OrganizationIdentity organizationIdentity = createFirstOrganizationIdentity(getOrganizationIdentity(userIdentity));
        loanMetricsUseCase.correctLoanRequestCount();
        Portfolio portfolio = createMeedlPortfolio(getPortfolio());
        log.info("Meedl portfolio process done-- {}", portfolio);
        calculationEngineUseCase.scheduleDailyInterestCalculation();
        calculationEngineUseCase.scheduleMonthlyInterestCalculation();
    }
}
