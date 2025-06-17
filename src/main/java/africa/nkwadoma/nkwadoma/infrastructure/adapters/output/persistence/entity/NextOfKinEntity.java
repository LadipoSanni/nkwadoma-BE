package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class NextOfKinEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String nextOfKinRelationship;
    private String contactAddress;
    private String userId;
}
