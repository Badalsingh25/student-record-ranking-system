import React, { useState } from 'react'
import { Medal, Trophy } from 'lucide-react'
import { api } from '../api'
import { useFetch } from '../hooks'
import { pct } from '../utils'
import { Avatar, Card, Empty, ErrorBanner, GradeBadge, PageHeading, Skeleton } from '../components/ui'

export default function Rankings({ version, classes }) {
  const [mode, setMode] = useState('top')          // "top" = min-heap, "full" = merge sort
  const [className, setClassName] = useState('')
  const { data, loading, error, reload } = useFetch(
    () => (mode === 'top' ? api.top(10, className) : api.rankings(className)), [mode, className, version])
  const rows = data || []

  return (
    <>
      <PageHeading eyebrow="LEADERBOARD" title="Academic rankings" subtitle="Ranked by average percentage. Ties share the same rank (1, 1, 3)." />
      <ErrorBanner message={error} onRetry={reload} />

            <Card className="flush">
        <div className="toolbar">
          <div>
            <h3>{mode === 'top' ? 'Top 10 performers' : 'Full leaderboard'}</h3>
            <p>{mode === 'top' ? 'Found with a bounded min-heap in O(n log k)' : 'Sorted with merge sort in O(n log n)'}</p>
          </div>
          <div className="seg" role="tablist">
            <button role="tab" aria-selected={mode === 'top'} className={mode === 'top' ? 'on' : ''} onClick={() => setMode('top')}>Top 10</button>
            <button role="tab" aria-selected={mode === 'full'} className={mode === 'full' ? 'on' : ''} onClick={() => setMode('full')}>Everyone</button>
          </div>
        </div>
        <div className="filters">
          <div className="search"><Trophy size={16} /><span className="hint">Inactive students and students without marks are not ranked</span></div>
          <select className="select" value={className} onChange={e => setClassName(e.target.value)} aria-label="Filter by class">
            <option value="">All classes</option>{classes.map(c => <option key={c}>{c}</option>)}
          </select>
        </div>
        <div className="table-scroll">
          <table>
            <thead><tr><th>RANK</th><th>STUDENT</th><th>CLASS</th><th>SUBJECTS</th><th>AVERAGE</th><th>GRADE</th></tr></thead>
            <tbody>
              {rows.map(s => (
                <tr key={s.id} className={s.rank <= 3 ? `hl hl${s.rank}` : ''}>
                  <td><div className={`rank ${s.rank <= 3 ? `top r${s.rank}` : ''}`}>{s.rank <= 3 ? <Medal size={17} /> : s.rank}</div></td>
                  <td><div className="person"><Avatar name={s.fullName} size={31} /><div><b>{s.fullName}</b><span>{s.rollNumber}</span></div></div></td>
                  <td>{s.className}</td>
                  <td>{s.records.length}</td>
                  <td><div className="score-cell"><b>{pct(s.averagePercentage)}</b><div className="track mini"><i style={{ width: `${Math.min(100, s.averagePercentage)}%` }} /></div></div></td>
                  <td><GradeBadge grade={s.overallGrade} /></td>
                </tr>
              ))}
              {!rows.length && <tr><td colSpan={6}>{loading ? <Skeleton /> : <Empty text="No ranking data available." />}</td></tr>}
            </tbody>
          </table>
        </div>
      </Card>
    </>
  )
}
