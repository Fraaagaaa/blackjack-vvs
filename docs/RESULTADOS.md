# Estado de validación de esta entrega

Fecha: 20-10-2026. Entrega de código para incorporación manual, priorizada
por petición del usuario. No se ha subido código ni abierto issues en GitHub.

## Comprobaciones realizadas

- Lectura del código completo, README, contratos Javadoc, rúbrica y apuntes.
- Comparación con el fork: `master` en
  `1f0d5a72f32f82cc903a6669eb62f0646e1c9d3d`, igual a la base del ZIP.
- Compilación Java 17 del SUT y las 27 clases de soporte/pruebas completada
  antes de la ejecución de Surefire.
- Ejecución parcial satisfactoria de CardTest, DeckTest, SuitTest, RankTest y
  OutcomeTest. Esto **no equivale a que la suite completa pase**.
- El primer intento de ejecución falló al cargar dinámicamente el agente de
  Mockito en el entorno aislado (Byte Buddy: no pudo adjuntarse a la JVM).
  Se añadió el agente explícito al arranque en Surefire, Failsafe y PIT.

## Pendiente de validar en vuestro equipo

| Comprobación | Comando | Estado al preparar la entrega |
|---|---|---|
| Suite completa y cobertura | `mvn clean verify` | Sin resultado completo confirmado |
| Cinco reproducciones de defectos | `mvn -Pdefects -Dtest='*DefectTest' test` | Escritas; confirmar fallos esperados |
| Análisis estático | `mvn compile spotbugs:spotbugs checkstyle:checkstyle` | Configurado, no ejecutado |
| Mutación | `mvn test-compile org.pitest:pitest-maven:mutationCoverage` | Configurada, no ejecutada |
| Presupuestos temporales | `mvn -Pperformance verify` | Escritos, no ejecutados |

No se proporcionan porcentajes de cobertura ni puntuaciones de mutación
inventados. Los cinco hallazgos de INCIDENCIAS están fundamentados en lectura
del código y acompañados de pruebas; confirmar su reproducción con el comando
indicado antes de presentarlos como resultados experimentales.

## Interpretación de informes

JaCoCo cuenta instrucciones de bytecode, líneas, ramas y métodos. Sus ramas
corresponden a if/switch; no cuenta manejo de excepciones como ramas y no
certifica MC/DC. Separar cobertura de ejecución de calidad de aserciones.
No hay exclusiones de clases productivas para aumentar porcentajes.

PIT mide qué cambios artificiales detecta la suite seleccionada. Revisar
sobrevivientes individualmente: falta de escenario, oráculo débil o posible
mutante equivalente. Registrar al menos un caso nuevo derivado del análisis,
y repetir PIT para comprobar el efecto. No afirmar que la herramienta por sí
sola satisface el nivel excelente de la rúbrica.

SpotBugs y Checkstyle: revisar cada hallazgo, descartar falsos positivos con
justificación y crear una prueba o issue derivada cuando proceda. El fichero
Checkstyle usa reglas acotadas de mantenibilidad; no una reforma estética del
proyecto. La configuración permite generar el informe sin bloquear por las
violaciones heredadas; esto no significa que el informe no tenga problemas.

Para la entrega académica todavía faltan la interpretación de resultados,
las issues publicadas con referencia a los tests/commit y la validación de
requisitos no funcionales acordados. El paquete no garantiza una calificación.

## Documentación oficial consultada

- https://www.jacoco.org/jacoco/trunk/doc/counters.html
- https://javadoc.io/static/org.mockito/mockito-core/5.15.2/org/mockito/Mockito.html
- https://pitest.org/quickstart/maven/
