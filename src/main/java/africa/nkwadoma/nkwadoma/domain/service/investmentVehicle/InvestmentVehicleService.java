package africa.nkwadoma.nkwadoma.domain.service.investmentVehicle;

import africa.nkwadoma.nkwadoma.application.ports.input.investmentVehicle.*;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.UserIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.investmentVehicle.*;
import africa.nkwadoma.nkwadoma.application.ports.output.meedlPortfolio.PortfolioOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.IdentityRole;
import africa.nkwadoma.nkwadoma.domain.enums.constants.InvestmentVehicleMessages;
import africa.nkwadoma.nkwadoma.domain.enums.constants.investmentVehicle.FinancierMessages;
import africa.nkwadoma.nkwadoma.domain.enums.investmentVehicle.FundRaisingStatus;
import africa.nkwadoma.nkwadoma.domain.enums.investmentVehicle.InvestmentVehicleStatus;
import africa.nkwadoma.nkwadoma.domain.enums.investmentVehicle.InvestmentVehicleType;
import africa.nkwadoma.nkwadoma.domain.enums.investmentVehicle.InvestmentVehicleVisibility;
import africa.nkwadoma.nkwadoma.domain.enums.investmentVehicle.*;
import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import africa.nkwadoma.nkwadoma.domain.model.investmentVehicle.*;
import africa.nkwadoma.nkwadoma.domain.model.meedlPortfolio.Portfolio;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.investmentVehicle.ViewInvestmentVehicleRequest;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.investmentVehicle.InvestmentVehicleMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.investmentVehicle.VehicleOperationMapper;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;


import java.time.LocalDate;
import java.math.BigInteger;
import java.util.*;
import java.util.List;

