export const pct = v => `${Number(v || 0).toFixed(1)}%`

export const initials = name =>
  (name || '?').split(' ').filter(Boolean).slice(0, 2).map(s => s[0]).join('').toUpperCase()

export const ordinal = n => `${n}${n === 1 ? 'st' : n === 2 ? 'nd' : n === 3 ? 'rd' : 'th'}`

export const GRADE_COLORS = {
  'A+': '#16a37a', A: '#3fbf8f', B: '#4f86f0', C: '#f0ad3e', D: '#ee8447', F: '#e5546b', 'N/A': '#9aa4b6',
}
// [from, to] pairs used for gradient bars and avatars
export const GRADIENTS = [
  ['#7c6cf6', '#b08cff'], ['#14b8a6', '#5eead4'], ['#f59e0b', '#fcd34d'],
  ['#ec4899', '#f9a8d4'], ['#3b82f6', '#93c5fd'], ['#8b5cf6', '#f472b6'],
]
export const CHART_COLORS = ['#7c6cf6', '#22b8a7', '#f4ad4f', '#ef6d86', '#5f93f7', '#9aa7ba']

export const emptyRecord = () => ({ term: 'Semester 3', subject: '', marksObtained: 0, maxMarks: 100 })

export const emptyStudent = () => ({
  rollNumber: '', fullName: '', email: '', phone: '', className: '', department: '',
  yearOfStudy: 2, status: 'ACTIVE', records: [{ ...emptyRecord(), subject: 'Data Structures' }],
})
