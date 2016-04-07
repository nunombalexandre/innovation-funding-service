package com.worth.ifs.user.builder;

import com.worth.ifs.BaseBuilder;
import com.worth.ifs.user.domain.Organisation;
import com.worth.ifs.user.domain.OrganisationSize;
import com.worth.ifs.user.domain.User;
import com.worth.ifs.user.resource.OrganisationResource;
import com.worth.ifs.user.resource.UserResource;

import java.util.List;
import java.util.function.BiConsumer;

import static com.worth.ifs.BuilderAmendFunctions.*;
import static java.util.Collections.emptyList;

/**
 * Builder for OrganisationResource entities.
 */
public class OrganisationResourceBuilder extends BaseBuilder<OrganisationResource, OrganisationResourceBuilder> {

    private OrganisationResourceBuilder(List<BiConsumer<Integer, OrganisationResource>> multiActions) {
        super(multiActions);
    }

    public static OrganisationResourceBuilder newOrganisationResource() {
        return new OrganisationResourceBuilder(emptyList()).
                with(uniqueIds()).
                with(idBasedNames("OrganisationResource "));
    }

    @Override
    protected OrganisationResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, OrganisationResource>> actions) {
        return new OrganisationResourceBuilder(actions);
    }

    @Override
    protected OrganisationResource createInitial() {
        return new OrganisationResource();
    }

    public OrganisationResourceBuilder withId(Long... ids) {
        return withArray((id, organisation) -> setField("id", id, organisation), ids);
    }

    public OrganisationResourceBuilder withName(String... names) {
        return withArray((name, organisation) -> setField("name", name, organisation), names);
    }

    public OrganisationResourceBuilder withCompanyHouseNumber(String... numbers) {
        return withArray((number, organisation) -> setField("companyHouseNumber", number, organisation), numbers);
    }

    public OrganisationResourceBuilder withOrganisationType(Long... organisationTypeIds) {
        return withArray((organisationTypeId, organisation) -> setField("organisationType", organisationTypeId, organisation), organisationTypeIds);
    }

    public OrganisationResourceBuilder withUsers(List<Long>... users) {
        return withArray((user, organisation) -> setField("users", user, organisation), users);
    }

    public OrganisationResourceBuilder withProcessRoles(List<Long>... processRoles) {
        return withArray((processRoleList, organisation) -> organisation.setProcessRoles(processRoleList), processRoles);
    }

    public OrganisationResourceBuilder withOrganisationSize(OrganisationSize... size) {
        return withArray((organisationSize, organisation) -> organisation.setOrganisationSize(organisationSize), size);
    }

    @Override
    public List<OrganisationResource> build(int numberToBuild) {
        List<OrganisationResource> built = super.build(numberToBuild);

        // now add back-refs where appropriate
        built.forEach(organisation -> {
            List<Long> users = organisation.getUsers();
            users.forEach(user -> {
                // TODO DW - INFUND-1556 - when OrganisationResource users are refactored to just be userIds, remove the code below that is creating a shell Organisation
                // based on the OrganisationResource
                //user.addUserOrganisation(new Organisation(organisation.getId(), organisation.getName(), organisation.getCompanyHouseNumber(), organisation.getOrganisationSize()));
            });
        });

        return built;
    }
}
