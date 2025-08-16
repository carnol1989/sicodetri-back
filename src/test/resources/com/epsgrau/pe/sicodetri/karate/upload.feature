Feature: Integración del endpoint /upload

  Background:
    # baseUrl viene de karate-config.js o del systemProperty que se inyecta en la IT
    * url baseUrl
    * configure cookies = { auto: true }

  Scenario: Éxito al subir un archivo Excel válido
    Given path '/api/v1/financial-fixed-assets/upload'
    And multipart file file = { read: 'classpath:data/assets.xlsx', filename: 'assets.xlsx', contentType: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' }
    And multipart field period = '2022-01-01'
    When method post
    Then status 200
    And match response.message == 'Archivo subido correctamente: assets.xlsx'
    And match response.code == '00'
    And match response.response == 2

  Scenario: Falla por formato no permitido (.txt en lugar de .xlsx)
    Given path '/api/v1/financial-fixed-assets/upload'
    And multipart file file = { read: 'classpath:data/invalid.txt', filename: 'invalid.txt', contentType: 'text/plain' }
    And multipart field period = '2022-01-01'
    When method post
    Then status 417
    And match response.message == 'Formato no permitido. Debe ser .xlsx'
    And match response.code == '01'

  Scenario: Falla al procesar un Excel vacío
    Given path '/api/v1/financial-fixed-assets/upload'
    And multipart file file = { read: 'classpath:data/empty.xlsx', filename: 'empty.xlsx', contentType: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' }
    And multipart field period = '2022-01-01'
    When method post
    Then status 200
    And match response.message == 'Archivo subido correctamente: empty.xlsx'
    And match response.code == '00'
    And match response.response == 0
