Feature: Google Homepage
  Background:
  Given I launch "CHROME" browser in "UAT" environment using "Objects.yaml" Object Repository




  Scenario: Google Search Naveen
    Given I open "Google_HomepageURL" page
    When I search for "Naveen Kumar K" in Google homepage
    Then I should get results with "Naveen Kumar K"

  Scenario: Google Search
    Given I open "Google_HomepageURL" page
    When I search for "Lokesh Kumar K" in Google homepage
    Then I should get results with "Lokesh Kumar K"