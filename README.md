# GeoEventos GUI

Cliente de escritorio para la plataforma **GeoEventos**, desarrollado en Java con Swing. Permite a empresas y establecimientos locales gestionar sus eventos geolocalizados a través de una interfaz gráfica intuitiva.

> Este repositorio contiene únicamente el cliente de escritorio. La lógica de negocio y el acceso a datos residen en [GeoEventosAPI](https://github.com/AlfredoSWDev/GeoEventosAPI).

---

## ¿Qué es GeoEventos?

GeoEventos es una plataforma que permite a empresas locales **publicar, promocionar y gestionar eventos** en un mapa interactivo, de forma similar a Google Maps. El modelo de negocio es **B2B (Business to Business)**: las empresas pagan por publicar sus eventos, mientras que los usuarios finales acceden a la información de forma totalmente gratuita.

El proyecto inicia con un **MVP CRUD** que permite a los clientes gestionar sus eventos en una base de datos, sentando las bases para la versión completa con mapa interactivo.

---

## Stack Tecnológico

| Tecnología      | Uso |
|-----------------|-----|
| Java 21         | Lenguaje principal |
| Java Swing      | Interfaz gráfica de usuario |
| SwingWorker     | Tareas asíncronas (subida de imágenes) |
| Java HttpClient | Comunicación con la API REST |
| Jackson         | Parseo de respuestas JSON |
| FlatLaf         | Look & Feel moderno para Swing |
| IntelliJ IDEA   | IDE (usa `.form` para diseño UI) |

---

## Arquitectura

El cliente **no se conecta directamente a la base de datos**. Toda la comunicación pasa por la API REST de GeoEventosAPI.

```
GeoEventos GUI
      │
      │  HTTP (JSON / multipart)
      ▼
GeoEventos API  ──►  PostgreSQL
                ──►  ImgBB
```

### Estructura del Proyecto

```
src/main/java/com/alfredo/
├── Main.java                        # Punto de entrada
├── api/
│   └── ApiClient.java               # Cliente HTTP centralizado
├── conf/
│   └── AppConfig.java               # Lector de config.properties
├── data/
│   ├── Conector.java                # Carga y búsqueda en tabla
│   ├── CrearEvento.java             # POST /api/eventos
│   ├── ActualizarEvento.java        # PUT  /api/eventos/{id}
│   ├── BorrarEvento.java            # DELETE /api/eventos/{id}
│   └── LeerEvento.java              # GET /api/eventos/{id}
├── model/
│   ├── Imagenes.java                # DTO respuesta ImgBB
│   ├── ImageData.java
│   └── ImageDetails.java
└── ui/
    ├── Principal.java               # Ventana principal (JTable + botones)
    ├── ManipularEventos.java        # Formulario Crear / Editar / Ver
    ├── SubirFotos.java              # SwingWorker para subida de imágenes
    ├── AbrirExploradorArchivos.java # Selector de archivos local
    └── DesplegarUI.java             # Utilidad para crear JFrames
```

---

## Funcionalidades (MVP)

- **Listar eventos** en una tabla con ID, nombre, valor y lugar
- **Buscar eventos** por nombre o lugar
- **Crear evento** con nombre, descripción, vigencia, valor, lugar y foto
- **Modificar evento** cargando sus datos actuales en el formulario
- **Ver detalle** de un evento en modo solo lectura
- **Eliminar evento** con confirmación previa
- **Subir imágenes** de forma asíncrona (sin congelar la UI)

---

## Requisitos

- Java 21+
- [GeoEventosAPI](https://github.com/tu-usuario/GeoEventosAPI) corriendo en `localhost:8080`

---

## Configuración

Edita `src/main/resources/config.properties`:

```properties
# URL base de la API REST
api.base.url=http://localhost:8080

# ImgBB (referencia, la subida la maneja la API)
imgbb.api.key=TU_API_KEY
imgbb.api.url=https://api.imgbb.com/1/upload
```

---

## Cómo ejecutar

1. Asegúrate de que **GeoEventosAPI** esté corriendo.
2. Clona este repositorio:
   ```bash
   git clone https://github.com/AlfredoSWDev/GeoEventosGUI.git
   ```
3. Abre el proyecto en IntelliJ IDEA.
4. Configura `config.properties` con tu URL de API.
5. Ejecuta `Main.java`.

---

## Flujos Principales

### Carga de datos
```
Principal (windowGainedFocus)
    └── Conector.cargarDatosTabla()
            └── GET /api/eventos
                    └── Llena el JTable
```

### Crear / Editar evento
```
Botón Crear/Editar
    └── ManipularEventos
            ├── (edición) GET /api/eventos/{id}  → precarga formulario
            ├── (foto)    POST /api/imagenes/subir → devuelve URL
            └── Guardar → POST o PUT /api/eventos
```

### Eliminar evento
```
Botón Borrar
    └── Confirmación
            └── DELETE /api/eventos/{id}
```

---

## Roadmap

- [ ] Integración con mapa interactivo (Google Maps / OpenStreetMap)
- [ ] Visualización geolocalizada de eventos
- [ ] Filtros avanzados por categoría, fecha y distancia
- [ ] Panel de estadísticas para clientes B2B
- [ ] Despliegue en AWS

---

## Repositorios del Proyecto

| Repositorio | Descripción |
|-------------|-------------|
| **GeoEventosGUI** | Este repositorio — cliente de escritorio Swing |
| [GeoEventosAPI](https://github.com/AlfredoSWDev/GeoEventosAPI) | API REST Spring Boot |
