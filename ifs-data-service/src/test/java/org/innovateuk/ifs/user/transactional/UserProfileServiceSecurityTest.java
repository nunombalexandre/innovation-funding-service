package org.innovateuk.ifs.user.transactional;

import org.innovateuk.ifs.BaseServiceSecurityTest;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.user.resource.*;
import org.innovateuk.ifs.user.security.UserLookupStrategies;
import org.innovateuk.ifs.user.security.UserPermissionRules;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.user.builder.AffiliationResourceBuilder.newAffiliationResource;
import static org.innovateuk.ifs.user.builder.ProfileAgreementResourceBuilder.newProfileAgreementResource;
import static org.innovateuk.ifs.user.builder.ProfileSkillsEditResourceBuilder.newProfileSkillsEditResource;
import static org.innovateuk.ifs.user.builder.ProfileSkillsResourceBuilder.newProfileSkillsResource;
import static org.innovateuk.ifs.user.builder.UserProfileResourceBuilder.newUserProfileResource;
import static org.innovateuk.ifs.user.builder.UserProfileStatusResourceBuilder.newUserProfileStatusResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.*;

/**
 * Tests around the integration of this service and Spring Security
 */
public class UserProfileServiceSecurityTest extends BaseServiceSecurityTest<UserProfileService> {

    private UserPermissionRules rules;
    private UserLookupStrategies userLookupStrategies;

    private static int ARRAY_SIZE_FOR_POST_FILTER_TESTS = 2;

    @Before
    public void lookupPermissionRules() {
        rules = getMockPermissionRulesBean(UserPermissionRules.class);
        userLookupStrategies = getMockPermissionEntityLookupStrategiesBean(UserLookupStrategies.class);
    }

    @Test
    public void getProfileSkills() {
        Long userId = 1L;

        assertAccessDenied(
                () -> classUnderTest.getProfileSkills(userId),
                () -> {
                    verify(rules).usersCanViewTheirOwnProfileSkills(isA(ProfileSkillsResource.class), eq(getLoggedInUser()));
                    verifyNoMoreInteractions(rules);
                }
        );
    }

    @Test
    public void updateProfileSkills() {
        Long userId = 1L;
        ProfileSkillsEditResource profileSkillsEditResource = newProfileSkillsEditResource().build();

        UserResource user = newUserResource().build();
        when(userLookupStrategies.findById(userId)).thenReturn(user);

        assertAccessDenied(() -> classUnderTest.updateProfileSkills(userId, profileSkillsEditResource), () -> {
            verify(rules).usersCanUpdateTheirOwnProfiles(user, getLoggedInUser());
            verifyNoMoreInteractions(rules);
        });
    }

    @Test
    public void getProfileAgreement() {
        Long userId = 1L;

        assertAccessDenied(
                () -> classUnderTest.getProfileAgreement(userId),
                () -> {
                    verify(rules).usersCanViewTheirOwnProfileAgreement(isA(ProfileAgreementResource.class), isA(UserResource.class));
                    verifyNoMoreInteractions(rules);
                }
        );
    }

    @Test
    public void updateProfileAgreement() {
        Long userId = 1L;

        UserResource user = newUserResource().build();
        when(userLookupStrategies.findById(userId)).thenReturn(user);

        assertAccessDenied(() -> classUnderTest.updateProfileAgreement(userId), () -> {
            verify(rules).usersCanUpdateTheirOwnProfiles(user, getLoggedInUser());
            verifyNoMoreInteractions(rules);
        });
    }

    @Test
    public void updateDetails() {
        UserResource user = newUserResource().build();

        assertAccessDenied(() -> classUnderTest.updateDetails(user), () -> {
            verify(rules).usersCanUpdateTheirOwnProfiles(user, getLoggedInUser());
            verifyNoMoreInteractions(rules);
        });
    }

