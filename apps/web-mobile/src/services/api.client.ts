const configuredUrl = process.env.EXPO_PUBLIC_API_URL?.trim();

export function getApiBaseUrl() {
  const baseUrl = (configuredUrl || 'https://orbit-erp-api-p9vp.onrender.com').replace(/\/$/, '');

  return baseUrl.endsWith('/api') ? baseUrl : `${baseUrl}/api`;
}

export async function pingApi(timeoutMs = 3500): Promise<boolean> {
  const controller = new AbortController();
  const timeout = setTimeout(() => controller.abort(), timeoutMs);

  try {
    const healthUrl = getApiBaseUrl().replace(/\/api$/, '') + '/health';
    const response = await fetch(healthUrl, { signal: controller.signal });
    return response.ok;
  } catch {
    return false;
  } finally {
    clearTimeout(timeout);
  }
}