package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.financier;

import africa.nkwadoma.nkwadoma.application.ports.output.financier.PoliticallyExposedPersonOutputPort;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.financier.BeneficialOwner;
import africa.nkwadoma.nkwadoma.domain.model.financier.PoliticallyExposedPerson;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.financier.BeneficialOwnerMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.financier.PoliticallyExposedPersonMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.financier.BeneficialOwnerEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.financier.PoliticallyExposedPersonEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.financier.PoliticallyExposedPersonRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PoliticallyExposedPersonAdapter implements PoliticallyExposedPersonOutputPort {

    private final PoliticallyExposedPersonRepository politicallyExposedPersonRepository;
    private final PoliticallyExposedPersonMapper politicallyExposedPersonMapper;
    @Override
    public PoliticallyExposedPerson save(PoliticallyExposedPerson politicallyExposedPerson) throws MeedlException {
        MeedlValidator.validateObjectInstance(politicallyExposedPerson, "Politically exposed person can not be empty");
        politicallyExposedPerson.validate();
        log.info("Politically exposed person to save in beneficial owner adapter : {}", politicallyExposedPerson);
        PoliticallyExposedPersonEntity politicallyExposedPersonEntityToSave = politicallyExposedPersonMapper.map(politicallyExposedPerson);
        log.info("Politically exposed person to save mapped : {}",politicallyExposedPersonEntityToSave);
        PoliticallyExposedPersonEntity politicallyExposedPersonEntity = politicallyExposedPersonRepository.save(politicallyExposedPersonEntityToSave);
        log.info("Politically exposed person saved successfully {}", politicallyExposedPersonEntity);
        return politicallyExposedPersonMapper.map(politicallyExposedPersonEntity);
    }

    @Override
    public PoliticallyExposedPerson findById(String politicallyExposedPersonId) throws MeedlException {
        MeedlValidator.validateUUID(politicallyExposedPersonId, "Politically exposed person id cannot be empty");
        PoliticallyExposedPersonEntity politicallyExposedPersonEntity = politicallyExposedPersonRepository.findById(politicallyExposedPersonId)
                .orElseThrow(() -> new MeedlException("Politically exposed person not found"));
        return politicallyExposedPersonMapper.map(politicallyExposedPersonEntity);
    }

    @Override
    public void deleteById(String politicallyExposedPersonId) throws MeedlException {
        MeedlValidator.validateUUID(politicallyExposedPersonId, "Beneficial owner id cannot be empty");
        politicallyExposedPersonRepository.deleteById(politicallyExposedPersonId);
    }
}
