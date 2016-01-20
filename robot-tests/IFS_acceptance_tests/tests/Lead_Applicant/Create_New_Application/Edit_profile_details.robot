*** Settings ***
Documentation     INFUND-1042 : As an applicant I want to be able to edit my user profile details so I can be identified to other users in the system
Suite Setup       Login as User    &{lead_applicant_credentials}
Suite Teardown    TestTeardown User closes the browser
Resource          ../../../resources/GLOBAL_LIBRARIES.robot
Resource          ../../../resources/variables/GLOBAL_VARIABLES.robot
Resource          ../../../resources/variables/User_credentials.robot
Resource          ../../../resources/keywords/Login_actions.robot
Resource          ../../../resources/keywords/Applicant_actions.robot

*** Test Cases ***
View and edit profile link is visible in the Dashboard page
    [Documentation]    INFUND-1042 : As an applicant I want to be able to edit my user profile details so I can be identified to other users in the system
    When the Applicant is in Dashboard page
    Then the link to Edit Profile should be present

View and edit profile link redirects to the Your profile page
    [Documentation]    INFUND-1042 : As an applicant I want to be able to edit my user profile details so I can be identified to other users in the system
    When the Applicant clicks the link Edit profile from Dashboard page
    Then the link to Edit Profile should be present in Your profile page

Edit the profile and verify if the changes are saved
    [Documentation]    INFUND-1042 : As an applicant I want to be able to edit my user profile details so I can be identified to other users in the system
    When the Applicant is in Dashboard page
    And the Applicant clicks the link Edit profile from Dashboard page
    And the applicant clicks the Edit your details link
    And the Applicant enters the profile details
    Then the Applicant should see the saved changes in Your profile page

Display error when First name is blank
    [Documentation]    INFUND-1042 : As an applicant I want to be able to edit my user profile details so I can be identified to other users in the system
    When the Applicant is in Your details page
    Then the Applicant leaves the First name empty and enters all other details
    And an Error message is displayed for First name

Display error when Last name is blank
    [Documentation]    INFUND-1042 : As an applicant I want to be able to edit my user profile details so I can be identified to other users in the system
    When the Applicant is in Your details page
    Then the Applicant leaves the Last name empty and enters all other details
    And an Error message is displayed for Last name

Display error when Phone number is blank or contains letters
    [Documentation]    INFUND-1042 : As an applicant I want to be able to edit my user profile details so I can be identified to other users in the system
    When the Applicant is in Your details page
    And the Applicant leaves the Phone number empty and enters all other details
    Then an Error message is displayed for Phone number
    And when the applicant enters some letters(more than the max) in the phone number field
    Then the applicant should get a validation error for the phone number

*** Keywords ***
the Applicant is in Dashboard page
    Go To    ${DASHBOARD_URL}

the link to Edit Profile should be present
    Wait Until Element Is Visible    link=View and edit your profile details

the Applicant clicks the link Edit profile from Dashboard page
    Click Element    link=View and edit your profile details

the link to Edit Profile should be present in Your profile page
    Wait Until Element Is Visible    link=Edit your details

the Applicant enters the profile details
    Select From List By Index    id=title    4
    Input Text    id=firstName    Chris
    Input Text    id=lastName    Brown
    Input Text    id=phoneNumber    +-0123456789
    Click Element    css=.extra-margin

the Applicant is in Your details page
    Go To    ${EDIT_PROFILE_URL}

an Error message is displayed for First name
    Wait Until Element Is Visible    css=.error-message
    Page Should Contain    Please enter a first name

the Applicant leaves the First name empty and enters all other details
    Input Text    id=firstName    ${EMPTY}
    Input Text    id=lastName    Brown
    Input Text    id=phoneNumber    0123456789
    Click Element    css=.extra-margin

the Applicant leaves the Last name empty and enters all other details
    Input Text    id=firstName    Chris
    Input Text    id=lastName    ${EMPTY}
    Input Text    id=phoneNumber    0123456789
    Click Element    css=.extra-margin

an Error message is displayed for Last name
    Wait Until Element Is Visible    css=.error-message
    Page Should Contain    Please enter a last name

the Applicant leaves the Phone number empty and enters all other details
    Input Text    id=firstName    Chris
    Input Text    id=lastName    Brown
    Input Text    id=phoneNumber    ${EMPTY}
    Click Element    css=.extra-margin

an Error message is displayed for Phone number
    Wait Until Element Is Visible    css=.error-message
    Page Should Contain    Please enter a phone number

the Applicant should see the saved changes in Your profile page
    Page Should Contain    Chris
    Page Should Contain    Brown
    Page Should Contain    0123456789

the applicant clicks the Edit your details link
    click element    link=Edit your details

when the applicant enters some letters(more than the max) in the phone number field
    Input Text    id=firstName    Chris
    Input Text    id=lastName    Brown
    Input Text    id=phoneNumber    wewewewewewewewewewewewe
    Click Element    css=.extra-margin

the applicant should get a validation error for the phone number
    Wait Until Element Is Visible    css=.error-message
    Page Should Contain    Please enter a valid phone number
    Page Should Contain    Input for your phone number has a maximum length of 20 characters