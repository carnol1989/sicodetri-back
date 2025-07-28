// src/test/resources/karate-config.js
function fn() {
  var config = {};

  // forzamos siempre "integration"
  config.env     = 'integration';

  // Karate runner (JUnit) pasa baseUrl como system-property
  var base = karate.properties['baseUrl'];
  if (!base) karate.fail('No recibí el system-property baseUrl');
  // la guardo para los features
  config.baseUrl = base;

  // manejo automático de cookies
  karate.configure('cookies', true);

  // hacemos login una sola vez
  var auth = karate.callSingle('classpath:com/epsgrau/pe/sicodetri/karate/login.feature', config);
  if (!auth.accessToken) {
    karate.fail('login.feature no devolvió accessToken');
  }
  config.accessToken = auth.accessToken;

  // inyectar el Bearer token en **todos** los escenarios
  karate.configure('headers', { Authorization: 'Bearer ' + config.accessToken });

  return config;
}