import { mkdir, writeFile } from 'node:fs/promises';
import { dirname, resolve } from 'node:path';
import { fileURLToPath } from 'node:url';

const apiBaseUrl = (process.env.API_BASE_URL ?? 'http://localhost:8080/api/v1').trim();
let parsedUrl;

try {
  parsedUrl = new URL(apiBaseUrl);
} catch {
  throw new Error('API_BASE_URL must be an absolute HTTP or HTTPS URL ending in /api/v1.');
}

if (
  !['http:', 'https:'].includes(parsedUrl.protocol) ||
  parsedUrl.username ||
  parsedUrl.password ||
  parsedUrl.search ||
  parsedUrl.hash ||
  !parsedUrl.pathname.replace(/\/+$/, '').endsWith('/api/v1')
) {
  throw new Error('API_BASE_URL must be an absolute HTTP or HTTPS URL ending in /api/v1.');
}

const publicDirectory = resolve(dirname(fileURLToPath(import.meta.url)), '../public');
const configFile = resolve(publicDirectory, 'app-config.js');

await mkdir(publicDirectory, { recursive: true });
await writeFile(
  configFile,
  `window.__APP_CONFIG__ = ${JSON.stringify({ apiBaseUrl: apiBaseUrl.replace(/\/+$/, '') })};\n`,
  'utf8'
);
