package org.innovateuk.ifs.management.viewmodel;

import org.innovateuk.ifs.competition.resource.*;

/**
 * Holder of key statistics for the in-flight dashboard
 */
public class CompetitionInFlightStatsViewModel {

    private int statOne;
    private int statTwo;
    private Integer statThree;
    private Integer statFour;
    private Integer statFive;
    private Integer statSix;
    private boolean canManageFundingNotifications = true;
    private boolean canReleaseFeedback = true;

    public CompetitionInFlightStatsViewModel() {

    }

    public CompetitionInFlightStatsViewModel(CompetitionReadyToOpenKeyStatisticsResource keyStatisticsResource) {
        this.statOne = keyStatisticsResource.getAssessorsInvited();
        this.statTwo = keyStatisticsResource.getAssessorsAccepted();
        this.canManageFundingNotifications = false;
        this.canReleaseFeedback = false;
    }

    public CompetitionInFlightStatsViewModel(CompetitionOpenKeyStatisticsResource keyStatisticsResource) {
        this.statOne = keyStatisticsResource.getAssessorsInvited();
        this.statTwo = keyStatisticsResource.getAssessorsAccepted();
        this.statThree = keyStatisticsResource.getApplicationsPerAssessor();
        this.statFour = keyStatisticsResource.getApplicationsStarted();
        this.statFive = keyStatisticsResource.getApplicationsPastHalf();
        this.statSix = keyStatisticsResource.getApplicationsSubmitted();
        this.canManageFundingNotifications = false;
        this.canReleaseFeedback = false;
    }

    public CompetitionInFlightStatsViewModel(CompetitionClosedKeyStatisticsResource keyStatisticsResource) {
        this.statOne = keyStatisticsResource.getApplicationsRequiringAssessors();
        this.statTwo = keyStatisticsResource.getAssignmentCount();
        this.statThree = keyStatisticsResource.getAssessorsWithoutApplications();
        this.statFour = keyStatisticsResource.getAssessorsInvited();
        this.statFive = keyStatisticsResource.getAssessorsAccepted();
        this.statSix = keyStatisticsResource.getApplicationsPerAssessor();
        this.canManageFundingNotifications = false;
        this.canReleaseFeedback = false;
    }

    public CompetitionInFlightStatsViewModel(CompetitionInAssessmentKeyStatisticsResource keyStatisticsResource) {
        this.statOne = keyStatisticsResource.getAssignmentCount();
        this.statTwo = keyStatisticsResource.getAssignmentsWaiting();
        this.statThree = keyStatisticsResource.getAssignmentsAccepted();
        this.statFour = keyStatisticsResource.getAssessmentsStarted();
        this.statFive = keyStatisticsResource.getAssessmentsSubmitted();
        this.canManageFundingNotifications = false;
        this.canReleaseFeedback = false;
    }

    public CompetitionInFlightStatsViewModel(CompetitionFundedKeyStatisticsResource keyStatisticsResource) {
        this.statOne = keyStatisticsResource.getApplicationsSubmitted();
        this.statTwo = keyStatisticsResource.getApplicationsFunded();
        this.statThree = keyStatisticsResource.getApplicationsNotFunded();
        this.statFour = keyStatisticsResource.getApplicationsOnHold();
        this.statFive = keyStatisticsResource.getApplicationsNotifiedOfDecision();
        this.statSix = keyStatisticsResource.getApplicationsAwaitingDecision();
        this.canManageFundingNotifications = keyStatisticsResource.getCanManageFundingNotifications();
        this.canReleaseFeedback = keyStatisticsResource.getCanReleaseFeedback();
    }

    public int getStatOne() {
        return statOne;
    }

    public void setStatOne(int statOne) {
        this.statOne = statOne;
    }

    public int getStatTwo() {
        return statTwo;
    }

    public void setStatTwo(int statTwo) {
        this.statTwo = statTwo;
    }

    public Integer getStatThree() {
        return statThree;
    }

    public void setStatThree(Integer statThree) {
        this.statThree = statThree;
    }

    public Integer getStatFour() {
        return statFour;
    }

    public void setStatFour(Integer statFour) {
        this.statFour = statFour;
    }

    public Integer getStatFive() {
        return statFive;
    }

    public void setStatFive(Integer statFive) {
        this.statFive = statFive;
    }

    public Integer getStatSix() {
        return statSix;
    }

    public void setStatSix(Integer statSix) {
        this.statSix = statSix;
    }

    public boolean getCanManageFundingNotifications() {
        return canManageFundingNotifications;
    }

    public boolean isCanReleaseFeedback() {
        return canReleaseFeedback;
    }
}