import static africa.nkwadoma.nkwadoma.domain.enums.constants.InvestmentVehicleMessages.INVESTMENT_VEHICLE_NAME_EXIST;
import static africa.nkwadoma.nkwadoma.domain.enums.constants.InvestmentVehicleMessages.INVESTMENT_VEHICLE_VISIBILITY_CANNOT_BE_NULL;
import static africa.nkwadoma.nkwadoma.domain.enums.investmentVehicle.InvestmentVehicleStatus.DRAFT;
import static africa.nkwadoma.nkwadoma.domain.enums.constants.investmentVehicle.InvestmentVehicleConstants.INVESTMENT_VEHICLE_URL;
import static africa.nkwadoma.nkwadoma.domain.enums.investmentVehicle.InvestmentVehicleStatus.PUBLISHED;
import static africa.nkwadoma.nkwadoma.domain.enums.investmentVehicle.InvestmentVehicleType.COMMERCIAL;

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



    @Override
    public InvestmentVehicle setUpInvestmentVehicle(InvestmentVehicle investmentVehicle) throws MeedlException {
        MeedlValidator.validateObjectInstance(investmentVehicle,"Investment Vehicle Object Cannot Be Null");
            investmentVehicle.validateDraft();
            investmentVehicle.setLastUpdatedDate(LocalDate.now());
            return saveInvestmentVehicleToDraft(investmentVehicle);
    }

    private InvestmentVehicle saveInvestmentVehicleToDraft(InvestmentVehicle investmentVehicle) throws MeedlException {
        if (ObjectUtils.isNotEmpty(investmentVehicle.getId())){
            return updateInvestmentVehicleInDraft(investmentVehicle);
        }
        investmentVehicle.setInvestmentVehicleStatus(DRAFT);
        investmentVehicle.setCreatedDate(LocalDate.now());
        return investmentVehicleOutputPort.save(investmentVehicle);
    }

    private InvestmentVehicle updateInvestmentVehicleInDraft(InvestmentVehicle investmentVehicle) throws MeedlException {
        InvestmentVehicle foundInvestmentVehicle =
                investmentVehicleOutputPort.findById(investmentVehicle.getId());
        investmentVehicleMapper.updateInvestmentVehicle(foundInvestmentVehicle,
                investmentVehicle);
        return investmentVehicleOutputPort.save(foundInvestmentVehicle);
    }

    private InvestmentVehicle publishInvestmentVehicle(InvestmentVehicle investmentVehicle) throws MeedlException {
        investmentVehicle.validate();
        checkIfInvestmentVehicleNameExist(investmentVehicle);
        setInvestmentVehicleNumbersOnMeedlPortfolio(investmentVehicle);
        investmentVehicle.setValues();
        return publishedInvestmentVehicle(investmentVehicle.getId(),investmentVehicle);
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
            return investmentVehicleOutputPort.findById(investmentVehicleId);
        }

        return getInvestmentVehicleFinancier(investmentVehicleId, userId);
    }

    private InvestmentVehicle getInvestmentVehicleFinancier(String investmentVehicleId, String userId) throws MeedlException {
        Financier foundFinancier = financierOutputPort.findFinancierByUserId(userId);
        MeedlValidator.validateUUID(foundFinancier.getId(), FinancierMessages.INVALID_FINANCIER_ID.getMessage());
        MeedlValidator.validateUUID(investmentVehicleId, InvestmentVehicleMessages.INVALID_INVESTMENT_VEHICLE_ID.getMessage());
        InvestmentVehicle foundInvestmentVehicle = investmentVehicleOutputPort.findById(investmentVehicleId);

        if (foundInvestmentVehicle.getInvestmentVehicleVisibility() == InvestmentVehicleVisibility.PUBLIC){
            return foundInvestmentVehicle;
        }
        if (foundInvestmentVehicle.getInvestmentVehicleVisibility() == InvestmentVehicleVisibility.PRIVATE){
            Optional<InvestmentVehicleFinancier> investmentVehicleFinancier = investmentVehicleFinancierOutputPort
                    .findByInvestmentVehicleIdAndFinancierId(investmentVehicleId, foundFinancier.getId());
            if (investmentVehicleFinancier.isPresent()){
                return foundInvestmentVehicle;
            } else {
                throw new MeedlException("Investment Vehicle not found");
            }
        }
        throw new MeedlException("Investment Vehicle not found");
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

    private InvestmentVehicle publishedInvestmentVehicle(String investmentVehicleId,InvestmentVehicle investmentVehicle) throws MeedlException {
        if (ObjectUtils.isNotEmpty(investmentVehicleId)) {
            MeedlValidator.validateUUID(investmentVehicleId, InvestmentVehicleMessages.INVALID_INVESTMENT_VEHICLE_ID.getMessage());
             InvestmentVehicle foundInvestmentVehicle = investmentVehicleOutputPort.findById(investmentVehicleId);
            if (foundInvestmentVehicle.getInvestmentVehicleStatus().equals(InvestmentVehicleStatus.PUBLISHED)) {
                throw new InvestmentException(InvestmentVehicleMessages.INVESTMENT_VEHICLE_ALREADY_PUBLISHED.getMessage());
            }
            investmentVehicleMapper.updateInvestmentVehicle(foundInvestmentVehicle,investmentVehicle);
        }
        String investmentVehicleLink = generateInvestmentVehicleLink(investmentVehicle.getId());
        investmentVehicle.setInvestmentVehicleLink(investmentVehicleLink);
        return investmentVehicleOutputPort.save(investmentVehicle);
    }

    @Override
    public Page<InvestmentVehicle> viewAllInvestmentVehicleByType(int pageSize, int pageNumber, InvestmentVehicleType type) throws MeedlException {
        MeedlValidator.validatePageNumber(pageNumber);
        MeedlValidator.validatePageSize(pageSize);
        MeedlValidator.validateDataElement(String.valueOf(type), InvestmentVehicleMessages.INVESTMENT_VEHICLE_TYPE_CANNOT_BE_NULL.getMessage());
        return investmentVehicleOutputPort.findAllInvestmentVehicleByType(pageSize, pageNumber, type);
    }

    @Override
    public Page<InvestmentVehicle> viewAllInvestmentVehicleByStatus(int pageSize, int pageNumber, InvestmentVehicleStatus status) throws MeedlException {
        MeedlValidator.validatePageNumber(pageNumber);
        MeedlValidator.validatePageSize(pageSize);
        MeedlValidator.validateObjectInstance(status, InvestmentVehicleMessages.INVESTMENT_VEHICLE_STATUS_CANNOT_BE_NULL.getMessage());
        return investmentVehicleOutputPort.findAllInvestmentVehicleByStatus(pageSize, pageNumber, status);
    }

    @Override
    public Page<InvestmentVehicle> viewAllInvestmentVehicleByTypeAndStatus(int pageSize, int pageNumber, InvestmentVehicleType type, InvestmentVehicleStatus status) throws MeedlException {
        MeedlValidator.validatePageSize(pageSize);
        MeedlValidator.validatePageNumber(pageNumber);
        MeedlValidator.validateObjectInstance(type, InvestmentVehicleMessages.INVESTMENT_VEHICLE_TYPE_CANNOT_BE_NULL.getMessage());
        MeedlValidator.validateObjectInstance(status, InvestmentVehicleMessages.INVESTMENT_VEHICLE_STATUS_CANNOT_BE_NULL.getMessage());
        return investmentVehicleOutputPort.findAllInvestmentVehicleByTypeAndStatus(pageSize, pageNumber, type, status);
    }

    @Override
    public Page<InvestmentVehicle> viewAllInvestmentVehicleBy(ViewInvestmentVehicleRequest viewInvestmentVehicleRequest, String userId) throws MeedlException {
        MeedlValidator.validatePageSize(viewInvestmentVehicleRequest.getPageSize());
        MeedlValidator.validatePageNumber(viewInvestmentVehicleRequest.getPageNumber());
        IdentityRole userRole = userIdentityOutputPort.findById(userId).getRole();
        Page<InvestmentVehicle> investmentVehicles = null;

        if (userRole.equals(IdentityRole.FINANCIER)) {
            investmentVehicles = investmentVehicleOutputPort.findAllInvestmentVehicleForFinancier(viewInvestmentVehicleRequest, userId);
        } else {
            investmentVehicles = investmentVehicleOutputPort.findAllInvestmentVehicleBy(viewInvestmentVehicleRequest);
        }
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
        investmentVehicle.setInvestmentVehicleVisibility(investmentVehicleVisibility);
        if (investmentVehicleVisibility.equals(InvestmentVehicleVisibility.PUBLIC)) {
            return investmentVehicleOutputPort.save(investmentVehicle);
        } else if (investmentVehicleVisibility.equals(InvestmentVehicleVisibility.PRIVATE)) {
            for (Financier eachFinancier : financiers) {
                Financier financier = financierOutputPort.findFinancierByFinancierId(eachFinancier.getId());
                InvestmentVehicleFinancier investmentVehicleFinancier = InvestmentVehicleFinancier.builder()
                                .investmentVehicle(investmentVehicle).financier(financier).
                        investmentVehicleDesignation(eachFinancier.getInvestmentVehicleDesignation()).build();
                investmentVehicleFinancierOutputPort.save(investmentVehicleFinancier);
            }
        }
        return investmentVehicleOutputPort.save(investmentVehicle);
    }

    @Override
    public InvestmentVehicle setInvestmentVehicleOperationStatus(InvestmentVehicle investmentVehicle) throws MeedlException {
        MeedlValidator.validateObjectInstance(investmentVehicle,"Investment vehicle object cannot be empty ");
        MeedlValidator.validateObjectInstance(investmentVehicle.getVehicleOperation(),"Vehicle Operation cannot be empty");
        investmentVehicle.getVehicleOperation().validateFundraisingAndDeployingStatus();
        InvestmentVehicle foundInvestmentVehicle = investmentVehicleOutputPort.findById(investmentVehicle.getId());
        if (foundInvestmentVehicle.getVehicleOperation() == null) {
            setNewInvestmentVehicleOperationStatus(investmentVehicle, foundInvestmentVehicle);
            return foundInvestmentVehicle;
        }
        updateExistingInvestmentVehicleOperationStatus(investmentVehicle, foundInvestmentVehicle);
        return foundInvestmentVehicle;
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
        publishInvestmentVehicle(foundInvestmentVehicle);
    }

    private void updateExistingInvestmentVehicleOperationStatus(InvestmentVehicle investmentVehicle, InvestmentVehicle foundInvestmentVehicle) throws MeedlException {
        vehicleOperationMapper.updateExistiingVehicleOperation(foundInvestmentVehicle.getVehicleOperation(), investmentVehicle.getVehicleOperation());
        foundInvestmentVehicle.setVehicleOperation(
                vehicleOperationOutputPort.save(foundInvestmentVehicle.getVehicleOperation())
        );
        investmentVehicleOutputPort.save(foundInvestmentVehicle);
    }


    private String generateInvestmentVehicleLink(String id) {
        return INVESTMENT_VEHICLE_URL+id;
    }


}
