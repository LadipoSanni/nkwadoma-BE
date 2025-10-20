package africa.nkwadoma.nkwadoma.testUtilities.data;

import africa.nkwadoma.nkwadoma.domain.enums.*;
import africa.nkwadoma.nkwadoma.domain.enums.identity.*;
import africa.nkwadoma.nkwadoma.domain.enums.identity.ActivationStatus;
import africa.nkwadoma.nkwadoma.domain.enums.investmentvehicle.*;
import africa.nkwadoma.nkwadoma.domain.enums.loanee.LoaneeStatus;
import africa.nkwadoma.nkwadoma.domain.enums.loanee.UploadedStatus;
import africa.nkwadoma.nkwadoma.domain.enums.loanenums.*;
import africa.nkwadoma.nkwadoma.domain.enums.loanee.ModeOfPayment;
import africa.nkwadoma.nkwadoma.domain.enums.loanee.OnboardingMode;
import africa.nkwadoma.nkwadoma.domain.model.education.*;
import africa.nkwadoma.nkwadoma.domain.model.financier.*;
import africa.nkwadoma.nkwadoma.domain.model.bankdetail.BankDetail;
import africa.nkwadoma.nkwadoma.domain.model.identity.*;
import africa.nkwadoma.nkwadoma.domain.model.investmentvehicle.*;
import africa.nkwadoma.nkwadoma.domain.model.loan.*;
import africa.nkwadoma.nkwadoma.domain.model.loan.LoanDetail;
import africa.nkwadoma.nkwadoma.domain.model.loan.disbursement.DisbursementRule;
import africa.nkwadoma.nkwadoma.domain.model.loan.loanBook.*;
import africa.nkwadoma.nkwadoma.domain.model.meedlPortfolio.Demography;
import africa.nkwadoma.nkwadoma.domain.model.meedlPortfolio.PlatformRequest;
import africa.nkwadoma.nkwadoma.domain.model.meedlPortfolio.Portfolio;
import africa.nkwadoma.nkwadoma.domain.model.notification.MeedlNotification;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.data.response.premblyresponses.*;
import africa.nkwadoma.nkwadoma.testUtilities.TestUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;

import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Slf4j
public class TestData {
    private static final String testId = "ead0f7cb-5483-4bb8-b271-813970a9c368";
    private static final int pageSize = 10;
    private static final int pageNumber = 0;
    public static UserIdentity createTestUserIdentity(String email, String testId) {
        UserIdentity userIdentity = createTestUserIdentity(email);
        userIdentity.setId(testId);
        return userIdentity;
    }
    public static DisbursementRule buildDisbursementRule() {
        return  DisbursementRule.builder()
                .percentageDistribution(List.of(100.00))
                .distributionDates(List.of(LocalDateTime.now()))
                .interval(DisbursementInterval.MONTHLY)
                .activationStatus(ActivationStatus.APPROVED)
                .name(TestUtils.generateName(5))
                .activationStatuses(Set.of(ActivationStatus.APPROVED))
                .build();
    }
    public static UserIdentity createTestUserIdentity(String email){
        return UserIdentity.builder()
                .id(UUID.randomUUID().toString())
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
        organizationIdentity.setTin("7682-56"+TestUtils.generateName(3));
        organizationIdentity.setRcNumber(rcNumber);
        organizationIdentity.setActivationStatus(ActivationStatus.INVITED);
        organizationIdentity.setServiceOfferings(List.of(new ServiceOffering()));
        organizationIdentity.getServiceOfferings().get(0).setIndustry(Industry.EDUCATION);
        organizationIdentity.getServiceOfferings().get(0).setTransactionLowerBound(new BigDecimal("0.00"));
        organizationIdentity.getServiceOfferings().get(0).setTransactionUpperBound(new BigDecimal("0.00"));
        organizationIdentity.getServiceOfferings().get(0).setName(ServiceOfferingType.TRAINING.name());
        organizationIdentity.setPhoneNumber("09876365713");
        organizationIdentity.setInvitedDate(LocalDateTime.now().toString());
        organizationIdentity.setRequestedInvitationDate(LocalDateTime.now());
        organizationIdentity.setWebsiteAddress("testdata.org");
        organizationIdentity.setOrganizationEmployees(employeePeter);
        organizationIdentity.setLogoImage("logo-img.png");
        organizationIdentity.setOrganizationType(OrganizationType.INSTITUTE_ORGANIZATION);
        organizationIdentity.setPageSize(pageSize);
        organizationIdentity.setPageNumber(pageNumber);

        return organizationIdentity;
    }
    public static IdentityVerification createTestIdentityVerification(String bvn, String nin){
        return IdentityVerification.builder()
                .decryptedBvn(bvn).decryptedNin(nin)
                .imageUrl("https://res.cloudinary.com/drhrd1xkn/image/upload/v1732027769/.jpg")
                .build();
    }
    public static PlatformRequest buildPlatformRequest() {
        return PlatformRequest.builder()
                .createdBy(UUID.randomUUID().toString())
                .obligorLoanLimit(BigDecimal.TEN)
                .requestTime(LocalDateTime.now())
                .pageNumber(0)
                .pageSize(10)
                .build();
    }


