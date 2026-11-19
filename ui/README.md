# Interfaz Saber Pro

Interfaz modular en HTML, CSS y JavaScript nativo. Se sirve como sitio estático y consume exclusivamente el API Gateway configurado en `js/config/appConfig.js`.

## Desarrollo local

En Windows, desde la raíz del repositorio, ejecuta `powershell -ExecutionPolicy Bypass -File ui/serve.ps1` y abre `http://localhost:5500`. El script usa `HttpListener` del sistema, sin instalar dependencias. El Gateway permite ese origen por CORS (`UI_ALLOWED_ORIGIN`). No abras `index.html` con `file://`, porque el navegador bloquea los módulos ES.

## Rutas y capacidades disponibles

- Usuarios: alta, listado, edición y desactivación.
- Preguntas: alta, consulta por UUID y envío a revisión.
- Revisiones: carga manual de una pregunta por UUID, consulta de sus revisiones y registro de decisión.
- Auditoría: consulta de solo lectura con filtros y paginación.
- Quizzes: estado “Próximamente”.

El backend actual no expone listado/búsqueda de preguntas, `PUT /api/questions/{id}`, `/reopen`, `/publish`, `/archive`, ni una ruta para obtener la cola de preguntas pendientes. Por eso los filtros de pregunta quedan visibles pero deshabilitados, la consulta es por UUID, y las acciones sin endpoint se muestran desactivadas con explicación. La opción propuesta es añadir esos endpoints al contrato de MCQuestion y un endpoint de preguntas pendientes para REVIEWER antes de activar esas operaciones en la UI.

El error 400 actual de los servicios devuelve un mensaje general, no un mapa de errores por campo. La UI pinta errores por campo si el API entrega `errors` o `fieldErrors`; mientras tanto presenta el mensaje general. Para resaltar campos con precisión se propone ampliar el contrato de error del backend.

El rol decodificado del JWT solo controla visibilidad y navegación. Cada solicitud sigue enviando el token al Gateway, que conserva la autorización real. La sesión se guarda en `sessionStorage` y vence según el claim `exp`; no hay cierre de sesión ni refresh token porque el backend no los ofrece.