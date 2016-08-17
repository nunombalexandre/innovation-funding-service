package com.worth.ifs.project.finance.transactional;

import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.project.resource.ProjectResource;
import com.worth.ifs.project.resource.SpendProfileResource;
import com.worth.ifs.project.resource.SpendProfileTableResource;
import com.worth.ifs.security.NotSecured;
import com.worth.ifs.security.SecuredBySpring;
import org.springframework.security.access.prepost.PreAuthorize;

/**
 * Service dealing with Project finance operations
 */
public interface ProjectFinanceService {

    @PreAuthorize("hasAuthority('project_finance')")
    @SecuredBySpring(value = "GENERATE_SPEND_PROFILE", securedType = ProjectResource.class, description = "A member of the internal Finance Team can generate a Spend Profile for any Project" )
    ServiceResult<Void> generateSpendProfile(Long projectId);

    @NotSecured(value = "", mustBeSecuredByOtherServices = false) // TODO - This needs to be changed once Security is added
    ServiceResult<SpendProfileTableResource> getSpendProfileTable(Long projectId, Long organisationId);

    @NotSecured(value="", mustBeSecuredByOtherServices = false) // TODO - This needs to be changed once Security is added
    ServiceResult<SpendProfileResource> getSpendProfile(Long projectId, Long organisationId);
}