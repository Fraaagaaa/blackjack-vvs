# Discrepancias y decisiones de contrato

Estos textos están preparados para issues individuales en
`https://github.com/Fraaagaaa/blackjack-vvs/issues`. No se han publicado:
la conexión devuelve permiso `push: false`. Referencia de la línea base:
`1f0d5a72f32f82cc903a6669eb62f0646e1c9d3d`. Los nombres de tests y la técnica
permiten localizar el hallazgo sin inventar un commit remoto de pruebas.
Resultados de ejecución: ver `RESULTADOS.md` y evidencias adjuntas.

## BUG001 — SPLIT de ases reparte dos cartas adicionales a cada mano

- Contrato: Javadoc de ActionSplit: al separar ases, el repartidor da solo una
  carta adicional a cada mano y no permite pedir más.
- Reproducción: dos ases visibles, apuesta 10, saldo suficiente, ejecutar split.
- Esperado: tamaños `[2, 2]` al acabar la acción.
- Observado en código: primero reparte una carta a cada mano; el bloque especial
  de ases vuelve a repartir otra, dejando `[3, 3]`.
- Prueba: `ActionSplitDefectTest.BUG001_splitAcesMustReceiveExactlyOneAdditionalCardEach`.
- Técnica: dinámica funcional de integración, partición ases frente a otros pares.
- Impacto: altera puntuación, resultado y cartas consumidas en una acción normal.

## BUG002 — SPLIT sin huecos elimina una carta de la mano original

- Contrato: límite de cuatro manos totales; una separación que no puede
  ejecutarse no debe perder una carta.
- Reproducción: consumir tres manos adicionales, crear un par apostado y llamar
  directamente a ActionSplit.execute.
- Esperado: la mano original conserva dos cartas.
- Observado en código: `remove(duplicate)` se ejecuta antes de comprobar que
  `nextHand()` devuelve una mano; permanece una sola carta.
- Prueba: `ActionSplitDefectTest.BUG002_rejectedSplitMustNotLoseOriginalCard`.
- Técnica: dinámica de integración, frontera de capacidad y atomicidad.
- Alcance: robustez de API. La interfaz de consola normalmente evita ofrecer
  esta acción cuando se alcanza el límite. No afirmar que ocurre por el menú.

## BUG003 — El iterador mutable permite desincronizar el contador de ases

- Contrato: getOptimalValue debe representar las cartas que hay en la mano.
- Reproducción: mano as + dos; obtener iterator, avanzar al as y llamar remove.
- Esperado: valor óptimo 2.
- Observado en código: el contador de ases queda a 1 y se calcula 12.
- Prueba: `HandDefectTest.BUG003_iteratorRemovalMustKeepAceScoreConsistent`.
- Técnica: dinámica unitaria, secuencia de operaciones públicas y consistencia.
- Alternativa de corrección: prohibir remove mediante un iterador no modificable
  o mantener los invariantes al eliminar. En ese caso actualizar el escenario
  para el contrato de iterador elegido, sin aceptar silenciosamente el valor 12.

## BUG004 — Quitar la última carta oculta no actualiza isRevealed

- Contrato interpretado: isRevealed describe si todas las cartas actuales están
  visibles, coherente con addElement/reveal/clear. Confirmar con el equipo.
- Reproducción: dos visible + as oculto; eliminar el as.
- Esperado: isRevealed true.
- Observado en código: permanece false.
- Prueba: `HandDefectTest.BUG004_removingLastHiddenCardMustRevealRemainingHand`.
- Técnica: dinámica unitaria, transición oculta → visible.
- Alcance: metadato obsoleto de la API; el bucle principal no consulta este flag.

## BUG005 — HIT prohibido vuelve a habilitar el permiso de pedir

- Contrato: después de DOUBLE y de separar ases no se permite HIT.
- Reproducción: mano con hit deshabilitado, llamar directamente a ActionHit.
- Esperado: seguir sin permiso de pedir cartas.
- Observado en código: no reparte en la primera llamada, pero ejecuta
  `setAllowedHit(true)` y permite que la siguiente sí reparta.
- Prueba: `ActionHitDefectTest.BUG005_deniedHitMustNotRestoreHitPermission`.
- Técnica: dinámica unitaria, transición inválida y persistencia de permisos.
- Alcance: robustez de API; PlayerHuman filtra inicialmente esta acción.

## Contratos que deben aclararse, no contados como bugs confirmados

- `getBet` sin apuesta registrada: desempaquetado null produce NullPointerException.
  ¿Precondición «debe existir apuesta» o devolución de cero/excepción de dominio?
- EOF en consola: los bucles repiten la lectura; posible no terminación.
  Debe acordarse cómo salir al cerrar la entrada y probarse en subproceso para
  poder cancelar sin dejar un hilo bloqueado.
- Negativos y overflow en operaciones monetarias: no existe contrato documentado
  de rechazo. No convertir suposiciones en pruebas normativas.
- Apuestas impares y banca insuficiente: concretar redondeo y pago disponible.
- Llegar exactamente a 21 con HIT termina la mano (`< 21`): el Javadoc dice
  devolver mano si no está pasada, mientras el menú no ofrece más acciones a 21.
  Puede ser una inconsistencia documental, no necesariamente un error de juego.
