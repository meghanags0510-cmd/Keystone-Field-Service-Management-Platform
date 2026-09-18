# Deployment Checklist

The assignment requires public deployment. This package intentionally does **not** invent fake live URLs. Deploy it and replace the placeholders in your submission form with the real URLs.

## Backend — Render
1. Create a PostgreSQL database.
2. Create a Web Service from the `backend` directory.
3. Build command:
   `./mvnw clean package` (or `mvn clean package` if Maven is installed)
4. Start command:
   `java -jar target/keystone-backend-1.0.0.jar`
5. Environment variables:
   - `DB_URL=jdbc:postgresql://...`
   - `DB_USERNAME=...`
   - `DB_PASSWORD=...`
   - `JWT_SECRET=<long-random-secret>`
   - `FRONTEND_ORIGIN=https://<your-vercel-app>`
6. Confirm:
   - `/actuator/health`
   - `/swagger-ui.html`

## Frontend — Vercel / Netlify
1. Use the `frontend` directory.
2. Build:
   `npm run build`
3. Publish directory:
   `dist`
4. Set:
   `VITE_API_URL=https://<your-backend>/`

## Submission URL checklist
- GitHub repository: `<paste after push>`
- Frontend: `<paste after deployment>`
- Backend API: `<paste after deployment>`
- Swagger: `<paste after deployment>/swagger-ui.html`
- Demo video: `<paste unlisted YouTube/Drive URL>`
- Feedback video: `<paste Drive/YouTube URL>`
- Report PDF: `<upload exported PDF>`
