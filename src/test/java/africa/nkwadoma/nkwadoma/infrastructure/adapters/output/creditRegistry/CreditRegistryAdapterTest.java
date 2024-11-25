package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.creditRegistry;

import africa.nkwadoma.nkwadoma.application.ports.output.creditRegistry.CreditRegistryOutputPort;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.data.response.CreditRegistryFindDetailResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
class CreditRegistryAdapterTest {
    @Autowired
    private CreditRegistryOutputPort creditRegistryOutputPort;
    @Test
    void getSessionCode(){
        String sessionCode = creditRegistryOutputPort.getSessionCode();
        assertNotNull(sessionCode);
        assertFalse(sessionCode.isEmpty());
        assertTrue(StringUtils.isNotBlank(sessionCode));
    }
    @Test
    void geCreditScoreWithBvn() {
        String searchQuery = "22200006749";
        int creditScore = 0;
        try {
            creditScore = creditRegistryOutputPort.getCreditScore(searchQuery);
        } catch (MeedlException e) {
            log.error("Error getting credit score {}", e.getMessage());
        }
        log.info("Credit score {}", creditScore);
        assertTrue(creditScore > 0);
    }
    @Test
    void geCreditScoreWithBvnThatDoesNotExist() {
        String searchQuery = "22200006741";
        int creditScore = 0;
        try {
            creditScore = creditRegistryOutputPort.getCreditScore(searchQuery);
        } catch (MeedlException e) {
            log.error("Error getting credit score {}", e.getMessage());
        }
        log.info("Credit score {}", creditScore);
        assertTrue(creditScore == 0);
    }
    @Test
    void geCreditScoreWithRegistryId() {
        String sessionCode = creditRegistryOutputPort.getSessionCode();
        String searchQuery = "735756718704397361";
        int creditScore = 0;
        try {
            creditScore = creditRegistryOutputPort.getCreditScore(searchQuery, sessionCode);
        } catch (MeedlException e) {
            log.error("Error getting credit score {}", e.getMessage());
        }
        log.info("Credit score {}", creditScore);
        assertTrue(creditScore >= 0);
    }
    @ParameterizedTest
    @ValueSource(strings = {StringUtils.EMPTY, StringUtils.SPACE, })
    void getCustomerDetailsWithInvalidSearchQuery(String searchQuery){
    assertThrows(MeedlException.class, ()-> creditRegistryOutputPort.getCustomerDetails(searchQuery, "sessionCode"));

    }
    @Test
    void getCustomerDetailsWithBvn() {
        String searchQuery = "22200085865";
        CreditRegistryFindDetailResponse findDetailsResponse = null;
        try {
            String sessionCode = creditRegistryOutputPort.getSessionCode();
            findDetailsResponse = creditRegistryOutputPort.getCustomerDetails(searchQuery, sessionCode);
        } catch (MeedlException e) {
            log.info("Could not find customer details {}", e.getMessage());
        }
        assertNotNull(findDetailsResponse);
        assertNotNull(findDetailsResponse.getSearchResult());
        assertFalse(findDetailsResponse.getSearchResult().isEmpty());
        assertTrue(findDetailsResponse.getSearchResult().stream().allMatch(customerDetail -> customerDetail.getRegistryID() != null));
        log.info("{}",findDetailsResponse.getSearchResult());
    }
    @Test
    void getCustomerDetailsWithNoneExistingDetails() {
        String searchQuery = "aofijhivjfsdnik";
        CreditRegistryFindDetailResponse findDetailsResponse = null;
        try {
            String sessionCode = creditRegistryOutputPort.getSessionCode();
            findDetailsResponse = creditRegistryOutputPort.getCustomerDetails(searchQuery, sessionCode);
        } catch (MeedlException e) {
            log.info("Could not find customer details {}", e.getMessage());
        }
        assertNotNull(findDetailsResponse);
        assertNotNull(findDetailsResponse.getSearchResult());
        assertTrue(findDetailsResponse.getSearchResult().isEmpty());
    }
}