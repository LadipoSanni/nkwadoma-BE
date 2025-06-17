package africa.nkwadoma.nkwadoma.application.ports.output.aes;

import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;

public interface AesOutputPort {
    String decryptAES(String encryptedData, String message) throws MeedlException;

    String encryptAES(String plainText) throws MeedlException;
}
