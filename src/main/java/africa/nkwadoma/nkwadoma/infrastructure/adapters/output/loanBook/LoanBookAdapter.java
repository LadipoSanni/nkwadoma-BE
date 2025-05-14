package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.loanBook;

import africa.nkwadoma.nkwadoma.application.ports.output.loanBook.LoanBookOutputPort;
import africa.nkwadoma.nkwadoma.domain.model.loan.LoanBook;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class LoanBookAdapter implements LoanBookOutputPort {

    @Override
    public LoanBook upLoadFile(LoanBook loanBook) {
        List<String[]> data;

        if (file.getName().endsWith(".csv")) {
            data = readCSV(file);
        } else if (file.getName().endsWith(".xlsx")) {
            data = readExcel(file);
        } else {
            throw new IllegalArgumentException("Unsupported file type.");
        }
    }

    @Override
    public Page<LoanBook> search(String loanBookName) {
        return null;
    }
}
