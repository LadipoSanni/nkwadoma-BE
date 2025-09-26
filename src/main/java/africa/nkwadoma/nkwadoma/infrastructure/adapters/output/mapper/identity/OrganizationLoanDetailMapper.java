package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.identity;

import africa.nkwadoma.nkwadoma.domain.model.identity.OrganizationLoanDetail;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.organization.OrganizationLoanDetailEntity;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)

public interface OrganizationLoanDetailMapper {
    OrganizationLoanDetailEntity toOrganizationLoanEntity(OrganizationLoanDetail programLoanDetail);

    OrganizationLoanDetail toOrganizationLoanDetail(OrganizationLoanDetailEntity programLoanDetailEntity);
}
