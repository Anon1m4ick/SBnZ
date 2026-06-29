import { useEffect, useMemo, useState } from "react";
import {
  api,
  BackwardVerificationResult,
  CepAlert,
  DiagnosticReport,
  DiagnosticRequest,
  TemplateScenarioResult
} from "./api";

type ReportSource = "demo" | "transmission" | `dataset:${string}`;
type View = "diagnosis" | "vehicle" | "trace" | "forward" | "backward" | "template" | "cep";
type CepKey = "overheating" | "voltage" | "misfire" | "maf" | "tyre-pressure" | "critical-warnings";

type CaseOption = {
  source: ReportSource;
  title: string;
  vehicle: string;
  symptoms: string;
};

const urgencyRank: Record<string, number> = {
  CRITICAL: 4,
  HIGH: 3,
  MEDIUM: 2,
  LOW: 1
};

const primaryCases: CaseOption[] = [
  {
    source: "demo",
    title: "Several warning lights",
    vehicle: "Volkswagen Golf diesel",
    symptoms: "Oil pressure, battery, coolant, smoke, braking and tyre warnings"
  },
  {
    source: "transmission",
    title: "Jerks while shifting",
    vehicle: "Toyota Corolla petrol",
    symptoms: "Noticeable jerk during gear changes"
  }
];

const savedCaseLabels: Record<string, Omit<CaseOption, "source">> = {
  "diesel-egr": {
    title: "Black smoke and power loss",
    vehicle: "Volkswagen Golf diesel",
    symptoms: "Check engine light, weak acceleration and black exhaust smoke"
  },
  transmission: {
    title: "Jerks while shifting",
    vehicle: "Toyota Corolla petrol",
    symptoms: "Noticeable jerk during gear changes"
  },
  "tyre-pressure": {
    title: "Tyre pressure warning",
    vehicle: "Skoda Octavia diesel",
    symptoms: "Tyre pressure warning with a low pressure reading"
  }
};

const cepScenarios: Array<{
  key: CepKey;
  title: string;
  signal: string;
  run: () => Promise<CepAlert[]>;
}> = [
  {
    key: "overheating",
    title: "Rising coolant temperature",
    signal: "Several temperature readings stay too high over time",
    run: api.cepOverheating
  },
  {
    key: "voltage",
    title: "Unstable charging voltage",
    signal: "Battery voltage repeatedly moves outside the expected range",
    run: api.cepVoltage
  },
  {
    key: "misfire",
    title: "Repeated misfire events",
    signal: "Misfire events appear several times in a short period",
    run: api.cepMisfire
  },
  {
    key: "maf",
    title: "Dropping air-flow readings",
    signal: "Air-flow readings keep falling while the engine is running",
    run: api.cepMaf
  },
  {
    key: "tyre-pressure",
    title: "Fast tyre pressure drop",
    signal: "Tyre pressure falls quickly across consecutive readings",
    run: api.cepTyrePressure
  },
  {
    key: "critical-warnings",
    title: "Repeated critical warnings",
    signal: "Critical warnings appear more than once in a short window",
    run: api.cepCriticalWarnings
  }
];

