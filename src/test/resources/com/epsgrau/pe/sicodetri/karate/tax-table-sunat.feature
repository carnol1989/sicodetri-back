@integration
Feature: Integración de TaxTableSunat

  Background:
    * url baseUrl + '/api/v1/tax-tables'

  Scenario: Validar carga de datos iniciales
    When method GET
    Then status 200
    * assert response.length > 0

  Scenario: Obtener todas las tasas tributarias
    When method GET
    Then status 200
    And match response[0] contains {id: '#number', accountingAccount: '#string', description: '#string', annualRate: '#number', usefulLifeYears: '#number', status: '#string'}

  Scenario: Obtener tasa tributaria por ID (opción 1)
    Given path '1'
    And param id = 1
    And param accountingAccount = '332'
    When method GET
    Then status 200
    And match response.response contains {id: '#number', accountingAccount: '#string', description: '#string', annualRate: '#number', usefulLifeYears: '#number', status: '#string'}

  Scenario: Obtener tasa tributaria por cuenta contable (opción 2)
    Given path '2'
    And param id = 1
    And param accountingAccount = '332'
    When method GET
    Then status 200
    And match response.response contains {accountingAccount: '332', usefulLifeYears: '#number', status: '#string'}
    And match response.response.id == '#number'

  Scenario: ID inexistente devuelve 404 NOT FOUND
    Given path '1'
    And param id = 999
    And param accountingAccount = '332'
    When method GET
    Then status 404

  Scenario: Cuenta contable inexistente devuelve 404 NOT FOUND
    Given path '2'
    And param id = 1
    And param accountingAccount = '999'
    When method GET
    Then status 404

  Scenario: Opción inválida devuelve 417 EXPECTATION_FAILED
    Given path '99'
    And param id = 1
    And param accountingAccount = '332'
    When method GET
    Then status 417
