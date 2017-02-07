package org.innovateuk.ifs.assessment.controller;

import org.apache.commons.lang3.RandomStringUtils;
import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.QuestionResource;
import org.innovateuk.ifs.assessment.form.AssessmentSummaryForm;
import org.innovateuk.ifs.assessment.model.AssessmentSummaryModelPopulator;
import org.innovateuk.ifs.assessment.resource.AssessorFormInputResponseResource;
import org.innovateuk.ifs.assessment.service.AssessmentService;
import org.innovateuk.ifs.assessment.service.AssessorFormInputResponseService;
import org.innovateuk.ifs.assessment.viewmodel.AssessmentSummaryQuestionViewModel;
import org.innovateuk.ifs.assessment.viewmodel.AssessmentSummaryViewModel;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.form.resource.FormInputResource;
import org.innovateuk.ifs.form.resource.FormInputType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.validation.BindingResult;

import java.util.List;

import static java.time.LocalDateTime.now;
import static java.util.Collections.emptyList;
import static java.util.Collections.nCopies;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Stream.concat;
import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.application.builder.QuestionResourceBuilder.newQuestionResource;
import static org.innovateuk.ifs.assessment.builder.AssessmentFundingDecisionOutcomeResourceBuilder.newAssessmentFundingDecisionOutcomeResource;
import static org.innovateuk.ifs.assessment.builder.AssessmentResourceBuilder.newAssessmentResource;
import static org.innovateuk.ifs.assessment.builder.AssessorFormInputResponseResourceBuilder.newAssessorFormInputResponseResource;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.id;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.form.builder.FormInputResourceBuilder.newFormInputResource;
import static org.innovateuk.ifs.util.CollectionFunctions.combineLists;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(MockitoJUnitRunner.class)
@TestPropertySource(locations = "classpath:application.properties")
public class AssessmentSummaryControllerTest extends BaseControllerMockMVCTest<AssessmentSummaryController> {
    @Mock
    private AssessmentService assessmentService;

    @Mock
    private AssessorFormInputResponseService assessorFormInputResponseService;

    @Spy
    @InjectMocks
    private AssessmentSummaryModelPopulator assessmentSummaryModelPopulator;

    @Override
    protected AssessmentSummaryController supplyControllerUnderTest() {
        return new AssessmentSummaryController();
    }

    private Long applicationId;
    private Long assessmentId;
    private Long competitionId;
    private ApplicationResource application;
    private CompetitionResource competition;

