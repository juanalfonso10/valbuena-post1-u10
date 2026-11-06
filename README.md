# Envios Service — Análisis de Métricas de Calidad

Post-contenido de la Unidad 10 de Patrones de Diseño de Software. Proyecto Maven plano
(Java 21, sin Spring Boot) que calcula el costo de un envío según método de transporte,
peso, distancia, fragilidad y tipo de cliente. `ServicioCalculoEnvio.calcularCosto()`
concentra toda la lógica en una cadena de condicionales y la suite de partida solo cubre
dos caminos. Esta primera parte diagnostica el problema con métricas reales de SonarQube
y JaCoCo, sin corregir nada todavía.

## Entorno de análisis

**Decisión de diseño 1 — SonarQube local con Docker (Opción A).** Se levantó con el
comando del enunciado (`docker run ... sonarqube:community`). La versión resultante fue
SonarQube Community Build v26.11.0.134175. Criterios:

- **Control total de la configuración.** La Parte 2 exige crear un Quality Profile y un
  Quality Gate propios ("Estándar Laboratorio"). El plan gratuito de SonarQube Cloud no
  permite Quality Gates ni Quality Profiles personalizados (solo los planes Team y
  Enterprise), así que con SonarCloud el Paso 9 no se podría cumplir.
- **Sin dependencia de conectividad ni de límites de un plan gratuito.** El análisis corre
  contra `http://localhost:9000` y el resultado no depende de cuotas externas.
- **Costo aceptado.** El servidor consume recursos locales y debe quedar encendido durante
  todo el laboratorio; el dashboard no es accesible desde otro equipo, por eso la evidencia
  queda en las capturas de `docs/`.

La instancia trae por defecto el modo MQR, que renombra los Code Smells como
"Maintainability issues". Se cambió a **Standard Experience** (Administration → General →
Mode) para que las métricas y las condiciones del Quality Gate coincidan con la
terminología de la guía (Code Smells, New Code Smells).

## Exclusiones del análisis

**Decisión de diseño 2 — no se excluye ninguna clase.** El proyecto no tiene clase de
arranque (`Main`), código generado ni configuración: todo `src/main/java` es lógica de
negocio. Excluir algo solo alteraría el porcentaje de cobertura sin reflejar mejor la
calidad del código que importa. La decisión queda comentada en `sonar-project.properties`.

Nota técnica: `mvn sonar:sonar` usa SonarScanner for Maven, que **no lee**
`sonar-project.properties` (ese archivo es para el SonarScanner CLI). Por eso las mismas
propiedades (`sonar.projectKey`, `sonar.projectName`, `sonar.host.url`,
`sonar.coverage.jacoco.xmlReportPaths`, etc.) están replicadas en `pom.xml`.

## Estado inicial del análisis

Comando: `mvn clean verify sonar:sonar -Dsonar.token=***` (2 pruebas, 0 fallos).

| Métrica | Valor real observado |
|---------|----------------------|
| Complejidad ciclomática de calcularCosto() | 20 |
| Complejidad cognitiva de calcularCosto() | 32 |
| Cobertura de líneas de ServicioCalculoEnvio | 23.8% (10 de 42 líneas) |
| Cobertura de ramas de ServicioCalculoEnvio | 18.4% (7 de 38 condiciones) |
| Cobertura total (SonarQube) | 21.3% |
| Code Smells reportados | 0 |
| Duplicación | 0.0% |
| Quality Gate (Sonar way por defecto) | Passed |

## Interpretación

**Qué método concentra el problema.** SonarQube informa la complejidad por archivo:
`ServicioCalculoEnvio.java` tiene complejidad ciclomática 20 y una sola función
(`functions = 1`), así que los 20 puntos pertenecen enteros a `calcularCosto()`. El valor
coincide con el conteo manual de la guía (CC = 1 + 19 puntos de decisión: 4 de la cadena
de métodos de envío, 12 de tramos de peso y de condiciones por distancia, 1 de fragilidad y 2 de
tipo de cliente). Está en el rango "Alta" de la Tabla 3.2 de la guía.

