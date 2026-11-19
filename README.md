# Saber Pro - Versión II

## Interfaz web

La UI está en [ui](ui/README.md) y usa HTML, CSS y módulos JavaScript nativos. En Windows, inicia el servidor estático con `powershell -ExecutionPolicy Bypass -File ui/serve.ps1` y abre `http://localhost:5500`. El Gateway permite ese origen mediante `UI_ALLOWED_ORIGIN`.

La UI no inventa endpoints. En particular, el listado/filtro de preguntas, edición, reapertura, publicación, archivo y cola automática de revisión requieren nuevas rutas backend antes de habilitar sus acciones.

## Descripción

Esta segunda versión de **Saber Pro** presenta un cambio en su arquitectura, pasando de un enfoque monolítico a una arquitectura basada en **microservicios**.

La aplicación se organiza en servicios independientes, cada uno encargado de una responsabilidad específica. Esto permite separar las funcionalidades del sistema y facilitar su mantenimiento y evolución.

## Diagrama de arquitectura C4

El modelo C4 de esta versión se encuentra disponible en Draw.io mediante el siguiente enlace:

[**Ver arquitectura C4 - Versión II**](https://viewer.diagrams.net/?lightbox=1&highlight=0000ff&nav=1&title=ArquitecturaFinalSaberPro.drawio&dark=auto#Uhttps%3A%2F%2Fdrive.google.com%2Fuc%3Fid%3D186l6JSymGhwhO-eN9q4LTD7VEG902ST9%26export%3Ddownload)

[**Documento**](https://docs.google.com/document/d/1AmxTNa1eKVW7fapF0oKqBbLF0SnMsPxm/edit?usp=sharing&ouid=101853165330918560541&rtpof=true&sd=true)
