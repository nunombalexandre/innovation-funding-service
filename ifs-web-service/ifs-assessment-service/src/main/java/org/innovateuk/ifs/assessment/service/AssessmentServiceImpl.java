package org.innovateuk.ifs.assessment.service;

import org.innovateuk.ifs.assessment.resource.*;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * This class contains methods to retrieve and store {@link org.innovateuk.ifs.assessment.resource.AssessmentResource} related data,
 * through the RestService {@link AssessmentRestService}.
 */
@Service
public class AssessmentServiceImpl implements AssessmentService {

    @Autowired
    private AssessmentRestService assessmentRestService;

    @Override
    public AssessmentResource getById(Long id) {
        return assessmentRestService.getById(id).getSuccessObjectOrThrowException();
    }

    @Override
    public List<AssessmentResource> getByUserAndCompetition(Long userId, Long competitionId) {
        return assessmentRestService.getByUserAndCompetition(userId, competitionId).getSuccessObjectOrThrowException();
    }

    @Override
    public AssessmentTotalScoreResource getTotalScore(Long assessmentId) {
        return assessmentRestService.getTotalScore(assessmentId).getSuccessObjectOrThrowException();
    }

    @Override
    public ServiceResult<Void> recommend(Long assessmentId, Boolean fundingConfirmation, String feedback, String comment) {
        return assessmentRestService.recommend(assessmentId, new AssessmentFundingDecisionResourceBuilder()
                .setFundingConfirmation(fundingConfirmation)
                .setFeedback(feedback)
                .setComment(comment)
                .createAssessmentFundingDecisionResource()).toServiceResult();
    }

    @Override
    public ServiceResult<Void> rejectInvitation(Long assessmentId, String reason, String comment) {
        ApplicationRejectionResource applicationRejection = new ApplicationRejectionResource();
        applicationRejection.setRejectReason(reason);
        applicationRejection.setRejectComment(comment);

        return assessmentRestService.rejectInvitation(assessmentId, applicationRejection).toServiceResult();
    }

    @Override
    public ServiceResult<Void> acceptInvitation(Long assessmentId) {
        return assessmentRestService.acceptInvitation(assessmentId).toServiceResult();
    }

    @Override
    public ServiceResult<Void> submitAssessments(List<Long> assessmentIds) {
        AssessmentSubmissionsResource assessmentSubmissions = new AssessmentSubmissionsResource();
        assessmentSubmissions.setAssessmentIds(assessmentIds);

        return assessmentRestService.submitAssessments(assessmentSubmissions).toServiceResult();
    }
}
