package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.identityVerificationManager;

import africa.nkwadoma.nkwadoma.application.ports.output.identity.IdentityVerificationOutputPort;
import africa.nkwadoma.nkwadoma.domain.model.identity.IdentityVerification;
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
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;


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
        String verificationResult = getNinVerificationResponse(responseEntity.getBody());
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
        ResponseEntity<PremblyNinResponse> responseEntity = ResponseEntity.ofNullable(new PremblyNinResponse());
        try {
            responseEntity = restTemplate.exchange(url, HttpMethod.POST, entity, PremblyNinResponse.class);
        } catch (HttpServerErrorException ex) {
            log.info("Prembly server error {}", ex.getMessage());
        }
        return responseEntity;
    }

    private String getNinVerificationResponse(PremblyNinResponse response) throws PremblyVerificationException {
        String responseMessage = StringUtils.EMPTY;
        if (response == null || response.getNinData() == null) {
            throw new PremblyVerificationException(PremblyVerificationMessage.PREMBLY_UNAVAILABLE.getValue());
        }
        switch (response.getResponseCode()) {
            case "00" -> {
                responseMessage = PremblyVerificationMessage.NIN_VERIFIED.getValue();
            }
            case "01" -> responseMessage = PremblyVerificationMessage.NIN_NOT_FOUND.getValue();
            case "02" -> {
                log.warn("{} : {}", PremblyAdapter.class.getName(), PremblyVerificationMessage.SERVICE_UNAVAILABLE.getValue());
                responseMessage = PremblyVerificationMessage.SERVICE_UNAVAILABLE.getValue();
            }
            case "03" -> {
                log.warn("{} : {}", PremblyAdapter.class.getName(), PremblyVerificationMessage.INSUFFICIENT_WALLET_BALANCE.getValue());
                responseMessage = PremblyVerificationMessage.INSUFFICIENT_WALLET_BALANCE.getValue();
            }
        }
        return responseMessage;
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