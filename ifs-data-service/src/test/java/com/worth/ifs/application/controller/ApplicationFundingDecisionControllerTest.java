package com.worth.ifs.application.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import com.worth.ifs.BaseControllerMockMVCTest;
import com.worth.ifs.application.resource.FundingDecision;
import org.junit.Test;
import org.springframework.http.MediaType;

import java.util.Map;

import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ApplicationFundingDecisionControllerTest extends BaseControllerMockMVCTest<ApplicationFundingDecisionController> {

    @Override
    protected ApplicationFundingDecisionController supplyControllerUnderTest() {
        return new ApplicationFundingDecisionController();
    }

    @Test
    public void applicationFundingDecisionControllerShouldReturnAppropriateStatusCode() throws Exception {
        Long competitionId = 1L;
        Map<Long, FundingDecision> decision = ImmutableMap.of(1L, FundingDecision.FUNDED, 2L, FundingDecision.NOT_FUNDED);

        when(applicationFundingService.makeFundingDecision(competitionId, decision)).thenReturn(serviceSuccess());
        when(applicationFundingService.notifyLeadApplicantsOfFundingDecisions(competitionId, decision)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/applicationfunding/1")
        			.contentType(MediaType.APPLICATION_JSON)
        			.content(new ObjectMapper().writeValueAsString(decision)))
                .andExpect(status().isOk())
                .andExpect(content().string(""));
    }

}
