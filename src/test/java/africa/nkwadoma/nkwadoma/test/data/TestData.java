package africa.nkwadoma.nkwadoma.test.data;

import africa.nkwadoma.nkwadoma.domain.enums.*;
import africa.nkwadoma.nkwadoma.domain.model.education.LoanBreakdown;
import africa.nkwadoma.nkwadoma.domain.model.education.LoanDetail;
import africa.nkwadoma.nkwadoma.domain.model.education.Program;
import africa.nkwadoma.nkwadoma.domain.model.education.ServiceOffering;
import africa.nkwadoma.nkwadoma.domain.model.identity.IdentityVerification;
import africa.nkwadoma.nkwadoma.domain.model.identity.OrganizationEmployeeIdentity;
import africa.nkwadoma.nkwadoma.domain.model.identity.OrganizationIdentity;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import africa.nkwadoma.nkwadoma.domain.model.loan.Loanee;
import africa.nkwadoma.nkwadoma.domain.model.loan.LoaneeLoanDetail;
import africa.nkwadoma.nkwadoma.domain.model.loan.Loan;

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
                .amountRequested(BigDecimal.valueOf(9000000.00))
                .initialDeposit(BigDecimal.valueOf(3000000.00))
                .build();
    }
    public static OrganizationEmployeeIdentity createOrganizationEmployeeIdentityTestData(UserIdentity identity){
        OrganizationEmployeeIdentity organizationEmployeeIdentity = new OrganizationEmployeeIdentity();
        organizationEmployeeIdentity.setOrganization(testId);
        organizationEmployeeIdentity.setMeedlUser(identity);
        return organizationEmployeeIdentity;
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
}
