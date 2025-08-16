package com.epsgrau.pe.sicodetri.controller;

import static com.epsgrau.pe.sicodetri.util.Constants.AUTHORIZATION_CODE;
import static com.epsgrau.pe.sicodetri.util.Constants.CODE;
import static com.epsgrau.pe.sicodetri.util.Constants.CODE_VERIFIER;
import static com.epsgrau.pe.sicodetri.util.Constants.ERROR;
import static com.epsgrau.pe.sicodetri.util.Constants.GRANT_TYPE;
import static com.epsgrau.pe.sicodetri.util.Constants.REDIRECT_URI;
import static com.epsgrau.pe.sicodetri.util.Constants.REFRESH_TOKEN;
import static com.epsgrau.pe.sicodetri.util.Constants.SCOPE;
import static com.epsgrau.pe.sicodetri.util.Constants.UNSUPPORTED_GRANT_TYPE;

import com.epsgrau.pe.sicodetri.config.props.AuthorizationServerProperties;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

/**
 * REST controller responsible for handling OAuth2 token requests.
 * This controller provides two endpoints under the {@code /auth/token} path to request
 * access tokens from the configured Authorization Server using either:
 * <ul>
 *   <li>Form-encoded parameters</li>
 *   <li>JSON payload</li>
 * </ul>
 * It supports the standard {@code authorization_code} and {@code refresh_token} grant types.
 * <b>Key Features:</b>
 * <ul>
 *   <li>Uses {@link RestTemplate} to perform token requests to the Authorization Server.</li>
 *   <li>Obtains client credentials and issuer URL from {@link AuthorizationServerProperties}.</li>
 *   <li>Handles unsupported grant types with a {@code 400 Bad Request} response.</li>
 * </ul>
 *
 * <b>Security:</b>
 * <ul>
 *   <li>Client authentication is performed via HTTP Basic Authentication using configured
 *   credentials.</li>
 *   <li>Endpoints are expected to be protected at the security configuration level.</li>
 * </ul>
 *
 * <b>Class</b>: {@link AuthController}<br/>
 * <b>Copyright</b>: &copy; 2024 EPS Grau. <br/>
 * <b>Company</b>: EPS Grau. <br/>
 *
 * @author EPS Grau <br/>
 *     <u>Service Provider</u>: Consultor TI <br/>
 *     <u>Developed by</u>: <br/>
 *     <ul>
 *     <li>Carlos Augusto Nole Machaca</li>
 *     </ul>
 *     <u>Changes</u>:<br/>
 *     <ul>
 *     <li>Ago 02, 2025 (JAR) Creation class.</li>
 *     </ul>
 * @version 1.0
 */
@SuppressFBWarnings(
    value = "EI_EXPOSE_REP2",
    justification = "RestTemplate es bean gestionado por Spring; "
        + "su configuración es estable tras el arranque"
)
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final RestTemplate restTemplate;
    private final AuthorizationServerProperties props;

    /**
    * Handles token requests with {@code application/x-www-form-urlencoded} input.
    * This method expects all necessary OAuth2 parameters to be provided in the request parameters
    * and forwards them to the configured Authorization Server's token endpoint.
    *
    * @param formIn the form-encoded parameters containing OAuth2 token request data
    * @return a {@link ResponseEntity} containing the token response from the Authorization Server
    */
    @PostMapping(
        path = "/token",
        consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Map<String, Object>> tokenFormController(
        @RequestParam MultiValueMap<String, String> formIn) {
        String tokenUrl = props.provider().issuer() + "/oauth2/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(props.client().id(), props.client().secret());
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(formIn, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(tokenUrl, request, Map.class);
        return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
    }

    /**
    * Handles token requests with {@code application/json} input.
    * This method supports:
    * <ul>
    *   <li>{@code authorization_code} grant type: requires {@code code}, {@code redirect_uri},
    *       and {@code code_verifier} parameters.</li>
    *   <li>{@code refresh_token} grant type: requires {@code refresh_token} parameter.</li>
    * </ul>
    * If the provided {@code grant_type} is not supported, a {@code 400 Bad Request} response
    * is returned with an {@code unsupported_grant_type} error.
    *
    * @param payload the JSON payload containing OAuth2 token request data
    * @return a {@link ResponseEntity} containing the token response from the Authorization Server
    */
    @PostMapping(
        path = "/token",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Map<String, Object>> tokenJsonController(
        @RequestBody Map<String, String> payload) {
        String grantType   = payload.getOrDefault(GRANT_TYPE, AUTHORIZATION_CODE);

        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(props.client().id(), props.client().secret());
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        if (AUTHORIZATION_CODE.equals(grantType)) {
            form.add(GRANT_TYPE,    AUTHORIZATION_CODE);
            form.add(CODE,          payload.get(CODE));
            form.add(REDIRECT_URI,  payload.get(REDIRECT_URI));
            form.add(CODE_VERIFIER, payload.get(CODE_VERIFIER));
            form.add(SCOPE, String.join(" ", props.client().scopes()));
        } else if (REFRESH_TOKEN.equals(grantType)) {
            form.add(GRANT_TYPE,     REFRESH_TOKEN);
            form.add(REFRESH_TOKEN,  payload.get(REFRESH_TOKEN));
        } else {
            return ResponseEntity.badRequest().body(Map.of(ERROR, UNSUPPORTED_GRANT_TYPE));
        }

        String tokenUrl    = props.provider().issuer() + "/oauth2/token";
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(form, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(tokenUrl, request, Map.class);
        return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
    }

}
