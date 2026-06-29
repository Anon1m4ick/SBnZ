import * as Tabs from "@radix-ui/react-tabs";
import { useEffect, useMemo, useState } from "react";
import { api, DiagnosticReport, DiagnosticRequest, RuleInfo } from "./api";

type Scenario = "diesel" | "transmission" | "cep" | "custom";

const scenarios: Array<{
  id: Exclude<Scenario, "custom">;
  name: string;
  context: string;
}> = [
  { id: "diesel", name: "Diesel multi-fault", context: "Forward chaining" },
  { id: "transmission", name: "Transmission jerk", context: "Negative condition" },
  { id: "cep", name: "Tyre pressure drop", context: "CEP stream" }
];

const urgencyOrder: Record<string, number> = {
  CRITICAL: 4,
  HIGH: 3,
  MEDIUM: 2,
  LOW: 1
};

export function App() {
  const [scenario, setScenario] = useState<Scenario>("diesel");
  const [report, setReport] = useState<DiagnosticReport | null>(null);
  const [rules, setRules] = useState<RuleInfo[]>([]);
  const [requestJson, setRequestJson] = useState("");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    void loadInitialData();
  }, []);

  const sortedDiagnoses = useMemo(
    () =>
      [...(report?.diagnoses ?? [])].sort(
        (a, b) =>
          (urgencyOrder[b.urgency] ?? 0) - (urgencyOrder[a.urgency] ?? 0) ||
          b.confidence - a.confidence
      ),
    [report]
  );
  const vehicle = report?.vehicle;
  const firedRules = report?.ruleTrace?.firedRuleNames ?? [];
  const mostUrgent = sortedDiagnoses[0]?.urgency ?? "NONE";
  const activeScenarioName =
    scenario === "custom"
      ? "Custom JSON"
      : scenarios.find((option) => option.id === scenario)?.name ?? label(scenario);

  async function loadInitialData() {
    setLoading(true);
    setError(null);

    try {
      const [demoReport, demoRequest, ruleList] = await Promise.all([
        api.demoReport(),
        api.demoRequest(),
        api.rules()
      ]);
      setReport(demoReport);
      setRequestJson(formatJson(demoRequest));
      setRules(ruleList);
    } catch (err) {
      setError(messageFrom(err));
    } finally {
      setLoading(false);
    }
  }

  async function loadScenario(nextScenario: Exclude<Scenario, "custom">) {
    setLoading(true);
    setError(null);
    setScenario(nextScenario);

    try {
      const [nextReport, nextRequest] = await loadScenarioData(nextScenario);

      setReport(nextReport);
      setRequestJson(formatJson(nextRequest));
    } catch (err) {
      setError(messageFrom(err));
    } finally {
      setLoading(false);
    }
  }

  async function evaluateCustom() {
    setLoading(true);
    setError(null);
    setScenario("custom");

    try {
      const parsed = JSON.parse(requestJson) as DiagnosticRequest;
      const nextReport = await api.evaluate(parsed);
      setReport(nextReport);
    } catch (err) {
      setError(messageFrom(err));
    } finally {
      setLoading(false);
    }
  }

  return (
    <main className="app-shell">
      <aside className="command-rail" aria-label="Diagnostic controls">
        <div className="brand-block">
          <div>
            <strong>Diagnostic Bay</strong>
            <span>Drools rule engine</span>
          </div>
        </div>

        <nav className="scenario-nav" aria-label="Demo scenarios">
          {scenarios.map((option, index) => (
            <button
              className={scenario === option.id ? "active" : ""}
              key={option.id}
              onClick={() => void loadScenario(option.id)}
            >
              <span>{String(index + 1).padStart(2, "0")}</span>
              <strong>{option.name}</strong>
              <small>{option.context}</small>
            </button>
          ))}
        </nav>

        <button className="run-button" onClick={() => void evaluateCustom()}>
          Run JSON
        </button>

        <div className="rail-readout">
          <span>Active set</span>
          <strong>{activeScenarioName}</strong>
          <span>Rules loaded</span>
          <strong>{rules.length || "-"}</strong>
        </div>
      </aside>

      <section className="workspace">
        <header className="diagnostic-header">
          <div>
            <p className="eyebrow">Workshop console</p>
            <h1>Vehicle Fault Diagnostics</h1>
            <p className="header-subtitle">
              {vehicle
                ? `${vehicle.make} ${vehicle.model} - ${vehicle.modelYear} - ${vehicle.odometerKm.toLocaleString()} km`
                : loading
                  ? "Loading diagnostic report"
                  : "No vehicle loaded"}
            </p>
          </div>

          <div className="vehicle-card">
            <span className="vehicle-label">Current vehicle</span>
            <strong>
              {vehicle ? `${vehicle.make} ${vehicle.model}` : "-"}
            </strong>
            <div className="vehicle-specs">
              <span>{vehicle?.engineType ? label(vehicle.engineType) : "-"}</span>
              <span>{vehicle?.vehicleId ?? "-"}</span>
            </div>
          </div>
        </header>

        {error ? <div className="error">{error}</div> : null}

        <section className="telemetry-strip" aria-live="polite">
          <Metric label="Rules fired" value={report?.firedRuleCount ?? "-"} />
          <Metric label="Diagnoses" value={report?.diagnoses.length ?? "-"} />
          <Metric
            label="Recommendations"
            value={report?.recommendations.length ?? "-"}
          />
          <article className="metric urgency-metric">
            <span>Highest urgency</span>
            <Badge tone={mostUrgent}>{label(mostUrgent)}</Badge>
          </article>
        </section>

        <Tabs.Root className="workbench" defaultValue="diagnosis">
          <Tabs.List className="workbench-tabs" aria-label="Diagnostic views">
            <Tabs.Trigger value="diagnosis">Diagnosis</Tabs.Trigger>
            <Tabs.Trigger value="trace">Rule trace</Tabs.Trigger>
            <Tabs.Trigger value="input">Input JSON</Tabs.Trigger>
            <Tabs.Trigger value="rules">Rules</Tabs.Trigger>
          </Tabs.List>

          <Tabs.Content value="diagnosis" className="tab-panel">
            <section className="inspection-grid">
              <div className="lane">
                <div className="section-heading">
                  <span>01</span>
                  <h2>Faults</h2>
                </div>
                <div className="stack">
                  {sortedDiagnoses.map((diagnosis) => (
                    <article className="item" key={diagnosis.component}>
                      <div className="item-heading">
                        <span>{label(diagnosis.component)}</span>
                        <Badge tone={diagnosis.urgency}>
                          {label(diagnosis.urgency)}
                        </Badge>
                      </div>
                      <p>{diagnosis.recommendationText}</p>
                      <div className="meter">
                        <span
                          style={{
                            width: `${Math.round(diagnosis.confidence * 100)}%`
                          }}
                        />
                      </div>
                      <div className="meta-row">
                        <span>{label(diagnosis.system)}</span>
                        <span>{Math.round(diagnosis.confidence * 100)}%</span>
                      </div>
                    </article>
                  ))}
                  {!sortedDiagnoses.length ? <EmptyState label="No diagnoses" /> : null}
                </div>
              </div>

              <div className="lane lane-accent">
                <div className="section-heading">
                  <span>02</span>
                  <h2>Actions</h2>
                </div>
                <div className="stack">
                  {(report?.recommendations ?? []).map((recommendation, index) => (
                    <article className="item" key={`${recommendation.action}-${index}`}>
                      <div className="item-heading">
                        <span>{label(recommendation.action)}</span>
                        <Badge tone={recommendation.urgency}>
                          {label(recommendation.urgency)}
                        </Badge>
                      </div>
                      <p>{recommendation.warningText}</p>
                      <div className="meta-row">
                        <span>{label(recommendation.serviceType)}</span>
                        <span>{label(recommendation.costRange)}</span>
                      </div>
                    </article>
                  ))}
                  {!report?.recommendations.length ? (
                    <EmptyState label="No recommendations" />
                  ) : null}
                </div>
              </div>
            </section>
          </Tabs.Content>

          <Tabs.Content value="trace" className="tab-panel">
            <section>
              <div className="section-heading">
                <span>03</span>
                <h2>Fired rules</h2>
              </div>
              <ol className="trace-list">
                {firedRules.map((rule, index) => (
                  <li key={`${rule}-${index}`}>
                    <span>{String(index + 1).padStart(2, "0")}</span>
                    <strong>{rule}</strong>
                  </li>
                ))}
              </ol>
              {!firedRules.length ? <EmptyState label="No fired rules" /> : null}
            </section>
          </Tabs.Content>

          <Tabs.Content value="input" className="tab-panel">
            <section className="json-editor">
              <div className="editor-heading">
                <div className="section-heading">
                  <span>04</span>
                  <h2>Diagnostic request</h2>
                </div>
                <button onClick={() => void evaluateCustom()}>Evaluate</button>
              </div>
              <textarea
                value={requestJson}
                onChange={(event) => setRequestJson(event.target.value)}
                spellCheck={false}
              />
            </section>
          </Tabs.Content>

          <Tabs.Content value="rules" className="tab-panel">
            <section>
              <div className="section-heading">
                <span>05</span>
                <h2>Implemented rules</h2>
              </div>
              <div className="rule-grid">
                {rules.map((rule) => (
                  <article className="rule" key={rule.id}>
                    <strong>{rule.id}</strong>
                    <span>{rule.module} - {rule.responsibility}</span>
                    <p>{rule.description}</p>
                  </article>
                ))}
              </div>
            </section>
          </Tabs.Content>
        </Tabs.Root>
      </section>
    </main>
  );
}

function loadScenarioData(scenario: Exclude<Scenario, "custom">) {
  if (scenario === "diesel") {
    return Promise.all([api.demoReport(), api.demoRequest()]);
  }

  if (scenario === "transmission") {
    return Promise.all([api.transmissionReport(), api.transmissionRequest()]);
  }

  return Promise.all([api.cepTyrePressureReport(), api.cepTyrePressureRequest()]);
}

function Metric({ label, value }: { label: string; value: string | number }) {
  return (
    <article className="metric">
      <span>{label}</span>
      <strong>{value}</strong>
    </article>
  );
}

function EmptyState({ label }: { label: string }) {
  return <div className="empty-state">{label}</div>;
}

function Badge({
  children,
  tone
}: {
  children: string;
  tone: string;
}) {
  return <span className={`badge badge-${tone.toLowerCase()}`}>{children}</span>;
}

function formatJson(value: unknown) {
  return JSON.stringify(value, null, 2);
}

function messageFrom(error: unknown) {
  return error instanceof Error ? error.message : "Unexpected error";
}

function label(value: string) {
  return value
    .toLowerCase()
    .split("_")
    .map((part) => part.charAt(0).toUpperCase() + part.slice(1))
    .join(" ");
}
