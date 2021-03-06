package org.innovateuk.ifs.form.domain;

import com.google.common.collect.Sets;
import org.innovateuk.ifs.validator.EmailValidator;
import org.innovateuk.ifs.validator.NotEmptyValidator;
import org.junit.Test;

import java.util.HashSet;

import static org.hamcrest.Matchers.hasItem;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class FormInputTest {

    @Test
    public void test_formInputTypeAddValidator() throws Exception {
        FormInput formInputType = new FormInput();

        String validatorTitle = "Email Validator";
        Class clazz = EmailValidator.class;

        FormValidator emailValidator = new FormValidator();
        emailValidator.setTitle(validatorTitle);
        emailValidator.setClazz(clazz);

        FormValidator notEmptyValidator = new FormValidator();
        notEmptyValidator.setTitle(validatorTitle);
        notEmptyValidator.setClazz(NotEmptyValidator.class);

        formInputType.addFormValidator(emailValidator);
        formInputType.addFormValidator(emailValidator);
        formInputType.addFormValidator(notEmptyValidator);

        assertThat(formInputType.getFormValidators(), hasItem(emailValidator));
        assertThat(formInputType.getFormValidators(), hasItem(notEmptyValidator));
        assertEquals(formInputType.getFormValidators(), Sets.newHashSet(emailValidator, notEmptyValidator));
    }

    @Test
    public void test_formInputTypeAddValidatorSet() throws Exception {
        FormInput formInputType = new FormInput();

        String validatorTitle = "Email Validator";
        Class clazz = EmailValidator.class;

        FormValidator emailValidator = new FormValidator();
        emailValidator.setTitle(validatorTitle);
        emailValidator.setClazz(clazz);

        FormValidator notEmptyValidator = new FormValidator();
        notEmptyValidator.setTitle(validatorTitle);
        notEmptyValidator.setClazz(NotEmptyValidator.class);

        HashSet validators = new HashSet();
        validators.add(emailValidator);
        formInputType.setFormValidators(validators);

        assertThat(formInputType.getFormValidators(), hasItem(emailValidator));
        assertEquals(formInputType.getFormValidators(), Sets.newHashSet(emailValidator));
    }

}
