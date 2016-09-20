*** Settings ***
Documentation     INFUND-3303: As an Assessor I want the ability to reject the application after I have been given access to the full details so I can make Innovate UK aware.
Suite Setup
Suite Teardown    the user closes the browser
Force Tags        Assessor
Resource          ../../../resources/GLOBAL_LIBRARIES.robot
Resource          ../../../resources/variables/GLOBAL_VARIABLES.robot
Resource          ../../../resources/variables/User_credentials.robot
Resource          ../../../resources/keywords/Login_actions.robot
Resource          ../../../resources/keywords/User_actions.robot

*** Variables ***
${NO_OF_DAYS_LEFT}
${CURRENT_DATE}
${MILESTONE_DATE}    2016-12-31
${STARTING_DATE}
${SCREEN_NO_OF_DAYS_LEFT}

*** Test Cases ***
Assessment overview should show the expected questions
    [Documentation]    INFUND-3400
    [Tags]    HappyPath
    [Setup]    guest user log-in    paul.plum@gmail.com    Passw0rd
    When the user navigates to the page    ${Assessment_overview_9}
    Then the user should see four sections

Number of days remaining until assessment submission
    [Documentation]    INFUND-4857
    [Tags]    HappyPath    Pending
    Then The user should see the text in the page    Days left to submit
    And the assessor should see the number of days remaining
    And the days remaining should be correct

Other Assessors should not be able to access this application
    [Documentation]    INFUND-3540
    [Tags]
    [Setup]    guest user log-in    felix.wilson@gmail.com    Passw0rd
    # Note: Here Assessor-Felix rejects application 8 and paul is able to assess the application.
    When the user navigates to the page    ${Assessment_overview_11}
    Then The user should see the element    css=#content .extra-margin details summary
    And the user clicks the button/link    css=#content .extra-margin details summary
    And the user clicks the button/link    css=#details-content-0 a
    And the user fills in rejection details
    Then the user clicks the button/link    jQuery=button:contains("X")
    And the user clicks the button/link    css=#details-content-0 a
    # Then the user fills in rejection details
    And the user clicks the button/link    jquery=button:contains("Reject")
    Then The user should be redirected to the correct page    ${Assessor_competition_dashboard}
    [Teardown]    Logout as user

*** Keywords ***
the user should see four sections
    the user should see the element    css=#section-16 .bold-medium
    the user should see the element    css=#section-71 .heading-medium
    the user should see the element    css=#section-17 .heading-medium

the user fills in rejection details
    the user clicks the button/link    jquery=button:contains("Reject")
    The user should see an error    This field cannot be left blank
    Select From List By Index    id=rejectReason    1
    the user should not see an error in the page
    The user enters text to a text field    id=rejectComment    Have conflicts with the area of expertise.

the assessor should see the number of days remaining
    the user should see the element    css=.sub-header .pie-overlay .day

the days remaining should be correct
    ${CURRENT_DATE}=    Get Current Date    result_format=%Y-%m-%d    exclude_millis=true
    ${STARTING_DATE}=    Add Time To Date    ${CURRENT_DATE}    1 day    result_format=%Y-%m-%d    exclude_millis=true
    ${MILESTONE_DATE}=    Convert Date    2016-12-31    result_format=%Y-%m-%d    exclude_millis=true
    ${NO_OF_DAYS_LEFT}=    Subtract Date From Date    ${MILESTONE_DATE}    ${STARTING_DATE}    verbose    exclude_millis=true
    ${NO_OF_DAYS_LEFT}=    Remove String    ${NO_OF_DAYS_LEFT}    days
    ${SCREEN_NO_OF_DAYS_LEFT}=    Get Text    css=.sub-header .pie-overlay .day
    Should Be Equal As Numbers    ${NO_OF_DAYS_LEFT}    ${SCREEN_NO_OF_DAYS_LEFT}
