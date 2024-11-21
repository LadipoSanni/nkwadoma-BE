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
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

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


    @BeforeAll
    void setUp() {
         userIdentity = UserIdentity.builder().id("96f2eb2b-1a78-4838-b5d8-66e95cc9ae9f").firstName("Adeshina").
                lastName("Qudus").email("test@example.com").role(IdentityRole.LOANEE).
                createdBy("96f2eb2b-1a78-4838-b5d8-66e95cc9ae9f").build();

         loanee = Loanee.builder().userIdentity(userIdentity).
                cohortId("3a6d1124-1349-4f5b-831a-ac269369a90f").createdBy(userIdentity.getCreatedBy()).
                loaneeLoanDetail(LoaneeLoanDetail.builder().amountRequested(BigDecimal.valueOf(9000000.00)).
                        initialDeposit(BigDecimal.valueOf(3000000.00)).build()).build();
         try{
             userIdentity = userIdentityOutputPort.save(loanee.getUserIdentity());
             loaneeLoanDetail = loaneeLoanDetailsOutputPort.save(loanee.getLoaneeLoanDetail());
             loanee.setLoaneeLoanDetail(loaneeLoanDetail);
             loanee.setUserIdentity(userIdentity);
             loanee = loaneeOutputPort.save(loanee);
             loanReferral = LoanReferral.builder().loanee(loanee).loanReferralStatus(LoanReferralStatus.ACCEPTED).build();
             loanReferral = loanReferralOutputPort.saveLoanReferral(loanReferral);
             loanRequest = LoanRequest.builder().loanAmountRequested(loanReferral.getLoanee().getLoaneeLoanDetail().getAmountRequested())
                     .status(LoanRequestStatus.DECLINED).referredBy("Brown Hills Institute").loanee(loanee)
                     .dateTimeApproved(LocalDateTime.now()).build();
             loanRequest = loanRequestOutputPort.save(loanRequest);
         }catch (MeedlException exception){
             log.error(exception.getMessage());
         }
    }

    @BeforeEach
    void setUpLoanOffer() {
        loanOffer = new LoanOffer();
        loanOffer.setLoanRequestId(loanRequest.getId());
        loanOffer.setLoanOfferStatus(LoanOfferStatus.OFFERED);
        loanOffer.setLoanAmountRequested(loanReferral.getLoanee().getLoaneeLoanDetail().getAmountRequested());
        loanOffer.setLoanee(loanee);
        loanOffer.setReferredBy(loanRequest.getReferredBy());
        loanOffer.setDateTimeApproved(loanRequest.getDateTimeApproved());
        loanOffer.setLoanRequestStatus(loanRequest.getStatus());
        loanOffer.setLoanReferralStatus(loanRequest.getLoanReferralStatus());
    }

    @Test
    void saveNullLoanOffer(){
       assertThrows(MeedlException.class, () -> loanOfferOutputPort.save(null));
    }

    @ParameterizedTest
    @ValueSource( strings = {"jduhjdkbkkvkgkd"})
    void saveLoanOfferWithInvalidLoanRequestId(String loanRequestId){
        loanOffer.setLoanRequestId(loanRequestId);
        assertThrows(MeedlException.class, () -> loanOfferOutputPort.save(loanOffer));
    }

    @ParameterizedTest
    @ValueSource(strings = {StringUtils.EMPTY,StringUtils.SPACE})
    void saveLoanOfferWithEmptyLoanRequestId(String loanRequestId){
        loanOffer.setLoanRequestId(loanRequestId);
        assertThrows(MeedlException.class, () -> loanOfferOutputPort.save(loanOffer));
    }

    @Order(1)
    @Test
    void saveLoanOffer(){
        LoanOffer savedLoanOffer = new LoanOffer();
        try{
            savedLoanOffer = loanOfferOutputPort.save(loanOffer);
            loanOfferId = savedLoanOffer.getId();
        } catch (MeedlException exception){
            log.error(exception.getMessage());
        }
        assertEquals(savedLoanOffer.getLoanRequestId(), loanOffer.getLoanRequestId());
        assertEquals(savedLoanOffer.getLoanAmountRequested(), loanOffer.getLoanAmountRequested());
        assertEquals(savedLoanOffer.getLoanReferralStatus(), loanOffer.getLoanReferralStatus());
    }


    @AfterAll
    void cleanUp(){
        try {
            loanReferralOutputPort.deleteLoanReferral(loanReferral.getId());
            loanOfferEntityRepository.deleteById(loanOfferId);
            loanRequestOutputPort.deleteLoanRequestById(loanRequest.getId());
            loaneeOutputPort.deleteLoanee(loanee.getId());
            userIdentityOutputPort.deleteUserById(userIdentity.getId());
            loaneeLoanDetailsOutputPort.delete(loaneeLoanDetail.getId());
        } catch (MeedlException exception) {
            log.error(exception.getMessage());
        }
    }

}
