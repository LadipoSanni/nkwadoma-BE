package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity;

import africa.nkwadoma.nkwadoma.domain.enums.*;
import jakarta.persistence.*;
import lombok.*;

import java.util.*;
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class ServiceOfferingEntity {
    @Id
    private String serviceOfferingId;
    private Industry industry;
    private ServiceOfferingType offering;
    @ElementCollection
    private List<String> serviceOfferings = new ArrayList<>();
}
