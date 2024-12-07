package africa.nkwadoma.nkwadoma.test.data;

import africa.nkwadoma.nkwadoma.domain.enums.*;
import africa.nkwadoma.nkwadoma.domain.enums.loanEnums.*;
import africa.nkwadoma.nkwadoma.domain.model.education.*;
import africa.nkwadoma.nkwadoma.domain.model.identity.IdentityVerification;
import africa.nkwadoma.nkwadoma.domain.model.identity.OrganizationEmployeeIdentity;
import africa.nkwadoma.nkwadoma.domain.model.identity.OrganizationIdentity;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import africa.nkwadoma.nkwadoma.domain.model.loan.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class TestData {
    private static final String testId = "ead0f7cb-5483-4bb8-b271-813970a9c368";

    public static UserIdentity createTestUserIdentity(String email){
        return UserIdentity.builder()
                .id(testId)
                .firstName("John")
                .lastName("Doe")
                .email(email)
                .phoneNumber("090876536217")
                .createdBy(testId)
                .role(IdentityRole.LOANEE)
                .alternateEmail("alt276@example.com")
                .alternatePhoneNumber("0986564534")
                .alternateContactAddress("10, Onigbagbo Street, Mushin, Lagos State")
                .build();
    }
    public static OrganizationIdentity createOrganizationTestData(String name, String rcNumber , List<OrganizationEmployeeIdentity> employeePeter) {
        OrganizationIdentity organizationIdentity = new OrganizationIdentity();
        organizationIdentity.setName(name);
        organizationIdentity.setId(testId);
        organizationIdentity.setEmail("testorganizationdata@gmail.com");
        organizationIdentity.setTin("7682-5627");
        organizationIdentity.setRcNumber(rcNumber);
        organizationIdentity.setServiceOfferings(List.of(new ServiceOffering()));
        organizationIdentity.getServiceOfferings().get(0).setIndustry(Industry.EDUCATION);
        organizationIdentity.getServiceOfferings().get(0).setName(ServiceOfferingType.TRAINING.name());
        organizationIdentity.setPhoneNumber("09876365713");
        organizationIdentity.setInvitedDate(LocalDateTime.now().toString());
        organizationIdentity.setWebsiteAddress("testdata.org");
        organizationIdentity.setOrganizationEmployees(employeePeter);
        organizationIdentity.setPageSize(10);
        organizationIdentity.setPageNumber(0);

        return organizationIdentity;
    }
    public static IdentityVerification createTestIdentityVerification(String bvn, String nin){
        return IdentityVerification.builder()
                .bvn(bvn).nin(nin)
                .imageUrl("https://res.cloudinary.com/drhrd1xkn/image/upload/v1732027769/.jpg")
                .build();
    }

    public static Loanee createTestLoanee(UserIdentity userIdentity, LoaneeLoanDetail loaneeLoanDetail){
        return Loanee.builder()
                .id(testId)
                .userIdentity(userIdentity)
                .cohortId(testId)
                .createdBy(userIdentity.getCreatedBy())
                .loaneeLoanDetail(loaneeLoanDetail)
                .build();
    }
    public static Loan createTestLoan(Loanee loanee){
        return Loan.builder()
                .loaneeId(testId)
                .loanOfferId(testId)
                .loanee(loanee)
                .startDate(LocalDateTime.now())
                .loanAccountId("account id")
                .build();
    }
    public static LoaneeLoanDetail createTestLoaneeLoanDetail(){
        return LoaneeLoanDetail.builder()
                .amountRequested(BigDecimal.valueOf(9000000))
                .initialDeposit(BigDecimal.valueOf(3000000))
                .build();
    }
    public static OrganizationEmployeeIdentity createOrganizationEmployeeIdentityTestData(UserIdentity identity){
        OrganizationEmployeeIdentity organizationEmployeeIdentity = new OrganizationEmployeeIdentity();
        organizationEmployeeIdentity.setOrganization(testId);
        organizationEmployeeIdentity.setMeedlUser(identity);
        return organizationEmployeeIdentity;
    }

    public static Cohort createCohortData(String name, String programId, String organizationId, List<LoanBreakdown> loanBreakdowns, String meedlUserId) {
        Cohort elites = new Cohort();
        elites.setStartDate(LocalDate.of(2024,10,18));
        elites.setProgramId(programId);
        elites.setName(name);
        elites.setCreatedBy(meedlUserId);
        elites.setLoanBreakdowns(loanBreakdowns);
        elites.setTuitionAmount(BigDecimal.valueOf(1000000));
        elites.setOrganizationId(organizationId);
        elites.setCohortStatus(CohortStatus.GRADUATED);
        return elites;
    }

    public static Program createProgramTestData(String programName){
         return Program.builder().name(programName).
                programStatus(ActivationStatus.ACTIVE).programDescription("Program description").
                mode(ProgramMode.FULL_TIME).duration(2).durationType(DurationType.YEARS).
                deliveryType(DeliveryType.ONSITE).
                createdAt(LocalDateTime.now()).programStartDate(LocalDate.now()).build();
    }

    public static LoanDetail createLoanDetail(){
      return LoanDetail.builder().debtPercentage(0.34).repaymentPercentage(0.67).monthlyExpected(BigDecimal.valueOf(450))
                .totalAmountRepaid(BigDecimal.valueOf(500)).totalInterestIncurred(BigDecimal.valueOf(600))
                .lastMonthActual(BigDecimal.valueOf(200)).totalAmountDisbursed(BigDecimal.valueOf(50000))
                .totalOutstanding(BigDecimal.valueOf(450)).build();
    }

    public static LoanBreakdown createLoanBreakDown(){
        return LoanBreakdown.builder().currency("USD").itemAmount(new BigDecimal("50000"))
                .itemName("Loan Break").build();
    }

    public static LoanProduct buildTestLoanProduct(String name, Vendor vendor) {
        LoanProduct loanProduct = new LoanProduct();
        loanProduct.setId("3a6d1124-1349-4f5b-831a-ac269369a90f");
        loanProduct.setName(name);
        loanProduct.setMandate("Test: A new mandate for test");
        loanProduct.setSponsors(List.of("Mark", "Jack"));
        loanProduct.setObligorLoanLimit(new BigDecimal("100.00"));
        loanProduct.setTermsAndCondition("Test: A new loan for test and terms and conditions");
        loanProduct.setLoanProductSize(new BigDecimal("1000000"));
        loanProduct.setPageSize(10);
        loanProduct.setPageNumber(0);
        loanProduct.setVendors(List.of(vendor));
        return loanProduct;
    }
    public static Vendor createTestVendor(String name) {
        Vendor vendor = new Vendor();
        vendor.setVendorName(name);
        vendor.setTermsAndConditions("Test: A new vendor for test with terms and condition imaginary");
        vendor.setProduct(Product.ACCOMMODATION);
        return vendor;
    }

    public static LoanOffer buildLoanOffer(LoanRequest loanRequest, Loanee loanee) {
        LoanOffer loanOffer = new LoanOffer();
        loanOffer.setDateTimeOffered(LocalDateTime.now());
        loanOffer.setLoanRequest(loanRequest);
        loanOffer.setLoanOfferStatus(LoanOfferStatus.OFFERED);
        loanOffer.setLoanee(loanee);
        return loanOffer;
    }

    public static LoanRequest buildLoanRequest(Loanee loanee, LoaneeLoanDetail loaneeLoanDetail) {
        LoanRequest loanRequest = new LoanRequest();
        loanRequest.setId("3a6d1124-1349-4f5b-831a-ac269369a90f");
        loanRequest.setLoanAmountApproved(BigDecimal.valueOf(500000));
        loanRequest.setLoanRequestDecision(LoanDecision.ACCEPTED);
        loanRequest.setLoanAmountRequested(BigDecimal.valueOf(900000));
        loanRequest.setStatus(LoanRequestStatus.NEW);
        loanRequest.setLoanReferralStatus(LoanReferralStatus.ACCEPTED);
        loanRequest.setReferredBy("Brown Hills Institute");
        loanee.setLoaneeLoanDetail(loaneeLoanDetail);
        loanRequest.setLoanee(loanee);
        loanRequest.setDateTimeApproved(LocalDateTime.now());
        return loanRequest;
    }
}
