Feature: Bewakoof adding Products to cart

  Background:
    Given I launch "CHROME" browser in "UAT" environment using "Objects.yaml" Object Repository

  Scenario Outline: Adding item to cart in Bewakoof
    Given I open "Bewakoof_HomepageURL" page
    And Login to Bewakoof using "<username>" and "<password>"
    When Navigate to "<category>" category
    And Add first item to cart with size "<size>"
    Then verify product added in cart with correct "<size>"
    Examples:
      | username   | password | category | size |
      | 9700772866 | 19041992 | Hoodies  | M    |