    public static Loanee createTestLoanee(UserIdentity userIdentity, LoaneeLoanDetail loaneeLoanDetail){
        return Loanee.builder()
                .id(testId)
                .userIdentity(userIdentity)
                .cohortId(testId)
                .onboardingMode(OnboardingMode.EMAIL_REFERRED)
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
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .loanStartDate(LocalDateTime.now())
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
        elites.setCohortType(CohortType.NON_LOAN_BOOK);
        return elites;
    }
    public static Program createProgramTestData(String programName){
        return createProgramTestData(programName, null);
    }

    public static Program createProgramTestData(String programName, OrganizationIdentity organizationIdentity){
        if (ObjectUtils.isEmpty(organizationIdentity)){
            organizationIdentity = createOrganizationTestData(
                    TestUtils.generateName(4),
                    "Rc043953443",
                    List.of(createOrganizationEmployeeIdentityTestData(
                            createTestUserIdentity(TestUtils.generateEmail(5)))));
        }
        return Program.builder()
                .name(programName)
                .programStatus(ActivationStatus.ACTIVE)
                .programDescription("Program description")
                .mode(ProgramMode.FULL_TIME)
                .duration(2)
                .durationType(DurationType.YEARS)
                .deliveryType(DeliveryType.ONSITE)
                .createdAt(LocalDateTime.now())
                .programStartDate(LocalDate.now())
                .organizationIdentity(organizationIdentity)
                .build();
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
    public static CalculationContext createCalculationContext(List<RepaymentHistory> repaymentHistories, Loanee loanee, Cohort cohort){
        return CalculationContext.builder()
                .previousTotalAmountPaid(new BigDecimal(1000))
                .previousTotalInterestIncurred(new BigDecimal(100000))
                .repaymentHistories(repaymentHistories)
                .loanee(loanee)
                .cohort(cohort)
                .build();

    }
    public static LoanProduct buildTestLoanProduct(String name) {
        LoanProduct loanProduct = buildTestLoanProduct();
        loanProduct.setName(name);
        return loanProduct;
    }
    public static LoanProduct buildTestLoanProduct() {
        Vendor vendor = TestData.createTestVendor(TestUtils.generateName(6));
        LoanProduct loanProduct = new LoanProduct();
        loanProduct.setId("3a6d1124-1349-4f5b-831a-ac269369a90f");
        loanProduct.setInvestmentVehicleId(testId);
        loanProduct.setTenor(2);
        loanProduct.setMoratorium(2);
        loanProduct.setName("test " +TestUtils.generateName(6));
        loanProduct.setMandate("Test: A new mandate for test");
        loanProduct.setSponsor("Mark");
        loanProduct.setObligorLoanLimit(new BigDecimal("100.00"));
        loanProduct.setTermsAndCondition("Test: A new loan for test and terms and conditions");
        loanProduct.setLoanProductSize(new BigDecimal("1000000"));
        loanProduct.setAvailableAmountToBeDisbursed(new BigDecimal("1000000"));
        loanProduct.setTotalAmountAvailable(new BigDecimal("1000000"));
        loanProduct.setAvailableAmountToBeOffered(new BigDecimal("1000000"));
        loanProduct.setPageSize(pageSize);
        loanProduct.setPageNumber(pageNumber);
        loanProduct.setVendors(List.of(vendor));
        loanProduct.setSponsors(List.of(Financier.builder().id(UUID.randomUUID().toString()).name("ifnf").build()));
        return loanProduct;
    }
    public static Vendor createTestVendor(String name, Set<String> providerServices) {
        Vendor vendor = createTestVendor(name);
        vendor.setProviderServices(providerServices);
        return vendor;
    }
    public static Vendor createTestVendor(String name) {
        Vendor vendor = new Vendor();
        vendor.setVendorName(name);
        vendor.setPageSize(10);
        vendor.setPageNumber(0);
        vendor.setCostOfService(new BigDecimal(9));
        vendor.setProviderServices(Set.of(TestUtils.generateName(9)));
        vendor.setTermsAndConditions("Test: A new vendor for test with terms and condition imaginary");
        return vendor;
    }

    public static LoanOffer buildLoanOffer(LoanRequest loanRequest) {
        LoanOffer loanOffer = new LoanOffer();
        loanOffer.setDateTimeOffered(LocalDateTime.now());
        loanOffer.setLoanOfferStatus(LoanOfferStatus.OFFERED);
        loanOffer.setId(loanRequest.getId());
        loanOffer.setAmountApproved(loanRequest.getLoanAmountRequested());
        return loanOffer;
    }

    public static LoanRequest buildLoanRequest(String loanReferralId) {
        LoanRequest loanRequest = new LoanRequest();
        loanRequest.setId(loanReferralId);
        loanRequest.setLoanAmountApproved(BigDecimal.valueOf(500000));
        loanRequest.setLoanRequestDecision(LoanDecision.ACCEPTED);
        loanRequest.setLoanAmountRequested(BigDecimal.valueOf(900000));
        loanRequest.setStatus(LoanRequestStatus.NEW);
        loanRequest.setLoanReferralStatus(LoanReferralStatus.ACCEPTED);
        loanRequest.setOnboardingMode(OnboardingMode.EMAIL_REFERRED);
        loanRequest.setReferredBy("Brown Hills Institute");
        loanRequest.setDateTimeApproved(LocalDateTime.now());
        loanRequest.setCreatedDate(LocalDateTime.now());
        return loanRequest;
    }

    public static Financier completeKycRequest(Financier financier, BankDetail bankDetail){
        log.info("Bank Detail in test data {}", bankDetail);
        financier.getUserIdentity().setBankDetail(bankDetail);
        financier.setFinancierType(FinancierType.INDIVIDUAL);
        financier.getUserIdentity().setAddress("No 289, Herbert Marculey way, Yaba, Lagos");
        financier.getUserIdentity().setNin("2025103002");
        financier.getUserIdentity().setBvn("2025143002");
        financier.getUserIdentity().setTaxId("00000122");
        financier.setSourceOfFunds(Set.of("PersonalOrJointSavings"));
//        financier.setOccupation("Doctor");
//
//        financier.setPersonalOrJointSavings("Personal or joint savings stated.");
//        financier.setEmploymentIncome("Employment income stated.");
//        financier.setSalesOfAssets("Sales of assets stated.");
//        financier.setDonation("Donation stated.");
//        financier.setInheritanceOrGift("Inheritance or gift stated.");
//        financier.setCompensationOfLegalSettlements("Compensation of legal settlements stated.");
//        financier.setProfitFromLegitimateActivities(new BigDecimal("1000"));

        List<BeneficialOwner> beneficialOwners = List.of(buildBeneficialOwner(60), buildBeneficialOwner(40));
        financier.setBeneficialOwners(beneficialOwners);
        financier.setDeclarationAndAgreement(Boolean.TRUE);
        financier.setPoliticallyExposed(Boolean.FALSE);
        financier.setRcNumber("RC"+TestUtils.generateRandomNumber(7));

        PoliticallyExposedPerson politicallyExposedPerson = new PoliticallyExposedPerson();
        politicallyExposedPerson.setPositionHeld("President");
        politicallyExposedPerson.setCountry(Country.SERBIA);
        List<PoliticallyExposedPerson> politicallyExposedPeople = List.of(politicallyExposedPerson);
        financier.setPoliticallyExposedPeople(politicallyExposedPeople);
        return financier;
    }




    public static LoaneeLoanAccount createLoaneeLoanAccount(LoanStatus loanStatus , AccountStatus status, String loaneeId) {
        LoaneeLoanAccount loaneeLoanAccount = new LoaneeLoanAccount();
        loaneeLoanAccount.setLoanStatus(loanStatus);
        loaneeLoanAccount.setAccountStatus(status);
        loaneeLoanAccount.setLoaneeId(loaneeId);
        return loaneeLoanAccount;
    }

    public static Financier buildCooperateFinancier(Cooperation cooperation, UserIdentity userIdentity) {
        Set<InvestmentVehicleDesignation> investmentVehicleDesignations = new HashSet<>();
        investmentVehicleDesignations.add(InvestmentVehicleDesignation.SPONSOR);
        Financier financier = buildFinancier(investmentVehicleDesignations);
        financier.setCooperation(cooperation);
        financier.setUserIdentity(userIdentity);
        financier.setFinancierType(FinancierType.COOPERATE);
        return financier;
    }
    public static Financier buildFinancierIndividual(UserIdentity userIdentity) {
        Set<InvestmentVehicleDesignation> investmentVehicleDesignations = new HashSet<>();
        investmentVehicleDesignations.add(InvestmentVehicleDesignation.SPONSOR);
        Financier financier = buildFinancier(investmentVehicleDesignations);
        financier.setUserIdentity(userIdentity);
        financier.setIdentity(userIdentity.getId());
        financier.setFinancierType(FinancierType.INDIVIDUAL);
        return financier;
    }
    public static LoanBook buildLoanBook(String absolutePath){
        return LoanBook.builder()
                .absoluteFilePath(absolutePath)
                .file(new File(absolutePath))
                .build();
    }
    public static LoanBook buildLoanBook(String absolutePath, String cohortId){
        return LoanBook.builder()
                .absoluteFilePath(absolutePath)
                .file(new File(absolutePath))
                .cohort(Cohort.builder().id(cohortId).build())
                .build();
    }


    public static RepaymentRecordBook buildRepaymentRecordBook(String absolutePath) {
        return RepaymentRecordBook.builder()
                .absoluteFilePath(absolutePath)
                .file(new File(absolutePath))
                .build();
    }
    public static Cooperation buildCooperation(String name,String emial){
        return Cooperation.builder()
                .name(name)
                .email(emial)
                .build();
    }


    private static Financier buildFinancier( Set<InvestmentVehicleDesignation> investmentVehicleDesignations) {
        return Financier.builder()
                .investmentVehicleDesignation(investmentVehicleDesignations)
                .accreditationStatus(AccreditationStatus.UNVERIFIED)
                .activationStatuses(List.of(ActivationStatus.INVITED, ActivationStatus.ACTIVE))
                .activationStatus(ActivationStatus.INVITED)
                .pageNumber(pageNumber)
                .pageSize(pageSize)
                .build();
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
        investmentVehicle.setSize(new BigDecimal("4000.00"));
        investmentVehicle.setInterestRateOffered(13F);
        investmentVehicle.setTotalAvailableAmount(new BigDecimal("0.00"));
        investmentVehicle.setInvestmentVehicleVisibility(InvestmentVehicleVisibility.PRIVATE);
        investmentVehicle.setMandate("Long-term Growth");
        investmentVehicle.setInvestmentVehicleType(InvestmentVehicleType.ENDOWMENT);
        investmentVehicle.setTenure(12);
        investmentVehicle.setCustodian("Custodian");
        investmentVehicle.setBankPartner("Gt Bank");
        investmentVehicle.setFundManager("Gt Manager");
        investmentVehicle.setMinimumInvestmentAmount(BigDecimal.valueOf(5000));
        investmentVehicle.setTrustee("trustee");
        investmentVehicle.setFundRaisingStatus(FundRaisingStatus.FUND_RAISING);
        investmentVehicle.setInvestmentVehicleStatus(InvestmentVehicleStatus.PUBLISHED);
        investmentVehicle.setStartDate(LocalDate.now());
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
        loanOffer.setPageSize(pageSize);
        loanOffer.setPageNumber(pageNumber);
        return loanOffer;
    }

    public static LoaneeLoanBreakdown createTestLoaneeLoanBreakdown(String loaneeBreakdownId) {
        return LoaneeLoanBreakdown.builder()
                .loaneeLoanBreakdownId(loaneeBreakdownId)
                .itemName("Feeding")
                .itemAmount(new BigDecimal(100000))
                .currency("NGN").build();
    }

    public static NextOfKin createNextOfKinData(UserIdentity userIdentity) {
        NextOfKin nextOfKin = new NextOfKin();
        nextOfKin.setUserId(userIdentity.getId());
        nextOfKin.setFirstName("Ahmad");
        nextOfKin.setLastName("Awwal");
        nextOfKin.setEmail("ahmad12@gmail.com");
        nextOfKin.setPhoneNumber("0785678901");
        nextOfKin.setAlternateContactAddress("alternate-contact-address filled");
        nextOfKin.setAlternateEmail("alternatetest@email.com");
        nextOfKin.setAlternatePhoneNumber("09098347384");
        nextOfKin.setNextOfKinRelationship("Brother");
        nextOfKin.setContactAddress("2, Spencer Street, Yaba, Lagos");

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
                .totalNumberOfLoans(33)
                .obligorLoanLimit(new BigDecimal("0.1"))
                .loanReferralPercentage(27.2727)
                .loanRequestPercentage(18.1818)
                .loanDisbursalPercentage(15.1515)
                .loanOfferPercentage(39.3939)
                .build();
    }

    public static MeedlNotification createNotification(UserIdentity userIdentity) {
        return MeedlNotification.builder()
                .user(userIdentity)
                .read(false)
                .timestamp(LocalDateTime.now())
                .contentId(testId)
                .title("Title")
                .senderMail("qudusa55@gmail.com")
                .senderFullName("John Doe")
                .allNotificationsCount(4)
                .unreadCount(2)
                .build();
    }

    public static InvestmentVehicleFinancier buildInvestmentVehicleFinancier(Financier financier, InvestmentVehicle investmentVehicle) {
        return InvestmentVehicleFinancier.builder()
                .financier(financier)
                .investmentVehicleDesignation(Set.of(InvestmentVehicleDesignation.DONOR))
                .investmentVehicle(investmentVehicle)
                .build();
    }


    public static BankDetail buildBankDetail() {
        return BankDetail.builder()
               .bankName("Lagos Main")
               .bankNumber("1234567890")
               .build();
    }

    public static CouponDistribution createCouponDistribution() {
        return CouponDistribution.builder()
                .due(0)
                .paid(0)
                .lastDatePaid(LocalDateTime.now())
                .lastDateDue(LocalDateTime.now())
                .build();
    }

    public static VehicleOperation createVehicleOperation(CouponDistribution couponDistribution) {
        return VehicleOperation.builder()
                .couponDistribution(couponDistribution)
                .couponDistributionStatus(CouponDistributionStatus.DEFAULT)
                .fundRaisingStatus(InvestmentVehicleMode.OPEN)
                .deployingStatus(InvestmentVehicleMode.OPEN)
                .operationStatus(OperationStatus.ACTIVE)
                .build();
    }

    public static CapitalDistribution buildCapitalDistribution() {
        return CapitalDistribution.builder()
                .due(0)
                .totalCapitalPaidOut(BigDecimal.ZERO)
                .build();
    }

    public static VehicleClosure buildVehicleClosure(CapitalDistribution capitalDistribution) {
        return VehicleClosure.builder()
                .capitalDistribution(capitalDistribution)
                .recollectionStatus(InvestmentVehicleMode.OPEN)
                .maturity("maturity")
                .build();
    }
    public static PoliticallyExposedPerson buildPoliticallyExposedPerson() {
        return PoliticallyExposedPerson.builder()
                .country(Country.AFGHANISTAN)
                .positionHeld("Vice President")
//                .relationship(UserRelationship.BROTHER)
                .additionalInformation("None")
                .build();
    }
    public static BeneficialOwner buildBeneficialOwner(double percentageOwnershipOrShare) {
        return BeneficialOwner.builder()
                .beneficialOwnerType(FinancierType.INDIVIDUAL)
                .entityName("Entity Name")
                .beneficialRcNumber("RC8789945")
                .countryOfIncorporation(Country.SERBIA)
                .beneficialOwnerFirstName("Beneficial first name")
                .beneficialOwnerLastName("Beneficial last name")
                .beneficialOwnerRelationship(UserRelationship.BROTHER)
                .beneficialOwnerDateOfBirth(LocalDate.now())
                .percentageOwnershipOrShare(percentageOwnershipOrShare)
                .votersCard("Voters card")
                .nationalIdCard("national id card")
                .driverLicense("Drivers license")
                .build();
    }

    public static FinancierBeneficialOwner buildFinancierBeneficialOwner(String email) {
        return FinancierBeneficialOwner.builder()
                .beneficialOwner(buildBeneficialOwner(100))
                .financier(buildFinancierIndividual(createTestUserIdentity(email)))
                .build();
    }
    public static FinancierPoliticallyExposedPerson buildFinancierPoliticallyExposedPerson(String email) {
        return FinancierPoliticallyExposedPerson.builder()
                .politicallyExposedPerson(buildPoliticallyExposedPerson())
                .financier(buildFinancierIndividual(createTestUserIdentity(email)))
                .build();
    }

    public static RepaymentHistory buildRepaymentHistory(String cohortId) {
        return RepaymentHistory.builder()
                .modeOfPayment(ModeOfPayment.CASH)
                .amountPaid(new BigDecimal("20000"))
                .amountOutstanding(BigDecimal.valueOf(20322))
                .cohort(Cohort.builder().id(cohortId).build())
                .loanee(Loanee.builder()
                        .userIdentity(UserIdentity.builder().email(TestUtils.generateEmail(7)).build())
                        .build())
                .paymentDateTime(LocalDateTime.now())
                .interestIncurred(BigDecimal.ZERO)
                .build();
    }
    public static RepaymentHistory buildRepaymentHistory(String cohortId, String amountPaid, LocalDateTime paymentTime) {
        return RepaymentHistory.builder()
                .modeOfPayment(ModeOfPayment.CASH)
                .amountPaid(new BigDecimal(amountPaid))
                .amountOutstanding(BigDecimal.valueOf(20322))
                .cohort(Cohort.builder().id(cohortId).build())
                .loanee(Loanee.builder()
                        .userIdentity(UserIdentity.builder().email(TestUtils.generateEmail(7)).build())
                        .build())
                .paymentDateTime(paymentTime)
                .interestIncurred(BigDecimal.ZERO)
                .build();
    }

    public static CohortLoanee buildCohortLoanee(Loanee loanee, Cohort cohort,LoaneeLoanDetail loaneeLoanDetail,String createdBy) {
        return CohortLoanee.builder()
                .cohort(cohort)
                .loanee(loanee)
                .createdBy(createdBy)
                .loaneeLoanDetail(loaneeLoanDetail)
                .deferralApproved(false)
                .deferralRequested(false)
                .loaneeStatus(LoaneeStatus.ADDED)
                .createdAt(LocalDateTime.now())
                .onboardingMode(OnboardingMode.EMAIL_REFERRED)
                .uploadedStatus(UploadedStatus.INVITED)
                .build();
    }

    public static LoanReferral buildLoanReferral(CohortLoanee cohortLoanee,LoanReferralStatus loanReferralStatus) {
        return LoanReferral.builder().loanReferralStatus(loanReferralStatus)
                .cohortLoanee(cohortLoanee).reasonForDeclining("E no consign you").build();
    }

    public static Loan buildLoan(String id) {
        return Loan.builder().loanOfferId(id)
                .loanAccountId(id)
                .startDate(LocalDateTime.now())
                .loanStatus(LoanStatus.PERFORMING)
                .build();
    }

    public static CohortLoanDetail buildCohortLoanDetail(Cohort elites) {
        return CohortLoanDetail.builder()
                .amountReceived(BigDecimal.valueOf(30000))
                .outstandingAmount(BigDecimal.valueOf(30000))
                .amountRequested(BigDecimal.valueOf(30000))
                .amountRepaid(BigDecimal.valueOf(30000))
                .interestIncurred(BigDecimal.valueOf(100000))
                .cohort(elites).build();
    }

    public static ProgramLoanDetail buildProgramLoanDetail(Program program) {
        return ProgramLoanDetail.builder()
                .program(program)
                .amountReceived(BigDecimal.valueOf(30000))
                .outstandingAmount(BigDecimal.valueOf(30000))
                .amountRequested(BigDecimal.valueOf(30000))
                .amountRepaid(BigDecimal.valueOf(30000))
                .interestIncurred(BigDecimal.valueOf(100000))
                .build();
    }


    public static OrganizationLoanDetail buildOrganizationLoanDetail(OrganizationIdentity organizationIdentity) {
        return OrganizationLoanDetail.builder()
                .organization(organizationIdentity)
                .amountReceived(BigDecimal.valueOf(30000))
                .outstandingAmount(BigDecimal.valueOf(30000))
                .amountRequested(BigDecimal.valueOf(30000))
                .amountRepaid(BigDecimal.valueOf(30000))
                .interestIncurred(BigDecimal.valueOf(100000))
                .build();
    }

    public static DailyInterest buildDailyInterest(LoaneeLoanDetail loaneeLoanDetail) {
        return DailyInterest.builder()
                .interest(BigDecimal.valueOf(5000.00))
                .createdAt(LocalDateTime.now())
                .loaneeLoanDetail(loaneeLoanDetail)
                .build();
    }

    public static MonthlyInterest buildMonthlyInterest(LoaneeLoanDetail loaneeLoanDetail) {
        return MonthlyInterest.builder()
                .interest(BigDecimal.valueOf(5000.00))
                .createdAt(LocalDateTime.now())
                .loaneeLoanDetail(loaneeLoanDetail)
                .build();
    }

    public static LoaneeLoanAggregate buildLoaneeLoanAggregate(Loanee loanee) {
        return LoaneeLoanAggregate.builder()
                .id(testId)
                .historicalDebt(BigDecimal.valueOf(5000.00))
                .totalAmountOutstanding(BigDecimal.valueOf(5000.00))
                .numberOfLoans(2)
                .loanee(loanee)
                .totalAmountRepaid(BigDecimal.valueOf(500.00))
                .build();
    }

    public static CooperateFinancier buildCooperateFinancier(Financier financier, Cooperation cooperation) {
        return CooperateFinancier.builder()
                .id(testId)
                .activationStatus(ActivationStatus.ACTIVE)
                .financier(financier)
                .cooperate(cooperation)
                .build();
    }

    public static InstituteMetrics createInstituteMetrics(OrganizationIdentity organizationIdentity) {
        return InstituteMetrics.builder()
                .id(testId)
                .numberOfCohort(0)
                .numberOfLoanees(0)
                .stillInTraining(0)
                .organization(organizationIdentity)
                .build();
    }

    public static Demography buildDemography() {
        return Demography.builder()
                .age17To25Count(2)
                .age25To35Count(3)
                .age35To45Count(2)
                .femaleCount(2)
                .maleCount(3)
                .totalGenderCount(5)
                .northCentralCount(2)
                .northEastCount(3)
                .northWestCount(4)
                .southWestCount(5)
                .southEastCount(6)
                .southSouthCount(6)
                .oLevelCount(3)
                .tertiaryCount(2)
                .name("Demo")
                .build();
    }

    private void mockValues(){
        //          if (isTestIdentityNumber(identityVerification)){
//        log.info("Bvn is for testing bvn: {}", identityVerification.getDecryptedNin());
//        return VerificationMock.createPremblyBvnTestResponse();
//    }
//            if (isTestIdentityNumber(identityVerification)){
//        log.info("Nin is for testing nin: {}", identityVerification.getDecryptedNin());
//        return VerificationMock.createPremblyNinTestResponse();
//    }
//private boolean isTestIdentityNumber(IdentityVerification identityVerification) {
//    log.info("Checking if identity number is for test : {}", identityVerification.getDecryptedNin().equals("01") && identityVerification.getDecryptedBvn().equals("01"));
//    return identityVerification.getDecryptedNin().startsWith("01") && identityVerification.getDecryptedBvn().startsWith("01");
//}

    }
}
