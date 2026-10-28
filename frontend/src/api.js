const BASE = import.meta.env.VITE_API_URL || 'http://localhost:8080/api'

export class ApiError extends Error {
  constructor(message, fieldErrors = {}) {
    super(message)
    this.fieldErrors = fieldErrors
  }
}

/** Builds "?a=1&b=2" and skips empty values so the URL stays clean. */
const qs = (params = {}) => {
  const p = new URLSearchParams()
  Object.entries(params).forEach(([k, v]) => {
    if (v !== undefined && v !== null && v !== '') p.set(k, v)
  })
  const s = p.toString()
  return s ? `?${s}` : ''
}

async function request(path, options = {}) {
  let response
  try {
    response = await fetch(`${BASE}${path}`, {
      ...options,
      headers: { 'Content-Type': 'application/json', ...(options.headers || {}) },
    })
  } catch {
    throw new ApiError('Cannot reach the server. Make sure Spring Boot is running on port 8080 and MySQL is configured.')
  }
  if (!response.ok) {
    let message = `Request failed (${response.status})`
    let fields = {}
    try {
      const data = await response.json()
      message = data.message || message
      fields = data.errors || {}
    } catch { /* response had no JSON body */ }
    throw new ApiError(message, fields)
  }
  return response.status === 204 ? null : response.json()
}

const json = body => JSON.stringify(body)

export const api = {
  dashboard: () => request('/dashboard'),
  classes: () => request('/classes'),
  students: params => request(`/students${qs(params)}`),
  create: body => request('/students', { method: 'POST', body: json(body) }),
  update: (id, body) => request(`/students/${id}`, { method: 'PUT', body: json(body) }),
  remove: id => request(`/students/${id}`, { method: 'DELETE' }),
  rankings: className => request(`/rankings${qs({ className })}`),
  top: (limit = 10, className) => request(`/rankings/top${qs({ limit, className })}`),
  dsaSort: params => request(`/dsa/sort${qs(params)}`),
  dsaSearch: params => request(`/dsa/search${qs(params)}`),
}
