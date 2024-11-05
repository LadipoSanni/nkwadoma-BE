package africa.nkwadoma.nkwadoma.infrastructure.adapters.config;

import africa.nkwadoma.nkwadoma.application.ports.input.email.SendColleagueEmailUseCase;
import africa.nkwadoma.nkwadoma.application.ports.input.email.SendOrganizationEmployeeEmailUseCase;
import africa.nkwadoma.nkwadoma.application.ports.output.education.CohortOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.education.ProgramCohortOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.education.ProgramOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.email.EmailOutputPort;
import africa.nkwadoma.nkwadoma.domain.service.education.CohortService;
import africa.nkwadoma.nkwadoma.domain.service.email.NotificationService;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.IdentityManagerOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.OrganizationEmployeeIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.OrganizationIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.UserIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.investmentVehicle.InvestmentVehicleOutputPort;
import africa.nkwadoma.nkwadoma.domain.service.identity.OrganizationIdentityService;
import africa.nkwadoma.nkwadoma.domain.service.identity.UserIdentityService;
import africa.nkwadoma.nkwadoma.domain.service.investmentVehicle.InvestmentVehicleService;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.OrganizationEmployeeIdentityAdapter;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.email.EmailAdapter;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.identityManager.KeycloakAdapter;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.identityVerificationManager.PremblyAdapter;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.KeyCloakMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.BlackListedTokenAdapter;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.InvestmentVehicleAdapter;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.OrganizationIdentityAdapter;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.UserIdentityAdapter;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.education.CohortPersistenceAdapter;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.education.ProgramCohortPersistenceAdapter;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.mapper.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.mapper.InvestmentVehicleMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.mapper.OrganizationEmployeeIdentityMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.mapper.OrganizationIdentityMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.mapper.UserIdentityMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.EmployeeAdminEntityRepository;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.InvestmentVehicleEntityRepository;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.OrganizationEntityRepository;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.UserEntityRepository;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.education.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.loan.LoanBreakdownRepository;
import africa.nkwadoma.nkwadoma.infrastructure.utilities.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.education.CohortRepository;
import org.keycloak.admin.client.Keycloak;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.authentication.*;
import org.thymeleaf.TemplateEngine;

@Configuration
public class BeanConfiguration {
    @Bean
    public OrganizationIdentityService organizationIdentityService(
            OrganizationIdentityOutputPort organizationIdentityOutputPort,
            IdentityManagerOutputPort identityManagerOutPutPort,
            UserIdentityOutputPort userIdentityOutputPort,
            OrganizationEmployeeIdentityOutputPort organizationEmployeeIdentityOutputPort,
            SendOrganizationEmployeeEmailUseCase sendOrganizationEmployeeEmailUseCase
            ){
        return new OrganizationIdentityService(organizationIdentityOutputPort,identityManagerOutPutPort,userIdentityOutputPort,organizationEmployeeIdentityOutputPort, sendOrganizationEmployeeEmailUseCase);
    }
    @Bean
    public UserIdentityService userIdentityService(UserIdentityOutputPort userIdentityOutputPort,
                                                   IdentityManagerOutputPort identityManagerOutPutPort,
                                                   OrganizationEmployeeIdentityOutputPort organizationEmployeeIdentityOutputPort,
                                                   TokenUtils tokenUtils,
                                                   SendOrganizationEmployeeEmailUseCase sendOrganizationEmployeeEmailUseCase,
                                                   PasswordEncoder passwordEncoder,
                                                   SendColleagueEmailUseCase sendColleagueEmailUseCase,
                                                   UserIdentityMapper userIdentityMapper,
                                                   BlackListedTokenAdapter blackListedTokenAdapter
                                                   ){
        return new UserIdentityService(userIdentityOutputPort,identityManagerOutPutPort,organizationEmployeeIdentityOutputPort,sendOrganizationEmployeeEmailUseCase, tokenUtils,passwordEncoder,sendColleagueEmailUseCase, userIdentityMapper, blackListedTokenAdapter);
    }

