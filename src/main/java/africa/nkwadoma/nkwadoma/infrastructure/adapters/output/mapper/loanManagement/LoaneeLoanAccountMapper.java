package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.loanManagement;

import africa.nkwadoma.nkwadoma.domain.model.loan.LoaneeLoanAccount;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanentity.LoaneeLoanAccountEntity;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)

public interface LoaneeLoanAccountMapper {
    LoaneeLoanAccountEntity toLoaneeLoanAccountEntity(LoaneeLoanAccount loaneeLoanAccount);

    LoaneeLoanAccount toLoaneeLoanAccount(LoaneeLoanAccountEntity loaneeLoanAccountEntity);
}
