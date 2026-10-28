import React, { useState } from 'react'
import { Plus, X } from 'lucide-react'
import { Modal } from './ui'
import { api } from '../api'
import { emptyRecord, emptyStudent, ordinal } from '../utils'

const toForm = s => s ? {
  rollNumber: s.rollNumber, fullName: s.fullName, email: s.email, phone: s.phone || '', className: s.className,
  department: s.department, yearOfStudy: s.yearOfStudy, status: s.status,
  records: s.records?.length ? s.records.map(({ term, subject, marksObtained, maxMarks }) => ({ term, subject, marksObtained, maxMarks })) : [emptyRecord()],
} : emptyStudent()

function Field({ label, error, children }) {
  return <label className={`field ${error ? 'has-error' : ''}`}>{label}{children}{error && <small>{error}</small>}</label>
}

export default function StudentModal({ student, classes, onClose, onSaved }) {
  const [form, setForm] = useState(() => toForm(student))
  const [saving, setSaving] = useState(false)
  const [error, setError] = useState('')
  const [fieldErrors, setFieldErrors] = useState({})

  const set = (k, v) => setForm(f => ({ ...f, [k]: v }))
  const setRecord = (i, k, v) => setForm(f => ({ ...f, records: f.records.map((r, j) => (j === i ? { ...r, [k]: v } : r)) }))
  const removeRecord = i => setForm(f => ({ ...f, records: f.records.filter((_, j) => j !== i) }))
  const addRecord = () => setForm(f => ({ ...f, records: [...f.records, { ...emptyRecord(), term: f.records.at(-1)?.term || 'Semester 3' }] }))

  async function submit(e) {
    e.preventDefault()
    setError(''); setFieldErrors({})
    const bad = form.records.findIndex(r => Number(r.marksObtained) > Number(r.maxMarks))
    if (bad >= 0) { setError(`Record ${bad + 1}: marks obtained cannot exceed maximum marks.`); return }
    setSaving(true)
    try {
      const body = {
        ...form,
        yearOfStudy: Number(form.yearOfStudy),
        records: form.records.map(r => ({ ...r, marksObtained: Number(r.marksObtained), maxMarks: Number(r.maxMarks) })),
      }
      if (student) await api.update(student.id, body); else await api.create(body)
      onSaved(student ? 'Student updated' : 'Student added')
    } catch (err) {
      setError(err.message); setFieldErrors(err.fieldErrors || {})
    } finally { setSaving(false) }
  }

  return (
    <Modal wide eyebrow={student ? 'UPDATE PROFILE' : 'STUDENT MANAGEMENT'} title={student ? 'Edit student' : 'Add new student'}
      subtitle="Enter student details and academic marks." onClose={onClose}>
      <form onSubmit={submit} noValidate={false}>
        {error && <div className="banner" role="alert">{error}</div>}
        <div className="form-grid">
          <Field label="Roll number *" error={fieldErrors.rollNumber}><input required maxLength="30" autoFocus value={form.rollNumber} onChange={e => set('rollNumber', e.target.value)} placeholder="CSE2026009" /></Field>
          <Field label="Full name *" error={fieldErrors.fullName}><input required value={form.fullName} onChange={e => set('fullName', e.target.value)} placeholder="Student name" /></Field>
          <Field label="Email address *" error={fieldErrors.email}><input required type="email" value={form.email} onChange={e => set('email', e.target.value)} placeholder="student@example.com" /></Field>
          <Field label="Phone" error={fieldErrors.phone}><input value={form.phone} onChange={e => set('phone', e.target.value)} placeholder="9876543210" /></Field>
          <Field label="Class / section *" error={fieldErrors.className}>
            <input required list="class-options" value={form.className} onChange={e => set('className', e.target.value)} placeholder="CSE-A" />
            <datalist id="class-options">{classes.map(c => <option key={c} value={c} />)}</datalist>
          </Field>
          <Field label="Department *" error={fieldErrors.department}><input required value={form.department} onChange={e => set('department', e.target.value)} placeholder="Computer Science" /></Field>
          <Field label="Year of study"><select value={form.yearOfStudy} onChange={e => set('yearOfStudy', e.target.value)}>{[1, 2, 3, 4, 5, 6, 7, 8].map(y => <option key={y} value={y}>{ordinal(y)} year</option>)}</select></Field>
          <Field label="Status"><select value={form.status} onChange={e => set('status', e.target.value)}><option value="ACTIVE">Active</option><option value="INACTIVE">Inactive</option><option value="GRADUATED">Graduated</option></select></Field>
        </div>

        <div className="section-label">ACADEMIC RECORDS <span>(average of all records = ranking score)</span></div>
        {form.records.map((r, i) => (
          <div className="record-row" key={i}>
            <Field label="Term" error={fieldErrors[`records[${i}].term`]}><input required value={r.term} onChange={e => setRecord(i, 'term', e.target.value)} /></Field>
            <Field label="Subject" error={fieldErrors[`records[${i}].subject`]}><input required value={r.subject} onChange={e => setRecord(i, 'subject', e.target.value)} /></Field>
            <Field label="Marks"><input type="number" min="0" step="0.5" required value={r.marksObtained} onChange={e => setRecord(i, 'marksObtained', e.target.value)} /></Field>
            <Field label="Out of"><input type="number" min="1" step="0.5" required value={r.maxMarks} onChange={e => setRecord(i, 'maxMarks', e.target.value)} /></Field>
            <button type="button" className="icon-btn" aria-label="Remove record" disabled={form.records.length === 1} onClick={() => removeRecord(i)}><X size={15} /></button>
          </div>
        ))}
        <button type="button" className="add-record" onClick={addRecord}><Plus size={15} /> Add subject record</button>

        <div className="modal-actions">
          <button type="button" className="btn btn-light" onClick={onClose}>Cancel</button>
          <button className="btn btn-primary" disabled={saving}>{saving ? 'Saving…' : student ? 'Save changes' : 'Create student'}</button>
        </div>
      </form>
    </Modal>
  )
}
