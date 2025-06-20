package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.creditregistry;

import africa.nkwadoma.nkwadoma.application.ports.output.creditregistry.CreditRegistryOutputPort;
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

import java.util.*;
import java.util.regex.Pattern;

import static africa.nkwadoma.nkwadoma.infrastructure.adapters.output.creditregistry.CreditRegistryConstant.*;

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
//        log.info("Credit registry url : {}", url);

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
        MeedlValidator.validateBvnOrNin(bvn, "Invalid bvn provided");
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
            log.info("No customer found with BVN {} in credit registry because the response list is null or empty", bvn);
            return 0;
        }
        List<String> registryIds = creditRegistryFindDetailResponse.getSearchResult().stream()
                                    .map(CustomerDetail::getRegistryID)
                                    .toList();
        if (registryIds.isEmpty()){
            return 0;
        }
        return getCreditScoreWithRegistryId(registryIds, sessionCode);
    }
    @Override
    public int getCreditScoreWithRegistryId(List<String> registryIds, String sessionCode) throws MeedlException {
        validateSessionCode(sessionCode);

        CreditScoreRequest requestBody = new CreditScoreRequest();
        requestBody.setSessionCode(sessionCode);
        requestBody.setCustomerRegistryIDList(registryIds);
        requestBody.setEnquiryReason("KYCCheck");
        log.info("Request body for get credit score: {}", requestBody);

        HttpEntity<CreditScoreRequest> entity = new HttpEntity<>(requestBody, headers);
        ResponseEntity<CreditScoreResponse> responseEntity = ResponseEntity.ofNullable(new CreditScoreResponse());
        try {
            responseEntity = restTemplate.exchange(
                    url + "/GetReport202",
                    HttpMethod.POST,
                    entity,
                    CreditScoreResponse.class);
            log.info("Response from credit registry API entity {}",responseEntity);
            log.info("Response from credit registry API {}", Objects.requireNonNull(responseEntity.getBody()));
        } catch (HttpServerErrorException | ResourceAccessException ex) {
            log.error("Credit registry server error {}", ex.getMessage());
        }
        return getCreditScore(Objects.requireNonNull(responseEntity.getBody()).getSmartScores());
    }

    private int getCreditScore(List<CreditScoreResponse.SMARTScore> smartScores) {
        log.info("credit score retrieval {}", Objects.requireNonNull(smartScores));
        long distinctScoreCount = smartScores.stream()
                .map(CreditScoreResponse.SMARTScore::getGenericScore)
                .peek(value -> log.info("{}", value))
                .distinct()
                .count();
        if (distinctScoreCount == 1){
            return smartScores.get(0).getGenericScore();
        }else {
            return (int) smartScores.stream()
                    .mapToInt(CreditScoreResponse.SMARTScore::getGenericScore)
                    .average()
                    .orElse(0);
        }
    }

    @Override
    public CreditRegistryFindDetailResponse getCustomerDetails(String bvn, String sessionCode) throws MeedlException {
        validateSessionCode(sessionCode);
        MeedlValidator.validateBvnOrNin(bvn, "Invalid bvn provided");

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
            log.info("Response from credit registry API find summary : {}", Objects.requireNonNull(responseEntity.getBody()));
        } catch (HttpServerErrorException | ResourceAccessException ex) {
            log.error("Credit registry server error {}", ex.getMessage());
        }
        return responseEntity.getBody();
    }

    private void validateSessionCode(String sessionCode) throws MeedlException {
        MeedlValidator.validateDataElement(sessionCode, "No session available at the moment. Please generate new session code, or contact the administrator");
        String regex = "^\\d{6}$";

        boolean isValid = Pattern.matches(regex, sessionCode);
        if (!isValid) {
            log.error("Invalid session code {}", sessionCode);
            throw new MeedlException("Invalid login session");
        }
    }
    private void validateRegistryId(String registryId) throws MeedlException {
        MeedlValidator.validateDataElement(registryId, "Registry ID not valid.");
        String regex = "^\\d{18}$";

        boolean isValid = Pattern.matches(regex, registryId);
        if (!isValid) {
            log.error("Invalid registryId {}", registryId);
            throw new MeedlException("Invalid Registration Id");
        }
    }


}
