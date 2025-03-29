package africa.nkwadoma.nkwadoma.infrastructure.adapters.config;

import africa.nkwadoma.nkwadoma.application.ports.input.email.SendColleagueEmailUseCase;
import africa.nkwadoma.nkwadoma.application.ports.input.email.OrganizationEmployeeEmailUseCase;
import africa.nkwadoma.nkwadoma.application.ports.input.loan.LoaneeUseCase;
import africa.nkwadoma.nkwadoma.application.ports.input.meedlNotification.MeedlNotificationUsecase;
import africa.nkwadoma.nkwadoma.application.ports.output.education.*;
import africa.nkwadoma.nkwadoma.application.ports.output.email.EmailOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.*;
import africa.nkwadoma.nkwadoma.application.ports.output.investmentVehicle.*;
import africa.nkwadoma.nkwadoma.application.ports.output.loan.LoanBreakdownOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.meedlNotification.MeedlNotificationOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.meedlPortfolio.PortfolioOutputPort;
import africa.nkwadoma.nkwadoma.domain.service.education.CohortService;
import africa.nkwadoma.nkwadoma.domain.service.email.NotificationService;
import africa.nkwadoma.nkwadoma.domain.service.identity.UserIdentityService;
import africa.nkwadoma.nkwadoma.domain.service.investmentVehicle.InvestmentVehicleService;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.mapper.StringTrimMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.education.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.email.EmailAdapter;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.identityManager.KeycloakAdapter;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.identityVerificationManager.PremblyAdapter;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.identityVerificationManager.QoreIdAdapter;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.identity.KeyCloakMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.identityManager.BlackListedTokenAdapter;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.identityVerificationManager.SmileIdAdapter;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.investmentVehicle.InvestmentVehicleAdapter;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.identityManager.UserIdentityAdapter;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.loanManagement.LoanBreakdownPersistenceAdapter;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.loanManagement.LoaneeLoanDetailPersistenceAdapter;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.loanManagement.LoaneePersistenceAdapter;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.investmentVehicle.VehicleOperationMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.mapper.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.investmentVehicle.InvestmentVehicleMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.mapper.UserIdentityMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.investmentVehicle.InvestmentVehicleEntityRepository;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.identity.UserEntityRepository;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.education.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.loan.LoanBreakdownRepository;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.loan.LoaneeLoanDetailRepository;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.loan.LoaneeRepository;
import africa.nkwadoma.nkwadoma.infrastructure.utilities.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.education.CohortRepository;
import org.keycloak.admin.client.Keycloak;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.authentication.*;
import org.springframework.web.client.RestTemplate;
import org.thymeleaf.TemplateEngine;

@Configuration
public class BeanConfiguration {
    private RestTemplate restTemplate;

