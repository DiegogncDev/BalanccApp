# SPEC: Rediseño y Selección Automática de Fecha en Add Balance

**Estado:** Borrador <!-- Borrador | En revisión | Aprobada -->

<!-- PARA LA PERSONA
Copia esta plantilla como SPEC.md en una carpeta de la funcionalidad.
Pide al agente que la complete contigo usando MOBILE_GUIDELINES.md.
SPEC.md define qué debe cumplirse; PLAN.md desarrolla cómo implementarlo;
TASKS.md organiza los pasos de ejecución.
-->

<!-- PARA EL AGENTE
- Lee las instrucciones del proyecto y MOBILE_GUIDELINES.md. Inspecciona el
  repositorio para comprobar el comportamiento actual. Si falta la guía, pide su ubicación.
- Completa esta spec con la persona: investiga lo comprobable y consulta las
  decisiones pendientes. Haz pocas preguntas por vez y actualiza las respuestas.
- No inventes requisitos ni exclusiones. Distingue propuestas de decisiones
  confirmadas y marca como PENDIENTE lo que aún no esté resuelto.
- Aplica las consideraciones mobile relevantes sin ampliar el alcance automáticamente.
- No incluyas diseño de clases, tablas, componentes, archivos o algoritmos:
  esos detalles pertenecen a PLAN.md. Sí registra restricciones explícitas del pedido.
- Mantén el documento breve y proporcional a la funcionalidad. Conserva los comentarios.
- Un documento completo no está aprobado automáticamente. Solicita aprobación
  antes de marcarlo como Aprobada. No implementes durante esta etapa.
-->

## Qué construimos y para quién

Mejorar la experiencia de usuario en la pantalla de agregar balance (`AddBalanceScreen` / `AddIncomeOrExpenseScreen`), transformando la card de selección de fecha para que sea clara, intuitiva y rápida de usar. Se reemplaza la interacción ambigua actual por dos selectores explícitos e independientes (uno para el día y otro para el mes), con la fecha y el mes del día actual seleccionados automáticamente por defecto al ingresar a la pantalla.

## Situación actual

- La card de fecha actual combina en un solo contenedor clicable la apertura del diálogo de selección de día (calendario de maxkeppeler) y un chip desplegable para seleccionar el mes.
- El selector de día se siente "escondido" porque requiere tocar la card entera sin un botón o selector visualmente obvio para el día.
- El selector de mes se confunde visualmente con el texto central que muestra la fecha y el año.
- El campo `selectedDay` inicia vacío (`""`), obligando al usuario a interactuar manualmente con el selector de día en cada registro para que el balance sea válido (`isValid`), incluso si el gasto/ingreso ocurrió hoy.

## Dentro del alcance

- **RF-01: Preselección automática de fecha actual:** Al abrir la pantalla de agregar balance, el día y el mes deben inicializarse automáticamente con los valores de la fecha actual (`LocalDate.now()`), permitiendo guardar sin requerir selección manual obligatoria si la transacción es del día de hoy.
- **RF-02: Card con dos selectores diferenciados y claros:** Rediseñar la sección/card de fecha para presentar dos controles o botones interactivos claramente diferenciados:
  - Selector de Día: Muestra el día seleccionado de forma prominente y permite abrir el selector de día (calendario/modal).
  - Selector de Mes: Muestra el mes seleccionado (localizado en español/inglés) y permite abrir el selector modal de mes.
- **RF-03: Claridad visual y jerarquía:** Eliminar la ambigüedad y superposición de textos confusos entre la fecha formateada y el chip de mes, ofreciendo etiquetas claras ("Día", "Mes") y estados visuales coherentes con el diseño de la app (`MaterialTheme`, `financialColors`, `BrandPurple`).
- **RF-04: Consistencia y sincronización de fecha:** Si el usuario cambia el mes o el día, el estado debe actualizarse correctamente y reflejarse de inmediato en el formulario y en la validación de guardado.

## Fuera de alcance

