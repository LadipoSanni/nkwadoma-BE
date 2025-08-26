package africa.nkwadoma.nkwadoma.domain.service.investmentvehicle;

import africa.nkwadoma.nkwadoma.application.ports.input.investmentvehicle.*;
import africa.nkwadoma.nkwadoma.application.ports.output.financier.FinancierOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.OrganizationIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.UserIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.investmentvehicle.*;
import africa.nkwadoma.nkwadoma.application.ports.output.meedlportfolio.PortfolioOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.BankPartner;
import africa.nkwadoma.nkwadoma.domain.enums.Custodian;
import africa.nkwadoma.nkwadoma.domain.enums.FundManager;
import africa.nkwadoma.nkwadoma.domain.enums.Trustee;
import africa.nkwadoma.nkwadoma.domain.enums.constants.InvestmentVehicleMessages;
import africa.nkwadoma.nkwadoma.domain.enums.constants.investmentVehicle.FinancierMessages;

import africa.nkwadoma.nkwadoma.domain.enums.identity.IdentityRole;
import africa.nkwadoma.nkwadoma.domain.enums.investmentvehicle.FundRaisingStatus;
import africa.nkwadoma.nkwadoma.domain.enums.investmentvehicle.InvestmentVehicleStatus;
import africa.nkwadoma.nkwadoma.domain.enums.investmentvehicle.InvestmentVehicleVisibility;
import africa.nkwadoma.nkwadoma.domain.enums.investmentvehicle.*;

import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import africa.nkwadoma.nkwadoma.domain.model.financier.Financier;
import africa.nkwadoma.nkwadoma.domain.model.identity.OrganizationIdentity;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import africa.nkwadoma.nkwadoma.domain.model.investmentvehicle.*;
import africa.nkwadoma.nkwadoma.domain.model.meedlPortfolio.Portfolio;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.investmentvehicle.InvestmentVehicleMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.investmentvehicle.VehicleOperationMapper;
import jdk.management.jfr.EventTypeInfo;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;


import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import static africa.nkwadoma.nkwadoma.domain.enums.constants.InvestmentVehicleMessages.INVESTMENT_VEHICLE_NAME_EXIST;
import static africa.nkwadoma.nkwadoma.domain.enums.constants.InvestmentVehicleMessages.INVESTMENT_VEHICLE_VISIBILITY_CANNOT_BE_NULL;
import static africa.nkwadoma.nkwadoma.domain.enums.investmentvehicle.InvestmentVehicleStatus.DRAFT;
import static africa.nkwadoma.nkwadoma.domain.enums.investmentvehicle.InvestmentVehicleStatus.PUBLISHED;
import static africa.nkwadoma.nkwadoma.domain.enums.investmentvehicle.InvestmentVehicleType.COMMERCIAL;

@Slf4j
@RequiredArgsConstructor
@Service
public class InvestmentVehicleService implements InvestmentVehicleUseCase {

    private final InvestmentVehicleOutputPort investmentVehicleOutputPort;
    private final InvestmentVehicleMapper investmentVehicleMapper;
    private final PortfolioOutputPort portfolioOutputPort;
    private final FinancierOutputPort financierOutputPort;
    private final InvestmentVehicleFinancierOutputPort investmentVehicleFinancierOutputPort;
    private final UserIdentityOutputPort userIdentityOutputPort;
    private final VehicleOperationOutputPort vehicleOperationOutputPort;
    private final CouponDistributionOutputPort couponDistributionOutputPort;
    private final VehicleOperationMapper vehicleOperationMapper;
    private final VehicleClosureOutputPort vehicleClosureOutputPort;
    private final OrganizationIdentityOutputPort organizationIdentityOutputPort;


    @Override
    public InvestmentVehicle setUpInvestmentVehicle(InvestmentVehicle investmentVehicle) throws MeedlException, MeedlException {
        MeedlValidator.validateObjectInstance(investmentVehicle,"Investment Vehicle Object Cannot Be Null");
            investmentVehicle.validateDraft();
            investmentVehicle.setLastUpdatedDate(LocalDateTime.now());
            investmentVehicle.setTotalAvailableAmount(BigDecimal.ZERO);
            return saveInvestmentVehicleToDraft(investmentVehicle);
    }

