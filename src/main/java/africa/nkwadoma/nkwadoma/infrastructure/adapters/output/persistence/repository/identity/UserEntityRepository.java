package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.identity;

import africa.nkwadoma.nkwadoma.domain.enums.identity.IdentityRole;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.identity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface UserEntityRepository extends JpaRepository<UserEntity, String> {
    Optional<UserEntity> findByEmailIgnoreCase(String email);

    UserEntity findByBvn(String bvn);

    List<UserEntity> findAllByRole(IdentityRole identityRole);

    boolean existsByEmailIgnoreCase(String email);

    @Query("SELECT u FROM UserEntity u WHERE u.role IN (:adminRoles)")
    List<UserEntity> findAllByRoles(Set<IdentityRole> adminRoles);

    @Query("select user from UserEntity  user where user.role = 'MEEDL_SUPER_ADMIN' ")
    UserEntity findByRole_MeedlSuperAdmin();

    @Query("select user from UserEntity  user where user.role = 'MEEDL_SUPER_ADMIN' ")
    Optional<UserEntity> findAllByRoleAndFinancierId(IdentityRole identityRole, String financierId);
}
