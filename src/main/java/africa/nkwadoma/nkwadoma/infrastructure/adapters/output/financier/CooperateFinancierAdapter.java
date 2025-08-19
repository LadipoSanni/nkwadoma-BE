package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.financier;

import africa.nkwadoma.nkwadoma.application.ports.output.financier.CooperateFinancierOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.constants.identity.UserMessages;
import africa.nkwadoma.nkwadoma.domain.enums.constants.investmentVehicle.FinancierMessages;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.exceptions.ResourceNotFoundException;
import africa.nkwadoma.nkwadoma.domain.model.financier.CooperateFinancier;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.financier.CooperateFinancierMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.financier.CooperateFinancierEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.financier.CooperateFinancierRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Component
public class CooperateFinancierAdapter implements CooperateFinancierOutputPort {

    private final CooperateFinancierMapper cooperateFinancierMapper;
    private final CooperateFinancierRepository cooperateFinancierRepository;

    @Override
    public CooperateFinancier save(CooperateFinancier cooperateFinancier) throws MeedlException {
        MeedlValidator.validateObjectInstance(cooperateFinancier, FinancierMessages.COOPERATE_FINANCIER_CANNOT_BE_EMPTY.getMessage());
        cooperateFinancier.validate();

        CooperateFinancierEntity cooperateFinancierEntity =
                cooperateFinancierMapper.toCooperateFinancierEntity(cooperateFinancier);

        cooperateFinancierEntity = cooperateFinancierRepository.save(cooperateFinancierEntity);

        return cooperateFinancierMapper.toCooperateFinancier(cooperateFinancierEntity);
    }

    @Override
    public void delete(String id) throws MeedlException {
        MeedlValidator.validateUUID(id,"Cooperate financier id cannot br empty");
        cooperateFinancierRepository.deleteById(id);
    }

    public CooperateFinancier findByUserId(String id) throws MeedlException {
        MeedlValidator.validateUUID(id, UserMessages.INVALID_USER_ID.getMessage());

        CooperateFinancierEntity cooperateFinancierEntity =
                cooperateFinancierRepository.findByFinancier_UserIdentityId(id);

        return cooperateFinancierMapper.toCooperateFinancier(cooperateFinancierEntity);
    }

    @Override
    public CooperateFinancier findById(String cooperateFinancierId) throws MeedlException {
        MeedlValidator.validateUUID(cooperateFinancierId,"Cooperate financier id cannot br empty");

        CooperateFinancierEntity cooperateFinancierEntity =
                cooperateFinancierRepository.findById(cooperateFinancierId).
                        orElseThrow(()-> new ResourceNotFoundException("Cooperate financier not found"));


        return cooperateFinancierMapper.toCooperateFinancier(cooperateFinancierEntity);
    }
}