    private InvestmentVehicle saveInvestmentVehicleToDraft(InvestmentVehicle investmentVehicle) throws MeedlException {
        if (ObjectUtils.isNotEmpty(investmentVehicle.getId())){
            return updateInvestmentVehicleInDraft(investmentVehicle);
        }
        investmentVehicle.setInvestmentVehicleStatus(DRAFT);
        investmentVehicle.setCreatedDate(LocalDateTime.now());
        return investmentVehicleOutputPort.save(investmentVehicle);
    }

    private InvestmentVehicle updateInvestmentVehicleInDraft(InvestmentVehicle investmentVehicle) throws MeedlException {
        InvestmentVehicle foundInvestmentVehicle =
                investmentVehicleOutputPort.findById(investmentVehicle.getId());
        investmentVehicleMapper.updateInvestmentVehicle(foundInvestmentVehicle,
                investmentVehicle);
        return investmentVehicleOutputPort.save(foundInvestmentVehicle);
    }

    private InvestmentVehicle prepareInvestmentVehicleForPublishing(InvestmentVehicle investmentVehicle) throws MeedlException {
        investmentVehicle.validate();
        checkIfInvestmentVehicleNameExist(investmentVehicle);
        setInvestmentVehicleNumbersOnMeedlPortfolio(investmentVehicle);
        investmentVehicle.setValues();
        return finalizeInvestmentVehiclePublishing(investmentVehicle.getId(),investmentVehicle);
    }

    private void setInvestmentVehicleNumbersOnMeedlPortfolio(InvestmentVehicle investmentVehicle) throws MeedlException {
        Portfolio portfolio = portfolioOutputPort.findPortfolio(Portfolio.builder().portfolioName("Meedl").build());
        if (investmentVehicle.getInvestmentVehicleType().equals(COMMERCIAL)){
            portfolio.setTotalNumberOfCommercialFundsInvestmentVehicle(
                    portfolio.getTotalNumberOfCommercialFundsInvestmentVehicle() + BigInteger.ONE.intValue()
            );
        }else {
            portfolio.setTotalNumberOfEndowmentFundsInvestmentVehicle(
                    portfolio.getTotalNumberOfEndowmentFundsInvestmentVehicle() + BigInteger.ONE.intValue()
            );
        }
        portfolio.setTotalNumberOfInvestmentVehicle(
                portfolio.getTotalNumberOfInvestmentVehicle() + BigInteger.ONE.intValue()
        );
        portfolioOutputPort.save(portfolio);
    }

    private void checkIfInvestmentVehicleNameExist(InvestmentVehicle investmentVehicle) throws MeedlException {
        InvestmentVehicle existingVehicle = investmentVehicleOutputPort.findByNameExcludingDraftStatus(investmentVehicle.getName(),DRAFT);
        if (!ObjectUtils.isEmpty(existingVehicle)) {
            throw new InvestmentException(INVESTMENT_VEHICLE_NAME_EXIST.getMessage());
        }
    }

    @Override
    public String deleteInvestmentVehicle(String investmentId) throws MeedlException {
        MeedlValidator.validateUUID(investmentId,InvestmentVehicleMessages.INVALID_INVESTMENT_VEHICLE_ID.getMessage());
        InvestmentVehicle investmentVehicle = investmentVehicleOutputPort.findById(investmentId);
        if (investmentVehicle.getInvestmentVehicleStatus().equals(PUBLISHED)){
            throw new InvestmentException(InvestmentVehicleMessages.PUBLISHED_INVESTMENT_VEHICLE_CANNOT_BE_DELETED.getMessage());
        }
        investmentVehicleOutputPort.deleteInvestmentVehicle(investmentId);
        return InvestmentVehicleMessages.DELETED.getMessage();
    }

    @Override
    public InvestmentVehicle viewInvestmentVehicleDetails(String investmentVehicleId, String userId) throws MeedlException {
        MeedlValidator.validateUUID(investmentVehicleId, InvestmentVehicleMessages.INVALID_INVESTMENT_VEHICLE_ID.getMessage());
        UserIdentity userIdentity = userIdentityOutputPort.findById(userId);
        if (userIdentity.getRole() == IdentityRole.PORTFOLIO_MANAGER) {
            log.info("Details being viewed by portfolio manger");
            return investmentVehicleOutputPort.findById(investmentVehicleId);
        }

        return getInvestmentVehicleFinancier(investmentVehicleId, userIdentity);
    }

