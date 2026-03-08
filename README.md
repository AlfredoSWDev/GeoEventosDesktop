# GeoEventos GUI

Cliente de escritorio para la plataforma **GeoEventos**, desarrollado en Java con Swing. Permite a empresas y establecimientos locales gestionar sus eventos geolocalizados a través de una interfaz gráfica intuitiva con mapa interactivo embebido.

> Este repositorio contiene únicamente el cliente de escritorio. La lógica de negocio y el acceso a datos residen en [GeoEventosAPI](https://github.com/AlfredoSWDev/GeoEventosAPI).

---

## ¿Qué es GeoEventos?

GeoEventos es una plataforma que permite a empresas locales **publicar, promocionar y gestionar eventos** en un mapa interactivo, de forma similar a Google Maps. El modelo de negocio es **B2B (Business to Business)**: las empresas pagan por publicar sus eventos, mientras que los usuarios finales acceden a la información de forma totalmente gratuita.

El proyecto inicia con un **MVP CRUD** que permite a los clientes gestionar sus eventos en una base de datos, con visualización geolocalizada a través de un mapa OpenStreetMap embebido.

---

## Stack Tecnológico

| Tecnología | Uso |
|------------|-----|
| Java 23 | Lenguaje principal |
| Java Swing | Interfaz gráfica de usuario |
| JavaFX WebView | Mapa embebido en la ventana Swing |
| Leaflet + OpenStreetMap | Mapa interactivo (sin API key, 100% gratuito) |
| SwingWorker | Tareas asíncronas (subida de imágenes) |
| Java HttpClient | Comunicación con la API REST |
| Jackson | Parseo de respuestas JSON |
| FlatLaf | Look & Feel moderno para Swing |
| IntelliJ IDEA | IDE (usa `.form` para diseño UI) |
| JUnit 5 | Framework de testing |
| Mockito | Mocks para unit tests |
| WireMock | Servidor HTTP falso para integration tests |
| AssertJ | Assertions fluidas |

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

Mapa embebido:
Swing JFrame
    └── JFXPanel (JavaFX)
            └── WebView
                    └── mapa.html + Leaflet + OpenStreetMap
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
    ├── Principal.java               # Ventana principal (JTable + mapa + botones)
    ├── MapaPanel.java               # Mapa Leaflet embebido via JavaFX WebView
    ├── ManipularEventos.java        # Formulario Crear / Editar / Ver
    ├── SubirFotos.java              # SwingWorker para subida de imágenes
    ├── AbrirExploradorArchivos.java # Selector de archivos local
    └── DesplegarUI.java             # Utilidad para crear JFrames

src/main/resources/
    ├── config.properties            # Configuración de la app
    └── mapa.html                    # HTML con Leaflet para el mapa embebido

src/test/java/com/alfredo/
├── model/
│   └── ImagenesTest.java            # Unit tests — deserialización JSON
├── data/
│   ├── ConectorTest.java            # Unit tests — carga y búsqueda en tabla
│   └── LeerEventoTest.java          # Unit tests — lectura de evento por ID
└── api/
    └── ApiClientIntegrationTest.java # Integration tests — GET/POST/PUT/DELETE

src/test/resources/
    └── config.properties            # Apunta a WireMock (localhost:8089)
```

---

## Funcionalidades

- **Listar eventos** en una tabla con ID, nombre, valor y lugar
- **Buscar eventos** por nombre o lugar
- **Crear evento** con nombre, descripción, vigencia, valor, lugar, coordenadas y foto
- **Modificar evento** cargando sus datos actuales en el formulario
- **Ver detalle** de un evento en modo solo lectura
- **Eliminar evento** con confirmación previa
- **Subir imágenes** de forma asíncrona (sin congelar la UI)
- **Mapa interactivo** con marcadores geolocalizados por evento
- **Click en marcador** para ver el detalle del evento directamente desde el mapa
- **Click en el mapa** para asignar coordenadas al crear o editar un evento
- **Mostrar/ocultar mapa** con un botón desde la ventana principal

---

## Requisitos

- Java 23+
- JavaFX 23
- [GeoEventosAPI](https://github.com/AlfredoSWDev/GeoEventosAPI) corriendo en `localhost:8080`

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

## Testing

Los tests no requieren que GeoEventosAPI esté corriendo — usan WireMock para simular el servidor.

```bash
mvn test
```

En entornos sin pantalla (servidores, CI/CD), instala Xvfb primero:

```bash
# Fedora / RHEL
sudo dnf install xorg-x11-server-Xvfb

# Ubuntu / Debian
sudo apt install xvfb
```

```bash
Xvfb :99 &
export DISPLAY=:99
mvn test
```

Resultado esperado: **27 tests, 0 failures**.

---

## Flujos Principales

### Carga de datos
```
Principal (windowGainedFocus)
    └── Conector.cargarDatosTabla()
            └── GET /api/eventos
                    └── Llena el JTable
```

### Mapa interactivo
```
Botón "Ver Mapa"
    └── MapaPanel.cargarEventos()
            └── GET /api/eventos
                    └── JS cargarEventos(json) → pinta marcadores

Click en marcador
    └── javabridge.onVerDetalle(id)
            └── ManipularEventos("Evento:", id)

Click en el mapa
    └── javabridge.onMapClick(lat, lng)
            └── rellena campos de coordenadas en el formulario
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

- [x] Integración con mapa interactivo (Leaflet + OpenStreetMap)
- [x] Visualización geolocalizada de eventos
- [x] Click en marcador para ver detalle
- [x] Click en mapa para asignar coordenadas
- [x] Unit tests e integration tests (JUnit 5 + Mockito + WireMock)
- [ ] Filtros avanzados por categoría, fecha y distancia
- [ ] Panel de estadísticas para clientes B2B
- [ ] Despliegue en AWS

---

## Repositorios del Proyecto

| Repositorio | Descripción |
|-------------|-------------|
| **GeoEventosGUI** | Este repositorio — cliente de escritorio Swing |
| [GeoEventosAPI](https://github.com/AlfredoSWDev/GeoEventosAPI) | API REST Spring Boot |
| [GeoEventosAndroid](https://github.com/AlfredoSWDev/GeoEventosAndroid) | Cliente móvil Android |