    @Override
    @Before
    public void setUp() {
        super.setUp();

        applicationId = 1L;
        assessmentId = 2L;
        competitionId = 3L;

        FormInputType anotherTypeOfFormInput = FormInputType.ASSESSOR_RESEARCH_CATEGORY;

        when(assessmentService.getById(assessmentId)).thenReturn(newAssessmentResource()
                .with(id(assessmentId))
                .withApplication(applicationId)
                .withCompetition(competitionId)
                .build());

        application = newApplicationResource()
                .with(id(applicationId))
                .withCompetition(competitionId)
                .build();
        when(applicationService.getById(applicationId)).thenReturn(application);

        competition = newCompetitionResource()
                .with(id(competitionId))
                .withAssessorAcceptsDate(now().minusDays(2))
                .withAssessorDeadlineDate(now().plusDays(4))
                .build();
        when(competitionService.getById(competitionId)).thenReturn(competition);

        // The first question will have no form inputs, therefore no assessment required and should not appear in the summary
        List<FormInputResource> formInputsForQuestion1 = emptyList();

        // The second question will have 'application in scope' type amongst the form inputs meaning that the AssessmentSummaryQuestionViewModel.applicationInScope should get populated with any response to this input
        List<FormInputResource> formInputsForQuestion2 = newFormInputResource()
                .withId(1L, 2L)
                .withType(anotherTypeOfFormInput, FormInputType.ASSESSOR_APPLICATION_IN_SCOPE)
                .withQuestion(2L, 2L)
                .build(2);

        // The third question will have 'feedback' and 'score' types amongst the form inputs meaning that the AssessmentSummaryQuestionViewModel.feedback and .scoreGiven should get populated with any response to this input
        List<FormInputResource> formInputsForQuestion3 = newFormInputResource()
                .withId(3L, 4L, 5L)
                .withType(anotherTypeOfFormInput, FormInputType.ASSESSOR_SCORE, FormInputType.TEXTAREA)
                .withQuestion(3L, 3L, 3L)
                .build(3);
        when(formInputService.findAssessmentInputsByCompetition(competitionId)).thenReturn(combineLists(formInputsForQuestion1, formInputsForQuestion2, formInputsForQuestion3));

        // The fourth question will have form inputs without a complete set of responses meaning that it should be incomplete
        List<FormInputResource> formInputsForQuestion4 = newFormInputResource()
                .withId(6L, 7L)
                .withType(anotherTypeOfFormInput, FormInputType.TEXTAREA)
                .withQuestion(4L, 4L)
                .build(2);
        when(formInputService.findAssessmentInputsByCompetition(competitionId)).thenReturn(concat(concat(concat(formInputsForQuestion1.stream(), formInputsForQuestion2.stream()), formInputsForQuestion3.stream()), formInputsForQuestion4.stream()).collect(toList()));

        List<AssessorFormInputResponseResource> assessorResponses = newAssessorFormInputResponseResource()
                .withQuestion(2L, 2L, 3L, 3L, 3L, 4L)
                .withFormInput(1L, 2L, 3L, 4L, 5L, 6L)
                .withValue("another response", "true", "another response", "15", "feedback", "another response")
                .build(6);
        when(assessorFormInputResponseService.getAllAssessorFormInputResponses(assessmentId))
                .thenReturn(assessorResponses);

        List<QuestionResource> questions = newQuestionResource()
                .withId(1L, 2L, 3L, 4L)
                .withSection(1L, 2L, 2L, 2L)
                .withQuestionNumber(null, null, "1", "2")
                .withShortName("Application details", "Scope", "Business opportunity", "Potential market")
                .withAssessorMaximumScore(null, null, 20, null)
                .build(4);

        when(questionService.getQuestionsByAssessment(assessmentId)).thenReturn(questions);
    }

    @Test
    public void getSummary() throws Exception {
        MvcResult result = mockMvc.perform(get("/{assessmentId}/summary", assessmentId))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("model"))
                .andExpect(view().name("assessment/application-summary"))
                .andReturn();

        AssessmentSummaryViewModel model = (AssessmentSummaryViewModel) result.getModelAndView().getModel().get("model");

        assertEquals(assessmentId, model.getAssessmentId());
        assertEquals(50, model.getDaysLeftPercentage());
        assertEquals(3, model.getDaysLeft());
        assertEquals(competition, model.getCompetition());
        assertEquals(application, model.getApplication());
        assertEquals(1, model.getQuestionsForScoreOverview().size());
        assertEquals(3, model.getQuestionsForReview().size());
        assertEquals(15, model.getTotalScoreGiven());
        assertEquals(20, model.getTotalScorePossible());
        assertEquals(75, model.getTotalScorePercentage());

        AssessmentSummaryQuestionViewModel scoreOverviewQuestion1 = model.getQuestionsForScoreOverview().get(0);
        assertEquals(Long.valueOf(3L), scoreOverviewQuestion1.getQuestionId());
        assertEquals("1. Business opportunity", scoreOverviewQuestion1.getDisplayLabel());
        assertEquals("Q1", scoreOverviewQuestion1.getDisplayLabelShort());
        assertTrue(scoreOverviewQuestion1.isScoreFormInputExists());
        assertEquals(Integer.valueOf(15), scoreOverviewQuestion1.getScoreGiven());
        assertEquals(Integer.valueOf(20), scoreOverviewQuestion1.getScorePossible());
        assertEquals("feedback", scoreOverviewQuestion1.getFeedback());
        assertNull(scoreOverviewQuestion1.getApplicationInScope());
        assertTrue(scoreOverviewQuestion1.isComplete());

