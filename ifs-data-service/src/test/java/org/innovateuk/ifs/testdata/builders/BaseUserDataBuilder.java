package org.innovateuk.ifs.testdata.builders;

import org.innovateuk.ifs.testdata.builders.data.BaseUserData;
import org.innovateuk.ifs.token.domain.Token;
import org.innovateuk.ifs.token.resource.TokenType;
import org.innovateuk.ifs.user.domain.Organisation;
import org.innovateuk.ifs.user.domain.Role;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.resource.UserRoleType;

import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;

import static org.innovateuk.ifs.user.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.innovateuk.ifs.user.builder.RoleResourceBuilder.newRoleResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;
import static java.util.Collections.singletonList;
import static java.util.Optional.empty;

/**
 * Base builder for generating data for non-active and active registered users
 */
public abstract class BaseUserDataBuilder<T extends BaseUserData, S> extends BaseDataBuilder<T, S> {

    public abstract S registerUser(String firstName, String lastName, String emailAddress, String organisationName, String phoneNumber);

    public abstract S createUserDirectly(String firstName, String lastName, String emailAddress, String organisationName, String phoneNumber);

    public S withNewOrganisation(OrganisationDataBuilder organisationBuilder) {
        return with(data -> organisationBuilder.build().getOrganisation());
    }

    protected void registerUser(String firstName, String lastName, String emailAddress, String organisationName, String phoneNumber, UserRoleType role, T data) {

        doAs(systemRegistrar(), () -> {
            Organisation organisation = retrieveOrganisationByName(organisationName);
            doRegisterUserWithExistingOrganisation(firstName, lastName, emailAddress, phoneNumber, organisation.getId(), role, data);
        });
    }

    protected void registerUserWithNewOrganisation(String firstName, String lastName, String emailAddress, String phoneNumber, String organisationName, UserRoleType role, T data) {

        doAs(systemRegistrar(), () -> {
            OrganisationResource newOrganisation =
                    organisationService.create(newOrganisationResource().
                            withId().
                            withName(organisationName).
                            build()).getSuccessObjectOrThrowException();

            doRegisterUserWithExistingOrganisation(firstName, lastName, emailAddress, phoneNumber, newOrganisation.getId(), role, data);
        });
    }

    public S verifyEmail() {

        return with(data -> {

            doAs(systemRegistrar(), () -> {
                UserResource user = data.getUser();

                Optional<Token> verifyToken = tokenRepository.findByTypeAndClassNameAndClassPk(TokenType.VERIFY_EMAIL_ADDRESS, User.class.getName(), user.getId());

                verifyToken.map(token -> registrationService.activateUser(token.getClassPk()).andOnSuccessReturnVoid(v -> {
                    tokenService.handleExtraAttributes(token);
                    tokenService.removeToken(token);
                })).orElseThrow(() -> new RuntimeException("No Invite Token exists for user " + user.getId()));
            });
        });
    }

    private void updateUserInUserData(T data, Long userId) {
        UserResource user = baseUserService.getUserById(userId).getSuccessObjectOrThrowException();
        data.setUser(user);
    }

    private UserResource createUserViaRegistration(String firstName, String lastName, String emailAddress, String phoneNumber, UserRoleType role, Long organisationId) {

        List<Role> roles = roleRepository.findByNameIn(singletonList(role.getName()));

        UserResource created = registrationService.createOrganisationUser(organisationId, newUserResource().
                withFirstName(firstName).
                withLastName(lastName).
                withEmail(emailAddress).
                withPhoneNumber(phoneNumber).
                withRolesGlobal(simpleMap(roles, r -> newRoleResource().withId(r.getId()).build())).
                withPassword("Passw0rd").
                build()).
                getSuccessObjectOrThrowException();

        registrationService.sendUserVerificationEmail(created, empty());

        return created;
    }

    private void doRegisterUserWithExistingOrganisation(String firstName, String lastName, String emailAddress, String phoneNumber, Long organisationId, UserRoleType role, T data) {
        UserResource registeredUser = createUserViaRegistration(firstName, lastName, emailAddress, phoneNumber, role, organisationId);
        updateUserInUserData(data, registeredUser.getId());
    }

    public BaseUserDataBuilder(List<BiConsumer<Integer, T>> newActions, ServiceLocator serviceLocator) {
        super(newActions, serviceLocator);
    }
}
