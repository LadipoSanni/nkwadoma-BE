package africa.nkwadoma.nkwadoma.infrastructure.adapters.config;

import africa.nkwadoma.nkwadoma.application.ports.input.email.SendColleagueEmailUseCase;
import africa.nkwadoma.nkwadoma.application.ports.input.email.SendLoaneeEmailUsecase;
import africa.nkwadoma.nkwadoma.application.ports.input.email.SendOrganizationEmployeeEmailUseCase;
import africa.nkwadoma.nkwadoma.application.ports.input.loan.LoaneeUseCase;
import africa.nkwadoma.nkwadoma.application.ports.output.education.*;
import africa.nkwadoma.nkwadoma.application.ports.output.email.EmailOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.*;
import africa.nkwadoma.nkwadoma.application.ports.output.loan.LoanBreakdownOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.loan.LoanReferralOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.loan.LoaneeLoanDetailsOutputPort;
import africa.nkwadoma.nkwadoma.domain.service.education.CohortService;
import africa.nkwadoma.nkwadoma.domain.service.email.NotificationService;
import africa.nkwadoma.nkwadoma.application.ports.output.investmentVehicle.InvestmentVehicleOutputPort;
import africa.nkwadoma.nkwadoma.domain.service.identity.OrganizationIdentityService;
import africa.nkwadoma.nkwadoma.domain.service.identity.UserIdentityService;
import africa.nkwadoma.nkwadoma.domain.service.investmentVehicle.InvestmentVehicleService;
import africa.nkwadoma.nkwadoma.domain.service.loanManagement.LoaneeService;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.education.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.identityManager.OrganizationEmployeeIdentityAdapter;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.email.EmailAdapter;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.identityManager.KeycloakAdapter;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.identityVerificationManager.PremblyAdapter;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.identityVerificationManager.QoreIdAdapter;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.identity.KeyCloakMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.identityManager.BlackListedTokenAdapter;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.identityVerificationManager.SmileIdAdapter;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.InvestmentVehicleAdapter;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.identityManager.OrganizationIdentityAdapter;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.identityManager.UserIdentityAdapter;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.loanManagement.LoanBreakdownPersistenceAdapter;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.loanManagement.LoaneeLoanDetailPersistenceAdapter;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.loanManagement.LoaneePersistenceAdapter;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.mapper.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.mapper.InvestmentVehicleMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.mapper.OrganizationEmployeeIdentityMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.mapper.OrganizationIdentityMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.mapper.UserIdentityMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.EmployeeAdminEntityRepository;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.InvestmentVehicleEntityRepository;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.OrganizationEntityRepository;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.identity.UserEntityRepository;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.education.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.loan.LoanBreakdownRepository;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.loan.LoaneeLoanDetailRepository;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.loan.LoaneeRepository;
import africa.nkwadoma.nkwadoma.infrastructure.utilities.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.education.CohortRepository;
import org.keycloak.admin.client.Keycloak;
import org.springframework.beans.factory.annotation.Autowired;
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
    public CohortService cohortService(CohortOutputPort cohortOutputPort,
                                       ProgramOutputPort programOutputPort,
                                       LoaneeOutputPort loaneeOutputPort,
                                       ProgramCohortOutputPort programCohortOutputPort,
                                       LoanDetailsOutputPort loanDetailsOutputPort,
                                       LoanBreakdownOutputPort loanBreakdownOutputPort,
                                       LoaneeUseCase loaneeUseCase,
                                       CohortMapper cohortMapper,UserIdentityOutputPort userIdentityOutputPort){
        return new CohortService(cohortOutputPort,programOutputPort,loaneeOutputPort,programCohortOutputPort
        ,loanDetailsOutputPort,loanBreakdownOutputPort,cohortMapper,userIdentityOutputPort,loaneeUseCase);
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
    public LoaneeService loaneeService(OrganizationEmployeeIdentityOutputPort organizationEmployeeIdentityOutputPort,
                                       LoaneeOutputPort loaneeOutputPort,
                                       UserIdentityOutputPort userIdentityOutputPort,
                                       IdentityManagerOutputPort identityManagerOutputPort,
                                       CohortOutputPort cohortOutputPort,
                                       LoaneeLoanDetailsOutputPort loaneeLoanDetailsOutputPort,
                                       LoanBreakdownOutputPort loanBreakdownOutputPort,
                                       OrganizationIdentityOutputPort organizationIdentityOutputPort,
                                       SendLoaneeEmailUsecase sendLoaneeEmailUsecase,
                                       LoanReferralOutputPort loanReferralOutputPort){
        return new LoaneeService(organizationEmployeeIdentityOutputPort,
                loaneeOutputPort,userIdentityOutputPort,
                identityManagerOutputPort,cohortOutputPort,loaneeLoanDetailsOutputPort,loanBreakdownOutputPort,
                organizationIdentityOutputPort,sendLoaneeEmailUsecase,loanReferralOutputPort);
    }

    @Bean
    public LoanBreakdownPersistenceAdapter loanBreakdownPersistenceAdapter(LoanBreakdownRepository loanBreakdownRepository,
    LoanBreakdownMapper loanBreakdownMapper,LoaneeLoanDetailMapper loaneeLoanDetailMapper){
        return new LoanBreakdownPersistenceAdapter(loanBreakdownRepository,loanBreakdownMapper,loaneeLoanDetailMapper);

    }

    @Bean
    public LoaneeLoanDetailPersistenceAdapter loaneeLoanDetailPersistenceAdapter(LoaneeLoanDetailRepository loaneeLoanDetailRepository,
                                                                                 LoaneeLoanDetailMapper loaneeLoanDetailMapper){
        return new LoaneeLoanDetailPersistenceAdapter(loaneeLoanDetailRepository,loaneeLoanDetailMapper);
    }


}
