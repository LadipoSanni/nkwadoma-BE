package africa.nkwadoma.nkwadoma.domain.service.loanmanagement.loanProduct;

import africa.nkwadoma.nkwadoma.application.ports.input.loanmanagement.loanProduct.CreateLoanProductUseCase;
import africa.nkwadoma.nkwadoma.application.ports.input.loanmanagement.loanProduct.ViewLoanProductUseCase;
import africa.nkwadoma.nkwadoma.application.ports.output.financier.FinancierOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.*;
import africa.nkwadoma.nkwadoma.application.ports.output.investmentvehicle.InvestmentVehicleFinancierOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.investmentvehicle.InvestmentVehicleOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.loanmanagement.loanProduct.LoanProductOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.loanmanagement.loanProduct.LoanProductVendorOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.loanmanagement.loanProduct.VendorOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.meedlportfolio.PortfolioOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.constants.MeedlConstants;
import africa.nkwadoma.nkwadoma.domain.enums.constants.loan.LoanMessages;
import africa.nkwadoma.nkwadoma.domain.enums.constants.loan.LoanProductMessage;
import africa.nkwadoma.nkwadoma.domain.enums.loanenums.LoanDecision;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.exceptions.loan.LoanException;
import africa.nkwadoma.nkwadoma.domain.model.financier.Financier;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import africa.nkwadoma.nkwadoma.domain.model.investmentvehicle.InvestmentVehicle;
import africa.nkwadoma.nkwadoma.domain.model.loan.LoanProduct;
import africa.nkwadoma.nkwadoma.domain.model.loan.LoanProductVendor;
import africa.nkwadoma.nkwadoma.domain.model.loan.Vendor;
import africa.nkwadoma.nkwadoma.domain.model.meedlPortfolio.Portfolio;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.loanManagement.loanProduct.LoanProductMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.data.domain.Page;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
@Slf4j
@EnableAsync
@Service
public class LoanProductService implements CreateLoanProductUseCase, ViewLoanProductUseCase {
    private final LoanProductOutputPort loanProductOutputPort;
    private final LoanProductMapper loanProductMapper;
    private final IdentityManagerOutputPort identityManagerOutPutPort;
    private final UserIdentityOutputPort userIdentityOutputPort;
    private final InvestmentVehicleOutputPort investmentVehicleOutputPort;
    private final InvestmentVehicleFinancierOutputPort investmentVehicleFinancierOutputPort;
    private final PortfolioOutputPort portfolioOutputPort;
    private final FinancierOutputPort financierOutputPort;
    private final VendorOutputPort vendorOutputPort;
    private final LoanProductVendorOutputPort loanProductVendorOutputPort;

    @Override
    public LoanProduct createLoanProduct(LoanProduct loanProduct) throws MeedlException {
        MeedlValidator.validateObjectInstance(loanProduct, LoanProductMessage.INVALID_LOAN_PRODUCT_REQUEST_DETAILS.getMessage());
        loanProduct.validateLoanProductDetails();
        validateSponsors(loanProduct);
        loanProduct.validateProviderServices();
        UserIdentity foundUser = userIdentityOutputPort.findById(loanProduct.getCreatedBy());
        identityManagerOutPutPort.verifyUserExistsAndIsEnabled(foundUser);
        log.info("The user with {} email has been verified ", foundUser.getEmail());
        if (loanProductOutputPort.existsByNameIgnoreCase(loanProduct.getName())){
            log.error("Loan product {} already exists", loanProduct.getName() );
            throw new LoanException("Loan product " + loanProduct.getName() + " already exists");
        }
        log.info("Searching for investment vehicle with id {} ", loanProduct.getInvestmentVehicleId());
        InvestmentVehicle investmentVehicle =
                investmentVehicleOutputPort.findById(loanProduct.getInvestmentVehicleId());
        setUpLoanProductInvestmentVehicleDetails(loanProduct, investmentVehicle);
        log.info("Saving vendors for this loan product");
        List<Vendor> vendors = vendorOutputPort.saveVendors(loanProduct.getVendors());
        log.info("About to save loan product to db on create... {}", loanProduct);
        LoanProduct savedLoanProduct = loanProductOutputPort.save(loanProduct);
        loanProduct.setId(savedLoanProduct.getId());
        loanProduct.setVendors(vendors);
        log.info("Saving loan product vendors");
        loanProductVendorOutputPort.save(vendors, savedLoanProduct);
        log.info("Loan product to be saved in create loan product service method {}", loanProduct);
        investmentVehicleOutputPort.save(investmentVehicle);
        updateNumberOfLoanProductOnMeedlPortfolio();
        return loanProduct;
    }

