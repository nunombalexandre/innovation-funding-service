package org.innovateuk.ifs.assessment.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions;
import org.innovateuk.ifs.invite.constant.InviteStatus;
import org.innovateuk.ifs.invite.resource.CompetitionInviteResource;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;

/**
 * Builder for {@link org.innovateuk.ifs.invite.resource.CompetitionInviteResource}
 */
public class CompetitionInviteResourceBuilder extends BaseBuilder<CompetitionInviteResource, CompetitionInviteResourceBuilder> {

    private CompetitionInviteResourceBuilder(List<BiConsumer<Integer, CompetitionInviteResource>> multiActions) {
        super(multiActions);
    }

    public static CompetitionInviteResourceBuilder newCompetitionInviteResource() {
        return new CompetitionInviteResourceBuilder(emptyList());
    }

    public CompetitionInviteResourceBuilder withIds(Long... ids) {
        return withArray(BaseBuilderAmendFunctions::setId, ids);
    }

    public CompetitionInviteResourceBuilder withCompetitionName(String... competitionNames) {
        return withArraySetFieldByReflection("competitionName", competitionNames);
    }

    public CompetitionInviteResourceBuilder withAcceptsDate(LocalDateTime... acceptsDates) {
        return withArraySetFieldByReflection("acceptsDate", acceptsDates);
    }

    public CompetitionInviteResourceBuilder withDeadlineDate(LocalDateTime... deadlineDates) {
        return withArraySetFieldByReflection("deadlineDate", deadlineDates);
    }

    public CompetitionInviteResourceBuilder withBriefingDate(LocalDateTime... briefingDates) {
        return withArraySetFieldByReflection("briefingDate", briefingDates);
    }

    public CompetitionInviteResourceBuilder withAssessorPay(BigDecimal... assessorPays) {
        return withArraySetFieldByReflection("assessorPay", assessorPays);
    }

    public CompetitionInviteResourceBuilder withEmail(String... emails) {
        return withArraySetFieldByReflection("email", emails);
    }

    public CompetitionInviteResourceBuilder withStatus(InviteStatus... statuses) {
        return withArraySetFieldByReflection("status", statuses);
    }

    @Override
    protected CompetitionInviteResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, CompetitionInviteResource>> actions) {
        return new CompetitionInviteResourceBuilder(actions);
    }

    @Override
    protected CompetitionInviteResource createInitial() {
        return new CompetitionInviteResource();
    }
}
