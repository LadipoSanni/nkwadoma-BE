package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.financier;

import africa.nkwadoma.nkwadoma.application.ports.output.financier.BeneficialOwnerOutputPort;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.financier.BeneficialOwner;
import africa.nkwadoma.nkwadoma.testUtilities.data.TestData;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Slf4j
public class BeneficialOwnerAdapterTest {
    @Autowired
    private BeneficialOwnerOutputPort beneficialOwnerOutputPort;
    private BeneficialOwner beneficialOwner;
    private String beneficialOwnerId;
    @BeforeAll
    void setUp() {
        beneficialOwner = TestData.buildBeneficialOwner(100);
    }

    @Test
    void saveBeneficialOwnerWithNegativePercentageOwnershipOrShare(){
        beneficialOwner.setPercentageOwnershipOrShare(-2);
        assertThrows(MeedlException.class, () -> beneficialOwnerOutputPort.save(beneficialOwner));
    }

    @Test
    void saveBeneficialOwnerWithPercentageOwnershipOrShareGreaterThan100(){
        beneficialOwner.setPercentageOwnershipOrShare(101);
        assertThrows(MeedlException.class, () -> beneficialOwnerOutputPort.save(beneficialOwner));
    }

    @Test
    @Order(1)
    void saveBeneficialOwner() {
        BeneficialOwner savedBeneficialOwner = null;
        try {
            savedBeneficialOwner = beneficialOwnerOutputPort.save(beneficialOwner);
        } catch (MeedlException e) {
            log.error("",e);
            throw new RuntimeException(e);
        }
        assertNotNull(savedBeneficialOwner);
        assertNotNull(savedBeneficialOwner.getId());
        assertEquals(beneficialOwner.getEntityName(), savedBeneficialOwner.getEntityName());
        log.info("Saved cooperation {}", savedBeneficialOwner);
        beneficialOwnerId = savedBeneficialOwner.getId();
    }
    @Test
    void saveBeneficialOwnerWithNull(){
        assertThrows(MeedlException.class, () -> beneficialOwnerOutputPort.save(null));
    }
    @ParameterizedTest
    @ValueSource(strings = {StringUtils.SPACE, StringUtils.EMPTY})
    void saveBeneficialOwnerWithInvalidName(String name){
        BeneficialOwner beneficialOwner = TestData.buildBeneficialOwner(100);
        beneficialOwner.setBeneficialOwnerFirstName(name);
        assertThrows(MeedlException.class, () -> beneficialOwnerOutputPort.save(beneficialOwner));
    }

    @Test
    @Order(2)
    void findBeneficialOwnerById() {
        BeneficialOwner foundBeneficialOwner = null;
        try {
            foundBeneficialOwner = beneficialOwnerOutputPort.findById(beneficialOwnerId);
        } catch (MeedlException e) {
            log.error("",e);
            throw new RuntimeException(e);
        }
        assertNotNull(foundBeneficialOwner);
        assertEquals(beneficialOwner.getEntityName(), foundBeneficialOwner.getEntityName());
        log.info("found beneficial owner {}", foundBeneficialOwner);
    }
    @Test
    @Order(3)
    void deleteBeneficialOwnerById() {
        try {
            beneficialOwnerOutputPort.deleteById(beneficialOwnerId);
        } catch (MeedlException e) {
            log.error("",e);
            throw new RuntimeException(e);
        }
        assertThrows(MeedlException.class, ()-> beneficialOwnerOutputPort.findById(beneficialOwnerId));
    }
}
