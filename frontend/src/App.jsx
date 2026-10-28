import React, { useCallback, useEffect, useState } from 'react'
import { ChartNoAxesCombined, Cpu, GraduationCap, LayoutDashboard, Plus, Trophy, Users } from 'lucide-react'
import { api } from './api'
import { useFetch } from './hooks'
import { ConfirmDialog, Toast } from './components/ui'
import StudentModal from './components/StudentModal'
import Dashboard from './pages/Dashboard'
import Students from './pages/Students'
import Rankings from './pages/Rankings'
import Analytics from './pages/Analytics'
import AlgorithmLab from './pages/AlgorithmLab'

const NAV = [
  ['Dashboard', LayoutDashboard], ['Students', Users], ['Rankings', Trophy],
  ['Analytics', ChartNoAxesCombined], ['Search & Sort', Cpu],
]
const pageFromHash = () => {
  const name = decodeURIComponent(window.location.hash.slice(1))
  return NAV.some(([n]) => n === name) ? name : 'Dashboard'
}

export default function App() {
  const [page, setPage] = useState(pageFromHash)
  const [version, setVersion] = useState(0)          // bumped after every change so pages refetch
  const [toast, setToast] = useState(null)
  const [editor, setEditor] = useState(null)          // null | { student: Student | null }
  const [deleting, setDeleting] = useState(null)
  const [busy, setBusy] = useState(false)
  const { data: classes } = useFetch(() => api.classes(), [version])

  const goTo = useCallback(name => { window.location.hash = encodeURIComponent(name); setPage(name) }, [])
  useEffect(() => {
    const onHash = () => setPage(pageFromHash())
    window.addEventListener('hashchange', onHash)
    return () => window.removeEventListener('hashchange', onHash)
  }, [])

  const notify = (message, type = 'success') => setToast({ message, type })
  const closeToast = useCallback(() => setToast(null), [])

  function saved(message) { setEditor(null); notify(message); setVersion(v => v + 1) }
  async function confirmDelete() {
    setBusy(true)
    try { await api.remove(deleting.id); notify('Student deleted'); setVersion(v => v + 1) }
    catch (e) { notify(e.message, 'error') }
    finally { setBusy(false); setDeleting(null) }
  }

  const classList = classes || []
  const props = { version, classes: classList }

  return (
    <div className="shell">
      <aside className="sidebar">
        <div className="brand"><div className="brand-mark"><GraduationCap size={23} /></div><div><b>CampusRank</b><small>STUDENT RANKING SYSTEM</small></div></div>
        <div className="nav-label">WORKSPACE</div>
        <nav aria-label="Main">
          {NAV.map(([name, Icon]) => (
            <button key={name} className={`nav-link ${page === name ? 'active' : ''}`} onClick={() => goTo(name)} aria-current={page === name ? 'page' : undefined}>
              <Icon size={18} /><span>{name}</span>
            </button>
          ))}
        </nav>
        <div className="side-note"><b>DSA concepts</b><span>Arrays · Sorting · Searching · Hash map</span></div>
      </aside>

      <main className="main">
        <header className="topbar">
          <div className="mobile-brand"><GraduationCap size={21} /> CampusRank</div>
          <div className="crumbs">Workspace <span>/</span> <b>{page}</b></div>
          <div className="top-actions">
            <button className="btn btn-primary" onClick={() => setEditor({ student: null })}><Plus size={16} /> <span className="hide-sm">Add student</span></button>
          </div>
        </header>

        <section className="page">
          {page === 'Dashboard' && <Dashboard {...props} goTo={goTo} />}
          {page === 'Students' && <Students {...props} onAdd={() => setEditor({ student: null })} onEdit={s => setEditor({ student: s })} onDelete={setDeleting} />}
          {page === 'Rankings' && <Rankings {...props} />}
          {page === 'Analytics' && <Analytics {...props} />}
          {page === 'Search & Sort' && <AlgorithmLab {...props} />}
        </section>
      </main>

      <nav className="bottom-nav" aria-label="Main (mobile)">
        {NAV.map(([name, Icon]) => <button key={name} className={page === name ? 'active' : ''} onClick={() => goTo(name)}><Icon size={19} /><span>{name.replace('Search & ', '')}</span></button>)}
      </nav>

      {editor && <StudentModal student={editor.student} classes={classList} onClose={() => setEditor(null)} onSaved={saved} />}
      {deleting && <ConfirmDialog title="Delete student?" message={`${deleting.fullName} and all their academic records will be permanently removed.`} onConfirm={confirmDelete} onCancel={() => setDeleting(null)} busy={busy} />}
      <Toast toast={toast} onClose={closeToast} />
    </div>
  )
}