export function App() {
  const [view, setView] = useState<View>("diagnosis");
  const [caseSource, setCaseSource] = useState<ReportSource>("demo");
  const [savedCases, setSavedCases] = useState<string[]>([]);
  const [report, setReport] = useState<DiagnosticReport | null>(null);
  const [diagnosticRequest, setDiagnosticRequest] = useState<DiagnosticRequest | null>(null);
  const [cepKey, setCepKey] = useState<CepKey>("overheating");
  const [cepAlerts, setCepAlerts] = useState<CepAlert[]>([]);
  const [templateParams, setTemplateParams] = useState({ coolant: 108, oil: 0.95, voltage: 13.6 });
  const [templateResults, setTemplateResults] = useState<TemplateScenarioResult[]>([]);
  const [backwardResult, setBackwardResult] = useState<BackwardVerificationResult | null>(null);
  const [rootEffect, setRootEffect] = useState("BLACK_SMOKE");
  const [rootCauses, setRootCauses] = useState<string[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    void boot();
  }, []);

  useEffect(() => {
    if (view === "template" && templateResults.length === 0) {
      void loadTemplate();
    }
  }, [view]);

  useEffect(() => {
    if (view === "backward" && diagnosticRequest && !backwardResult) {
      void loadBackward(rootEffect);
    }
  }, [view, diagnosticRequest]);

  const caseOptions = useMemo<CaseOption[]>(() => {
    const builtInTitles = new Set(primaryCases.map((option) => option.title));
    const savedOptions = savedCases
      .map((savedCase) => ({
        source: `dataset:${savedCase}` as ReportSource,
        ...(savedCaseLabels[savedCase] ?? {
          title: label(savedCase),
          vehicle: "Saved vehicle case",
          symptoms: "Prepared vehicle symptoms"
        })
      }))
      .filter((option) => !builtInTitles.has(option.title));

    return [...primaryCases, ...savedOptions];
  }, [savedCases]);

  const sortedDiagnoses = useMemo(
    () =>
      [...(report?.diagnoses ?? [])].sort(
        (a, b) =>
          (urgencyRank[b.urgency] ?? 0) - (urgencyRank[a.urgency] ?? 0) ||
          b.confidence - a.confidence
      ),
    [report]
  );

  const mainDiagnosis = sortedDiagnoses[0];
  const highestUrgency = mainDiagnosis?.urgency ?? "NONE";

  async function boot() {
    await withLoading(async () => {
      const [nextReport, nextRequest, nextSavedCases] = await Promise.all([
        api.demoReport(),
        api.demoRequest(),
        api.datasets()
      ]);
      setReport(nextReport);
      setDiagnosticRequest(nextRequest);
      setSavedCases(nextSavedCases);
    });
  }

  async function loadCase(source: ReportSource) {
    setCaseSource(source);
    await withLoading(async () => {
      if (source === "demo") {
        const [nextReport, nextRequest] = await Promise.all([api.demoReport(), api.demoRequest()]);
        setReport(nextReport);
        setDiagnosticRequest(nextRequest);
        return;
      }

      if (source === "transmission") {
        const [nextReport, nextRequest] = await Promise.all([
          api.transmissionReport(),
          api.transmissionRequest()
        ]);
        setReport(nextReport);
        setDiagnosticRequest(nextRequest);
        return;
      }

      const caseName = source.replace("dataset:", "");
      const [nextReport, nextRequest] = await Promise.all([
        api.datasetReport(caseName),
        api.dataset(caseName)
      ]);
      setReport(nextReport);
      setDiagnosticRequest(nextRequest);
    });
  }

  async function loadCep(nextKey: CepKey) {
    setCepKey(nextKey);
    const scenario = cepScenarios.find((item) => item.key === nextKey);
    if (!scenario) {
      return;
    }
    await withLoading(async () => {
      setCepAlerts(await scenario.run());
    });
  }

  async function loadTemplate() {
    await withLoading(async () => {
      setTemplateResults(
        await api.templateDemo(templateParams.coolant, templateParams.oil, templateParams.voltage)
      );
    });
  }

  async function loadBackward(effect = rootEffect) {
    if (!diagnosticRequest) {
      return;
    }
    setRootEffect(effect);
    await withLoading(async () => {
      const [nextResult, nextCauses] = await Promise.all([
        api.verifyLambda(diagnosticRequest),
        api.rootCause(effect)
      ]);
      setBackwardResult(nextResult);
      setRootCauses(nextCauses);
    });
  }

  async function withLoading(action: () => Promise<void>) {
    setLoading(true);
    setError(null);
    try {
      await action();
    } catch (err) {
      setError(err instanceof Error ? err.message : "The check could not be completed.");
    } finally {
      setLoading(false);
    }
  }

  return (
    <main className="app-shell">
      <aside className="rail">
        <div className="brand">
          <strong>Vehicle Diagnostics</strong>
          <span>Clear next steps for drivers</span>
        </div>
        <nav>
          <button className={view === "diagnosis" ? "active" : ""} onClick={() => setView("diagnosis")}>
            Diagnosis
          </button>
          <button className={view === "vehicle" ? "active" : ""} onClick={() => setView("vehicle")}>
            Vehicle details
          </button>
          <button className={view === "trace" ? "active" : ""} onClick={() => setView("trace")}>
            Rule trace
          </button>
          <button className={view === "forward" ? "active" : ""} onClick={() => setView("forward")}>
            Forward chaining
          </button>
          <button className={view === "backward" ? "active" : ""} onClick={() => setView("backward")}>
            Backward chaining
          </button>
          <button className={view === "template" ? "active" : ""} onClick={() => setView("template")}>
            Template rules
          </button>
          <button
            className={view === "cep" ? "active" : ""}
            onClick={() => {
              setView("cep");
              void loadCep(cepKey);
            }}
          >
            CEP scenarios
          </button>
        </nav>
      </aside>

      <section className="workspace">
        <header className="topline">
          <div>
            <p className="eyebrow">Vehicle fault assistant</p>
            <h1>Find out what to do next</h1>
          </div>
          <div className="status">
            <span>{loading ? "Checking" : "Urgency"}</span>
            <strong>{friendlyUrgency(highestUrgency)}</strong>
          </div>
        </header>

        {error ? <div className="error">{error}</div> : null}

        {view === "diagnosis" ? (
          <section className="panel">
            <CasePicker caseOptions={caseOptions} caseSource={caseSource} loadCase={loadCase} />
            <ReportView report={report} diagnoses={sortedDiagnoses} />
          </section>
        ) : null}

        {view === "vehicle" ? (
          <section className="panel">
            <CasePicker caseOptions={caseOptions} caseSource={caseSource} loadCase={loadCase} />
            <VehicleDetails report={report} />
          </section>
        ) : null}

        {view === "trace" ? (
          <section className="panel">
            <CasePicker caseOptions={caseOptions} caseSource={caseSource} loadCase={loadCase} />
            <RuleTrace report={report} />
          </section>
        ) : null}

        {view === "forward" ? (
          <section className="panel">
            <CasePicker caseOptions={caseOptions} caseSource={caseSource} loadCase={loadCase} />
            <ForwardChaining report={report} diagnoses={sortedDiagnoses} />
          </section>
        ) : null}

        {view === "backward" ? (
          <section className="panel">
            <BackwardChaining
              result={backwardResult}
              rootEffect={rootEffect}
              rootCauses={rootCauses}
              loadBackward={loadBackward}
            />
          </section>
        ) : null}

        {view === "template" ? (
          <section className="panel">
            <TemplateRules
              params={templateParams}
              results={templateResults}
              setParams={setTemplateParams}
              runTemplate={loadTemplate}
            />
          </section>
        ) : null}

        {view === "cep" ? (
          <section className="panel">
            <CepScenarios activeKey={cepKey} alerts={cepAlerts} loadCep={loadCep} />
          </section>
        ) : null}
      </section>
    </main>
  );
}

