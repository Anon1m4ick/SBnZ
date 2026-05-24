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
