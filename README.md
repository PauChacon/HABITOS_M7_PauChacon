# 📱 Projecte M07 — Hàbits Atòmics

Aplicació Android de seguiment d'hàbits diaris desenvolupada amb **Kotlin + XML Views**, connectada a un backend propi amb **FastAPI + MariaDB** desplegat a **AWS Academy**.

---

## 🧱 Stack tecnològic

| Capa | Tecnologia |
|---|---|
| Frontend | Kotlin + XML Views (Android Studio) |
| Networking | Retrofit + OkHttpClient + Gson |
| Backend | FastAPI (Python) + Uvicorn |
| Base de dades | MariaDB 10.5 en EC2 Amazon Linux 2023 |
| Infraestructura | AWS Academy — EC2 t3.micro amb IP Elàstica fixa |

---

## 🚀 Millores i funcionalitats implementades

Quan es va començar el projecte, l'aplicació ja tenia una base visual funcional: pantalles de login, registre, menú principal amb llista d'hàbits, pantalla d'edició i algunes pantalles secundàries com ajustos i termes d'ús. No obstant això, la majoria de funcionalitats eren visuals o estàtiques — el login simplement navegava cap al menú sense cap comprovació, el registre no enviava res a cap servidor, i no hi havia cap base de dades ni backend real. A partir d'aquí es va construir tota la lògica real de l'aplicació.

### Backend complet des de zero
Es va muntar un servidor EC2 a AWS Academy amb Amazon Linux 2023, es va instal·lar MariaDB directament a la instància i es va crear la base de dades `habitosdb` amb les taules `usuarios`, `habitos` i `historial`. Es va desenvolupar una API REST completa amb FastAPI i Uvicorn al port 8000, amb tots els endpoints necessaris per gestionar usuaris i hàbits. Per evitar haver d'actualitzar la IP del servidor cada vegada que es reinicia el lab, es va configurar una **IP Elàstica fixa** (`100.50.65.108`) a AWS.

### Login real amb comprovació a la API
Abans, el botó de login simplement navegava cap al menú sense comprovar res. Es va implementar una crida real a l'API que verifica que l'usuari existeix i que la contrasenya és correcta. Si les credencials no coincideixen, es mostra un missatge d'error. Si són correctes, les dades de l'usuari (id, username, email, telèfon) es guarden a `SharedPreferences` per mantenir la sessió activa.

### Registre funcional amb connexió a la base de dades
El registre ja tenia les validacions de format (longitud del nom, format de correu, telèfon de 9 xifres, contrasenya segura), però no enviava res al servidor. Es va connectar amb l'endpoint `/register` de la API, de manera que ara els usuaris es creen realment a la base de dades. Si l'usuari o el correu ja existeix, es mostra un error específic. En acabar el registre, redirigeix al login en lloc d'anar directament al menú.

### Sessió persistent i auto-login
Es va implementar el sistema de sessió amb `SharedPreferences`: en fer login es guarden les dades de l'usuari, i quan s'obre l'app es comprova si ja hi ha sessió activa. Si és així, es salta directament al menú sense haver de tornar a iniciar sessió.

### Hàbits vinculats a l'usuari
Abans tots els hàbits eren compartits — qualsevol usuari veia tots els hàbits de la base de dades. Es va afegir el camp `id_usuario` a la taula d'hàbits i es van modificar els endpoints per filtrar sempre per usuari. Ara cada usuari només veu i gestiona els seus propis hàbits.

### Marcar hàbits com a completats
Es va afegir el camp `completado` a la taula d'hàbits i un endpoint `PATCH /habitos/{id}/completar`. Al tocar una targeta d'hàbit al menú principal, l'hàbit es marca com a completat: el nom apareix ratllat, la targeta es torna semitransparent i la icona es tenyeix de verd. L'estat es guarda a la base de dades i persisteix entre sessions.

### Gestió de compte des d'Ajustos
La pantalla d'ajustos es va refer completament. Es van eliminar les opcions que no funcionaven i es van implementar les tres accions reals: editar perfil (canviar username, correu i telèfon amb actualització a la BD i a la sessió local), tancar sessió (neteja les `SharedPreferences` i torna al login), i eliminar compte (esborra l'usuari de la base de dades i neteja la sessió).

### Historial d'accions
Es va crear la taula `historial` a la base de dades i els endpoints corresponents. Cada vegada que l'usuari crea o esborra un hàbit, es registra automàticament una entrada amb el tipus d'acció, les dades de l'hàbit i la data i hora exacta. La pantalla d'historial mostra aquest registre ordenat del més recent al més antic, amb les entrades de creació en verd i les d'esborrat en vermell.

### Gràfics millorats amb navegació
La pantalla de gràfics es va millorar passant d'un únic gràfic estàtic a quatre tipus de gràfics navegables amb fletxes: hàbits per categoria, importants vs normals, distribució per franja horària i resum general. Es va afegir el `BottomNavigationView` a la pantalla i es va eliminar el botó de tornar.

