package africa.nkwadoma.nkwadoma.infrastructure.adapters.config;

import africa.nkwadoma.nkwadoma.application.ports.input.email.SendColleagueEmailUseCase;
import africa.nkwadoma.nkwadoma.application.ports.input.email.SendOrganizationEmployeeEmailUseCase;
import africa.nkwadoma.nkwadoma.application.ports.output.email.EmailOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.email.TokenGeneratorOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.*;
import africa.nkwadoma.nkwadoma.domain.service.email.NotificationService;
import africa.nkwadoma.nkwadoma.domain.service.identity.OrganizationIdentityService;
import africa.nkwadoma.nkwadoma.domain.service.identity.UserIdentityService;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.OrganizationEmployeeIdentityAdapter;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.email.EmailAdapter;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.identityManager.KeycloakAdapter;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.identityVerificationManager.PremblyAdapter;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.KeyCloakMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.OrganizationIdentityAdapter;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.PasswordHistoryAdapter;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.UserIdentityAdapter;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.mapper.OrganizationEmployeeIdentityMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.mapper.OrganizationIdentityMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.mapper.PasswordHistoryMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.mapper.UserIdentityMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.EmployeeAdminEntityRepository;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.OrganizationEntityRepository;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.PasswordHistoryRepository;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.UserEntityRepository;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.token.TokenGeneratorAdapter;
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
            IdentityManagerOutPutPort identityManagerOutPutPort,
            UserIdentityOutputPort userIdentityOutputPort,
            OrganizationEmployeeIdentityOutputPort organizationEmployeeIdentityOutputPort,
            SendOrganizationEmployeeEmailUseCase sendOrganizationEmployeeEmailUseCase
            ){
        return new OrganizationIdentityService(organizationIdentityOutputPort,identityManagerOutPutPort,userIdentityOutputPort,organizationEmployeeIdentityOutputPort, sendOrganizationEmployeeEmailUseCase);
    }
    @Bean
    public UserIdentityService userIdentityService(UserIdentityOutputPort userIdentityOutputPort,
                                                   IdentityManagerOutPutPort identityManagerOutPutPort,
                                                   OrganizationEmployeeIdentityOutputPort organizationEmployeeIdentityOutputPort,
                                                   TokenGeneratorOutputPort tokenGeneratorOutputPort,
                                                   PasswordEncoder passwordEncoder,
                                                   PasswordHistoryOutputPort passwordHistoryOutputPort,
                                                   SendColleagueEmailUseCase sendColleagueEmailUseCase
                                                   ){
        return new UserIdentityService(userIdentityOutputPort,identityManagerOutPutPort,organizationEmployeeIdentityOutputPort,tokenGeneratorOutputPort,passwordEncoder,passwordHistoryOutputPort,sendColleagueEmailUseCase);
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
                                                                   OrganizationIdentityMapper organizationIdentityMapper){
        return new OrganizationIdentityAdapter(organizationEntityRepository,organizationIdentityMapper);
    }
    @Bean
    public UserIdentityAdapter userIdentityAdapter(UserEntityRepository userEntityRepository,
                                                   UserIdentityMapper userIdentityMapper){
        return new UserIdentityAdapter(userEntityRepository,userIdentityMapper);
    }

    @Bean
    public OrganizationEmployeeIdentityAdapter organizationOrganizationEmployeeIdentityAdapter(
            EmployeeAdminEntityRepository employeeAdminEntityRepository,
            OrganizationEmployeeIdentityMapper organizationEmployeeIdentityMapper
    ){
       return new OrganizationEmployeeIdentityAdapter(employeeAdminEntityRepository,organizationEmployeeIdentityMapper);
    }

    @Bean
    public TokenGeneratorAdapter tokenGeneratorAdapter(){
        return new TokenGeneratorAdapter();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public PasswordHistoryAdapter passwordHistoryAdapter(
            PasswordHistoryMapper passwordHistoryMapper,
            PasswordHistoryRepository passwordHistoryRepository
    ){
        return new PasswordHistoryAdapter(passwordHistoryMapper,passwordHistoryRepository);
    }

    @Bean
    public NotificationService emailService(EmailOutputPort emailOutputPort, TokenGeneratorOutputPort tokenGeneratorOutputPort){
        return new NotificationService(emailOutputPort,tokenGeneratorOutputPort);
    }



    @Bean
    public JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter() {
        return new JwtGrantedAuthoritiesConverter();
    }
}
