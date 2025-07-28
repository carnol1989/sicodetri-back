package com.epsgrau.pe.sicodetri.controller;

import static com.epsgrau.pe.sicodetri.util.Constants.AUTHORIZATION_CODE;
import static com.epsgrau.pe.sicodetri.util.Constants.STATE;

import java.util.Map;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for handling the OAuth2 authorization code callback.
 * Exposes an endpoint to receive the authorization code and state parameters
 * returned by the OAuth2 provider after user consent.
 * <b>Class</b>: {@link Oauth2CallbackController}<br/>
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
 *     <li>Jul 21, 2025 (JAR) Creation class.</li>
 *     </ul>
 * @version 1.0
 */
@RestController
public class Oauth2CallbackController {

    /**
     * Callback endpoint to capture the authorization code and state returned
     * by the OAuth2 authorization server.
     * Returns a JSON map containing the received {@code authorization_code} and {@code state}.
     *
     * @param code  the authorization code issued by the OAuth2 provider
     * @param state the original state parameter to prevent CSRF attacks
     * @return a {@link ResponseEntity} with a map of {@code authorization_code} and {@code state}
    */
    @GetMapping(
        path = "/v1/callback",
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Map<String, String>> callback(
        @RequestParam("code") String code,
        @RequestParam("state") String state
    ) {
        return ResponseEntity.ok(Map.of(
            AUTHORIZATION_CODE, code,
            STATE, state
        ));
    }

}
