package africa.nkwadoma.nkwadoma.domain.validation;

import africa.nkwadoma.nkwadoma.application.ports.output.education.LoaneeOutputPort;
import africa.nkwadoma.nkwadoma.domain.model.loan.loanBook.LoanBook;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@AllArgsConstructor
public class LoanBookValidator {
    private final LoaneeOutputPort loaneeOutputPort;

    public void validateUserDataUploadFile(LoanBook loanBook, List<Map<String, String>> data, List<String> requiredHeaders) {

    }
    public void validateUserDataFileHeader(LoanBook loanBook, List<String> requiredHeaders, Map<String, Integer> headerIndexMap){
        for (String required : requiredHeaders) {
            if (!headerIndexMap.containsKey(required)) {
//                throw new MeedlException("Missing required column: " + required);
            }
        }
    }
    public void verifyUserExistInCohort(LoanBook loanBook){

    }
}
