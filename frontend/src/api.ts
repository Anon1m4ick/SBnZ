export type Vehicle = {
  vehicleId: string;
  make: string;
  model: string;
  engineType: string;
  modelYear: number;
  odometerKm: number;
  engineRunning: boolean;
};

export type Diagnosis = {
  component: string;
  system: string;
  confidence: number;
  urgency: string;
  recommendationText: string;
  ruleTrace: string[];
};

export type Recommendation = {
  action: string;
  serviceType: string;
  costRange: string;
  urgency: string;
  warningText: string;
  components: string[];
};

export type RuleTrace = {
  firedRuleNames: string[];
  timestamp: string;
  explanation: string;
};

export type DiagnosticReport = {
  vehicle: Vehicle;
  systemProblems: Array<Record<string, unknown>>;
  faultCandidates: Array<Record<string, unknown>>;
  diagnoses: Diagnosis[];
  recommendations: Recommendation[];
  ruleTrace: RuleTrace;
  firedRuleCount: number;
};

export type DiagnosticRequest = Record<string, unknown>;

export type RuleInfo = {
  id: string;
  module: string;
  responsibility: string;
  description: string;
};

export type TemplateScenarioResult = {
  engineType: string;
  coolantTemp: number;
  oilPressure: number;
  voltage: number;
  firedRuleCount: number;
  firedRules: string[];
  systemProblems: Array<Record<string, unknown>>;
};

export type CepAlert = {
  type: string;
  vehicleId: string;
  message: string;
  detectedAt: string;
};

export type BackwardVerificationResult = {
  confirmed: boolean;
  matchingBindings: number;
  queryName: string;
};

async function request<T>(url: string, init?: RequestInit): Promise<T> {
  const response = await fetch(url, {
    headers: { "Content-Type": "application/json" },
    ...init
  });

  if (!response.ok) {
    const message = await response.text();
    throw new Error(message || `Request failed with ${response.status}`);
  }

  if (typeof response.headers.get("content-type") === "string" &&
      response.headers.get("content-type")?.includes("text/plain")) {
    return response.text() as Promise<T>;
  }

  return response.json() as Promise<T>;
}

export const api = {
  demoReport: () => request<DiagnosticReport>("/api/diagnostics/demo/report"),
  demoRequest: () => request<DiagnosticRequest>("/api/diagnostics/demo/request"),
  transmissionReport: () =>
    request<DiagnosticReport>("/api/diagnostics/demo/transmission/report"),
  transmissionRequest: () =>
    request<DiagnosticRequest>("/api/diagnostics/demo/transmission/request"),
  demoDiagnoses: () => request<Diagnosis[]>("/api/diagnostics/demo/diagnoses"),
  demoRecommendations: () =>
    request<Recommendation[]>("/api/diagnostics/demo/recommendations"),
  demoTrace: () => request<RuleTrace>("/api/diagnostics/demo/trace"),
  rules: () => request<RuleInfo[]>("/api/diagnostics/rules"),
  evaluate: (body: DiagnosticRequest) =>
    request<DiagnosticReport>("/api/diagnostics/evaluate", {
      method: "POST",
      body: JSON.stringify(body)
    }),
  templateDrl: () => request<string>("/api/diagnostics/template/drl"),
  templateDemo: (coolant: number, oil: number, voltage: number) =>
    request<TemplateScenarioResult[]>(
      `/api/diagnostics/template/demo?coolant=${coolant}&oil=${oil}&voltage=${voltage}`
    ),
  templateScenario: (engine: string, coolant: number, oil: number, voltage: number) =>
    request<TemplateScenarioResult>(
      `/api/diagnostics/template/scenario?engine=${engine}&coolant=${coolant}&oil=${oil}&voltage=${voltage}`
    ),
  cepOverheating: () => request<CepAlert[]>("/api/diagnostics/cep/overheating"),
  cepVoltage: () => request<CepAlert[]>("/api/diagnostics/cep/voltage"),
  cepMisfire: () => request<CepAlert[]>("/api/diagnostics/cep/misfire"),
  cepMaf: () => request<CepAlert[]>("/api/diagnostics/cep/maf"),
  cepTyrePressure: () => request<CepAlert[]>("/api/diagnostics/cep/tyre-pressure"),
  cepCriticalWarnings: () =>
    request<CepAlert[]>("/api/diagnostics/cep/critical-warnings"),
  verifyLambda: (body: DiagnosticRequest) =>
    request<BackwardVerificationResult>("/api/diagnostics/backward/verify-lambda", {
      method: "POST",
      body: JSON.stringify(body)
    }),
  rootCause: (effect: string) =>
    request<string[]>(`/api/diagnostics/backward/root-cause?effect=${encodeURIComponent(effect)}`),
  datasets: () => request<string[]>("/api/diagnostics/datasets"),
  dataset: (name: string) =>
    request<DiagnosticRequest>(`/api/diagnostics/datasets/${name}`),
  datasetReport: (name: string) =>
    request<DiagnosticReport>(`/api/diagnostics/datasets/${name}/report`)
};
