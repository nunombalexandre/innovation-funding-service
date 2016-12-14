package org.innovateuk.ifs.profile;

import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.application.service.OrganisationService;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.security.UserAuthenticationService;
import org.innovateuk.ifs.invite.service.EthnicityRestService;
import org.innovateuk.ifs.organisation.resource.OrganisationAddressResource;
import org.innovateuk.ifs.profile.form.UserDetailsForm;
import org.innovateuk.ifs.profile.viewmodel.UserDetailsViewModel;
import org.innovateuk.ifs.user.resource.EthnicityResource;
import org.innovateuk.ifs.user.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.UserService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

import static org.innovateuk.ifs.util.ProfileUtil.getAddress;
import static org.innovateuk.ifs.util.ProfileUtil.getUserOrganisationId;

/**
 * This controller will handle all requests that are related to a user profile.
 */

@Controller
@RequestMapping("/profile")
public class ProfileController {
    private static final Log LOG = LogFactory.getLog(ProfileController.class);

    @Autowired
    private UserService userService;
    
    @Autowired
    private OrganisationService organisationService;

    @Autowired
    private EthnicityRestService ethnicityRestService;

    @Autowired
    UserAuthenticationService userAuthenticationService;


    @RequestMapping(value = "/view", method = RequestMethod.GET)
    public String viewUserProfile(Model model, HttpServletRequest request) {
        final UserResource userResource = userAuthenticationService.getAuthenticatedUser(request);
        final OrganisationResource organisationResource = organisationService.getOrganisationById(getUserOrganisationId(userResource));

        model.addAttribute("model", new UserDetailsViewModel(userResource, organisationResource, ethnicityRestService.findAllActive().getSuccessObjectOrThrowException()));
        model.addAttribute("userIsLoggedIn", userIsLoggedIn(request));
        return "profile/user-profile";
    }

    private void populateUserDetailsForm(Model model, HttpServletRequest request){
        final UserResource user = userAuthenticationService.getAuthenticatedUser(request);
        final OrganisationResource organisationResource = organisationService.getOrganisationById(getUserOrganisationId(user));
        UserDetailsForm userDetailsForm = buildUserDetailsForm(user, organisationResource);
        setFormActionURL(userDetailsForm);
        model.addAttribute("userDetailsForm", userDetailsForm);
    }
    
	private UserDetailsForm buildUserDetailsForm(final UserResource user, final OrganisationResource organisation){
        UserDetailsForm form = new UserDetailsForm();
        form.setEmail(user.getEmail());
        form.setFirstName(user.getFirstName());
        form.setLastName(user.getLastName());
        form.setTitle(user.getTitle());
        form.setPhoneNumber(user.getPhoneNumber());
        form.setEthnicity(user.getEthnicity() != null ? user.getEthnicity().toString() : null);
        form.setGender(user.getGender() != null ? user.getGender().name() : null);
        form.setDisability(user.getDisability() != null ? user.getDisability().name() : null);

        if(organisation == null) {
        	LOG.warn("No organisation retrieved for user" + user.getId());
			return form;
		}
		form.setOrganisationName(organisation.getName());
		form.setCompanyHouseNumber(organisation.getCompanyHouseNumber());
		
		Optional<OrganisationAddressResource> organisationAddress = getAddress(organisation);
		
		if(organisationAddress.isPresent() && organisationAddress.get().getAddress() != null) {
			AddressResource address = organisationAddress.get().getAddress();
			
			form.setAddressLine1(address.getAddressLine1());
			form.setAddressLine2(address.getAddressLine2());
			form.setAddressLine3(address.getAddressLine3());
			form.setCounty(address.getCounty());
			form.setPostcode(address.getPostcode());
			form.setTown(address.getTown());
		}
		return form;
    }
	


    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    public String submitUserProfile(@Valid @ModelAttribute UserDetailsForm userDetailsForm, BindingResult bindingResult,
                                    Model model, HttpServletRequest request) {
        String destination = "profile/edit-user-profile";

        boolean userIsLoggedIn = userIsLoggedIn(request);
        model.addAttribute("userIsLoggedIn", userIsLoggedIn);
        final UserResource loggedInUser = userAuthenticationService.getAuthenticatedUser(request);

        if(!bindingResult.hasErrors()) {
            RestResult<UserResource> updateProfileResult = updateUser(loggedInUser, userDetailsForm);

            if (updateProfileResult.isSuccess()) {
                destination = viewUserProfile(model, request);
            } else {
                addEnvelopeErrorsToBindingResultErrors(updateProfileResult.getFailure().getErrors(), bindingResult);
            }
        }

        return destination;
    }

    @RequestMapping(value = "/edit", method = RequestMethod.GET)
    public String editUserProfile(HttpServletRequest request, Model model) {
        populateUserDetailsForm(model, request);
        boolean userIsLoggedIn = userIsLoggedIn(request);
        model.addAttribute("userIsLoggedIn", userIsLoggedIn);
        model.addAttribute("ethnicityOptions", getEthnicityOptions());
        return "profile/edit-user-profile";
    }

    private List<EthnicityResource> getEthnicityOptions() {
        return ethnicityRestService.findAllActive().getSuccessObjectOrThrowException();
    }

    private boolean userIsLoggedIn(HttpServletRequest request) {
        Authentication authentication = userAuthenticationService.getAuthentication(request);

        return authentication != null;
    }

    private void setFormActionURL(UserDetailsForm userDetailsForm) {
        userDetailsForm.setActionUrl("/profile/edit");
    }

    private RestResult<UserResource> updateUser(final UserResource loggedInUser, UserDetailsForm userDetailsForm) {
        return userService.updateDetails(
                loggedInUser.getId(),
                loggedInUser.getEmail(),
                userDetailsForm.getFirstName(),
                userDetailsForm.getLastName(),
                userDetailsForm.getTitle(),
                userDetailsForm.getPhoneNumber(),
                userDetailsForm.getGender(),
                Long.parseLong(userDetailsForm.getEthnicity()),
                userDetailsForm.getDisability());
    }

    private void addEnvelopeErrorsToBindingResultErrors(List<Error> errors, BindingResult bindingResult) {
        errors.forEach(
            error -> bindingResult.addError(new ObjectError(error.getErrorKey(), new String[] {error.getErrorKey()}, null, null))
        );
    }
}