package com.worth.ifs.application.controller;

import static com.worth.ifs.security.SecuritySetter.swapOutForUser;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;

import com.worth.ifs.BaseControllerIntegrationTest;
import com.worth.ifs.application.constant.ApplicationStatusConstants;
import com.worth.ifs.application.domain.Application;
import com.worth.ifs.application.domain.ApplicationStatus;
import com.worth.ifs.application.resource.ApplicationSummaryPageResource;
import com.worth.ifs.application.resource.ApplicationSummaryResource;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.user.domain.ProcessRole;
import com.worth.ifs.user.domain.Role;
import com.worth.ifs.user.domain.User;
import com.worth.ifs.user.domain.UserRoleType;

@Rollback
public class ApplicationSummaryControllerIntegrationTest extends BaseControllerIntegrationTest<ApplicationSummaryController> {

	public static final long APPLICATION_ID = 1L;
	public static final long COMPETITION_ID = 1L;
	
    private Long leadApplicantProcessRole;
    private Long leadApplicantId;
    
    private Long compAdminUserId;
    private Long compAdminRoleId;

    @Before
    public void setUp() throws Exception {
        leadApplicantId = 1L;
        leadApplicantProcessRole = 1L;
        List<ProcessRole> leadApplicantProccessRoles = new ArrayList<>();
        leadApplicantProccessRoles.add(
            new ProcessRole(
                leadApplicantProcessRole,
                null,
                new Application(
                    APPLICATION_ID,
                    "",
                    new ApplicationStatus(
                        ApplicationStatusConstants.CREATED.getId(),
                        ApplicationStatusConstants.CREATED.getName()
                    )
                ),
                null,
                null
            )
        );
        User user = new User(leadApplicantId, "steve", "smith", "steve.smith@empire.com", "", leadApplicantProccessRoles, "123abc");
        leadApplicantProccessRoles.get(0).setUser(user);
        
        compAdminUserId = 2L;
        compAdminRoleId = 2L;
        User compAdminUser = new User(compAdminUserId, "jim", "kirk", "j.kirk@starfleet.org", "", new ArrayList<>(), "123abc");
        Role compAdminRole = new Role(compAdminRoleId, UserRoleType.COMP_ADMIN.getName(), new ArrayList<>());
        compAdminUser.getRoles().add(compAdminRole);
        swapOutForUser(compAdminUser);
    }

    @After
    public void tearDown() throws Exception {
        swapOutForUser(null);
    }

    @Override
    @Autowired
    protected void setControllerUnderTest(ApplicationSummaryController controller) {
        this.controller = controller;
    }

    @Test
    public void testApplicationSummaryById() throws Exception {
        RestResult<ApplicationSummaryResource> result = controller.getApplicationSummaryById(APPLICATION_ID);
        
        assertTrue(result.isSuccess());
        assertEquals(Long.valueOf(APPLICATION_ID), result.getSuccessObject().getId());
        assertEquals(ApplicationStatusConstants.OPEN.getName(), result.getSuccessObject().getApplicationStatusName());
        assertEquals("A novel solution to an old problem", result.getSuccessObject().getName());
        assertEquals("Steve Smith", result.getSuccessObject().getLead());
        assertEquals(Integer.valueOf(36), result.getSuccessObject().getCompletedPercentage());
    }
    
    @Test
    public void testApplicationSummariesByCompetitionId() throws Exception {
        RestResult<ApplicationSummaryPageResource> result = controller.getApplicationSummaryByCompetitionId(COMPETITION_ID, 0, null);
        
        assertTrue(result.isSuccess());
        assertEquals(0, result.getSuccessObject().getNumber());
        assertEquals(20, result.getSuccessObject().getSize());
        assertEquals(6, result.getSuccessObject().getTotalElements());
        assertEquals(1, result.getSuccessObject().getTotalPages());
        assertEquals(Long.valueOf(APPLICATION_ID), result.getSuccessObject().getContent().get(0).getId());
        assertEquals(ApplicationStatusConstants.OPEN.getName(), result.getSuccessObject().getContent().get(0).getApplicationStatusName());
        assertEquals("A novel solution to an old problem", result.getSuccessObject().getContent().get(0).getName());
        assertEquals("Steve Smith", result.getSuccessObject().getContent().get(0).getLead());
        assertEquals(Integer.valueOf(36), result.getSuccessObject().getContent().get(0).getCompletedPercentage());
    }

}