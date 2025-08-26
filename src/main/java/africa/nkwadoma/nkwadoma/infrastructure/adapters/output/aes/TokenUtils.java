package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.aes;

import africa.nkwadoma.nkwadoma.application.ports.output.aes.AesOutputPort;
import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import africa.nkwadoma.nkwadoma.domain.validation.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.spec.IvParameterSpec;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Slf4j
@Component
public class TokenUtils implements AesOutputPort {

    @Value("${iv_aes_key}")
    private String ivAESKey;
    @Value("${aes_secret_key}")
    private String AESSecretKey;

    private static final String AES_TRANSFORMATION = "AES/CBC/PKCS5Padding";

    public String decryptAES(String encryptedData, String message) throws MeedlException {
        log.info("Decryption of data started {}", encryptedData);
        MeedlValidator.validateDataElement(encryptedData, "Please provide a valid data.");
        printLastTwoCharactersOfScretValues(AESSecretKey, "AES Secret key");
        printLastTwoCharactersOfScretValues(ivAESKey, "IV AES Key");
        String key = String.format("%-16s", AESSecretKey).substring(0, 16);

        SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES");
        IvParameterSpec ivSpec = new IvParameterSpec(ivAESKey.getBytes(StandardCharsets.UTF_8));
        byte[] decryptedValue = null;
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);

            byte[] decodedValue = Base64.getDecoder().decode(encryptedData);
            decryptedValue = cipher.doFinal(decodedValue);
        } catch (Exception e) {
            log.error("Error processing identity verification. Error decrypting identity with root cause : {}", e.getMessage());
            log.error(message);
            ///Todo notify back office admin
            throw new MeedlException(message);
        }
        log.info("Decryption completed");
        return new String(decryptedValue);
    }

    private void printLastTwoCharactersOfScretValues(String input, String message) {
        if (MeedlValidator.isEmptyString(input)){
            log.warn("{} not provided", message);
            return;
        }
        int length = input.length();
        if (length == 1) {
            log.warn("{} has only one character: {}", message, input);
        } else {
            String lastTwo = input.substring(length - 2, length);
            log.info("{} last two characters: {}. The size {}", message, lastTwo, input.length());
        }
    }
    public String encryptAES(String plainText) throws MeedlException {
        try {
            MeedlValidator.validateDataElement(plainText, "Please provide a valid data.");

            String key = String.format("%-16s", AESSecretKey).substring(0, 16);

            SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES");
            IvParameterSpec ivSpec = new IvParameterSpec(ivAESKey.getBytes(StandardCharsets.UTF_8));

            Cipher cipher = Cipher.getInstance(AES_TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);

            byte[] encryptedBytes = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));

            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            log.error("Error encrypting data. Root cause: {}", e.getMessage());
            throw new MeedlException("Error encrypting data");
        }
    }
}
