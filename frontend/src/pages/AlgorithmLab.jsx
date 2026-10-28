import React, { useState } from 'react'
import { Play, Search } from 'lucide-react'
import { api } from '../api'
import { pct } from '../utils'
import { Avatar, Card, Empty, ErrorBanner, GradeBadge, PageHeading } from '../components/ui'

const ALGORITHMS = [['merge', 'Merge'], ['quick', 'Quick'], ['heap', 'Heap'], ['insertion', 'Insertion']]
const FIELDS = [['marks', 'Marks (average %)'], ['name', 'Name'], ['roll', 'Roll number']]
const SEARCH_MODES = [
  ['hash', 'Hash map', 'Roll number', 'CSE2026001'],
  ['binary', 'Binary search', 'Roll number', 'CSE2026001'],
  ['linear', 'Linear search', 'Roll number', 'CSE2026001'],
  ['prefix', 'Name prefix', 'Name starts with', 'Aa'],
]

function Seg({ options, value, onChange, label }) {
  return (
    <div className="seg" role="group" aria-label={label}>
      {options.map(([v, text]) => <button key={v} type="button" className={value === v ? 'on' : ''} onClick={() => onChange(v)}>{text}</button>)}
    </div>
  )
}

function Metric({ label, value, hint }) {
  return <div className="metric"><span>{label}</span><b>{value}</b>{hint && <small>{hint}</small>}</div>
}

function SortLab({ classes }) {
  const [algorithm, setAlgorithm] = useState('merge')
  const [field, setField] = useState('marks')
  const [order, setOrder] = useState('')
  const [className, setClassName] = useState('')
  const [result, setResult] = useState(null)
  const [busy, setBusy] = useState(false)
  const [error, setError] = useState('')

  async function run() {
    setBusy(true); setError('')
    try {
      setResult(await api.dsaSort({ algorithm, field, order, className }))
    } catch (e) { setError(e.message) } finally { setBusy(false) }
  }

  const run1 = result?.run
  return (
    <Card title="Sort students" subtitle="Choose an algorithm; comparisons and swaps are shown" chip="SORTING">
      <ErrorBanner message={error} />
      <div className="lab-controls">
        <div><label>Algorithm</label><Seg label="Algorithm" options={ALGORITHMS} value={algorithm} onChange={setAlgorithm} /></div>
        <div><label>Sort by</label><select className="select" value={field} onChange={e => setField(e.target.value)}>{FIELDS.map(([v, t]) => <option key={v} value={v}>{t}</option>)}</select></div>
        <div><label>Order</label><select className="select" value={order} onChange={e => setOrder(e.target.value)}><option value="">Default</option><option value="asc">Ascending</option><option value="desc">Descending</option></select></div>
        <div><label>Class</label><select className="select" value={className} onChange={e => setClassName(e.target.value)}><option value="">All classes</option>{classes.map(c => <option key={c}>{c}</option>)}</select></div>
      </div>
      <div className="lab-actions">
        <button className="btn btn-primary" onClick={run} disabled={busy}><Play size={15} /> Run sort</button>
      </div>

      {run1 && (
        <>
          <div className="metrics">
            <Metric label="Algorithm" value={run1.label} hint={run1.stable ? 'Stable' : 'Not stable'} />
            <Metric label="Comparisons" value={run1.comparisons.toLocaleString()} hint={`n = ${run1.n}`} />
            <Metric label="Swaps / moves" value={run1.swaps.toLocaleString()} />
            <Metric label="Time" value={`${run1.elapsedMicros} µs`} hint={run1.timeComplexity} />
          </div>
          <div className="table-scroll lab-table">
            <table>
              <thead><tr><th>#</th><th>STUDENT</th><th>ROLL NUMBER</th><th>CLASS</th><th>AVERAGE</th><th>GRADE</th></tr></thead>
              <tbody>{result.students.slice(0, 15).map((s, i) => (
                <tr key={s.id}><td>{i + 1}</td><td><div className="person"><Avatar name={s.fullName} size={28} /><b>{s.fullName}</b></div></td><td className="mono">{s.rollNumber}</td><td>{s.className}</td><td>{s.records.length ? pct(s.averagePercentage) : '—'}</td><td><GradeBadge grade={s.overallGrade} /></td></tr>
              ))}</tbody>
            </table>
            {result.students.length > 15 && <p className="hint pad">Showing the first 15 of {result.students.length} sorted students.</p>}
          </div>
        </>
      )}

    </Card>
  )
}

function SearchLab() {
  const [mode, setMode] = useState('hash')
  const [query, setQuery] = useState('')
  const [result, setResult] = useState(null)
  const [error, setError] = useState('')
  const [busy, setBusy] = useState(false)
  const current = SEARCH_MODES.find(m => m[0] === mode)

  async function submit(e) {
    e.preventDefault()
    if (!query.trim()) return
    setBusy(true); setError('')
    try { setResult(await api.dsaSearch({ mode, q: query.trim() })) }
    catch (err) { setError(err.message) } finally { setBusy(false) }
  }

  return (
    <Card title="Search students" subtitle="Find a student by roll number or name prefix" chip="SEARCHING">
      <ErrorBanner message={error} />
      <Seg label="Search mode" options={SEARCH_MODES.map(([v, t]) => [v, t])} value={mode} onChange={m => { setMode(m); setResult(null) }} />
      <form className="search-form" onSubmit={submit}>
        <div className="search"><Search size={17} /><input value={query} onChange={e => setQuery(e.target.value)} placeholder={`${current[2]}, e.g. ${current[3]}`} aria-label="Search query" /></div>
        <button className="btn btn-primary" disabled={busy || !query.trim()}>Search</button>
      </form>

      {result && (
        <>
          <div className="metrics three">
            <Metric label="Method" value={result.label} />
            <Metric label="Steps (comparisons)" value={result.comparisons.toLocaleString()} hint={`array size n = ${result.n}`} />
            <Metric label="Complexity" value={result.complexity} hint={`${result.elapsedMicros} µs`} />
          </div>
          {result.matches.length ? result.matches.map(s => (
            <div className="match" key={s.id}>
              <Avatar name={s.fullName} size={38} />
              <div className="who"><b>{s.fullName}</b><span>{s.rollNumber} · {s.className} · {s.department}</span></div>
              <div className="score"><b>{s.records.length ? pct(s.averagePercentage) : '—'}</b><GradeBadge grade={s.overallGrade} /></div>
            </div>
          )) : <Empty text={`No student found for "${result.query}".`} />}
        </>
      )}
    </Card>
  )
}

export default function AlgorithmLab({ classes }) {
  return (
    <>
      <PageHeading eyebrow="DSA" title="Search & sort" subtitle="Sort students by marks, name or roll number and search records using our own algorithms." />
      <div className="stack">
        <SortLab classes={classes} />
        <SearchLab />
      </div>
    </>
  )
}
