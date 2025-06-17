package africa.nkwadoma.nkwadoma.domain.service.loanmanagement.loanbook;

import africa.nkwadoma.nkwadoma.application.ports.input.education.CohortUseCase;
import africa.nkwadoma.nkwadoma.application.ports.input.loanmanagement.*;
import africa.nkwadoma.nkwadoma.application.ports.input.loanmanagement.loanbook.AsynchronousLoanBookProcessingUseCase;
import africa.nkwadoma.nkwadoma.application.ports.input.loanmanagement.loanbook.LoanBookUseCase;
import africa.nkwadoma.nkwadoma.application.ports.input.loanmanagement.loanbook.RepaymentHistoryUseCase;
import africa.nkwadoma.nkwadoma.application.ports.output.education.CohortOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.education.LoaneeOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.IdentityManagerOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.UserIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.loanmanagement.LoanProductOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.loanmanagement.LoaneeLoanDetailsOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.IdentityRole;
import africa.nkwadoma.nkwadoma.domain.enums.constants.CohortMessages;
import africa.nkwadoma.nkwadoma.domain.enums.loanee.LoaneeStatus;
import africa.nkwadoma.nkwadoma.domain.enums.loanee.ModeOfPayment;
import africa.nkwadoma.nkwadoma.domain.enums.loanee.OnboardingMode;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.education.Cohort;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import africa.nkwadoma.nkwadoma.domain.model.loan.*;
import africa.nkwadoma.nkwadoma.domain.model.loan.loanBook.LoanBook;
import africa.nkwadoma.nkwadoma.domain.model.loan.loanBook.RepaymentHistory;
import africa.nkwadoma.nkwadoma.domain.model.notification.MeedlNotification;
import africa.nkwadoma.nkwadoma.domain.validation.LoanBookValidator;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.aes.TokenUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class LoanBookService implements LoanBookUseCase {
    private final UserIdentityOutputPort userIdentityOutputPort;
    private final LoaneeOutputPort loaneeOutputPort;
    private final LoaneeLoanDetailsOutputPort loaneeLoanDetailsOutputPort;
    private final IdentityManagerOutputPort identityManagerOutputPort;
    private final CohortUseCase cohortUseCase;
    private final LoanBookValidator loanBookValidator;
    private final AsynchronousLoanBookProcessingUseCase asynchronousLoanBookProcessingUseCase;
    private final LoaneeUseCase loaneeUseCase;
    private final RespondToLoanReferralUseCase respondToLoanReferralUseCase;
    private final ViewLoanReferralsUseCase viewLoanReferralsUseCase;
    private final CreateLoanProductUseCase createLoanProductUseCase;
    private final LoanRequestUseCase loanRequestUseCase;
    private final LoanOfferUseCase loanOfferUseCase;
    private final RepaymentHistoryUseCase repaymentHistoryUseCase;
    private final TokenUtils tokenUtils;
    private final LoanProductOutputPort loanProductOutputPort;
    private final CohortOutputPort cohortOutputPort;

//    @Async
    @Override
    public LoanBook upLoadUserData(LoanBook loanBook) throws MeedlException {
        asynchronousLoanBookProcessingUseCase.upLoadUserData(loanBook);
        return loanBook;
    }


    @Override
    public void uploadRepaymentRecord(LoanBook repaymentRecordBook) throws MeedlException {
        MeedlValidator.validateObjectInstance(repaymentRecordBook, "Repayment record book cannot be empty.");
        repaymentRecordBook.validateRepaymentRecord();
        List<String> requiredHeaders = getRepaymentRecordUploadRequiredHeaders();

        List<Map<String, String>>  data = readFile(repaymentRecordBook, requiredHeaders);
        repaymentRecordBook.setMeedlNotification(new MeedlNotification());
        log.info("Repayment record book read is {}", data);


        Cohort savedCohort = findCohort(repaymentRecordBook.getCohort());
        repaymentRecordBook.setCohort(savedCohort);
        List<RepaymentHistory> convertedRepaymentHistories = convertToRepaymentHistory(data);
        repaymentRecordBook.setRepaymentHistories(convertedRepaymentHistories);
        List<RepaymentHistory> savedRepaymentHistories = repaymentHistoryUseCase.saveCohortRepaymentHistory(repaymentRecordBook);
        log.info("Repayment record uploaded..");
    }

    private Cohort findCohort(Cohort cohort) throws MeedlException {
        MeedlValidator.validateObjectInstance(cohort, CohortMessages.COHORT_CANNOT_BE_EMPTY.getMessage());
        return cohortUseCase.viewCohortDetails(cohort.getCreatedBy(), cohort.getId());
    }
    private List<RepaymentHistory> convertToRepaymentHistory(List<Map<String, String>>  data) {
        List<RepaymentHistory> repaymentHistories = new ArrayList<>();

        log.info("Started creating Repayment record from data gotten from file upload {}, size {}",data, data.size());
        for (Map<String, String> row  : data) {
        log.info("Payment date extracted {}", row.get("paymentdate").trim());
            RepaymentHistory repaymentHistory = RepaymentHistory.builder()
                    .firstName(row.get("firstname").trim())
                    .lastName(row.get("lastname").trim())
                    .loanee(Loanee.builder().userIdentity(UserIdentity.builder().email(validateUseEmail(row.get("email").trim())).build()).build())
                    .amountPaid(validateMoney(row.get("amountpaid").trim(), "Amount repaid should be properly indicated"))
                    .paymentDateTime(parseFlexibleDateTime(row.get("paymentdate").trim()))
                    .modeOfPayment(validateModeOfPayment(row.get("modeofpayment").trim()))
                    .build();
            log.info("Repayment history model created from file {}", repaymentHistory);
            log.info("Payment date maped from file {}", repaymentHistory.getPaymentDateTime());
            repaymentHistories.add(repaymentHistory);
        }
        return repaymentHistories;
    }

    private String validateUseEmail(String email) {
        try{
            MeedlValidator.validateEmail(email);
        }catch (MeedlException e){

        }
        return email;
    }

    private LocalDateTime parseFlexibleDateTime(String dateStr) {
        log.info("Repayment date before formating {}", dateStr);
        if (dateStr == null || MeedlValidator.isEmptyString(dateStr)) {
            return null;
        }

        dateStr = dateStr.trim().replace("/", "-");
        log.info("Repayment date after formating {}", dateStr);
        List<DateTimeFormatter> formatters = List.of(
                DateTimeFormatter.ISO_LOCAL_DATE_TIME,
                DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"),
                DateTimeFormatter.ofPattern("dd-MM-yyyy"),
                DateTimeFormatter.ofPattern("yyyy-M-d"),
                DateTimeFormatter.ofPattern("d-M-yyyy"),
                DateTimeFormatter.ofPattern("yyyy-M-d")
                );

        for (DateTimeFormatter formatter : formatters) {
            try {
                log.info("The formatter is {} for {}", formatter, dateStr);
                if (formatter == DateTimeFormatter.ISO_LOCAL_DATE_TIME) {
                    log.info("In ISO_LOCAL_DATE_TIME {}",dateStr);
                    return LocalDateTime.parse(dateStr, formatter);
                } else {
                    return LocalDate.parse(dateStr, formatter).atStartOfDay();
                }
            } catch (DateTimeParseException ignored) {
                log.error("Error occurred while converting the format.");
//                return LocalDate.parse(dateStr, formatter).atStartOfDay();
            }
        }

        log.error("The date format was invalid: {}", dateStr);
        return null;
    }

    private ModeOfPayment validateModeOfPayment(String modeOfRepaymentToConvert) {
        if (MeedlValidator.isEmptyString(modeOfRepaymentToConvert)) {
            log.error("Mode of repayment as a string to be converted is empty {}", modeOfRepaymentToConvert);
            //Todo create notification of this error
            modeOfRepaymentToConvert = ModeOfPayment.TRANSFER.name();
        }
        ModeOfPayment modeOfPayment = null;
        try {
            log.info("The mode of repayment uploaded is {}", modeOfRepaymentToConvert);
            modeOfPayment = ModeOfPayment.valueOf(modeOfRepaymentToConvert.toUpperCase());
        } catch (Exception e) {
            log.error("Error converting mode of repayment from string to enum.", e);
            //Todo create notification on error
        }
        return modeOfPayment;
    }


    private BigDecimal validateMoney(String amountTobeConverted, String message) {
        BigDecimal amount = null;
        try {
            amount = new BigDecimal(amountTobeConverted);
            MeedlValidator.validateBigDecimalDataElement(amount);
        } catch (Exception e) {
            log.error("An error occurred while converting string to money {} ", message,e);
            //TODO notification should be made here
        }
        return amount;
    }


    List<Loanee> convertToLoanees(List<Map<String, String>> data, Cohort cohort, String actorId) {
        List<Loanee> loanees = new ArrayList<>();

        for (Map<String, String> row : data) {
            UserIdentity userIdentity = UserIdentity.builder()
                    .firstName(row.get("firstname"))
                    .lastName(row.get("lastname"))
                    .email(row.get("email"))
                    .phoneNumber(row.get("phonenumber"))
                    .dateOfBirth(row.get("dob"))
                    .role(IdentityRole.LOANEE)
                    .createdAt(LocalDateTime.now())
                    .bvn(encryptValue(row.get("bvn")))
                    .nin(encryptValue(row.get("nin")))
                    .createdBy(actorId)
                    .build();

            LoaneeLoanDetail loaneeLoanDetail = LoaneeLoanDetail.builder()
                    .initialDeposit(new BigDecimal(row.get("initialdeposit")))
                    .amountRequested(new BigDecimal(row.get("amountrequested")))
                    .amountReceived(new BigDecimal(row.get("amountreceived")))
                    .build();
            log.info("loan product name found from csv {}", row.get("loanproduct"));
            Loanee loanee = Loanee.builder()
                    .userIdentity(userIdentity)
                    .loaneeLoanDetail(loaneeLoanDetail)
                    .loaneeStatus(LoaneeStatus.ADDED)
                    .onboardingMode(OnboardingMode.FILE_UPLOADED_FOR_DISBURSED_LOANS)
                    .cohortId(cohort.getId())
                    .cohortName(row.get("loanproduct"))
                    .build();

            loanees.add(loanee);
        }

        return savedData(loanees);
    }

    private String encryptValue(String value) {
        try {
            MeedlValidator.validateBvn(value);
            return tokenUtils.encryptAES(value);
        } catch (MeedlException e) {
            log.error("Unable to encrypt value {}", value);
        }
        return null;
    }

    private List<Loanee> savedData(List<Loanee> loanees){
        List<Loanee> savedLoanees = new ArrayList<>();
        log.info("Started saving converted data of loanees");
        for (Loanee loanee : loanees){
            try {
                UserIdentity userIdentity = identityManagerOutputPort.createUser(loanee.getUserIdentity());
                userIdentityOutputPort.save(userIdentity);
                LoaneeLoanDetail savedLoaneeLoanDetail = loaneeLoanDetailsOutputPort.save(loanee.getLoaneeLoanDetail());
                loanee.getLoaneeLoanDetail().setId(savedLoaneeLoanDetail.getId());
                log.info("Loanee's loan details after saving in file upload {}", savedLoaneeLoanDetail);
                loanee.setUserIdentity(userIdentity);
                loanee.setLoaneeLoanDetail(loanee.getLoaneeLoanDetail());

                Loanee savedLoanee = loaneeOutputPort.save(loanee);
                savedLoanee.setCohortName(loanee.getCohortName());
                savedLoanee.getLoaneeLoanDetail().setAmountApproved(loanee.getLoaneeLoanDetail().getAmountApproved());
                log.info("Loanee's amount approved in file upload: {}", savedLoanee.getLoaneeLoanDetail());
                log.info("Loanee's actual loan details in file upload: {}", loanee.getLoaneeLoanDetail());
                savedLoanees.add(savedLoanee);
            } catch (MeedlException e) {
                log.info("Error occurred while saving data .", e);
                throw new RuntimeException(e);
            }
        }
        log.info("Done saving loanee data from file to db. loanees size {}", savedLoanees.size());
        return savedLoanees;
    }
    private List<Map<String, String>> readFile(LoanBook loanBoook, List<String> requiredHeaders) throws MeedlException {
        List<Map<String, String>> data;
        if (loanBoook.getFile().getName().endsWith(".csv")) {
            try {
                data = validateAndReadCSV(loanBoook, requiredHeaders);
            }catch (IOException e){
                log.error("Error occurred reading csv",e);
                throw new MeedlException(e.getMessage());
            }
        } else if (loanBoook.getFile().getName().endsWith(".xlsx")) {
            try{
                data = validateAndReadExcel(loanBoook.getFile());
            }catch (IOException e){
                log.error("Error occurred reading excel",e);
                throw new MeedlException(e.getMessage());
            }
        } else {
            log.error("Unsupported file type.");
            throw new MeedlException("Unsupported file type.");
        }
        return data;
    }

    private List<Map<String, String>> validateAndReadCSV(LoanBook loanBook, List<String> requiredHeaders) throws IOException, MeedlException {
        List<Map<String, String>> records = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(loanBook.getFile()))) {
            String headerLine = br.readLine();

            Map<String, Integer> headerIndexMap = getAndVAlidateFileHeaderMap(requiredHeaders, headerLine);

            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;

                String[] values = line.split(",");
                Map<String, String> rowMap = new HashMap<>();

                for (String header : requiredHeaders) {
                    log.info("The header to get its value : {}", header);
                    if (!headerIndexMap.containsKey(header)) {
                        log.warn("Skipping missing header: {}", header);
                        continue;
                    }
                    int index = headerIndexMap.get(header);
                    if (index >= values.length) {
                        throw new MeedlException("Missing value for column: " + header);
                    }
                    rowMap.put(header, values[index].trim());
                }

                records.add(rowMap);
            }
        }

        if (records.isEmpty()) {
            throw new MeedlException("CSV file has no data rows.");
        }

        return records;
    }

    private static Map<String, Integer> getAndVAlidateFileHeaderMap(List<String> requiredHeaders, String headerLine) throws MeedlException {
        if (headerLine == null) {
            log.info("CSV file is empty or missing headers.");
            throw new MeedlException("CSV file is empty or missing headers.");
        }

        String[] headers = headerLine.split(",");
        Map<String, Integer> headerIndexMap = new HashMap<>();

        extractFileHeaderMap(headers, headerIndexMap);

        validateFileHeader(requiredHeaders, headerIndexMap);
        return headerIndexMap;
    }

    private static void extractFileHeaderMap(String[] headers, Map<String, Integer> headerIndexMap) {
        for (int i = 0; i < headers.length; i++) {
            String formattedFileHeader = formatFileHeader(headers[i].trim());
            headerIndexMap.put(formattedFileHeader, i);
        }
    }

    private static String formatFileHeader(String header) {
        if (header == null) {
            return null;
        }
        return header.replaceAll("\\s+", "").toLowerCase();
    }

    private static void validateFileHeader(List<String> requiredHeaders, Map<String, Integer> headerIndexMap) throws MeedlException {
        for (String required : requiredHeaders) {
            if (required.equals("bvn") || required.equals("nin") || required.equals("modeofpayment")){
                continue;
            }
            if (!headerIndexMap.containsKey(required)) {
                throw new MeedlException("Missing required column: " + required);
            }
        }
    }


    private List<Map<String, String>> validateAndReadExcel(File file) throws IOException, MeedlException {
        List<Map<String, String>> records = new ArrayList<>();
        List<String> requiredHeaders = List.of("firstName", "lastName", "email", "phoneNumber", "DON", "initialDeposit", "amountRequested", "amountReceived");

        try (FileInputStream fis = new FileInputStream(file);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = sheet.iterator();

            if (!rowIterator.hasNext()) {
                throw new MeedlException("Excel file is empty.");
            }

            // Read header row
            Row headerRow = rowIterator.next();
            Map<String, Integer> headerIndexMap = new HashMap<>();

            for (Cell cell : headerRow) {
                String header = cell.getStringCellValue().trim();
                headerIndexMap.put(header, cell.getColumnIndex());
            }

            validateFileHeader(requiredHeaders, headerIndexMap);

            // Read and map data rows
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                if (isRowEmpty(row)) continue;

                Map<String, String> rowMap = new HashMap<>();
                for (String header : requiredHeaders) {
                    int colIndex = headerIndexMap.get(header);
                    Cell cell = row.getCell(colIndex, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                    if (cell == null) {
                        throw new MeedlException("Missing value for column: " + header);
                    }
                    rowMap.put(header, getCellValueAsString(cell));
                }

                records.add(rowMap);
            }
        }

        if (records.isEmpty()) {
            throw new MeedlException("Excel file has no data rows.");
        }

        return records;
    }
    private boolean isRowEmpty(Row row) {
        for (Cell cell : row) {
            if (cell != null && cell.getCellType() != CellType.BLANK) {
                return false;
            }
        }
        return true;
    }

    private String getCellValueAsString(Cell cell) {
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue().trim();
            case NUMERIC -> String.valueOf(cell.getNumericCellValue()).replaceAll("\\.0$", "");
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            case FORMULA -> cell.getCellFormula();
            default -> "";
        };
    }
    private List<String> getUserDataUploadHeaders() {
        return List.of("firstname", "lastname",
                "email", "phonenumber",
                "dob", "initialdeposit",
                "amountrequested", "amountreceived",
                "bvn", "nin", "loanproduct");
    }
    private List<String> getRepaymentRecordUploadRequiredHeaders() {
        return List.of("firstname", "lastname",
                "email", "paymentdate",
                "amountpaid", "modeofpayment");
    }

}
