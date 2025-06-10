# SICODETRI

**Authors**:       Carlos Nole

**Document Version**:  1.0.0

**Supported Spring Boot Version**: 3.2.8

### Descripción y funciones
Aplicación Web monolítica que permite realizar la conciliación de la Depreciación Tributaria vs Financiera de los Activos Fijos.

1. Subida de archivo excel (Formato 7) a la aplicación web.
2. Información del archivo excel se guarda en base de datos para el periodo indicado. Si se carga de nuevo el mismo periodo se elimina la información anterior.
3. Consulta de información guardada en base de datos.  
3.1. Opción 1: Búsqueda por periodo contable.  
3.2. Opción 2: Búsqueda por código de activo fijo principal.  
3.3. Opción 3: Búsqueda por cuenta contable.
4. Genera la proyección:  
4.1. Deducciones Temporales
4.2. Adiciones Temporales
5. CRUD del maestro de la Tabla de Tasas de SUNAT.

### Prerequisitos
Siga las siguientes instrucciones para iniciar el proyecto como ejemplo de consumo.
- JDK 17
- Maven 3.9.6
- Archivo excel que es el input para el proceso depreciación (Formato 7). Se debe tener las sgtes. consideraciones:
  - Extensión de archivo `.xlsx`.
  - Celdas con sólo valores, sin fórmulas.
  - Filas de sumatorias debe ser eliminadas.
  - Conservar la cabecera, se empieza a leer el archivo desde la fila 12.

### ¿Cómo usar?
- Construir el proyecto (Build Project)
- Hacer `clean install` con maven
- Hacer clic en `Run`
- Puede probar los sgtes. endpoints:  

