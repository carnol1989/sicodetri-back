package com.epsgrau.pe.sicodetri.model;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Model representing the detailed information of a fixed asset.
 * Contains descriptive attributes such as description, brand, model, and
 * serial number or plate, used for transfer between layers or API responses.
 * <b>Class</b>: {@link FixedAssetDetail}<br/>
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
 *     <li>Jun 06, 2025 (JAR) Creation class.</li>
 *     </ul>
 * @version 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FixedAssetDetail implements Serializable {

    private static final long serialVersionUID = 1L;

    private String description;
    private String fixedAssetBrand;
    private String fixedAssetModel;
    private String fixedAssetSerialNumberAndOrPlate;

}