    private void setUpLoanProductInvestmentVehicleDetails(LoanProduct loanProduct, InvestmentVehicle investmentVehicle) throws MeedlException {
        checkProductSizeNotMoreThanAvailableInvestmentAmount(loanProduct, investmentVehicle);
        verifyFinanciersExistInVehicle(loanProduct, investmentVehicle);
        investmentVehicle.setTotalAvailableAmount(investmentVehicle.getTotalAvailableAmount().subtract(loanProduct.getLoanProductSize()));
        loanProduct.addInvestmentVehicleValues(investmentVehicle);
        initializeAvailableAmounts(loanProduct);
        if (ObjectUtils.isEmpty(loanProduct.getTotalOutstandingLoan())) {
            loanProduct.setTotalOutstandingLoan(BigDecimal.ZERO);
        }
    }

    private void updateNumberOfLoanProductOnMeedlPortfolio() throws MeedlException {
        Portfolio portfolio = Portfolio.builder().portfolioName(MeedlConstants.MEEDL).build();
        portfolio = portfolioOutputPort.findPortfolio(portfolio);
        portfolio.setNumberOfLoanProducts(portfolio.getNumberOfLoanProducts() + 1);
        portfolioOutputPort.save(portfolio);
    }

    private void validateSponsors(LoanProduct loanProduct) throws MeedlException {
        if (MeedlValidator.isEmptyCollection(loanProduct.getSponsors())){
            log.error("Sponsors is empty when creating loan product {}", loanProduct.getSponsors());
            throw new MeedlException("Sponsors for this loan product is required");
        }
    }
    private void initializeAvailableAmounts(LoanProduct loanProduct) {
        loanProduct.setTotalAmountAvailable(loanProduct.getLoanProductSize());
        loanProduct.setAvailableAmountToBeOffered(loanProduct.getLoanProductSize());
        loanProduct.setAvailableAmountToBeDisbursed(loanProduct.getTotalAmountAvailable());
    }
    private void verifyFinanciersExistInVehicle(LoanProduct loanProduct, InvestmentVehicle investmentVehicle) throws MeedlException {
        List<String> sponsorsIds = new ArrayList<>();
        for (Financier financier : loanProduct.getSponsors()){
            int count = investmentVehicleFinancierOutputPort.checkIfFinancierExistInVehicle(financier.getId(), investmentVehicle.getId());
            if (count == 0){
                log.error("Investment vehicle financier not found for financier with id {} and vehicle with id {}", financier.getId(), investmentVehicle.getId());
                throw new MeedlException("Apparently financier with name %s is not part of %s".formatted( financier.getName(),  investmentVehicle.getName()));
            }
            sponsorsIds.add(financier.getId());
        }
        loanProduct.setSponsorIds(sponsorsIds);
        log.info("Done verifying if financiers are part of the select vehicle {}", investmentVehicle.getId());
    }

