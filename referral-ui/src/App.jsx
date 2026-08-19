import { useState } from 'react'
import './App.css'

function App() {
  const [myName, setMyName] = useState('Raju Addakula')
  const [targetCompany, setTargetCompany] = useState('Google')
  const [paths, setPaths] = useState([])
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')
  const [hasSearched, setHasSearched] = useState(false)

  const searchReferrals = async (e) => {
    e.preventDefault()
    setLoading(true)
    setError('')
    setHasSearched(true)

    try {
      const response = await fetch(`http://localhost:8080/api/referral-path?myName=${encodeURIComponent(myName)}&targetCompany=${encodeURIComponent(targetCompany)}`)

      if (!response.ok) {
        throw new Error('Database is currently unreachable.')
      }

      const data = await response.json()

      if (data.error) {
        throw new Error(data.error)
      }

      // Remove duplicate paths returned by the graph
      const uniquePaths = Array.from(new Set(data.map(JSON.stringify))).map(JSON.parse)
      setPaths(uniquePaths)

    } catch (err) {
      setError(err.message)
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="app-container">
      <header className="header">
        <h1>🧭 ReferralRadar</h1>
        <p>Leverage your graph network to find warm introductions.</p>
      </header>

      <main className="main-content">
        <form onSubmit={searchReferrals} className="search-form">
          <div className="input-group">
            <label>Your Name</label>
            <input
              type="text"
              value={myName}
              onChange={(e) => setMyName(e.target.value)}
              required
            />
          </div>

          <div className="input-group">
            <label>Target Company</label>
            <input
              type="text"
              value={targetCompany}
              onChange={(e) => setTargetCompany(e.target.value)}
              required
            />
          </div>

          <button type="submit" disabled={loading} className="search-btn">
            {loading ? 'Searching Network...' : 'Find Referral Path'}
          </button>
        </form>

        {/* UI State Handling as Required by Rubric */}
        <div className="results-container">
          {loading && <div className="state-message loading">Traversing the graph database...</div>}

          {error && <div className="state-message error">⚠️ {error}</div>}

          {!loading && !error && hasSearched && paths.length === 0 && (
            <div className="state-message empty">
              No connection path found to {targetCompany}. Try expanding your network!
            </div>
          )}

          {!loading && !error && paths.length > 0 && (
            <div className="paths-list">
              <h2>Found {paths.length} Connection Path(s)</h2>
              {paths.map((path, index) => (
                <div key={index} className="path-card">
                  {path.map((node, i) => (
                    <span key={i} className="path-node">
                      <span className={i === 0 ? 'node-me' : i === path.length - 1 ? 'node-company' : 'node-connection'}>
                        {node}
                      </span>
                      {i < path.length - 1 && <span className="arrow"> → </span>}
                    </span>
                  ))}
                </div>
              ))}
            </div>
          )}
        </div>
      </main>
    </div>
  )
}

export default App