package africa.nkwadoma.nkwadoma.infrastructure.config;

import africa.nkwadoma.nkwadoma.application.ports.output.email.EmailOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.email.TokenGeneratorOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.IdentityManagerOutPutPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.OrganizationEmployeeIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.OrganizationIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.UserIdentityOutputPort;
import africa.nkwadoma.nkwadoma.domain.service.identity.OrganizationIdentityService;
import africa.nkwadoma.nkwadoma.domain.service.identity.UserIdentityService;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.OrganizationOrganizationEmployeeIdentityAdapter;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.email.EmailAdapter;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.identityManager.KeycloakAdapter;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.identityVerificationManager.PremblyAdapter;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.KeyCloakMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.OrganizationIdentityAdapter;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.UserIdentityAdapter;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.mapper.OrganizationEmployeeIdentityMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.mapper.OrganizationIdentityMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.mapper.UserIdentityMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.EmployeeAdminEntityRepository;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.OrganizationEntityRepository;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.UserEntityRepository;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.token.TokenGeneratorAdapter;
import org.keycloak.admin.client.Keycloak;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.thymeleaf.TemplateEngine;

@Configuration
public class BeanConfiguration {
    @Bean
    public OrganizationIdentityService organizationIdentityService(
            OrganizationIdentityOutputPort organizationIdentityOutputPort,
            IdentityManagerOutPutPort identityManagerOutPutPort,
            UserIdentityOutputPort userIdentityOutputPort,
            OrganizationEmployeeIdentityOutputPort organizationEmployeeIdentityOutputPort,
            TokenGeneratorOutputPort tokenGeneratorOutputPort,
            EmailOutputPort emailOutputPort
            ){
        return new OrganizationIdentityService(organizationIdentityOutputPort,identityManagerOutPutPort,userIdentityOutputPort,organizationEmployeeIdentityOutputPort,tokenGeneratorOutputPort,emailOutputPort);
    }
    @Bean
    public UserIdentityService userIdentityService(UserIdentityOutputPort userIdentityOutputPort,IdentityManagerOutPutPort identityManagerOutPutPort,OrganizationEmployeeIdentityOutputPort organizationEmployeeIdentityOutputPort){
        return new UserIdentityService(userIdentityOutputPort,identityManagerOutPutPort,organizationEmployeeIdentityOutputPort);
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
    public OrganizationIdentityAdapter organizationIdentityAdapter(OrganizationEntityRepository organizationEntityRepository, OrganizationIdentityMapper organizationIdentityMapper){
        return new OrganizationIdentityAdapter(organizationEntityRepository,organizationIdentityMapper);
    }
    @Bean
    public UserIdentityAdapter userIdentityAdapter(UserEntityRepository userEntityRepository, UserIdentityMapper userIdentityMapper){
        return new UserIdentityAdapter(userEntityRepository,userIdentityMapper);
    }

    @Bean
    public OrganizationOrganizationEmployeeIdentityAdapter organizationOrganizationEmployeeIdentityAdapter(
            EmployeeAdminEntityRepository employeeAdminEntityRepository,
            OrganizationEmployeeIdentityMapper organizationEmployeeIdentityMapper
    ){
       return new OrganizationOrganizationEmployeeIdentityAdapter(employeeAdminEntityRepository,organizationEmployeeIdentityMapper);
    }

    @Bean
    public TokenGeneratorAdapter tokenGeneratorAdapter(){
        return new TokenGeneratorAdapter();
    }

}
