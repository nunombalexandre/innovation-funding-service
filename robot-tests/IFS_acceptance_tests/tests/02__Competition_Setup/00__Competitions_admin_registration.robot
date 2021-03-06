*** Settings ***
Documentation     INFUND-2129 As an internal Innovate UK user I want to be able to register with IFS as a competition administrator so that I can access the system with appropriate permissions for my role
...
...               INFUND-1987 As a Competition Administrator I want to be able to export specified data from all successfully submitted applications so that the competitions team can work with this data in the existing competitions database
Suite Setup       The guest user opens the browser
Suite Teardown    the user closes the browser
Force Tags        CompAdmin
Resource          ../../resources/defaultResources.robot

*** Test Cases ***
If user from the list is not registered shouldn't be able to login
    [Documentation]    INFUND-2129
    [Tags]
    [Setup]    delete the emails from the default test mailbox
    Given the user navigates to the page    ${LOGIN_URL}
    When the guest user enters the log in credentials    ${test_mailbox_one}+admin2@gmail.com    Passw0rd
    And the user clicks the button/link    css=button[name="_eventId_proceed"]
    Then the user should see the text in the page    Your email/password combination doesn't seem to work

Registration for a user who is in the list
    [Documentation]    INFUND-2129
    [Tags]    HappyPath    Email
    [Setup]    Delete the emails from both default test mailboxes
    Given the user navigates to the page    ${COMPETITION_DETAILS_URL}
    And User creates new account verifies email and login    ${test_mailbox_one}+admin1@gmail.com
    Then the user should be redirected to the correct page    ${COMP_ADMINISTRATOR_DASHBOARD}

*** Keywords ***
User creates new account verifies email and login
    [Arguments]    ${CREATE_ACCOUNT_EMAIL}
    the user clicks the button/link    jQuery=.column-third .button:contains("Apply now")
    the user clicks the button/link    jQuery=.button:contains("Create account")
    the user clicks the button/link    jQuery=.button:contains("Create")
    the user enters text to a text field    id=organisationSearchName    Innovate
    the user clicks the button/link    id=org-search
    the user clicks the button/link    LINK=INNOVATE LTD
    the user selects the checkbox    address-same
    the user clicks the button/link    jQuery=.button:contains("Save organisation and continue")
    the user clicks the button/link    jQuery=.button:contains("Save and continue")
    the user enters the details and clicks the create account    ${CREATE_ACCOUNT_EMAIL}
    the user should be redirected to the correct page    ${REGISTRATION_SUCCESS}
    the user reads his email from the default mailbox and clicks the link    ${CREATE_ACCOUNT_EMAIL}    Please verify your email address    Once verified you can sign into your account
    the user should be redirected to the correct page    ${REGISTRATION_VERIFIED}
    the user clicks the button/link    jQuery=.button:contains("Sign in")
    the guest user inserts user email & password    ${CREATE_ACCOUNT_EMAIL}    Passw0rd123
    the guest user clicks the log-in button

the user enters the details and clicks the create account
    [Arguments]    ${CREATE_ACCOUNT_EMAIL}
    Input Text    id=firstName    Eric
    Input Text    id=lastName    Cantona
    Input Text    id=phoneNumber    0505050508
    Input Text    id=email    ${CREATE_ACCOUNT_EMAIL}
    Input Password    id=password    Passw0rd123
    Input Password    id=retypedPassword    Passw0rd123
    And the user selects the checkbox    termsAndConditions
    Submit Form
