Feature: Bookstore Search Functionality

  Scenario: Search for a book by title
    Given I am on the bookstore homepage
    When I search for "Harry Potter"
    Then I should see results containing "Harry Potter and the Sorcerer's Stone"

  Scenario: Search for a book that does not exist
    Given I am on the bookstore homepage
    When I search for "Nonexistent Book"
    Then I should see no results