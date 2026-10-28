import React, { useState } from 'react'
import { ChevronDown, ChevronLeft, ChevronRight, ChevronUp, Filter, Pencil, Plus, Search, Trash2 } from 'lucide-react'
import { api } from '../api'
import { useDebounce, useFetch } from '../hooks'
import { ordinal, pct } from '../utils'
import { Avatar, Card, Empty, ErrorBanner, GradeBadge, PageHeading, Skeleton, StatusBadge } from '../components/ui'

const PAGE_SIZE = 10
const COLUMNS = [
  ['fullName', 'STUDENT'], ['rollNumber', 'ROLL NUMBER'], ['className', 'CLASS / DEPT.'], ['yearOfStudy', 'YEAR'],
  [null, 'AVERAGE'], [null, 'GRADE'], ['status', 'STATUS'],
]

export default function Students({ version, classes, onAdd, onEdit, onDelete }) {
  const [search, setSearch] = useState('')
  const [className, setClassName] = useState('')
  const [status, setStatus] = useState('')
  const [sort, setSort] = useState({ by: 'rollNumber', dir: 'asc' })
  const [page, setPage] = useState(0)
  const q = useDebounce(search)

  const { data, loading, error, reload } = useFetch(
    () => api.students({ page, size: PAGE_SIZE, search: q, className, status, sortBy: sort.by, direction: sort.dir }),
    [page, q, className, status, sort, version])

  const rows = data?.content || []
  const total = data?.totalElements || 0
  const pages = Math.max(1, data?.totalPages || 1)
  const reset = fn => v => { fn(v); setPage(0) }
  const toggleSort = by => { setSort(s => ({ by, dir: s.by === by && s.dir === 'asc' ? 'desc' : 'asc' })); setPage(0) }

  return (
    <>
      <PageHeading eyebrow="STUDENT MANAGEMENT" title="Student records" subtitle="Add, edit, delete, search and filter student records.">
        <button className="btn btn-primary" onClick={onAdd}><Plus size={17} /> Add student</button>
      </PageHeading>
      <ErrorBanner message={error} onRetry={reload} />

      <Card className="flush">
        <div className="toolbar">
          <div><h3>All students <span className="count">{total}</span></h3><p>Search by name, roll number or email</p></div>
        </div>
        <div className="filters">
          <div className="search"><Search size={17} /><input value={search} onChange={e => reset(setSearch)(e.target.value)} placeholder="Search students…" aria-label="Search students" /></div>
          <div className="select-wrap"><Filter size={15} />
            <select value={className} onChange={e => reset(setClassName)(e.target.value)} aria-label="Filter by class">
              <option value="">All classes</option>{classes.map(c => <option key={c}>{c}</option>)}
            </select>
          </div>
          <select className="select" value={status} onChange={e => reset(setStatus)(e.target.value)} aria-label="Filter by status">
            <option value="">All statuses</option><option value="ACTIVE">Active</option><option value="INACTIVE">Inactive</option><option value="GRADUATED">Graduated</option>
          </select>
        </div>

        <div className="table-scroll">
          <table>
            <thead><tr>
              {COLUMNS.map(([key, label]) => (
                <th key={label}>
                  {key ? <button className="th-sort" onClick={() => toggleSort(key)}>{label}{sort.by === key && (sort.dir === 'asc' ? <ChevronUp size={12} /> : <ChevronDown size={12} />)}</button> : label}
                </th>
              ))}
              <th>ACTIONS</th>
            </tr></thead>
            <tbody>
              {rows.map(s => (
                <tr key={s.id}>
                    <td><div className="person"><Avatar name={s.fullName} size={31} /><div><b>{s.fullName}</b><span>{s.email}</span></div></div></td>
                    <td className="mono">{s.rollNumber}</td>
                    <td><b className="strong">{s.className}</b><span className="sub">{s.department}</span></td>
                    <td>{ordinal(s.yearOfStudy)}</td>
                    <td><b className="strong">{s.records.length ? pct(s.averagePercentage) : '—'}</b></td>
                    <td><GradeBadge grade={s.overallGrade} /></td>
                    <td><StatusBadge status={s.status} /></td>
                    <td>
                      <div className="row-actions">
                        <button title="Edit student" aria-label="Edit student" onClick={() => onEdit(s)}><Pencil size={15} /></button>
                        <button title="Delete student" aria-label="Delete student" className="danger" onClick={() => onDelete(s)}><Trash2 size={15} /></button>
                      </div>
                    </td>
                  </tr>
              ))}
              {!rows.length && <tr><td colSpan={8}>{loading ? <Skeleton /> : <Empty text="No students match your filters." />}</td></tr>}
            </tbody>
          </table>
        </div>

        <div className="pagination">
          <span>Showing <b>{total ? page * PAGE_SIZE + 1 : 0}–{Math.min((page + 1) * PAGE_SIZE, total)}</b> of <b>{total}</b></span>
          <div>
            <button disabled={page <= 0} onClick={() => setPage(p => p - 1)} aria-label="Previous page"><ChevronLeft size={16} /></button>
            <span>Page {page + 1} of {pages}</span>
            <button disabled={page + 1 >= pages} onClick={() => setPage(p => p + 1)} aria-label="Next page"><ChevronRight size={16} /></button>
          </div>
        </div>
      </Card>
    </>
  )
}
