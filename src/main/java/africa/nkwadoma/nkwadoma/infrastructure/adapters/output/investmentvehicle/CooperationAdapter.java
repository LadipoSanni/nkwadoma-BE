package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.investmentvehicle;

import africa.nkwadoma.nkwadoma.application.ports.output.investmentvehicle.CooperationOutputPort;
import africa.nkwadoma.nkwadoma.domain.exceptions.InvestmentException;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.exceptions.ResourceNotFoundException;
import africa.nkwadoma.nkwadoma.domain.model.investmentvehicle.Cooperation;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.investmentvehicle.CooperationMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.investmentvehicle.CooperationEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.investmentvehicle.CooperationRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class CooperationAdapter implements CooperationOutputPort {
    private final CooperationRepository cooperationRepository;
    private final CooperationMapper cooperationMapper;
    @Override
    public Cooperation save(Cooperation cooperation) throws MeedlException {
        MeedlValidator.validateObjectInstance(cooperation, "Cooperation can not be empty");
        cooperation.validate();
        confirmCooperationDoesNotPreviouslyExist(cooperation);
        log.info("Cooperation to save in cooperation adapter : {}", cooperation);
        CooperationEntity cooperationEntityToSave = cooperationMapper.toCooperationEntity(cooperation);
        log.info("Cooperation to save mapped : {}",cooperationEntityToSave);
        CooperationEntity cooperationEntity = cooperationRepository.save(cooperationEntityToSave);
        log.info("Cooperation saved successfully {}", cooperationEntity);
        return cooperationMapper.toCooperation(cooperationEntity);
    }

    private void confirmCooperationDoesNotPreviouslyExist(Cooperation cooperation) throws MeedlException {
        boolean cooperationExistByName = cooperationRepository.existsByName(cooperation.getName());
        if (cooperationExistByName) {
            log.error("Cooperation already exists with name {} ", cooperation.getName());
            throw new InvestmentException("Cooperation with the same name already exists");
        }
    }

    @Override
    public Cooperation findById(String cooperationId) throws MeedlException {
        MeedlValidator.validateUUID(cooperationId, "Cooperation id cannot be empty");
        CooperationEntity cooperationEntity = cooperationRepository.findById(cooperationId)
                .orElseThrow(() -> new ResourceNotFoundException("Cooperation not found"));
        return cooperationMapper.toCooperation(cooperationEntity);
    }

    @Override
    public void deleteById(String cooperationId) throws MeedlException {
        MeedlValidator.validateUUID(cooperationId, "Cooperation id cannot be empty");
        cooperationRepository.deleteById(cooperationId);
    }

    @Override
    public Cooperation findByName(String name) throws MeedlException {
        MeedlValidator.validateDataElement(name, "Cooperation name can not be empty");
        CooperationEntity cooperationEntity = cooperationRepository.findByName(name);
        return cooperationMapper.toCooperation(cooperationEntity);
    }

    @Override
    public Cooperation findByEmail(String email) throws MeedlException {
        MeedlValidator.validateEmail(email);
        CooperationEntity cooperationEntity = cooperationRepository.findByEmail(email);

        return cooperationMapper.toCooperation(cooperationEntity);
    }
}
