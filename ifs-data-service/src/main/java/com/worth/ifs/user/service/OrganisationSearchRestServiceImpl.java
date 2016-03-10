package com.worth.ifs.user.service;

import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.commons.service.BaseRestService;
import com.worth.ifs.organisation.resource.OrganisationSearchResult;
import com.worth.ifs.user.domain.Organisation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * OrganisationRestServiceImpl is a utility for CRUD operations on {@link Organisation}.
 * This class connects to the {@link com.worth.ifs.user.controller.OrganisationController}
 * through a REST call.
 */
@Service
public class OrganisationSearchRestServiceImpl extends BaseRestService implements OrganisationSearchRestService {

    @Value("${ifs.data.service.rest.organisationsearch}")
    String organisationRestURL;

    @Override
    public RestResult<List<OrganisationSearchResult>> searchOrganisation(Long organisationTypeId, String organisationSearchText) {
        return getWithRestResult(organisationRestURL + "/searchOrganisations/" + organisationTypeId + "/"+ organisationSearchText, new ParameterizedTypeReference<List<OrganisationSearchResult>>() {});
    }

    @Override
    public RestResult<OrganisationSearchResult> getOrganisation(Long organisationTypeId, String searchOrganisationId) {
        return getWithRestResult(organisationRestURL + "/getOrganisation/" + organisationTypeId + "/" + searchOrganisationId, new ParameterizedTypeReference<OrganisationSearchResult>() {});
    }
}