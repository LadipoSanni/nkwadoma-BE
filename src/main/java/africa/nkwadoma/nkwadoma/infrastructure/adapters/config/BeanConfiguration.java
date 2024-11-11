package africa.nkwadoma.nkwadoma.infrastructure.adapters.config;

import africa.nkwadoma.nkwadoma.application.ports.input.email.SendColleagueEmailUseCase;
import africa.nkwadoma.nkwadoma.application.ports.input.email.SendOrganizationEmployeeEmailUseCase;
import africa.nkwadoma.nkwadoma.application.ports.output.education.*;
import africa.nkwadoma.nkwadoma.application.ports.output.email.EmailOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.loan.LoanBreakdownOutputPort;
import africa.nkwadoma.nkwadoma.domain.model.education.LoanDetail;
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
import africa.nkwadoma.nkwadoma.domain.service.loanManagement.LoaneeService;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.OrganizationEmployeeIdentityAdapter;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.email.EmailAdapter;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.identityManager.KeycloakAdapter;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.identityVerificationManager.PremblyAdapter;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.KeyCloakMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.BlackListedTokenAdapter;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.InvestmentVehicleAdapter;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.OrganizationIdentityAdapter;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.UserIdentityAdapter;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.education.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.loan.LoanBreakdownPersistenceAdapter;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.loan.LoaneePersistenceAdapter;
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
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.loan.LoaneeRepository;
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
            SendOrganizationEmployeeEmailUseCase sendOrganizationEmployeeEmailUseCase,
            OrganizationIdentityMapper organizationIdentityMapper
            ){
        return new OrganizationIdentityService(organizationIdentityOutputPort,identityManagerOutPutPort,organizationIdentityMapper, userIdentityOutputPort,organizationEmployeeIdentityOutputPort, sendOrganizationEmployeeEmailUseCase);
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
    public CohortService cohortService(CohortOutputPort cohortOutputPort,CohortLoaneeOutputPort cohortLoaneeOutputPort){
        return new CohortService(cohortOutputPort,cohortLoaneeOutputPort);
    }

    @Bean
    public CohortPersistenceAdapter cohortPersistenceAdapter(
            ProgramOutputPort programOutputPort, CohortRepository cohortRepository, CohortMapper cohortMapper,
            UserIdentityOutputPort userIdentityOutputPort, ProgramCohortOutputPort programCohortOutputPort,
            LoanBreakdownRepository loanBreakdownRepository, LoanBreakdownOutputPort loanBreakdownOutputPort,
            LoanDetailsOutputPort loanDetailsOutputPort
            ){
        return new CohortPersistenceAdapter(programOutputPort,cohortRepository,
                cohortMapper,userIdentityOutputPort,programCohortOutputPort,
                 loanBreakdownRepository,loanBreakdownOutputPort,loanDetailsOutputPort);
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

    @Bean
    public LoanDetailsPersistenceAdapter loanDetailsPersistenceAdapter(LoanDetailRepository loanDetailRepository,
                 LoanDetailMapper loanDetailMapper){
        return new LoanDetailsPersistenceAdapter(loanDetailRepository,loanDetailMapper);
    }

    @Bean
    public LoaneePersistenceAdapter loaneePersistenceAdapter(LoaneeMapper loaneeMapper, LoaneeRepository loaneeRepository){
        return new LoaneePersistenceAdapter(loaneeMapper,loaneeRepository);
    }


    @Bean
    public CohortLoaneePersistenceAdapter cohortLoaneePesistenceAdapter(CohortLoaneeRepository cohortLoaneeRepository,
                                                                        CohortLoaneeMapper cohortLoaneeMapper){
        return new CohortLoaneePersistenceAdapter(cohortLoaneeRepository,cohortLoaneeMapper);
    }

    @Bean
    public LoaneeService loaneeService(OrganizationIdentityOutputPort organizationIdentityOutputPort,
                                       OrganizationEmployeeIdentityOutputPort organizationEmployeeIdentityOutputPort,
                                       ProgramCohortOutputPort programCohortOutputPort,
                                       CohortLoaneeOutputPort cohortLoaneeOutputPort,
                                       LoaneeOutputPort loaneeOutputPort,
                                       UserIdentityOutputPort userIdentityOutputPort,
                                       IdentityManagerOutputPort identityManagerOutputPort,
                                       CohortOutputPort cohortOutputPort){
        return new LoaneeService(organizationIdentityOutputPort,organizationEmployeeIdentityOutputPort,
                programCohortOutputPort,cohortLoaneeOutputPort,loaneeOutputPort,userIdentityOutputPort,
                identityManagerOutputPort,cohortOutputPort);
    }

    @Bean
    public LoanBreakdownPersistenceAdapter loanBreakdownPersistenceAdapter(LoanBreakdownRepository loanBreakdownRepository,
    LoanBreakdownMapper loanBreakdownMapper){
        return new LoanBreakdownPersistenceAdapter(loanBreakdownRepository,loanBreakdownMapper);

    }
}
