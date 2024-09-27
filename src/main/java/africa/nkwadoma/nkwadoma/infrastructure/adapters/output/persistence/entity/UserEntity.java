package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity;

import jakarta.persistence.*;
import lombok.*;


@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "middl_user")
public class UserEntity {
    @Id
    private String id;
    private String email;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private boolean emailVerified;
    private boolean enabled;
    private String createdAt;
    private String role;
    private String createdBy;
}
