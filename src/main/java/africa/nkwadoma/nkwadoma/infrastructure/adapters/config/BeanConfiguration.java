package africa.nkwadoma.nkwadoma.infrastructure.adapters.config;

import africa.nkwadoma.nkwadoma.application.ports.input.notification.SendColleagueEmailUseCase;
import africa.nkwadoma.nkwadoma.application.ports.input.notification.OrganizationEmployeeEmailUseCase;
import africa.nkwadoma.nkwadoma.application.ports.output.aes.AesOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.education.*;
import africa.nkwadoma.nkwadoma.application.ports.output.financier.CooperateFinancierOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.notification.email.AsynchronousMailingOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.notification.email.EmailOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.financier.FinancierOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.*;
import africa.nkwadoma.nkwadoma.application.ports.output.investmentvehicle.*;
import africa.nkwadoma.nkwadoma.application.ports.output.notification.email.EmailTokenOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.notification.meedlNotification.AsynchronousNotificationOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.notification.meedlNotification.MeedlNotificationOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.meedlportfolio.PortfolioOutputPort;
import africa.nkwadoma.nkwadoma.domain.service.notification.NotificationService;
import africa.nkwadoma.nkwadoma.domain.service.identity.UserIdentityService;
import africa.nkwadoma.nkwadoma.domain.service.investmentvehicle.InvestmentVehicleService;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.mapper.StringTrimMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.aes.TokenUtils;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.education.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.notification.email.EmailAdapter;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.identitymanager.KeycloakAdapter;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.identityverificationmanager.PremblyAdapter;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.identityverificationmanager.QoreIdAdapter;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.identity.KeyCloakMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.identitymanager.BlackListedTokenAdapter;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.identityverificationmanager.SmileIdAdapter;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.investmentvehicle.InvestmentVehicleAdapter;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.identitymanager.UserIdentityAdapter;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.loanmanagement.LoanBreakdownPersistenceAdapter;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.loanmanagement.LoaneeLoanDetailPersistenceAdapter;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.loanmanagement.LoaneePersistenceAdapter;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.investmentvehicle.VehicleOperationMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.mapper.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.investmentvehicle.InvestmentVehicleMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.mapper.UserIdentityMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.investmentvehicle.InvestmentVehicleEntityRepository;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.identity.UserEntityRepository;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.education.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.loan.LoanBreakdownRepository;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.loan.LoaneeLoanDetailRepository;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.loan.LoaneeRepository;
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
                                                   AesOutputPort tokenUtils, EmailTokenOutputPort emailTokenOutputPort,
                                                   OrganizationEmployeeEmailUseCase sendOrganizationEmployeeEmailUseCase,
                                                   BlackListedTokenAdapter blackListedTokenAdapter,
                                                   OrganizationIdentityOutputPort organizationIdentityOutputPort,
                                                   AsynchronousMailingOutputPort asynchronousMailingOutputPort,
                                                   AsynchronousNotificationOutputPort asynchronousNotificationOutputPort,
                                                   FinancierOutputPort financierOutputPort
                                                   ){
        return new UserIdentityService(userIdentityOutputPort,identityManagerOutPutPort,organizationEmployeeIdentityOutputPort,sendOrganizationEmployeeEmailUseCase,
                tokenUtils, emailTokenOutputPort, blackListedTokenAdapter,
                organizationIdentityOutputPort, asynchronousMailingOutputPort, asynchronousNotificationOutputPort,financierOutputPort
        );
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
                                                                     InvestmentVehicleMapper investmentVehicleMapper, UserIdentityOutputPort userIdentityOutputport){
        return new InvestmentVehicleAdapter(vehicleEntityRepository, investmentVehicleMapper, userIdentityOutputport);
    }

    @Bean
    public InvestmentVehicleService investmentVehicleService(InvestmentVehicleOutputPort investmentVehicleIdentityOutputPort,
                                                             InvestmentVehicleMapper investmentVehicleMapper,
                                                             PortfolioOutputPort portfolioOutputPort,
                                                             FinancierOutputPort financierOutputPort,
                                                             InvestmentVehicleFinancierOutputPort investmentVehicleFinancierOutputPort,
                                                             UserIdentityOutputPort userIdentityOutputPort,
                                                             VehicleOperationOutputPort vehicleOperationOutputPort,
                                                             CouponDistributionOutputPort couponDistributionOutputPort,
                                                             VehicleOperationMapper vehicleOperationMapper,
                                                             VehicleClosureOutputPort vehicleClosureOutputPort){
        return new InvestmentVehicleService(investmentVehicleIdentityOutputPort,investmentVehicleMapper,portfolioOutputPort,
                financierOutputPort,investmentVehicleFinancierOutputPort,userIdentityOutputPort,
                vehicleOperationOutputPort,couponDistributionOutputPort,vehicleOperationMapper,vehicleClosureOutputPort);
    }
    @Bean
    public StringTrimMapper stringTrimMapper() {
        return new StringTrimMapper();
    }

    @Bean
    public CohortPersistenceAdapter cohortPersistenceAdapter(
             CohortRepository cohortRepository, CohortMapper cohortMapper,
             ProgramCohortOutputPort programCohortOutputPort,
            LoanBreakdownRepository loanBreakdownRepository
            ){
        return new CohortPersistenceAdapter(cohortRepository,
                cohortMapper,programCohortOutputPort,
                 loanBreakdownRepository);
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }


    @Bean
    public NotificationService emailService(EmailOutputPort emailOutputPort, AesOutputPort tokenUtils, EmailTokenOutputPort emailTokenOutputPort,
                                            UserIdentityOutputPort userIdentityOutputPort,
                                            MeedlNotificationOutputPort meedlNotificationOutputPort){
        return new NotificationService(emailOutputPort,tokenUtils, emailTokenOutputPort, userIdentityOutputPort,meedlNotificationOutputPort);
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