    @Override
    public InvestmentVehicle viewInvestmentVehicleDetailsViaLink(String investmentVehicleLink) throws MeedlException {
        MeedlValidator.validateDataElement(investmentVehicleLink, InvestmentVehicleMessages.INVALID_INVESTMENT_VEHICLE_LINK.getMessage());
        InvestmentVehicle foundInvestmentVehicle = investmentVehicleOutputPort.findByInvestmentVehicleLink(investmentVehicleLink);
        if (foundInvestmentVehicle.getInvestmentVehicleVisibility() != InvestmentVehicleVisibility.PUBLIC){
            log.info("Investment vehicle is not public therefore can not be viewed view link {}", investmentVehicleLink);
            throw new ResourceNotFoundException("Investment vehicle not found.");
        }
        return foundInvestmentVehicle;
    }

    private InvestmentVehicle getInvestmentVehicleFinancier(String investmentVehicleId, UserIdentity userIdentity) throws MeedlException {
        Financier foundFinancier = null;
        if (IdentityRole.isCooperateFinancier(userIdentity.getRole())){
            Optional<OrganizationIdentity> optionalOrganizationIdentity = organizationIdentityOutputPort.findByUserId(userIdentity.getId());
            if (optionalOrganizationIdentity.isEmpty()){
                log.error("Unable to find the organization you belong to as a cooperate financier. user id {}", userIdentity.getId());
                throw new MeedlException("Unable to find the organization you belong to as a cooperate financier");
            }
            OrganizationIdentity organizationIdentity = optionalOrganizationIdentity.get();
            foundFinancier = financierOutputPort.findFinancierByOrganizationId(organizationIdentity.getId());
        }else if (IdentityRole.FINANCIER.equals(userIdentity.getRole())) {
            foundFinancier = financierOutputPort.findFinancierByUserId(userIdentity.getId());
        }
        MeedlValidator.validateObjectInstance(foundFinancier, "Financier viewing investment vehicle detail not found");
        MeedlValidator.validateUUID(foundFinancier.getId(), FinancierMessages.INVALID_FINANCIER_ID.getMessage());
        MeedlValidator.validateUUID(investmentVehicleId, InvestmentVehicleMessages.INVALID_INVESTMENT_VEHICLE_ID.getMessage());
        InvestmentVehicle foundInvestmentVehicle = investmentVehicleOutputPort.findById(investmentVehicleId);

        if (foundInvestmentVehicle.getInvestmentVehicleVisibility() == InvestmentVehicleVisibility.PUBLIC){
            return foundInvestmentVehicle;
        }
        if (foundInvestmentVehicle.getInvestmentVehicleVisibility() == InvestmentVehicleVisibility.PRIVATE){
            List<InvestmentVehicleFinancier> investmentVehicleFinancier = investmentVehicleFinancierOutputPort
                    .findByAll(investmentVehicleId, foundFinancier.getId());
            if (!investmentVehicleFinancier.isEmpty()){
                return foundInvestmentVehicle;
            }
        }
        throw new ResourceNotFoundException("Investment Vehicle not found");
    }


    @Override
    public Page<InvestmentVehicle> viewAllInvestmentVehicle(String userId,int pageSize, int pageNumber) throws MeedlException {
        UserIdentity userIdentity = userIdentityOutputPort.findById(userId);
        if (userIdentity.getRole() == IdentityRole.FINANCIER) {
            return investmentVehicleOutputPort.findAllInvestmentVehicleExcludingPrivate(userIdentity.getId(),pageSize,pageNumber);
        }
        return investmentVehicleOutputPort.findAllInvestmentVehicle(pageSize, pageNumber);
    }

    @Override
    public Page<InvestmentVehicle> searchInvestmentVehicle(String userId, InvestmentVehicle investmentVehicle,
                                                           int pageSize, int pageNumber) throws MeedlException {
        UserIdentity userIdentity = userIdentityOutputPort.findById(userId);
        if (userIdentity.getRole() == IdentityRole.FINANCIER) {
            return investmentVehicleOutputPort.searchInvestmentVehicleExcludingPrivate(userIdentity.getId(),
                    investmentVehicle,pageSize,pageNumber);
        }
        return investmentVehicleOutputPort.searchInvestmentVehicle(
                investmentVehicle.getName(),investmentVehicle,pageSize,pageNumber);
    }