function CasePicker({
  caseOptions,
  caseSource,
  loadCase
}: {
  caseOptions: CaseOption[];
  caseSource: ReportSource;
  loadCase: (source: ReportSource) => Promise<void>;
}) {
  return (
    <>
      <div className="section-intro">
        <h2>Choose the situation closest to your car</h2>
        <p>The app gives practical guidance based on what the car is showing and how it feels.</p>
      </div>

      <div className="scenario-tabs" aria-label="Vehicle situations">
        {caseOptions.map((option) => (
          <button
            className={caseSource === option.source ? "active" : ""}
            key={option.source}
            onClick={() => void loadCase(option.source)}
          >
            <strong>{option.title}</strong>
            <span>{option.vehicle}</span>
            <small>{option.symptoms}</small>
          </button>
        ))}
      </div>
    </>
  );
}

function ReportView({
  report,
  diagnoses
}: {
  report: DiagnosticReport | null;
  diagnoses: DiagnosticReport["diagnoses"];
}) {
  if (!report) {
    return <div className="empty">Choose a situation to see guidance.</div>;
  }

  const mainDiagnosis = diagnoses[0];
  const mainRecommendation = report.recommendations[0];
  const evidence = report.faultCandidates
    .flatMap((candidate) => {
      const value = candidate.evidence;
      return Array.isArray(value) ? value : [];
    })
    .slice(0, 6);

  return (
    <div className="guidance">
      <section className="summary-card">
        <span>Urgency</span>
        <strong>{friendlyUrgency(mainDiagnosis?.urgency ?? "NONE")}</strong>
        <p>{urgencyText(mainDiagnosis?.urgency ?? "NONE")}</p>
      </section>

      <section className="summary-card">
        <span>Vehicle</span>
        <strong>{report.vehicle.make} {report.vehicle.model}</strong>
        <p>{report.vehicle.modelYear} model, {report.vehicle.odometerKm.toLocaleString()} km</p>
      </section>

      <section>
        <h2>Likely problem</h2>
        <div className="stack">
          {diagnoses.map((diagnosis) => (
            <article className="card" key={diagnosis.component}>
              <div className="card-head">
                <h3>{plainComponent(diagnosis.component)}</h3>
                <Badge value={diagnosis.urgency} />
              </div>
              <p>{diagnosis.recommendationText}</p>
              <strong>{plainSystem(diagnosis.system)}</strong>
            </article>
          ))}
        </div>
      </section>

      <section>
        <h2>What you should do</h2>
        <div className="stack">
          {report.recommendations.map((recommendation, index) => (
            <article className="card" key={`${recommendation.action}-${index}`}>
              <div className="card-head">
                <h3>{plainAction(recommendation.action)}</h3>
                <Badge value={recommendation.urgency} />
              </div>
              <p>{recommendation.warningText}</p>
              <small>{plainService(recommendation.serviceType)} / {plainCost(recommendation.costRange)}</small>
            </article>
          ))}
        </div>
      </section>

      <section className="signs">
        <h2>Signs used</h2>
        <div className="card">
          {evidence.length ? (
            <ul>
              {evidence.map((item, index) => (
                <li key={`${item}-${index}`}>{sentence(item)}</li>
              ))}
            </ul>
          ) : (
            <p>{mainRecommendation?.warningText ?? "No additional signs are available."}</p>
          )}
        </div>
      </section>
    </div>
  );
}

