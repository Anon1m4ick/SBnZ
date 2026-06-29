# Vehicle Fault Diagnostics Client

Lightweight React + Radix UI client for demonstrating the Drools diagnostic service.

## Run

```powershell
npm install
npm run dev
```

The Vite dev server runs on `http://localhost:5173` and proxies `/api` to the Spring Boot service on `http://localhost:8080`.

## Features

- Load diesel, transmission, and CEP tyre-pressure demo reports.
- Display diagnoses, recommendations, fired rules, and rule catalog.
- Edit the current request JSON and post it to `/api/diagnostics/evaluate`.
