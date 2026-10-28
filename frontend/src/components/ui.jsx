import React, { useEffect } from 'react'
import { AlertCircle, CircleCheck, Search, X } from 'lucide-react'
import { GRADE_COLORS, GRADIENTS, initials } from '../utils'

export function Avatar({ name, size = 34 }) {
  const hash = [...(name || '?')].reduce((h, c) => (h * 31 + c.charCodeAt(0)) >>> 0, 7)
  const [from, to] = GRADIENTS[hash % GRADIENTS.length]
  return (
    <div className="avatar-chip" aria-hidden="true"
      style={{ width: size, height: size, fontSize: Math.max(10, size * 0.34), background: `linear-gradient(135deg, ${from}, ${to})` }}>
      {initials(name)}
    </div>
  )
}

export function GradeBadge({ grade }) {
  const color = GRADE_COLORS[grade] || GRADE_COLORS['N/A']
  return <span className="grade" style={{ '--g': color }}>{grade}</span>
}

export function StatusBadge({ status }) {
  return <span className={`status status-${String(status).toLowerCase()}`}><i />{status}</span>
}

export function Empty({ text, icon: Icon = Search }) {
  return <div className="empty"><div className="empty-icon"><Icon size={19} /></div><span>{text}</span></div>
}

export function Skeleton({ rows = 5 }) {
  return <div className="skeleton-list" aria-busy="true">{Array.from({ length: rows }, (_, i) => <div key={i} className="skeleton" />)}</div>
}

export function ErrorBanner({ message, onRetry }) {
  if (!message) return null
  return (
    <div className="banner" role="alert">
      <AlertCircle size={18} /><span>{message}</span>
      {onRetry && <button onClick={onRetry}>Retry</button>}
    </div>
  )
}

export function PageHeading({ eyebrow, title, subtitle, children }) {
  return (
    <div className="page-head">
      <div>
        <div className="eyebrow">{eyebrow}</div>
        <h1>{title}<span className="accent">.</span></h1>
        {subtitle && <p>{subtitle}</p>}
      </div>
      <div className="head-actions">{children}</div>
    </div>
  )
}

export function Card({ title, subtitle, chip, action, className = '', children }) {
  return (
    <section className={`card ${className}`}>
      {(title || action) && (
        <div className="card-head">
          <div><h3>{title}</h3>{subtitle && <p>{subtitle}</p>}</div>
          {chip && <span className="chip">{chip}</span>}
          {action}
        </div>
      )}
      {children}
    </section>
  )
}

export function Toast({ toast, onClose }) {
  useEffect(() => {
    if (!toast) return undefined
    const id = setTimeout(onClose, 3200)
    return () => clearTimeout(id)
  }, [toast, onClose])
  if (!toast) return null
  const Icon = toast.type === 'error' ? AlertCircle : CircleCheck
  return <div className={`toast toast-${toast.type}`} role="status"><Icon size={17} />{toast.message}</div>
}

export function Modal({ title, eyebrow, subtitle, onClose, children, wide = false }) {
  useEffect(() => {
    const onKey = e => { if (e.key === 'Escape') onClose() }
    window.addEventListener('keydown', onKey)
    return () => window.removeEventListener('keydown', onKey)
  }, [onClose])
  return (
    <div className="backdrop" onMouseDown={e => { if (e.target === e.currentTarget) onClose() }}>
      <div className={`modal ${wide ? 'modal-wide' : ''}`} role="dialog" aria-modal="true" aria-label={title}>
        <div className="modal-head">
          <div>{eyebrow && <span className="eyebrow">{eyebrow}</span>}<h2>{title}</h2>{subtitle && <p>{subtitle}</p>}</div>
          <button type="button" className="icon-btn" onClick={onClose} aria-label="Close"><X size={19} /></button>
        </div>
        {children}
      </div>
    </div>
  )
}

export function ConfirmDialog({ title, message, confirmLabel = 'Delete', onConfirm, onCancel, busy }) {
  return (
    <Modal title={title} onClose={onCancel}>
      <p className="confirm-text">{message}</p>
      <div className="modal-actions">
        <button className="btn btn-light" onClick={onCancel}>Cancel</button>
        <button className="btn btn-danger" onClick={onConfirm} disabled={busy}>{busy ? 'Working…' : confirmLabel}</button>
      </div>
    </Modal>
  )
}
