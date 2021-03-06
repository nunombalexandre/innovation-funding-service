package org.innovateuk.ifs.form.security;


import org.innovateuk.ifs.BasePermissionRulesTest;
import org.innovateuk.ifs.application.builder.QuestionBuilder;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.domain.Question;
import org.innovateuk.ifs.form.builder.FormInputBuilder;
import org.innovateuk.ifs.form.domain.FormInput;
import org.innovateuk.ifs.form.resource.FormInputResponseResource;
import org.innovateuk.ifs.user.builder.UserResourceBuilder;
import org.innovateuk.ifs.user.domain.Organisation;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;

import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.form.builder.FormInputResponseResourceBuilder.newFormInputResponseResource;
import static org.innovateuk.ifs.user.builder.OrganisationBuilder.newOrganisation;
import static org.innovateuk.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static org.innovateuk.ifs.user.resource.UserRoleType.COLLABORATOR;
import static org.innovateuk.ifs.user.resource.UserRoleType.LEADAPPLICANT;
import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class FormInputResponsePermissionRulesTest extends BasePermissionRulesTest<FormInputResponsePermissionRules> {

    private FormInputResponseResource sharedInputResponse;
    private FormInputResponseResource formInputResponseUpdatedByLead;
    private FormInputResponseResource formInputResponseUpdatedByCollaborator;
    private UserResource leadApplicantForApplicationOnOrganisation1;
    private ProcessRole processRoleForLeadOnApplicationOnOrganisation1;
    private ProcessRole processRoleForCollaboratorOnApplicationOnOrganisation2;
    private UserResource collaboratorForApplicationOnOrganisation2;
    private Organisation organisation1;
    private Organisation organisation2;
    private Application application;
    private UserResource userNotOnApplication;

    @Override
    protected FormInputResponsePermissionRules supplyPermissionRulesUnderTest() {
        return new FormInputResponsePermissionRules();
    }

    @Before
    public void setup() throws Exception {

        application = newApplication().build();
        organisation1 = newOrganisation().build();
        organisation2 = newOrganisation().build();

        // Set up a lead applicant who has answered a question.
        leadApplicantForApplicationOnOrganisation1 = UserResourceBuilder.newUserResource().build();
        processRoleForLeadOnApplicationOnOrganisation1 = newProcessRole().withApplication(application).withOrganisationId(organisation1.getId()).build();
        formInputResponseUpdatedByLead = newFormInputResponseResource().withUpdatedBy(processRoleForLeadOnApplicationOnOrganisation1.getId()).withApplication(application.getId()).build();
        when(processRoleRepositoryMock.findOne(processRoleForLeadOnApplicationOnOrganisation1.getId())).thenReturn(processRoleForLeadOnApplicationOnOrganisation1);
        when(processRoleRepositoryMock.findByUserIdAndRoleIdAndApplicationIdAndOrganisationId(leadApplicantForApplicationOnOrganisation1.getId(), getRole(LEADAPPLICANT).getId(), application.getId(), organisation1.getId())).thenReturn(newProcessRole().build());

        // Set up a collaborator who has answered a question
        collaboratorForApplicationOnOrganisation2 = UserResourceBuilder.newUserResource().build();
        processRoleForCollaboratorOnApplicationOnOrganisation2 = newProcessRole().withApplication(application).withOrganisationId(organisation2.getId()).build();
        formInputResponseUpdatedByCollaborator = newFormInputResponseResource().withUpdatedBy(processRoleForCollaboratorOnApplicationOnOrganisation2.getId()).withApplication(application.getId()).build();
        when(processRoleRepositoryMock.findOne(processRoleForCollaboratorOnApplicationOnOrganisation2.getId())).thenReturn(processRoleForCollaboratorOnApplicationOnOrganisation2);
        when(processRoleRepositoryMock.findByUserIdAndRoleIdAndApplicationIdAndOrganisationId(collaboratorForApplicationOnOrganisation2.getId(), getRole(COLLABORATOR).getId(), application.getId(), organisation2.getId())).thenReturn(newProcessRole().build());

        // Set up a question to which both lead applicant and collaborator should be able to see.
        final Question question = QuestionBuilder.newQuestion().withMultipleStatuses(false).build();
        final FormInput formInput = FormInputBuilder.newFormInput().withQuestion(question).build();
        sharedInputResponse = newFormInputResponseResource().withApplication(application.getId()).build();
        when(formInputRepositoryMock.findOne(sharedInputResponse.getFormInput())).thenReturn(formInput);
        when(processRoleRepositoryMock.findByUserIdAndApplicationId(leadApplicantForApplicationOnOrganisation1.getId(), application.getId())).thenReturn(newProcessRole().withRole(getRole(LEADAPPLICANT)).build());
        when(processRoleRepositoryMock.findByUserIdAndApplicationId(collaboratorForApplicationOnOrganisation2.getId(), application.getId())).thenReturn(newProcessRole().withRole(getRole(COLLABORATOR)).build());

        userNotOnApplication = UserResourceBuilder.newUserResource().build();
    }


    @Test
    public void testConsortiumCanSeeTheInputResponsesForTheirOrganisationAndApplication() {
        assertTrue(rules.consortiumCanSeeTheInputResponsesForTheirOrganisationAndApplication(formInputResponseUpdatedByLead, leadApplicantForApplicationOnOrganisation1));
        assertTrue(rules.consortiumCanSeeTheInputResponsesForTheirOrganisationAndApplication(formInputResponseUpdatedByCollaborator, collaboratorForApplicationOnOrganisation2));
        assertFalse(rules.consortiumCanSeeTheInputResponsesForTheirOrganisationAndApplication(formInputResponseUpdatedByLead, collaboratorForApplicationOnOrganisation2));
        assertFalse(rules.consortiumCanSeeTheInputResponsesForTheirOrganisationAndApplication(formInputResponseUpdatedByCollaborator, leadApplicantForApplicationOnOrganisation1));
    }

    @Test
    public void testConsortiumCanSeeTheInputResponsesForApplicationWhenSharedBetweenOrganisations() {
        assertTrue(rules.consortiumCanSeeTheInputResponsesForApplicationWhenSharedBetweenOrganisations(sharedInputResponse, leadApplicantForApplicationOnOrganisation1));
        assertTrue(rules.consortiumCanSeeTheInputResponsesForApplicationWhenSharedBetweenOrganisations(sharedInputResponse, collaboratorForApplicationOnOrganisation2));
        assertFalse(rules.consortiumCanSeeTheInputResponsesForApplicationWhenSharedBetweenOrganisations(sharedInputResponse, userNotOnApplication));
    }

    @Test
    public void testInternalUserCanSeeFormInputResponsesForApplications() {
        assertTrue(rules.internalUserCanSeeFormInputResponsesForApplications(sharedInputResponse, compAdminUser()));
        assertTrue(rules.internalUserCanSeeFormInputResponsesForApplications(sharedInputResponse, projectFinanceUser()));
        assertFalse(rules.internalUserCanSeeFormInputResponsesForApplications(sharedInputResponse, leadApplicantForApplicationOnOrganisation1));
    }

}

