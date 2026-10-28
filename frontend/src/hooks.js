import { useCallback, useEffect, useRef, useState } from 'react'

/** Runs `fetcher` whenever `deps` change. Keeps the previous data on screen while reloading. */
export function useFetch(fetcher, deps = []) {
  const [state, setState] = useState({ data: null, loading: true, error: '' })
  const [tick, setTick] = useState(0)
  const fetcherRef = useRef(fetcher)
  fetcherRef.current = fetcher

  useEffect(() => {
    let cancelled = false
    setState(s => ({ ...s, loading: true, error: '' }))
    fetcherRef.current()
      .then(data => { if (!cancelled) setState({ data, loading: false, error: '' }) })
      .catch(e => { if (!cancelled) setState(s => ({ ...s, loading: false, error: e.message })) })
    return () => { cancelled = true }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [...deps, tick])

  const reload = useCallback(() => setTick(t => t + 1), [])
  return { ...state, reload }
}

export function useDebounce(value, delay = 300) {
  const [debounced, setDebounced] = useState(value)
  useEffect(() => {
    const id = setTimeout(() => setDebounced(value), delay)
    return () => clearTimeout(id)
  }, [value, delay])
  return debounced
}
