package africa.nkwadoma.nkwadoma.domain.service.loanManagement;

import africa.nkwadoma.nkwadoma.application.ports.output.education.LoaneeOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.UserIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.loan.LoanOfferOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.loan.LoanRequestOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.loan.LoaneeLoanAccountOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.IdentityRole;
import africa.nkwadoma.nkwadoma.domain.enums.loanEnums.*;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import africa.nkwadoma.nkwadoma.domain.model.loan.LoanOffer;
import africa.nkwadoma.nkwadoma.domain.model.loan.LoanRequest;
import africa.nkwadoma.nkwadoma.domain.model.loan.Loanee;
import africa.nkwadoma.nkwadoma.domain.model.loan.LoaneeLoanAccount;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.mapper.LoanOfferMapper;
import africa.nkwadoma.nkwadoma.test.data.TestData;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@Slf4j
@ExtendWith(MockitoExtension.class)
public class LoanOfferServiceTest {
    @InjectMocks
    private LoanService loanService;
    @Mock
    private LoanRequestOutputPort loanRequestOutputPort;
    @Mock
    private LoanOfferOutputPort loanOfferOutputPort;
    private LoanOffer loanOffer;
    private LoanRequest loanRequest;
    private Loanee loanee;
    private UserIdentity userIdentity;
    private UserIdentity userIdentityLoanee;
    private String mockId = "96f2eb2b-1a78-4838-b5d8-66e95cc9ae9f";
    private String mockId2 = "96f2eb2b-1a78-4838-b5d8-76e95cc9ae9f";
    @Mock
    private UserIdentityOutputPort userIdentityOutputPort;
    private LoaneeLoanAccount loaneeLoanAccount;
    @Mock
    private LoanOfferMapper loanOfferMapper;
    @Mock
    private LoaneeLoanAccountOutputPort loaneeLoanAccountOutputPort;
    @Mock
    private LoaneeOutputPort loaneeOutputPort;


    @BeforeEach
    void setUpLoanOffer() {
        userIdentity = UserIdentity.builder().id(mockId).firstName("qudus").lastName("ade").email("qudusa55@gmail.com").build();
        loanee = Loanee.builder().id(mockId).userIdentity(userIdentity).build();
        loanRequest = LoanRequest.builder().id(mockId).loanAmountRequested(BigDecimal.valueOf(340000))
                .status(LoanRequestStatus.APPROVED).referredBy("Brown Hills Institute").loanee(loanee)
                .dateTimeApproved(LocalDateTime.now()).build();
        loanOffer = new LoanOffer();
        loanOffer.setDateTimeOffered(LocalDateTime.now());
        loanOffer.setLoanRequest(loanRequest);
        loanOffer.setLoanOfferStatus(LoanOfferStatus.OFFERED);
        loanOffer.setLoanee(loanee);
        loanOffer.setUserId(mockId);
        loanOffer.setId(mockId);

        loaneeLoanAccount = TestData.createLoaneeLoanAccount(LoanStatus.AWAITING_DISBURSAL, AccountStatus.NEW,loanOffer.getLoaneeId());
        userIdentity = TestData.createTestUserIdentity("qudusa55@gmail.com");
    }

    @Test
    void createLoanOfferWithValidLoanRequestId() {
        LoanOffer cretedLoanOffer = new LoanOffer();
        try {
            when(loanOfferOutputPort.save(any(LoanOffer.class))).thenReturn(loanOffer);
            cretedLoanOffer = loanService.createLoanOffer(loanRequest);
        } catch (MeedlException exception) {
            log.error(exception.getMessage());
        }
        assertEquals(LoanRequestStatus.APPROVED, cretedLoanOffer.getLoanRequest().getStatus());
        assertEquals(LoanOfferStatus.OFFERED, cretedLoanOffer.getLoanOfferStatus());
    }

    @Test
    void createLoanOfferWithUnApprovedLoanRequest() {
        loanRequest.setId(mockId);
        loanRequest.setStatus(LoanRequestStatus.DECLINED);
        assertThrows(MeedlException.class, () -> loanService.createLoanOffer(loanRequest));
    }

    @Test
    void loaneeCannotAcceptLoanOfferThatNotAssignedToLoanee() throws MeedlException {
        loanOffer.setLoanee(Loanee.builder().id(mockId2).build());
        when(loanOfferOutputPort.findLoanOfferById(mockId)).thenReturn(loanOffer);
        when(loaneeOutputPort.findByUserId(any())).thenReturn(Optional.ofNullable(loanee));
        assertThrows(MeedlException.class, () -> loanService.acceptLoanOffer(loanOffer));
    }

    @Test
    void loaneeLoanAccountIsCreatedIfLoanOfferIsAccepted(){
        loanOffer.setLoaneeResponse(LoanDecision.ACCEPTED);
        try {
            when(loanOfferOutputPort.findLoanOfferById(mockId)).thenReturn(loanOffer);
            when(loaneeOutputPort.findByUserId(mockId)).thenReturn(Optional.ofNullable(loanee));
            when(loanOfferOutputPort.save(loanOffer)).thenReturn(loanOffer);
            when(loaneeLoanAccountOutputPort.save(any())).thenReturn(loaneeLoanAccount);
            when(loaneeLoanAccountOutputPort.findByLoaneeId(mockId)).thenReturn(null);
            loaneeLoanAccount = loanService.acceptLoanOffer(loanOffer);
        }catch (MeedlException exception){
            log.error(exception.getMessage());
        }
        assertEquals(AccountStatus.NEW,loaneeLoanAccount.getAccountStatus());
        assertEquals(LoanStatus.AWAITING_DISBURSAL,loaneeLoanAccount.getLoanStatus());
    }

    @Test
    void loaneeLoanAccountIsNotCreatedIfLoanOfferIsDeclined(){
        loanOffer.setLoaneeResponse(LoanDecision.DECLINED);
        try {
            when(loanOfferOutputPort.findLoanOfferById(mockId)).thenReturn(loanOffer);
            when(loaneeOutputPort.findByUserId(mockId)).thenReturn(Optional.ofNullable(loanee));
            when(loanOfferOutputPort.save(loanOffer)).thenReturn(loanOffer);
            loaneeLoanAccount = loanService.acceptLoanOffer(loanOffer);
        }catch (MeedlException exception){
            log.error(exception.getMessage());
        }
        assertNull(loaneeLoanAccount);
    }

}

