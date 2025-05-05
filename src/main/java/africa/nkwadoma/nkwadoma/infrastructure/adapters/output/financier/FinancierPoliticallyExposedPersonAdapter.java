package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.financier;

import africa.nkwadoma.nkwadoma.application.ports.output.financier.FinancierPoliticallyExposedPersonOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.constants.investmentVehicle.FinancierMessages;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.financier.FinancierPoliticallyExposedPerson;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.financier.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.financier.FinancierPoliticallyExposedPersonEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.financier.FinancierPoliticallyExposedPersonRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class FinancierPoliticallyExposedPersonAdapter implements FinancierPoliticallyExposedPersonOutputPort {
    private final FinancierPoliticallyExposedPersonRepository financierPoliticallyExposedPersonRepository;
    private final FinancierPoliticallyExposedPersonMapper financierPoliticallyExposedPersonMapper;
    private final PoliticallyExposedPersonMapper politicallyExposedPersonMapper;
    private final FinancierMapper financierMapper;
    @Override
    public FinancierPoliticallyExposedPerson save(FinancierPoliticallyExposedPerson financierPoliticallyExposedPerson) throws MeedlException {
        MeedlValidator.validateObjectInstance(financierPoliticallyExposedPerson, "Financier politically exposed person can not be empty");
        financierPoliticallyExposedPerson.validate();
        log.info("Financier  to save in politically exposed person adapter : {}", financierPoliticallyExposedPerson);
        FinancierPoliticallyExposedPersonEntity financierPoliticallyExposedPersonEntityToSave = mapToFinancierPoliticallyExposedPersonEntity(financierPoliticallyExposedPerson);

        log.info("Financier politically exposed person to save mapped : {}",financierPoliticallyExposedPersonEntityToSave);
        FinancierPoliticallyExposedPersonEntity financierPoliticallyExposedPersonEntity = financierPoliticallyExposedPersonRepository.save(financierPoliticallyExposedPersonEntityToSave);
        log.info("Financier politically exposed person saved successfully {}", financierPoliticallyExposedPersonEntity);
        return financierPoliticallyExposedPersonMapper.map(financierPoliticallyExposedPersonEntity);
    }

    @Override
    public FinancierPoliticallyExposedPerson findById(String politicallyExposedPersonId) throws MeedlException {
        MeedlValidator.validateUUID(politicallyExposedPersonId, "Financier politically exposed person id cannot be empty");
        FinancierPoliticallyExposedPersonEntity financierPoliticallyExposedPersonEntity= financierPoliticallyExposedPersonRepository.findById(politicallyExposedPersonId)
                .orElseThrow(() -> new MeedlException("Financier politically exposed person not found"));
        return financierPoliticallyExposedPersonMapper.map(financierPoliticallyExposedPersonEntity);
    }

    @Override
    public void deleteById(String politicallyExposedPersonId) throws MeedlException {
        MeedlValidator.validateUUID(politicallyExposedPersonId, "Financier politically exposed person id cannot be empty");
        financierPoliticallyExposedPersonRepository.deleteById(politicallyExposedPersonId);
    }

    @Override
    public List<FinancierPoliticallyExposedPerson> findAllByFinancierId(String financierId) throws MeedlException {
        MeedlValidator.validateUUID(financierId, FinancierMessages.INVALID_FINANCIER_ID.getMessage());
        List<FinancierPoliticallyExposedPersonEntity> financierPoliticallyExposedPersonEntities = financierPoliticallyExposedPersonRepository.findAllByFinancier_Id(financierId);
        log.info("Financier politically exposed person found {}", financierPoliticallyExposedPersonEntities);
        return financierPoliticallyExposedPersonEntities
                .stream().map(this::mapToFinancierPoliticallyExposedPerson)
                .toList();
    }
    private FinancierPoliticallyExposedPersonEntity mapToFinancierPoliticallyExposedPersonEntity(FinancierPoliticallyExposedPerson financierPoliticallyExposedPerson) {
        FinancierPoliticallyExposedPersonEntity financierBeneficialOwnerEntity = new FinancierPoliticallyExposedPersonEntity();
        financierBeneficialOwnerEntity.setPoliticallyExposedPerson(politicallyExposedPersonMapper.map(financierPoliticallyExposedPerson.getPoliticallyExposedPerson()));
        financierBeneficialOwnerEntity.setFinancier(financierMapper.map(financierPoliticallyExposedPerson.getFinancier()));
        return financierBeneficialOwnerEntity;
    }
    private FinancierPoliticallyExposedPerson mapToFinancierPoliticallyExposedPerson(FinancierPoliticallyExposedPersonEntity financierPoliticallyExposedPersonEntity) {
        FinancierPoliticallyExposedPerson financierPoliticallyExposedPerson = new FinancierPoliticallyExposedPerson();
        financierPoliticallyExposedPerson.setId(financierPoliticallyExposedPersonEntity.getId());
        financierPoliticallyExposedPerson.setPoliticallyExposedPerson(politicallyExposedPersonMapper.map(financierPoliticallyExposedPersonEntity.getPoliticallyExposedPerson()));
        financierPoliticallyExposedPerson.setFinancier(financierMapper.map(financierPoliticallyExposedPersonEntity.getFinancier()));
        return financierPoliticallyExposedPerson;
    }
}
