package com.epsgrau.pe.sicodetri.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FixedAssetDetail {

    private String description;
    private String fixedAssetBrand;
    private String fixedAssetModel;
    private String fixedAssetSerialNumberAndOrPlate;

}
