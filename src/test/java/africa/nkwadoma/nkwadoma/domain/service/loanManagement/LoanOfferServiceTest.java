package africa.nkwadoma.nkwadoma.domain.service.loanManagement;

import africa.nkwadoma.nkwadoma.application.ports.input.loan.*;
import africa.nkwadoma.nkwadoma.application.ports.output.education.*;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.*;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.UserIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.loan.*;
import africa.nkwadoma.nkwadoma.domain.enums.IdentityRole;
import africa.nkwadoma.nkwadoma.domain.enums.constants.loan.LoanOfferMessages;
import africa.nkwadoma.nkwadoma.domain.enums.loanEnums.*;
import africa.nkwadoma.nkwadoma.domain.enums.loanEnums.LoanOfferStatus;
import africa.nkwadoma.nkwadoma.domain.enums.loanEnums.LoanRequestStatus;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.exceptions.loan.LoanOfferException;
import africa.nkwadoma.nkwadoma.domain.model.identity.*;
import africa.nkwadoma.nkwadoma.domain.model.loan.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.mapper.LoanOfferMapper;
import africa.nkwadoma.nkwadoma.test.data.TestData;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
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
    @Mock
    private UserIdentityOutputPort userIdentityOutputPort;
    @Mock
    private OrganizationEmployeeIdentityOutputPort organizationEmployeeIdentityOutputPort;
    private LoanOffer loanOffer;
    private LoaneeLoanDetail loaneeLoanDetail;
    private LoanRequest loanRequest;
    private Loanee loanee;
    private LoanProduct loanProduct;
    private Vendor vendor;
    private UserIdentity userIdentity;
    private UserIdentity userIdentityLoanee;
    private String mockId = "96f2eb2b-1a78-4838-b5d8-66e95cc9ae9f";
    private String loaneeId = "1f732a03-00ad-4187-825d-94969153c3d1";

    private String mockId2 = "96f2eb2b-1a78-4838-b5d8-76e95cc9ae9f";
    private LoaneeLoanAccount loaneeLoanAccount;
    @Mock
    private LoanOfferMapper loanOfferMapper;
    @Mock
    private LoaneeLoanAccountOutputPort loaneeLoanAccountOutputPort;
    @Mock
    private LoaneeOutputPort loaneeOutputPort;
    @Mock
    private OrganizationIdentityOutputPort organizationIdentityOutputPort;
    @Mock
    private LoanMetricsUseCase loanMetricsUseCase;
    @Mock
    private LoaneeLoanBreakDownOutputPort loaneeLoanBreakDownOutputPort;
    @Mock
    private  LoanProductOutputPort loanProductOutputPort;

    @BeforeEach
    void setUpLoanOffer() {
        userIdentity =TestData.createTestUserIdentity("test@example.com");
                        userIdentity.setRole(IdentityRole.PORTFOLIO_MANAGER);
        loaneeLoanDetail = TestData.createTestLoaneeLoanDetail();
        loanee = TestData.createTestLoanee(userIdentity,loaneeLoanDetail);
                 loanee.setId(userIdentity.getId());
        loanRequest = TestData.buildLoanRequest(loanee,loaneeLoanDetail);
        loanOffer = TestData.buildLoanOffer(loanRequest,loanee,mockId);
        vendor = TestData.createTestVendor("vendor");
        loanProduct = TestData.buildTestLoanProduct("loanProduct",vendor);
        loaneeLoanAccount = TestData.createLoaneeLoanAccount(LoanStatus.AWAITING_DISBURSAL, AccountStatus.NEW,loanOffer.getLoaneeId());
    }

    @Test
    void createLoanOfferWithValidLoanRequestId() {
        OrganizationIdentity organizationIdentity = OrganizationIdentity.builder().id(mockId2).build();
        loanRequest.setStatus(LoanRequestStatus.APPROVED);
        LoanOffer cretedLoanOffer = new LoanOffer();
        try {
            when(loanOfferOutputPort.save(any(LoanOffer.class))).thenReturn(loanOffer);
            when(organizationIdentityOutputPort.findOrganizationByName(anyString())).
                    thenReturn(Optional.ofNullable(organizationIdentity));
            when(loanMetricsUseCase.save(any())).thenReturn(LoanMetrics.builder().
                    organizationId(mockId2).loanOfferCount(1).build());
            cretedLoanOffer = loanService.createLoanOffer(loanRequest);
        } catch (MeedlException exception) {
            log.error(exception.getMessage());
        }
        assertEquals(LoanRequestStatus.APPROVED, cretedLoanOffer.getLoanRequest().getStatus());
        assertEquals(LoanOfferStatus.OFFERED, cretedLoanOffer.getLoanOfferStatus());
        assertNotNull(cretedLoanOffer.getDateTimeOffered());
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
        loanOffer.setLoanProduct(loanProduct);
        when(loanOfferOutputPort.findLoanOfferById(mockId)).thenReturn(loanOffer);
        when(loaneeOutputPort.findByUserId(any())).thenReturn(Optional.ofNullable(loanee));
        assertThrows(MeedlException.class, () -> loanService.acceptLoanOffer(loanOffer));
    }

    @Test
    void loaneeLoanAccountIsCreatedIfLoanOfferIsAccepted(){
        loanOffer.setLoanee(Loanee.builder().id("ead0f7cb-5483-4bb8-b271-813970a9c368").build());
        loanOffer.setLoaneeResponse(LoanDecision.ACCEPTED);
        loanOffer.setLoanProduct(loanProduct);
        try {
            when(loanOfferOutputPort.findLoanOfferById(mockId)).thenReturn(loanOffer);
            when(loaneeOutputPort.findByUserId(mockId)).thenReturn(Optional.ofNullable(loanee));
            when(loanProductOutputPort.findById(loanOffer.getLoanProduct().getId())).thenReturn(loanProduct);
            when(loanOfferOutputPort.save(loanOffer)).thenReturn(loanOffer);
            when(loaneeLoanAccountOutputPort.save(any())).thenReturn(loaneeLoanAccount);
            when(loaneeLoanAccountOutputPort.findByLoaneeId(any())).thenReturn(null);
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
    @Test
    void viewAllLoanOffer(){
        Page<LoanOffer> loanOffers = null;
        try {
            when(userIdentityOutputPort.findById(mockId)).thenReturn(userIdentity);
            when(loanOfferOutputPort.findAllLoanOffers(1,0)).
                    thenReturn(new PageImpl<>(List.of(loanOffer)));
            loanOffers = loanService.viewAllLoanOffers(mockId,1,0);
        }catch (MeedlException exception){
            log.info(exception.getMessage());
        }
        assertEquals(1,loanOffers.getSize());
    }

    @ParameterizedTest
    @ValueSource(strings = {StringUtils.EMPTY,StringUtils.SPACE,"hjdhjdbdjbff"})
    void viewLoanOfferWithInvalidLoanOfferId(String invalidId){
        assertThrows(MeedlException.class,()-> loanService.viewLoanOfferDetails(mockId,invalidId));
    }

    @Test
    void viewLoanOfferWithNullLoanOFferId(){
        assertThrows(MeedlException.class,()-> loanService.viewLoanOfferDetails(mockId,null));
    }

    @Test
    void loaneeCannotViewLoanOfferThatNotAssignedToLoanee() throws MeedlException {
        userIdentity.setRole(IdentityRole.LOANEE);
        LoanOffer mockLoanOffer = new LoanOffer();
        mockLoanOffer.setLoaneeId(mockId);
        Loanee mockLoanee = new Loanee();
        UserIdentity mockLoaneeIdentity = new UserIdentity();
        mockLoaneeIdentity.setId(loaneeId);
        mockLoanee.setUserIdentity(mockLoaneeIdentity);
        when(userIdentityOutputPort.findById(mockId)).thenReturn(userIdentity);
        when(loanOfferOutputPort.findLoanOfferById(mockId)).thenReturn(mockLoanOffer);
        when(loaneeLoanBreakDownOutputPort.findAllByLoaneeId(mockId))
                .thenReturn(List.of(TestData.createTestLoaneeLoanBreakdown(mockId)));
        when(loaneeOutputPort.findLoaneeById(mockId)).thenReturn(mockLoanee);
        assertThrows(
                LoanOfferException.class,
                () -> loanService.viewLoanOfferDetails(mockId, mockId)
        );
    }


    @Test
    void viewLoanOfferDetails(){
        try {
            loanOffer.setLoaneeId(mockId);
            userIdentity.setRole(IdentityRole.LOANEE);
            when(userIdentityOutputPort.findById(mockId)).thenReturn(userIdentity);
            when(loanOfferOutputPort.findLoanOfferById(mockId)).thenReturn(loanOffer);
            when(loaneeLoanBreakDownOutputPort.findAllByLoaneeId(anyString()))
                     .thenReturn(List.of(TestData.createTestLoaneeLoanBreakdown(mockId)));
            when(loaneeOutputPort.findLoaneeById(mockId)).thenReturn(loanee);
            loanOffer = loanService.viewLoanOfferDetails(mockId,mockId);
        }catch (MeedlException meedlException){
            log.error(meedlException.getMessage());
        }
        assertEquals(loanOffer.getLoanRequest(),loanRequest);
    }
}

