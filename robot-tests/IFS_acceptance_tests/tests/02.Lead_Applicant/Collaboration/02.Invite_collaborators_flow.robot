*** Settings ***
Documentation     INFUND-901: As a lead applicant I want to invite application contributors to collaborate with me on the application, so that they can contribute to the application in a collaborative competition
...
...
...               INFUND-896: As a lead applicant i want to invite partner organisations to collaborate on line in my application, so that i can create the consortium needed to complete the proposed project
...
...
...               INFUND-928: As a lead applicant i want a separate screen within the application form, so that i can invite/track partners/contributors throughout the application process
...
...
...               INFUND-929: As a lead applicant i want to be able to have a separate screen, so that i can invite contributors to the application
Suite Setup       Login as User    &{lead_applicant_credentials}
Suite Teardown    TestTeardown User closes the browser
Force Tags        Create new application    collaboration
Resource          ../../../resources/GLOBAL_LIBRARIES.robot
Resource          ../../../resources/variables/GLOBAL_VARIABLES.robot
Resource          ../../../resources/variables/User_credentials.robot
Resource          ../../../resources/keywords/Login_actions.robot
Resource          ../../../resources/keywords/Applicant_actions.robot

*** Variables ***
${INVITE_COLLABORATORS_PAGE}    ${SERVER}/application/1/contributors/invite?newApplication
${INVITE_COLLABORATORS2_PAGE}    ${SERVER}/application/2/contributors/invite?newApplication
${INVITE_COLLABORATORS_PAGE}    ${SERVER}/application/1/contributors/invite?newApplication
${APPLICATION_TEAM_PAGE}    ${SERVER}/application/1/contributors

*** Test Cases ***
Valid invitation submit
    [Documentation]    INFUND-901
    [Tags]  HappyPath
    Given the applicant is in the invite contributors page
    When the applicant enters valid inputs
    Then the applicant should be redirected to the overview page
    And the invite notification should be visible

Lead applicant can access the Application team page(Link in the overview page)
    [Documentation]    INFUND-928
    [Tags]      HappyPath
    Given Applicant goes to the Overview page
    When Lead Applicant clicks link "View team members and add collaborators"
    Then Lead Applicant should be redirected to the Application team page
    Then Lead Applicant should have the correct status

Status of the invited people(Application team page)
    [Documentation]    INFUND-929
    [Tags]      HappyPath
    Given the applicant goes to the application 1 team page
    Then The status of the Invited people should be correct in the application team page

Status of the invited people(Manage contributors page)
    [Documentation]    Infund-928
    [Tags]      HappyPath
    Given Application goes to the Application Team page
    And Applicant clicks on "Invite new contributors"
    Then the status of the people should be correct in the Manage contributors page

The Lead Applicant can add new collaborators
    [Documentation]    INFUND-928
    [Tags]      HappyPath
    Given Application goes to the Application Team page
    And Applicant clicks on "Invite new contributors"
    And the applicant clicks the add person link
    When the user adds new collaborator
    And the applicant can enter Organisation name, Name and E-mail
    And Applicant clicks on "Save Changes"
    Then Applicant is redirected to Application Team page

Verify the invited collaborators are not editable
    [Documentation]    INFUND-929
    [Tags]
    Given Application goes to the Application Team page
    and Applicant clicks on "Invite new contributors"
    Then the invited collaborators are not editable

Pending collaborators should not be available in the assign list
    [Documentation]    INFUND-928
    [Tags]
    Given Applicant goes to the 'Public description' question
    Then the applicant should not be able to assign the question to the users that still pending the invite    tester

*** Keywords ***
the applicant is in the invite contributors page
    go to    ${INVITE_COLLABORATORS_PAGE}

the applicant enters valid inputs
    click element    jquery=button:contains('Add person')
    Input Text    css=li:nth-child(1) tr:nth-of-type(2) td:nth-of-type(1) input    tester
    Input Text    css=li:nth-child(1) tr:nth-of-type(2) td:nth-of-type(2) input    test@example.com
    Click Element    jquery=li:nth-last-child(1) button:contains('Add partner organisation')
    Input Text    name=organisations[1].organisationName    Fannie May
    Input Text    css=li:nth-child(2) tr:nth-of-type(1) td:nth-of-type(1) input    Collaborator 2
    Input Text    css=li:nth-child(2) tr:nth-of-type(1) td:nth-of-type(2) input    collaborator2@fanniemay.com
    Click Element    jquery=li:nth-child(2) button:contains('Add person')
    Input Text    css=li:nth-child(2) tr:nth-of-type(2) td:nth-of-type(1) input    Collaborator 3
    Input Text    css=li:nth-child(2) tr:nth-of-type(2) td:nth-of-type(2) input    collaborator3@fanniemay.com
    focus    jquery=li:nth-child(2) button:contains('Add person')
    Sleep    1s
    Click Element    jquery=button:contains("Begin application")

