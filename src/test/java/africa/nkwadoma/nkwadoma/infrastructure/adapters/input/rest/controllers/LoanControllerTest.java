package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.controllers;

import africa.nkwadoma.nkwadoma.application.ports.input.loanmanagement.CreateLoanProductUseCase;
import africa.nkwadoma.nkwadoma.application.ports.input.loanmanagement.ViewLoanProductUseCase;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.loan.LoanProduct;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.controllers.loanmanagement.LoanController;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.loanManagement.LoanProductRequest;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.appResponse.ApiResponse;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.loanManagement.LoanProductResponse;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.mapper.loanManagement.LoanProductRestMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Slf4j
class LoanControllerTest {
    @Mock
    private CreateLoanProductUseCase createLoanProductUseCase;
    @Mock
    private ViewLoanProductUseCase viewLoanProductUseCase;
    @Mock
    private LoanProductRestMapper loanProductMapper;
    @InjectMocks
    private LoanController loanController;

    private LoanProductRequest loanProductRequest;
    private LoanProduct loanProduct;
    private LoanProductResponse loanProductResponse;
    private Jwt jwt;
    @BeforeEach
    void setUp() {
        loanProductRequest = new LoanProductRequest();
        loanProductRequest.setName("Test Loan Product: unit testing within application");
        loanProductRequest.setMandate("Test: A new mandate for test");
        loanProductRequest.setObligorLoanLimit(new BigDecimal("100"));
        loanProductRequest.setTermsAndCondition("Test: A new loan for test and terms and conditions");
        loanProductRequest.setLoanProductSize(new BigDecimal("1000"));

        loanProduct = new LoanProduct();
        loanProduct.setName("Test Loan Product: unit testing within application");
        loanProduct.setMandate("Test: A new mandate for test");
        loanProduct.setSponsor( "Jack");
        loanProduct.setObligorLoanLimit(new BigDecimal("100"));
        loanProduct.setTermsAndCondition("Test: A new loan for test and terms and conditions");
        loanProduct.setLoanProductSize(new BigDecimal("1000"));

        loanProductResponse = new LoanProductResponse();
        loanProductResponse.setName("Test Loan Product: unit testing within application");
        loanProductResponse.setMandate("Test: A new mandate for test");
        loanProductResponse.setSponsor("Mark");
        loanProductResponse.setObligorLoanLimit(new BigDecimal("100"));
        loanProductResponse.setTermsAndCondition("Test: A new loan for test and terms and conditions");
        loanProductResponse.setLoanProductSize(new BigDecimal("1000"));

        jwt = mock(Jwt.class);
    }

    @Test
    void createLoanProduct() {
        when(loanProductMapper.mapToLoanProduct(loanProductRequest)).thenReturn(loanProduct);
        when(loanProductMapper.mapToLoanProductResponse(loanProduct)).thenReturn(loanProductResponse);

        ResponseEntity<ApiResponse<?>> apiResponse  = null;
        try {
            when(createLoanProductUseCase.createLoanProduct(loanProduct)).thenReturn(loanProduct);
            apiResponse = loanController.createLoanProduct(jwt, loanProductRequest);
        } catch (MeedlException e) {
            log.error("Failed to create loan product {}", e.getMessage());
        }
        assertNotNull(apiResponse);
        assertNotNull(apiResponse.getBody());
        assertEquals(apiResponse.getStatusCode(), HttpStatus.CREATED);
        assertEquals(apiResponse.getBody().getStatusCode(), HttpStatus.CREATED.toString());
        assertNotNull(apiResponse.getBody().getData());

        LoanProductResponse responseBody = (LoanProductResponse) apiResponse.getBody().getData();
        assertEquals(loanProductResponse.getName(), responseBody.getName());
        verify(loanProductMapper, times(1)).mapToLoanProduct(loanProductRequest);
        verify(loanProductMapper, times(1)).mapToLoanProductResponse(loanProduct);

        try {
            verify(createLoanProductUseCase, times(1)).createLoanProduct(loanProduct);
        } catch (MeedlException e) {
            log.error("Failed to create loan product.... verify number of times {}", e.getMessage());
        }
    }
    @Test
    void createLoanProductWithInvalidDetail() {
        loanProductRequest.setName(null);
        loanProductRequest.setMandate(StringUtils.SPACE);
        loanProductRequest.setTermsAndCondition(StringUtils.EMPTY);

        when(loanProductMapper.mapToLoanProduct(loanProductRequest)).thenReturn(loanProduct);
        try {
            when(createLoanProductUseCase.createLoanProduct(loanProduct)).thenThrow(MeedlException.class);
            assertThrows(MeedlException.class, ()-> loanController.createLoanProduct(jwt, loanProductRequest));
            verify(loanProductMapper, times(1)).mapToLoanProduct(loanProductRequest);
        } catch (MeedlException e) {
            log.error("Failed to create loan product with invalid details {}", e.getMessage());
        }
    }
    @Test
    void viewLoanProductDetailsById() {
        when(loanProductMapper.mapToLoanProductResponse(loanProduct)).thenReturn(loanProductResponse);
        ResponseEntity<ApiResponse<?>> apiResponse = null;
        try {
            when(viewLoanProductUseCase.viewLoanProductDetailsById(loanProduct.getId())).thenReturn(loanProduct);
            apiResponse = loanController.viewLoanProductDetailsById(loanProduct.getId());
        } catch (MeedlException e) {
            log.error("Failed to view loan product by id {}", e.getMessage());
        }
        assertNotNull(apiResponse);
        assertNotNull(apiResponse.getBody());
        assertEquals(apiResponse.getStatusCode(), HttpStatus.OK);
        assertEquals(apiResponse.getBody().getStatusCode(), HttpStatus.OK.toString());
        assertNotNull(apiResponse.getBody().getData());

        LoanProductResponse responseBody = (LoanProductResponse) apiResponse.getBody().getData();
        assertEquals(loanProductResponse.getName(), responseBody.getName());
        verify(loanProductMapper, times(1)).mapToLoanProductResponse(loanProduct);
        try {
            verify(viewLoanProductUseCase, times(1)).viewLoanProductDetailsById(loanProduct.getId());
        } catch (MeedlException e) {
            log.error("Error while verifying viewLoanProductUseCase {}", e.getMessage());
        }
    }
    @Test
    void viewLoanProductDetailsByIdWithInvalidId() {
        try {
            when(viewLoanProductUseCase.viewLoanProductDetailsById(null)).thenThrow(MeedlException.class);
            assertThrows(MeedlException.class, ()-> loanController.viewLoanProductDetailsById(null));
            verify(viewLoanProductUseCase, times(1)).viewLoanProductDetailsById(null);
        } catch (MeedlException e) {
            log.error("Failed to view loan product details {}", e.getMessage());
        }
    }

}