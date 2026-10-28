import React from 'react'
import { Bar, BarChart, CartesianGrid, Cell, Pie, PieChart, ResponsiveContainer, Tooltip, XAxis, YAxis } from 'recharts'
import { ChartNoAxesCombined } from 'lucide-react'
import { api } from '../api'
import { useFetch } from '../hooks'
import { GRADE_COLORS, pct } from '../utils'
import { Card, Empty, ErrorBanner, PageHeading } from '../components/ui'

const tooltipStyle = { background: 'var(--card)', border: '1px solid var(--line)', borderRadius: 12, fontSize: 12 }
const tick = { fill: 'var(--muted)', fontSize: 11 }

export default function Analytics({ version }) {
  const dash = useFetch(() => api.dashboard(), [version])
  const d = dash.data

  const gradeData = Object.entries(d?.gradeDistribution || {}).map(([name, value]) => ({ name, value })).filter(x => x.value > 0)
  const subjects = d?.subjectAverages || []
  const best = [...subjects].sort((a, b) => b.averagePercentage - a.averagePercentage)[0]

  return (
    <>
      <PageHeading eyebrow="INSIGHTS" title="Analytics" subtitle="Grade distribution and subject averages." />
      <ErrorBanner message={dash.error} onRetry={dash.reload} />
      <div className="grid-2">
        <Card title="Grade composition" subtitle="Share of students per grade">
          {gradeData.length ? (
            <>
              <div className="donut">
                <ResponsiveContainer width="100%" height={250}>
                  <PieChart><Pie data={gradeData} dataKey="value" nameKey="name" innerRadius={66} outerRadius={98} paddingAngle={3} stroke="none">
                    {gradeData.map(g => <Cell key={g.name} fill={GRADE_COLORS[g.name]} />)}
                  </Pie><Tooltip contentStyle={tooltipStyle} /></PieChart>
                </ResponsiveContainer>
                <div className="donut-center"><b>{gradeData.reduce((n, g) => n + g.value, 0)}</b><span>Graded</span></div>
              </div>
              <div className="legend">{gradeData.map(g => <div key={g.name}><span><i style={{ background: GRADE_COLORS[g.name] }} />{g.name}</span><b>{g.value}</b></div>)}</div>
            </>
          ) : <Empty text="No grade data yet." />}
        </Card>

        <Card title="Subject averages" subtitle="Average percentage per subject">
          {subjects.length ? (
            <div className="chart" style={{ height: 270 }}>
              <ResponsiveContainer width="100%" height="100%">
                <BarChart data={subjects} layout="vertical" margin={{ left: 8, right: 18, top: 10, bottom: 5 }}>
                  <defs>
                    <linearGradient id="subject-grad" x1="0" y1="0" x2="1" y2="0">
                      <stop offset="0%" stopColor="#7c6cf6" /><stop offset="100%" stopColor="#14b8a6" />
                    </linearGradient>
                  </defs>
                  <CartesianGrid strokeDasharray="3 3" horizontal={false} stroke="var(--line)" />
                  <XAxis type="number" domain={[0, 100]} tickLine={false} axisLine={false} tick={tick} />
                  <YAxis type="category" dataKey="subject" width={115} tickLine={false} axisLine={false} tick={tick} />
                  <Tooltip formatter={v => pct(v)} contentStyle={tooltipStyle} cursor={{ fill: 'var(--hover)' }} />
                  <Bar dataKey="averagePercentage" name="Average" fill="url(#subject-grad)" radius={[0, 6, 6, 0]} maxBarSize={24} />
                </BarChart>
              </ResponsiveContainer>
            </div>
          ) : <Empty text="No subject data available." />}
          {best && <div className="insight"><ChartNoAxesCombined size={19} /><div><b>Insight</b><p>{best.subject} has the highest average ({pct(best.averagePercentage)}).</p></div></div>}
        </Card>

      </div>
    </>
  )
}
