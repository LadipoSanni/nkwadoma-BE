package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.loanBook;

import africa.nkwadoma.nkwadoma.application.ports.output.loanBook.LoanBookOutputPort;
import africa.nkwadoma.nkwadoma.domain.model.loan.LoanBook;
import africa.nkwadoma.nkwadoma.testUtilities.data.TestData;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Slf4j
public class LoanBookAdapterTest {
    @Autowired
    private LoanBookOutputPort loanBookOutputPort;
    private final String absoluteCSVFilePath = "/Users/admin/nkwadoma-BE/src/test/java/africa/nkwadoma/nkwadoma/infrastructure/adapters/output/loanBook/";
    private final String CSVName = "loanBook.csv";
    private LoanBook loanBook;
    @BeforeAll
    void setUp() throws IOException {
        Path filePath = Path.of(absoluteCSVFilePath+CSVName);

        Files.write(filePath, List.of(
                "Name,Age,Location",
                "John,30,Nigeria",
                "Jane,25,Kenya"
        ));
        String loanBookName = "Loan Book Meedl";
        loanBook = TestData.buildLoanBook(loanBookName, absoluteCSVFilePath+CSVName );
    }
    @Test
    void upLoadExcelSheet() {
        loanBookOutputPort.upLoadFile(loanBook);
        Page<LoanBook> allFoundLoanBook = loanBookOutputPort.search(loanBook.getName());
        assertNotNull(allFoundLoanBook);
    }


}
