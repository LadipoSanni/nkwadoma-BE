package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository;

import africa.nkwadoma.nkwadoma.domain.enums.ActivationStatus;
import africa.nkwadoma.nkwadoma.domain.enums.IdentityRole;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.organization.OrganizationEmployeeEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.identity.*;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.*;

public interface EmployeeAdminEntityRepository extends JpaRepository<OrganizationEmployeeEntity,String> {
    @Query("""
          select
          oe.id as id, oe.meedlUser.firstName as firstName, oe.meedlUser.lastName as lastName,
          oe.meedlUser.email as email, o.status as status
          from OrganizationEmployeeEntity oe
          join OrganizationEntity o on oe.organization = o.id
          where o.id = :organizationId
    """)
    Page<OrganizationEmployeeProjection> findAllByOrganization(String organizationId, Pageable pageable);
    OrganizationEmployeeEntity findByMeedlUserId(String userId);
    void deleteByMeedlUserId(String id);
    Optional<OrganizationEmployeeEntity> findByMeedlUser_CreatedBy(String createdBy);

    List<OrganizationEmployeeEntity> findAllByOrganization(String organizationId);

    @Query("SELECT o FROM OrganizationEmployeeEntity o " +
            "WHERE o.organization = :organizationId " +
            "AND (:roles IS NULL OR o.meedlUser.role IN :roles) " +
            "AND (:activationStatus IS NULL OR o.status = :activationStatus) " +
            "AND (:enabled IS NULL OR o.meedlUser.enabled = :enabled)" +
            "AND (" +
            "   upper(concat(o.meedlUser.firstName, ' ', o.meedlUser.lastName)) LIKE upper(concat('%', :nameFragment, '%')) " +
            "   OR upper(concat(o.meedlUser.lastName, ' ', o.meedlUser.firstName)) LIKE upper(concat('%', :nameFragment, '%')) " +
            "   OR upper(o.meedlUser.firstName) LIKE upper(concat('%', :nameFragment, '%')) " +
            "   OR upper(o.meedlUser.lastName) LIKE upper(concat('%', :nameFragment, '%')) " +
            "   OR upper(o.meedlUser.email) LIKE upper(concat('%', :nameFragment, '%'))" +
            ") " +
            "ORDER BY o.meedlUser.createdAt DESC")
    Page<OrganizationEmployeeEntity> findAdminsByNameFilters(
            @Param("organizationId") String organizationId,
            @Param("nameFragment") String nameFragment,
            @Param("roles") Set<IdentityRole> roles,
            @Param("activationStatus") ActivationStatus activationStatus,
            @Param("enabled") Boolean onlyEnabled,
            Pageable pageable
    );


    @Query("""
    SELECT e FROM OrganizationEmployeeEntity e
    WHERE e.organization = :organizationId
      AND e.meedlUser.role IN :roles
      AND (:status IS NULL OR e.status = :status)
      AND (:enabled IS NULL OR e.meedlUser.enabled = :enabled)
      ORDER BY e.meedlUser.createdAt DESC
""")
    Page<OrganizationEmployeeEntity> findAllByOrgIdRoleInAndOptionalFilters(
            @Param("organizationId") String organizationId,
            @Param("roles") Set<IdentityRole> roles,
            @Param("status") ActivationStatus status,
            @Param("enabled") Boolean enabled,
            Pageable pageable
    );

    List<OrganizationEmployeeEntity> findByOrganization(String organizationId);

    @Query("""
            select o from OrganizationEmployeeEntity o
            where o.organization = :organizationId
            and upper(concat(o.meedlUser.firstName, ' ', o.meedlUser.lastName)) like upper(concat('%', :nameFragment, '%'))
            """)
    Page<OrganizationEmployeeEntity> findEmployeeInOrganizationbByIdAndName(@Param("organizationId")
                                                                            String organizationId,
                                                                            @Param("nameFragment") String nameFragment,
                                                                            Pageable pageRequest);

    List<OrganizationEmployeeEntity> findOrganizationEmployeeEntityByOrganizationAndMeedlUserRole(String organizationId, IdentityRole identityRole);

    Optional<OrganizationEmployeeEntity> findByMeedlUserRoleAndOrganization(IdentityRole identityRole, String organizationId);
}