    private void checkProductSizeNotMoreThanAvailableInvestmentAmount(LoanProduct loanProduct, InvestmentVehicle investmentVehicle) throws MeedlException {

        log.info("Loan product size is : {}", loanProduct.getLoanProductSize());
        log.info("Investment vehicle available balance is : {}", investmentVehicle.getTotalAvailableAmount());
        if (loanProduct.getLoanProductSize().compareTo(investmentVehicle.getTotalAvailableAmount()) > BigInteger.ZERO.intValue()) {
            log.warn("Attempt to create loan product that exceeds the investment vehicle available amount. Loan product size {}, vehicle available amount {}", loanProduct.getLoanProductSize(), investmentVehicle.getTotalAvailableAmount());
            throw new MeedlException("Loan product size cannot be greater than investment vehicle available amount.");
        }
        log.info("Validation of loan product size in relation to available amount on investment vehicle.");
    }

    @Override
    public Page<LoanProduct> viewAllLoanProduct(LoanProduct loanProduct) {
        return loanProductOutputPort.findAllLoanProduct(loanProduct);
    }
    @Override
    public Page<LoanProduct> search(String loanProductName, int pageSize, int pageNumber) throws MeedlException {
        MeedlValidator.validateDataElement(loanProductName, LoanMessages.LOAN_PRODUCT_NAME_REQUIRED.getMessage());
        return loanProductOutputPort.search(loanProductName,pageSize,pageNumber);
    }
    @Override
    public LoanProduct viewLoanProductDetailsById(String loanProductId) throws MeedlException {
        MeedlValidator.validateUUID(loanProductId, LoanProductMessage.INVALID_LOAN_PRODUCT_ID.getMessage());
        log.info("Service level of view loan product {}", loanProductId);
        LoanProduct loanProduct = loanProductOutputPort.findById(loanProductId);
        log.info("Updating loan product vendors on the view ");
        List<Vendor> vendors = loanProductVendorOutputPort.getVendorsByLoanProductId(loanProductId);
        loanProduct.setVendors(vendors);
        getLoanProductSponsors(loanProduct);
        return loanProduct;
    }

    private void getLoanProductSponsors(LoanProduct loanProduct) throws MeedlException {
        if (ObjectUtils.isEmpty(loanProduct.getSponsorIds())){
            log.warn("Loan product has no sponsors");
            return;
        }
        log.info("Updating sponsors list in view loan product");
        List<Financier> sponsors = new ArrayList<>();
        for (String financierId : loanProduct.getSponsorIds()){
            sponsors.add(financierOutputPort.findById(financierId));
        }
        loanProduct.setSponsors(sponsors);
    }

    @Override
    public void deleteLoanProductById(LoanProduct loanProduct) throws MeedlException {
        MeedlValidator.validateObjectInstance(loanProduct, LoanMessages.LOAN_CANNOT_BE_EMPTY.getMessage());
        MeedlValidator.validateUUID(loanProduct.getId(), LoanProductMessage.INVALID_LOAN_PRODUCT_ID.getMessage());
        int offerCount = loanProductOutputPort.countLoanOfferFromLoanProduct(loanProduct.getId(), List.of(LoanDecision.OFFERED, LoanDecision.ACCEPTED));
        if (offerCount == 0) {
            LoanProduct foundLoanProduct = loanProductOutputPort.findById(loanProduct.getId());
            log.info("Updating the total available amount on investment vehicle with the size of the loan product");
            InvestmentVehicle investmentVehicle = investmentVehicleOutputPort.findById(foundLoanProduct.getInvestmentVehicleId());
            investmentVehicle.setTotalAvailableAmount(
                    investmentVehicle.getTotalAvailableAmount().add(foundLoanProduct.getLoanProductSize()));
            investmentVehicleOutputPort.save(investmentVehicle);
            loanProductOutputPort.deleteById(loanProduct.getId());
            log.info("Successfully deleted loan product with id {}", loanProduct.getId());
        }else {
            log.error("This loan product cannot be deleted because it has been used in a loan offer. {}", loanProduct.getId());
            throw new MeedlException("This loan product cannot be deleted because it has been used in a loan offer");
        }

    }

