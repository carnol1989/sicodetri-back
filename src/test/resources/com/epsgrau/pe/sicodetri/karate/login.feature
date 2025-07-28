Feature: Obtener access token para Karate

  Background:
    * url baseUrl
    * configure cookies = true
    * configure followRedirects = false

    # helper de Java para Base64
    * def Base64 = Java.type('java.util.Base64')
    * def basicAuth =
    """
    function(u,p){
      // construye el header Basic
      return 'Basic ' +
        Base64.getEncoder().encodeToString((u + ':' + p).getBytes());
    }
    """

  Scenario: login + authorize + token
    # 1) GET /login → obtenemos cookie y HTML con <input _csrf>
    Given path 'login'
    And header Accept = 'text/html'
    When method get
    Then status 200
    # extraemos el token del formulario y de la cookie
    * def html = response
    * match html.contains('<input') == true
    * def m = html.match(/<input[^>]*name=['"]_csrf['"][^>]*value=['"]([^'"]+)['"][^>]*>/)
    * assert m != null
    * def csrf = m[1]
    * eval karate.log('_csrf=',csrf)
    * def xsrf = responseCookies['XSRF-TOKEN'].value
    * eval karate.log('xsrf=',xsrf)

    # 2) POST /login → enviamos form + cabecera CSRF + cookies
    Given path 'login'
    And header Content-Type = 'application/x-www-form-urlencoded'
    And cookie XSRF-TOKEN   = xsrf
    And form field _csrf    = csrf
    And form field username = 'katherine_gutierrez'
    And form field password = 'Password123!'
    When method post
    Then status 302
    * def jSession = responseCookies['JSESSIONID'].value
    * eval karate.log('jSession=',jSession)
    * def xsrf = responseCookies['XSRF-TOKEN'].value
    * eval karate.log('xsrf=',xsrf)

    # 3) GET /oauth2/authorize → obtenemos JSON con code
    * configure followRedirects = true
    Given path 'oauth2/authorize'
    And cookie JSESSIONID   = jSession
    And cookie XSRF-TOKEN   = xsrf
    And param response_type = 'code'
    And param client_id     = 'sicodetriapp'
    And param redirect_uri  = baseUrl + '/v1/callback'
    And param scope         = 'openid roles'
    And param state         = 'estado123'
    When method get
    Then status 200
    * def authResp = response
    * def code    = authResp.authorization_code
    * eval karate.log('authorization_code=',code)

    # 4) POST /oauth2/token → canjeamos el code por tokens
    Given url baseUrl + '/oauth2/token'
    And header Content-Type  = 'application/x-www-form-urlencoded'
    And header Accept = 'application/json'
    And cookie JSESSIONID     = jSession
    And cookie XSRF-TOKEN     = xsrf
    And form field grant_type    = 'authorization_code'
    And form field code          = code
    And form field redirect_uri  = baseUrl + '/v1/callback'
    And form field client_id     = 'sicodetriapp'
    And form field client_secret = 'eps2025'
    When method post
    Then status 200
    * def token = response
    # devolvemos el access_token para karate-config.js
    * def accessToken = token.access_token
    * match accessToken != null
    * eval karate.log('access_token=',accessToken)
    # asignamos la variable de salida
    * karate.set('accessToken', accessToken)