function VehicleDetails({ report }: { report: DiagnosticReport | null }) {
  if (!report) {
    return <div className="empty">Choose a situation to see vehicle details.</div>;
  }

  const evidence = report.faultCandidates.flatMap((candidate) => {
    const value = candidate.evidence;
    return Array.isArray(value) ? value : [];
  });

  return (
    <div className="details-grid">
      <article className="summary-card">
        <span>Vehicle</span>
        <strong>{report.vehicle.make} {report.vehicle.model}</strong>
        <p>{report.vehicle.modelYear} model, {report.vehicle.odometerKm.toLocaleString()} km</p>
      </article>
      <article className="summary-card">
        <span>Engine</span>
        <strong>{label(report.vehicle.engineType)}</strong>
        <p>{report.vehicle.engineRunning ? "Engine was running during the check." : "Engine was not running during the check."}</p>
      </article>
      <section className="signs">
        <h2>Signs used</h2>
        <div className="card">
          <ul>
            {evidence.map((item, index) => (
              <li key={`${item}-${index}`}>{sentence(item)}</li>
            ))}
          </ul>
        </div>
      </section>
    </div>
  );
}

function RuleTrace({ report }: { report: DiagnosticReport | null }) {
  if (!report) {
    return <div className="empty">Choose a situation to see the rule trace.</div>;
  }

  return (
    <section className="trace-page">
      <div className="section-intro">
        <h2>Rule trace</h2>
      </div>
      <ol>
        {report.ruleTrace.firedRuleNames.map((rule, index) => (
          <li key={`${rule}-${index}`}>
            <span>{String(index + 1).padStart(2, "0")}</span>
            <strong>{rule}</strong>
          </li>
        ))}
      </ol>
    </section>
  );
}

function ForwardChaining({
  report,
  diagnoses
}: {
  report: DiagnosticReport | null;
  diagnoses: DiagnosticReport["diagnoses"];
}) {
  if (!report) {
    return <div className="empty">Choose a situation to see forward chaining results.</div>;
  }

  return (
    <section className="reasoning-page">
      <div className="section-intro">
        <h2>Forward chaining</h2>
        <p>The result is built from the vehicle facts, fired rules, diagnoses and recommendations.</p>
      </div>

      <div className="metric-grid">
        <article className="summary-card">
          <span>Rules fired</span>
          <strong>{report.firedRuleCount}</strong>
          <p>{report.ruleTrace.firedRuleNames[0] ?? "No rule fired for this case."}</p>
        </article>
        <article className="summary-card">
          <span>Diagnoses found</span>
          <strong>{diagnoses.length}</strong>
          <p>{diagnoses[0] ? plainComponent(diagnoses[0].component) : "No diagnosis produced."}</p>
        </article>
      </div>

      <div className="reasoning-layout">
        <section>
          <h2>Produced diagnoses</h2>
          <div className="stack">
            {diagnoses.map((diagnosis) => (
              <article className="card" key={diagnosis.component}>
                <div className="card-head">
                  <h3>{plainComponent(diagnosis.component)}</h3>
                  <Badge value={diagnosis.urgency} />
                </div>
                <p>{diagnosis.recommendationText}</p>
              </article>
            ))}
          </div>
        </section>

        <section>
          <h2>Rules fired in order</h2>
          <div className="data-list">
            {report.ruleTrace.firedRuleNames.map((rule, index) => (
              <div key={`${rule}-${index}`}>
                <span>{String(index + 1).padStart(2, "0")}</span>
                <strong>{rule}</strong>
              </div>
            ))}
          </div>
        </section>
      </div>
    </section>
  );
}

