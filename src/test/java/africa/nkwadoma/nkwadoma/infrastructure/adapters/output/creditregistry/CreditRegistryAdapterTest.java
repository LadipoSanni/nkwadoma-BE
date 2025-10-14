package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.creditregistry;

import africa.nkwadoma.nkwadoma.application.ports.output.creditregistry.CreditRegistryOutputPort;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.data.response.CreditRegistryFindDetailResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CreditRegistryAdapterTest {
    @Value("${TEST_BVN}")
    String bvnNumber;
    private String registryId ;
    @Autowired
    private CreditRegistryOutputPort creditRegistryOutputPort;
    @Test
    @Order(1)
    void getCustomerDetailsWithBvn() {
        CreditRegistryFindDetailResponse findDetailsResponse = null;
        try {
            String sessionCode = creditRegistryOutputPort.getSessionCode();
            findDetailsResponse = creditRegistryOutputPort.getCustomerDetails(bvnNumber, sessionCode);
        } catch (MeedlException e) {
            log.error("Could not find customer details {}", e.getMessage());
        }
        assertNotNull(findDetailsResponse);
        assertNotNull(findDetailsResponse.getSearchResult());
        assertFalse(findDetailsResponse.getSearchResult().isEmpty());
        assertTrue(findDetailsResponse.getSearchResult().stream().allMatch(customerDetail -> customerDetail.getRegistryID() != null));
        registryId = findDetailsResponse.getSearchResult().get(0).getRegistryID();
        log.info("{}",findDetailsResponse.getSearchResult());
    }
    @Test
    void getSessionCode(){
        String sessionCode = creditRegistryOutputPort.getSessionCode();
        assertNotNull(sessionCode);
        assertFalse(sessionCode.isEmpty());
        assertTrue(StringUtils.isNotBlank(sessionCode));
    }
    @Test
    void geCreditScoreWithBvn() {
        int creditScore = 0;
        try {
            creditScore = creditRegistryOutputPort.getCreditScoreWithBvn(bvnNumber);
        } catch (MeedlException e) {
            log.error("Error getting credit score {}", e.getMessage());
        }
        log.info("Credit score {}", creditScore);
        assertTrue(creditScore > 0);
    }
    @ParameterizedTest
    @ValueSource(strings = {StringUtils.SPACE, StringUtils.EMPTY, "invalid values"})
    void getCreditScoreWithInvalidBvn(String bvn){
        assertThrows(MeedlException.class, ()-> creditRegistryOutputPort.getCreditScoreWithBvn(bvn));
    }
//    @Test
//    void getCreditScoreWithBvnThatDoesNotExist() {
//        String searchQuery = "92500096741";
//        int creditScore = 0;
//        try {
//            creditScore = creditRegistryOutputPort.getCreditScoreWithBvn(searchQuery);
//        } catch (MeedlException e) {
//            log.error("Error getting credit score {}", e.getMessage());
//        }
//        log.info("Credit score {}", creditScore);
//        assertTrue(creditScore == 0);
//    }
//    @ParameterizedTest
    @ValueSource(strings = {StringUtils.SPACE, StringUtils.EMPTY, "invalid values"})
    void getCreditScoreWithInvalidRegistryId(String registryId){
        String sessionCode = creditRegistryOutputPort.getSessionCode();
        assertThrows(MeedlException.class, ()-> creditRegistryOutputPort.getCreditScoreWithRegistryId(Collections.singletonList(registryId), sessionCode));
    }
    @ParameterizedTest
    @ValueSource(strings = {StringUtils.SPACE, StringUtils.EMPTY, "invalid values"})
    void getCreditScoreWithInvalidSessionCode(String invalidSessionCode){
        assertThrows(MeedlException.class, ()-> creditRegistryOutputPort.getCreditScoreWithRegistryId(Collections.singletonList(registryId), invalidSessionCode));
    }
    @Test
    void geCreditScoreWithRegistryId() {
        String sessionCode = creditRegistryOutputPort.getSessionCode();
        int creditScore = 0;
        try {
            creditScore = creditRegistryOutputPort.getCreditScoreWithRegistryId(Collections.singletonList(registryId), sessionCode);
        } catch (MeedlException e) {
            log.error("Error getting credit score {}", e.getMessage());
        }
        log.info("Credit score {}", creditScore);
        assertTrue(creditScore >= 0);
    }
    @ParameterizedTest
    @ValueSource(strings = {StringUtils.EMPTY, StringUtils.SPACE })
    void getCustomerDetailsWithInvalidSearchQuery(String searchQuery){
    assertThrows(MeedlException.class, ()-> creditRegistryOutputPort.getCustomerDetails(searchQuery, "sessionCode"));
    }
}