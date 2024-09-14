package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.identityVerificationManager;

import africa.nkwadoma.nkwadoma.application.ports.output.identity.IdentityVerificationOutputPort;
import africa.nkwadoma.nkwadoma.domain.model.IdentityVerification;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.data.response.PremblyNinResponse;
import africa.nkwadoma.nkwadoma.infrastructure.commons.PremblyVerificationMessage;
import africa.nkwadoma.nkwadoma.infrastructure.enums.PremblyParameter;
import africa.nkwadoma.nkwadoma.infrastructure.exceptions.InfrastructureException;
import africa.nkwadoma.nkwadoma.infrastructure.exceptions.PremblyVerificationException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;


@Service
@Slf4j
public class PremblyAdapter implements IdentityVerificationOutputPort {


    @Value("${PREMBLY_URL}")
    private String premblyUrl;


    @Value("${PREMBLY_APP_ID}")
    private String appId;

    @Value("${PREMBLY_APP_KEY}")
    private String apiKey;

    @Override
    public PremblyNinResponse verifyIdentity(IdentityVerification identityVerification) throws InfrastructureException {
        return getNinDetails(identityVerification);
    }

    public PremblyNinResponse getNinDetails(IdentityVerification verificationRequest) throws InfrastructureException {
        validateIdentityVerificationRequest(verificationRequest);
        ResponseEntity<PremblyNinResponse> responseEntity = getIdentityDetailsByNin(verificationRequest);
        String verificationResult = verifyNinResponse(responseEntity.getBody());
        log.info("Verification Result1: {}", responseEntity.getBody());
        return responseEntity.getBody();
    }

    private ResponseEntity<PremblyNinResponse> getIdentityDetailsByNin(IdentityVerification verificationRequest) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = getHttpHeaders();
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add(PremblyParameter.NIN_NUMBER.getValue(), verificationRequest.getIdentityId());
        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(formData, headers);
        String url = premblyUrl.concat(PremblyParameter.NIN_URL.getValue());
        log.info(url);
        ResponseEntity<PremblyNinResponse> responseEntity = restTemplate.exchange(url, HttpMethod.POST, entity, PremblyNinResponse.class);
        log.info("Response from NIN API {}", responseEntity.getBody());
        return responseEntity;
    }

    private String verifyNinResponse(PremblyNinResponse response) throws PremblyVerificationException {
        if (response != null && response.getNinData() != null) {
            switch (response.getResponseCode()) {
                case "00" -> {
                    return PremblyVerificationMessage.NIN_VERIFIED.getValue();
                }
                case "02" -> throw new PremblyVerificationException(PremblyVerificationMessage.SERVICE_UNAVAILABLE.getValue());
                case "01" -> throw new PremblyVerificationException(PremblyVerificationMessage.NIN_NOT_FOUND.getValue());
            }
        }
        throw new PremblyVerificationException(PremblyVerificationMessage.VERIFICATION_UNSUCCESSFUL.getValue());
    }

    private HttpHeaders getHttpHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set(PremblyParameter.ACCEPT.getValue(), PremblyParameter.APPLICATION_JSON.getValue());
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.add(PremblyParameter.APP_ID.getValue(), appId);
        headers.add(PremblyParameter.API_KEY.getValue(), apiKey);
        return headers;
    }

    private void validateIdentityVerificationRequest(IdentityVerification identityVerification) throws InfrastructureException {
        if (identityVerification == null || StringUtils.isEmpty(identityVerification.getIdentityId()) || StringUtils.isEmpty(identityVerification.getIdentityImage())) {
            throw new InfrastructureException("credentials should not be empty");
        }

    }
}