- Modificar la lógica de persistencia de Room o entidades `BalanceEntity` / `BalanceModel`.
- Cambios en el selector de categoría o en el campo de monto.
- Soporte para selección de hora/minuto.
- Cambios en otras pantallas fuera del flujo de agregar balance.

## Flujo de usuario

1. El usuario navega a la pantalla "Agregar" (Add Balance).
2. La card de fecha se muestra con el día y el mes actuales ya preseleccionados (por ejemplo, "Día: 8", "Mes: Octubre").
3. Si la fecha de la transacción es la de hoy, el usuario no necesita tocar la fecha y puede ingresar el monto y guardar directamente.
4. Si el usuario desea cambiar el día:
   - Presiona el selector de Día específico.
   - Se abre el selector/calendario de días.
   - Elige el día deseado y la card actualiza el valor visible.
5. Si el usuario desea cambiar el mes:
   - Presiona el selector de Mes específico.
   - Se abre el selector de meses.
   - Elige el nuevo mes y la card actualiza el valor visible (ajustando los límites del día si el mes cambia).
6. El usuario completa el monto y guarda el balance con la fecha seleccionada.

## Datos y reglas de negocio

- `selectedDay` no puede quedar vacío al guardar. Debe inicializarse con el día actual (ej. `LocalDate.now().dayOfMonth.toString()`).
- El mes debe inicializarse con el mes actual o el mes seleccionado en la vista previa.
- La selección de día debe ser válida para el mes y año configurados (1 al último día del mes correspondiente).
- El formulario es válido (`isValid = true`) si el monto es válido y el día está presente.

## Comportamiento mobile y casos alternativos

| Situación | Comportamiento esperado |
| --- | --- |
| Carga inicial de la pantalla | Se cargan automáticamente el día y mes correspondientes al día de hoy si no fueron provistos previamente. |
| Cambio de mes | Si el día previamente seleccionado excede los días del nuevo mes (ej. día 31 en febrero), se ajusta automáticamente al último día válido del nuevo mes. |
| Entrada inválida | No se permite seleccionar días fuera del rango del mes/año seleccionado. |
| Cancelar o cerrar diálogo de selección | Se mantiene el valor previamente seleccionado sin alteraciones. |
| Rotación de pantalla / Recreación | El ViewModel retiene el día y mes seleccionados sin reiniciarse. |
| Pasar a segundo plano y regresar | Se conserva el estado seleccionado en el formulario. |
| Idioma del dispositivo (ES/EN) | Los nombres de los meses y etiquetas ("Día", "Mes", "Fecha") se muestran traducidos según el locale. |

**Puntos de la guía no aplicables y motivo:**
- *Conectividad y trabajo en segundo plano:* La selección de fecha es una operación completamente local y síncrona en memoria/UI.

## Restricciones del pedido

- Mantener la arquitectura SDMD (Domain -> Data -> Presentation / Jetpack Compose).
- Utilizar componentes de Material 3 y el sistema de diseño existente (`BrandPurple`, `financialColors`, `RoundedCornerShape`, etc.).
- Sin hardcodear strings (usar `strings.xml` para español e inglés).

## Criterios de Aceptación (Checklist)

- [ ] **CA-01 (Preselección automática):** Al abrir la pantalla de agregar balance, el día y el mes se inicializan automáticamente con la fecha de hoy, permitiendo guardar sin requerir toque adicional en la fecha.
- [ ] **CA-02 (Separación de controles):** La card de fecha cuenta con dos selectores visualmente independientes e intuitivos (uno para Día y otro para Mes) con etiquetas claras y áreas táctiles bien definidas.
- [ ] **CA-03 (Selector de Día):** Al pulsar el control de Día, se abre el diálogo/calendario para seleccionar el día del mes actual/elegido.
- [ ] **CA-04 (Selector de Mes):** Al pulsar el control de Mes, se abre el diálogo de selección de mes.
- [ ] **CA-05 (Soporte bilingüe y accesibilidad):** Textos y meses se muestran en el idioma configurado (es/en) y las áreas de clic cumplen tamaños táctiles estándar (mínimo 48dp).
