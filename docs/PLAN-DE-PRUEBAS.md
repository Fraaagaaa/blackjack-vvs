# Plan de validación y verificación

## Línea base y alcance

SUT: `Fraaagaaa/blackjack-vvs`, rama `master`, commit
`1f0d5a72f32f82cc903a6669eb62f0646e1c9d3d` (verificado el 20-10-2026).
El ZIP coincide con esa versión salvo finales de línea CRLF. No se modifica
`src/`, `build.xml` ni el README original. Maven se incorpora como vía adicional
para compilar y ejecutar la validación con JDK 17.

Fuentes de requisitos: README y contratos Javadoc del SUT, indicaciones del
profesor, `Validación.pdf`, `rubrica.pdf` y el diagrama del ciclo de pruebas.
Los detalles deducidos del código se distinguen de las reglas especificadas.
La especificación es incompleta: no se inventan requisitos de rechazo de valores
negativos o nulos, ni reglas de casino que este programa no declara.

## Requisitos y oráculos

| ID | Comportamiento esperado | Pruebas principales |
|---|---|---|
| R01 | Carta conserva rango/palo; boca abajo oculta su identidad | CardTest, CardHandIT |
| R02 | Baraja FIFO; extraer vacía devuelve null; barajar conserva cartas | DeckTest, DealerPlayerIT |
| R03 | Valores 2–10; figuras 10; as 1 u 11 para el mejor total sin exceder 21 | HandTest, HandProperties, CardHandIT |
| R04 | HIT añade una carta; impide split/double/surrender posteriores | ActionHitTest, RoundLifecycleIT |
| R05 | STAND termina la mano | ActionStandTest, PlayerHumanTest, RoundLifecycleIT |
| R06 | DOUBLE duplica apuesta, da una carta y termina la mano | ActionDoubleTest, DealerPlayerIT |
| R07 | SPLIT separa valores iguales; máximo tres manos extra; ases reciben una carta extra por mano | ActionSplitTest, ActionSplitDefectTest, RoundLifecycleIT |
| R08 | SURRENDER termina y pierde media apuesta | ActionSurrenderTest, DealerTest, DealerPlayerIT |
| R09 | CPU pide hasta 17 y se planta en 17, incluido 17 blando | PlayerCPUTest, DealerPlayerIT |
| R10 | Apuesta entre mínimo y saldo; entrada inválida se repite | PlayerHumanTest |
| R11 | Ganar, perder, empate, blackjack y rendición liquidan apuesta una vez | DealerTest, DealerPlayerIT |
| R12 | Recoger cartas y reiniciar permite otra ronda | HandTest, PlayerHumanTest, RoundLifecycleIT |
| R13 | Partida termina por saldo agotado o salida del usuario | BlackJackGameTest, PlayerHumanTest |

`Outcome.getMultiplier` se aplica al fondo combinado, no directamente a la
apuesta: por ejemplo 1.25 sobre 20 devuelve 25 (beneficio de 15 sobre apuesta 10).
Los tests económicos usan apuestas pares para no inventar una política de
redondeo; apuestas impares y banca insuficiente requieren aclaración de contrato.

## Diseño de datos

- Particiones: vacía/no vacía; carta visible/oculta; as/figura/numeral; acción
  permitida/prohibida; entrada válida/no numérica/fuera de rango; cada desenlace.
- Fronteras: saldo 0/1/2 con mínimo 1; apuestas 0/1/99/100/101 con saldo 100;
  CPU 16/17/18; puntuaciones 20/21/22; cero a tres manos adicionales y cuarta
  solicitud rechazada; baraja 0/1/2/52 y extracción 53.
- Aleatorios: jqwik genera 200 listas de rangos, tamaño 0–12 y semilla
  reproducible 20261020. El oráculo enumera sumas posibles de los ases,
  independientemente de la fórmula implementada en Hand. Las pruebas de shuffle
  comprueban conservación, nunca que el orden necesariamente cambie.
- Cada caso contiene una aserción o una verificación de interacción sobre un
  aspecto. Una colección puede representar un único resultado (orden FIFO,
  bifurcación de manos), sin agrupar requisitos no relacionados.

## Aislamiento e integración incremental

La suite usa objetos nuevos por caso y no depende del orden. Hand recibe ICard
mock; acciones reciben Hand/IDealer/IBanker mock; Player se prueba mediante una
subclase mínima con Hand mock. Cuando el SUT crea sus colaboradores con `new`,
Mockito `mockConstruction` intercepta Hand, Deck o los participantes de la partida.
Los ámbitos se cierran con try-with-resources. No se usan reflexión sobre campos
privados ni cambios de producción para facilitar las pruebas.

1. Card + Hand: quitar dobles de cartas y comprobar valores/visibilidad.
2. Dealer + Deck + Player + Hand: reparto, agotamiento, reciclado y apuestas.
3. Acciones + participantes reales: hit, double, surrender y split.
4. PlayerHuman + consola en memoria + acciones + banca: secuencias de turno.
5. ArchUnit: contratos y dependencias de producción.

Los archivos `*IT` se ejecutan mediante Failsafe tras los `*Test` de Surefire.
Las reproducciones de defectos conservan el oráculo correcto y llevan
`@Tag("known-bug")`. **El perfil por defecto excluye esos tests**: una ejecución
verde no significa ausencia de defectos. `-Pdefects` los incluye y debe fallar
mientras permanezcan los errores. No se usa `@Disabled` ni se ajusta la salida
esperada al resultado incorrecto.

## Técnica, clasificación y herramienta

| Herramienta | Técnica | Clasificación |
|---|---|---|
| JUnit 5 + Mockito | Escenarios, fronteras y mocks | Dinámica funcional, caja negra en el oráculo; instrumentación de construcción para aislar |
| jqwik | Generación de datos y propiedades | Dinámica funcional, caja negra |
| SpotBugs | Inspección de bytecode | Estática funcional, caja blanca |
| Checkstyle | Reglas de mantenibilidad | Estática no funcional, caja blanca |
| ArchUnit | Reglas arquitectónicas sobre clases | Dinámica estructural, caja blanca según clasificación docente |
| JUnit assertTimeout | Presupuesto de duración | Dinámica no funcional; se conocen bucles/carga del SUT |
| JaCoCo | Instrumentación y cobertura | Medición de instrucciones, líneas, ramas y métodos |
| PIT | Mutación | Evaluación de sensibilidad de las aserciones |

Las pruebas temporales son presupuestos exploratorios propuestos: 100 000
operaciones de ida/vuelta de baraja en 5 s y 10 000 reciclados completos en 10 s.
No son SLA suministrados por el profesor, benchmarks JMH ni mediciones de memoria.
La segunda es integración, no una prueba del sistema interactivo completo.
Se ejecutan explícitamente con `-Pperformance`; se recomienda acordar requisitos
y medir en el equipo objetivo antes de usarlas como criterio de aceptación.

## Criterio de cierre y límites

Cada método público implementado debe aparecer en la matriz de trazabilidad;
las interfaces se ejercitan a través de implementaciones y no tienen código
propio ejecutable. No se testea el compilador a través de `Enum.values/valueOf`.
El constructor se prueba mediante estado observable o uso del objeto.

Aceptar la infraestructura si la suite normal e integración pasan, los defectos
se reproducen, los informes se generan y las limitaciones están explicadas.
La aceptación del producto queda abierta mientras haya discrepancias confirmadas.
No se exige 100% artificial excluyendo clases de JaCoCo. No se confunde cobertura
de ramas con MC/DC ni con cobertura de todas las secuencias de estados.
