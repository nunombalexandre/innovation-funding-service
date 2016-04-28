package com.worth.ifs.finance.security;

import com.worth.ifs.finance.domain.ApplicationFinance;
import com.worth.ifs.finance.domain.Cost;
import com.worth.ifs.finance.repository.CostRepository;
import com.worth.ifs.finance.resource.cost.CostItem;
import com.worth.ifs.security.PermissionRule;
import com.worth.ifs.security.PermissionRules;
import com.worth.ifs.user.repository.ProcessRoleRepository;
import com.worth.ifs.user.repository.RoleRepository;
import com.worth.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.worth.ifs.security.SecurityRuleUtil.checkRole;
import static com.worth.ifs.user.domain.UserRoleType.COLLABORATOR;
import static com.worth.ifs.user.domain.UserRoleType.LEADAPPLICANT;


/**
 * Permission rules for {@link Cost} and {@link CostItem} for permissioning
 */
@Component
@PermissionRules
public class CostPermissionRules {

    @Autowired
    private ProcessRoleRepository processRoleRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private CostRepository costRepository;

    @PermissionRule(value = "UPDATE", description = "The consortium can update the cost for their application and organisation")
    public boolean consortiumCanUpdateACostForTheirApplicationAndOrganisation(final Cost cost, final UserResource user) {
        return isCollaborator(cost, user);
    }

    @PermissionRule(value = "DELETE", description = "The consortium can update the cost for their application and organisation")
    public boolean consortiumCanDeleteACostForTheirApplicationAndOrganisation(final Cost cost, final UserResource user) {
        return isCollaborator(cost, user);
    }

    @PermissionRule(value = "READ", description = "The consortium can read the cost for their application and organisation")
    public boolean consortiumCanReadACostForTheirApplicationAndOrganisation(final Cost cost, final UserResource user) {
        return isCollaborator(cost, user);
    }

    @PermissionRule(value = "READ", description = "The consortium can read the cost for their application and organisation")
    public boolean consortiumCanReadACostForTheirApplicationAndOrganisation(final CostItem costItem, final UserResource user) {
        return isCollaborator(costRepository.findOne(costItem.getId()), user);
    }

    private boolean isCollaborator(final Cost cost, final UserResource user) {
        final ApplicationFinance applicationFinance = cost.getApplicationFinance();
        final Long applicationId = applicationFinance.getApplication().getId();
        final Long organisationId = applicationFinance.getOrganisation().getId();
        final boolean isLead = checkRole(user, applicationId, organisationId, LEADAPPLICANT, roleRepository, processRoleRepository);
        final boolean isCollaborator = checkRole(user, applicationId, organisationId, COLLABORATOR, roleRepository, processRoleRepository);
        return isLead || isCollaborator;
    }

}