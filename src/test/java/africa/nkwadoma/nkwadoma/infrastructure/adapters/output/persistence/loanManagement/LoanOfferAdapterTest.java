package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.loanManagement;

import africa.nkwadoma.nkwadoma.application.ports.output.education.LoaneeOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.UserIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.loan.LoanOfferOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.loan.LoanReferralOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.loan.LoanRequestOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.loan.LoaneeLoanDetailsOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.IdentityRole;
import africa.nkwadoma.nkwadoma.domain.enums.loanEnums.LoanOfferStatus;
import africa.nkwadoma.nkwadoma.domain.enums.loanEnums.LoanReferralStatus;
import africa.nkwadoma.nkwadoma.domain.enums.loanEnums.LoanRequestStatus;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import africa.nkwadoma.nkwadoma.domain.model.loan.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.loan.LoanOfferEntityRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Slf4j
public class LoanOfferAdapterTest {
    @Autowired
    private LoanOfferOutputPort loanOfferOutputPort;
    @Autowired
    private UserIdentityOutputPort userIdentityOutputPort;
    @Autowired
    private LoaneeOutputPort loaneeOutputPort;
    @Autowired
    private LoaneeLoanDetailsOutputPort loaneeLoanDetailsOutputPort;
    @Autowired
    private LoanReferralOutputPort loanReferralOutputPort;
    @Autowired
    private LoanOfferEntityRepository loanOfferEntityRepository;
    @Autowired
    private LoanRequestOutputPort loanRequestOutputPort;
    private LoanOffer loanOffer;
    private UserIdentity userIdentity;
    private Loanee loanee;
    private LoanReferral loanReferral;
    private LoaneeLoanDetail loaneeLoanDetail;
    private LoanRequest loanRequest;
    private String loanOfferId;
    private String loanRequestId;
    private int pageSize = 1;
    private int pageNumber = 0;


    @BeforeAll
    void setUp() {
        userIdentity = UserIdentity.builder().id("e8cfea66-440d-43bc-84f2-9f1c55029009").firstName("Adeshina").
                lastName("Qudus").email("test@example.com").role(IdentityRole.LOANEE).
                createdBy("96f2eb2b-1a78-4838-b5d8-66e95cc9ae9f").build();
        loaneeLoanDetail = LoaneeLoanDetail.builder().amountRequested(BigDecimal.valueOf(9000000.00)).
                initialDeposit(BigDecimal.valueOf(3000000.00)).build();
        loanee = Loanee.builder().userIdentity(userIdentity).
                cohortId("fdc36020-9389-43d2-938d-fd317a870916")
                .loaneeLoanDetail(loaneeLoanDetail).build();
        try {
            userIdentity = userIdentityOutputPort.save(userIdentity);
            loaneeLoanDetail = loaneeLoanDetailsOutputPort.save(loanee.getLoaneeLoanDetail());
            loanee.setLoaneeLoanDetail(loaneeLoanDetail);
            loanee.setUserIdentity(userIdentity);
            loanee = loaneeOutputPort.save(loanee);
            loanReferral = LoanReferral.builder().loanee(loanee).loanReferralStatus(LoanReferralStatus.ACCEPTED).build();
            loanReferral = loanReferralOutputPort.save(loanReferral);
            loanRequest = LoanRequest.builder().loanAmountRequested(loanReferral.getLoanee().getLoaneeLoanDetail().getAmountRequested())
                    .status(LoanRequestStatus.APPROVED).referredBy("Brown Hills Institute").loanee(loanee).createdDate(LocalDateTime.now()).
                    loaneeId("88ee2dd8-df66-4f67-b718-dfd1635f8053").loanReferralId(loanReferral.getId()).cohortId("3012eabb-4cc7-4f48-bae9-04c0056518f0")
                    .dateTimeApproved(LocalDateTime.now()).build();
            loanRequest = loanRequestOutputPort.save(loanRequest);
            log.info("Loan request saved: {}", loanRequest);
            assertNotNull(loanRequest.getId());
            loanRequestId = loanRequest.getId();
        } catch (MeedlException exception) {
            log.error(exception.getMessage());
        }
    }

    @BeforeEach
    void setUpLoanOffer() {
        loanOffer = new LoanOffer();
        loanOffer.setLoanOfferStatus(LoanOfferStatus.OFFERED);
        loanOffer.setLoanee(loanee);
        loanOffer.setLoanRequest(loanRequest);
    }

    @Test
    void saveNullLoanOffer() {
        assertThrows(MeedlException.class, () -> loanOfferOutputPort.save(null));
    }

    @ParameterizedTest
    @ValueSource(strings = {"jduhjdkbkkvkgkd", StringUtils.EMPTY, StringUtils.SPACE})
    void saveLoanOfferWithInvalidLoanRequestId(String loanRequestId) {
        loanOffer.getLoanRequest().setId(loanRequestId);
        assertThrows(MeedlException.class, () -> loanOfferOutputPort.save(loanOffer));
    }

    @Order(1)
    @Test
    void saveLoanOffer() {
        LoanOffer savedLoanOffer = new LoanOffer();
        try {
            savedLoanOffer = loanOfferOutputPort.save(loanOffer);
            loanOfferId = savedLoanOffer.getId();
        } catch (MeedlException exception) {
            log.error(exception.getMessage());
        }
        assertEquals(savedLoanOffer.getLoanRequest().getId(), loanRequest.getId());
        assertEquals(savedLoanOffer.getLoanRequest().getLoanAmountRequested(), loanRequest.getLoanAmountRequested());
        assertEquals(savedLoanOffer.getLoanRequest().getLoanReferralStatus(), loanRequest.getLoanReferralStatus());
    }

    @ParameterizedTest
    @ValueSource(strings = {StringUtils.EMPTY,StringUtils.SPACE,"kjkjdjksjk"})
    void findLoanOfferWithInvalidId(String invalidId){
        assertThrows(MeedlException.class,()-> loanOfferOutputPort.findLoanOfferById(invalidId));
    }

    @Test
    void findLoanOfferWithNullId(){
        assertThrows(MeedlException.class,()-> loanOfferOutputPort.findLoanOfferById(null));
    }

    @Order(2)
    @Test
    void findLoanOfferById(){
        LoanOffer offer = new LoanOffer();
        try{
            offer = loanOfferOutputPort.findLoanOfferById(loanOfferId);
        }catch (MeedlException exception){
            log.error(exception.getMessage());
        }
        assertEquals(offer.getId(), loanOfferId);
    }

    @Order(2)
    @Test
    void viewAllLoanOffersByOrganizationAdmin(){
        Page<LoanOffer> loanOffers = null;
        try {
            loanOffers = loanOfferOutputPort.findAllLoanOffers( pageSize, pageNumber);
        }catch (MeedlException exception){
            log.info(exception.getMessage());
        }
        assertEquals(1,loanOffers.getSize());
    }



    @AfterAll
    void cleanUp() {
        try {
            loanReferralOutputPort.deleteLoanReferral(loanReferral.getId());
            loanOfferOutputPort.deleteLoanOfferById(loanOfferId);
            loanRequestOutputPort.deleteLoanRequestById(loanRequestId);
            loaneeOutputPort.deleteLoanee(loanee.getId());
            userIdentityOutputPort.deleteUserById(userIdentity.getId());
            loaneeLoanDetailsOutputPort.delete(loaneeLoanDetail.getId());
        } catch (MeedlException exception) {
            log.error(exception.getMessage());
        }
    }
}