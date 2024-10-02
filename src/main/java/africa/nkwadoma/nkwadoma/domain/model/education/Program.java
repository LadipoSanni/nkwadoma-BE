package africa.nkwadoma.nkwadoma.domain.model.education;

import africa.nkwadoma.nkwadoma.domain.enums.DurationStatus;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Program {
    private String programDescription;
    private String name;
    private DurationStatus durationStatus;
    private String organizationId;
}
