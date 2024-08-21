# SICODETRI

**Authors**:       Carlos Nole

**Document Version**:  1.0.0

**Supported Spring Boot Version**: 3.2.8

### Descripción y funciones
Aplicación Web monolítica que permite realizar la conciliación de la Depreciación Tributaria vs Financiera de los Activos Fijos.

1. Subida de archivo excel (Formato 7) a la aplicación web.
2. Información del archivo excel se guarda en base de datos para el periodo indicado. Si se carga de nuevo el mismo periodo se elimina la información anterior.
3. Consulta de información guardada en base de datos.

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
1. **Subir archivo excel**:  
Request:
```bash
curl --location 'http://localhost:8081/api/v1/financial-fixed-asset/upload' \
--header 'Cookie: JSESSIONID=2540F11172438274A07896F7D5EDF82D' \
--form 'file=@"/C:/epsgrau/Formato 7 financiero 2021.xlsx"'
```  
Response:  
```json
{
    "timestamp": "2024-08-19T21:47:58.8565709",
    "code": "00",
    "message": "Archivo subio correctamente: Formato 7 financiero 2021.xlsx",
    "response": null,
    "exception": {
        "timestamp": null,
        "message": null,
        "details": null
    }
}
```
2. **Guardar información subida en la BD:**  
Request:
```bash
curl --location 'http://localhost:8081/api/v1/financial-fixed-asset?period=2021-01-01' \
--header 'Cookie: JSESSIONID=2540F11172438274A07896F7D5EDF82D' \
--form 'file=@"/C:/epsgrau/Formato 7 financiero 2022.xlsx"'
```
Response:
```json
{
    "timestamp": "2024-08-19T21:48:23.231606",
    "code": "00",
    "message": "Informacion guardada de manera correcta para el periodo 2021-01-01",
    "response": null,
    "exception": {
        "timestamp": null,
        "message": null,
        "details": null
    }
}
```
3. **Obtener información guardada en la BD:**  
Request:
```bash
curl --location --request GET 'http://localhost:8081/api/v1/financial-fixed-asset?period=2021-01-01' \
--header 'Cookie: JSESSIONID=2540F11172438274A07896F7D5EDF82D'
```
Response:
```json
{
    "timestamp": "2024-08-19T21:49:04.4251861",
    "code": "00",
    "message": "Se obtuvo informacion para el periodo 2021-01-01",
    "response": [
        {
            "id": 1,
            "accountingPeriod": "2021-01-01",
            "mainFixedAssetCode": "21.02.010150",
            "fixedAssetCodeComponent": "",
            "fixedAssetAccountingAccount": "0126111003",
            //continua los campos de las columnas cargas
            "addedBy": "CARNOL",
            "additionDate": "2024-08-19T21:48:21.075759",
            "modifiedBy": null,
            "modificationDate": null
        }
        //continua los items
    ],
    "exception": {
        "timestamp": null,
        "message": null,
        "details": null
    }
}
```

### Documentación APIs
- [API Docs (yml)](http://localhost:8081/v3/api-docs) 
- [API Docs (GUI)](http://localhost:8081/swagger-ui/index.html)

### Dependencias
| Nombre                   | Versión | 
|--------------------------|---------|
| spring-boot-starter-web  | 3.2.8   |
| spring-boot-starter-test | 3.2.8   |
| spring-boot-starter-data-jp | 3.2.8   |
| spring-boot-starter-validatio | 3.2.8   |
| poi-ooxml | 5.3.0   |
| lombok                   | 1.18.34 |
| mockito-core             | 5.3.1   |
| springdoc-openapi-starter-webmvc-ui | 2.3.0 |

### Licencia
Este proyecto es propiedad intelectual de &reg;EPS Grau. El uso sin autorización está prohibido.