package africa.nkwadoma.nkwadoma.test.data;

import africa.nkwadoma.nkwadoma.domain.enums.IdentityRole;
import africa.nkwadoma.nkwadoma.domain.enums.Industry;
import africa.nkwadoma.nkwadoma.domain.model.education.ServiceOffering;
import africa.nkwadoma.nkwadoma.domain.model.identity.OrganizationEmployeeIdentity;
import africa.nkwadoma.nkwadoma.domain.model.identity.OrganizationIdentity;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import africa.nkwadoma.nkwadoma.domain.model.loan.Loanee;
import africa.nkwadoma.nkwadoma.domain.model.loan.LoaneeLoanDetail;

import java.math.BigDecimal;
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

    public static Loanee createLoaneeTestData(UserIdentity userIdentity, LoaneeLoanDetail loaneeLoanDetail) {
        return Loanee.builder().userIdentity(userIdentity).
                 cohortId("3a6d1124-1349-4f5b-831a-ac269369a90f").createdBy(userIdentity.getCreatedBy())
                .loaneeLoanDetail(loaneeLoanDetail).build();
    }

    public static LoaneeLoanDetail createLoaneeLoanDetail() {
        return LoaneeLoanDetail.builder().amountRequested(BigDecimal.valueOf(9000000.00)).
                initialDeposit(BigDecimal.valueOf(3000000.00)).build();
    }

}