**1. Subir archivo excel**:  
Request:
```bash
curl --location 'http://localhost:8082/api/v1/financial-fixed-assets/upload' \
--form 'file=@"/C:/epsgrau/Formato 7 financiero 2022.xlsx"' \
--form 'period="2022-01-01"'
```  
Response:  
```json
{
    "timestamp": "2024-09-07T23:04:16.4889082",
    "code": "00",
    "message": "Archivo subido correctamente: Formato 7 financiero 2022.xlsx",
    "response": 1,
    "exception": null
}
```
**2. Guardar información subida en la BD:**  
Request:
```bash
curl --location --request POST 'http://localhost:8082/api/v1/financial-fixed-assets?period=2022-01-01' \
--header 'Cookie: JSESSIONID=7220CAAC6F2D92F6A24D47A003C75FB4'
```
Response:
```json
{
  "timestamp": "2024-09-07T23:04:36.4697453",
  "code": "00",
  "message": "Información guardada correctamente para el periodo: 2022-01-01",
  "response": 1,
  "exception": null
}
```
**3. Obtener información guardada en la BD:**  
Request:
```bash
curl --location 'http://localhost:8082/api/v1/financial-fixed-assets/1?period=2022-01-01&mainFixedAssetCode=22.01.010862&accountingAccount=0126111003' \
--header 'Cookie: JSESSIONID=7220CAAC6F2D92F6A24D47A003C75FB4'
```
Response:
```json
{
  "timestamp": "2024-09-07T23:05:33.8916245",
  "code": "00",
  "message": "Se obtuvo informacion para el periodo: 2022-01-01",
  "response": [
    {
      "id": 1,
      "accountingPeriod": "2022-01-01",
      "mainFixedAssetCode": "22.01.010862",
      "fixedAssetCodeComponent": "",
      "fixedAssetAccountingAccount": "3321161001",
      "description": "CONSTRUCCION DE EDIFICIO PTAP CURUMUY",
      "fixedAssetBrand": "NINGUNA",
      "fixedAssetModel": "NINGUNO",
      "fixedAssetSerialNumberAndOrPlate": "/",
      "initialBalance": 27493700.6500,
      "acquisitionsAdditions": 0.0000,
      "improvements": 0.0000,
      "transfersAndOrReclassifications": 0.0000,
      "withdrawalsAndOrRetirements": 0.0000,
      "otherAdjustments": 0.0000,
      "bookValueOfFixedAssets": 27493700.6500,
      "inflationAdjustment": 0.0000,
      "adjustedValueOfFixedAssets": 27493700.6500,
      "acquisitionDate": "2007-12-31",
      "startDateFixedAssetUsage": "2007-12-31",
      "retirementDate": "1900-01-01",
      "appliedMethod": "LINEA RECTA",
      "authorizationDocumentNumber": "1.0",
      "usefulLifeYears": 30.0,
      "accumulatedDepreciationTheEndPrevious": 12830393.5700,
      "currentDepreciation": 916456.6800,
      "currentDepreciationRelatedWithdrawalsRetirements": 0.0000,
      "currentDepreciationRelatedOtherAdjustments": 0.0000,
      "accumulatedHistoricalDepreciation": 13746850.2500,
      "depreciationInflationAdjustment": 0.0000,
      "accumulatedDepreciationAdjustedInflation": 13746850.2500,
      "netFixedAssets": 13746850.4000,
      "depreciacionFinancieraList": [],
      "depreciacionTributariaList": [],
      "addedBy": "CARNOL",
      "additionDate": "2024-09-07T23:04:36.430506",
      "modifiedBy": null,
      "modificationDate": null
    }
  ],
  "exception": null
}
```
**4. Genera la proyección:**  
**4.1. Deducciones Temporales**  
**Request:**
```bash
curl --location --request POST 'http://localhost:8082/api/v1/financial-fixed-assets/projection?period=2022-01-01&account=332' \
--header 'Cookie: JSESSIONID=7220CAAC6F2D92F6A24D47A003C75FB4'
```
**Response:**
```json
{
    "timestamp": "2024-09-07T23:09:06.2970708",
    "code": "00",
    "message": "Proyección generada correctamente.",
    "response": 1,
    "exception": null
}
```
**4.2. Adiciones Temporales**  
**Request:**
```bash
curl --location --request POST 'http://localhost:8082/api/v1/financial-fixed-assets/projection?period=2023-12-01&account=ALL'
```
**Response:**
```json
{
    "timestamp": "2024-09-07T23:09:06.2970708",
    "code": "00",
    "message": "Proyección generada correctamente.",
    "response": 1,
    "exception": null
}
```
**5. Tabla de Tasas de SUNAT**
**5.1. Obtener todas las tasas tributarias:**  
**Request:**
```bash
curl --location 'http://localhost:8082/api/v1/tax-tables' \
--header 'Cookie: JSESSIONID=7220CAAC6F2D92F6A24D47A003C75FB4'
```
**Response**
```json
[
    {
        "id": 1,
        "accountingAccount": "332",
        "description": "Edificaciones y Otras Construcciones",
        "annualRate": 5,
        "status": "S"
    }
]
```
**5.2. Obtener tasa tributaria por: id, cuenta contable:**  
**Request:**
```bash
curl --location 'http://localhost:8082/api/v1/tax-tables/2?id=2&accountingAccount=332' \
--header 'Cookie: JSESSIONID=7220CAAC6F2D92F6A24D47A003C75FB4'
```
**Response:**
```json
{
    "timestamp": "2024-09-07T23:16:11.9742841",
    "code": "00",
    "message": "Se obtuvo informacion para cuenta contable: 332",
    "response": {
        "id": 1,
        "accountingAccount": "332",
        "description": "Edificaciones y Otras Construcciones",
        "annualRate": 5,
        "status": "S"
    },
    "exception": {
        "timestamp": null,
        "message": null,
        "details": null
    }
}
```

### Documentación APIs
- [API Docs (yml)](http://localhost:8082/v3/api-docs) 
- [API Docs (GUI)](http://localhost:8082/swagger-ui/index.html)

### Dependencias
| Nombre                              | Versión  | 
|-------------------------------------|----------|
| spring-boot-starter-web             | 3.2.8    |
| spring-boot-starter-test            | 3.2.8    |
| spring-boot-starter-data-jpa        | 3.2.8    |
| spring-boot-starter-data-jdbc       | 3.2.8    |
| spring-boot-starter-validation      | 3.2.8    |
| spring-boot-devtools                | 3.2.8    |
| postgresql                          | 42.7.2   |
| poi-ooxml                           | 5.3.0    |
| lombok                              | 1.18.34  |
| mockito-core                        | 5.3.1    |
| springdoc-openapi-starter-webmvc-ui | 2.3.0    |
| itext-core                          | 8.0.2    |
| jasperreports                       | 6.21.3   |
| jasperreports-fonts                 | 6.21.3   |
| guava                               | 30.1-jre |
| spotbugs-annotations                | 4.0.0    |

### Plugins
| Nombre                         | Versión | 
|--------------------------------|---------|
| spring-boot-maven-plugin       | 3.2.8   |
| jacoco-maven-plugin            | 0.8.8   |
| maven-checkstyle-plugin        | 3.3.1   |
| spotbugs-maven-plugin          | 4.8.6.6 |

### Licencia
Este proyecto es propiedad intelectual de &reg;EPS Grau. El uso sin autorización está prohibido.