    @Bean
    public UserIdentityService userIdentityService(UserIdentityOutputPort userIdentityOutputPort,
                                                   IdentityManagerOutputPort identityManagerOutPutPort,
                                                   OrganizationEmployeeIdentityOutputPort organizationEmployeeIdentityOutputPort,
                                                   TokenUtils tokenUtils,
                                                   OrganizationEmployeeEmailUseCase sendOrganizationEmployeeEmailUseCase,
                                                   PasswordEncoder passwordEncoder,
                                                   SendColleagueEmailUseCase sendColleagueEmailUseCase,
                                                   UserIdentityMapper userIdentityMapper,
                                                   BlackListedTokenAdapter blackListedTokenAdapter,
                                                   OrganizationIdentityOutputPort organizationIdentityOutputPort
                                                   ){
        return new UserIdentityService(userIdentityOutputPort,identityManagerOutPutPort,organizationEmployeeIdentityOutputPort,sendOrganizationEmployeeEmailUseCase, tokenUtils,passwordEncoder,sendColleagueEmailUseCase, userIdentityMapper, blackListedTokenAdapter, organizationIdentityOutputPort);
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
    public RestTemplate restTemplate(){
        if (restTemplate == null) {
            restTemplate = new RestTemplate();
        }
        return restTemplate;
    }
    @Bean
    @Qualifier("premblyAdapter")
    public PremblyAdapter premblyAdapter(){
        return new PremblyAdapter(restTemplate());
    }
    @Bean
    @Qualifier("smileIdAdapter")
    public SmileIdAdapter smileIdAdapter(){
        return new SmileIdAdapter();
    }
    @Bean
    @Qualifier("qoreIdAdapter")
    public QoreIdAdapter qoreIdAdapter(){
        return new QoreIdAdapter();
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
    public InvestmentVehicleService investmentVehicleService(InvestmentVehicleOutputPort investmentVehicleIdentityOutputPort,
                                                             InvestmentVehicleMapper investmentVehicleMapper,
                                                             PortfolioOutputPort portfolioOutputPort,
                                                             FinancierOutputPort financierOutputPort,
                                                             InvestmentVehicleFinancierOutputPort investmentVehicleFinancierOutputPort,
                                                             VehicleOperationOutputPort vehicleOperationOutputPort,
                                                             CouponDistributionOutputPort couponDistributionOutputPort,
                                                             VehicleOperationMapper vehicleOperationMapper){
        return new InvestmentVehicleService(investmentVehicleIdentityOutputPort,investmentVehicleMapper,portfolioOutputPort,
                financierOutputPort,investmentVehicleFinancierOutputPort,vehicleOperationOutputPort,couponDistributionOutputPort,
                vehicleOperationMapper);
    }
    @Bean
    public CohortService cohortService(CohortOutputPort cohortOutputPort,
                                       ProgramOutputPort programOutputPort,
                                       LoaneeOutputPort loaneeOutputPort,
                                       ProgramCohortOutputPort programCohortOutputPort,
                                       LoanDetailsOutputPort loanDetailsOutputPort,
                                       LoanBreakdownOutputPort loanBreakdownOutputPort,
                                       LoaneeUseCase loaneeUseCase,
                                       CohortMapper cohortMapper,UserIdentityOutputPort userIdentityOutputPort,
                                       OrganizationIdentityOutputPort organizationIdentityOutputPort){
        return new CohortService(cohortOutputPort,programOutputPort,loaneeOutputPort,programCohortOutputPort
        ,loanDetailsOutputPort,loanBreakdownOutputPort,cohortMapper,userIdentityOutputPort,loaneeUseCase,
                organizationIdentityOutputPort);
    }
    @Bean
    public StringTrimMapper stringTrimMapper() {
        return new StringTrimMapper();
    }

    @Bean
    public CohortPersistenceAdapter cohortPersistenceAdapter(
             CohortRepository cohortRepository, CohortMapper cohortMapper,
            UserIdentityOutputPort userIdentityOutputPort, ProgramCohortOutputPort programCohortOutputPort,
            LoanBreakdownRepository loanBreakdownRepository
            ){
        return new CohortPersistenceAdapter(cohortRepository,
                cohortMapper,userIdentityOutputPort,programCohortOutputPort,
                 loanBreakdownRepository);
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }


    @Bean
    public NotificationService emailService(EmailOutputPort emailOutputPort, TokenUtils tokenUtils,
                                            UserIdentityOutputPort userIdentityOutputPort,
                                            MeedlNotificationOutputPort meedlNotificationOutputPort){
        return new NotificationService(emailOutputPort,tokenUtils,userIdentityOutputPort,meedlNotificationOutputPort);
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
    public LoanBreakdownPersistenceAdapter loanBreakdownPersistenceAdapter(LoanBreakdownRepository loanBreakdownRepository,
    LoanBreakdownMapper loanBreakdownMapper){
        return new LoanBreakdownPersistenceAdapter(loanBreakdownRepository,loanBreakdownMapper);

    }

    @Bean
    public LoaneeLoanDetailPersistenceAdapter loaneeLoanDetailPersistenceAdapter(LoaneeLoanDetailRepository loaneeLoanDetailRepository,
                                                                                 LoaneeLoanDetailMapper loaneeLoanDetailMapper){
        return new LoaneeLoanDetailPersistenceAdapter(loaneeLoanDetailRepository,loaneeLoanDetailMapper);
    }

}
