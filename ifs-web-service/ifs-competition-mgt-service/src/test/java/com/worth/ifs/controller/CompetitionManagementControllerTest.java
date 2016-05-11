
package com.worth.ifs.controller;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.worth.ifs.application.resource.ApplicationSummaryPageResource;
import com.worth.ifs.application.resource.CompetitionSummaryResource;
import com.worth.ifs.application.service.ApplicationSummaryService;
import com.worth.ifs.application.service.CompetitionService;
import com.worth.ifs.competition.resource.CompetitionResource.Status;
import com.worth.ifs.controller.CompetitionManagementController;
import com.worth.ifs.service.ApplicationSummarySortFieldService;

@RunWith(MockitoJUnitRunner.class)
public class CompetitionManagementControllerTest  {

    public static final Long COMPETITION_ID = Long.valueOf(123L);
    
    @InjectMocks
	private CompetitionManagementController controller;
	
    @Mock
    private ApplicationSummaryService applicationSummaryService;
    
    @Mock
    private CompetitionService competitionService;

    @Mock
    private ApplicationSummarySortFieldService applicationSummarySortFieldService;
    
    private MockMvc mockMvc;
    
    @Before
    public void setupMockMvc() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }
    
    @Test
    public void getByCompetitionIdForOpenCompetition() throws Exception {
    	
    	
        CompetitionSummaryResource competitionSummaryResource = new CompetitionSummaryResource();
        competitionSummaryResource.setCompetitionStatus(Status.OPEN);
        when(applicationSummaryService.getCompetitionSummaryByCompetitionId(COMPETITION_ID)).thenReturn(competitionSummaryResource);

        when(applicationSummarySortFieldService.sortFieldForOpenCompetition(null)).thenReturn("sortfield");
        
        ApplicationSummaryPageResource resource = new ApplicationSummaryPageResource();
    	when(applicationSummaryService.findByCompetitionId(COMPETITION_ID, "sortfield", 0, 20)).thenReturn(resource);

    	mockMvc.perform(get("/competition/123"))
                .andExpect(status().isOk())
                .andExpect(view().name("comp-mgt-open"))
                .andExpect(model().attribute("competitionSummary", competitionSummaryResource))
                .andExpect(model().attribute("results", resource))
                .andExpect(model().attribute("activeSortField", "sortfield"))
                .andExpect(model().attribute("activeTab", "allApplications"));
    	
    	verify(applicationSummaryService).findByCompetitionId(COMPETITION_ID, "sortfield", 0, 20);
    	verify(applicationSummaryService).getCompetitionSummaryByCompetitionId(COMPETITION_ID);
    }
    
    @Test
    public void getByCompetitionIdForOpenCompetitionProvidingPage() throws Exception {
    	
        CompetitionSummaryResource competitionSummaryResource = new CompetitionSummaryResource();
        competitionSummaryResource.setCompetitionStatus(Status.OPEN);
        when(applicationSummaryService.getCompetitionSummaryByCompetitionId(COMPETITION_ID)).thenReturn(competitionSummaryResource);

        when(applicationSummarySortFieldService.sortFieldForOpenCompetition(null)).thenReturn("sortfield");
        
    	ApplicationSummaryPageResource resource = new ApplicationSummaryPageResource();
    	when(applicationSummaryService.findByCompetitionId(COMPETITION_ID, "sortfield", 2, 20)).thenReturn(resource);

    	mockMvc.perform(get("/competition/123?page=3"))
                .andExpect(status().isOk())
                .andExpect(view().name("comp-mgt-open"))
                .andExpect(model().attribute("competitionSummary", competitionSummaryResource))
                .andExpect(model().attribute("results", resource))
                .andExpect(model().attribute("activeSortField", "sortfield"))
                .andExpect(model().attribute("activeTab", "allApplications"));
    	
    	verify(applicationSummaryService).findByCompetitionId(COMPETITION_ID, "sortfield", 2, 20);
    	verify(applicationSummaryService).getCompetitionSummaryByCompetitionId(COMPETITION_ID);
    }
    
    @Test
    public void getByCompetitionIdProvidingSort() throws Exception {
    	
        CompetitionSummaryResource competitionSummaryResource = new CompetitionSummaryResource();
        competitionSummaryResource.setCompetitionStatus(Status.OPEN);
        when(applicationSummaryService.getCompetitionSummaryByCompetitionId(COMPETITION_ID)).thenReturn(competitionSummaryResource);

        when(applicationSummarySortFieldService.sortFieldForOpenCompetition("lead")).thenReturn("properSort");

    	ApplicationSummaryPageResource resource = new ApplicationSummaryPageResource();
    	when(applicationSummaryService.findByCompetitionId(COMPETITION_ID, "properSort", 0, 20)).thenReturn(resource);

    	mockMvc.perform(get("/competition/123?sort=lead"))
                .andExpect(status().isOk())
                .andExpect(view().name("comp-mgt-open"))
                .andExpect(model().attribute("competitionSummary", competitionSummaryResource))
                .andExpect(model().attribute("results", resource))
                .andExpect(model().attribute("activeSortField", "properSort"))
                .andExpect(model().attribute("activeTab", "allApplications"));
    	
    	verify(applicationSummaryService).findByCompetitionId(COMPETITION_ID, "properSort", 0, 20);
    	verify(applicationSummaryService).getCompetitionSummaryByCompetitionId(COMPETITION_ID);
    }
    
    @Test
    public void getByCompetitionIdForCompetitionInAssessmentSubmittedIsDefault() throws Exception {
    	
    	CompetitionSummaryResource competitionSummaryResource = new CompetitionSummaryResource();
    	competitionSummaryResource.setCompetitionStatus(Status.IN_ASSESSMENT);
        when(applicationSummaryService.getCompetitionSummaryByCompetitionId(COMPETITION_ID)).thenReturn(competitionSummaryResource);

        when(applicationSummarySortFieldService.sortFieldForSubmittedApplications(null)).thenReturn("sortfield");

        ApplicationSummaryPageResource summary = new ApplicationSummaryPageResource();
        when(applicationSummaryService.getSubmittedApplicationSummariesByCompetitionId(COMPETITION_ID, "sortfield", 0, 20)).thenReturn(summary);

    	mockMvc.perform(get("/competition/123"))
                .andExpect(status().isOk())
                .andExpect(view().name("comp-mgt-in-assessment"))
                .andExpect(model().attribute("competitionSummary", competitionSummaryResource))
                .andExpect(model().attribute("results", summary))
                .andExpect(model().attribute("activeTab", "submitted"))
    			.andExpect(model().attribute("activeSortField", "sortfield"));
    	verify(applicationSummaryService).getSubmittedApplicationSummariesByCompetitionId(COMPETITION_ID, "sortfield", 0, 20);
    	verify(applicationSummaryService).getCompetitionSummaryByCompetitionId(COMPETITION_ID);
    }
    
    @Test
    public void getByCompetitionIdForCompetitionInAssessmentSubmittedRequested() throws Exception {
    	
    	CompetitionSummaryResource competitionSummaryResource = new CompetitionSummaryResource();
    	competitionSummaryResource.setCompetitionStatus(Status.IN_ASSESSMENT);
        when(applicationSummaryService.getCompetitionSummaryByCompetitionId(COMPETITION_ID)).thenReturn(competitionSummaryResource);

        when(applicationSummarySortFieldService.sortFieldForSubmittedApplications(null)).thenReturn("sortfield");

    	ApplicationSummaryPageResource summary = new ApplicationSummaryPageResource();
        when(applicationSummaryService.getSubmittedApplicationSummariesByCompetitionId(COMPETITION_ID, "sortfield", 0, 20)).thenReturn(summary);

    	mockMvc.perform(get("/competition/123?tab=submitted"))
                .andExpect(status().isOk())
                .andExpect(view().name("comp-mgt-in-assessment"))
                .andExpect(model().attribute("competitionSummary", competitionSummaryResource))
                .andExpect(model().attribute("results", summary))
                .andExpect(model().attribute("activeTab", "submitted"))
    			.andExpect(model().attribute("activeSortField", "sortfield"));
    	verify(applicationSummaryService).getSubmittedApplicationSummariesByCompetitionId(COMPETITION_ID, "sortfield", 0, 20);
    	verify(applicationSummaryService).getCompetitionSummaryByCompetitionId(COMPETITION_ID);
    }
    
    @Test
    public void getByCompetitionIdForCompetitionInAssessmentNotSubmittedRequested() throws Exception {
    	
    	CompetitionSummaryResource competitionSummaryResource = new CompetitionSummaryResource();
    	competitionSummaryResource.setCompetitionStatus(Status.IN_ASSESSMENT);
        when(applicationSummaryService.getCompetitionSummaryByCompetitionId(COMPETITION_ID)).thenReturn(competitionSummaryResource);

        when(applicationSummarySortFieldService.sortFieldForNotSubmittedApplications(null)).thenReturn("sortfield");

    	ApplicationSummaryPageResource summary = new ApplicationSummaryPageResource();
        when(applicationSummaryService.getNotSubmittedApplicationSummariesByCompetitionId(COMPETITION_ID, "sortfield", 0, 20)).thenReturn(summary);

    	mockMvc.perform(get("/competition/123?tab=notSubmitted"))
                .andExpect(status().isOk())
                .andExpect(view().name("comp-mgt-in-assessment"))
                .andExpect(model().attribute("competitionSummary", competitionSummaryResource))
                .andExpect(model().attribute("results", summary))
                .andExpect(model().attribute("activeTab", "notSubmitted"))
    			.andExpect(model().attribute("activeSortField", "sortfield"));
    	
    	verify(applicationSummaryService).getNotSubmittedApplicationSummariesByCompetitionId(COMPETITION_ID, "sortfield", 0, 20);
    	verify(applicationSummaryService).getCompetitionSummaryByCompetitionId(COMPETITION_ID);
    }
    
    @Test
    public void getByCompetitionIdForCompetitionFundersPanelNotSubmittedRequested() throws Exception {
    	
    	CompetitionSummaryResource competitionSummaryResource = new CompetitionSummaryResource();
    	competitionSummaryResource.setCompetitionStatus(Status.FUNDERS_PANEL);
        when(applicationSummaryService.getCompetitionSummaryByCompetitionId(COMPETITION_ID)).thenReturn(competitionSummaryResource);

        when(applicationSummarySortFieldService.sortFieldForNotSubmittedApplications(null)).thenReturn("sortfield");

    	ApplicationSummaryPageResource summary = new ApplicationSummaryPageResource();
        when(applicationSummaryService.getNotSubmittedApplicationSummariesByCompetitionId(COMPETITION_ID, "sortfield", 0, 20)).thenReturn(summary);

    	mockMvc.perform(get("/competition/123?tab=notSubmitted"))
                .andExpect(status().isOk())
                .andExpect(view().name("comp-mgt-funders-panel"))
                .andExpect(model().attribute("competitionSummary", competitionSummaryResource))
                .andExpect(model().attribute("results", summary))
                .andExpect(model().attribute("activeTab", "notSubmitted"))
    			.andExpect(model().attribute("activeSortField", "sortfield"));
    	
    	verify(applicationSummaryService).getNotSubmittedApplicationSummariesByCompetitionId(COMPETITION_ID, "sortfield", 0, 20);
    	verify(applicationSummaryService).getCompetitionSummaryByCompetitionId(COMPETITION_ID);
    }
    
    @Test
    public void getByCompetitionIdForCompetitionFundersPanelSubmittedRequested() throws Exception {
    	
    	CompetitionSummaryResource competitionSummaryResource = new CompetitionSummaryResource();
    	competitionSummaryResource.setCompetitionStatus(Status.FUNDERS_PANEL);
        when(applicationSummaryService.getCompetitionSummaryByCompetitionId(COMPETITION_ID)).thenReturn(competitionSummaryResource);

        when(applicationSummarySortFieldService.sortFieldForSubmittedApplications(null)).thenReturn("sortfield");

    	ApplicationSummaryPageResource summary = new ApplicationSummaryPageResource();
        when(applicationSummaryService.getSubmittedApplicationSummariesByCompetitionId(COMPETITION_ID, "sortfield", 0, Integer.MAX_VALUE)).thenReturn(summary);

    	mockMvc.perform(get("/competition/123?tab=submitted"))
                .andExpect(status().isOk())
                .andExpect(view().name("comp-mgt-funders-panel"))
                .andExpect(model().attribute("competitionSummary", competitionSummaryResource))
                .andExpect(model().attribute("results", summary))
                .andExpect(model().attribute("activeTab", "submitted"))
    			.andExpect(model().attribute("activeSortField", "sortfield"));
    	
    	verify(applicationSummaryService).getSubmittedApplicationSummariesByCompetitionId(COMPETITION_ID, "sortfield", 0, Integer.MAX_VALUE);
    	verify(applicationSummaryService).getCompetitionSummaryByCompetitionId(COMPETITION_ID);
    }
    
    
    @Test
    public void getByCompetitionIdProvidingInvalidPage() throws Exception {
    	
    	mockMvc.perform(get("/competition/123?page=0"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/competition/123"));
    	
    	verifyNoMoreInteractions(applicationSummaryService);
    }
}