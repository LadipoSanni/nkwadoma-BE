package africa.nkwadoma.nkwadoma.domain.service.loanmanagement.loanbook;

import africa.nkwadoma.nkwadoma.application.ports.input.education.CohortUseCase;
import africa.nkwadoma.nkwadoma.application.ports.input.loanmanagement.*;
import africa.nkwadoma.nkwadoma.application.ports.input.loanmanagement.loanbook.AsynchronousLoanBookProcessingUseCase;
import africa.nkwadoma.nkwadoma.application.ports.input.loanmanagement.loanbook.RepaymentHistoryUseCase;
import africa.nkwadoma.nkwadoma.application.ports.output.education.CohortOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.education.LoaneeOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.IdentityManagerOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.UserIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.loanmanagement.LoanProductOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.loanmanagement.LoaneeLoanDetailsOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.notification.email.AsynchronousMailingOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.IdentityRole;
import africa.nkwadoma.nkwadoma.domain.enums.constants.CohortMessages;
import africa.nkwadoma.nkwadoma.domain.enums.loanenums.LoanDecision;
import africa.nkwadoma.nkwadoma.domain.enums.loanenums.LoanReferralStatus;
import africa.nkwadoma.nkwadoma.domain.enums.loanee.LoaneeStatus;
import africa.nkwadoma.nkwadoma.domain.enums.loanee.ModeOfPayment;
import africa.nkwadoma.nkwadoma.domain.enums.loanee.OnboardingMode;
import africa.nkwadoma.nkwadoma.domain.enums.loanee.UploadedStatus;
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
import africa.nkwadoma.nkwadoma.infrastructure.exceptions.LoanException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
@AllArgsConstructor
public class AsynchronousLoanBookProcessing implements AsynchronousLoanBookProcessingUseCase {
    private final UserIdentityOutputPort userIdentityOutputPort;
    private final LoaneeOutputPort loaneeOutputPort;
    private final LoaneeLoanDetailsOutputPort loaneeLoanDetailsOutputPort;
    private final IdentityManagerOutputPort identityManagerOutputPort;
    private final CohortUseCase cohortUseCase;
    private final LoanBookValidator loanBookValidator;
    private final AsynchronousMailingOutputPort asynchronousMailingOutputPort;
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
    @Override
    public void upLoadUserData(LoanBook loanBook) throws MeedlException {
        MeedlValidator.validateObjectInstance(loanBook, "Loan book cannot be empty.");
        loanBook.validateLoanBook();

        List<String> requiredHeaders = getUserDataUploadHeaders();

        List<Map<String, String>> data = readFile(loanBook, requiredHeaders);
        loanBookValidator.validateUserDataUploadFile(loanBook, data, requiredHeaders);
        log.info("Loan book read is {}", data);

        Cohort savedCohort = findCohort(loanBook.getCohort());
        List<Loanee> convertedLoanees = convertToLoanees(data, savedCohort, loanBook.getActorId());
//        validateStartDates(convertedLoanees, savedCohort);
        loanBook.setLoanees(convertedLoanees);
        referCohort(loanBook);
        updateLoaneeCount(savedCohort,convertedLoanees);
        completeLoanProcessing(loanBook);
    }

    private void validateAllFileLoanProductExist(List<Loanee> convertedLoanees) throws MeedlException {
        log.info("Validating the loan product name.");
        loanBookValidator.validateAllLoanProductExist(convertedLoanees);
    }

    private void validateStartDates(List<Loanee> convertedLoanees, Cohort savedCohort) throws MeedlException {
        for (Loanee loanee : convertedLoanees) {
            validateStartDate(loanee.getUpdatedAt(), savedCohort.getStartDate());
        }

    }

    private void validateStartDate(LocalDateTime loanStartDate, LocalDate cohortStartDate) throws MeedlException {
        LocalDate loanStartAsDate = loanStartDate.toLocalDate();

        if (loanStartAsDate.isBefore(cohortStartDate)) {
            log.info("Loan start date {} cannot be before cohort start date {}.", loanStartAsDate, cohortStartDate);
            throw new LoanException("Loan start date " +loanStartAsDate +" cannot be before cohort start date "+cohortStartDate );
        }
    }

