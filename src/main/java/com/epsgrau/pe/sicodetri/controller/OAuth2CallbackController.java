package com.epsgrau.pe.sicodetri.controller;

import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * This class entity for tax additions. <br/>
 * <b>Class</b>: {@link OAuth2CallbackController}<br/>
 * <b>Copyright</b>: &Copy; 2024 EPS Grau. <br/>
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
 *     <li>Jul 16, 2025 (JAR) Creation class.</li>
 *     </ul>
 * @version 1.0
 */
@RestController
public class OAuth2CallbackController {

    /**
    * Callback.
    *
    * @param code Code
    * @param state State
    * @return ResponseEntity with authorization_code and state
    */
    @GetMapping("/v1/callback")
    public ResponseEntity<Map<String, String>> callback(
        @RequestParam("code") String code,
        @RequestParam("state") String state
    ) {
        return ResponseEntity.ok(Map.of(
            "authorization_code", code,
            "state", state
        ));
    }

}
