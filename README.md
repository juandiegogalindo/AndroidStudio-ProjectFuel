# PlataformaCombustible-Android

## 1. Nombre del Proyecto
**Plataforma de Gestión de Combustibles – Versión Android**

---

## 2. Descripción General

Aplicación móvil desarrollada en Android Studio como implementación inicial de una plataforma de gestión de combustibles.  

El sistema simula funcionalidades básicas asociadas a la administración de inventario, registro de salidas y consulta de precios de combustible, tomando como referencia el contexto normativo del sector energético en Colombia.

El proyecto representa una primera aproximación a la capa de presentación (Frontend móvil) de una arquitectura mayor.

---

## 3. Objetivo

Desarrollar una aplicación Android que permita:

- Consultar precio de combustible según tipo de vehículo.
- Administrar inventario de combustible.
- Registrar salidas con cálculo automático y generación de historial.

---

## 4. Tecnologías Utilizadas

- **Lenguaje:** Java
- **IDE:** Android Studio
- **SDK mínimo:** API 21 (Android 5.0 Lollipop)
- **Arquitectura:** Activities + Intents
- **Componentes UI:**
  - LinearLayout
  - ScrollView
  - Button
  - EditText
  - Spinner
  - TextView
  - ListView
  - Toast
- **Estructuras de Datos:**
  - ArrayList
- **Control de versiones:** Git
- **Repositorio remoto:** GitHub

---

## 5. Historias de Usuario Implementadas

### 5.1 Consulta de Precio
Como usuario, deseo seleccionar el tipo de vehículo para consultar el precio del combustible correspondiente.

Funcionalidad:
- Spinner para selección.
- Cálculo simulado de precio.
- Visualización en pantalla.

---

### 5.2 Manejo de Inventario
Como estación de servicio, deseo registrar cantidades de combustible para llevar control del inventario disponible.

Funcionalidad:
- Registro de galones.
- Acumulación dinámica.
- Actualización en tiempo real.

---

### 5.3 Registro de Salidas
Como estación de servicio, deseo registrar salidas de combustible indicando tipo, cantidad y fecha.

Funcionalidad:
- Selector de tipo (Corriente, Extra, Diesel).
- Cálculo automático (galones × precio).
- Validación de inventario.
- Generación de historial dinámico.
- Registro automático de fecha.
- ListView con salidas recientes.

---

## 6. Estructura del Proyecto

app/
├── java/co.edu.unipiloto.scrumbacklog/
│ ├── MainActivity.java
│ ├── ConsultaActivity.java
│ ├── InventarioActivity.java
│ └── SalidasActivity.java
│
├── res/
│ ├── layout/
│ │ ├── activity_main.xml
│ │ ├── activity_consulta.xml
│ │ ├── activity_inventario.xml
│ │ └── activity_salidas.xml
│ └── values/

---

## 7. Flujo de Navegación

MainActivity (Menú Principal)

→ ConsultaActivity  
→ InventarioActivity  
→ SalidasActivity  

Cada Activity incluye botón de retorno mediante método `finish()`.

---

## 8. Conceptos Técnicos Aplicados

- Ciclo de vida de Activities.
- Navegación mediante Intents.
- Programación orientada a eventos.
- Manejo de adaptadores (ArrayAdapter).
- ListView dinámico.
- Validación lógica de negocio.
- Manipulación de fechas (SimpleDateFormat).
- Uso de variables globales en Activity.
- Control básico de inventario simulado.

---

## 9. Alcance del Proyecto

Este proyecto representa una implementación básica de la capa de presentación.  

No incluye:

- Base de datos persistente.
- Backend.
- Autenticación.
- Microservicios.
- Integración normativa dinámica.

Su finalidad es académica y de aprendizaje del entorno Android.

---

## 10. Instalación y Ejecución

1. Clonar repositorio: https://github.com/juandiegogalindo/AndroidStudio-ActividadScrum.git
2. Abrir en Android Studio.
3. Sincronizar Gradle.
4. Ejecutar en emulador o dispositivo físico.

---

## 11. Autores
1. Juan Pablo Coronado
2. Juan Diego Galindo
3. Sofia Torres Paez
   
Ingeniería de Sistemas  
Universidad Piloto de Colombia  

---

## 12. Referencias Académicas

- Documentación oficial Android Developers.
- Decreto 1428 de 2025 – Ministerio de Hacienda y Crédito Público.
- Arquitectura por capas en aplicaciones móviles.
- Microsoft Copilot (2026). Asistencia conceptual en diseño de plataforma de combustibles.
---