function BackwardChaining({
  result,
  rootEffect,
  rootCauses,
  loadBackward
}: {
  result: BackwardVerificationResult | null;
  rootEffect: string;
  rootCauses: string[];
  loadBackward: (effect: string) => Promise<void>;
}) {
  const effects = ["BLACK_SMOKE", "POWER_LOSS", "INCREASED_FUEL_CONSUMPTION", "LOW_BATTERY_VOLTAGE", "EGR_VALVE"];

  return (
    <section className="reasoning-page">
      <div className="section-intro">
        <h2>Backward chaining</h2>
        <p>Queries check whether a suspected cause can explain an observed vehicle effect.</p>
      </div>

      <div className="toolbar">
        <label>
          <span>Observed effect</span>
          <select value={rootEffect} onChange={(event) => void loadBackward(event.target.value)}>
            {effects.map((effect) => (
              <option key={effect} value={effect}>
                {label(effect)}
              </option>
            ))}
          </select>
        </label>
      </div>

      <div className="metric-grid">
        <article className="summary-card">
          <span>Lambda check</span>
          <strong>{result?.confirmed ? "Matched" : "No match"}</strong>
          <p>{result ? `${result.matchingBindings} matching query result(s)` : "Run a case to check this query."}</p>
        </article>
        <article className="summary-card">
          <span>Query used</span>
          <strong>{result?.queryName ?? "Waiting"}</strong>
          <p>Shown because backward chaining is part of the required rule demonstration.</p>
        </article>
      </div>

      <section>
        <h2>Possible root causes</h2>
        <div className="data-list">
          {rootCauses.length ? (
            rootCauses.map((cause) => (
              <div key={cause}>
                <strong>{label(cause)}</strong>
              </div>
            ))
          ) : (
            <div>
              <strong>No root cause returned for this effect.</strong>
            </div>
          )}
        </div>
      </section>
    </section>
  );
}

function TemplateRules({
  params,
  results,
  setParams,
  runTemplate
}: {
  params: { coolant: number; oil: number; voltage: number };
  results: TemplateScenarioResult[];
  setParams: (params: { coolant: number; oil: number; voltage: number }) => void;
  runTemplate: () => Promise<void>;
}) {
  return (
    <section className="reasoning-page">
      <div className="section-intro">
        <h2>Template rules</h2>
        <p>The same readings are checked against petrol, diesel and hybrid thresholds generated from a rule template.</p>
      </div>

      <div className="input-grid">
        <label>
          <span>Coolant temp</span>
          <input
            type="number"
            value={params.coolant}
            onChange={(event) => setParams({ ...params, coolant: Number(event.target.value) })}
          />
        </label>
        <label>
          <span>Oil pressure</span>
          <input
            type="number"
            step="0.05"
            value={params.oil}
            onChange={(event) => setParams({ ...params, oil: Number(event.target.value) })}
          />
        </label>
        <label>
          <span>Battery voltage</span>
          <input
            type="number"
            step="0.1"
            value={params.voltage}
            onChange={(event) => setParams({ ...params, voltage: Number(event.target.value) })}
          />
        </label>
        <button onClick={() => void runTemplate()}>Run template</button>
      </div>

      <div className="card-grid">
        {results.map((result) => (
          <article className="card" key={result.engineType}>
            <div className="card-head">
              <h3>{label(result.engineType)}</h3>
              <span className="badge badge-medium">{result.firedRuleCount} fired</span>
            </div>
            <div className="compact-list">
              {result.firedRules.length ? (
                result.firedRules.map((rule) => <span key={rule}>{rule}</span>)
              ) : (
                <span>No threshold rule fired.</span>
              )}
            </div>
          </article>
        ))}
      </div>
    </section>
  );
}

