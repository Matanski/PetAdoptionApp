# PetAdoption

A client/server pet-adoption and rehoming system, built for the **Advanced Java Programming** course at HIT (Holon Institute of Technology), Semester B 2026.

The project is split across three independent IntelliJ projects plus a separate algorithm library:

| Project | Role | Part |
|---|---|---|
| [PetAdoptionAlgoModule](https://github.com/Matanski/PetAdoptionAlgoModule) | Text-compression algorithm library (RLE + LZW), packaged as `AlgorithmModule.jar` | A |
| `PetServer/` | TCP server exposing the **pet** API. Uses the algorithm to compress descriptions. | B + C |
| `AdoptionServer/` | TCP server exposing the **adoption** API. No algorithm needed. | B + C |
| `PetAdoptionClient/` | JavaFX desktop client (MVC) that talks to both servers over sockets. | D |

## Architecture

```
                         ┌─────────────────────────┐
                         │   PetAdoptionClient      │
                         │   (JavaFX, MVC)          │
                         │                          │
                         │  View → AppController →  │
                         │        ServerClient      │
                         └───────┬─────────┬────────┘
                    pet/*  JSON  │         │  adoption/*  JSON
                     over TCP    │         │   over TCP
                    (port 34567) │         │  (port 34568)
                         ┌───────▼───┐ ┌───▼──────────┐
                         │ PetServer │ │AdoptionServer│
                         └─────┬─────┘ └──────┬───────┘
              Server → HandleRequest → ControllerFactory → Controller
                                    → Service → IDao → *.dat file
                     (PetServer also → IAlgoTextCompression)
```

## How a request flows (server side)

1. `Server` listens on a `ServerSocket`; every accepted client socket is handled on its own thread.
2. `HandleRequest` reads the JSON line, deserializes it into a `Request` with **gson**, and reads `headers.action` (e.g. `"pet/save"`).
3. `ControllerFactory` splits the action into a prefix (`pet`) and sub-action (`save`) and returns the registered `Controller` (**Factory Pattern**).
4. The `Controller` calls the matching `Service` method and wraps the result in a `Response`.
5. `HandleRequest` serializes the `Response` back to JSON and writes it to the socket.

### JSON wire format
```json
{ "headers": { "action": "pet/save" },
  "body":    { "id": 1, "name": "Buddy", "species": "Dog", "age": 3, "description": "..." } }
```
Response:
```json
{ "status": 200, "message": "OK", "data": { ... } }
```

## Design principles

- **Open/Closed** — Services depend on the `IAlgoTextCompression` and `IDao<T>` interfaces, not concrete classes.
- **Strategy Pattern** — the compression algorithm is injected into `ServicePet` via its constructor.
- **Factory Pattern** — `ControllerFactory` resolves the right controller from the action string at runtime.
- **MVC + Loose Coupling** (client) — `view` / `controller` / `model` are separated; the View never touches sockets directly.
- **Single Responsibility** — networking (`server`), routing (`controller`), business logic (`service`), and persistence (`dao`) are all separate layers.

## Project layout (identical structure in both servers, per spec)

```
PetServer/                        AdoptionServer/
├── lib/                          ├── lib/
│   ├── AlgorithmModule.jar       │   └── gson-2.10.jar          (no algo jar)
│   └── gson-2.10.jar             ├── src/main/java/com/hit/
├── src/main/java/com/hit/        │   ├── dm/ (AdoptionRequest, User)
│   ├── dm/ (Pet)                 │   ├── dao/ (IDao, AdoptionRequestDaoFileImpl)
│   ├── dao/ (IDao, PetDaoFileImpl)│   ├── service/ (ServiceAdoption)
│   ├── service/ (ServicePet)     │   ├── controller/ (Controller, ControllerFactory, AdoptionController)
│   ├── controller/ (Controller,  │   ├── model/ (Request, Response)
│   │   ControllerFactory,        │   └── server/ (Server, HandleRequest, ServerDriver)
│   │   PetController)            ├── src/main/test/  (ServiceAdoptionTest)
│   ├── model/ (Request, Response)└── src/main/resources/adoptions.dat
│   └── server/ (Server, HandleRequest, ServerDriver)
├── src/main/test/  (ServicePetTest)
└── src/main/resources/pets.dat
```

## Running the system

Start the two servers first, then the client. In IntelliJ, open each folder as a project (or as modules) and run:

1. **PetServer** — run `com.hit.server.ServerDriver` (listens on port 34567)
2. **AdoptionServer** — run `com.hit.server.ServerDriver` (listens on port 34568)
3. **PetAdoptionClient** — run `com.hit.client.Launcher`

> Run `Launcher`, **not** `MainApp`. JavaFX refuses to start a class that extends `Application`
> when the JavaFX jars are on the plain classpath; `Launcher` (which does not extend `Application`)
> works around that so the bundled jars in `lib/` are enough — no module-path setup needed.

## Tests

Each server has a JUnit 4 test that wires Service + DAO (+ algorithm for the pet server) together:

- `PetServer` → `ServicePetTest` (6 tests)
- `AdoptionServer` → `ServiceAdoptionTest` (6 tests)

## Dependencies (all bundled in each project's `lib/`)

- **gson 2.10** — JSON serialization (both servers + client)
- **AlgorithmModule.jar** — Part A library (pet server only)
- **JavaFX 23 (Windows)** — client UI
- **JUnit 4.13.2 + Hamcrest 1.3** — referenced from IntelliJ's bundled JUnit library

## Course Info

- **Course:** Advanced Java Programming — HIT, Holon Institute of Technology
- **Instructor:** Nissim Barami
- **Semester:** B 2026
