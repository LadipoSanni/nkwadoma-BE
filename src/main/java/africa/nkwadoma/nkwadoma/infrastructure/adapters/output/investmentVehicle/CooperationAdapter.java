package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.investmentVehicle;

import africa.nkwadoma.nkwadoma.application.ports.output.investmentVehicle.CooperationOutputPort;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.investmentVehicle.Cooperation;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.investmentVehicle.CooperationMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.investmentVehicle.CooperationEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.investmentVehicle.CooperationRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

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
        log.info("Cooperation saved successfully with name : {}", cooperation.getName());
        CooperationEntity cooperationEntity = cooperationRepository.save(cooperationMapper.toCooperationEntity(cooperation));
        log.info("Cooperation saved successfully {}", cooperationEntity.getName());
        return cooperationMapper.toCooperation(cooperationEntity);
    }

    private void confirmCooperationDoesNotPreviouslyExist(Cooperation cooperation) throws MeedlException {
        boolean cooperationExistByEmail = cooperationRepository.existsByEmail(cooperation.getUserIdentity().getEmail());
        if (cooperationExistByEmail) {
            log.error("Cooperation already exists with email {} ", cooperation.getUserIdentity().getEmail());
            throw new MeedlException("Cooperation with the same email already exists");
        }
        boolean cooperationExistByName = cooperationRepository.existsByName(cooperation.getName());
        if (cooperationExistByName) {
            log.error("Cooperation already exists with name {} ", cooperation.getName());
            throw new MeedlException("Cooperation with the same name already exists");
        }
    }

    @Override
    public Cooperation findById(String cooperationId) throws MeedlException {
        MeedlValidator.validateUUID(cooperationId, "Cooperation id cannot be empty");
        CooperationEntity cooperationEntity = cooperationRepository.findById(cooperationId)
                .orElseThrow(() -> new MeedlException("Cooperation not found"));
        return cooperationMapper.toCooperation(cooperationEntity);
    }

    @Override
    public void deleteById(String cooperationId) throws MeedlException {
        MeedlValidator.validateUUID(cooperationId, "Cooperation id cannot be empty");
        cooperationRepository.deleteById(cooperationId);
    }

    @Override
    public Cooperation findByEmail(String email) throws MeedlException {
        MeedlValidator.validateEmail(email);
        CooperationEntity cooperationEntity = cooperationRepository.findByEmail(email);
        return cooperationMapper.toCooperation(cooperationEntity);
    }

    @Override
    public Cooperation findByName(String name) throws MeedlException {
        MeedlValidator.validateDataElement(name, "Cooperation name can not be empty");
        CooperationEntity cooperationEntity = cooperationRepository.findByName(name);
        return cooperationMapper.toCooperation(cooperationEntity);
    }
}
