package com.worth.ifs.competition.transactional;

import com.google.common.collect.Lists;
import com.worth.ifs.application.domain.Question;
import com.worth.ifs.application.repository.QuestionRepository;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.competition.resource.CompetitionSetupQuestionResource;
import com.worth.ifs.competition.resource.CompetitionSetupQuestionType;
import com.worth.ifs.form.domain.FormInput;
import com.worth.ifs.form.mapper.GuidanceRowMapper;
import com.worth.ifs.form.repository.FormInputRepository;
import com.worth.ifs.form.resource.FormInputScope;
import com.worth.ifs.form.resource.FormInputType;
import com.worth.ifs.transactional.BaseTransactionalService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service for operations around the usage and processing of Competitions questions in setup.
 */
@Service
public class CompetitionSetupQuestionServiceImpl extends BaseTransactionalService implements CompetitionSetupQuestionService {
    
	private static final Log LOG = LogFactory.getLog(CompetitionSetupQuestionServiceImpl.class);

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private FormInputRepository formInputRepository;

    @Autowired
    private GuidanceRowMapper guidanceRowMapper;

    @Override
    public ServiceResult<CompetitionSetupQuestionResource> getByQuestionId(Long questionId) {
        Question question = questionRepository.findOne(questionId);
        CompetitionSetupQuestionResource setupResource = new CompetitionSetupQuestionResource();

        question.getFormInputs().forEach(formInput -> {
            if(FormInputScope.ASSESSMENT.equals(formInput.getScope())) {
                mapAssessmentFormInput(formInput, setupResource);
            } else {
                mapApplicationFormInput(formInput, setupResource);
            }
        });

        setupResource.setScoreTotal(question.getAssessorMaximumScore());
        setupResource.setNumber(question.getQuestionNumber());
        setupResource.setShortTitle(question.getShortName());
        setupResource.setTitle(question.getName());
        setupResource.setSubTitle(question.getDescription());
        setupResource.setQuestionId(question.getId());
        setupResource.setType(CompetitionSetupQuestionType.typeFromQuestionTitle(question.getShortName()));
        setupResource.setShortTitleEditable(isShortNameEditable(setupResource.getType()));

        return ServiceResult.serviceSuccess(setupResource);
    }

    private void mapApplicationFormInput(FormInput formInput, CompetitionSetupQuestionResource setupResource) {
        switch (formInput.getType()) {
            case FILEUPLOAD:
                setupResource.setAppendix(formInput.getActive());
                break;
            case TEXTAREA:
                setupResource.setGuidanceTitle(formInput.getGuidanceQuestion());
                setupResource.setGuidance(formInput.getGuidanceAnswer());
                setupResource.setMaxWords(wordCountWithDefault(formInput.getWordCount()));
                break;
        }
    }

    private void mapAssessmentFormInput(FormInput formInput, CompetitionSetupQuestionResource setupResource) {
        switch (formInput.getType()) {
            case TEXTAREA:
                setupResource.setWrittenFeedback(formInput.getActive());
                setupResource.setAssessmentMaxWords(wordCountWithDefault(formInput.getWordCount()));
                setupResource.setAssessmentGuidance(formInput.getGuidanceQuestion());
                setupResource.setGuidanceRows(Lists.newArrayList(guidanceRowMapper.mapToResource(formInput.getGuidanceRows())));
                break;
            case ASSESSOR_SCORE:
                setupResource.setScored(formInput.getActive());
                break;
            case ASSESSOR_APPLICATION_IN_SCOPE:
                setupResource.setScope(formInput.getActive());
                break;
            case ASSESSOR_RESEARCH_CATEGORY:
                setupResource.setResearchCategoryQuestion(formInput.getActive());
                break;
        }
    }

    @Override
    public ServiceResult<CompetitionSetupQuestionResource> save(CompetitionSetupQuestionResource competitionSetupQuestionResource) {
        Long questionId = competitionSetupQuestionResource.getQuestionId();
        Question question = questionRepository.findOne(questionId);

        if (isShortNameEditable(CompetitionSetupQuestionType.typeFromQuestionTitle(question.getShortName()))) {
            question.setShortName(competitionSetupQuestionResource.getShortTitle());
        }

        question.setName(competitionSetupQuestionResource.getTitle());
        question.setDescription(competitionSetupQuestionResource.getSubTitle());

        FormInput questionFormInput = formInputRepository.findByQuestionIdAndScopeAndType(questionId, FormInputScope.APPLICATION, FormInputType.TEXTAREA);
        questionFormInput.setGuidanceQuestion(competitionSetupQuestionResource.getGuidanceTitle());
        questionFormInput.setGuidanceAnswer(competitionSetupQuestionResource.getGuidance());
        questionFormInput.setWordCount(competitionSetupQuestionResource.getMaxWords());

        markAppendixAsActiveOrInactive(questionId, competitionSetupQuestionResource, question, questionFormInput);

        //TODO INFUND-5685 and INFUND-5631 Save assessor form inputs for AssessorFormInputTypes

        return ServiceResult.serviceSuccess(competitionSetupQuestionResource);
    }

    private void markAppendixAsActiveOrInactive(Long questionId, CompetitionSetupQuestionResource competitionSetupQuestionResource, Question question, FormInput questionFormInput) {
        FormInput appendixFormInput = formInputRepository.findByQuestionIdAndScopeAndType(questionId, FormInputScope.APPLICATION, FormInputType.FILEUPLOAD);
        if (appendixFormInput != null && competitionSetupQuestionResource.getAppendix() != null) {
            appendixFormInput.setActive(competitionSetupQuestionResource.getAppendix());
        }
    }

    private int wordCountWithDefault(Integer wordCount) {
        if (wordCount != null && wordCount > 0) {
            return wordCount;
        } else {
            return 400;
        }
    }

    private boolean isShortNameEditable(CompetitionSetupQuestionType type) {
        return CompetitionSetupQuestionType.ASSESSED_QUESTION.equals(type);
    }
}