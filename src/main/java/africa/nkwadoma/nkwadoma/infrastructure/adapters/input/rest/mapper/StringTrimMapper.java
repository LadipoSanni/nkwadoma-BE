package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Named;
import org.springframework.stereotype.Component;

@Mapper
@Component
public class StringTrimMapper {
    @Named("trimString")
    public String trimString(String value) {
        return value == null ? null : value.trim();
    }
}

