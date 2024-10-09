package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence;

import africa.nkwadoma.nkwadoma.application.ports.output.investmentVehicle.InvestmentVehicleIdentityOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.FundRaisingStatus;
import africa.nkwadoma.nkwadoma.domain.exceptions.InvestmentException;
import africa.nkwadoma.nkwadoma.domain.exceptions.MiddlException;
import africa.nkwadoma.nkwadoma.domain.model.investmentVehicle.InvestmentVehicleIdentity;
import africa.nkwadoma.nkwadoma.domain.validation.InvestmentVehicleIdentityValidator;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.investmentVehicle.InvestmentVehicleEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.mapper.InvestmentVehicleIdentityMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.InvestmentVehicleEntityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Objects;

import static africa.nkwadoma.nkwadoma.domain.enums.constants.InvestmentMessages.*;


@Slf4j
@RequiredArgsConstructor
public class InvestmentVehicleIdentityAdapter implements InvestmentVehicleIdentityOutputPort {

    private final InvestmentVehicleEntityRepository investmentVehicleRepository;
    private final InvestmentVehicleIdentityMapper investmentVehicleIdentityMapper;

    @Override
    public InvestmentVehicleIdentity save(InvestmentVehicleIdentity investmentVehicleIdentity) throws MiddlException {
        InvestmentVehicleIdentityValidator.validateInvestmentIdentityValidator(investmentVehicleIdentity);
        checkIfInvestmentVehicleNameExist(investmentVehicleIdentity);
        if (investmentVehicleIdentity.getId()== null) investmentVehicleIdentity.setFundRaisingStatus(FundRaisingStatus.FUND_RAISING);
        InvestmentVehicleEntity investmentEntity =
                investmentVehicleIdentityMapper.toInvestmentVehicleEntity(investmentVehicleIdentity);
        investmentEntity = investmentVehicleRepository.save(investmentEntity);
        return investmentVehicleIdentityMapper.toInvestmentVehicleIdentity(investmentEntity);
    }

    private void checkIfInvestmentVehicleNameExist(InvestmentVehicleIdentity investmentVehicleIdentity) throws MiddlException {
        if (investmentVehicleRepository.findByName(investmentVehicleIdentity.getName()).isPresent() &&
                !Objects.equals(investmentVehicleRepository.findByName(investmentVehicleIdentity.getName()).get().getId()
                        ,investmentVehicleIdentity.getId())){
              throw new InvestmentException(INVESTMENT_VEHICLE_NAME_EXIST.getMessage());
            }

    }

    @Override
    public InvestmentVehicleIdentity findById(String id) throws MiddlException {
        if (id != null){
            InvestmentVehicleEntity investmentVehicleEntity =
                    investmentVehicleRepository.findById(id).orElseThrow(()->new InvestmentException(INVESTMENT_VEHICLE_NOT_FOUND.getMessage()));
            log.info("this is ======={}",investmentVehicleEntity);
            return investmentVehicleIdentityMapper.toInvestmentVehicleIdentity(investmentVehicleEntity);
        }
        throw new InvestmentException(INVESTMENT_IDENTITY_CANNOT_BE_NULL.getMessage());
    }

}
