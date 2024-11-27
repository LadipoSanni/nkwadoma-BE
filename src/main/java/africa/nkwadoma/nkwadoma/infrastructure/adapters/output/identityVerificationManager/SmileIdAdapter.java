package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.identityVerificationManager;

import africa.nkwadoma.nkwadoma.application.ports.output.identity.IdentityVerificationOutputPort;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.identity.IdentityVerification;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.data.response.premblyresponses.PremblyNinResponse;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.data.response.premblyresponses.PremblyResponse;
import africa.nkwadoma.nkwadoma.infrastructure.commons.IdentityVerificationMessage;
import africa.nkwadoma.nkwadoma.infrastructure.enums.prembly.PremblyParameter;
import africa.nkwadoma.nkwadoma.infrastructure.exceptions.InfrastructureException;
import africa.nkwadoma.nkwadoma.infrastructure.exceptions.IdentityVerificationException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

@Slf4j
public class SmileIdAdapter implements IdentityVerificationOutputPort {
    private String smileIdUrl = "null value";
    private String appId = "null value";
    private String apiKey = "null value";

    @Override
    public PremblyResponse verifyIdentity(IdentityVerification identityVerification) throws InfrastructureException {
        return getNinDetails(identityVerification);
    }

    @Override
    public PremblyResponse verifyLiveliness(IdentityVerification identityVerification) {
        return null;
    }

    @Override
    public PremblyResponse verifyBvn(IdentityVerification identityVerification) throws MeedlException {
        return null;
    }

    public PremblyNinResponse getNinDetails(IdentityVerification verificationRequest) throws InfrastructureException {
        validateIdentityVerificationRequest(verificationRequest);
        ResponseEntity<PremblyNinResponse> responseEntity = getIdentityDetailsByNin(verificationRequest);
        String verificationResult = getNinVerificationResponse(responseEntity.getBody());
        log.info("Verification Result smileId: {}", verificationResult);
        log.info("Verification response entity: {}", responseEntity.getBody());
        return responseEntity.getBody();
    }

    private ResponseEntity<PremblyNinResponse> getIdentityDetailsByNin(IdentityVerification verificationRequest) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = getHttpHeaders();
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add(PremblyParameter.NIN_NUMBER.getValue(), verificationRequest.getIdentityId());
        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(formData, headers);
        String url = smileIdUrl.concat(PremblyParameter.NIN_URL.getValue());
        log.info(url);
        ResponseEntity<PremblyNinResponse> responseEntity = ResponseEntity.ofNullable(new PremblyNinResponse());
        try {
            responseEntity = restTemplate.exchange(url, HttpMethod.POST, entity, PremblyNinResponse.class);
        } catch (HttpServerErrorException ex) {
            log.info("Smile id server error {}", ex.getMessage());
        }
        return responseEntity;
    }

    private String getNinVerificationResponse(PremblyNinResponse response) throws IdentityVerificationException {
        String responseMessage = StringUtils.EMPTY;
        if (response == null || response.getNinData() == null) {
            throw new IdentityVerificationException(IdentityVerificationMessage.SMILEID_UNAVAILABLE.getValue());
        }
        switch (response.getResponseCode()) {
            case "00" -> {
                responseMessage = IdentityVerificationMessage.NIN_VERIFIED.getValue();
            }
            case "01" -> responseMessage = IdentityVerificationMessage.NIN_NOT_FOUND.getValue();
            case "02" -> {
                log.warn("{} : {}", SmileIdAdapter.class.getName(), IdentityVerificationMessage.SERVICE_UNAVAILABLE.getValue());
                responseMessage = IdentityVerificationMessage.SERVICE_UNAVAILABLE.getValue();
            }
            case "03" -> {
                log.warn("{} : {}", SmileIdAdapter.class.getName(), IdentityVerificationMessage.INSUFFICIENT_WALLET_BALANCE.getValue());
                responseMessage = IdentityVerificationMessage.INSUFFICIENT_WALLET_BALANCE.getValue();
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
        if (identityVerification == null || StringUtils.isEmpty(identityVerification.getIdentityId()) || StringUtils.isEmpty(identityVerification.getImageUrl())) {
            throw new InfrastructureException("credentials should not be empty");
        }

    }
}
