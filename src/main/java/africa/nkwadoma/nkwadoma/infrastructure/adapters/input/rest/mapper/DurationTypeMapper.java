package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.mapper;


import africa.nkwadoma.nkwadoma.domain.enums.*;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface DurationTypeMapper {
    @EnumMapping(nameTransformationStrategy = MappingConstants.CASE_TRANSFORMATION, configuration = "upper")
    @ValueMappings({
            @ValueMapping(target = "MONTHS", source = "Months"),
            @ValueMapping(target = "WEEKS", source = "Weeks"),
            @ValueMapping(target = "YEARS", source = "Years")
    })
    DurationType toDurationType(String durationType);

    default DurationType maptoDurationType(String durationType) {
        durationType = durationType.trim().toUpperCase();
        return DurationType.valueOf(durationType);
    }
}
