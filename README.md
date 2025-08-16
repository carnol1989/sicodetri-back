# SICODETRI

**Authors**:       Carlos Nole

**Document Version**:  1.0.0

**Supported Spring Boot Version**: 3.2.8

### Descripción y funciones
Aplicación Web monolítica que permite realizar la conciliación de la Depreciación Tributaria vs Financiera de los Activos Fijos.

1. Login (OAuth + JWT)
1.1. Paso 1: GET /login → capturar CSRF y Cookie  
1.2. Paso 2: POST /login → autenticar usuario (TaxTableViewer)  
1.3. Paso 3: Solicitar el código de autorización  
1.4. Paso 4: Canjear el código por tokens
2. Subida de archivo excel (Formato 7) a la aplicación web.
3. Información del archivo excel se guarda en base de datos para el periodo indicado. Si se carga de nuevo el mismo periodo se elimina la información anterior.
4. Consulta de información guardada en base de datos.  
4.1. Opción 1: Búsqueda por periodo contable.  
4.2. Opción 2: Búsqueda por código de activo fijo principal.  
4.3. Opción 3: Búsqueda por cuenta contable.
5. Genera la proyección:  
5.1. Deducciones Temporales  
5.2. Adiciones Temporales  
5.3. Adiciones Permanentes
6. Generación de reportes  
6.1. Reporte de Libro Tributario  
6.2. Reporte de Detalle Activo Fijo
7. CRUD del maestro de la Tabla de Tasas de SUNAT  
7.1. Obtener todas las tasas tributarias  
7.2. Obtener tasa tributaria por: id, cuenta contable

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

