package africa.nkwadoma.nkwadoma.domain.service.loanManagement;

import africa.nkwadoma.nkwadoma.application.ports.input.loan.CreateLoanProductUseCase;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.UserIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.loan.LoanProductOutputPort;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.education.Program;
import africa.nkwadoma.nkwadoma.domain.model.loan.LoanProduct;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static africa.nkwadoma.nkwadoma.domain.enums.loanEnums.DurationType.Months;
import static africa.nkwadoma.nkwadoma.domain.enums.loanEnums.DurationType.Years;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@Slf4j
@SpringBootTest
@ExtendWith(MockitoExtension.class)
class LoanProductServiceTest {
    @Mock
    private UserIdentityOutputPort userIdentityOutputPort;
    @Mock
    private LoanProductOutputPort loanProductOutputPort;

    @InjectMocks
    private LoanService loanService;
    private LoanProduct loanProduct;

    @BeforeEach
    void setUp() {
        loanProduct = new LoanProduct();
        loanProduct.setName("Test Loan Product: unit testing within application");
        loanProduct.setMandate("Test: A new mandate for test");
        loanProduct.setSponsors(List.of("Mark", "Jack"));
        loanProduct.setObligorLoanLimit(new BigDecimal("100"));
        loanProduct.setTermsAndCondition("Test: A new loan for test and terms and conditions");
        loanProduct.setLoanProductSize(new BigDecimal("1000"));
    }
    @Test
    @Order(1)
    void createLoanProduct() {
        LoanProduct createdLoanProduct = null;
        try {
            loanProduct.setId("uuid.idwith32numeric");
            when(loanService.createLoanProduct(loanProduct)).thenReturn(loanProduct);
            createdLoanProduct = loanService.createLoanProduct(loanProduct);
            assertNotNull(createdLoanProduct);
            log.info(createdLoanProduct.getId());
            assertNotNull(createdLoanProduct.getId());
        } catch (MeedlException exception) {
            log.error(exception.getMessage());
        }
    }


    @Test
    void createLoanProductWithNullLoanProduct(){
        assertThrows(MeedlException.class, () -> loanService.createLoanProduct(null));
    }
    @Test
    void createLoanProductWithNullMandate(){
        loanProduct.setMandate(null);
        assertThrows(MeedlException.class,()-> loanService.createLoanProduct(loanProduct));
    }
    @ParameterizedTest
    @ValueSource(strings = {StringUtils.EMPTY, StringUtils.SPACE})
    void createLoanProductWithInvalidMandate(String name){
        loanProduct.setMandate(name);
        assertThrows(MeedlException.class,()-> loanService.createLoanProduct(loanProduct));
    }
    @Test
    void createLoanProductWithNullLoanProductName(){
        loanProduct.setName(null);
        assertThrows(MeedlException.class,()-> loanService.createLoanProduct((loanProduct)));
    }
    @Test
    void createLoanProductWithNegativeLoanProductSize(){
        loanProduct.setLoanProductSize(new BigDecimal(-1));
        assertThrows(MeedlException.class,()-> loanService.createLoanProduct(loanProduct));
    }
    @Test
    void createLoanProductWithNoObligorLimit(){
        loanProduct.setObligorLoanLimit(new BigDecimal(-1));
        assertThrows(MeedlException.class,()-> loanService.createLoanProduct(loanProduct));
    }
    @Test
    void createLoanProductWithNoTermsAndConditions(){
        loanProduct.setTermsAndCondition(null);
        assertThrows(MeedlException.class,()-> loanService.createLoanProduct(loanProduct));
    }
    @Test
    void deleteLoanProductWithNullRequest(){
        assertThrows(MeedlException.class, ()-> loanService.deleteLoanProductById(null));
    }

    @Test
    void viewAllPrograms() {
        int pageNumber = 2;
        int pageSize = 10;

        Page<LoanProduct> expectedPage = new PageImpl<>(Collections.singletonList(loanProduct), PageRequest.of(pageNumber, pageSize), 1);
            when(loanService.viewAllLoanProduct( pageSize, pageNumber)).
                    thenReturn(new PageImpl<>(List.of(loanProduct)));
            Page<LoanProduct> loanProductPage = loanService.viewAllLoanProduct(pageSize, pageNumber);
            List<LoanProduct> loanProductList = loanProductPage.toList();

            assertNotNull(loanProductPage);
            assertNotNull(loanProductList);
            assertEquals(loanProductList.get(0).getMandate(), loanProduct.getMandate());
            assertEquals(loanProductList.get(0).getName(), loanProduct.getName());
            assertEquals(loanProductList.get(0).getTermsAndCondition(), loanProduct.getTermsAndCondition());

    }
}