    @Bean
    public EmailAdapter emailAdapter(TemplateEngine templateEngine, JavaMailSender javaMailSender){
        return new EmailAdapter(templateEngine,javaMailSender);
    }

    @Bean
    public KeycloakAdapter keycloakAdapter(Keycloak keycloak, KeyCloakMapper mapper){
        return new KeycloakAdapter(keycloak,mapper);
    }

    @Bean
    public PremblyAdapter premblyAdapter(){
        return new PremblyAdapter();
    }

    @Bean
    public OrganizationIdentityAdapter organizationIdentityAdapter(OrganizationEntityRepository organizationEntityRepository,
                                                                   OrganizationIdentityMapper organizationIdentityMapper,
                                                                   ServiceOfferEntityRepository serviceOfferEntityRepository, OrganizationServiceOfferingRepository organizationServiceOfferingRepository){
        return new OrganizationIdentityAdapter(organizationEntityRepository, serviceOfferEntityRepository, organizationIdentityMapper, organizationServiceOfferingRepository);
    }
    @Bean
    public UserIdentityAdapter userIdentityAdapter(UserEntityRepository userEntityRepository,
                                                   UserIdentityMapper userIdentityMapper,
                                                   OrganizationEmployeeIdentityOutputPort employeeIdentityOutputPort){
        return new UserIdentityAdapter(userEntityRepository,userIdentityMapper,employeeIdentityOutputPort);
    }

    @Bean
    public InvestmentVehicleAdapter investmentVehicleIdentityAdapter(InvestmentVehicleEntityRepository vehicleEntityRepository,
                                                                     InvestmentVehicleMapper investmentVehicleMapper){
        return new InvestmentVehicleAdapter(vehicleEntityRepository, investmentVehicleMapper);
    }

    @Bean
    public InvestmentVehicleService investmentVehicleService(InvestmentVehicleOutputPort investmentVehicleIdentityOutputPort){
        return new InvestmentVehicleService(investmentVehicleIdentityOutputPort);
    }
    @Bean
    public CohortService cohortService(CohortOutputPort cohortOutputPort){
        return new CohortService(cohortOutputPort);
    }

    @Bean
    public CohortPersistenceAdapter cohortPersistenceAdapter(
            ProgramOutputPort programOutputPort, CohortRepository cohortRepository, CohortMapper cohortMapper,
            UserIdentityOutputPort userIdentityOutputPort, ProgramCohortOutputPort programCohortOutputPort,
            LoanBreakdownRepository loanBreakdownRepository
    ){
        return new CohortPersistenceAdapter(programOutputPort,cohortRepository,
                cohortMapper,userIdentityOutputPort,programCohortOutputPort, loanBreakdownRepository);
    }

    @Bean
    public OrganizationEmployeeIdentityAdapter organizationOrganizationEmployeeIdentityAdapter(
            EmployeeAdminEntityRepository employeeAdminEntityRepository,
            OrganizationEmployeeIdentityMapper organizationEmployeeIdentityMapper
    ){
       return new OrganizationEmployeeIdentityAdapter(employeeAdminEntityRepository,organizationEmployeeIdentityMapper);
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }


    @Bean
    public NotificationService emailService(EmailOutputPort emailOutputPort, TokenUtils tokenUtils){
        return new NotificationService(emailOutputPort,tokenUtils);
    }

    @Bean
    public JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter() {
        return new JwtGrantedAuthoritiesConverter();
    }

    @Bean
    public ProgramCohortPersistenceAdapter programCohortPersistenceAdapter(ProgramCohortRepository programCohortRepository,
    ProgramCohortMapper programCohortMapper,ProgramOutputPort programOutputPort){
        return new ProgramCohortPersistenceAdapter(programCohortRepository,programCohortMapper,programOutputPort);
    }
}
