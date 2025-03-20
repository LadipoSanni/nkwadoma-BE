package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.mapper.invesmentVehicle;

import africa.nkwadoma.nkwadoma.domain.model.investmentVehicle.Financier;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.investmentVehicle.KycRequest;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.investmentVehicle.FinancierRequest;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.investmentVehicle.KycResponse;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.investmentVehicle.FinancierResponse;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.mapper.UserIdentityMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", uses = UserIdentityMapper.class, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface FinancierRestMapper {
    Financier map(FinancierRequest financierRequest);
    FinancierResponse map(Financier financier);

    KycResponse mapToFinancierResponse(Financier financier);

    @Mapping(target = "individual.id", source = "userId")
//    @Mapping(target = "individual.nextOfKin.firstName", source = "kycRequest.nextOfKinFirstName")
//    @Mapping(target = "individual.nextOfKin.lastName", source = "kycRequest.nextOfKinLastName")
//    @Mapping(target = "individual.nextOfKin.phoneNumber", source = "kycRequest.nextOfKinPhoneNumber")
//    @Mapping(target = "individual.nextOfKin.email", source = "kycRequest.nextOfKinEmail")
//    @Mapping(target = "individual.nextOfKin.contactAddress", source = "kycRequest.nextOfKinContactAddress")
//    @Mapping(target = "individual.nextOfKin.nextOfKinRelationship", source = "kycRequest.relationshipWithNextOfKin")
    Financier map(KycRequest kycRequest, String userId);

    Financier map(FinancierRequest financierRequest, String sub);
}