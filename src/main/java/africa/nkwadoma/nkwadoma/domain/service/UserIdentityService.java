package africa.nkwadoma.nkwadoma.domain.service;

import africa.nkwadoma.nkwadoma.application.ports.input.CreateUserUseCase;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.IdentityManagerOutPutPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.UserIdentityOutputPort;
import africa.nkwadoma.nkwadoma.domain.exceptions.MiddlException;
import africa.nkwadoma.nkwadoma.domain.model.UserIdentity;
import africa.nkwadoma.nkwadoma.infrastructure.exceptions.InfrastructureException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static africa.nkwadoma.nkwadoma.domain.constants.MiddlMessages.EMAIL_ALREADY_EXISTS;

@Service
@RequiredArgsConstructor
public class UserIdentityService implements CreateUserUseCase {
    private final UserIdentityOutputPort userIdentityOutputPort;
    private final IdentityManagerOutPutPort identityManagerOutPutPort;

    @Override
    public UserIdentity createUser(UserIdentity userIdentity) throws MiddlException {
      try{
          LocalDateTime localDateTime = LocalDateTime.now();
          if (identityManagerOutPutPort.getUserByEmail(userIdentity.getEmail()).isPresent()){
              throw new MiddlException(EMAIL_ALREADY_EXISTS);
          }
          userIdentity = identityManagerOutPutPort.createUser(userIdentity);
          userIdentity.setCreatedAt(localDateTime.toString());
          userIdentityOutputPort.save(userIdentity);
          return userIdentity;
      }catch (MiddlException exception){
          throw new MiddlException(exception.getMessage());
      }

    }
}
