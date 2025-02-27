package africa.nkwadoma.nkwadoma.test.data;

import africa.nkwadoma.nkwadoma.domain.enums.*;
import africa.nkwadoma.nkwadoma.domain.enums.investmentVehicle.InvestmentVehicleType;
import africa.nkwadoma.nkwadoma.domain.enums.loanEnums.*;
import africa.nkwadoma.nkwadoma.domain.model.education.*;
import africa.nkwadoma.nkwadoma.domain.model.identity.IdentityVerification;
import africa.nkwadoma.nkwadoma.domain.model.identity.OrganizationEmployeeIdentity;
import africa.nkwadoma.nkwadoma.domain.model.identity.OrganizationIdentity;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import africa.nkwadoma.nkwadoma.domain.model.investmentVehicle.InvestmentVehicle;
import africa.nkwadoma.nkwadoma.domain.model.loan.*;
import africa.nkwadoma.nkwadoma.domain.model.loan.LoanDetail;
import africa.nkwadoma.nkwadoma.domain.model.meedlPortfolio.Portfolio;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.data.response.premblyresponses.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class TestData {
    private static final String testId = "ead0f7cb-5483-4bb8-b271-813970a9c368";

    public static UserIdentity createTestUserIdentity(String email, String testId) {
        UserIdentity userIdentity = createTestUserIdentity(email);
        userIdentity.setId(testId);
        return userIdentity;
    }
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
                .image("loanee-img.png")
                .gender("Male")
                .nationality("Nigerian")
                .stateOfOrigin("Osun")
                .dateOfBirth("29th April 1990")
                .maritalStatus("Single")
                .stateOfResidence("Lagos")
                .residentialAddress("1, Spencer Street, Yaba, Lagos")
                .alternatePhoneNumber("0986564534")
                .alternateContactAddress("10, Onigbagbo Street, Mushin, Lagos State")
                .build();
    }

    public static LoanMetrics createTestLoanMetrics(String organizationId) {
        return LoanMetrics.builder().id(organizationId).build();

    }

    public static OrganizationIdentity createOrganizationTestData(String name, String rcNumber , List<OrganizationEmployeeIdentity> employeePeter) {
        OrganizationIdentity organizationIdentity = new OrganizationIdentity();
        organizationIdentity.setName(name);
        organizationIdentity.setId(testId);
        organizationIdentity.setEmail("testorganizationdata@gmail.com");
        organizationIdentity.setTin("7682-5627");
        organizationIdentity.setRcNumber(rcNumber);
        organizationIdentity.setStatus(ActivationStatus.INVITED);
        organizationIdentity.setServiceOfferings(List.of(new ServiceOffering()));
        organizationIdentity.getServiceOfferings().get(0).setIndustry(Industry.EDUCATION);
        organizationIdentity.getServiceOfferings().get(0).setTransactionLowerBound(new BigDecimal("0.00"));
        organizationIdentity.getServiceOfferings().get(0).setTransactionUpperBound(new BigDecimal("0.00"));
        organizationIdentity.getServiceOfferings().get(0).setName(ServiceOfferingType.TRAINING.name());
        organizationIdentity.setPhoneNumber("09876365713");
        organizationIdentity.setInvitedDate(LocalDateTime.now().toString());
        organizationIdentity.setWebsiteAddress("testdata.org");
        organizationIdentity.setOrganizationEmployees(employeePeter);
        organizationIdentity.setLogoImage("logo-img.png");
        organizationIdentity.setPageSize(0);
        organizationIdentity.setPageNumber(10);

        return organizationIdentity;
    }
    public static IdentityVerification createTestIdentityVerification(String bvn, String nin){
        return IdentityVerification.builder()
                .decryptedBvn(bvn).decryptedNin(nin)
                .imageUrl("https://res.cloudinary.com/drhrd1xkn/image/upload/v1732027769/.jpg")
                .build();
    }

    public static Loanee createTestLoanee(UserIdentity userIdentity, LoaneeLoanDetail loaneeLoanDetail){
        return Loanee.builder()
                .id(testId)
                .userIdentity(userIdentity)
                .cohortId(testId)
                .loaneeLoanDetail(loaneeLoanDetail)
                .build();
    }
    public static Loan createTestLoan(Loanee loanee){
        return Loan.builder()
                .loaneeId(testId)
                .loanOfferId(testId)
                .loanee(loanee)
                .loanAccountId(testId)
                .startDate(LocalDateTime.now())
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

    public static africa.nkwadoma.nkwadoma.domain.model.education.LoanDetail createLoanDetail(){
      return africa.nkwadoma.nkwadoma.domain.model.education.LoanDetail.builder().debtPercentage(0.34).repaymentPercentage(0.67).monthlyExpected(BigDecimal.valueOf(450))
                .totalAmountRepaid(BigDecimal.valueOf(500)).totalInterestIncurred(BigDecimal.valueOf(600))
                .lastMonthActual(BigDecimal.valueOf(200)).totalAmountDisbursed(BigDecimal.valueOf(50000))
                .totalOutstanding(BigDecimal.valueOf(450)).build();
    }

    public static LoanBreakdown createLoanBreakDown(){
        return LoanBreakdown.builder().currency("USD").itemAmount(new BigDecimal("50000"))
                .itemName("Accommodation").build();
    }

    public static LoanProduct buildTestLoanProduct(String name, Vendor vendor) {
        LoanProduct loanProduct = new LoanProduct();
        loanProduct.setId("3a6d1124-1349-4f5b-831a-ac269369a90f");
        loanProduct.setInvestmentVehicleId(testId);
        loanProduct.setTenor(2);
        loanProduct.setMoratorium(2);
        loanProduct.setName(name);
        loanProduct.setMandate("Test: A new mandate for test");
        loanProduct.setSponsor("Mark");
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

    public static LoaneeLoanAccount createLoaneeLoanAccount(LoanStatus loanStatus ,AccountStatus status,String loaneeId) {
        LoaneeLoanAccount loaneeLoanAccount = new LoaneeLoanAccount();
        loaneeLoanAccount.setLoanStatus(loanStatus);
        loaneeLoanAccount.setAccountStatus(status);
        loaneeLoanAccount.setLoaneeId(loaneeId);
        return loaneeLoanAccount;
    }
    public static PremblyResponse createTestPremblyResponse(){
        PremblyResponse response = new PremblyBvnResponse();
        Verification verifier = Verification.builder().status("VERIFIED").build();
        response.setDetail("VERIFIED");
        response.setVerification(verifier);
        response.setResponseCode("CREATED");
        return response;
    }

    public static InvestmentVehicle buildInvestmentVehicle(String name) {
        InvestmentVehicle investmentVehicle = new InvestmentVehicle();
        investmentVehicle.setName(name);
        investmentVehicle.setSize(BigDecimal.valueOf(4000));
        investmentVehicle.setRate(13F);
        investmentVehicle.setMandate("Long-term Growth");
        investmentVehicle.setInvestmentVehicleType(InvestmentVehicleType.ENDOWMENT);
        investmentVehicle.setTenure(12);
        investmentVehicle.setCustodian("Custodian");
        investmentVehicle.setBankPartner("Gt Bank");
        investmentVehicle.setFundManager("Gt Manager");
        investmentVehicle.setMinimumInvestmentAmount(BigDecimal.valueOf(5000));
        investmentVehicle.setTrustee("trustee");
        investmentVehicle.setSponsors("sponsors");
        return investmentVehicle;
    }

    public static LoanOffer buildLoanOffer(LoanRequest loanRequest,Loanee loanee, String id){
        LoanOffer loanOffer = new LoanOffer();
        loanOffer.setDateTimeOffered(LocalDateTime.now());
        loanOffer.setLoanRequest(loanRequest);
        loanOffer.setLoanOfferStatus(LoanOfferStatus.OFFERED);
        loanOffer.setLoanee(loanee);
        loanOffer.setUserId(id);
        loanOffer.setId(id);
        loanOffer.setProgramId(id);
        loanOffer.setOrganizationId(id);
        loanOffer.setType(LoanType.LOAN_OFFER);
        loanOffer.setName("ojo");
        loanOffer.setPageSize(10);
        loanOffer.setPageNumber(0);
        return loanOffer;
    }

    public static LoaneeLoanBreakdown createTestLoaneeLoanBreakdown(String loaneeBreakdownId) {
        return LoaneeLoanBreakdown.builder()
                .loaneeLoanBreakdownId(loaneeBreakdownId)
                .itemName("Feeding")
                .itemAmount(new BigDecimal(100000))
                .currency("NGN").build();
    }

    public static NextOfKin createNextOfKinData(Loanee loanee) {
        NextOfKin nextOfKin = new NextOfKin();
        nextOfKin.setFirstName("Ahmad");
        nextOfKin.setLastName("Awwal");
        nextOfKin.setEmail("ahmad12@gmail.com");
        nextOfKin.setPhoneNumber("0785678901");
        nextOfKin.setNextOfKinRelationship("Brother");
        nextOfKin.setContactAddress("2, Spencer Street, Yaba, Lagos");
        nextOfKin.setLoanee(loanee);
        return nextOfKin;
    }

    public static LoanDetail createLoanLifeCycle() {
        LoanDetail loanDetail = new LoanDetail();
        loanDetail.setCohortName("Cohort");
        loanDetail.setDeposit(BigDecimal.valueOf(5000));
        loanDetail.setAmountRequested(BigDecimal.valueOf(5000));
        loanDetail.setProgramName("Program");
        loanDetail.setOfferDate(LocalDate.now());
        loanDetail.setFirstName("Ahmad");
        loanDetail.setLastName("Awwal");
        loanDetail.setStartDate(LocalDate.now());
        return loanDetail;
    }
    public static PremblyBvnResponse createPremblyBvnTestResponse() {
        return PremblyBvnResponse.builder()
                .verificationCallSuccessful(true)
                .detail("Verification successful")
                .responseCode("00")
                .data(PremblyBvnResponse.BvnData.builder()
                        .bvn("12345678901")
                        .firstName("John")
                        .middleName("Doe")
                        .lastName("Smith")
                        .dateOfBirth("1990-01-01")
                        .registrationDate("2020-05-15")
                        .enrollmentBank("First Bank")
                        .enrollmentBranch("Lagos Main")
                        .email("john.doe@example.com")
                        .gender("Male")
                        .levelOfAccount("Tier 3")
                        .lgaOfOrigin("Ikeja")
                        .lgaOfResidence("Surulere")
                        .maritalStatus("Single")
                        .nin("12345678910")
                        .nameOnCard("John D. Smith")
                        .nationality("Nigerian")
                        .phoneNumber1("+2348012345678")
                        .phoneNumber2("+2348098765432")
                        .residentialAddress("123, Lagos Street, Ikeja")
                        .stateOfOrigin("Lagos")
                        .stateOfResidence("Lagos")
                        .title("Mr.")
                        .watchListed("No")
                        .image("base64-image-string")
                        .number("12345")
                        .faceData(createMockFaceData()) // Assuming an empty instance is okay
                        .build())
                .verification(createMockVerification()) // Assuming no data for verification
                .session(null) // Assuming no session data
                .build();
    }

    public static Verification createMockVerification() {
        return Verification.builder()
                .status("VERIFIED")
                .validIdentity(true) // This will be updated dynamically if updateValidIdentity() is called
                .reference("REF-123456345")
                .build();
    }
    public static PremblyFaceData createMockFaceData() {
        return PremblyFaceData.builder()
                .faceVerified(true)
                .message("Face Match")
                .confidence("99.9987564086914")
                .responseCode("00")
                .build();
    }

    public static PremblyNinResponse createPremblyNinTestResponse() {
        return PremblyNinResponse.builder()
                .verificationCallSuccessful(true)
                .detail("Verification successful")
                .responseCode("00")
                .ninData(PremblyNinResponse.NinData.builder()
                        .nin("12345678901")
                        .firstname("John")
                        .middleName("Doe")
                        .lastname("Smith")
                        .birthDate("1990-01-01")
                        .birthCountry("Nigeria")
                        .birthState("Lagos")
                        .birthLGA("Ikeja")
                        .gender("Male")
                        .email("john.doe@example.com")
                        .telephoneNo("+2348012345678")
                        .residenceAddress("123, Lagos Street, Ikeja")
                        .residenceState("Lagos")
                        .residenceLGA("Ikeja")
                        .residenceTown("Ikeja")
                        .maritalStatus("Single")
                        .employmentStatus("Employed")
                        .educationalLevel("Bachelor's Degree")
                        .profession("Software Engineer")
                        .selfOriginState("Lagos")
                        .selfOriginLGA("Ikeja")
                        .selfOriginPlace("Mainland")
                        .signature("base64-signature-string")
                        .photo("base64-photo-string")
                        .trackingId("TRACK-123456")
                        .userId("USR-987654")
                        .vnin("VNIN-54321")
                        .nokFirstName("Jane")
                        .nokSurname("Doe")
                        .nokMiddleName("Ann")
                        .nokAddress1("456, Abuja Street, FCT")
                        .nokAddress2("Suite 202")
                        .nokState("Abuja")
                        .nokLGA("Municipal")
                        .nokPostalCode("900001")
                        .nokTown("Central Area")
                        .spokenLanguage("English")
                        .build())
                .faceData(createMockFaceData())
                .verification(createMockVerification())
                .session(null)
                .endpointName("NIN Verification")
                .userId("USR-987654")
                .build();
    }

    public static Portfolio createMeedlPortfolio() {
        return Portfolio.builder()
                .portfolioName("Portfolio")
                .totalNumberOfCommercialFundsInvestmentVehicle(2)
                .totalNumberOfFinancier(2)
                .totalNumberOfEndowmentFundsInvestmentVehicle(2)
                .totalNumberOfInvestmentVehicle(2)
                .totalNumberOfInstitutionalFinancier(2)
                .totalNumberOfIndividualFinancier(2)
                .build();
    }
}