function CepScenarios({
  activeKey,
  alerts,
  loadCep
}: {
  activeKey: CepKey;
  alerts: CepAlert[];
  loadCep: (key: CepKey) => Promise<void>;
}) {
  return (
    <section className="reasoning-page">
      <div className="section-intro">
        <h2>CEP scenarios</h2>
        <p>These six scenarios use time-based sensor events, separate from the normal vehicle case picker.</p>
      </div>

      <div className="scenario-tabs" aria-label="CEP scenarios">
        {cepScenarios.map((scenario) => (
          <button
            className={activeKey === scenario.key ? "active" : ""}
            key={scenario.key}
            onClick={() => void loadCep(scenario.key)}
          >
            <strong>{scenario.title}</strong>
            <small>{scenario.signal}</small>
          </button>
        ))}
      </div>

      <section>
        <h2>Detected alerts</h2>
        <div className="data-list">
          {alerts.length ? (
            alerts.map((alert, index) => (
              <div key={`${alert.type}-${alert.detectedAt}-${index}`}>
                <span>{label(alert.type)}</span>
                <strong>{alert.message}</strong>
                <small>{alert.vehicleId} / {new Date(alert.detectedAt).toLocaleString()}</small>
              </div>
            ))
          ) : (
            <div>
              <strong>No alert returned for this scenario.</strong>
            </div>
          )}
        </div>
      </section>
    </section>
  );
}

function Badge({ value }: { value: string }) {
  return <span className={`badge badge-${value.toLowerCase()}`}>{friendlyUrgency(value)}</span>;
}

function friendlyUrgency(value: string) {
  if (value === "CRITICAL") {
    return "Stop driving";
  }
  if (value === "HIGH") {
    return "Repair soon";
  }
  if (value === "MEDIUM") {
    return "Book service";
  }
  if (value === "LOW") {
    return "Monitor";
  }
  return "No result";
}

function urgencyText(value: string) {
  if (value === "CRITICAL") {
    return "Do not continue driving until the car is checked.";
  }
  if (value === "HIGH") {
    return "Avoid long or demanding trips and arrange repair quickly.";
  }
  if (value === "MEDIUM") {
    return "Plan a service visit before the next regular interval.";
  }
  if (value === "LOW") {
    return "Keep an eye on it and recheck if the warning returns.";
  }
  return "Choose a vehicle situation to get guidance.";
}

function plainAction(value: string) {
  const map: Record<string, string> = {
    STOP_DRIVING_IMMEDIATELY: "Stop driving",
    REPAIR_WITHIN_48H: "Arrange repair soon",
    REPAIR_BEFORE_NEXT_SERVICE: "Book a service visit",
    MONITOR: "Monitor and recheck"
  };
  return map[value] ?? label(value);
}

function plainService(value: string) {
  const map: Record<string, string> = {
    DIESEL_SPECIALIST: "Diesel specialist",
    AUTO_ELECTRICIAN: "Auto electrician",
    TYRE_SHOP: "Tyre shop",
    BRAKE_SERVICE: "Brake service",
    MECHANIC: "Mechanic"
  };
  return map[value] ?? label(value);
}

function plainCost(value: string) {
  const map: Record<string, string> = {
    LOW: "lower cost",
    LOW_TO_MEDIUM: "lower to medium cost",
    MEDIUM: "medium cost",
    HIGH: "higher cost"
  };
  return map[value] ?? label(value).toLowerCase();
}

function plainComponent(value: string) {
  const map: Record<string, string> = {
    OIL_PUMP: "Oil pressure problem",
    EGR_VALVE: "EGR flow problem",
    IGNITION_COIL: "Ignition misfire",
    LAMBDA_SENSOR: "Oxygen sensor problem",
    ALTERNATOR: "Charging system problem",
    BRAKE_PAD_OR_DISC: "Brake pad or disc problem",
    LOW_TYRE_PRESSURE: "Low tyre pressure",
    TRANSMISSION_OR_CLUTCH: "Transmission or clutch problem",
    TURBO_ACTUATOR: "Turbo boost control problem"
  };
  return map[value] ?? label(value);
}

function plainSystem(value: string) {
  const map: Record<string, string> = {
    COOLING_SYSTEM: "Cooling area",
    LUBRICATION_SYSTEM: "Oil and lubrication area",
    IGNITION_SYSTEM: "Ignition area",
    FUEL_AIR_SYSTEM: "Fuel and air area",
    ELECTRICAL_SYSTEM: "Electrical area",
    BRAKE_SYSTEM: "Brake area",
    TYRE_SYSTEM: "Tyre area",
    TRANSMISSION_SYSTEM: "Transmission area"
  };
  return map[value] ?? label(value);
}

function sentence(value: unknown) {
  if (typeof value !== "string") {
    return "";
  }
  return value.charAt(0).toUpperCase() + value.slice(1);
}

function label(value: string) {
  return value
    .toLowerCase()
    .split(/[_-]/)
    .map((part) => part.charAt(0).toUpperCase() + part.slice(1))
    .join(" ");
}