    private InvestmentVehicle finalizeInvestmentVehiclePublishing(String investmentVehicleId, InvestmentVehicle investmentVehicle) throws MeedlException {
        if (ObjectUtils.isNotEmpty(investmentVehicleId)) {
            MeedlValidator.validateUUID(investmentVehicleId, InvestmentVehicleMessages.INVALID_INVESTMENT_VEHICLE_ID.getMessage());
             InvestmentVehicle foundInvestmentVehicle = investmentVehicleOutputPort.findById(investmentVehicleId);
            if (foundInvestmentVehicle.getInvestmentVehicleStatus().equals(InvestmentVehicleStatus.PUBLISHED)) {
                throw new InvestmentException(InvestmentVehicleMessages.INVESTMENT_VEHICLE_ALREADY_PUBLISHED.getMessage());
            }
            investmentVehicleMapper.updateInvestmentVehicle(foundInvestmentVehicle,investmentVehicle);
        }
        return investmentVehicleOutputPort.save(investmentVehicle);
    }

    @Override
    public Page<InvestmentVehicle> viewAllInvestmentVehicleBy(int pageSize, int pageNumber, InvestmentVehicle investmentVehicle, String sortField, String userId) throws MeedlException {
        MeedlValidator.validatePageSize(pageSize);
        MeedlValidator.validatePageNumber(pageNumber);
        Page<InvestmentVehicle> investmentVehicles = null;
        investmentVehicles = investmentVehicleOutputPort.findAllInvestmentVehicleBy(pageSize, pageNumber, investmentVehicle, sortField, userId);
        return investmentVehicles;
    }

    @Override
    public Page<InvestmentVehicle> viewAllInvestmentVehicleByFundRaisingStatus(int pageSize, int pageNumber, FundRaisingStatus fundRaisingStatus) throws MeedlException {
        MeedlValidator.validateObjectInstance(fundRaisingStatus, "FundRaisingStatus cannot be empty or null");
        return investmentVehicleOutputPort.findAllInvestmentVehicleByFundRaisingStatus(pageSize, pageNumber, fundRaisingStatus);
    }

    @Override
    public InvestmentVehicle setInvestmentVehicleVisibility(String investmentVehicleId, InvestmentVehicleVisibility investmentVehicleVisibility,
                                                            List<Financier> financiers) throws MeedlException {
        MeedlValidator.validateUUID(investmentVehicleId, InvestmentVehicleMessages.INVALID_INVESTMENT_VEHICLE_ID.getMessage());
        MeedlValidator.validateObjectInstance(investmentVehicleVisibility, INVESTMENT_VEHICLE_VISIBILITY_CANNOT_BE_NULL.getMessage());
        InvestmentVehicle investmentVehicle = investmentVehicleOutputPort.findById(investmentVehicleId);
        if(ObjectUtils.isNotEmpty(investmentVehicle.getInvestmentVehicleVisibility())) {
            return updateVisibility(investmentVehicleId, investmentVehicleVisibility, investmentVehicle,financiers);
        }
        investmentVehicle.setInvestmentVehicleVisibility(investmentVehicleVisibility);
        if (investmentVehicleVisibility.equals(InvestmentVehicleVisibility.PUBLIC)) {
            return prepareInvestmentVehicleForPublishing(investmentVehicle);
        } else if (investmentVehicleVisibility.equals(InvestmentVehicleVisibility.PRIVATE)) {
            if (financiers.isEmpty()) {
                throw new InvestmentException(InvestmentVehicleMessages.CANNOT_MAKE_INVESTMENT_VEHICLE_PRIVATE_WITH_EMPTY_FINANCIER.getMessage());
            }
            addFinancierToVehicle(financiers, investmentVehicle);
        }
        investmentVehicle = investmentVehicleOutputPort.save(investmentVehicle);
        return prepareInvestmentVehicleForPublishing(investmentVehicle);
    }