**1. Login (OAuth + JWT)**:  
**1.1. Paso 1: GET /login → capturar CSRF y Cookie**  
**Request:**
```bash
curl --location 'http://localhost:8082/login' \
--header 'Accept: text/html' \
--header 'User-Agent: PostmanRuntime/7.32.2'
```  
**Response:**
```html
<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <meta name="description" content="">
    <meta name="author" content="">
    <title>Please sign in</title>
    <link href="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0-beta/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-/Y6pD6FV/Vv2HJnA6t+vslU6fwYXjCFtcEpHbNJ0lyAFsXTsjBbfaDjzALeQsN6M" crossorigin="anonymous">
    <link href="https://getbootstrap.com/docs/4.0/examples/signin/signin.css" rel="stylesheet" integrity="sha384-oOE/3m0LUMPub4kaC09mrdEhIc+e3exm4xOGxAmuFXhBNF4hcg/6MiAXAf5p0P56" crossorigin="anonymous"/>
  </head>
  <body>
     <div class="container">
      <form class="form-signin" method="post" action="/login">
        <h2 class="form-signin-heading">Please sign in</h2>
        <p>
          <label for="username" class="sr-only">Username</label>
          <input type="text" id="username" name="username" class="form-control" placeholder="Username" required autofocus>
        </p>
        <p>
          <label for="password" class="sr-only">Password</label>
          <input type="password" id="password" name="password" class="form-control" placeholder="Password" required>
        </p>
<input name="_csrf" type="hidden" value="9xKVnSXokfSFN8GUPHdTEefCalFh8lUNNeBFTxQ754cwwOVRkSP3rECJ85GoVqSmXlpncNChR2hYkGYgUYF0KiwK0bEJpdxh" />
        <button class="btn btn-lg btn-primary btn-block" type="submit">Sign in</button>
      </form>
</div>
</body></html>
```
**1.2. Paso 2: POST /login → autenticar usuario (TaxTableViewer)**  
**Request:**
```bash
curl --location 'http://localhost:8082/login' \
--header 'Content-Type: application/x-www-form-urlencoded' \
--header 'Cookie: JSESSIONID=709881FFCD5474F6F144CFAA3B2FE42E; XSRF-TOKEN=c5eb4b37-9055-4160-a3a4-161176427e95' \
--data-urlencode '_csrf=9xKVnSXokfSFN8GUPHdTEefCalFh8lUNNeBFTxQ754cwwOVRkSP3rECJ85GoVqSmXlpncNChR2hYkGYgUYF0KiwK0bEJpdxh' \
--data-urlencode 'username=tax_viewer' \
--data-urlencode 'password=Password123!'
```  
**Response:**
```html
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <meta name="description" content="">
    <meta name="author" content="">
    <title>Please sign in</title>
    <link href="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0-beta/css/bootstrap.min.css" rel="stylesheet"
        integrity="sha384-/Y6pD6FV/Vv2HJnA6t+vslU6fwYXjCFtcEpHbNJ0lyAFsXTsjBbfaDjzALeQsN6M" crossorigin="anonymous">
    <link href="https://getbootstrap.com/docs/4.0/examples/signin/signin.css" rel="stylesheet"
        integrity="sha384-oOE/3m0LUMPub4kaC09mrdEhIc+e3exm4xOGxAmuFXhBNF4hcg/6MiAXAf5p0P56" crossorigin="anonymous" />
</head>
<body>
    <div class="container">
        <form class="form-signin" method="post" action="/login">
            <h2 class="form-signin-heading">Please sign in</h2>
            <p>
                <label for="username" class="sr-only">Username</label>
                <input type="text" id="username" name="username" class="form-control" placeholder="Username" required autofocus>
            </p>
            <p>
                <label for="password" class="sr-only">Password</label>
                <input type="password" id="password" name="password" class="form-control" placeholder="Password" required>
            </p>
            <input name="_csrf" type="hidden" value="s0JDBu_Dq42hrIncTk-9Vi-2X9_8TPKW9hYd04oz7iSyts7J0HcmZNuhmLqMlbnpe2KJZxmGcr7PLca7xyAs4r0F2haF0_f8" />
            <button class="btn btn-lg btn-primary btn-block" type="submit">Sign in</button>
        </form>
    </div>
</body>
</html>
```
**1.3. Paso 3: Solicitar el código de autorización**
**Request:**
```bash
curl --location --request GET 'http://localhost:8082/oauth2/authorize?response_type=code&client_id=sicodetriapp&redirect_uri=http%3A%2F%2Flocalhost%3A8082%2Fv1%2Fcallback&scope=openid%20roles&state=miEstadoAleatorio' \
--header 'Cookie: JSESSIONID=<tu-session-id>; XSRF-TOKEN=<tu-xsrf-token>; JSESSIONID=709881FFCD5474F6F144CFAA3B2FE42E; XSRF-TOKEN=c5eb4b37-9055-4160-a3a4-161176427e95' \
--header 'Content-Type: application/x-www-form-urlencoded' \
--data-urlencode 'response_type=code' \
--data-urlencode 'client_id=sicodetriapp' \
--data-urlencode 'redirect_uri=http://localhost:8082/v1/callback' \
--data-urlencode 'scope=openid roles' \
--data-urlencode 'state=miEstadoAleatorio'
```  
**Response:**
```json
{
    "state": "miEstadoAleatorio",
    "authorization_code": "eh-BjRFPqGaCZX8u6Lreh_AwO1H7_awuYEhjmC0GemGnENALoXs4HvM7uwW3QcffOJTTCv-u7psGU7sM3S_DwzcdBSm8tcqcpRpyQLUylKtH0vO3Dsb8u1ylxl1qDwoq"
}
```
**1.4. Paso 4: Canjear el código por tokens**
**Request:**
```bash
curl --location 'http://localhost:8082/oauth2/token' \
--header 'Content-Type: application/x-www-form-urlencoded' \
--header 'Accept: application/json' \
--header 'Authorization: Basic c2ljb2RldHJpYXBwOmVwczIwMjU=' \
--header 'Cookie: JSESSIONID=709881FFCD5474F6F144CFAA3B2FE42E; XSRF-TOKEN=c5eb4b37-9055-4160-a3a4-161176427e95' \
--data-urlencode 'grant_type=authorization_code' \
--data-urlencode 'code=eh-BjRFPqGaCZX8u6Lreh_AwO1H7_awuYEhjmC0GemGnENALoXs4HvM7uwW3QcffOJTTCv-u7psGU7sM3S_DwzcdBSm8tcqcpRpyQLUylKtH0vO3Dsb8u1ylxl1qDwoq' \
--data-urlencode 'redirect_uri=http://localhost:8082/v1/callback'
```  
**Response:**
```json
{
    "access_token": "eyJraWQiOiIxZDk1NWY4OC1iYWJhLTQ5M2YtOWNmNC05MDQ3NGNiMGNmYWEiLCJhbGciOiJSUzI1NiJ9.eyJzdWIiOiJ0YXhfdmlld2VyIiwiYXVkIjoic2ljb2RldHJpYXBwIiwibmJmIjoxNzUzNzU0ODIzLCJzY29wZSI6WyJvcGVuaWQiLCJyb2xlcyJdLCJyb2xlcyI6WyJUYXhUYWJsZVZpZXdlciJdLCJpc3MiOiJodHRwOi8vbG9jYWxob3N0OjgwODIiLCJleHAiOjE3NTM3NTU3MjMsImlhdCI6MTc1Mzc1NDgyM30.LHmfVqdGT5oT-XxvfCASeHBjTm28-nOUDiacNZHP1PyPRSGbYEf30x3gLiboCPwk98cjKftNMTYA68LacGIzIucELSaS4yg0sTTEycFK9Udb668kETnFzvc8iOr18p3G1WvyJOpDz81epSnqBNxJHem-Ljcbn9v-GEeT_knutqcZyaKIQLkP2FscG0Xg8r3Rvzdy9xX0VQvEe426xkbySio8Ond68ULIA383F8oOQjfY6uXaczfVwUycvNQIOV_3P2qJiGDrL5vsVeMAAYucg_M1lQ7F3RS393ytbv8GinWjTTK1G7m5Uq5Eultg94rz-P2zyh6d4prU0RageY15Qw",
    "refresh_token": "YtsgLmQkdMowQyrKkFtoFLZN6aSBXJb7JrnNUONYr_9ZVpFSi_9iWhu5IE87XSp6-8xN103fLhs9KY1wuspH0BQiV6xrNhbyWhRxG96ywF480EY-h7Wz97oJcOyb2gIh",
    "scope": "openid roles",
    "id_token": "eyJraWQiOiIxZDk1NWY4OC1iYWJhLTQ5M2YtOWNmNC05MDQ3NGNiMGNmYWEiLCJhbGciOiJSUzI1NiJ9.eyJzdWIiOiJ0YXhfdmlld2VyIiwiYXVkIjoic2ljb2RldHJpYXBwIiwiYXpwIjoic2ljb2RldHJpYXBwIiwiYXV0aF90aW1lIjoxNzUzNzU0NzgzLCJpc3MiOiJodHRwOi8vbG9jYWxob3N0OjgwODIiLCJleHAiOjE3NTM3NTY2MjQsImlhdCI6MTc1Mzc1NDgyNCwic2lkIjoiQlhsZGV2UTFGR2V1NVUxa0pNaURrNUhOMnJfRjFpOVRldExuckstVDRNWSJ9.f5rp_V6l5hi_a1Viy7Zor3HJ-VqHwdPbe6k6kD8Pp0GNF4v-8X6sUkaw2xAcDSou5eRqEoVlDREAcHqSOYoouGnx9x1WLiFFOBZ5HOWoIlNIjyXSk45wbc9gdljzblAjAuKcnHVZLrqiFHEUfHR2QWup7z9SDHXE0bWyUAN0ymFoDFSQiO2TneOVjcZMAF5RKS3GD4wrh8tAN3um440npw94xGXePfWjshVkK6a0l8gQtUukvmIishHS7liVu5IzyHSFvXKI0JR1t_YcqUwpWpTignOsUbb8n8Xe5kVeOoedXkeLHmjy-GQtA6zlogdUE_Qj2Puu7XccsgBxytwbWQ",
    "token_type": "Bearer",
    "expires_in": 900
}
```
**2. Subir archivo excel**:  
**Request:**
```bash
curl --location 'http://localhost:8082/api/v1/financial-fixed-assets/upload' \
--header 'Authorization: Bearer eyJraWQiOiIxZDk1NWY4OC1iYWJhLTQ5M2YtOWNmNC05MDQ3NGNiMGNmYWEiLCJhbGciOiJSUzI1NiJ9.eyJzdWIiOiJrYXRoZXJpbmVfZ3V0aWVycmV6IiwiYXVkIjoic2ljb2RldHJpYXBwIiwibmJmIjoxNzUzNzU0OTkzLCJzY29wZSI6WyJvcGVuaWQiLCJyb2xlcyJdLCJyb2xlcyI6WyJBc3NldFVwbG9hZGVyIiwiRGVwcmVjaWF0aW9uUHJvY2Vzc29yIiwiUmVwb3J0R2VuZXJhdG9yIiwiVGF4VGFibGVWaWV3ZXIiXSwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDo4MDgyIiwiZXhwIjoxNzUzNzU1ODkzLCJpYXQiOjE3NTM3NTQ5OTN9.JcASD9_OWRPH2S90Ip9gnl2vghFZL1wikCaZP-kaUkNB3bu4xjnuYsCCCrfQe_-txD9UC4foZHcBUu7e6SzKoxm34rZHgZHU0-h_wn9ub0cnLY4Gz0Rd26lFuzW0TGIeekLAvfUlpjtmCp7rDSiFr8jul082L5MLRD2WnXcizPc0Mn1HqJMTyzhy58nzYTyFVQy-SSl8oKbNbnJuE7Dl6ycjC3keXcCWfd1l07vOI93utkxY7kSQx_JSC2M_i6XDLci1Ie1ZvhPfWhZoqBad26OGSGPa4mhILIKMojMbiAFj3RcvzrX_6-2YnrG-7gubhpVDGhekFGbJZMExd4K5Vg' \
--header 'Cookie: JSESSIONID=CECA7BA911523FA03FAE281971BCE844; XSRF-TOKEN=ec66bdd4-6b09-4e18-b7ac-8a0656d333b4' \
--form 'file=@"/C:/epsgrau/Formato 7 financiero 2022.xlsx"' \
--form 'period="2022-01-01"'
```  
**Response:**  
```json
{
  "timestamp": "2025-07-28T21:10:07.981097",
  "code": "00",
  "message": "Archivo subido correctamente: Formato 7 financiero 2022.xlsx",
  "response": 2,
  "exception": null
}
```
**3. Guardar información subida en la BD:**  
**Request:**
```bash
curl --location --request POST 'http://localhost:8082/api/v1/financial-fixed-assets?period=2022-01-01' \
--header 'Authorization: Bearer eyJraWQiOiIxZDk1NWY4OC1iYWJhLTQ5M2YtOWNmNC05MDQ3NGNiMGNmYWEiLCJhbGciOiJSUzI1NiJ9.eyJzdWIiOiJrYXRoZXJpbmVfZ3V0aWVycmV6IiwiYXVkIjoic2ljb2RldHJpYXBwIiwibmJmIjoxNzUzNzU0OTkzLCJzY29wZSI6WyJvcGVuaWQiLCJyb2xlcyJdLCJyb2xlcyI6WyJBc3NldFVwbG9hZGVyIiwiRGVwcmVjaWF0aW9uUHJvY2Vzc29yIiwiUmVwb3J0R2VuZXJhdG9yIiwiVGF4VGFibGVWaWV3ZXIiXSwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDo4MDgyIiwiZXhwIjoxNzUzNzU1ODkzLCJpYXQiOjE3NTM3NTQ5OTN9.JcASD9_OWRPH2S90Ip9gnl2vghFZL1wikCaZP-kaUkNB3bu4xjnuYsCCCrfQe_-txD9UC4foZHcBUu7e6SzKoxm34rZHgZHU0-h_wn9ub0cnLY4Gz0Rd26lFuzW0TGIeekLAvfUlpjtmCp7rDSiFr8jul082L5MLRD2WnXcizPc0Mn1HqJMTyzhy58nzYTyFVQy-SSl8oKbNbnJuE7Dl6ycjC3keXcCWfd1l07vOI93utkxY7kSQx_JSC2M_i6XDLci1Ie1ZvhPfWhZoqBad26OGSGPa4mhILIKMojMbiAFj3RcvzrX_6-2YnrG-7gubhpVDGhekFGbJZMExd4K5Vg' \
--header 'Cookie: JSESSIONID=059112D57EFC1D0851FF329BA1563384; XSRF-TOKEN=ec66bdd4-6b09-4e18-b7ac-8a0656d333b4'
```
**Response:**
```json
{
  "timestamp": "2025-07-28T21:11:18.7911628",
  "code": "00",
  "message": "Información guardada correctamente para el periodo: 2022-01-01",
  "response": 2,
  "exception": null
}
```
**4. Obtener información guardada en la BD:**  
**4.1. Opción 1: Búsqueda por periodo contable.**
**Request:**
```bash
curl --location 'http://localhost:8082/api/v1/financial-fixed-assets/1?period=2022-01-01&mainFixedAssetCode=22.01.010862&accountingAccount=0126111003' \
--header 'Authorization: Bearer eyJraWQiOiIxZDk1NWY4OC1iYWJhLTQ5M2YtOWNmNC05MDQ3NGNiMGNmYWEiLCJhbGciOiJSUzI1NiJ9.eyJzdWIiOiJrYXRoZXJpbmVfZ3V0aWVycmV6IiwiYXVkIjoic2ljb2RldHJpYXBwIiwibmJmIjoxNzUzNzU0OTkzLCJzY29wZSI6WyJvcGVuaWQiLCJyb2xlcyJdLCJyb2xlcyI6WyJBc3NldFVwbG9hZGVyIiwiRGVwcmVjaWF0aW9uUHJvY2Vzc29yIiwiUmVwb3J0R2VuZXJhdG9yIiwiVGF4VGFibGVWaWV3ZXIiXSwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDo4MDgyIiwiZXhwIjoxNzUzNzU1ODkzLCJpYXQiOjE3NTM3NTQ5OTN9.JcASD9_OWRPH2S90Ip9gnl2vghFZL1wikCaZP-kaUkNB3bu4xjnuYsCCCrfQe_-txD9UC4foZHcBUu7e6SzKoxm34rZHgZHU0-h_wn9ub0cnLY4Gz0Rd26lFuzW0TGIeekLAvfUlpjtmCp7rDSiFr8jul082L5MLRD2WnXcizPc0Mn1HqJMTyzhy58nzYTyFVQy-SSl8oKbNbnJuE7Dl6ycjC3keXcCWfd1l07vOI93utkxY7kSQx_JSC2M_i6XDLci1Ie1ZvhPfWhZoqBad26OGSGPa4mhILIKMojMbiAFj3RcvzrX_6-2YnrG-7gubhpVDGhekFGbJZMExd4K5Vg' \
--header 'Cookie: JSESSIONID=557ED477386744AEA25545281BC02048; XSRF-TOKEN=ec66bdd4-6b09-4e18-b7ac-8a0656d333b4'
```
**Response:**
```json
{
  "timestamp": "2025-07-28T21:12:09.4863872",
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
      "authorizationDocumentNumber": "1",
      "usefulLifeYears": 30.0,
      "accumulatedDepreciationTheEndPrevious": 12830393.5700,
      "currentDepreciation": 916456.6800,
      "currentDepreciationRelatedWithdrawalsRetirements": 0.0000,
      "currentDepreciationRelatedOtherAdjustments": 0.0000,
      "accumulatedHistoricalDepreciation": 13746850.2500,
      "depreciationInflationAdjustment": 0.0000,
      "accumulatedDepreciationAdjustedInflation": 13746850.2500,
      "netFixedAssets": 13746850.4000,
      "historicalCost": 0.0000,
      "revaluedAmount": 0.0000,
      "wellAppraisedPreInvestmentStudyDry": "",
      "rowNum": 0,
      "physicalNumberOfRows": 0,
      "depreciacionFinancieraList": [],
      "depreciacionTributariaList": [],
      "addedBy": "CARNOL",
      "additionDate": "2025-07-28T21:11:18.725819",
      "modifiedBy": null,
      "modificationDate": null
    },
    {
      "id": 2,
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
      "authorizationDocumentNumber": "1",
      "usefulLifeYears": 30.0,
      "accumulatedDepreciationTheEndPrevious": 12830393.5700,
      "currentDepreciation": 916456.6800,
      "currentDepreciationRelatedWithdrawalsRetirements": 0.0000,
      "currentDepreciationRelatedOtherAdjustments": 0.0000,
      "accumulatedHistoricalDepreciation": 13746850.2500,
      "depreciationInflationAdjustment": 0.0000,
      "accumulatedDepreciationAdjustedInflation": 13746850.2500,
      "netFixedAssets": 13746850.4000,
      "historicalCost": 20143512.0000,
      "revaluedAmount": 7350188.6500,
      "wellAppraisedPreInvestmentStudyDry": "tasacion 2013",
      "rowNum": 0,
      "physicalNumberOfRows": 0,
      "depreciacionFinancieraList": [],
      "depreciacionTributariaList": [],
      "addedBy": "CARNOL",
      "additionDate": "2025-07-28T21:11:18.725819",
      "modifiedBy": null,
      "modificationDate": null
    }
  ],
  "exception": null
}
```
**4.2. Opción 2: Búsqueda por código de activo fijo principal.**   
**Request:**
```bash
curl --location 'http://localhost:8082/api/v1/financial-fixed-assets/2?period=2022-01-01&mainFixedAssetCode=22.01.010862&accountingAccount=0126111003' \
--header 'Authorization: Bearer eyJraWQiOiIxZDk1NWY4OC1iYWJhLTQ5M2YtOWNmNC05MDQ3NGNiMGNmYWEiLCJhbGciOiJSUzI1NiJ9.eyJzdWIiOiJrYXRoZXJpbmVfZ3V0aWVycmV6IiwiYXVkIjoic2ljb2RldHJpYXBwIiwibmJmIjoxNzUzNzU0OTkzLCJzY29wZSI6WyJvcGVuaWQiLCJyb2xlcyJdLCJyb2xlcyI6WyJBc3NldFVwbG9hZGVyIiwiRGVwcmVjaWF0aW9uUHJvY2Vzc29yIiwiUmVwb3J0R2VuZXJhdG9yIiwiVGF4VGFibGVWaWV3ZXIiXSwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDo4MDgyIiwiZXhwIjoxNzUzNzU1ODkzLCJpYXQiOjE3NTM3NTQ5OTN9.JcASD9_OWRPH2S90Ip9gnl2vghFZL1wikCaZP-kaUkNB3bu4xjnuYsCCCrfQe_-txD9UC4foZHcBUu7e6SzKoxm34rZHgZHU0-h_wn9ub0cnLY4Gz0Rd26lFuzW0TGIeekLAvfUlpjtmCp7rDSiFr8jul082L5MLRD2WnXcizPc0Mn1HqJMTyzhy58nzYTyFVQy-SSl8oKbNbnJuE7Dl6ycjC3keXcCWfd1l07vOI93utkxY7kSQx_JSC2M_i6XDLci1Ie1ZvhPfWhZoqBad26OGSGPa4mhILIKMojMbiAFj3RcvzrX_6-2YnrG-7gubhpVDGhekFGbJZMExd4K5Vg' \
--header 'Cookie: JSESSIONID=2C42C51FD8618B8FA0E0517AC4683AC3; XSRF-TOKEN=ec66bdd4-6b09-4e18-b7ac-8a0656d333b4'
```
**Response:**
```json
{
    "timestamp": "2025-07-28T21:15:28.8510907",
    "code": "00",
    "message": "Se obtuvo informacion para el código de activo fijo principal: 22.01.010862",
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
            "authorizationDocumentNumber": "1",
            "usefulLifeYears": 30.0,
            "accumulatedDepreciationTheEndPrevious": 12830393.5700,
            "currentDepreciation": 916456.6800,
            "currentDepreciationRelatedWithdrawalsRetirements": 0.0000,
            "currentDepreciationRelatedOtherAdjustments": 0.0000,
            "accumulatedHistoricalDepreciation": 13746850.2500,
            "depreciationInflationAdjustment": 0.0000,
            "accumulatedDepreciationAdjustedInflation": 13746850.2500,
            "netFixedAssets": 13746850.4000,
            "historicalCost": 0.0000,
            "revaluedAmount": 0.0000,
            "wellAppraisedPreInvestmentStudyDry": "",
            "rowNum": 0,
            "physicalNumberOfRows": 0,
            "depreciacionFinancieraList": [],
            "depreciacionTributariaList": [],
            "addedBy": "CARNOL",
            "additionDate": "2025-07-28T21:11:18.725819",
            "modifiedBy": null,
            "modificationDate": null
        },
        {
            "id": 2,
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
            "authorizationDocumentNumber": "1",
            "usefulLifeYears": 30.0,
            "accumulatedDepreciationTheEndPrevious": 12830393.5700,
            "currentDepreciation": 916456.6800,
            "currentDepreciationRelatedWithdrawalsRetirements": 0.0000,
            "currentDepreciationRelatedOtherAdjustments": 0.0000,
            "accumulatedHistoricalDepreciation": 13746850.2500,
            "depreciationInflationAdjustment": 0.0000,
            "accumulatedDepreciationAdjustedInflation": 13746850.2500,
            "netFixedAssets": 13746850.4000,
            "historicalCost": 20143512.0000,
            "revaluedAmount": 7350188.6500,
            "wellAppraisedPreInvestmentStudyDry": "tasacion 2013",
            "rowNum": 0,
            "physicalNumberOfRows": 0,
            "depreciacionFinancieraList": [],
            "depreciacionTributariaList": [],
            "addedBy": "CARNOL",
            "additionDate": "2025-07-28T21:11:18.725819",
            "modifiedBy": null,
            "modificationDate": null
        }
    ],
    "exception": null
}
```
**4.3. Opción 3: Búsqueda por cuenta contable.**  
**Request:**
```bash
curl --location 'http://localhost:8082/api/v1/financial-fixed-assets/3?period=2022-01-01&mainFixedAssetCode=22.01.010862&accountingAccount=3321161001' \
--header 'Authorization: Bearer eyJraWQiOiIxZDk1NWY4OC1iYWJhLTQ5M2YtOWNmNC05MDQ3NGNiMGNmYWEiLCJhbGciOiJSUzI1NiJ9.eyJzdWIiOiJrYXRoZXJpbmVfZ3V0aWVycmV6IiwiYXVkIjoic2ljb2RldHJpYXBwIiwibmJmIjoxNzUzNzU0OTkzLCJzY29wZSI6WyJvcGVuaWQiLCJyb2xlcyJdLCJyb2xlcyI6WyJBc3NldFVwbG9hZGVyIiwiRGVwcmVjaWF0aW9uUHJvY2Vzc29yIiwiUmVwb3J0R2VuZXJhdG9yIiwiVGF4VGFibGVWaWV3ZXIiXSwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDo4MDgyIiwiZXhwIjoxNzUzNzU1ODkzLCJpYXQiOjE3NTM3NTQ5OTN9.JcASD9_OWRPH2S90Ip9gnl2vghFZL1wikCaZP-kaUkNB3bu4xjnuYsCCCrfQe_-txD9UC4foZHcBUu7e6SzKoxm34rZHgZHU0-h_wn9ub0cnLY4Gz0Rd26lFuzW0TGIeekLAvfUlpjtmCp7rDSiFr8jul082L5MLRD2WnXcizPc0Mn1HqJMTyzhy58nzYTyFVQy-SSl8oKbNbnJuE7Dl6ycjC3keXcCWfd1l07vOI93utkxY7kSQx_JSC2M_i6XDLci1Ie1ZvhPfWhZoqBad26OGSGPa4mhILIKMojMbiAFj3RcvzrX_6-2YnrG-7gubhpVDGhekFGbJZMExd4K5Vg' \
--header 'Cookie: JSESSIONID=2C42C51FD8618B8FA0E0517AC4683AC3; XSRF-TOKEN=ec66bdd4-6b09-4e18-b7ac-8a0656d333b4'
```
**Response:**
```json
{
  "timestamp": "2025-07-28T21:17:25.4128981",
  "code": "00",
  "message": "Se obtuvo informacion para la cuenta contable: 3321161001",
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
      "authorizationDocumentNumber": "1",
      "usefulLifeYears": 30.0,
      "accumulatedDepreciationTheEndPrevious": 12830393.5700,
      "currentDepreciation": 916456.6800,
      "currentDepreciationRelatedWithdrawalsRetirements": 0.0000,
      "currentDepreciationRelatedOtherAdjustments": 0.0000,
      "accumulatedHistoricalDepreciation": 13746850.2500,
      "depreciationInflationAdjustment": 0.0000,
      "accumulatedDepreciationAdjustedInflation": 13746850.2500,
      "netFixedAssets": 13746850.4000,
      "historicalCost": 0.0000,
      "revaluedAmount": 0.0000,
      "wellAppraisedPreInvestmentStudyDry": "",
      "rowNum": 0,
      "physicalNumberOfRows": 0,
      "depreciacionFinancieraList": [],
      "depreciacionTributariaList": [],
      "addedBy": "CARNOL",
      "additionDate": "2025-07-28T21:11:18.725819",
      "modifiedBy": null,
      "modificationDate": null
    },
    {
      "id": 2,
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
      "authorizationDocumentNumber": "1",
      "usefulLifeYears": 30.0,
      "accumulatedDepreciationTheEndPrevious": 12830393.5700,
      "currentDepreciation": 916456.6800,
      "currentDepreciationRelatedWithdrawalsRetirements": 0.0000,
      "currentDepreciationRelatedOtherAdjustments": 0.0000,
      "accumulatedHistoricalDepreciation": 13746850.2500,
      "depreciationInflationAdjustment": 0.0000,
      "accumulatedDepreciationAdjustedInflation": 13746850.2500,
      "netFixedAssets": 13746850.4000,
      "historicalCost": 20143512.0000,
      "revaluedAmount": 7350188.6500,
      "wellAppraisedPreInvestmentStudyDry": "tasacion 2013",
      "rowNum": 0,
      "physicalNumberOfRows": 0,
      "depreciacionFinancieraList": [],
      "depreciacionTributariaList": [],
      "addedBy": "CARNOL",
      "additionDate": "2025-07-28T21:11:18.725819",
      "modifiedBy": null,
      "modificationDate": null
    }
  ],
  "exception": null
}
```
**5. Genera la proyección:**  
**5.1. Deducciones Temporales**  
**Request:**
```bash
curl --location --request POST 'http://localhost:8082/api/v1/financial-fixed-assets/projection?type=TEMPORARY_DEDUCTIONS&period=2022-01-01&account=332'
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
**5.2. Adiciones Temporales**  
**Request:**
```bash
curl --location --request POST 'http://localhost:8082/api/v1/financial-fixed-assets/projection?type=TEMPORARY_ADDITIONS&period=2023-12-01&account=ALL'
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
**5.3. Adiciones Permanentes**  
**Request:**
```bash
curl --location --request POST 'http://localhost:8082/api/v1/financial-fixed-assets/projection?type=PERMANENT_ADDITIONS&period=2022-01-01&account=332'
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
**6. Generación de reportes**  
**6.1. Reporte de Libro Tributario**  
**Request:**
```bash
curl --location 'http://localhost:8082/api/v1/financial-fixed-assets/report?option=1&fileType=xlsx&year=2022&mainCode=22.01.010862&accountingAccount=3321161001&startAcquisitionDate=2007-12-31&endAcquisitionDate=2007-12-31&startDateUse=2007-12-31&endDateUse=2007-12-31' \
--header 'Authorization: Bearer eyJraWQiOiIxZDk1NWY4OC1iYWJhLTQ5M2YtOWNmNC05MDQ3NGNiMGNmYWEiLCJhbGciOiJSUzI1NiJ9.eyJzdWIiOiJrYXRoZXJpbmVfZ3V0aWVycmV6IiwiYXVkIjoic2ljb2RldHJpYXBwIiwibmJmIjoxNzUzNzU0OTkzLCJzY29wZSI6WyJvcGVuaWQiLCJyb2xlcyJdLCJyb2xlcyI6WyJBc3NldFVwbG9hZGVyIiwiRGVwcmVjaWF0aW9uUHJvY2Vzc29yIiwiUmVwb3J0R2VuZXJhdG9yIiwiVGF4VGFibGVWaWV3ZXIiXSwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDo4MDgyIiwiZXhwIjoxNzUzNzU1ODkzLCJpYXQiOjE3NTM3NTQ5OTN9.JcASD9_OWRPH2S90Ip9gnl2vghFZL1wikCaZP-kaUkNB3bu4xjnuYsCCCrfQe_-txD9UC4foZHcBUu7e6SzKoxm34rZHgZHU0-h_wn9ub0cnLY4Gz0Rd26lFuzW0TGIeekLAvfUlpjtmCp7rDSiFr8jul082L5MLRD2WnXcizPc0Mn1HqJMTyzhy58nzYTyFVQy-SSl8oKbNbnJuE7Dl6ycjC3keXcCWfd1l07vOI93utkxY7kSQx_JSC2M_i6XDLci1Ie1ZvhPfWhZoqBad26OGSGPa4mhILIKMojMbiAFj3RcvzrX_6-2YnrG-7gubhpVDGhekFGbJZMExd4K5Vg' \
--header 'Cookie: JSESSIONID=4B04851F10865BEB45D17514A6652438; XSRF-TOKEN=ec66bdd4-6b09-4e18-b7ac-8a0656d333b4'
```  
**Response:**
```raw
PK
```
**6.2. Reporte de Detalle Activo Fijo**
**Request:**
```bash
curl --location 'http://localhost:8082/api/v1/financial-fixed-assets/report?option=2&fileType=xlsx&year=2022&mainCode=22.01.010862&accountingAccount=3321161001&startAcquisitionDate=2007-12-31&endAcquisitionDate=2007-12-31&startDateUse=2007-12-31&endDateUse=2007-12-31' \
--header 'Authorization: Bearer eyJraWQiOiIxZDk1NWY4OC1iYWJhLTQ5M2YtOWNmNC05MDQ3NGNiMGNmYWEiLCJhbGciOiJSUzI1NiJ9.eyJzdWIiOiJrYXRoZXJpbmVfZ3V0aWVycmV6IiwiYXVkIjoic2ljb2RldHJpYXBwIiwibmJmIjoxNzUzNzU0OTkzLCJzY29wZSI6WyJvcGVuaWQiLCJyb2xlcyJdLCJyb2xlcyI6WyJBc3NldFVwbG9hZGVyIiwiRGVwcmVjaWF0aW9uUHJvY2Vzc29yIiwiUmVwb3J0R2VuZXJhdG9yIiwiVGF4VGFibGVWaWV3ZXIiXSwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDo4MDgyIiwiZXhwIjoxNzUzNzU1ODkzLCJpYXQiOjE3NTM3NTQ5OTN9.JcASD9_OWRPH2S90Ip9gnl2vghFZL1wikCaZP-kaUkNB3bu4xjnuYsCCCrfQe_-txD9UC4foZHcBUu7e6SzKoxm34rZHgZHU0-h_wn9ub0cnLY4Gz0Rd26lFuzW0TGIeekLAvfUlpjtmCp7rDSiFr8jul082L5MLRD2WnXcizPc0Mn1HqJMTyzhy58nzYTyFVQy-SSl8oKbNbnJuE7Dl6ycjC3keXcCWfd1l07vOI93utkxY7kSQx_JSC2M_i6XDLci1Ie1ZvhPfWhZoqBad26OGSGPa4mhILIKMojMbiAFj3RcvzrX_6-2YnrG-7gubhpVDGhekFGbJZMExd4K5Vg' \
--header 'Cookie: JSESSIONID=E6B6782F359D626EE9867A3E54447525; XSRF-TOKEN=ec66bdd4-6b09-4e18-b7ac-8a0656d333b4'
```  
**Response:**
```raw
PK
```
**7. Tabla de Tasas de SUNAT**  
**7.1. Obtener todas las tasas tributarias:**  
**Request:**
```bash
curl --location 'http://localhost:8082/api/v1/tax-tables' \
--header 'Authorization: Bearer eyJraWQiOiIxZDk1NWY4OC1iYWJhLTQ5M2YtOWNmNC05MDQ3NGNiMGNmYWEiLCJhbGciOiJSUzI1NiJ9.eyJzdWIiOiJrYXRoZXJpbmVfZ3V0aWVycmV6IiwiYXVkIjoic2ljb2RldHJpYXBwIiwibmJmIjoxNzUzNzU2MTE0LCJzY29wZSI6WyJvcGVuaWQiLCJyb2xlcyJdLCJyb2xlcyI6WyJBc3NldFVwbG9hZGVyIiwiRGVwcmVjaWF0aW9uUHJvY2Vzc29yIiwiUmVwb3J0R2VuZXJhdG9yIiwiVGF4VGFibGVWaWV3ZXIiXSwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDo4MDgyIiwiZXhwIjoxNzUzNzU3MDE0LCJpYXQiOjE3NTM3NTYxMTR9.RdcHtOLvYMQgKjYL1acpum4AUqiA-viEZABgofQ91J8QHJt_lJULrqh2MYFpPNmjFRLCxSgWLhQOCfi0MktSqJ4PBlHhCmn5nPdE2EpnsbrTcqV_B3bPjjlr_IMVJeby2MCB8f7H9ktdBLBOoRB4H1ATrAmycQhQR5fguMe1YfyO_p_SufWwAC6xFpdfYN8MSbnTjpDOQ7u92OWjfKjK3QMB-hTwPisFTB7eCRrs_mvrsX7e6Nu_brHQT_lp6L_7JT3UJPzd5_ACaeyl7FpUTnLZy6Mp_K9FbZBZZbFxPV8IpsA3x92MF1ehpw2Vr0igFb74qI7h0ibYTUx26OeB2g' \
--header 'Cookie: JSESSIONID=099C61081FACD39C16FF37518F4882F1; XSRF-TOKEN=a27429f4-dd85-4392-b3c4-be41626a3510'
```
**Response**
```json
[
  {
    "id": 12,
    "accountingAccount": "331",
    "description": "Terrenos",
    "annualRate": 0,
    "usefulLifeYears": 0,
    "status": "S",
    "addedBy": null,
    "additionDate": null,
    "modifiedBy": null,
    "modificationDate": null
  },
  {
    "id": 13,
    "accountingAccount": "332",
    "description": "Edificaciones y Otras Construcciones",
    "annualRate": 5,
    "usefulLifeYears": 20,
    "status": "S",
    "addedBy": null,
    "additionDate": null,
    "modifiedBy": null,
    "modificationDate": null
  },
  {
    "id": 14,
    "accountingAccount": "333",
    "description": "Maquinaria y equipo utilizados por las actividades minera, petrolera y de construcción; excepto muebles, enseres y equipo de oficinas.",
    "annualRate": 20,
    "usefulLifeYears": 5,
    "status": "S",
    "addedBy": null,
    "additionDate": null,
    "modifiedBy": null,
    "modificationDate": null
  },
  {
    "id": 15,
    "accountingAccount": "334",
    "description": "Vehículos de transporte terrestre (excepto ferrocarriles); hornos en general.",
    "annualRate": 20,
    "usefulLifeYears": 5,
    "status": "S",
    "addedBy": null,
    "additionDate": null,
    "modifiedBy": null,
    "modificationDate": null
  },
  {
    "id": 16,
    "accountingAccount": "335",
    "description": "Muebles y Enseres, Equipos de Oficina",
    "annualRate": 10,
    "usefulLifeYears": 10,
    "status": "S",
    "addedBy": null,
    "additionDate": null,
    "modifiedBy": null,
    "modificationDate": null
  },
  {
    "id": 17,
    "accountingAccount": "336",
    "description": "Equipos de procesamiento de datos.",
    "annualRate": 25,
    "usefulLifeYears": 4,
    "status": "S",
    "addedBy": null,
    "additionDate": null,
    "modifiedBy": null,
    "modificationDate": null
  },
  {
    "id": 18,
    "accountingAccount": "337",
    "description": "Otros bienes del activo fijo",
    "annualRate": 10,
    "usefulLifeYears": 10,
    "status": "S",
    "addedBy": null,
    "additionDate": null,
    "modifiedBy": null,
    "modificationDate": null
  },
  {
    "id": 19,
    "accountingAccount": "338",
    "description": "Unidades por Recibir",
    "annualRate": 0,
    "usefulLifeYears": 0,
    "status": "S",
    "addedBy": null,
    "additionDate": null,
    "modifiedBy": null,
    "modificationDate": null
  },
  {
    "id": 20,
    "accountingAccount": "339",
    "description": "Obras en Curso",
    "annualRate": 0,
    "usefulLifeYears": 0,
    "status": "S",
    "addedBy": null,
    "additionDate": null,
    "modifiedBy": null,
    "modificationDate": null
  },
  {
    "id": 21,
    "accountingAccount": null,
    "description": "Ganado de trabajo y reproducción; redes de pesca.",
    "annualRate": 25,
    "usefulLifeYears": 4,
    "status": "S",
    "addedBy": null,
    "additionDate": null,
    "modifiedBy": null,
    "modificationDate": null
  },
  {
    "id": 22,
    "accountingAccount": null,
    "description": "Maquinaria y equipo adquirido a partir del 01- 01-91",
    "annualRate": 10,
    "usefulLifeYears": 10,
    "status": "S",
    "addedBy": null,
    "additionDate": null,
    "modifiedBy": null,
    "modificationDate": null
  }
]
```
**7.2. Obtener tasa tributaria por: id, cuenta contable:**  
**Request:**
```bash
curl --location 'http://localhost:8082/api/v1/tax-tables/2?id=13&accountingAccount=332' \
--header 'Authorization: Bearer eyJraWQiOiIxZDk1NWY4OC1iYWJhLTQ5M2YtOWNmNC05MDQ3NGNiMGNmYWEiLCJhbGciOiJSUzI1NiJ9.eyJzdWIiOiJrYXRoZXJpbmVfZ3V0aWVycmV6IiwiYXVkIjoic2ljb2RldHJpYXBwIiwibmJmIjoxNzUzNzU2MTE0LCJzY29wZSI6WyJvcGVuaWQiLCJyb2xlcyJdLCJyb2xlcyI6WyJBc3NldFVwbG9hZGVyIiwiRGVwcmVjaWF0aW9uUHJvY2Vzc29yIiwiUmVwb3J0R2VuZXJhdG9yIiwiVGF4VGFibGVWaWV3ZXIiXSwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDo4MDgyIiwiZXhwIjoxNzUzNzU3MDE0LCJpYXQiOjE3NTM3NTYxMTR9.RdcHtOLvYMQgKjYL1acpum4AUqiA-viEZABgofQ91J8QHJt_lJULrqh2MYFpPNmjFRLCxSgWLhQOCfi0MktSqJ4PBlHhCmn5nPdE2EpnsbrTcqV_B3bPjjlr_IMVJeby2MCB8f7H9ktdBLBOoRB4H1ATrAmycQhQR5fguMe1YfyO_p_SufWwAC6xFpdfYN8MSbnTjpDOQ7u92OWjfKjK3QMB-hTwPisFTB7eCRrs_mvrsX7e6Nu_brHQT_lp6L_7JT3UJPzd5_ACaeyl7FpUTnLZy6Mp_K9FbZBZZbFxPV8IpsA3x92MF1ehpw2Vr0igFb74qI7h0ibYTUx26OeB2g' \
--header 'Cookie: JSESSIONID=D5A2DAA639F363D453D6613C371A6C24; XSRF-TOKEN=a27429f4-dd85-4392-b3c4-be41626a3510'
```
**Response:**
```json
{
  "timestamp": "2025-07-28T21:28:50.8571307",
  "code": null,
  "message": "Se obtuvo información para cuenta contable: 332",
  "response": {
    "id": 13,
    "accountingAccount": "332",
    "description": "Edificaciones y Otras Construcciones",
    "annualRate": 5,
    "usefulLifeYears": 20,
    "status": "S",
    "addedBy": null,
    "additionDate": null,
    "modifiedBy": null,
    "modificationDate": null
  },
  "exception": null
}
```

### Documentación APIs
- [API Docs (yml)](http://localhost:8082/v3/api-docs) 
- [API Docs (GUI)](http://localhost:8082/swagger-ui/index.html)

### Informes Generados
A continuación las rutas donde puedes consultar los informes generados tras el build:
- **Pruebas unitarias:** `target/site/jacoco/index.html`
- **Pruebas de integración:** `target/karate-reports/karate-summary.html`
- **Javadoc:** `target/site/apidocs/index.html`  

**NOTA:**  
Se proporciona los comandos maven para ejecutar sólo las pruebas unitarias, integración y documentación javadocs:
- **Pruebas unitarias:** `mvn clean test -Dmaven.javadoc.skip=true`
- **Pruebas de integración:** `mvn clean verify -DskipTests -Dmaven.javadoc.skip=true`
- **Javadoc:** `mvn clean javadoc:javadoc`
- **Generar reporte de cobertura:** `mvn clean test jacoco:report`


### Dependencias
| Nombre                                          | Versión  | 
|-------------------------------------------------|----------|
| spring-boot-starter-web                         | 3.2.8    |
| spring-boot-starter-test                        | 3.2.8    |
| h2                                              | 2.2.224  |
| karate-junit5                                   | 7.5      |
| spring-boot-starter-data-jpa                    | 3.2.8    |
| spring-boot-starter-data-jdbc                   | 3.2.8    |
| spring-boot-starter-validation                  | 3.2.8    |
| spring-boot-starter-security                    | 3.2.8    |
| spring-boot-starter-oauth2-resource-server      | 3.2.8    |
| spring-boot-starter-oauth2-authorization-server | 3.2.8    |
| spring-security-oauth2-authorization-server | 1.1.0    |
| spring-boot-devtools                            | 3.2.8    |
| postgresql                                      | 42.7.2   |
| poi-ooxml                                       | 5.3.0    |
| lombok                                          | 1.18.34  |
| mockito-core                                    | 5.3.1    |
| mockito-inline                                  | 5.2.0    |
| springdoc-openapi-starter-webmvc-ui             | 2.3.0    |
| itext-core                                      | 8.0.2    |
| jasperreports                                   | 6.21.3   |
| jasperreports-fonts                             | 6.21.3   |
| guava                                           | 30.1-jre |
| spotbugs-annotations                            | 4.0.0    |

### Plugins
| Nombre                   | Versión | 
|--------------------------|---------|
| spring-boot-maven-plugin | 3.2.8   |
| jacoco-maven-plugin      | 0.8.8   |
| maven-checkstyle-plugin  | 3.3.1   |
| maven-failsafe-plugin    | 3.1.2   |
| maven-javadoc-plugin     | 3.4.1   |
| spotbugs-maven-plugin    | 4.8.6.6 |

### Licencia
Este proyecto es propiedad intelectual de &reg;EPS Grau. El uso sin autorización está prohibido.