the applicant should be redirected to the overview page
    Wait Until Page Contains    Application overview

Lead Applicant clicks link "View team members and add collaborators"
    page should contain link    View team members and add collaborators
    click link    View team members and add collaborators

Lead Applicant should be redirected to the Application team page
    page should contain    Application team
    Page Should Contain    View and manage your partner companies and individuals contributing to the application. If a partner is ‘pending’ they have not yet confirmed their role within this project. Click on a name to send this person an email

Lead Applicant should have the correct status
    page should contain element    css=#content h2.heading-medium
    ${input_value} =    get text    css=#content h2.heading-medium
    Should Be Equal As Strings    ${input_value}    Empire Ltd (Lead organisation)
    page should contain link    Steve Smith
    ${input_value} =    get text    css=.list-bullet li small
    Should Be Equal As Strings    ${input_value}    (Lead Applicant)

Applicant clicks on "Invite new contributors"
    click Element    css=a.button
    wait until page contains    Manage Contributors

the applicant clicks the add person link
    Click Element    jquery=li:nth-child(1) button:contains('Add person')

the user adds new collaborator
    Wait Until Element Is Visible    css=li:nth-child(1) tr:nth-of-type(3) td:nth-of-type(1)
    Input Text    css=li:nth-child(1) tr:nth-of-type(3) td:nth-of-type(1) input    Roger Axe
    Input Text    css=li:nth-child(1) tr:nth-of-type(3) td:nth-of-type(2) input    roger.axe@gmail.com
    focus    jquery=li:nth-child(1) button:contains('Add person')
    sleep    1s

Applicant clicks on "Save Changes"
    Click Element    css=.contributorsForm button.button
    Sleep    1s

Applicant is redirected to Application Team page
    Location Should be    ${APPLICATION_TEAM_URL}

the applicant can enter Organisation name, Name and E-mail
    Click Element    jquery=li:nth-last-child(1) button:contains('Add partner organisation')
    Input Text    name=organisations[2].organisationName    Z Ltd
    Input Text    css=li:nth-child(3) tr:nth-of-type(1) td:nth-of-type(1) input    Elvis Furcic
    Input Text    css=li:nth-child(3) tr:nth-of-type(1) td:nth-of-type(2) input    elvis.furcic@gmail.com
    focus    jquery=li:nth-child(2) button:contains('Add person')
    Sleep    2s

the applicant goes to the application 1 team page
    GO TO    ${APPLICATION_TEAM_PAGE}

The status of the Invited people should be correct in the application team page
    Element Should Contain    css=#content ul li:nth-child(1)    (Lead Applicant)
    Element Should Contain    css=#content ul li:nth-child(2)    (pending)
    Element Should Contain    css=p+ div .heading-medium small    (Lead organisation)
    Element Should Contain    css=div+ div .heading-medium small    (pending)

the invited collaborators are not editable
    #Element Should Contain    css=li:nth-child(2) tr:nth-of-type(2) td:nth-of-type(3)    (pending)
    page should contain element    jQuery=li:nth-child(1) tr:nth-of-type(1) td:nth-child(1) [readonly]
    page should contain element    jQuery=li:nth-child(1) tr:nth-of-type(1) td:nth-child(2) [readonly]
    page should contain element    jQuery=li:nth-child(2) tr:nth-of-type(1) td:nth-child(1) [readonly]
    page should contain element    jQuery=li:nth-child(2) tr:nth-of-type(1) td:nth-child(2) [readonly]
    page should contain element    jQuery=li:nth-child(3) tr:nth-of-type(1) td:nth-child(2) [readonly]
    page should contain element    jQuery=li:nth-child(3) tr:nth-of-type(1) td:nth-child(1) [readonly]

the applicant should not be able to assign the question to the users that still pending the invite
    [Arguments]    ${assignee_name}
    Click Element    css=#form-input-12 .assign-button button
    Page Should Not Contain Element    xpath=//div[@id="form-input-12"]//button[contains(text(),"${assignee_name}")]

the invite notification should be visible
    Wait Until Element Is Visible    css=.event-alert
    page should contain    Invites send

the status of the people should be correct in the Manage contributors page
    Element Should Contain    css=li:nth-child(1) tr:nth-of-type(1) td:nth-child(3)    That's you!
    Element Should Contain    css=li:nth-child(1) tr:nth-of-type(2) td:nth-child(3)    (pending)
    Element Should Contain    css=li:nth-child(2) tr:nth-of-type(1) td:nth-child(3)    (pending)