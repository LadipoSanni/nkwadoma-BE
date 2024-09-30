package africa.nkwadoma.nkwadoma.application.ports.output.email;

import africa.nkwadoma.nkwadoma.domain.exceptions.MiddlException;

public interface TokenGeneratorOutputPort {
    String generateToken(String email)  throws MiddlException;
    String decodeJWT(String token);
}
