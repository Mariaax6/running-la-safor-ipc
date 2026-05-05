# Decisiones de implementación - Running la Safor IPC

## 1. Introducción

En este documento se describen las decisiones adoptadas para la implementación de la aplicación Running la Safor a partir del diseño conceptual previamente definido.

El objetivo es establecer una base clara sobre cómo se desarrollará la aplicación, qué herramientas se utilizarán y cómo se estructurará el sistema.

---

## 2. Uso de la librería proporcionada

La aplicación utilizará la librería proporcionada `IPC2026.jar`, la cual incluye:

- Gestión de usuarios
- Gestión de sesiones
- Importación de actividades desde ficheros GPX
- Cálculo de estadísticas (distancia, velocidad, desnivel, etc.)
- Gestión de anotaciones
- Gestión de mapas
- Persistencia de datos en base de datos

No se reimplementará ninguna de estas funcionalidades.  
La aplicación se centrará exclusivamente en la interfaz gráfica y en la interacción con dicha librería.

---

## 3. Arquitectura de la aplicación

La aplicación se desarrollará utilizando JavaFX y seguirá una estructura basada en:

- Vistas (FXML)
- Controladores (Java)
- Uso de la librería como modelo de datos

Se separará la lógica de interfaz de la lógica de datos para mantener un diseño limpio y organizado.

---

## 4. Organización de la interfaz

Se utilizará un `BorderPane` como contenedor principal de la aplicación:

- Parte superior: información del usuario o cabecera
- Parte lateral: menú de navegación
- Parte central: contenido dinámico

Las distintas pantallas se cargarán dentro del área central en función de la opción seleccionada por el usuario.

---

## 5. Vistas principales

Las vistas que se implementarán son:

- Vista de inicio de sesión
- Vista de registro
- Vista principal
- Vista de actividades
- Vista de visualización de actividad (mapa)
- Vista de anotaciones
- Vista de historial de sesiones
- Vista de gestión de mapas
- Vista de perfil de usuario

---

## 6. Navegación

La navegación entre las distintas funcionalidades se realizará dentro de una única ventana principal.

No se abrirán múltiples ventanas independientes.  
El contenido se actualizará dinámicamente dentro del `BorderPane`.

---

## 7. Gestión de actividades

- Las actividades se importarán desde ficheros GPX.
- Se mostrarán en una lista dentro de la aplicación.
- El usuario podrá seleccionar una actividad para visualizarla.
- Se permitirá eliminar actividades.

---

## 8. Visualización en mapa

- El mapa mostrará el trazado de la actividad.
- Se permitirá hacer zoom sobre el mapa.
- Se indicará el punto inicial (color verde) y final (color rojo).
- Se podrán visualizar anotaciones sobre la ruta.

---

## 9. Anotaciones

- Se podrán crear anotaciones sobre una actividad.
- Tipos de anotación: punto, texto, línea y círculo.
- Cada anotación tendrá:
  - Texto descriptivo
  - Color
  - Grosor

---

## 10. Historial de sesiones

- Se mostrará una lista de sesiones de uso.
- Se incluirán datos como duración, actividades importadas y visualizadas.
- Se mostrarán estadísticas acumuladas.

---

## 11. Gestión de mapas

- Se podrán añadir nuevos mapas al sistema.
- Cada mapa se definirá mediante:
  - Imagen en formato JPG
  - Coordenadas geográficas (bounding box)

---

## 12. Control de versiones

El desarrollo del proyecto se realizará utilizando GitHub:

- Se realizarán commits frecuentes
- Cada integrante contribuirá en su parte asignada
- Los commits tendrán mensajes claros y descriptivos

---

## 13. Conclusión

Las decisiones de implementación permiten estructurar el desarrollo de la aplicación de forma clara y organizada, aprovechando la librería proporcionada y manteniendo coherencia con el diseño conceptual definido previamente.
