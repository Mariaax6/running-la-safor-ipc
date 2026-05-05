# Diseño conceptual - Running la Safor IPC

## 1. Introducción

Este documento recoge el diseño conceptual de la aplicación Running la Safor, desarrollada para la asignatura Interfaces Persona Computador.

La aplicación permitirá a los socios del club registrar actividades deportivas al aire libre mediante ficheros GPX, visualizar el trazado sobre un mapa, consultar estadísticas, añadir anotaciones y revisar su historial de uso.

El objetivo de este diseño conceptual es identificar los principales objetos de tarea, sus atributos, sus acciones y la estructura general de navegación de la aplicación antes de comenzar la implementación.

---

## 2. Escenarios de uso considerados

A partir del caso práctico se han identificado los siguientes bloques de escenarios:

### 2.1 Usuarios

- Registro de un nuevo usuario.
- Autenticación mediante nickname y contraseña.
- Modificación del perfil del usuario.
- Cierre de sesión.
- Consulta del historial de sesiones.

### 2.2 Actividades

- Importación de una actividad nueva desde un fichero GPX.
- Visualización de una actividad registrada.
- Consulta de estadísticas de una actividad.
- Consulta del acumulado de actividades.
- Eliminación de una actividad.

### 2.3 Ruta en el mapa

- Visualización del trazado de la actividad sobre un mapa.
- Identificación visual del punto inicial y final de la ruta.
- Ampliación y reducción del mapa mediante zoom.
- Visualización de anotaciones asociadas a la ruta.

### 2.4 Anotaciones

- Creación de anotaciones geográficas sobre una actividad.
- Selección del tipo de anotación: punto, texto, línea o círculo.
- Introducción de texto descriptivo.
- Selección de color y grosor de la anotación.

### 2.5 Análisis de actividad

- Visualización del perfil de desnivel.
- Visualización de la velocidad sobre el trazado.
- Relación entre puntos de la gráfica y puntos del mapa.

### 2.6 Mapas

- Consulta de mapas disponibles.
- Añadido de un nuevo mapa al sistema.
- Introducción de la imagen del mapa y de sus coordenadas geográficas.

---

## 3. Objetos de tarea

Los objetos de tarea son aquellos elementos principales con los que interactúa el usuario en la aplicación.

| Objeto de tarea | Descripción | Atributos principales | Acciones del usuario |
|---|---|---|---|
| Usuario | Persona registrada en la aplicación | nickname, email, contraseña, fecha de nacimiento, avatar | registrarse, iniciar sesión, modificar perfil, cerrar sesión |
| Sesión | Periodo de uso de la aplicación | inicio, fin, duración, actividades importadas, actividades vistas, anotaciones creadas | consultar historial |
| Actividad | Actividad deportiva importada desde un fichero GPX | nombre, fecha, distancia, duración, velocidad media, ritmo, desnivel, altitud mínima y máxima | importar, visualizar, renombrar, borrar |
| Ruta | Trazado GPS de una actividad | punto inicial, punto final, recorrido | visualizar en mapa, hacer zoom |
| Mapa | Imagen geográfica sobre la que se representa una ruta | nombre, imagen, latitud mínima/máxima, longitud mínima/máxima | consultar, añadir, eliminar si no se usa |
| Anotación | Marca creada por el usuario sobre una ruta | tipo, texto, color, grosor, posición geográfica | crear, visualizar, modificar color, borrar |
| Perfil de desnivel | Representación gráfica de la altitud de la actividad | distancia, altitud | consultar, relacionar con el mapa |
| Velocidad sobre trazado | Representación visual de la velocidad en diferentes tramos | tramo, velocidad | activar o desactivar visualización |

---

## 4. Relaciones entre objetos

Las relaciones principales entre los objetos de tarea son:

```text
Usuario
 ├── tiene muchas Actividades
 ├── tiene muchas Sesiones
 └── tiene un Avatar

Actividad
 ├── pertenece a un Usuario
 ├── contiene una Ruta
 ├── tiene Estadísticas
 ├── tiene Anotaciones
 └── se representa sobre un Mapa

Ruta
 ├── tiene Punto inicial
 ├── tiene Punto final
 └── está formada por puntos GPS

Anotación
 ├── pertenece a una Actividad
 ├── tiene un Tipo
 ├── tiene Texto
 ├── tiene Color
 └── tiene una o varias posiciones geográficas

Mapa
 ├── tiene una Imagen
 ├── tiene un Bounding Box
 └── puede cubrir una Actividad

Sesión
 ├── pertenece a un Usuario
 ├── tiene duración
 └── registra estadísticas de uso
