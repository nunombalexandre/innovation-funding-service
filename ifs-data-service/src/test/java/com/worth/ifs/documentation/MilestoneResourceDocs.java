package com.worth.ifs.documentation;

import static com.worth.ifs.competition.builder.MilestoneResourceBuilder.newMilestoneResource;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

import java.time.LocalDateTime;

import org.springframework.restdocs.payload.FieldDescriptor;

import com.worth.ifs.competition.builder.MilestoneResourceBuilder;

public class MilestoneResourceDocs {
    public static final FieldDescriptor[] milestone = {
        fieldWithPath("id").description("Id of the milestoneResource"),
                fieldWithPath("name").description("name of the milestone"),
                fieldWithPath("date").description("date of the particular milestone"),
                fieldWithPath("competition_id").description("Id of the competitionResource")
    };

    public static final MilestoneResourceBuilder milestoneResourceBuilder = newMilestoneResource()
            .withId(5L)
            .withDate(LocalDateTime.now())
            .withName("Briefing event test")
            .withCompetitionId(10L);
}
