Feature: Basic Arithmetic

  Background: A Calculator
    Given a calculator I just turned on

  Scenario: Addition
    When I add 4 and 5
    Then the result is 9

  Scenario: Subtraction
    When I subtract 7 to 2
    Then the result is 5

  Scenario: Multiplication
    When I multiply 3 and 4
    Then the result is 12

  Scenario: Invalid Operation
    When I perform an invalid operation
    Then an error should be thrown

  Scenario Outline: Several additions
    When I add <a> and <b>
    Then the result is <c>

  Examples: Single digits
    | a | b | c  |
    | 1 | 2 | 3  |
    | 3 | 7 | 10 |