### Navegació amb BottomNavigationView
Es va afegir un ítem "Inici" al menú inferior i es va corregir el problema que feia que l'ítem "Historial" aparegués seleccionat quan no s'estava en aquella pantalla. Cada pantalla selecciona ara el seu propi ítem al obrir-se. El menú lateral (hamburguesa) mostra el nom real de l'usuari en lloc del text estàtic "Hola, Usuario".

### Pantalla dedicada per esborrar hàbits
Es va crear una pantalla separada `BorrarHabitos` on es mostren tots els hàbits amb la icona tenyida de vermell. En tocar un hàbit apareix un diàleg de confirmació. Això va permetre simplificar el menú principal, on ara tocar un hàbit sempre significa marcar-lo com a completat, sense modes ni confusions.

### Suport multiidioma
Es van externalitzar tots els textos de la interfície a `strings.xml` i es va crear una traducció completa a l'anglès a `values-en/strings.xml`. L'app selecciona automàticament l'idioma segons la configuració del dispositiu.

### Salutació dinàmica
El menú principal mostra una salutació aleatòria entre 15 opcions (Hola, Ey, Qué tal, Buenas...) seguida del nom real de l'usuari, en lloc d'un text estàtic.

---

## ☁️ Infraestructura AWS

El backend està desplegat en una instància EC2 d'AWS Academy amb IP Elàstica fixa `100.50.65.108`. La base de dades MariaDB corre directament a la mateixa instància. La documentació interactiva Swagger és accessible a `http://100.50.65.108:8000/docs`.

### Estructura de la base de dades

**`usuarios`** — emmagatzema els comptes d'usuari amb username, email, telèfon i contrasenya.

**`habitos`** — emmagatzema els hàbits de cada usuari amb nom, categoria, nivell d'importància, hora i estat de completat, vinculats per `id_usuario`.

**`historial`** — registra cada creació o esborrat d'hàbit amb el tipus d'acció, les dades de l'hàbit i la data i hora.

---

## 🔗 Endpoints de la API

| Mètode | Endpoint | Descripció |
|---|---|---|
| POST | `/register` | Crear nou compte |
| POST | `/login` | Iniciar sessió |
| GET | `/usuarios` | Llistar usuaris |
| PUT | `/usuarios/{id}` | Editar perfil |
| DELETE | `/usuarios/{id}` | Eliminar compte |
| GET | `/habitos/{userId}` | Obtenir hàbits de l'usuari |
| POST | `/habitos/{userId}` | Crear hàbit |
| PUT | `/habitos/{id}` | Editar hàbit |
| PATCH | `/habitos/{id}/completar` | Marcar/desmarcar com a completat |
| DELETE | `/habitos/{id}` | Eliminar hàbit |
| GET | `/historial/{userId}` | Obtenir historial de l'usuari |
| POST | `/historial/{userId}` | Afegir entrada a l'historial |

---

## 🧪 Testing

### JUnit (tests unitaris)
Tests unitaris per als ViewModels de registre (`RegisterViewModel`) i edició de perfil (`EditarPerfilViewModel`), comprovant totes les validacions dels formularis: nom d'usuari massa curt o llarg, correu amb format incorrecte o buit, telèfon amb lletres o longitud incorrecta, contrasenya feble, i contrasenyes que no coincideixen.

### Espresso (tests d'UI)
Tests d'interfície per a la pantalla de registre que verifiquen que els missatges d'error correctes apareixen en pantalla quan l'usuari introdueix dades invàlides, cobrint els mateixos casos que els tests unitaris però des de la perspectiva de la interfície gràfica. El test de camp vàlid amb navegació està comentat intencionadament per evitar dependre del servidor en els tests automàtics.

---

## 📁 Estructura del projecte

```
app/
├── src/
│   ├── main/
│   │   ├── java/com/example/projecte_m07/
│   │   │   ├── habitos/              # HabitosAPI, HabitsService, models
│   │   │   ├── viewmodel/            # RegisterViewModel, EditarPerfilViewModel
│   │   │   ├── MainActivity          # Login
│   │   │   ├── Register              # Registre d'usuari
│   │   │   ├── Menu                  # Pantalla principal amb llista d'hàbits
│   │   │   ├── CrearHabito           # Formulari de creació d'hàbits
│   │   │   ├── BorrarHabitos         # Llista d'hàbits per eliminar
│   │   │   ├── EditarHabitoDetalle   # Edició d'un hàbit individual
│   │   │   ├── MenuHistorial         # Historial d'accions
│   │   │   ├── GraficoHabitos        # Gràfics i estadístiques
│   │   │   ├── Ajustes               # Configuració del compte
│   │   │   └── EditarPerfil          # Edició de dades de l'usuari
│   │   └── res/
│   │       ├── layout/               # XMLs de totes les pantalles
│   │       ├── values/               # Colors, strings (CA/ES), themes, styles
│   │       └── values-en/            # Strings en anglès
│   ├── androidTest/                  # Tests Espresso (RegisterUITest)
│   └── test/                         # Tests JUnit (RegisterViewModelTest, EditarPerfilViewModelTest)
```

---

## 👨‍💻 Autor

Pau Chacón — Cicle Superior DAM
