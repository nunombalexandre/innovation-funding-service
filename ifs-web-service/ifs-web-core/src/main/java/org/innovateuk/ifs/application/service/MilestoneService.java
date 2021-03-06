package org.innovateuk.ifs.application.service;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.MilestoneResource;
import org.innovateuk.ifs.competition.resource.MilestoneType;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Interface for CRUD operations on {@link MilestoneResource} related data.
 */
@Service
public interface MilestoneService {

    List<MilestoneResource> getAllPublicMilestonesByCompetitionId(Long competitionId);

    List<MilestoneResource> getAllMilestonesByCompetitionId(Long competitionId);

    MilestoneResource getMilestoneByTypeAndCompetitionId(MilestoneType type, Long competitionId);

    ServiceResult<Void> updateMilestones(List<MilestoneResource> milestones);

    ServiceResult<Void> updateMilestone(MilestoneResource milestone);

    ServiceResult<MilestoneResource> create(MilestoneType type, Long competitionId);
}