    private void updateLoaneeCount(Cohort savedCohort, List<Loanee> loanees) throws MeedlException {
        savedCohort = findCohort(savedCohort);
        savedCohort.setNumberOfLoanees(savedCohort.getNumberOfLoanees() + loanees.size());
        cohortOutputPort.save(savedCohort);
        loaneeUseCase.increaseNumberOfLoaneesInOrganization(savedCohort, loanees.size());
        loaneeUseCase.increaseNumberOfLoaneesInProgram(savedCohort, loanees.size());
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

    private void completeLoanProcessing(LoanBook loanBook) {
        loanBook.getLoanees()
                .forEach(loanee -> {
                    try {
                        log.info("Loanee with cohort name but is loan product name {}", loanee.getCohortName());
                        log.info("Loan start date before processing start loan before accepting loan referral {}", loanee.getReferralDateTime());
                        LoanReferral loanReferral = acceptLoanReferral(loanee);
                        LoanRequest loanRequest = acceptLoanRequest(loanee, loanReferral, loanBook);
                        acceptLoanOffer(loanRequest);
                        startLoan(loanRequest,loanee.getUpdatedAt() );
                    } catch (MeedlException e) {
                        log.error("Error accepting loan referral.",e);
                    }
                });
    }

    private void startLoan(LoanRequest loanRequest, LocalDateTime loanStartDate) throws MeedlException {
        log.info("The loan start date is {} for user with email {}", loanStartDate, loanRequest.getLoanee().getUserIdentity().getEmail());
        Loan loan = Loan.builder().loaneeId(loanRequest.getLoanee().getId()).startDate(loanStartDate).loanOfferId(loanRequest.getId()).build();
        createLoanProductUseCase.startLoan(loan);
        log.info("Loan started for loanee {}", loanRequest.getLoanee().getUserIdentity().getEmail());
    }

    private void acceptLoanOffer(LoanRequest loanRequest) throws MeedlException {
        LoanOffer loanOffer = new LoanOffer();
        loanOffer.setId(loanRequest.getId());
        loanOffer.setLoaneeResponse(LoanDecision.ACCEPTED);
        loanOffer.setUserId(loanRequest.getLoanee().getUserIdentity().getId());
        loanOfferUseCase.acceptLoanOffer(loanOffer);
    }

    private LoanReferral acceptLoanReferral(Loanee loanee) throws MeedlException {
        LoanReferral loanReferral = LoanReferral.builder()
                .loanee(loanee)
                .build();
        loanReferral = viewLoanReferralsUseCase.viewLoanReferral(loanReferral);
        loanReferral.setLoanReferralStatus(LoanReferralStatus.ACCEPTED);
        respondToLoanReferralUseCase.respondToLoanReferral(loanReferral);
        return loanReferral;
    }
    private LoanRequest acceptLoanRequest(Loanee loanee, LoanReferral loanReferral, LoanBook loanBook) throws MeedlException {
        log.info("Loan Amount Approved is {}", loanee.getLoaneeLoanDetail().getAmountApproved());
        log.info("Amount received by this loanee {}", loanee.getLoaneeLoanDetail().getAmountReceived());
        LoanRequest loanRequest = LoanRequest.builder()
                .loanAmountApproved(loanee.getLoaneeLoanDetail().getAmountReceived())
                .loanAmountRequested(loanee.getLoaneeLoanDetail().getAmountRequested())
                .loanRequestDecision(LoanDecision.ACCEPTED)
                .id(loanReferral.getId())
                .loanProductId(findLoanProductIdByName(loanee.getCohortName()))
                .loanee(loanee)
                .actorId(loanBook.getActorId())
                .referredBy(loanee.getReferredBy())
                .build();
        log.info("Accepting loan request for uploaded loanee {}", loanRequest);
        return loanRequestUseCase.respondToLoanRequest(loanRequest);
    }

    private String findLoanProductIdByName(String loanProductName) {
        LoanProduct loanProduct = null;
        try {
            log.info("Loan product name being searched for in upload user data {}", loanProductName);
            loanProduct = loanProductOutputPort.findByName(loanProductName);
        } catch (MeedlException e) {
            log.error("Loan product does not exist by this name {}", loanProductName);
            throw new RuntimeException(e);
        }

        return loanProduct.getId();
    }


    private void referCohort(LoanBook loanBook) {
        Iterator<Loanee> iterator = loanBook.getLoanees().iterator();
        while (iterator.hasNext()) {
            Loanee loanee = iterator.next();
            log.info("About to refer loanee with details {}", loanee);
            log.info("About to refer loanee in cohort with loan details {}", loanee.getLoaneeLoanDetail());
            try {
                inviteTrainee(loanee);
            } catch (MeedlException e) {
                log.error("Failed to invite trainee with id: {}", loanee.getId(), e);
                iterator.remove();
            }
        }

        log.info("Number of referable loanees :{} ",  loanBook.getLoanees().size());
//        asynchronousMailingOutputPort.notifyLoanReferralActors(loanBook.getLoanees());
    }
    private void inviteTrainee (Loanee loanee) throws MeedlException {
        log.info("Single loanee is being referred...");
        loaneeUseCase.referLoanee(loanee);
    }


    private Cohort findCohort(Cohort cohort) throws MeedlException {
        MeedlValidator.validateObjectInstance(cohort, CohortMessages.COHORT_CANNOT_BE_EMPTY.getMessage());
        return cohortUseCase.viewCohortDetails(cohort.getCreatedBy(), cohort.getId());
    }
    private List<RepaymentHistory> convertToRepaymentHistory(List<Map<String, String>>  data) throws MeedlException {
        List<RepaymentHistory> repaymentHistories = new ArrayList<>();

        log.info("Started creating Repayment record from data gotten from file upload {}, size {}",data, data.size());
        loanBookValidator.validateDateTimeFormat(data, "paymentdate");
        for (Map<String, String> row  : data) {

            RepaymentHistory repaymentHistory = RepaymentHistory.builder()
                    .loanee(Loanee.builder().userIdentity(UserIdentity.builder().email(validateUseEmail(row.get("email").trim())).build()).build())
                    .amountPaid(validateMoney(row.get("amountpaid").trim(), "Amount repaid should be properly indicated"))
                    .paymentDateTime(parseFlexibleDateTime(row.get("paymentdate").trim()))
//                    .modeOfPayment(validateModeOfPayment(row.get("modeofpayment").trim()))
                    .modeOfPayment(ModeOfPayment.TRANSFER)
                    .build();
            log.info("Repayment history model created from file {}", repaymentHistory);
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


    private LocalDateTime parseFlexibleDateTime(String dateStr) throws MeedlException {
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
        throw new LoanException("Date doesn't match format. Date: "+dateStr + " Example format : 21/10/2019");
    }

    private ModeOfPayment validateModeOfPayment(String modeOfRepaymentToConvert) {
        if (MeedlValidator.isEmptyString(modeOfRepaymentToConvert)) {
            log.error("Mode of repayment as a string to be converted is empty {}", modeOfRepaymentToConvert);
            //Todo create notification of this error
        }
        ModeOfPayment modeOfPayment = null;
        try {
            modeOfPayment = ModeOfPayment.valueOf(modeOfRepaymentToConvert);
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

    List<Loanee> convertToLoanees(List<Map<String, String>> data, Cohort cohort, String actorId) throws MeedlException {
        List<Loanee> loanees = new ArrayList<>();

        for (Map<String, String> row : data) {
            UserIdentity userIdentity = UserIdentity.builder()
                    .firstName(row.get("firstname"))
                    .lastName(row.get("lastname"))
                    .middleName(row.get("middlename"))
                    .email(row.get("email"))
                    .phoneNumber(row.get("phonenumber"))
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
                    .uploadedStatus(UploadedStatus.ADDED)
                    .cohortId(cohort.getId())
                    .cohortName(row.get("loanproduct"))
                    .updatedAt(parseFlexibleDateTime(row.get("loanstartdate")))
                    .build();

            loanees.add(loanee);
        }
        validateAllFileLoanProductExist(loanees);

        return savedData(loanees);
    }

    private String encryptValue(String value) {
        try {
            MeedlValidator.validateBvn(value);
            return tokenUtils.encryptAES(value);
        } catch (MeedlException e) {
            log.error("Unable to encrypt value {}", value);
        }
        return StringUtils.EMPTY;
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
    private List<Map<String, String>> readFile(LoanBook loanBook, List<String> requiredHeaders) throws MeedlException {
        List<Map<String, String>> data;
        if (loanBook.getFile().getName().endsWith(".csv")) {
            log.info("the file type is .csv");
            try {
                data = validateAndReadCSV(loanBook, requiredHeaders);
            }catch (IOException e){
                log.error("Error occurred reading csv",e);
                throw new MeedlException(e.getMessage());
            }
        } else if (loanBook.getFile().getName().endsWith(".xlsx")) {
            try{
                log.info("the file is a .xlsx file");
                data = validateAndReadExcel(loanBook.getFile());
            }catch (IOException e){
                log.error("Error occurred reading excel",e);
                throw new LoanException(e.getMessage());
            }
        } else {
            log.error("Unsupported file type.");
            throw new LoanException("Unsupported file type.");
        }
        return data;
    }

    private List<Map<String, String>> validateAndReadCSV(LoanBook loanBook, List<String> requiredHeaders) throws IOException, MeedlException {
        List<Map<String, String>> records = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(loanBook.getFile()))) {
            String headerLine = br.readLine();

            Map<String, Integer> headerIndexMap = getAndValidateFileHeaderMap(requiredHeaders, headerLine);

            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;

                String[] values = line.split(",");
                log.info("The row line to get has {}", Arrays.toString(values));
                Map<String, String> rowMap = new HashMap<>();

                for (String header : requiredHeaders) {
                    log.info("The header to get its value : {}", header);
                    if (!headerIndexMap.containsKey(header)) {
                        log.warn("Skipping missing header: {}", header);
                        continue;
                    }
                    int index = headerIndexMap.get(header);
                    if (index >= values.length) {
                        log.error("Missing value for column: {}", header);
                        throw new LoanException("Missing value for column: " + header);
                    }
                    rowMap.put(header, values[index].trim());
                }
                log.info("The row map is :{}", rowMap);

                records.add(rowMap);
                log.info("The records with row map is : {}", records);
            }
        }

        if (records.isEmpty()) {
            throw new LoanException("CSV file has no data rows.");
        }

        return records;
    }

    private static Map<String, Integer> getAndValidateFileHeaderMap(List<String> requiredHeaders, String headerLine) throws MeedlException {
        if (headerLine == null) {
            log.info("CSV file is empty or missing headers.");
            throw new LoanException("CSV file is empty or missing headers.");
        }

        log.info("Header line first read {}", headerLine);
        String[] headers = headerLine.split(",");
        log.info("Headers splited into a list {}", Arrays.toString(headers));
        Map<String, Integer> headerIndexMap = new HashMap<>();

        extractFileHeaderMap(headers, headerIndexMap);

        validateFileHeader(requiredHeaders, headerIndexMap);
        return headerIndexMap;
    }

    private static void extractFileHeaderMap(String[] headers, Map<String, Integer> headerIndexMap) {
        for (int i = 0; i < headers.length; i++) {
            log.info("Header in loop each value : {}", headers[i]);
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
        log.info("Validation file headers with the required headers which are : {}", requiredHeaders);
        for (String required : requiredHeaders) {
            if (required.equals("bvn") || required.equals("nin")
                    || required.equals("modeofpayment") || required.equals("middlename")){
                continue;
            }
            if (!headerIndexMap.containsKey(required)) {
                throw new LoanException("Missing required column: " + required);
            }
        }
    }


    private List<Map<String, String>> validateAndReadExcel(File file) throws IOException, MeedlException {
//        List<Map<String, String>> records = new ArrayList<>();
        throw new MeedlException("Please convert file to csv ");
//        List<String> requiredHeaders = List.of("firstName", "lastName", "email", "phoneNumber", "DON", "initialDeposit", "amountRequested", "amountReceived");
//
//        try (FileInputStream fis = new FileInputStream(file);
//             Workbook workbook = new XSSFWorkbook(fis)) {
//
//            Sheet sheet = workbook.getSheetAt(0);
//            Iterator<Row> rowIterator = sheet.iterator();
//
//            if (!rowIterator.hasNext()) {
//                throw new MeedlException("Excel file is empty.");
//            }
//
//            // Read header row
//            Row headerRow = rowIterator.next();
//            Map<String, Integer> headerIndexMap = new HashMap<>();
//
//            for (Cell cell : headerRow) {
//                String header = cell.getStringCellValue().trim();
//                headerIndexMap.put(header, cell.getColumnIndex());
//            }

//            validateFileHeader(requiredHeaders, headerIndexMap);

            // Read and map data rows
//            while (rowIterator.hasNext()) {
//                Row row = rowIterator.next();
//                if (isRowEmpty(row)) continue;
//
//                Map<String, String> rowMap = new HashMap<>();
//                for (String header : requiredHeaders) {
//                    log.info("Header for excel to get the actual cell : {}", header);
//                    int colIndex = headerIndexMap.get(header);
//                    Cell cell = row.getCell(colIndex, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
//                    if (cell == null) {
//                        throw new MeedlException("Missing value for column: " + header);
//                    }
//                    rowMap.put(header, getCellValueAsString(cell));
//                }
//
//                records.add(rowMap);
//            }
//        }

//        if (records.isEmpty()) {
//            throw new MeedlException("Excel file has no data rows.");
//        }

//        return records;
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
        return List.of("firstname", "lastname", "middlename",
                "email", "phonenumber", "initialdeposit",
                "loanstartdate",
                "amountrequested", "amountreceived",
                "bvn", "nin", "loanproduct");
    }
    private List<String> getRepaymentRecordUploadRequiredHeaders() {
        return List.of(
                "email", "paymentdate",
                "amountpaid");
    }

}