    private void addFinancierToVehicle(List<Financier> financiers, InvestmentVehicle investmentVehicle) throws MeedlException {
        for (Financier eachFinancier : financiers) {
            Financier financier = financierOutputPort.findFinancierByFinancierId(eachFinancier.getId());
            InvestmentVehicleFinancier investmentVehicleFinancier = InvestmentVehicleFinancier.builder()
                            .investmentVehicle(investmentVehicle).financier(financier).
                    investmentVehicleDesignation(eachFinancier.getInvestmentVehicleDesignation()).build();
            if (!investmentVehicleFinancierOutputPort.findByAll(investmentVehicle.getId(),financier.getId()).isEmpty()) {
                throw new InvestmentException(InvestmentVehicleMessages.FINANCIER_ALREADY_EXIST_IN_VEHICLE.getMessage());
            }
            investmentVehicleFinancierOutputPort.save(investmentVehicleFinancier);
        }
    }

    private InvestmentVehicle updateVisibility(String investmentVehicleId, InvestmentVehicleVisibility investmentVehicleVisibility,
                                               InvestmentVehicle investmentVehicle,List<Financier> financiers) throws MeedlException {
            if (investmentVehicleVisibility.equals(InvestmentVehicleVisibility.DEFAULT)) {
                setVisibilityToDefault(investmentVehicleId);
                investmentVehicle.setInvestmentVehicleVisibility(investmentVehicleVisibility);
            }else if (investmentVehicleVisibility.equals(InvestmentVehicleVisibility.PRIVATE)) {
                if (!investmentVehicleFinancierOutputPort
                        .checkIfFinancierExistInVehicle(investmentVehicle.getId()) && financiers.isEmpty()){
                    throw new InvestmentException(InvestmentVehicleMessages.CANNOT_MAKE_INVESTMENT_VEHICLE_PRIVATE_WITH_EMPTY_FINANCIER.getMessage());
                };
                addFinancierToVehicle(financiers, investmentVehicle);
                investmentVehicle.setInvestmentVehicleVisibility(investmentVehicleVisibility);
            }else {
                investmentVehicle.setInvestmentVehicleVisibility(investmentVehicleVisibility);
            }
        return investmentVehicleOutputPort.save(investmentVehicle);
    }


    private void setVisibilityToDefault(String investmentVehicleId) throws MeedlException {
        boolean invested = investmentVehicleFinancierOutputPort.checkIfAnyFinancierHaveInvestedInVehicle(investmentVehicleId);
        if (invested) {
            throw new MeedlException(InvestmentVehicleMessages.
                    CANNOT_CHANGE_INVESTMENT_VEHICLE_TO_DEFAULT.getMessage());
        }
        investmentVehicleFinancierOutputPort.removeFinancierAssociationWithInvestmentVehicle(investmentVehicleId);
    }

    @Override
    public InvestmentVehicle setInvestmentVehicleOperationStatus(InvestmentVehicle investmentVehicle) throws MeedlException {
        MeedlValidator.validateObjectInstance(investmentVehicle,"Investment vehicle object cannot be empty ");
        MeedlValidator.validateObjectInstance(investmentVehicle.getVehicleOperation(),"Vehicle Operation cannot be empty");
        investmentVehicle.validateVehicleStatuses();
        InvestmentVehicle foundInvestmentVehicle = investmentVehicleOutputPort.findById(investmentVehicle.getId());
        if (foundInvestmentVehicle.getVehicleOperation() == null) {
            setNewInvestmentVehicleOperationStatus(investmentVehicle, foundInvestmentVehicle);
            String investmentVehicleLink = generateInvestmentVehicleLink(foundInvestmentVehicle.getName());
            foundInvestmentVehicle.setInvestmentVehicleLink(investmentVehicleLink);
            return investmentVehicleOutputPort.save(foundInvestmentVehicle);
        }
        updateExistingInvestmentVehicleOperationStatus(investmentVehicle, foundInvestmentVehicle);
        return foundInvestmentVehicle;
    }

    @Override
    public Page<InvestmentVehicle> viewAllInvestmentVehicleInvestedIn(String userId, String financierId, InvestmentVehicleType investmentVehicleType, int pageSize, int pageNumber) throws MeedlException {
        UserIdentity userIdentity = userIdentityOutputPort.findById(userId);
        if (userIdentity.getRole().equals(IdentityRole.PORTFOLIO_MANAGER)){
            MeedlValidator.validateUUID(financierId,"Financier id cannot be empty");
            return investmentVehicleOutputPort.findAllInvestmentVehicleFinancierWasAddedToByFinancierId(financierId,pageSize,pageNumber);
        }
        return investmentVehicleOutputPort.findAllInvestmentVehicleFinancierWasAddedTo(userIdentity.getId(), investmentVehicleType,pageSize,pageNumber);
    }

