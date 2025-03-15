Feature: Search Books

  Scenario: Search books by author
    Given the following books exist in the library
      | title      | author   | published   |
      | Book One   | Author A | 2023-01-01  |
      | Book Two   | Author B | 2024-02-01  |
      | Book Three | Author A | 2025-03-01  |
    When the customer searches for books by author 'Author A'
    Then 2 books should have been found
    And Book 1 should have the title 'Book One'
    And Book 2 should have the title 'Book Three'

  Scenario: Search books by publication date range
    Given the following books exist in the library
      | title      | author   | published   |
      | Book One   | Author A | 2023-01-01  |
      | Book Two   | Author B | 2024-02-01  |
      | Book Three | Author A | 2025-03-01  |
    When the customer searches for books published between '2023-01-01' and '2024-12-31'
    Then 2 books should have been found
    And Book 1 should have the title 'Book One'
    And Book 2 should have the title 'Book Two'