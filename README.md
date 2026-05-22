# PetAdoption

A client/server-style Java application for managing pet adoptions and rehoming, built as part of the **Advanced Java Programming** course at HIT (Holon Institute of Technology), Semester B 2026.

This is **Part B** of the project — the application layer that uses the [PetAdoptionAlgoModule](https://github.com/Matanski/PetAdoptionAlgoModule) algorithm library (Part A) as a binary dependency.

## Overview

The application stores pet listings and adoption requests, persisting them to a local file via Java object serialization. Pet descriptions are transparently compressed before storage and decompressed on retrieval using a pluggable text-compression algorithm injected at runtime — a classic **Strategy Pattern** wired through dependency injection.

## Architecture

```
+----------------------+         +-----------------+         +----------------------+
|   ServicePet         |  uses   |  IAlgo          |  impl   |  LzwAlgoImpl         |
|   ServiceAdoption    +-------->+  TextCompression+<--------+  RleAlgoImpl         |
|   (Business Logic)   |         |  (Strategy)     |         |  (in AlgorithmModule)|
+----------+-----------+         +-----------------+         +----------------------+
           |
           | uses
           v
+----------------------+         +-----------------+
|   IDao<T>            |  impl   |  *DaoFileImpl   |
|   (Repository API)   +<--------+  (ObjectStream  |
|                      |         |   persistence)  |
+----------------------+         +-----------------+
```

### Design Principles

- **Open/Closed Principle** — Services depend on the `IAlgoTextCompression` and `IDao<T>` interfaces, not concrete classes. New algorithms or persistence backends can be added without touching service code.
- **Strategy Pattern** — The compression algorithm is passed into `ServicePet` via constructor injection. Swapping `LzwAlgoImpl` for `RleAlgoImpl` is a one-line change.
- **Dependency Injection** — All wiring happens externally (in `Main` or `ServicePetTest`), keeping classes decoupled and easy to test.

## Project Structure

```
PetAdoption/
├── lib/
│   ├── AlgorithmModule.jar     (Part A compiled output)
│   ├── junit-4.13.2.jar
│   └── hamcrest-core-1.3.jar
├── src/main/java/com/hit/
│   ├── dm/                     Data Models (Pet, User, AdoptionRequest)
│   ├── dao/                    IDao + file-based implementations
│   ├── service/                ServicePet, ServiceAdoption
│   └── Main.java
├── src/main/test/com/hit/
│   └── service/ServicePetTest.java
└── src/main/resources/
    └── datasource.txt          Persistence file written by the DAO
```

## Key Classes

| Class | Responsibility |
|---|---|
| `Pet`, `User`, `AdoptionRequest` | Plain serializable data models |
| `IDao<T>` | Generic CRUD contract — `save`, `get`, `getAll`, `delete`, `update` |
| `PetDaoFileImpl` / `AdoptionRequestDaoFileImpl` | File-backed DAOs using `ObjectInputStream` / `ObjectOutputStream` |
| `ServicePet` | Pet business logic; compresses/decompresses descriptions via injected `IAlgoTextCompression` |
| `ServiceAdoption` | Adoption-request workflow (submit, approve, reject, list) |
| `ServicePetTest` | JUnit 4 test class that wires algorithm + DAO + service together end-to-end |

## Running the Tests

Open the project in IntelliJ IDEA → right-click `ServicePetTest` → **Run 'ServicePetTest'**.

All 6 tests should pass:

```
JUnit version 4.13.2
......
Time: 0.048
OK (6 tests)
```

The tests use the real `src/main/resources/datasource.txt`, so after running them you can open that file and see that data was actually written and read back.

## Dependencies

- **JDK 8 or higher**
- **JUnit 4.13.2** + **Hamcrest 1.3** (bundled in `lib/`)
- **AlgorithmModule.jar** (bundled in `lib/`; source in the [PetAdoptionAlgoModule](https://github.com/Matanski/PetAdoptionAlgoModule) repo)

## Course Info

- **Course:** Advanced Java Programming
- **Institution:** HIT — Holon Institute of Technology
- **Instructor:** Nissim Barami
- **Semester:** B 2026
