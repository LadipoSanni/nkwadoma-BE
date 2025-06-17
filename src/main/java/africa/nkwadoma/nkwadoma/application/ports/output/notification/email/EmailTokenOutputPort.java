package africa.nkwadoma.nkwadoma.application.ports.output.notification.email;

import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;

public interface EmailTokenOutputPort {

    String generateToken(String email) throws MeedlException;

    String generateToken(String email, String id) throws MeedlException;

    String decodeJWT(String token);

    String decodeJWTGetEmail(String token) throws MeedlException;

    String decodeJWTGetId(String token) throws MeedlException;
}
