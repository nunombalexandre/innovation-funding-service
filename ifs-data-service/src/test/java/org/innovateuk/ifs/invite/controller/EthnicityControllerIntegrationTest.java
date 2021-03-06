package org.innovateuk.ifs.invite.controller;

import org.innovateuk.ifs.BaseControllerIntegrationTest;
import org.innovateuk.ifs.user.domain.Ethnicity;
import org.innovateuk.ifs.user.repository.EthnicityRepository;
import org.innovateuk.ifs.user.resource.EthnicityResource;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.innovateuk.ifs.user.builder.EthnicityBuilder.newEthnicity;
import static org.innovateuk.ifs.user.builder.EthnicityResourceBuilder.newEthnicityResource;
import static java.util.stream.Collectors.toList;
import static org.junit.Assert.assertEquals;

public class EthnicityControllerIntegrationTest extends BaseControllerIntegrationTest<EthnicityController> {

    @Autowired
    @Override
    protected void setControllerUnderTest(EthnicityController controller) {
        this.controller = controller;
    }

    @Autowired
    private EthnicityRepository repository;

    @Test
    public void findAllActive() throws Exception {
        repository.deleteAll();
        flushAndClearSession();

        List<Ethnicity> saved = newEthnicity()
                .withId(null, null, null, null)
                .withActive(true, true, true, false)
                .withName("VisibleName 3", "VisibleName 1", "VisibleName 2", "HiddenName")
                .withDescription("Visible 3", "Visible 1", "Visible 2", "Hidden")
                .withPriority(3, 1, 2, 4)
                .build(4).stream().map(ethnicity -> repository.save(ethnicity)).collect(toList());

        List<EthnicityResource> expected = newEthnicityResource()
                .withId(saved.get(1).getId(), saved.get(2).getId(), saved.get(0).getId())
                .withName("VisibleName 1", "VisibleName 2", "VisibleName 3")
                .withDescription("Visible 1", "Visible 2", "Visible 3")
                .withPriority(1, 2, 3)
                .build(3);

        List<EthnicityResource> found = controller.findAllActive().getSuccessObjectOrThrowException();
        assertEquals(expected, found);
    }
}