**Qué ramas quedaron sin cubrir.** El reporte HTML de JaCoCo (`docs/jacoco-inicial.png`)
marca 31 de 38 ramas sin ejercitar. Las dos pruebas de partida solo recorren ESTANDAR con
3 kg y 100 km, sin fragilidad, para un cliente REGULAR y uno PREMIUM:

- **Sin ejercitar en absoluto:** los métodos EXPRESS (líneas 19 a 30), OVERNIGHT (31 a 41,
  incluida la excepción por más de 300 km) e INTERNACIONAL (42 a 52, incluido el recargo
  por más de 5000 km), y el `else` que rechaza un método no soportado (línea 54).
- **Parciales:** dentro de ESTANDAR solo se tomó el tramo de peso `<= 5`; los tramos
  5–20 kg y más de 20 kg (líneas 11 a 14) y el recargo por más de 500 km (línea 17) nunca
  se ejecutaron. Tampoco el recargo por fragilidad (línea 58) ni el descuento CORPORATIVO
  (línea 64).
- **Única condición cubierta en ambos sentidos:** `tipoCliente.equals("PREMIUM")`
  (línea 61), gracias a la segunda prueba.

**Por qué coinciden complejidad alta y cobertura baja.** Apuntan al mismo método porque
toda la lógica vive en él: cada punto de decisión agrega un camino independiente, y la
complejidad ciclomática es justamente el número mínimo de casos necesarios para cubrir
todas sus ramas. Con CC = 20 y solo 2 pruebas, la cobertura de ramas no podía pasar de un
porcentaje bajo.

**Por qué SonarQube reporta 0 Code Smells.** En esta versión el perfil por defecto para
Java ya no se llama "Sonar way" sino **"Sonar way core"** (263 reglas), y no activa
ninguna regla de complejidad: ni S1541 (complejidad ciclomática por método) ni S3776
(complejidad cognitiva). El problema existe en las métricas pero no genera ningún issue
ni hace fallar el Quality Gate por defecto. Esto se corrige en la Parte 2 con un Quality
Profile propio.

**Decisión de diseño 3 — señal principal: complejidad ciclomática.** De las tres señales
disponibles se usó la ciclomática (20) para elegir el método a intervenir, con la
cobertura como confirmación:

- Es la única de las tres que cuenta caminos independientes, así que explica directamente
  la cobertura baja y dice cuántos casos de prueba hacen falta.
- La complejidad cognitiva (32) también es alta, pero mide la dificultad de lectura (castiga
  el anidamiento) y no se traduce en un número de pruebas. Además, la regla de complejidad
  por método que pide el Quality Gate de la Parte 2 (S1541) limita la ciclomática.
- La cobertura sola (18.4% de ramas) diría "faltan pruebas", pero no que el problema es de
  diseño: escribir 20 pruebas contra un método de más de 60 líneas lo dejaría igual de frágil ante
  cambios. La ciclomática es la que justifica refactorizar antes de completar las pruebas.

## Capturas del análisis inicial

Dashboard del proyecto (Overall Code): Quality Gate Passed, 0 Code Smells, 21.3% de cobertura.
![Dashboard SonarQube](docs/sonar-dashboard-inicial.png)

Measures → Complexity: complejidad ciclomática 20 en `ServicioCalculoEnvio.java`.
![Complejidad SonarQube](docs/sonar-complejidad-inicial.png)

Measures → Coverage: 18.4% de cobertura de condiciones, 31 condiciones sin cubrir.
![Cobertura de ramas SonarQube](docs/sonar-cobertura-ramas-inicial.png)

Reporte HTML de JaCoCo: en rojo las ramas nunca ejecutadas, en amarillo las parciales.
![Reporte JaCoCo](docs/jacoco-inicial.png)

## Ejecutar el análisis
```bash
docker run -d --name sonarqube -p 9000:9000 \
  -e SONAR_ES_BOOTSTRAP_CHECKS_DISABLE=true sonarqube:community
mvn clean verify sonar:sonar -Dsonar.token=TU_TOKEN
```
