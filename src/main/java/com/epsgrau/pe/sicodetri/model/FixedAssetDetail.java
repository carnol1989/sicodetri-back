package com.epsgrau.pe.sicodetri.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * This class is a model for mapping fixed asset details. <br/>
 * <b>Class</b>: {@link FixedAssetDetail}<br/>
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
 *     <li>Ago 15, 2024 (JAR) Creation class.</li>
 *     </ul>
 * @version 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FixedAssetDetail {

    private String description;
    private String fixedAssetBrand;
    private String fixedAssetModel;
    private String fixedAssetSerialNumberAndOrPlate;

}