        AssessmentSummaryQuestionViewModel reviewQuestion1 = model.getQuestionsForReview().get(0);
        assertEquals(Long.valueOf(2L), reviewQuestion1.getQuestionId());
        assertEquals("Scope", reviewQuestion1.getDisplayLabel());
        assertEquals("", reviewQuestion1.getDisplayLabelShort());
        assertFalse(reviewQuestion1.isScoreFormInputExists());
        assertNull(reviewQuestion1.getScoreGiven());
        assertNull(reviewQuestion1.getScorePossible());
        assertNull(reviewQuestion1.getFeedback());
        assertTrue(reviewQuestion1.getApplicationInScope());
        assertTrue(reviewQuestion1.isComplete());

        AssessmentSummaryQuestionViewModel reviewQuestion2 = model.getQuestionsForReview().get(1);
        assertEquals(Long.valueOf(3L), reviewQuestion2.getQuestionId());
        assertEquals("1. Business opportunity", reviewQuestion2.getDisplayLabel());
        assertEquals("Q1", reviewQuestion2.getDisplayLabelShort());
        assertTrue(reviewQuestion2.isScoreFormInputExists());
        assertEquals(Integer.valueOf(15), reviewQuestion2.getScoreGiven());
        assertEquals(Integer.valueOf(20), reviewQuestion2.getScorePossible());
        assertEquals("feedback", reviewQuestion2.getFeedback());
        assertNull(reviewQuestion2.getApplicationInScope());
        assertTrue(reviewQuestion2.isComplete());