    @Override
    public Page<InvestmentVehicle> searchMyInvestment(String userId, InvestmentVehicle investmentVehicle, int pageSize, int pageNumber) throws MeedlException {
        UserIdentity userIdentity = userIdentityOutputPort.findById(userId);
        return investmentVehicleOutputPort.searchInvestmentVehicleFinancierWasAddedTo(userIdentity.getId(),investmentVehicle,pageSize,pageNumber);
    }

    private void setNewInvestmentVehicleOperationStatus(InvestmentVehicle investmentVehicle, InvestmentVehicle foundInvestmentVehicle) throws MeedlException {
        CouponDistribution couponDistribution =
                couponDistributionOutputPort.save(CouponDistribution.builder().build());
        investmentVehicle.getVehicleOperation().setCouponDistribution(couponDistribution);
        investmentVehicle.getVehicleOperation().setCouponDistributionStatus(CouponDistributionStatus.DEFAULT);
        investmentVehicle.getVehicleOperation().setOperationStatus(OperationStatus.ACTIVE);
        foundInvestmentVehicle.setVehicleOperation(
                vehicleOperationOutputPort.save(investmentVehicle.getVehicleOperation())
        );
        investmentVehicleOutputPort.save(foundInvestmentVehicle);
    }

    private void updateExistingInvestmentVehicleOperationStatus(InvestmentVehicle investmentVehicle, InvestmentVehicle foundInvestmentVehicle) throws MeedlException {
        foundInvestmentVehicle.getVehicleOperation().setDeployingStatus(investmentVehicle.getVehicleOperation().getDeployingStatus());
        foundInvestmentVehicle.getVehicleOperation().setFundRaisingStatus(investmentVehicle.getVehicleOperation().getFundRaisingStatus());
        foundInvestmentVehicle.getVehicleOperation().setCouponDistributionStatus(
                    investmentVehicle.getVehicleOperation().getCouponDistributionStatus());
        if (ObjectUtils.isNotEmpty(foundInvestmentVehicle.getVehicleClosureStatus())){
            investmentVehicle.getVehicleClosureStatus().setId(foundInvestmentVehicle.getVehicleClosureStatus().getId());
            foundInvestmentVehicle.setVehicleClosureStatus(
                    investmentVehicle.getVehicleClosureStatus()
            );
            vehicleClosureOutputPort.save(foundInvestmentVehicle.getVehicleClosureStatus());
        }else {
            foundInvestmentVehicle.setVehicleClosureStatus(
                    vehicleClosureOutputPort.save(investmentVehicle.getVehicleClosureStatus())
            );
        }
        vehicleOperationOutputPort.save(foundInvestmentVehicle.getVehicleOperation());
        investmentVehicleOutputPort.save(foundInvestmentVehicle);
    }

    private String generateInvestmentVehicleLink(String name) throws MeedlException {
        Random random = new Random();
        String baseSlug = name.trim().toLowerCase()
                .replaceAll("[\\s_]+", "-")
//                .replaceAll("-{2,}", "-")
                ;

        String uniqueSlug = baseSlug;
        int counter = 1;
        while (investmentVehicleOutputPort.existByInvestmentVehicleLink(uniqueSlug)) {
            log.info("generating link for investment vehicle with name {}, search for name link {} time(s).",name, counter);
            int randomNum = 1000 + random.nextInt(counter * 90);
            uniqueSlug = baseSlug + "-" + randomNum;
            counter++;
        }
        log.info("Link generated {} for investment vehicle with name {}.",uniqueSlug, name);
        return uniqueSlug;
    }

    @Override
    public FundStakeHolder viewFundStakeHolders() {
        FundStakeHolder fundStakeHolder = new FundStakeHolder();

        fundStakeHolder.setBankPartners(Arrays.asList(BankPartner.values()));
        fundStakeHolder.setFundManagers(Arrays.asList(FundManager.values()));
        fundStakeHolder.setTrustees(Arrays.asList(Trustee.values()));
        fundStakeHolder.setCustodians(Arrays.asList(Custodian.values()));
        return fundStakeHolder;
    }

}
