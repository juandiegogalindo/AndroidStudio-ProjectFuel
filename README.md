# PlataformaCombustible-Android

## 1. Nombre del Proyecto
**Scrum Backlog Inicial Proyecto Combustibles – Versión Android**

---

## 2. Descripción General
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

## 6. Alcance del Proyecto

Este proyecto representa una implementación básica de la capa de presentación.  

No incluye:

- Base de datos persistente.
- Backend.
- Autenticación.
- Microservicios.
- Integración normativa dinámica.

Su finalidad es académica y de aprendizaje del entorno Android.

---

## 7. Instalación y Ejecución

1. Clonar repositorio: https://github.com/juandiegogalindo/AndroidStudio-ActividadScrum.git
2. Abrir en Android Studio.
3. Sincronizar Gradle.
4. Ejecutar en emulador o dispositivo físico.

---

## 8. Imagenes de Referencia.
1. Pagina Principal

<img width="390" height="255" alt="image" src="https://github.com/user-attachments/assets/0788faef-176b-4707-b860-fec857f5acc2" />

2. Consulta Precio

<img width="390" height="463" alt="image" src="https://github.com/user-attachments/assets/06b9ea22-a372-4bc1-8890-84f9e9ec4423" />
  
3. Manejo Inventario.

<img width="393" height="455" alt="image" src="https://github.com/user-attachments/assets/097ece11-c84c-4c11-9906-4f6bccb50fe9" />

4. Manejo salidas

<img width="393" height="484" alt="image" src="https://github.com/user-attachments/assets/ca5b2b95-a7ab-47d4-bdad-8cfd87a98386" />

---

## 8. Autores
1. Juan Pablo Coronado
2. Juan Diego Galindo
3. Sofia Torres Paez
   
Ingeniería de Sistemas  
Universidad Piloto de Colombia  

---