    @Override
    public LoanProduct updateLoanProduct(LoanProduct loanProduct) throws MeedlException {
        MeedlValidator.validateObjectInstance(loanProduct, LoanProductMessage.LOAN_PRODUCT_REQUIRED.getMessage());
        MeedlValidator.validateUUID(loanProduct.getId(), LoanProductMessage.INVALID_LOAN_PRODUCT_ID.getMessage());
        log.info("In update loan product details, finding loan product by id === {}", loanProduct.getId());
        LoanProduct foundLoanProduct = loanProductOutputPort.findById(loanProduct.getId());
        if (foundLoanProduct.getTotalNumberOfLoanee() > BigInteger.ZERO.intValue()) {
            log.error("Loan product {} cannot be updated as it has already been loaned out", foundLoanProduct.getName());
            throw new LoanException("Loan product " + foundLoanProduct.getName() + " cannot be updated as it has already been loaned out");
        }

        int offerCount = loanProductOutputPort.countLoanOfferFromLoanProduct(loanProduct.getId(), List.of(LoanDecision.OFFERED, LoanDecision.ACCEPTED));
        if (offerCount == 0) {
            return updateLoanProduct(loanProduct, foundLoanProduct);
        }else {
            log.error("This loan product cannot be updated because it has been used in a loan offer. {}", loanProduct.getId());
            throw new MeedlException("This loan product cannot be updated because it has been used in a loan offer");
        }
    }

    private LoanProduct updateLoanProduct(LoanProduct loanProduct, LoanProduct foundLoanProduct) throws MeedlException {
        updateLoanProductInvestmentVehicleDetails(loanProduct, foundLoanProduct);
        foundLoanProduct = loanProductMapper.updateLoanProduct(foundLoanProduct, loanProduct);
        foundLoanProduct.setUpdatedAt(LocalDateTime.now());
        log.info("Loan product sponsors id to be updated -----> {}", loanProduct.getSponsorIds());

        foundLoanProduct.setSponsors(loanProduct.getSponsors());

        log.info("About to save the updated loan product ...");
        updateVendorDetails(loanProduct);
        return loanProductOutputPort.save(foundLoanProduct);
    }

    private void updateLoanProductInvestmentVehicleDetails(LoanProduct loanProduct, LoanProduct foundLoanProduct) throws MeedlException {
        log.info("previous investment vehicle id ---> {} current investment vehicle id {}, are they the same {} ", foundLoanProduct.getInvestmentVehicleId(), loanProduct.getInvestmentVehicleId(), loanProduct.getInvestmentVehicleId().equals(foundLoanProduct.getInvestmentVehicleId()));
        if (!loanProduct.getInvestmentVehicleId().equals(foundLoanProduct.getInvestmentVehicleId())){
            log.info("Update loan product based on new investment vehicle in use ");
            setUpLoanProductSizeWithDifferentInvestmentVehicle(loanProduct);
            refundPreviousVehicleAvailableAmount(foundLoanProduct);
        }else {
            boolean isNotEqual = foundLoanProduct.getLoanProductSize()
                    .compareTo(loanProduct.getLoanProductSize()) != 0;
            log.info("is new loan product size greater than the previous ? {} , previous {} , new {}",
                    isNotEqual, foundLoanProduct.getLoanProductSize(), loanProduct.getLoanProductSize());
            if (isNotEqual) {
                log.info("The vehicle is the same but the amount selected is different");
                InvestmentVehicle investmentVehicle = investmentVehicleOutputPort.findById(foundLoanProduct.getInvestmentVehicleId());
                verifyFinanciersExistInVehicle(loanProduct, investmentVehicle);
                validateAndUpdateInvestmentVehicleAmountForLoanProduct(foundLoanProduct, loanProduct, investmentVehicle);
                log.info("setting other loan product values that depends on the size...");
                initializeAvailableAmounts(loanProduct);
                investmentVehicleOutputPort.save(investmentVehicle);

            }
        }
    }

