# Production Deployment

The Angular frontend is deployed to Vercel. The Spring Boot API runs as a Docker web service on Render, and MySQL runs on Railway. The API URL and database credentials are deployment settings; do not commit them.

> The selected Render free plan is for a demo, not production: the service can spin down while idle. Railway MySQL usage and public TCP egress may still incur charges.

## 1. Provision MySQL on Railway

1. Create a MySQL service in Railway and choose a region close to the Render service.
2. Initialize the database with `database/schema.sql`. Do not load sample data into a production database unless it is intentionally required.
3. Enable Railway public networking/TCP proxy so the Render service can connect across providers. This may incur network egress charges.
4. Record the public host, port, database name, username, and password in a password manager. Configure them as Render environment variables; never add them to this repository.

## 2. Deploy the API on Render

The root `render.yaml` defines a free web service for the demo. Link the repository to Render and deploy the Blueprint. Set the prompted variables using the Railway public connection details:

- `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USERNAME`, `DB_PASSWORD`
- `CORS_ALLOWED_ORIGINS` to the exact Vercel origin, for example `https://your-project.vercel.app`

The Render Blueprint activates the `production` Spring profile. `DB_SSL_MODE` defaults to `REQUIRED`, and the service listens on Render's `PORT`. Keep the database schema compatible with the API; Hibernate validates the schema at startup.

After deployment, verify `https://<render-service>.onrender.com/api/v1/dashboard/summary` returns HTTP 200. This endpoint requires a reachable, initialized database.

## 3. Deploy the frontend on Vercel

From `frontend`, configure the Vercel project root as `frontend` and add this production environment variable in the Vercel project settings:

```text
API_BASE_URL=https://<render-service>.onrender.com/api/v1
```

The build script generates an ignored `public/app-config.js` from `API_BASE_URL`; local builds default to `http://localhost:8080/api/v1`. The Vercel rewrite serves Angular routes on direct navigation.

Authenticate and deploy with the Vercel CLI:

```powershell
cd frontend
npx vercel login
npx vercel link
npx vercel --prod
```

Set `CORS_ALLOWED_ORIGINS` on Render to the final Vercel production origin. Redeploy the API after changing that setting.

## Production checks

- Confirm the Vercel build uses the Render HTTPS API URL, not localhost.
- Confirm API CORS allows only the intended Vercel origin.
- Confirm the database uses TLS and credentials are only in provider environment settings.
- Upgrade Render to a paid, always-on instance and configure Railway backups/monitoring before using this as production.
- The frontend build currently succeeds but reports that its initial bundle is above Angular's 500 kB warning budget.
