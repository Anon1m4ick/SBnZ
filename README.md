# Vehicle Fault Diagnostics System

Knowledge-Based Systems homework project based on `Project_Proposal_Vehicle_Fault_Diagnostics.pdf`.

The project structure:

- `model` contains the domain facts used by Drools.
- `kjar` contains the KIE module and DRL rules.
- `service` is a Spring Boot REST application that activates the rules.

## Implemented Homework Scope

- Domain class diagram: [docs/class-diagram.md](docs/class-diagram.md)
- Data model: `Vehicle`, `Symptom`, `DTCCode`, `SensorReading`, `SystemProblem`, `FaultCandidate`, `Diagnosis`, `Recommendation`, `RuleTrace`, and supporting enums.
- The project follows the forward-chaining route by implementing the 20 rules from the proposal's "Basic Rules" table as ordinary Drools production rules: 10 for Member 1 and 10 for Member 2.
- Rule activation demo on Spring Boot startup and through `GET /api/diagnostics/demo`.

## Rules

Member 1 - Symptom and OBD-II diagnosis:

1. `M1-BR-01 Check engine and P0300 mark ignition system suspect`
2. `M1-BR-02 Cylinder misfire candidate from DTC and power loss`
3. `M1-BR-03 Cooling threshold creates system problem`
4. `M1-BR-04 Oil pressure critical fault candidate`
5. `M1-BR-05 EGR restriction candidate from P0401 and black smoke`
6. `M1-BR-06 Lambda sensor fault candidate`
7. `M1-BR-07 Charging system candidate`
8. `M1-BR-08 Brake pad or warped disc candidate`
9. `M1-BR-09 Low tyre pressure candidate`
10. `M1-BR-10 Transmission or clutch candidate`

Member 2 - Fault prioritization and repair recommendation:

1. `M2-BR-01 Critical diagnosis gets stop-driving recommendation`
2. `M2-BR-02 Two high diagnoses in one system escalate combined risk`
3. `M2-BR-03 Electrical system route`
4. `M2-BR-04 Brake system route`
5. `M2-BR-05 Low tyre pressure gets tyre shop route`
6. `M2-BR-06 Lambda sensor cost range`
7. `M2-BR-07 Turbo actuator cost range`
8. `M2-BR-08 White smoke and overheating warning`
9. `M2-BR-09 EGR fault adds secondary DPF warning`
10. `M2-BR-10 Low-confidence non-critical diagnosis asks for monitoring`

There are also two supporting production rules: one turns candidates into final diagnoses, and one creates a turbo candidate so the turbo repair recommendation rule can be demonstrated. The demo data triggers at least 20 activations.

## Run

```powershell
mvn clean test
mvn -DskipTests package
export JAVA_HOME=~/Library/Java/JavaVirtualMachines/corretto-17.0.13/Contents/Home
java -jar service\target\service-0.0.1-SNAPSHOT.jar
```

Then open:

```text
http://localhost:8080/api/diagnostics/demo
```

The Spring Boot startup log also prints the demo rule execution result.

Useful endpoints:

- `GET /api/diagnostics/demo/request` - demo input facts
- `GET /api/diagnostics/demo/report` - complete rule execution report
- `GET /api/diagnostics/demo/transmission/report` - separate scenario for `M1-BR-10`, which requires no misfire DTC
- `GET /api/diagnostics/demo/diagnoses` - diagnoses only
- `GET /api/diagnostics/demo/recommendations` - recommendations only
- `GET /api/diagnostics/demo/trace` - fired-rule trace only
- `GET /api/diagnostics/rules` - implemented rule catalog
- `POST /api/diagnostics/evaluate` - evaluate a custom diagnostic request

For `spring-boot:run`, first install the local modules once:

```powershell
.\mvnw.cmd install -DskipTests
.\mvnw.cmd -pl service spring-boot:run
```

Recommended checks:

```powershell
java -version
javac -version
mvn -version
```

## Homework 4 – Rule Template mechanism (DZ4)

Implements proposal section **4.3 Template Mechanism – Parameterization by Vehicle Type**:

- Template file: `kjar/src/main/resources/templatetable/sensor_thresholds.drt`
- Parameter table: `SensorThresholdRow` rows for PETROL / DIESEL / HYBRID in `ThresholdTemplateService`
- Runtime compilation: `ObjectDataCompiler` → generated DRL → `KieSession` (isolated from main `vehicle-diagnosis.drl`)

**Threshold table :**

| engineType | maxCoolantTemp | minOilPressure | voltageMin | voltageMax |
|------------|----------------|----------------|------------|------------|
| PETROL     | 110            | 1.0            | 13.5       | 14.8       |
| DIESEL     | 105            | 1.2            | 13.5       | 14.8       |
| HYBRID     | 100            | 0.9            | 13.8       | 15.2       |

Three rule types (coolant overheating, low oil pressure, battery voltage out of range) generate **9 rules** (3 per engine type).

**Test:** `mvn -pl service -Dtest=SensorThresholdTemplateTest test`

After starting the service (`java -jar service/target/service-0.0.1-SNAPSHOT.jar`)

| URL | What it shows |
|-----|----------------|
| http://localhost:8080/api/diagnostics/template/drl | Generated DRL from `.drt` + parameter table (9 rules: 3 per engine type) |
| http://localhost:8080/api/diagnostics/template/demo?coolant=108&oil=0.95&voltage=13.6 | Same sensor readings, **different** results for PETROL / DIESEL / HYBRID |
| http://localhost:8080/api/diagnostics/template/scenario?engine=DIESEL&coolant=108&oil=5.0&voltage=14.0 | Single engine type scenario |

**Expected demo result** (`/template/demo?coolant=108&oil=0.95&voltage=13.6`):

- **PETROL** (110°C / 1.0 bar / 13.5-14.8 V): coolant 108 < 110 no; oil 0.95 < 1.0 yes; voltage 13.6 in range no → 1 rule (oil)
- **DIESEL** (105°C / 1.2 bar / 13.5-14.8 V): coolant 108 > 105 yes; oil 0.95 < 1.2 yes; voltage 13.6 in range no → 2 rules (cooling + oil)
- **HYBRID** (100°C / 0.9 bar / 13.8-15.2 V): coolant 108 > 100 yes; oil 0.95 > 0.9 no; voltage 13.6 < 13.8 yes → 2 rules (cooling + electrical)

Optional query parameters for `/template/demo`: `coolant`, `oil`, `voltage` (defaults: `108`, `0.95`, `13.6`).

For `/template/scenario`: required `engine` (`PETROL`, `DIESEL`, or `HYBRID`); optional `coolant`, `oil`, `voltage`.