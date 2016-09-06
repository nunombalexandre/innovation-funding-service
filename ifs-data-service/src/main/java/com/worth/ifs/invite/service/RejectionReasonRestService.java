package com.worth.ifs.invite.service;

import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.invite.resource.RejectionReasonResource;

import java.util.List;

/**
 * Interface for CRUD operations on {@link com.worth.ifs.invite.domain.RejectionReason} related data.
 */
public interface RejectionReasonRestService {

    RestResult<List<RejectionReasonResource>> findAllActive();

}