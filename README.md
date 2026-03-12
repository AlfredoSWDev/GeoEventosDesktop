**GeoEventosGUI**
=====

**Introducción**
=============

GeoEventosGUI es una aplicación cliente basada en Java Swing que permite a los usuarios visualizar, crear, modificar y eliminar eventos. La aplicación se integra con el servicio web GeoEventosAPI para proporcionar una experiencia de usuario completa.

**Características**
================

* Visualizar eventos en una tabla con ID, nombre, valor y ubicación
* Buscar eventos por nombre o ubicación
* Crear eventos con nombre, descripción, validez, valor, ubicación, coordenadas y foto
* Modificar eventos editando sus detalles
* Ver detalles de evento en modo lector
* Eliminar eventos con confirmación
* Subir fotos a ImgBB y obtener un enlace
* Vista de mapa con marcadores geolocalizados para eventos
* Click en un marcador para ver detalles de evento
* Click en la mapa para asignar coordenadas a un nuevo evento

**Detalles Técnicos**
==================

* Java 23+
* JavaFX 23
* GeoEventosAPI (servicio web RESTful)
* Leaflet y OpenStreetMap para la vista de mapa
* JUnit 5 y Mockito para pruebas unitarias
* WireMock para pruebas de integración

**Pruebas**
==========

Para ejecutar las pruebas, ejecute el siguiente comando:
```
mvn test
```
En entornos sin pantalla gráfica (servidores, CI/CD), instale Xvfb y estabelecer el entorno de variables `DISPLAY`:
```
Xvfb :99 &
export DISPLAY=:99
mvn test
```

**Plan de Trabajo**
================

* Integrar con la vista de mapa (Leaflet y OpenStreetMap)
* Visualizar eventos geolocalizados
* Click en marcador para ver detalles de evento
* Click en mapa para asignar coordenadas
* Pruebas unitarias y de integración (JUnit 5 y Mockito)
* Filtrar eventos por categoría, fecha y distancia
* Panel para estadísticas del cliente
* Implementación en AWS

**Repositorios Relevantes**
=====================

* **GeoEventosGUI** (este repositorio)
* [GeoEventosAPI](https://github.com/AlfredoSWDev/GeoEventosAPI) (servicio web RESTful)
* [GeoEventosAndroid](https://github.com/AlfredoSWDev/GeoEventosAndroid) (cliente Android)

**Licencia**
==========

Este proyecto está licenciado bajo la licencia MIT. Ver el archivo [LICENSE](LICENSE) para detalles.

