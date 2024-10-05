package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "password_history",uniqueConstraints = {@UniqueConstraint(columnNames = {"password", "middl_user"},
        name = "uk_password_history")})
public class PasswordHistoryEntity {
    @Id
    @UuidGenerator
    private String id;
    private String middlUser;
    private String password;
}
