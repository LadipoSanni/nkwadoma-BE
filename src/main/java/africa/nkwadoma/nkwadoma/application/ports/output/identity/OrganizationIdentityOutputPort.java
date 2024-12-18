package africa.nkwadoma.nkwadoma.application.ports.output.identity;

import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.education.*;
import africa.nkwadoma.nkwadoma.domain.model.identity.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.organization.OrganizationEntity;
import org.springframework.data.domain.Page;

import java.util.*;

public interface OrganizationIdentityOutputPort {
    OrganizationIdentity save(OrganizationIdentity organizationIdentity) throws MeedlException;

    Optional<OrganizationEntity> findByRcNumber(String rcNumber) throws MeedlException;

    OrganizationIdentity findByEmail(String email) throws MeedlException;
    void delete(String organizationId) throws MeedlException;
    OrganizationIdentity findById(String id) throws MeedlException;
    List<ServiceOffering> getServiceOfferings(OrganizationIdentity organizationIdentity) throws MeedlException;
    Page<OrganizationIdentity> viewAllOrganization(OrganizationIdentity organizationIdentity) throws MeedlException;
    boolean existsById(String organizationId) throws MeedlException;
    List<ServiceOffering> findServiceOfferingById(String id) throws MeedlException;
    List<OrganizationServiceOffering> findOrganizationServiceOfferingsByOrganizationId(String organizationId) throws MeedlException;
    void deleteOrganizationServiceOffering(String organizationServiceOfferingId) throws MeedlException;
    void deleteServiceOffering(String serviceOfferingId) throws MeedlException;
    List<OrganizationIdentity> findByName(String name) throws MeedlException;

    void updateNumberOfCohortInOrganization(String organizationId) throws MeedlException;
    Optional<OrganizationIdentity> findByTin(String tin) throws MeedlException;
}

