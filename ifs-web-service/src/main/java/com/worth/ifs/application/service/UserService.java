package com.worth.ifs.application.service;

import com.worth.ifs.application.resource.ApplicationResource;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.user.domain.ProcessRole;
import com.worth.ifs.user.domain.User;
import com.worth.ifs.user.resource.UserResource;

import java.util.List;
import java.util.Set;

/**
 * Interface for CRUD operations on {@link User} related data.
 */
public interface UserService {
    List<User> getAssignable(Long applicationId);
    Boolean isLeadApplicant(Long userId, ApplicationResource application);
    ProcessRole getLeadApplicantProcessRoleOrNull(ApplicationResource application);
    RestResult<List<UserResource>> findUserByEmail(String email);
    Set<User> getAssignableUsers(ApplicationResource application);
    Set<User> getApplicationUsers(ApplicationResource application);
    RestResult<UserResource> createLeadApplicantForOrganisation(String firstName, String lastName, String password, String email, String title, String phoneNumber, Long organisationId);
    RestResult<UserResource> updateDetails(String email, String firstName, String lastName, String title, String phoneNumber);
}
