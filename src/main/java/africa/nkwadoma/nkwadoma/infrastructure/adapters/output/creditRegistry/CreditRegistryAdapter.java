package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.creditRegistry;

import africa.nkwadoma.nkwadoma.application.ports.output.creditRegistry.CreditRegistryOutputPort;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.data.request.CreditScoreRequest;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.data.response.CreditRegistryFindDetailResponse;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.data.response.CreditRegistryLoginResponse;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.data.response.CreditScoreResponse;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.data.response.CustomerDetail;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

import static africa.nkwadoma.nkwadoma.infrastructure.enums.CreditRegistryConstant.*;

@Slf4j
@Component
public class CreditRegistryAdapter implements CreditRegistryOutputPort {
    @Value("${CREDIT_REGISTRY_EMAIL}")
    private String EmailAddress;
    @Value("${CREDIT_REGISTRY_SUBSCRIBER_ID}")
    private String SubscriberID;
    @Value("${CREDIT_REGISTRY_PASSWORD}")
    private String Password;
    @Value("${CREDIT_REGISTRY_BASE_URL}")
    private String url;
    private final RestTemplate restTemplate = new RestTemplate();
    private final HttpHeaders headers =  new HttpHeaders();

    @Override
    public String getSessionCode() {
        Map<String, String> formData = new HashMap<>();
        formData.put(EMAIL.getValue(), EmailAddress);
        formData.put(SUBSCRIBER_ID.getValue(), SubscriberID);
        formData.put(PASSWORD.getValue(), Password);

        HttpEntity<Map<String, String>> entity = new HttpEntity<>(formData, headers);
        ResponseEntity<CreditRegistryLoginResponse> responseEntity = ResponseEntity.ofNullable(new CreditRegistryLoginResponse());
        try {
            responseEntity = restTemplate.exchange(
                    url + "/Login",
                    HttpMethod.POST,
                    entity,
                    CreditRegistryLoginResponse.class);
            log.info("Response from credit registry API {}", Objects.requireNonNull(responseEntity.getBody()).getSessionCode());
        } catch (HttpServerErrorException | ResourceAccessException ex) {
            log.error("Credit registry server error {}", ex.getMessage());
        }
        return Objects.requireNonNull(responseEntity.getBody()).getSessionCode();
    }
    @Override
    public int getCreditScoreWithBvn(String bvn) throws MeedlException {
        MeedlValidator.validateBvn(bvn);
        String sessionCode = getSessionCode();
        CreditRegistryFindDetailResponse creditRegistryFindDetailResponse;
        try {
            creditRegistryFindDetailResponse = getCustomerDetails(bvn, sessionCode);
        } catch (MeedlException e) {
            log.error("Error getting customer details {}", e.getMessage());
            throw new MeedlException("Error getting customer details {}" + e.getMessage());
        }
        if (creditRegistryFindDetailResponse.getSearchResult() == null
                || creditRegistryFindDetailResponse.getSearchResult().isEmpty()){
            log.info("No customer found with BVN {} in credit registry", bvn);
            return 0;
        }
        String registryId = creditRegistryFindDetailResponse.getSearchResult().stream()
                                    .filter(customerDetail -> bvn.equals(customerDetail.getBvn()))
                                    .map(CustomerDetail::getRegistryID)
                                    .findFirst().orElse("0");
        if (registryId.equals("0")){
            return 0;
        }
        return getCreditScoreWithRegistryId(registryId, sessionCode);
    }
    @Override
    public int getCreditScoreWithRegistryId(String registryId, String sessionCode) throws MeedlException {
        validateSessionCode(sessionCode);
        validateRegistryId(registryId);

        CreditScoreRequest requestBody = new CreditScoreRequest();
        requestBody.setSessionCode(sessionCode);
        requestBody.setCustomerRegistryIDList(Collections.singletonList(registryId));
        requestBody.setEnquiryReason("KYCCheck");
        log.info("Request body: {}", requestBody);

        HttpEntity<CreditScoreRequest> entity = new HttpEntity<>(requestBody, headers);
        ResponseEntity<CreditScoreResponse> responseEntity = ResponseEntity.ofNullable(new CreditScoreResponse());
        try {
            responseEntity = restTemplate.exchange(
                    url + "/GetReport202",
                    HttpMethod.POST,
                    entity,
                    CreditScoreResponse.class);
            log.info("Response from credit registry API {}", Objects.requireNonNull(responseEntity.getBody()));
        } catch (HttpServerErrorException | ResourceAccessException ex) {
            log.error("Credit registry server error {}", ex.getMessage());
        }
        return Objects.requireNonNull(responseEntity.getBody()).getSmartScores().get(0).getGenericScore();
    }

    @Override
    public CreditRegistryFindDetailResponse getCustomerDetails(String bvn, String sessionCode) throws MeedlException {
        validateSessionCode(sessionCode);
        MeedlValidator.validateBvn(bvn);

        Map<String, String> formData = new HashMap<>();
        formData.put(SESSION_CODE.getValue(), sessionCode);
        formData.put( CUSTOMER_QUERY.getValue(), bvn);
        formData.put(GET_NO_MATCH_REPORT.getValue(), "IfNoMatch");
        formData.put(MIN_RELEVANCE.getValue(), "0");
        formData.put(MAX_RECORDS.getValue(), "0");
        formData.put(ENQUIRY_REASON.getValue(), "KYCCheck");

        HttpEntity<Map<String, String>> entity = new HttpEntity<>(formData, headers);
        ResponseEntity<CreditRegistryFindDetailResponse> responseEntity = ResponseEntity.ofNullable(new CreditRegistryFindDetailResponse());

        try {
            responseEntity = restTemplate.exchange(
                    url + "/FindSummary",
                    HttpMethod.POST,
                    entity,
                    CreditRegistryFindDetailResponse.class);
            log.info("Response from credit registry API {}", Objects.requireNonNull(responseEntity.getBody()));
        } catch (HttpServerErrorException | ResourceAccessException ex) {
            log.error("Credit registry server error {}", ex.getMessage());
        }
        return responseEntity.getBody();
    }
    private void validateSessionCode(String sessionCode) throws MeedlException {
        MeedlValidator.validateDataElement(sessionCode);
        String regex = "^\\d{6}$";

        boolean isValid = Pattern.matches(regex, sessionCode);
        if (!isValid) {
            log.error("Invalid session code {}", sessionCode);
            throw new MeedlException("Invalid login session");
        }
    }
    private void validateRegistryId(String registryId) throws MeedlException {
        MeedlValidator.validateDataElement(registryId);
        String regex = "^\\d{18}$";

        boolean isValid = Pattern.matches(regex, registryId);
        if (!isValid) {
            log.error("Invalid registryId {}", registryId);
            throw new MeedlException("Invalid Registration Id");
        }
    }


}
