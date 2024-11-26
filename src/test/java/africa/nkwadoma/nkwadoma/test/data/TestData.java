package africa.nkwadoma.nkwadoma.test.data;

import africa.nkwadoma.nkwadoma.domain.enums.*;
import africa.nkwadoma.nkwadoma.domain.model.education.LoanBreakdown;
import africa.nkwadoma.nkwadoma.domain.model.education.LoanDetail;
import africa.nkwadoma.nkwadoma.domain.model.education.Program;
import africa.nkwadoma.nkwadoma.domain.model.education.ServiceOffering;
import africa.nkwadoma.nkwadoma.domain.model.identity.OrganizationEmployeeIdentity;
import africa.nkwadoma.nkwadoma.domain.model.identity.OrganizationIdentity;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import africa.nkwadoma.nkwadoma.domain.model.loan.Loanee;
import africa.nkwadoma.nkwadoma.domain.model.loan.LoaneeLoanDetail;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class TestData {
    private static final String testId = "ead0f7cb-5483-4bb8-b271-813970a9c368";

    public static UserIdentity createTestUserIdentity(String email){
        UserIdentity userIdentity = new UserIdentity();
        userIdentity.setFirstName("Peter");
        userIdentity.setLastName("Mark");
        userIdentity.setEmail(email);
        userIdentity.setPhoneNumber("090876536217");
        userIdentity.setId(testId);
        userIdentity.setCreatedBy(testId);
        userIdentity.setRole(IdentityRole.LOANEE);
        return userIdentity;
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