        AssessmentSummaryQuestionViewModel reviewQuestion3 = model.getQuestionsForReview().get(2);
        assertEquals(Long.valueOf(4L), reviewQuestion3.getQuestionId());
        assertEquals("2. Potential market", reviewQuestion3.getDisplayLabel());
        assertEquals("Q2", reviewQuestion3.getDisplayLabelShort());
        assertFalse(reviewQuestion3.isScoreFormInputExists());
        assertNull(reviewQuestion3.getScoreGiven());
        assertNull(reviewQuestion3.getScorePossible());
        assertNull(reviewQuestion3.getFeedback());
        assertNull(reviewQuestion3.getApplicationInScope());
        assertFalse(reviewQuestion3.isComplete());
    }

    @Test
    public void getSummary_withExistingFundingConfirmation() throws Exception {
        Long assessmentWithExistingOutcomeId = 99L;
        String expectedFeedback = "feedback";
        String expectedComment = "comment";

        when(assessmentService.getById(assessmentWithExistingOutcomeId)).thenReturn(newAssessmentResource()
                .with(id(assessmentWithExistingOutcomeId))
                .withApplication(applicationId)
                .withCompetition(competitionId)
                .withFundingDecision(newAssessmentFundingDecisionOutcomeResource()
                        .withFundingConfirmation(true)
                        .withFeedback(expectedFeedback)
                        .withComment(expectedComment)
                        .build())
                .build());

        AssessmentSummaryForm expectedForm = new AssessmentSummaryForm();
        expectedForm.setFundingConfirmation(true);
        expectedForm.setFeedback(expectedFeedback);
        expectedForm.setComment(expectedComment);

        MvcResult result = mockMvc.perform(get("/{assessmentId}/summary", assessmentWithExistingOutcomeId))
                .andExpect(status().isOk())
                .andExpect(model().attribute("form", expectedForm))
                .andExpect(model().attributeExists("model"))
                .andExpect(model().hasNoErrors())
                .andExpect(view().name("assessment/application-summary"))
                .andReturn();

        AssessmentSummaryViewModel model = (AssessmentSummaryViewModel) result.getModelAndView().getModel().get("model");

        assertEquals(assessmentWithExistingOutcomeId, model.getAssessmentId());
        assertEquals(competition, model.getCompetition());
        assertEquals(application, model.getApplication());
    }

    @Test
    public void save() throws Exception {
        String feedback = String.join(" ", nCopies(100, "feedback"));
        String comment = String.join(" ", nCopies(100, "comment"));

        when(assessmentService.recommend(assessmentId, true, feedback, comment)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/{assessmentId}/summary", assessmentId)
                .contentType(APPLICATION_FORM_URLENCODED)
                .param("fundingConfirmation", "true")
                .param("feedback", feedback)
                .param("comment", comment))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/assessor/dashboard/competition/" + competitionId));

        verify(assessmentService).recommend(assessmentId, true, feedback, comment);
    }

    @Test
    public void save_noFundingConfirmation() throws Exception {
        String feedback = String.join(" ", nCopies(100, "feedback"));
        String comment = String.join(" ", nCopies(100, "comment"));

        MvcResult result = mockMvc.perform(post("/{assessmentId}/summary", assessmentId)
                .contentType(APPLICATION_FORM_URLENCODED)
                .param("feedback", feedback)
                .param("comment", comment))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("form"))
                .andExpect(model().attributeExists("model"))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrors("form", "fundingConfirmation"))
                .andExpect(view().name("assessment/application-summary"))
                .andReturn();

        AssessmentSummaryForm form = (AssessmentSummaryForm) result.getModelAndView().getModel().get("form");

        assertNull(form.getFundingConfirmation());
        assertEquals(feedback, form.getFeedback());
        assertEquals(comment, form.getComment());

        BindingResult bindingResult = form.getBindingResult();

        assertTrue(bindingResult.hasErrors());
        assertEquals(0, bindingResult.getGlobalErrorCount());
        assertEquals(1, bindingResult.getFieldErrorCount());
        assertTrue(bindingResult.hasFieldErrors("fundingConfirmation"));
        assertEquals("Please indicate your decision.", bindingResult.getFieldError("fundingConfirmation").getDefaultMessage());

        verify(assessmentService).getById(assessmentId);
        verifyNoMoreInteractions(assessmentService);
    }

    @Test
    public void save_noFeedbackAndFundingConfirmationIsTrue() throws Exception {
        String comment = String.join(" ", nCopies(100, "comment"));

        when(assessmentService.recommend(assessmentId, true, null, comment)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/{assessmentId}/summary", assessmentId)
                .contentType(APPLICATION_FORM_URLENCODED)
                .param("fundingConfirmation", "true")
                .param("comment", comment))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/assessor/dashboard/competition/" + competitionId));

        verify(assessmentService).recommend(assessmentId, true, null, comment);
    }

    @Test
    public void save_noFeedbackAndFundingConfirmationIsFalse() throws Exception {
        String comment = String.join(" ", nCopies(100, "comment"));

        MvcResult result = mockMvc.perform(post("/{assessmentId}/summary", assessmentId)
                .contentType(APPLICATION_FORM_URLENCODED)
                .param("fundingConfirmation", "false")
                .param("comment", comment))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("form"))
                .andExpect(model().attributeExists("model"))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrors("form"))
                .andExpect(view().name("assessment/application-summary"))
                .andReturn();

        AssessmentSummaryForm form = (AssessmentSummaryForm) result.getModelAndView().getModel().get("form");

        assertFalse(form.getFundingConfirmation());
        assertNull(form.getFeedback());
        assertEquals(comment, form.getComment());

        BindingResult bindingResult = form.getBindingResult();

        assertTrue(bindingResult.hasErrors());
        assertEquals(0, bindingResult.getGlobalErrorCount());
        assertEquals(1, bindingResult.getFieldErrorCount());
        assertTrue(bindingResult.hasFieldErrors("feedback"));
        assertEquals("Please enter your feedback.", bindingResult.getFieldError("feedback").getDefaultMessage());

        verify(assessmentService).getById(assessmentId);
        verifyNoMoreInteractions(assessmentService);
    }

    @Test
    public void save_noComment() throws Exception {
        String feedback = String.join(" ", nCopies(100, "feedback"));

        when(assessmentService.recommend(assessmentId, true, feedback, null)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/{assessmentId}/summary", assessmentId)
                .contentType(APPLICATION_FORM_URLENCODED)
                .param("fundingConfirmation", "true")
                .param("feedback", feedback))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/assessor/dashboard/competition/" + competitionId));

        verify(assessmentService).recommend(assessmentId, true, feedback, null);
    }

    @Test
    public void save_exceedsCharacterSizeLimit() throws Exception {
        String feedback = RandomStringUtils.random(5001);
        String comment = RandomStringUtils.random(5001);

        MvcResult result = mockMvc.perform(post("/{assessmentId}/summary", assessmentId)
                .contentType(APPLICATION_FORM_URLENCODED)
                .param("fundingConfirmation", "true")
                .param("feedback", feedback)
                .param("comment", comment))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("form"))
                .andExpect(model().attributeExists("model"))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrors("form", "feedback"))
                .andExpect(model().attributeHasFieldErrors("form", "comment"))
                .andExpect(view().name("assessment/application-summary"))
                .andReturn();

        AssessmentSummaryForm form = (AssessmentSummaryForm) result.getModelAndView().getModel().get("form");

        assertTrue(form.getFundingConfirmation());
        assertEquals(feedback, form.getFeedback());
        assertEquals(comment, form.getComment());

        BindingResult bindingResult = form.getBindingResult();

        assertTrue(bindingResult.hasErrors());
        assertEquals(0, bindingResult.getGlobalErrorCount());
        assertEquals(2, bindingResult.getFieldErrorCount());
        assertTrue(bindingResult.hasFieldErrors("feedback"));
        assertTrue(bindingResult.hasFieldErrors("comment"));
        assertEquals("This field cannot contain more than {1} characters.", bindingResult.getFieldError("feedback").getDefaultMessage());
        assertEquals(5000, bindingResult.getFieldError("feedback").getArguments()[1]);
        assertEquals("This field cannot contain more than {1} characters.", bindingResult.getFieldError("comment").getDefaultMessage());
        assertEquals(5000, bindingResult.getFieldError("comment").getArguments()[1]);

        verify(assessmentService).getById(assessmentId);
        verifyNoMoreInteractions(assessmentService);
    }

    @Test
    public void save_exceedsWordLimit() throws Exception {
        String feedback = String.join(" ", nCopies(101, "feedback"));
        String comment = String.join(" ", nCopies(101, "comment"));

        MvcResult result = mockMvc.perform(post("/{assessmentId}/summary", assessmentId)
                .contentType(APPLICATION_FORM_URLENCODED)
                .param("fundingConfirmation", "true")
                .param("feedback", feedback)
                .param("comment", comment))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("form"))
                .andExpect(model().attributeExists("model"))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrors("form", "feedback"))
                .andExpect(model().attributeHasFieldErrors("form", "comment"))
                .andExpect(view().name("assessment/application-summary"))
                .andReturn();

        AssessmentSummaryForm form = (AssessmentSummaryForm) result.getModelAndView().getModel().get("form");

        assertTrue(form.getFundingConfirmation());
        assertEquals(feedback, form.getFeedback());
        assertEquals(comment, form.getComment());

        BindingResult bindingResult = form.getBindingResult();
        assertEquals(0, bindingResult.getGlobalErrorCount());
        assertEquals(2, bindingResult.getFieldErrorCount());
        assertTrue(bindingResult.hasFieldErrors("feedback"));
        assertTrue(bindingResult.hasFieldErrors("comment"));
        assertEquals("Maximum word count exceeded. Please reduce your word count to {1}.", bindingResult.getFieldError("feedback").getDefaultMessage());
        assertEquals(100, bindingResult.getFieldError("feedback").getArguments()[1]);
        assertEquals("Maximum word count exceeded. Please reduce your word count to {1}.", bindingResult.getFieldError("comment").getDefaultMessage());
        assertEquals(100, bindingResult.getFieldError("comment").getArguments()[1]);

        verify(assessmentService).getById(assessmentId);
        verifyNoMoreInteractions(assessmentService);
    }
}
