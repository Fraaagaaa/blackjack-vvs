# Validación del blackjack — VVS

Este añadido conserva el juego original y aporta pruebas, configuración y
trazabilidad. Requiere **JDK 17 y Maven 3.9+** para la suite; el README original
describe la aplicación histórica con Ant.

## Ejecución

Desde la carpeta que contiene `pom.xml`:

```bash
mvn clean verify
```

Ejecuta pruebas unitarias, propiedades jqwik e integración/arquitectura.
Informes: `target/surefire-reports`, `target/failsafe-reports` y
`target/site/jacoco/index.html`.

```bash
# Reproducciones: se espera fallo mientras los bugs sigan presentes.
mvn -Pdefects -Dtest='*DefectTest' test

# Incluye también las comprobaciones temporales exploratorias.
mvn -Pperformance verify

# Análisis estático funcional y de mantenibilidad.
mvn compile spotbugs:spotbugs checkstyle:checkstyle

# Mutación sobre tests de comportamiento e integración que pasan.
mvn test-compile org.pitest:pitest-maven:mutationCoverage
```

PIT excluye reproducciones que fallan y pruebas temporales: necesita línea base
verde. La propiedad jqwik se ejecuta en verify, pero no forma parte de targetTests
de PIT en esta configuración. Consulte el alcance y los resultados antes de
interpretar el porcentaje de mutación.

No confundir la suite verde por defecto con aceptación del SUT: **los defectos
conocidos se excluyen por etiqueta**, no están corregidos. Tampoco se asigna una
nota automática a la práctica.

## Documentación

- [Plan y selección de datos](docs/PLAN-DE-PRUEBAS.md)
- [Modelos de estados](docs/ESTADOS.md)
- [API pública y pruebas](docs/TRAZABILIDAD.md)
- [Discrepancias para issues individuales](docs/INCIDENCIAS.md)
- [Resultados reales y límites](docs/RESULTADOS.md)

La CI de `.github/workflows/tests.yml` ejecuta verify y conserva informes.
El análisis estático y la mutación se ejecutan con los comandos anteriores;
no están incluidos en cada push para evitar una ejecución costosa innecesaria.

## Incorporación al fork

El ZIP contiene únicamente archivos añadidos. Copiar su contenido sobre vuestro
checkout de `Fraaagaaa/blackjack-vvs`, junto a `src/` y `build.xml`. Crear una rama para revisión, ejecutar verify y revisar
las incidencias antes de publicarlas. No hace falta reemplazar `src/`.
