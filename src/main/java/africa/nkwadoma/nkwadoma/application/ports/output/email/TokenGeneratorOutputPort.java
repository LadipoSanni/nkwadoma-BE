package africa.nkwadoma.nkwadoma.application.ports.output.email;

import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;

public interface TokenGeneratorOutputPort {
    String generateToken(String email)  throws MeedlException;
    String decodeJWT(String token) throws MeedlException;
}
