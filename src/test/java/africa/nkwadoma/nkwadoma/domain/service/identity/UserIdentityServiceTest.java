package africa.nkwadoma.nkwadoma.domain.service.identity;

import africa.nkwadoma.nkwadoma.application.ports.output.identity.UserIdentityOutputPort;
import africa.nkwadoma.nkwadoma.domain.exceptions.MiddlException;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Slf4j
class UserIdentityServiceTest {
    @Autowired
    private UserIdentityService userIdentityService;
    @Autowired
    private UserIdentityOutputPort userIdentityOutputPort;
    private UserIdentity favour;

    @BeforeEach
    void setUp(){
        favour = new UserIdentity();
        favour.setFirstName("favour");
        favour.setLastName("gabriel");
        favour.setEmail("favour@gmail.com");
        favour.setCreatedBy("d25ac212-d83a-4019-9680-58c5e98e736e");
    }

    @Test
    void inviteColleague(){
       try{
          assertThrows(MiddlException.class,()->userIdentityOutputPort.findById(favour.getId()));
           UserIdentity invitedColleague = userIdentityService.inviteColleague(favour);
           assertNotNull(invitedColleague);
           assertEquals(favour.getFirstName(),invitedColleague.getFirstName());
           assertEquals(favour.getRole(),invitedColleague.getRole());
           UserIdentity foundInvitedColleague = userIdentityOutputPort.findById(favour.getId());
           assertEquals(foundInvitedColleague.getCreatedBy(),invitedColleague.getCreatedBy());
           assertEquals(favour.getLastName(),foundInvitedColleague.getLastName());
       }catch (MiddlException exception){
           log.info("{} {}",exception.getClass().getName(),exception.getMessage());
       }
    }
    @Test
    void inviteColleagueWithInviterIdThatDoesNotExist(){
        favour.setCreatedBy("notexisting");
        assertThrows(MiddlException.class,()->userIdentityService.inviteColleague(favour));
    }

    @Test
    void inviteColleagueWithEmptyInviterId(){
        favour.setCreatedBy(StringUtils.EMPTY);
        assertThrows(MiddlException.class,()->userIdentityService.inviteColleague(favour));
    }

    @Test
    void inviteColleagueWithNullInviterId(){
        favour.setCreatedBy(null);
        assertThrows(MiddlException.class,()->userIdentityService.inviteColleague(favour));
    }
    @Test
    void  inviteColleagueWithNullUserIdentity(){
        assertThrows(MiddlException.class,()->userIdentityService.inviteColleague(new UserIdentity()));
    }
    @Test
    void  inviteColleagueWithEmptyUserIdentity(){
        favour.setFirstName(StringUtils.EMPTY);
        favour.setLastName(StringUtils.EMPTY);
        favour.setEmail(StringUtils.EMPTY);
        favour.setCreatedBy(StringUtils.EMPTY);
        assertThrows(MiddlException.class,()->userIdentityService.inviteColleague(favour));
    }

    @Test
    void inviteColleagueWithDifferentDomainEmail(){
        favour.setEmail("differentdomainemail@yahoo.com");
        assertThrows(MiddlException.class,()->userIdentityService.inviteColleague(favour));
    }





}