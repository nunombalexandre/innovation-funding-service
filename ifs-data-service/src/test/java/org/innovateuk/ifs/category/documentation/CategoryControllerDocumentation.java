package org.innovateuk.ifs.category.documentation;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.category.controller.CategoryController;
import org.innovateuk.ifs.category.resource.InnovationAreaResource;
import org.innovateuk.ifs.category.resource.InnovationSectorResource;
import org.innovateuk.ifs.category.resource.ResearchCategoryResource;
import org.junit.Before;
import org.junit.Test;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;

import java.util.List;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.documentation.CategoryDocs.innovationAreaResourceBuilder;
import static org.innovateuk.ifs.documentation.CategoryDocs.innovationSectorResourceBuilder;
import static org.innovateuk.ifs.documentation.CategoryDocs.researchCategoryResourceBuilder;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;

public class CategoryControllerDocumentation extends BaseControllerMockMVCTest<CategoryController> {
    private RestDocumentationResultHandler document;

    @Override
    protected CategoryController supplyControllerUnderTest() {
        return new CategoryController();
    }

    @Before
    public void setup(){
        this.document = document("category/{method-name}",
                preprocessResponse(prettyPrint()));
    }

    @Test
    public void findInnovationAreas() throws Exception {
        List<InnovationAreaResource> innovationAreaResources = innovationAreaResourceBuilder.build(2);

        when(categoryServiceMock.getInnovationAreas()).thenReturn(serviceSuccess(innovationAreaResources));

        mockMvc.perform(get("/category/findInnovationAreas"))
                .andDo(this.document.snippets(
                        responseFields(
                                fieldWithPath("[]").description("list with all innovation areas")
                        )
                ));
    }

    @Test
    public void findInnovationSectors() throws Exception {
        List<InnovationSectorResource> innovationSectorResources = innovationSectorResourceBuilder.build(2);

        when(categoryServiceMock.getInnovationSectors()).thenReturn(serviceSuccess(innovationSectorResources));

        mockMvc.perform(get("/category/findInnovationSectors"))
                .andDo(this.document.snippets(
                        responseFields(
                                fieldWithPath("[]").description("list with all innovation sectors")
                        )
                ));
    }

    @Test
    public void findResearchCategories() throws Exception {
        List<ResearchCategoryResource> researchCategoryResources = researchCategoryResourceBuilder.build(2);

        when(categoryServiceMock.getResearchCategories()).thenReturn(serviceSuccess(researchCategoryResources));

        mockMvc.perform(get("/category/findResearchCategories"))
                .andDo(this.document.snippets(
                        responseFields(
                                fieldWithPath("[]").description("list with all research categories")
                        )
                ));
    }

    @Test
    public void findByInnovationSector() throws Exception {
        List<InnovationAreaResource> innovationAreaResources = innovationAreaResourceBuilder.build(2);

        when(categoryServiceMock.getInnovationAreasBySector(anyLong())).thenReturn(serviceSuccess(innovationAreaResources));

        mockMvc.perform(get("/category/findByInnovationSector/{sectorId}", 1L))
                .andDo(this.document.snippets(
                        pathParameters(
                                parameterWithName("sectorId").description("sector id to filter on")
                        ),
                        responseFields(
                                fieldWithPath("[]").description("list with all innovation areas that have the sector id")
                        )
                ));
    }
}
