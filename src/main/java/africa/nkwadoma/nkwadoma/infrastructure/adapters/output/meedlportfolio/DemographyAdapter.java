package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.meedlportfolio;

import africa.nkwadoma.nkwadoma.application.ports.output.meedlportfolio.DemographyOutputPort;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.meedlPortfolio.Demography;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.meedlportfolio.DemographyEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.mapper.DemographyMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.meedlportfolio.DemographyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DemographyAdapter implements DemographyOutputPort {

    private final DemographyRepository demographyRepository;
    private final DemographyMapper demographyMapper;


    @Override
    public Demography save(Demography demography) throws MeedlException {
        MeedlValidator.validateObjectInstance(demography,"Demography cannot be empty");
        DemographyEntity demographyEntity = demographyMapper.toDemographyEntity(demography);
        demographyEntity = demographyRepository.save(demographyEntity);
        return demographyMapper.toDemography(demographyEntity);
    }

    @Override
    public void deleteById(String demographyId) throws MeedlException {
        MeedlValidator.validateUUID(demographyId,"Demography id cannot be empty");
        demographyRepository.deleteById(demographyId);
    }
}
