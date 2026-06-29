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

async function request<T>(url: string, init?: RequestInit): Promise<T> {
  const response = await fetch(url, {
    headers: { "Content-Type": "application/json" },
    ...init
  });

  if (!response.ok) {
    const message = await response.text();
    throw new Error(message || `Request failed with ${response.status}`);
  }

  return response.json() as Promise<T>;
}

export const api = {
  demoReport: () => request<DiagnosticReport>("/api/diagnostics/demo/report"),
  transmissionReport: () =>
    request<DiagnosticReport>("/api/diagnostics/demo/transmission/report"),
  cepTyrePressureReport: () =>
    request<DiagnosticReport>("/api/diagnostics/demo/cep/tyre-pressure/report"),
  demoRequest: () => request<DiagnosticRequest>("/api/diagnostics/demo/request"),
  transmissionRequest: () =>
    request<DiagnosticRequest>("/api/diagnostics/demo/transmission/request"),
  cepTyrePressureRequest: () =>
    request<DiagnosticRequest>("/api/diagnostics/demo/cep/tyre-pressure/request"),
  rules: () => request<RuleInfo[]>("/api/diagnostics/rules"),
  evaluate: (body: DiagnosticRequest) =>
    request<DiagnosticReport>("/api/diagnostics/evaluate", {
      method: "POST",
      body: JSON.stringify(body)
    })
};
