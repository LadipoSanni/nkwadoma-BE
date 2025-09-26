package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.meedlportfolio;

import africa.nkwadoma.nkwadoma.application.ports.output.meedlportfolio.PlatformRequestOutputPort;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.meedlPortfolio.PlatformRequest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Slf4j
class PlatformRequestAdapterTest {

    @Autowired
    private PlatformRequestOutputPort platformRequestOutputPort;

    private PlatformRequest platformRequest;
    private String requestId;

    @BeforeAll
    void setUp() {
        // Build a valid request
        platformRequest = PlatformRequest.builder()
                .obligorLoanLimit(BigDecimal.TEN)
                .createdBy("test-user")
                .requestTime(LocalDateTime.now())
                .pageNumber(0)
                .pageSize(10)
                .build();
        log.info("Setup complete for PlatformRequestAdapterTest");
    }

    @Test
    @Order(1)
    void save() {
        PlatformRequest saved = null;
        try {
            saved = platformRequestOutputPort.save(platformRequest);
        } catch (MeedlException e) {
            log.error("Error saving platform request", e);
            throw new RuntimeException(e);
        }
        assertNotNull(saved);
        assertNotNull(saved.getId());
        log.info("Saved platform request: {}", saved);
        requestId = saved.getId();
    }

    @Test
    void saveWithNull() {
        assertThrows(MeedlException.class, () -> platformRequestOutputPort.save(null));
    }

    @Test
    @Order(2)
    void viewAll() {
        Page<PlatformRequest> page = null;
        try {
            page = platformRequestOutputPort.viewAll(platformRequest);
        } catch (MeedlException e) {
            log.error("Error viewing all platform requests", e);
            throw new RuntimeException(e);
        }
        assertNotNull(page);
        assertFalse(page.isEmpty());
        log.info("Viewed all platform requests, total: {}", page.getTotalElements());
    }

    @Test
    void viewAllWithInvalidPage() {
        PlatformRequest invalidRequest = PlatformRequest.builder()
                .pageNumber(-1)
                .pageSize(10)
                .build();
        assertThrows(MeedlException.class, () -> platformRequestOutputPort.viewAll(invalidRequest));
    }

    @Test
    @Order(3)
    void viewDetail() {
        PlatformRequest found = null;
        try {
            PlatformRequest req = PlatformRequest.builder().id(requestId).build();
            found = platformRequestOutputPort.viewDetail(req);
        } catch (MeedlException e) {
            log.error("Error viewing detail", e);
            throw new RuntimeException(e);
        }
        assertNotNull(found);
        assertEquals(requestId, found.getId());
        log.info("Viewed detail for request: {}", found);
    }

    @Test
    void viewDetailWithInvalidId() {
        PlatformRequest req = PlatformRequest.builder().id("invalid-uuid").build();
        assertThrows(MeedlException.class, () -> platformRequestOutputPort.viewDetail(req));
    }

    @Test
    void viewDetailNotFound() {
        PlatformRequest req = PlatformRequest.builder().id(UUID.randomUUID().toString()).build();
        assertThrows(MeedlException.class, () -> platformRequestOutputPort.viewDetail(req));
    }

    @Test
    @Order(4)
    void deleteById() {
        try {
            PlatformRequest req = PlatformRequest.builder().id(requestId).build();
            platformRequestOutputPort.deleteById(req);
        } catch (MeedlException e) {
            log.error("Error deleting platform request", e);
            throw new RuntimeException(e);
        }
        PlatformRequest req = PlatformRequest.builder().id(requestId).build();
        assertThrows(MeedlException.class, () -> platformRequestOutputPort.viewDetail(req));
    }

    @Test
    void deleteByIdWithInvalidId() {
        PlatformRequest req = PlatformRequest.builder().id("invalid-uuid").build();
        assertThrows(MeedlException.class, () -> platformRequestOutputPort.deleteById(req));
    }

    @AfterAll
    void tearDown() {
        log.info("Completed PlatformRequestAdapterTest");
    }
}