    private void setUpLoanProductSizeWithDifferentInvestmentVehicle(LoanProduct loanProduct) throws MeedlException {
        InvestmentVehicle investmentVehicle = investmentVehicleOutputPort.findById(loanProduct.getInvestmentVehicleId());
        setUpLoanProductInvestmentVehicleDetails(loanProduct, investmentVehicle);
        investmentVehicleOutputPort.save(investmentVehicle);
    }

    private void refundPreviousVehicleAvailableAmount(LoanProduct foundLoanProduct) throws MeedlException {
        InvestmentVehicle investmentVehicle = investmentVehicleOutputPort.findById(foundLoanProduct.getInvestmentVehicleId());
        investmentVehicle.setTotalAvailableAmount(
                investmentVehicle.getTotalAvailableAmount()
                        .add(foundLoanProduct.getLoanProductSize())
        );
        investmentVehicleOutputPort.save(investmentVehicle);
    }

    private void updateVendorDetails(LoanProduct loanProduct) throws MeedlException {
        log.info("Finding all loan product vendors to update by loan product id {}", loanProduct.getId());
        List<Vendor> vendors = null;
//        loanProductVendorOutputPort.getVendorsByLoanProductId(loanProduct.getId());
        List<LoanProductVendor> loanProductVendors = loanProductVendorOutputPort.findAllByLoanProductId(loanProduct.getId());

        List<String> loanProductVendorIds = getLoanProductVendorIds(loanProductVendors);
//        List<String> vendorIds = getVendorIds(vendors);

        log.info("About to delete existing loan product vendors in update flow");
        loanProductVendorOutputPort.deleteMultipleById(loanProductVendorIds);
//        vendorOutputPort.deleteMultipleById(vendorIds);

        log.info("Saving vendor and loan product vendor details in update loan product");
        vendors = vendorOutputPort.saveVendors(loanProduct.getVendors());
        loanProduct.setVendors(vendors);
        loanProductVendorOutputPort.save(loanProduct.getVendors(),loanProduct);
    }

    private List<String> getLoanProductVendorIds(List<LoanProductVendor> loanProductVendors) {
        return loanProductVendors.stream()
                .filter(Objects::nonNull)
                .map(LoanProductVendor::getId)
                .filter(MeedlValidator::isNotEmptyString)
                .toList();
    }


    private static List<String> getVendorIds(List<Vendor> vendors) {
        return vendors.stream()
                .filter(Objects::nonNull)
                .map(Vendor::getId)
                .filter(MeedlValidator::isNotEmptyString)
                .toList();

    }

    private void validateAndUpdateInvestmentVehicleAmountForLoanProduct(LoanProduct foundLoanProduct, LoanProduct loanProduct, InvestmentVehicle investmentVehicle) throws MeedlException {
        log.info("Updating loan product size with respect to investment vehicle");
        BigDecimal investmentVehiclePreviousAmountAvailable = investmentVehicle.getTotalAvailableAmount()
                .add(foundLoanProduct.getLoanProductSize());
        if (investmentVehiclePreviousAmountAvailable
                .compareTo(loanProduct.getLoanProductSize()) < 0) {
            log.error("Loan product size update failed. Requested size [{}] exceeds available amount [{}] in investment vehicle (after refunding previous allocation).",
                    loanProduct.getLoanProductSize(), investmentVehiclePreviousAmountAvailable);

            throw new MeedlException(
                    String.format("The new loan product size (%s) is greater than the amount currently available (%s) in the investment vehicle, even after refunding the previously allocated amount.",
                            loanProduct.getLoanProductSize(), investmentVehiclePreviousAmountAvailable)
            );
        }
        log.info("Updated total available amount in investment vehicle to {}", investmentVehicle.getTotalAvailableAmount());
        investmentVehicle.setTotalAvailableAmount(
                investmentVehiclePreviousAmountAvailable.subtract(loanProduct.getLoanProductSize())
        );
    }

}
