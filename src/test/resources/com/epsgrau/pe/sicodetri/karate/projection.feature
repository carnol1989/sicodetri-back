Feature: Integración del endpoint /projection

  Background:
    * url baseUrl
    * configure cookies = { auto: true }
    # limpiar la base antes de cada escenario
    Given path '/api/v1/test/cleanup'
    When method post
    Then status 200

  Scenario: Éxito – proyección temporal por cuenta 332
    # 1) upload + save
    Given path '/api/v1/financial-fixed-assets/upload'
    And multipart file file = { read: 'classpath:data/assets.xlsx', filename: 'assets.xlsx', contentType: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' }
    And multipart field period = '2022-01-01'
    When method post
    Then status 200

    Given path '/api/v1/financial-fixed-assets'
    And param period = '2022-01-01'
    When method post
    Then status 201

    # 2) projection TEMPORARY_DEDUCTIONS / cuenta 332
    Given path '/api/v1/financial-fixed-assets/projection'
    And param type = 'TEMPORARY_DEDUCTIONS'
    And param period = '2022-01-01'
    And param account = '332'
    When method post
    Then status 201
    And match response.code == '00'
    And match response.response == 2

  Scenario: Éxito – proyección temporal para ALL
    # 1) upload + save (de nuevo, porque hicimos cleanup)
    Given path '/api/v1/financial-fixed-assets/upload'
    And multipart file file = { read: 'classpath:data/assets.xlsx', filename: 'assets.xlsx', contentType: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' }
    And multipart field period = '2022-01-01'
    When method post
    Then status 200

    Given path '/api/v1/financial-fixed-assets'
    And param period = '2022-01-01'
    When method post
    Then status 201

    # 2) projection TEMPORARY_DEDUCTIONS / ALL
    Given path '/api/v1/financial-fixed-assets/projection'
    And param type = 'TEMPORARY_DEDUCTIONS'
    And param period = '2022-01-01'
    And param account = 'ALL'
    When method post
    Then status 201
    And match response.code == '00'
    And match response.response == 2

  Scenario: Error – sin registros para el periodo
    Given path '/api/v1/financial-fixed-assets/projection'
    And param type = 'TEMPORARY_DEDUCTIONS'
    And param period = '2022-01-02'
    And param account = '332'
    When method post
    Then status 404
    And match response.code == '01'
    And match response.message contains 'Error al generar proyección. No se encontraron registros.'

  Scenario: Error – formato de fecha inválido
    Given path '/api/v1/financial-fixed-assets/projection'
    And param type = 'TEMPORARY_DEDUCTIONS'
    And param period = '01-2022-01'
    And param account = '332'
    When method post
    Then status 400
    And match response.code == '01'
    And match response.message == "Formato de fecha inválido. Use 'YYYY-MM-DD'."

  Scenario: Éxito – proyección temporary additions para cuenta 332
    # 1) upload + save de assets-2023.xlsx
    Given path '/api/v1/financial-fixed-assets/upload'
    And multipart file file = { read: 'classpath:data/assets-2023.xlsx', filename: 'assets-2023.xlsx', contentType: 'application/vnd-openxmlformats-officedocument.spreadsheetml.sheet' }
    And multipart field period = '2023-12-01'
    When method post
    Then status 200

    Given path '/api/v1/financial-fixed-assets'
    And param period = '2023-12-01'
    When method post
    Then status 201

    # 2) projection TEMPORARY_ADDITIONS
    Given path '/api/v1/financial-fixed-assets/projection'
    And param type = 'TEMPORARY_ADDITIONS'
    And param period = '2023-12-01'
    And param account = 'ALL'
    When method post
    Then status 201
    And match response.code == '00'
    And match response.response == 1

  Scenario: Éxito – proyección permanent additions para activos elegibles
    # 1) upload + save de assets.xlsx
    Given path '/api/v1/financial-fixed-assets/upload'
    And multipart file file = { read: 'classpath:data/assets.xlsx', filename: 'assets.xlsx', contentType: 'application/vnd-openxmlformats-officedocument-spreadsheetml.sheet' }
    And multipart field period = '2022-01-01'
    When method post
    Then status 200

    Given path '/api/v1/financial-fixed-assets'
    And param period = '2022-01-01'
    When method post
    Then status 201

    # 2) projection PERMANENT_ADDITIONS
    Given path '/api/v1/financial-fixed-assets/projection'
    And param type = 'PERMANENT_ADDITIONS'
    And param period = '2022-01-01'
    And param account = '332'
    When method post
    Then status 201
    And match response.code == '00'
    And match response.response == 2

  Scenario: Error – permanent additions sin activos elegibles
    Given path '/api/v1/financial-fixed-assets/projection'
    And param type = 'PERMANENT_ADDITIONS'
    And param period = '2025-01-01'
    And param account = '332'
    When method post
    Then status 404
    And match response.code == '01'
    And match response.message contains 'Error al generar proyección. No se encontraron registros.'
