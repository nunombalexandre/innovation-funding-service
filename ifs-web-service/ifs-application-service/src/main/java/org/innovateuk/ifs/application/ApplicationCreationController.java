package org.innovateuk.ifs.application;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.ApplicationService;
import org.innovateuk.ifs.commons.security.UserAuthenticationService;
import org.innovateuk.ifs.registration.OrganisationCreationController;
import org.innovateuk.ifs.registration.RegistrationController;
import org.innovateuk.ifs.util.CookieUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.innovateuk.ifs.util.InviteUtil.INVITE_HASH;

/**
 * This controller will handle all requests that are related to the create of a application.
 * This is used when the users want create a new application and that also includes the creation of the organisation.
 * These URLs are publicly available, since there user might not have a account yet.
 * <p>
 * The user input is stored in cookies, so we can use the data after a page refresh / redirect.
 * For user-account creation, have a look at {@link RegistrationController}
 */
@Controller
@RequestMapping("/application/create")
public class ApplicationCreationController {
    public static final String COMPETITION_ID = "competitionId";
    public static final String USER_ID = "userId";
    private static final String APPLICATION_ID = "applicationId";
    private static final Log log = LogFactory.getLog(ApplicationCreationController.class);

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private UserAuthenticationService userAuthenticationService;

    @Autowired
    private CookieUtil cookieUtil;

    @RequestMapping("/check-eligibility/{competitionId}")
    public String checkEligibility(Model model,
                                   @PathVariable(COMPETITION_ID) Long competitionId,
                                   HttpServletResponse response) {
        model.addAttribute(COMPETITION_ID, competitionId);
        cookieUtil.saveToCookie(response, COMPETITION_ID, String.valueOf(competitionId));
        cookieUtil.removeCookie(response, INVITE_HASH);
        cookieUtil.removeCookie(response, OrganisationCreationController.ORGANISATION_ID);

        return "create-application/check-eligibility";
    }

    @RequestMapping("/your-details")
    public String checkEligibility() {
        return "create-application/your-details";
    }

    @RequestMapping("/initialize-application")
    public String initializeApplication(HttpServletRequest request,
                                        HttpServletResponse response) {
        log.info("get competition id");

        Long competitionId = Long.valueOf(cookieUtil.getCookieValue(request, COMPETITION_ID));
        log.info("get user id");
        Long userId = Long.valueOf(cookieUtil.getCookieValue(request, USER_ID));

        ApplicationResource application = applicationService.createApplication(competitionId, userId, "");
        if (application == null || application.getId() == null) {
            log.error("Application not created with competitionID: " + competitionId);
            log.error("Application not created with userId: " + userId);
        } else {
            cookieUtil.saveToCookie(response, APPLICATION_ID, String.valueOf(application.getId()));

            // TODO INFUND-936 temporary measure to redirect to login screen until email verification is in place below
            if (userAuthenticationService.getAuthentication(request) == null) {
                return "redirect:/";
            }
            // TODO INFUND-936 temporary measure to redirect to login screen until email verification is in place above
            return String.format("redirect:/application/%s/contributors/invite?newApplication", String.valueOf(application.getId()));

        }
        return null;
    }
}
