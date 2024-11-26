package africa.nkwadoma.nkwadoma.test.data;

import africa.nkwadoma.nkwadoma.domain.enums.IdentityRole;
import africa.nkwadoma.nkwadoma.domain.enums.Industry;
import africa.nkwadoma.nkwadoma.domain.model.education.ServiceOffering;
import africa.nkwadoma.nkwadoma.domain.model.identity.OrganizationEmployeeIdentity;
import africa.nkwadoma.nkwadoma.domain.model.identity.OrganizationIdentity;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import africa.nkwadoma.nkwadoma.domain.model.loan.Loanee;
import africa.nkwadoma.nkwadoma.domain.model.loan.LoaneeLoanDetail;
import africa.nkwadoma.nkwadoma.domain.model.loan.Loan;
import africa.nkwadoma.nkwadoma.domain.model.loan.Loanee;
import africa.nkwadoma.nkwadoma.domain.model.loan.LoaneeLoanDetail;

import java.math.BigDecimal;
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
        organizationIdentity.setPhoneNumber("09876365713");
        organizationIdentity.setInvitedDate(LocalDateTime.now().toString());
        organizationIdentity.setWebsiteAddress("testdata.org");
        organizationIdentity.setOrganizationEmployees(employeePeter);
        organizationIdentity.setPageSize(10);
        organizationIdentity.setPageNumber(0);

        return organizationIdentity;
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
        Loan loan = new Loan();
        loan.setLoanAccountId("account id");
        loan.setStartDate(LocalDateTime.now());
        loan.setLoanee(loanee);
        loan.setLoaneeId(testId);
        loan.setLoanOfferId(testId);

        Loan
        return loan;
    }
    public static LoaneeLoanDetail createTestLoaneeLoanDetail(){
        return LoaneeLoanDetail.builder()
                .amountRequested(BigDecimal.valueOf(9000000.00))
                .initialDeposit(BigDecimal.valueOf(3000000.00))
                .build();
    }

}
