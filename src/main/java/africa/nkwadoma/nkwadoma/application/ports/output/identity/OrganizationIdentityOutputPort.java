package africa.nkwadoma.nkwadoma.application.ports.output.identity;

import africa.nkwadoma.nkwadoma.domain.enums.ActivationStatus;
import africa.nkwadoma.nkwadoma.domain.enums.loanenums.LoanType;
import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import africa.nkwadoma.nkwadoma.domain.model.education.*;
import africa.nkwadoma.nkwadoma.domain.model.identity.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.organization.*;
import org.springframework.data.domain.*;

import java.util.*;

public interface OrganizationIdentityOutputPort {
    OrganizationIdentity save(OrganizationIdentity organizationIdentity) throws MeedlException;

    Optional<OrganizationEntity> findByRcNumber(String rcNumber) throws MeedlException;

    OrganizationIdentity findByEmail(String email) throws MeedlException;
    void delete(String organizationId) throws MeedlException;
    OrganizationIdentity findById(String id) throws MeedlException;
    List<ServiceOffering> getServiceOfferings(String organizationId) throws MeedlException;
    Page<OrganizationIdentity> viewAllOrganization(OrganizationIdentity organizationIdentity) throws MeedlException;
    Page<OrganizationIdentity> viewAllOrganizationByStatus(OrganizationIdentity organizationIdentity, Set<ActivationStatus> status) throws MeedlException;
    List<ServiceOffering> findServiceOfferingById(String id) throws MeedlException;
    Optional<OrganizationIdentity> findByOrganizationId(String organizationId) throws MeedlException;
    List<OrganizationServiceOffering> findOrganizationServiceOfferingsByOrganizationId(String organizationId) throws MeedlException;

    void deleteOrganizationServiceOffering(String organizationServiceOfferingId) throws MeedlException;
    void deleteServiceOffering(String serviceOfferingId) throws MeedlException;
    Page<OrganizationIdentity> findAllWithLoanMetrics(LoanType loanType,int pageSize , int pageNumber) throws MeedlException;
    Page<OrganizationIdentity> findByName(String name,ActivationStatus activationStatus, int pageSize, int pageNumber) throws MeedlException;
    Optional<OrganizationIdentity> findOrganizationByName(String name) throws MeedlException;
    void updateNumberOfCohortInOrganization(String organizationId) throws MeedlException;
    Optional<OrganizationIdentity> findByTin(String tin) throws MeedlException;

    Page<OrganizationIdentity> findByNameSortingByLoanType(String name, LoanType loanType, int pageSize, int pageNumber) throws MeedlException;

    OrganizationIdentity findOrganizationByCohortId(String cohortId) throws MeedlException;

    List<OrganizationIdentity> findAllOrganization();

    Optional<OrganizationIdentity> findByUserId(String userId) throws MeedlException;
}

