# Prototipos de baja fidelidad - Running la Safor IPC

## 1. Introducción

Este documento recoge los prototipos de baja fidelidad de la aplicación Running la Safor.

---

## 2. Pantalla de inicio de sesión

```text
+------------------------------------------------+
|                Running la Safor                |
+------------------------------------------------+
|                                                |
| Nickname                                       |
| [__________________________________________]   |
|                                                |
| Contraseña                                     |
| [__________________________________________]   |
|                                                |
| [ Iniciar sesión ]                             |
|                                                |
| ¿No tienes cuenta? [ Registrarse ]             |
|                                                |
+------------------------------------------------+

## 3. Pantalla de registro

```text
+------------------------------------------------+
|                  Registro                      |
+------------------------------------------------+
| Nickname                                       |
| [__________________________________________]   |
|                                                |
| Email                                          |
| [__________________________________________]   |
|                                                |
| Contraseña                                     |
| [__________________________________________]   |
|                                                |
| Fecha de nacimiento                            |
| [____ / ____ / ______]                         |
|                                                |
| Avatar                                         |
| [ Seleccionar imagen ]                         |
|                                                |
| [ Registrarse ]        [ Volver ]              |
+------------------------------------------------+

## 4. Pantalla principal

```text
+--------------------------------------------------------------+
| Running la Safor                              Usuario / Avatar |
+----------------------+---------------------------------------+
| Actividades          |                                       |
| Importar GPX         |                                       |
| Historial            |          CONTENIDO PRINCIPAL          |
| Mapas                |                                       |
| Perfil               |                                       |
| Cerrar sesión        |                                       |
+----------------------+---------------------------------------+

## 5. Pantalla de actividades

```text
+--------------------------------------------------------------+
| Actividades                                      [Importar]   |
+--------------------------------------------------------------+
| Nombre                Fecha             Distancia   Duración |
|--------------------------------------------------------------|
| Carrera Valencia      12/04/2026        10.2 km     55 min   |
| Calderona Trail       20/04/2026        15.8 km     1h 40m   |
| Ruta Pirineos         25/04/2026        8.5 km      50 min   |
+--------------------------------------------------------------+
| [ Visualizar ]   [ Renombrar ]   [ Borrar ]                  |
+--------------------------------------------------------------+
| Acumulado mensual:                                           |
| Distancia total: __ km | Tiempo total: __ | Desnivel: __ m    |
+--------------------------------------------------------------+

## 6. Pantalla de visualización de actividad

```text
+--------------------------------------------------------------------+
| Carrera Valencia                               [ Zoom + ] [ Zoom - ] |
+--------------------------------------------+-----------------------+
|                                            | Estadísticas          |
|                                            |-----------------------|
|                                            | Distancia: __ km      |
|                                            | Duración: __          |
|                  MAPA                      | Velocidad media: __   |
|                                            | Ritmo medio: __       |
|        Ruta dibujada sobre el mapa         | Desnivel +: __ m      |
|                                            | Desnivel -: __ m      |
|        Inicio verde / Final rojo           | Altitud mín: __ m     |
|                                            | Altitud máx: __ m     |
+--------------------------------------------+-----------------------+
| [ Añadir anotación ]   [ Ver velocidad sobre trazado ]             |
+--------------------------------------------------------------------+
| Perfil de desnivel                                                 |
|                                                                    |
|       gráfica distancia / altitud                                  |
|                                                                    |
+--------------------------------------------------------------------+

## 7. Pantalla de añadir anotación

```text
+------------------------------------------------+
|              Añadir anotación                  |
+------------------------------------------------+
| Tipo de anotación                              |
| [ Punto / Texto / Línea / Círculo ]            |
|                                                |
| Texto                                          |
| [__________________________________________]   |
|                                                |
| Color                                          |
| [ Seleccionar color ]                          |
|                                                |
| Grosor                                         |
| [__________]                                   |
|                                                |
| [ Guardar ]                 [ Cancelar ]       |
+------------------------------------------------+

## 8. Pantalla de historial de sesiones

```text
+----------------------------------------------------------------+
| Historial de sesiones                                          |
+----------------------------------------------------------------+
| Inicio           Fin              Duración   GPX   Vistas  Anot. |
|----------------------------------------------------------------|
| 10:00            10:35            35 min     1     2       1     |
| 17:20            18:00            40 min     2     3       0     |
| 09:15            09:45            30 min     1     1       2     |
+----------------------------------------------------------------+
| Totales                                                        |
| Actividades importadas: __                                     |
| Actividades visualizadas: __                                   |
| Anotaciones creadas: __                                        |
+----------------------------------------------------------------+

## 9. Pantalla de gestión de mapas

```text
+--------------------------------------------------------------+
| Gestión de mapas                                             |
+--------------------------------------------------------------+
| Mapas disponibles                                            |
|--------------------------------------------------------------|
| Valencia                                                     |
| Sierra Calderona                                             |
| Pirineos                                                     |
+--------------------------------------------------------------+
| Añadir nuevo mapa                                            |
|                                                              |
| Nombre del mapa                                              |
| [__________________________________________]                 |
|                                                              |
| Imagen JPG                                                   |
| [ Seleccionar archivo ]                                      |
|                                                              |
| Latitud mínima   [____________]                              |
| Latitud máxima   [____________]                              |
| Longitud mínima  [____________]                              |
| Longitud máxima  [____________]                              |
|                                                              |
| [ Guardar mapa ]                                             |
+--------------------------------------------------------------+

## 10. Pantalla de modificación de perfil

```text
+------------------------------------------------+
|              Modificar perfil                  |
+------------------------------------------------+
| Nickname                                       |
| [ nickname no editable ]                       |
|                                                |
| Email                                          |
| [__________________________________________]   |
|                                                |
| Nueva contraseña                               |
| [__________________________________________]   |
|                                                |
| Fecha de nacimiento                            |
| [____ / ____ / ______]                         |
|                                                |
| Avatar actual                                  |
| [ imagen ]                                     |
|                                                |
| Nuevo avatar                                   |
| [ Seleccionar imagen ]                         |
|                                                |
| [ Guardar cambios ]       [ Cancelar ]         |
+------------------------------------------------+
