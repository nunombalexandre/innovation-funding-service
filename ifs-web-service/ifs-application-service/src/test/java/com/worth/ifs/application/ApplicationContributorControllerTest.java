package com.worth.ifs.application;

import static com.worth.ifs.commons.rest.RestResult.restSuccess;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.Arrays;

import javax.servlet.http.Cookie;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.validation.Validator;

import com.worth.ifs.BaseControllerMockMVCTest;
import com.worth.ifs.application.form.ContributorsForm;
import com.worth.ifs.filter.CookieFlashMessageFilter;
import com.worth.ifs.invite.resource.InviteOrganisationResource;
import com.worth.ifs.invite.resource.InviteResource;
import com.worth.ifs.user.domain.User;

@RunWith(MockitoJUnitRunner.class)
@TestPropertySource(locations = "classpath:application.properties")
public class ApplicationContributorControllerTest extends BaseControllerMockMVCTest<ApplicationContributorController> {

    @Mock
    private Validator validator;
    @Mock
    CookieFlashMessageFilter cookieFlashMessageFilter;

    private Long applicationId;
    private Long alternativeApplicationId;
    private String redirectUrl;
    private String viewName;
    private String inviteUrl;
    private String applicationRedirectUrl;
    private String inviteOverviewRedirectUrl;


    @Override
    protected ApplicationContributorController supplyControllerUnderTest() {
        return new ApplicationContributorController();
    }

    @Before
    public void setUp() {
        super.setUp();

        this.setupCompetition();
        this.setupApplicationWithRoles();
        this.setupApplicationResponses();
        this.loginDefaultUser();
        this.setupFinances();
        this.setupInvites();

        applicationId = applications.get(0).getId();
        alternativeApplicationId = applicationId + 1;
        inviteUrl = String.format("/application/%d/contributors/invite", applicationId);
        redirectUrl = String.format("redirect:/application/%d/contributors/invite", applicationId);
        applicationRedirectUrl = String.format("redirect:/application/%d", applicationId);
        inviteOverviewRedirectUrl = String.format("redirect:/application/%d/contributors", applicationId);
        viewName = "application-contributors/invite";
    }

