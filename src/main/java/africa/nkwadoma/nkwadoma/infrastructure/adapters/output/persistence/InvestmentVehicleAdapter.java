package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence;

import africa.nkwadoma.nkwadoma.application.ports.output.investmentVehicle.InvestmentVehicleOutputPort;
import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.investmentVehicle.InvestmentVehicle;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.investmentVehicle.InvestmentVehicleEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.mapper.InvestmentVehicleMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.InvestmentVehicleEntityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

import static africa.nkwadoma.nkwadoma.domain.enums.constants.InvestmentMessages.*;


@Slf4j
@RequiredArgsConstructor
public class InvestmentVehicleAdapter implements InvestmentVehicleOutputPort {

    private final InvestmentVehicleEntityRepository investmentVehicleRepository;
    private final InvestmentVehicleMapper investmentVehicleMapper;

    @Override
    public InvestmentVehicle save(InvestmentVehicle investmentVehicle) throws MeedlException {
        MeedlValidator.validateObjectInstance(investmentVehicle,"Investment Vehicle Object Cannot Be Null");
        investmentVehicle.validate();
        InvestmentVehicleEntity investmentEntity =
                investmentVehicleMapper.toInvestmentVehicleEntity(investmentVehicle);
        investmentEntity = investmentVehicleRepository.save(investmentEntity);
        return investmentVehicleMapper.toInvestmentVehicle(investmentEntity);
    }


    @Override
    public Page<InvestmentVehicle> findAllInvestmentVehicle(int pageSize, int pageNumber) {
        Pageable pageRequest = PageRequest.of(pageNumber,pageSize);
        Page<InvestmentVehicleEntity> investmentVehicleEntities = investmentVehicleRepository.findAll(pageRequest);
        return investmentVehicleEntities.map(investmentVehicleMapper::toInvestmentVehicle);
    }

    @Override
    public InvestmentVehicle findByName(String name) throws MeedlException {
        MeedlValidator.validateObjectName(name);
        InvestmentVehicleEntity investmentVehicleEntity =
                investmentVehicleRepository.findByName(name);
        return investmentVehicleMapper.toInvestmentVehicle(investmentVehicleEntity);
    }

    @Override
    public InvestmentVehicle findById(String id) throws MeedlException {
        if (id != null){
            InvestmentVehicleEntity investmentVehicleEntity =
                    investmentVehicleRepository.findById(id).orElseThrow(()->new InvestmentException(INVESTMENT_VEHICLE_NOT_FOUND.getMessage()));
            return investmentVehicleMapper.toInvestmentVehicle(investmentVehicleEntity);
        }
        throw new InvestmentException(INVESTMENT_IDENTITY_CANNOT_BE_NULL.getMessage());
    }

    @Override
    public void deleteInvestmentVehicle(String id) throws MeedlException {
        MeedlValidator.validateUUID(id);
        investmentVehicleRepository.deleteById(id);
    }


}
