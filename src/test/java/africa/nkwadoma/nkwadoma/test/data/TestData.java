package africa.nkwadoma.nkwadoma.test.data;

import africa.nkwadoma.nkwadoma.domain.enums.IdentityRole;
import africa.nkwadoma.nkwadoma.domain.enums.Industry;
import africa.nkwadoma.nkwadoma.domain.model.education.ServiceOffering;
import africa.nkwadoma.nkwadoma.domain.model.identity.OrganizationEmployeeIdentity;
import africa.nkwadoma.nkwadoma.domain.model.identity.OrganizationIdentity;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import africa.nkwadoma.nkwadoma.domain.model.loan.Loan;
import africa.nkwadoma.nkwadoma.domain.model.loan.Loanee;
import africa.nkwadoma.nkwadoma.domain.model.loan.LoaneeLoanDetail;

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
        organizationIdentity.setPhoneNumber("09876365713");
        organizationIdentity.setInvitedDate(LocalDateTime.now().toString());
        organizationIdentity.setWebsiteAddress("testdata.org");
        organizationIdentity.setOrganizationEmployees(employeePeter);
        organizationIdentity.setPageSize(10);
        organizationIdentity.setPageNumber(0);

        return organizationIdentity;
    }

    public static Loanee createTestLoanee(UserIdentity userIdentity, LoaneeLoanDetail loaneeLoanDetail){
        Loanee loanee = new Loanee();
        loanee.setUserIdentity(userIdentity);
        loanee.setCreatedBy(testId);
        loanee.setCohortId(testId);
        loanee.setLoaneeLoanDetail(loaneeLoanDetail);
        return loanee;
    }
    public static Loan createTestLoan(Loanee loanee){
        Loan loan = new Loan();
        loan.setLoanAccountId("account id");
        loan.setStartDate(LocalDateTime.now());
        loan.setLoanee(loanee);
        return loan;
    }

}
