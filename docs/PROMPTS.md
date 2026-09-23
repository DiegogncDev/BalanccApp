Quiero que me ayudes a escribir la especificación de una nueva funcionalidad para BalanccApp, una app Android de finanzas personales para registrar ingresos y gastos por mes y año.

La funcionalidad a especificar es: [describe aquí la funcionalidad]

Primero, revisa el proyecto para entender cómo funciona y qué convenciones sigue:

- Lee `docs/GENERIC_RULES.md` y `docs/CURRENT_ARCHITECTURE.md`.
- Revisa `docs/MOBILE_GUIDELINES.md` e incorpora al proceso SDMD las revisiones que sean pertinentes.
- Investiga el código actual (UI Compose, ViewModels, use cases, Domain, Data, Room, Hilt, navegación) para comprobar el comportamiento existente y la convivencia de la nueva funcionalidad con lo ya implementado.

Después, prepara un borrador de `SPEC.md` usando `docs/SPEC_TEMPLATE.md` con la información que podamos comprobar, para completarlo y corregirlo juntos.

La especificación debe definir:

- Objetivo y comportamiento esperado.
- Qué está incluido y qué queda fuera del alcance.
- Flujos, reglas de negocio y casos de error.
- Criterios de aceptación concretos y cómo validar cada uno.
- Restricciones técnicas y decisiones pendientes.

Distingue claramente en el borrador:

- Hechos comprobados en el código.
- Decisiones confirmadas por mí.
- Puntos pendientes (márcalos como PENDIENTE).

No asumas decisiones que no estén definidas: pregúntame antes de incorporarlas. Puedes proponer opciones y recomendar una, pero espera mi confirmación antes de reflejarla como decisión tomada.

Hazme pocas preguntas por vez y actualiza la especificación con mis respuestas.

No implementes nada hasta que revisemos y aprobemos explícitamente la especificación.
