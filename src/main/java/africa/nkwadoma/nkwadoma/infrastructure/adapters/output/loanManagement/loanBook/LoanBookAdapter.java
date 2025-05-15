package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.loanManagement.loanBook;

import africa.nkwadoma.nkwadoma.application.ports.input.education.CohortUseCase;
import africa.nkwadoma.nkwadoma.application.ports.output.education.LoaneeOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.IdentityManagerOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.UserIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.loanManagement.LoaneeLoanDetailsOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.loanManagement.loanBook.LoanBookOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.IdentityRole;
import africa.nkwadoma.nkwadoma.domain.enums.constants.CohortMessages;
import africa.nkwadoma.nkwadoma.domain.enums.constants.ProgramMessages;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.education.Cohort;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import africa.nkwadoma.nkwadoma.domain.model.loan.LoanBook;
import africa.nkwadoma.nkwadoma.domain.model.loan.Loanee;
import africa.nkwadoma.nkwadoma.domain.model.loan.LoaneeLoanDetail;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class LoanBookAdapter implements LoanBookOutputPort {

    private final UserIdentityOutputPort userIdentityOutputPort;
    private final LoaneeOutputPort loaneeOutputPort;
    private final LoaneeLoanDetailsOutputPort loaneeLoanDetailsOutputPort;
    private final IdentityManagerOutputPort identityManagerOutputPort;
    private final CohortUseCase cohortUseCase;

    @Override
    public LoanBook upLoadFile(LoanBook loanBook) throws MeedlException {
        File file = loanBook.getFile();
        List<String[]> data;

        if (file.getName().endsWith(".csv")) {
            try {
                data = readCSV(file);
            }catch (IOException e){
                log.error("Error occurred reading csv",e);
                throw new MeedlException(e.getMessage());
            }
        } else if (file.getName().endsWith(".xlsx")) {
            try{
                data = readExcel(file);
            }catch (IOException e){
                log.error("Error occurred reading excel",e);
                throw new MeedlException(e.getMessage());
            }
        } else {
            log.error("Unsupported file type.");
            throw new MeedlException("Unsupported file type.");
        }
        log.info("Loan book read is {}", data);
        Cohort savedCohort = createCohort(loanBook.getCohort());
        List<Loanee> convertedLoanees = convertToLoanees(data, savedCohort);
        loanBook.setLoanees(convertedLoanees);
        return loanBook;
    }

    private Cohort createCohort(Cohort cohort) throws MeedlException {
        MeedlValidator.validateObjectInstance(cohort, CohortMessages.COHORT_CANNOT_BE_EMPTY.getMessage());
        MeedlValidator.validateUUID(cohort.getProgramId(), ProgramMessages.INVALID_PROGRAM_ID.getMessage());
        return cohortUseCase.createCohort(cohort);
    }

    private List<Loanee> convertToLoanees(List<String[]> data, Cohort cohort) {
        List<Loanee> loanees = new ArrayList<>();

        log.info("Started creating loanee data gotten from file upload");
        for (String[] row : data) {

            UserIdentity userIdentity = UserIdentity.builder()
                    .firstName(row[0].trim())
                    .lastName(row[1].trim())
                    .email(row[2].trim())
                    .phoneNumber(row[3].trim())
                    .role(IdentityRole.LOANEE)
                    .createdAt(LocalDateTime.now())
                    .createdBy("73de0343-be48-4967-99ea-10be007e4347")
                    .build();
            log.info("Created user identity with email {}", userIdentity.getEmail());
            LoaneeLoanDetail loaneeLoanDetail = LoaneeLoanDetail.builder()
                    .initialDeposit(new BigDecimal(row[5].trim()))
                    .amountRequested(new BigDecimal(row[6].trim()))
                    .amountReceived(new BigDecimal(row[7].trim()))
                    .build();

            Loanee loanee = Loanee.builder()
                    .userIdentity(userIdentity)
                    .loaneeLoanDetail(loaneeLoanDetail)
                    .cohortId(cohort.getId())
                    .build();

            loanees.add(loanee);
        }
        return savedData(loanees);
    }
    private List<Loanee> savedData(List<Loanee> loanees){
        List<Loanee> savedLoanees = new ArrayList<>();
        log.info("Started saving converted data of loanees");
        for (Loanee loanee : loanees){
            try {
                UserIdentity userIdentity = identityManagerOutputPort.createUser(loanee.getUserIdentity());
                userIdentityOutputPort.save(userIdentity);
                LoaneeLoanDetail savedLoaneeLoanDetails = loaneeLoanDetailsOutputPort.save(loanee.getLoaneeLoanDetail());

                loanee.setUserIdentity(userIdentity);
                loanee.setLoaneeLoanDetail(savedLoaneeLoanDetails);

                Loanee savedLoanee = loaneeOutputPort.save(loanee);
                savedLoanees.add(savedLoanee);
            } catch (MeedlException e) {
                log.info("Error occurred while saving data .", e);
                throw new RuntimeException(e);
            }
        }
        log.info("Done saving loanee data from file to db. loanees size {}", savedLoanees.size());
        return savedLoanees;
    }

    private List<String[]> readCSV(File file) throws IOException {
        List<String[]> rows = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            br.readLine();
            while ((line = br.readLine()) != null) {
                rows.add(line.split(","));
            }
        }
        return rows;
    }

    private List<String[]> readExcel(File file) throws IOException {
        List<String[]> rows = new ArrayList<>();
        try (FileInputStream fis = new FileInputStream(file);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0);
            boolean headerSkipped = false;
            for (Row row : sheet) {
                if (!headerSkipped) {
                    headerSkipped = true;
                    continue; // skip header
                }
                String[] values = new String[3];
                values[0] = row.getCell(0).getStringCellValue();
                values[1] = String.valueOf((int) row.getCell(1).getNumericCellValue());
                values[2] = row.getCell(2).getStringCellValue();
                rows.add(values);
            }
        }
        return rows;
    }
}
