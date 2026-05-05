# Prototipos de baja fidelidad - Running la Safor IPC

## 1. Introducción

Este documento recoge los prototipos de baja fidelidad de la aplicación Running la Safor.

Los prototipos representan la estructura básica de las pantallas principales antes de implementar la interfaz definitiva. Su objetivo es validar la navegación, la distribución de la información y las acciones principales del usuario.

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
```

Acciones principales:

- Introducir nickname.
- Introducir contraseña.
- Iniciar sesión.
- Acceder al registro.

---

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
```

Acciones principales:

- Introducir nickname.
- Introducir email.
- Introducir contraseña.
- Indicar fecha de nacimiento.
- Seleccionar avatar opcional.
- Confirmar registro.

---

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
```

Acciones principales:

- Acceder a actividades.
- Importar una nueva actividad.
- Consultar historial.
- Gestionar mapas.
- Modificar perfil.
- Cerrar sesión.

---

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
```

Acciones principales:

- Ver actividades importadas.
- Importar fichero GPX.
- Visualizar una actividad.
- Renombrar una actividad.
- Borrar una actividad.
- Consultar acumulado mensual.

---

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
```

Acciones principales:

- Ver ruta sobre el mapa.
- Hacer zoom.
- Consultar estadísticas.
- Ver inicio y final de la ruta.
- Añadir anotaciones.
- Ver perfil de desnivel.
- Activar visualización de velocidad sobre el trazado.

---

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
```

Acciones principales:

- Seleccionar tipo de anotación.
- Escribir texto.
- Seleccionar color.
- Indicar grosor.
- Guardar o cancelar la anotación.

---

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
```

Acciones principales:

- Consultar sesiones anteriores.
- Ver duración de cada sesión.
- Ver actividades importadas.
- Ver actividades visualizadas.
- Ver anotaciones creadas.
- Consultar totales acumulados.

---

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
```

Acciones principales:

- Consultar mapas disponibles.
- Añadir un nuevo mapa.
- Seleccionar imagen JPG.
- Introducir coordenadas del bounding box.
- Guardar mapa.

---

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
```

Acciones principales:

- Consultar datos actuales.
- Modificar email.
- Modificar contraseña.
- Cambiar avatar.
- Guardar cambios.

---

## 11. Conclusión

Estos prototipos de baja fidelidad permiten definir la estructura inicial de la interfaz antes de comenzar la implementación.

Las pantallas propuestas cubren los escenarios principales del caso práctico: gestión de usuarios, importación y visualización de actividades, uso del mapa, creación de anotaciones, historial de sesiones, gestión de mapas y modificación del perfil.