    @Test
    public void getUserAffiliations() {
        Long userId = 1L;

        classUnderTest.getUserAffiliations(userId);
        verify(rules, times(ARRAY_SIZE_FOR_POST_FILTER_TESTS)).usersCanViewTheirOwnAffiliations(isA(AffiliationResource.class), eq(getLoggedInUser()));
        verifyNoMoreInteractions(rules);
    }

    @Test
    public void updateUserAffiliations() {
        Long userId = 1L;
        List<AffiliationResource> affiliations = newAffiliationResource().build(2);

        UserResource user = newUserResource().build();
        when(userLookupStrategies.findById(userId)).thenReturn(user);

        assertAccessDenied(() -> classUnderTest.updateUserAffiliations(userId, affiliations), () -> {
            verify(rules).usersCanUpdateTheirOwnProfiles(user, getLoggedInUser());
            verifyNoMoreInteractions(rules);
        });
    }

    @Test
    public void getUserProfileDetails() {
        Long userId = 1L;

        UserResource user = newUserResource().build();
        when(userLookupStrategies.findById(userId)).thenReturn(user);

        assertAccessDenied(() -> classUnderTest.getUserProfile(userId), () -> {
            verify(rules).usersCanViewTheirOwnProfile(isA(UserProfileResource.class), eq(getLoggedInUser()));
            verifyNoMoreInteractions(rules);
        });
    }

    @Test
    public void getUserProfileStatus() {
        Long userId = 1L;

        UserResource user = newUserResource().build();
        when(userLookupStrategies.findById(userId)).thenReturn(user);

        assertAccessDenied(() -> classUnderTest.getUserProfileStatus(userId), () -> {
            verify(rules).usersAndCompAdminCanViewProfileStatus(isA(UserProfileStatusResource.class), eq(getLoggedInUser()));
            verifyNoMoreInteractions(rules);
        });
    }

    @Test
    public void updateUserDetails() {
        Long userId = 1L;
        UserProfileResource profile = newUserProfileResource().build();

        UserResource user = newUserResource().build();
        when(userLookupStrategies.findById(userId)).thenReturn(user);

        assertAccessDenied(() -> classUnderTest.updateUserProfile(userId, profile), () -> {
            verify(rules).usersCanUpdateTheirOwnProfiles(user, getLoggedInUser());
            verifyNoMoreInteractions(rules);
        });
    }

    @Override
    protected Class<? extends UserProfileService> getClassUnderTest() {
        return TestUserProfileService.class;
    }

    public static class TestUserProfileService implements UserProfileService {

        @Override
        public ServiceResult<ProfileSkillsResource> getProfileSkills(long userId) {
            return serviceSuccess(newProfileSkillsResource().build());
        }

        @Override
        public ServiceResult<Void> updateProfileSkills(long userId, ProfileSkillsEditResource profileResource) {
            return null;
        }

        @Override
        public ServiceResult<ProfileAgreementResource> getProfileAgreement(long userId) {
            return serviceSuccess(newProfileAgreementResource().build());
        }

        @Override
        public ServiceResult<Void> updateProfileAgreement(long userId) {
            return null;
        }

        @Override
        public ServiceResult<Void> updateDetails(UserResource userResource) {
            return null;
        }

        @Override
        public ServiceResult<List<AffiliationResource>> getUserAffiliations(Long userId) {
            return serviceSuccess(newAffiliationResource().build(ARRAY_SIZE_FOR_POST_FILTER_TESTS));
        }

        @Override
        public ServiceResult<Void> updateUserAffiliations(Long userId, List<AffiliationResource> userProfile) {
            return null;
        }

        @Override
        public ServiceResult<UserProfileResource> getUserProfile(Long userId) {
            return serviceSuccess(newUserProfileResource().build());
        }

        @Override
        public ServiceResult<Void> updateUserProfile(Long userId, UserProfileResource profileDetails) {
            return null;
        }

        @Override
        public ServiceResult<UserProfileStatusResource> getUserProfileStatus(Long userId) {
            return serviceSuccess(newUserProfileStatusResource().build());
        }
    }
}
