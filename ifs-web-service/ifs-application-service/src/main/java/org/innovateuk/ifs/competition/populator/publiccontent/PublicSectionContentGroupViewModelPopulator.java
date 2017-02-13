package org.innovateuk.ifs.competition.populator.publiccontent;


import org.innovateuk.ifs.competition.publiccontent.resource.ContentGroupResource;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentResource;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentSectionResource;
import org.innovateuk.ifs.competition.viewmodel.publiccontent.PublicSectionContentGroupViewModel;
import org.innovateuk.ifs.util.CollectionFunctions;

/**
 * Abstract class to populate the generic fields needed in the view.
 * @param <M> the view model class.
 */
public abstract class PublicSectionContentGroupViewModelPopulator<M extends PublicSectionContentGroupViewModel> extends PublicContentSectionViewModelPopulator<M> {

    protected void populateSection(M model, PublicContentResource publicContentResource, PublicContentSectionResource section) {
            model.setFileEntries(CollectionFunctions.simpleToMap(section.getContentGroups(),
                    ContentGroupResource::getId, ContentGroupResource::getFileEntry));
            model.setContentGroups(section.getContentGroups());
    }
}