    @Test
    public void testInviteContributors() throws Exception {
        mockMvc.perform(get(inviteUrl))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name(viewName));
    }

    @Test
    public void testInviteContributorsCookie() throws Exception {
        Cookie cookie = new Cookie("contributor_invite_state", "{\"applicationId\":1,\"organisations\":[{\"organisationName\":\"Empire Ltd\",\"organisationId\":3,\"organisationInviteId\":null,\"invites\":[{\"userId\":null,\"personName\":\"Nico Bijl\",\"email\":\"nico@worth.systems\",\"inviteStatus\":null}]}]}}");

//        contributorsForm.getOrganisationMap()
        MvcResult mockResult = mockMvc.perform(get(inviteUrl).cookie(cookie))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name(viewName))
                .andExpect(model().attributeExists("leadOrganisation", "leadApplicant", "contributorsForm"))
                .andReturn();

        ContributorsForm contributorsFormResult = (ContributorsForm) mockResult.getModelAndView().getModelMap().get("contributorsForm");
        assertNotNull(contributorsFormResult.getOrganisations().get(0));
        assertEquals(1, contributorsFormResult.getOrganisations().get(0).getInvites().size());
        assertEquals("Nico Bijl", contributorsFormResult.getOrganisations().get(0).getInvites().get(0).getPersonName());
        assertEquals("nico@worth.systems", contributorsFormResult.getOrganisations().get(0).getInvites().get(0).getEmail());
    }

    @Test
    public void testInviteContributorsPostAddPerson() throws Exception {
        mockMvc.perform(post(inviteUrl)
                .param("organisations[0].organisationName", "Empire Ltd")
                .param("organisations[0].organisationId", "1")
                .param("add_person", "0"))
                .andExpect(status().is3xxRedirection())
                .andExpect(cookie().exists("contributor_invite_state"))
                .andExpect(cookie().value("contributor_invite_state", "{\"triedToSave\":false,\"applicationId\":1,\"organisations\":[{\"organisationName\":\"Empire Ltd\",\"organisationNameConfirmed\":null,\"organisationId\":1,\"organisationInviteId\":null,\"invites\":[{\"userId\":null,\"personName\":\"\",\"email\":\"\",\"inviteStatus\":null}]}]}"))
                .andExpect(view().name(redirectUrl));
    }

    @Test
    public void testInviteContributorsPostPerson() throws Exception {
        mockMvc.perform(
                post(inviteUrl)
                        .param("organisations[0].organisationName", "Empire Ltd")
                        .param("organisations[0].organisationId", "1")
                        .param("organisations[0].invites[0].personName", "Nico Bijl")
                        .param("organisations[0].invites[0].email", "nico@worth.systems")
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(cookie().exists("contributor_invite_state"))
                .andExpect(cookie().value("contributor_invite_state", "{\"triedToSave\":false,\"applicationId\":1,\"organisations\":[{\"organisationName\":\"Empire Ltd\",\"organisationNameConfirmed\":null,\"organisationId\":1,\"organisationInviteId\":null,\"invites\":[{\"userId\":null,\"personName\":\"Nico Bijl\",\"email\":\"nico@worth.systems\",\"inviteStatus\":null}]}]}"))
                .andExpect(view().name(redirectUrl));
    }

    @Test
    public void testInviteContributorsPostInvalidPerson() throws Exception {
        mockMvc.perform(
                post(inviteUrl)
                        .param("organisations[0].organisationName", "Empire Ltd")
                        .param("organisations[0].organisationId", "1")
                        .param("organisations[0].organisationInviteId", "")
                        .param("organisations[0].invites[0].personName", "Nico Bijl")
                        .param("organisations[0].invites[0].email", "")
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(cookie().exists("contributor_invite_state"))
                .andExpect(cookie().value("contributor_invite_state", "{\"triedToSave\":false,\"applicationId\":1,\"organisations\":[{\"organisationName\":\"Empire Ltd\",\"organisationNameConfirmed\":null,\"organisationId\":1,\"organisationInviteId\":null,\"invites\":[{\"userId\":null,\"personName\":\"Nico Bijl\",\"email\":\"\",\"inviteStatus\":null}]}]}"))
                .andExpect(view().name(redirectUrl));
    }

    @Test
    public void testInviteContributorsBeginApplication() throws Exception {
        mockMvc.perform(
                post(inviteUrl)
                        .param("organisations[0].organisationName", "Empire Ltd")
                        .param("organisations[0].organisationId", "1")
                        .param("organisations[0].invites[0].personName", "Nico Bijl")
                        .param("organisations[0].invites[0].email", "nico@gmail.com")
                        .param("save_contributors", "")
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(cookie().exists("contributor_invite_state"))
                .andExpect(cookie().value("contributor_invite_state", ""))
                .andExpect(view().name(inviteOverviewRedirectUrl));


        mockMvc.perform(
                post(inviteUrl+"")
                        .param("newApplication", "Empire Ltd")
                        .param("organisations[0].organisationName", "Empire Ltd")
                        .param("organisations[0].organisationId", "1")
                        .param("organisations[0].invites[0].personName", "Nico Bijl")
                        .param("organisations[0].invites[0].email", "nico@gmail.com")
                        .param("save_contributors", "")
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(cookie().exists("contributor_invite_state"))
                .andExpect(cookie().value("contributor_invite_state", ""))
                .andExpect(view().name(applicationRedirectUrl));
    }
    
    @Test
    public void testLeadCanInviteToOtherOrganisation() throws Exception {
        mockMvc.perform(
                post(inviteUrl)
                        .param("organisations[0].organisationName", "Empire Ltd")
                        .param("organisations[0].organisationId", "1")
                        .param("organisations[1].organisationName", "Some Other Org Ltd")
                        .param("organisations[1].organisationId", "2")
                        .param("organisations[1].invites[0].personName", "Jim Kirk")
                        .param("organisations[1].invites[0].email", "j.kirk@starfleet.org")
                        .param("save_contributors", "")
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(cookie().exists("contributor_invite_state"))
                .andExpect(cookie().value("contributor_invite_state", ""))
                .andExpect(view().name(inviteOverviewRedirectUrl));
    }
    
    @Test
    public void testNonLeadCannotInviteToOtherOrganisation() throws Exception {
    	
    	this.setupUserInvite("user@email.com", 3L);
    	this.loginNonLeadUser("user@email.com");
    	
        mockMvc.perform(
                post(inviteUrl)
                        .param("organisations[0].organisationName", "Empire Ltd")
                        .param("organisations[0].organisationId", "1")
                        .param("organisations[1].organisationName", "Some Other Org Ltd")
                        .param("organisations[1].organisationId", "2")
                        .param("organisations[1].invites[0].personName", "Jim Kirk")
                        .param("organisations[1].invites[0].email", "j.kirk@starfleet.org")
                        .param("save_contributors", "")
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(cookie().exists("contributor_invite_state"))
                .andExpect(cookie().value("contributor_invite_state", "{\"triedToSave\":true,\"applicationId\":1,\"organisations\":[{\"organisationName\":\"Empire Ltd\",\"organisationNameConfirmed\":null,\"organisationId\":1,\"organisationInviteId\":null,\"invites\":[]},{\"organisationName\":\"Some Other Org Ltd\",\"organisationNameConfirmed\":null,\"organisationId\":2,\"organisationInviteId\":null,\"invites\":[{\"userId\":null,\"personName\":\"Jim Kirk\",\"email\":\"j.kirk@starfleet.org\",\"inviteStatus\":null}]}]}"))
                .andExpect(view().name(redirectUrl));
    }
    


	@Test
    public void testNonLeadCanInviteToTheirOwnOrganisation() throws Exception {
    	
		this.setupUserInvite("user@email.com", 2L);
		this.loginNonLeadUser("user@email.com");
    	
        mockMvc.perform(
                post(inviteUrl)
                        .param("organisations[0].organisationName", "Empire Ltd")
                        .param("organisations[0].organisationId", "1")
                        .param("organisations[1].organisationName", "Some Other Org Ltd")
                        .param("organisations[1].organisationId", "2")
                        .param("organisations[1].invites[0].personName", "Jim Kirk")
                        .param("organisations[1].invites[0].email", "j.kirk@starfleet.org")
                        .param("save_contributors", "")
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(cookie().exists("contributor_invite_state"))
                .andExpect(cookie().value("contributor_invite_state", ""))
                .andExpect(view().name(inviteOverviewRedirectUrl));
    }

    
    
    @Test
    public void testInviteContributorsRemovePerson() throws Exception {
        mockMvc.perform(
                post(inviteUrl)
                        .param("organisations[0].organisationName", "Empire Ltd")
                        .param("organisations[0].organisationId", "1")
                        .param("organisations[0].invites[0].personName", "Nico Bijl")
                        .param("organisations[0].invites[0].email", "nico@worth.systems")
                        .param("organisations[0].invites[1].personName", "Brent de Kok")
                        .param("organisations[0].invites[1].email", "brent@worth.systems")
                        .param("remove_person", "0_1")
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(cookie().exists("contributor_invite_state"))
                .andExpect(cookie().value("contributor_invite_state", "{\"triedToSave\":false,\"applicationId\":1,\"organisations\":[{\"organisationName\":\"Empire Ltd\",\"organisationNameConfirmed\":null,\"organisationId\":1,\"organisationInviteId\":null,\"invites\":[{\"userId\":null,\"personName\":\"Nico Bijl\",\"email\":\"nico@worth.systems\",\"inviteStatus\":null}]}]}"))
                .andExpect(view().name(redirectUrl));
    }

    @Test
    public void testInviteContributorsRemovePerson2() throws Exception {
        mockMvc.perform(
                post(inviteUrl)
                        .param("organisations[0].organisationName", "Empire Ltd")
                        .param("organisations[0].organisationId", "1")
                        .param("organisations[0].invites[0].personName", "Nico Bijl")
                        .param("organisations[0].invites[0].email", "nico@worth.systems")
                        .param("organisations[0].invites[1].personName", "Brent de Kok")
                        .param("organisations[0].invites[1].email", "brent@worth.systems")
                        .param("remove_person", "0_0")
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(cookie().exists("contributor_invite_state"))
                .andExpect(cookie().value("contributor_invite_state", "{\"triedToSave\":false,\"applicationId\":1,\"organisations\":[{\"organisationName\":\"Empire Ltd\",\"organisationNameConfirmed\":null,\"organisationId\":1,\"organisationInviteId\":null,\"invites\":[{\"userId\":null,\"personName\":\"Brent de Kok\",\"email\":\"brent@worth.systems\",\"inviteStatus\":null}]}]}"))
                .andExpect(view().name(redirectUrl));


    }

    /**
     * When user adds a partner organisation, it should just add a empty person row, so the user can fill in directly.
     */
    @Test
    public void testInviteContributorsPostAddPartner() throws Exception {

        mockMvc.perform(post(inviteUrl)
                .param("organisations[0].organisationName", "Empire Ltd")
                .param("organisations[0].organisationId", "1")
                .param("organisations[0].invites[0].personName", "Nico Bijl")
                .param("organisations[0].invites[0].email", "nico@worth.systems")
                .param("organisations[0].invites[1].personName", "Brent de Kok")
                .param("organisations[0].invites[1].email", "brent@worth.systems")
                .param("add_partner", ""))
                .andExpect(status().is3xxRedirection())
                .andExpect(cookie().exists("contributor_invite_state"))
                .andExpect(cookie().value("contributor_invite_state", "{\"triedToSave\":false,\"applicationId\":1,\"organisations\":[{\"organisationName\":\"Empire Ltd\",\"organisationNameConfirmed\":null,\"organisationId\":1,\"organisationInviteId\":null,\"invites\":[{\"userId\":null,\"personName\":\"Nico Bijl\",\"email\":\"nico@worth.systems\",\"inviteStatus\":null},{\"userId\":null,\"personName\":\"Brent de Kok\",\"email\":\"brent@worth.systems\",\"inviteStatus\":null}]},{\"organisationName\":\"\",\"organisationNameConfirmed\":null,\"organisationId\":null,\"organisationInviteId\":null,\"invites\":[{\"userId\":null,\"personName\":\"\",\"email\":\"\",\"inviteStatus\":null}]}]}"))
                .andExpect(view().name(redirectUrl));
    }


    /**
     * When the last person is removed from a partner organisation, also remove the organisation.
     * @throws Exception
     */
    @Test
    public void testInviteContributorsPostRemovePersonAndPartner() throws Exception {
        mockMvc.perform(post(inviteUrl)
                .param("organisations[0].organisationName", "Empire Ltd")
                .param("organisations[0].organisationId", "1")
                .param("organisations[0].invites[0].personName", "Nico Bijl")
                .param("organisations[0].invites[0].email", "nico@worth.systems")
                .param("organisations[0].invites[1].personName", "Brent de Kok")
                .param("organisations[0].invites[1].email", "brent@worth.systems")
                .param("organisations[1].organisationName", "SomePartner")
                .param("organisations[1].invites[0].personName", "Nico Bijl")
                .param("organisations[1].invites[0].email", "nico@worth.systems")
                .param("remove_person", "1_0"))
                .andExpect(status().is3xxRedirection())
                .andExpect(cookie().exists("contributor_invite_state"))
                .andExpect(cookie().value("contributor_invite_state", "{\"triedToSave\":false,\"applicationId\":1,\"organisations\":[{\"organisationName\":\"Empire Ltd\",\"organisationNameConfirmed\":null,\"organisationId\":1,\"organisationInviteId\":null,\"invites\":[{\"userId\":null,\"personName\":\"Nico Bijl\",\"email\":\"nico@worth.systems\",\"inviteStatus\":null},{\"userId\":null,\"personName\":\"Brent de Kok\",\"email\":\"brent@worth.systems\",\"inviteStatus\":null}]}]}"))
                .andExpect(view().name(redirectUrl));
    }

    @Test
    public void testInviteContributorsPostRemovePersonFromPartner() throws Exception {
        mockMvc.perform(post(inviteUrl)
                .param("organisations[0].organisationName", "Empire Ltd")
                .param("organisations[0].organisationId", "1")
                .param("organisations[0].invites[0].personName", "Nico Bijl")
                .param("organisations[0].invites[0].email", "nico@worth.systems")
                .param("organisations[0].invites[1].personName", "Brent de Kok")
                .param("organisations[0].invites[1].email", "brent@worth.systems")
                .param("organisations[1].organisationName", "SomePartner")
                .param("organisations[1].invites[0].personName", "Nico Bijl")
                .param("organisations[1].invites[0].email", "nico@worth.systems")
                .param("organisations[1].invites[1].personName", "Brent de Kok")
                .param("organisations[1].invites[1].email", "brent@worth.systems")
                .param("applicationId", applicationId.toString())
                .param("remove_person", "1_0"))
                .andExpect(status().is3xxRedirection())
                .andExpect(cookie().exists("contributor_invite_state"))
                .andExpect(cookie().value("contributor_invite_state", "{\"triedToSave\":false,\"applicationId\":1,\"organisations\":[{\"organisationName\":\"Empire Ltd\",\"organisationNameConfirmed\":null,\"organisationId\":1,\"organisationInviteId\":null,\"invites\":[{\"userId\":null,\"personName\":\"Nico Bijl\",\"email\":\"nico@worth.systems\",\"inviteStatus\":null},{\"userId\":null,\"personName\":\"Brent de Kok\",\"email\":\"brent@worth.systems\",\"inviteStatus\":null}]},{\"organisationName\":\"SomePartner\",\"organisationNameConfirmed\":null,\"organisationId\":null,\"organisationInviteId\":null,\"invites\":[{\"userId\":null,\"personName\":\"Brent de Kok\",\"email\":\"brent@worth.systems\",\"inviteStatus\":null}]}]}"))
                .andExpect(view().name(redirectUrl));
    }

    @Test
    public void whenCookieHasDifferingApplicationIdFromGetParameterItShouldBeIgnored() throws Exception {
        Cookie cookie = new Cookie("contributor_invite_state", "{\"applicationId\":"+alternativeApplicationId+",\"organisations\":[{\"organisationName\":\"Empire Ltd\",\"organisationNameConfirmed\":null,\"organisationId\":3,\"organisationInviteId\":null,\"invites\":[{\"userId\":null,\"personName\":\"Nico Bijl\",\"email\":\"nico@worth.systems\",\"inviteStatus\":null}]}]}}");

//        contributorsForm.getOrganisationMap()
        MvcResult mockResult = mockMvc.perform(get(inviteUrl).cookie(cookie))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name(viewName))
                .andExpect(model().attributeExists("leadOrganisation", "leadApplicant", "contributorsForm"))
                .andReturn();

        ContributorsForm contributorsFormResult = (ContributorsForm) mockResult.getModelAndView().getModelMap().get("contributorsForm");
        assertNotNull(contributorsFormResult.getOrganisations().get(0));
        assertEquals(0, contributorsFormResult.getOrganisations().get(0).getInvites().size());
    }
    
	private void loginNonLeadUser(String email) {
    	User user = new User(2L, "test", "name", email, null, null, null);
    	loginUser(user);
	}
	
    private void setupUserInvite(String email, Long organisationId) {
    	InviteOrganisationResource inviteOrgResource = new InviteOrganisationResource();
    	inviteOrgResource.setOrganisation(organisationId);
    	inviteOrgResource.setInviteResources(Arrays.asList(new InviteResource(null, null, email, null, null, null, null)));
    	when(inviteRestService.getInvitesByApplication(isA(Long.class))).thenReturn(restSuccess(Arrays.asList(inviteOrgResource)));
	}


}