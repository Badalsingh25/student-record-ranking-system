import React from 'react'
import { Bar, BarChart, CartesianGrid, Cell, ResponsiveContainer, Tooltip, XAxis, YAxis } from 'recharts'
import { ArrowUpRight, BookOpen, ChartNoAxesCombined, CircleCheck, Medal, RefreshCw, Users } from 'lucide-react'
import { api } from '../api'
import { useFetch } from '../hooks'
import { GRADE_COLORS, GRADIENTS, pct } from '../utils'
import { Avatar, Card, Empty, ErrorBanner, GradeBadge, Skeleton } from '../components/ui'

export default function Dashboard({ version, goTo }) {
  const { data: d, loading, error, reload } = useFetch(() => api.dashboard(), [version])
  const stats = [
    { label: 'Total students', value: d?.totalStudents, note: 'Registered profiles', icon: Users, tint: 'purple' },
    { label: 'Active students', value: d?.activeStudents, note: 'Currently enrolled', icon: CircleCheck, tint: 'green' },
    { label: 'Class sections', value: d?.classCount, note: 'Across departments', icon: BookOpen, tint: 'orange' },
    { label: 'Average score', value: d && pct(d.averagePercentage), note: `${d?.academicRecords ?? 0} marks recorded`, icon: ChartNoAxesCombined, tint: 'blue' },
  ]
  const grades = Object.entries(d?.gradeDistribution || {}).map(([grade, count]) => ({ grade, count }))
  const hasGrades = grades.some(g => g.count > 0)

  return (
    <>
      <section className="hero">
        <div className="hero-text">
          <span className="hero-tag">ACADEMIC OVERVIEW</span>
          <h1>Student Record &amp; Ranking System</h1>
          <p>Track student performance, grades and rankings in one place.</p>
          <button className="btn btn-glass" onClick={reload}><RefreshCw size={15} /> Refresh data</button>
        </div>
        <div className="hero-stats">
          <div><b>{d ? d.totalStudents : '—'}</b><span>Students</span></div>
          <div><b>{d ? pct(d.averagePercentage) : '—'}</b><span>Average score</span></div>
          <div><b>{d?.topStudents?.[0]?.fullName?.split(' ')[0] || '—'}</b><span>Top performer</span></div>
        </div>
      </section>
      <ErrorBanner message={error} onRetry={reload} />

      <div className="stats">
        {stats.map(s => (
          <div className={`card stat ${s.tint}`} key={s.label}>
            <div className="stat-top"><span>{s.label}</span><div className={`stat-icon ${s.tint}`}><s.icon size={19} /></div></div>
            <div className="stat-value">{s.value ?? '—'}</div>
            <div className="stat-note">{s.note}</div>
          </div>
        ))}
      </div>

      <div className="grid-2">
        <Card title="Grade distribution" subtitle="Students per overall grade" chip="OVERVIEW">
          {hasGrades ? (
            <div className="chart" style={{ height: 250 }}>
              <ResponsiveContainer width="100%" height="100%">
                <BarChart data={grades} margin={{ top: 10, right: 5, left: -22, bottom: 0 }}>
                  <defs>
                    {grades.map(g => (
                      <linearGradient key={g.grade} id={`grade-${g.grade.replace('+', 'p')}`} x1="0" y1="0" x2="0" y2="1">
                        <stop offset="0%" stopColor={GRADE_COLORS[g.grade]} stopOpacity={1} />
                        <stop offset="100%" stopColor={GRADE_COLORS[g.grade]} stopOpacity={0.45} />
                      </linearGradient>
                    ))}
                  </defs>
                  <CartesianGrid strokeDasharray="3 3" vertical={false} stroke="var(--line)" />
                  <XAxis dataKey="grade" tickLine={false} axisLine={false} tick={{ fill: 'var(--muted)', fontSize: 12 }} />
                  <YAxis allowDecimals={false} tickLine={false} axisLine={false} tick={{ fill: 'var(--muted)', fontSize: 11 }} />
                  <Tooltip cursor={{ fill: 'var(--hover)' }} contentStyle={{ background: 'var(--card)', border: '1px solid var(--line)', borderRadius: 12, fontSize: 12 }} />
                  <Bar dataKey="count" name="Students" radius={[6, 6, 0, 0]} maxBarSize={38}>
                    {grades.map(g => <Cell key={g.grade} fill={`url(#grade-${g.grade.replace('+', 'p')})`} />)}
                  </Bar>
                </BarChart>
              </ResponsiveContainer>
            </div>
          ) : loading ? <Skeleton rows={4} /> : <Empty text="Add academic records to see the grade distribution." />}
        </Card>

        <Card title="Top performers" subtitle="Selected with a min-heap (top-K)" action={<button className="link" onClick={() => goTo('Rankings')}>Full ranking <ArrowUpRight size={14} /></button>}>
          {d?.topStudents?.length ? d.topStudents.map(s => (
            <div className="top-row" key={s.id}>
              <div className={`medal m${s.rank}`}>{s.rank === 1 ? <Medal size={16} /> : s.rank}</div>
              <Avatar name={s.fullName} />
              <div className="who"><b>{s.fullName}</b><span>{s.rollNumber} · {s.className}</span></div>
              <div className="score"><b>{pct(s.averagePercentage)}</b><GradeBadge grade={s.overallGrade} /></div>
            </div>
          )) : loading ? <Skeleton rows={5} /> : <Empty text="Rankings will appear once students have marks." />}
        </Card>
      </div>

      <Card title="Subject performance" subtitle="Average percentage per subject" chip="SUBJECT INSIGHTS" className="mt">
        {d?.subjectAverages?.length ? (
          <div className="bars">
            {d.subjectAverages.map((s, i) => (
              <div className="bar-row" key={s.subject}>
                <span>{s.subject}</span>
                <div className="track"><i style={{ width: `${Math.min(100, s.averagePercentage)}%`, background: `linear-gradient(90deg, ${GRADIENTS[i % GRADIENTS.length][0]}, ${GRADIENTS[i % GRADIENTS.length][1]})` }} /></div>
                <b>{pct(s.averagePercentage)}</b>
              </div>
            ))}
          </div>
        ) : loading ? <Skeleton rows={3} /> : <Empty text="No subject records available." />}
      </Card>
    </>
  )
}
