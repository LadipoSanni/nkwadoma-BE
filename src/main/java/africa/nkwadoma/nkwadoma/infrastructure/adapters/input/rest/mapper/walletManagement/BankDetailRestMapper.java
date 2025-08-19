package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.mapper.walletManagement;

import africa.nkwadoma.nkwadoma.domain.model.bankdetail.BankDetail;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.walletManagement.BankDetailRequest;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.mapper.DurationTypeMapper;
import jakarta.validation.Valid;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, uses = DurationTypeMapper.class)
public interface BankDetailRestMapper {
    @Mapping(target = "userId", source = "userId")
    BankDetail map(String userId, @Valid BankDetailRequest bankDetailRequest);
}
