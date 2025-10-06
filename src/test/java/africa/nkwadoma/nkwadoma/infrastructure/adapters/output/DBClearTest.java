package africa.nkwadoma.nkwadoma.infrastructure.adapters.output;

import africa.nkwadoma.nkwadoma.application.ports.output.education.CohortLoaneeOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.OrganizationEmployeeIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.loanmanagement.LoanReferralOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.loanmanagement.loanProduct.LoanProductOutputPort;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Slf4j
public class DBClearTest {

    @Autowired
    private LoanProductOutputPort loanProductOutputPort;

    @Autowired
    private CohortLoaneeOutputPort cohortLoaneeOutputPort;
    @Autowired
    private OrganizationEmployeeIdentityOutputPort organizationEmployeeIdentityOutputPort;
    @Autowired
    private LoanReferralOutputPort loanReferralOutputPort;

    @Test
    public void deleteAllTest(){
//        LoanProduct createdLoanProduct = TestData.buildTestLoanProduct();
//
//        Page<LoanProduct> loanProducts = loanProductOutputPort.findAllLoanProduct(createdLoanProduct);
//        loanProducts
//                .forEach(loanProduct -> {
//                    try {
//                        loanProductOutputPort.deleteById(loanProduct.getId());
//                    } catch (MeedlException e) {
//                        log.error("Error------> : ",e);
//                    }
//                });
//
//        organizationEmployeeIdentityOutputPort.deleteAllEmployee();
//        loanReferralOutputPort.deleteAll();
//        cohortLoaneeOutputPort.deleteAll();
}
}
