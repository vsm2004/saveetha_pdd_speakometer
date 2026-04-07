import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import { ArrowLeft, Clock, Activity, TrendingUp } from 'lucide-react';

export default function History() {
  const navigate = useNavigate();
  const userId = localStorage.getItem('USER_ID');
  const isPremium = localStorage.getItem('IS_PREMIUM') === 'true' || localStorage.getItem('IS_PREMIUM') === '1';
  
  const [sessions, setSessions] = useState([]);
  const [totalSessions, setTotalSessions] = useState(0);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (!userId) {
      navigate('/login');
      return;
    }


    const fetchHistory = async () => {
      try {
        const response = await axios.get(`http://localhost:8000/api/sessions/${userId}`);
        const data = response.data;
        if (data.status === 'success') {
          setSessions(data.sessions || []);
          setTotalSessions(data.total_sessions || 0);
        }
      } catch (err) {
        console.error('Error fetching history:', err);
      } finally {
        setLoading(false);
      }
    };
    fetchHistory();
  }, [userId, navigate, isPremium]);

  const calculateImprovement = () => {
    if (sessions.length < 2) return 0;
    const avg = sessions.reduce((acc, s) => acc + s.score, 0) / sessions.length;
    return Math.round(sessions[0].score - avg);
  };

  const avgScore = sessions.length ? Math.round(sessions.reduce((acc, s) => acc + s.score, 0) / sessions.length) : 0;
  const improvement = calculateImprovement();

  return (
    <div style={{ padding: '24px', maxWidth: '800px', margin: '0 auto' }}>
      <header style={{ display: 'flex', alignItems: 'center', gap: '16px', marginBottom: '40px' }}>
        <button onClick={() => navigate('/dashboard')} style={{ background: 'var(--glass-bg)', border: '1px solid var(--glass-border)', borderRadius: '50%', width: '40px', height: '40px', display: 'flex', alignItems: 'center', justifyContent: 'center', color: 'white', cursor: 'pointer' }}>
          <ArrowLeft size={20} />
        </button>
        <h1 style={{ fontSize: '24px' }}>Performance History</h1>
      </header>
      
      {loading ? (
        <div style={{ display: 'flex', justifyContent: 'center', padding: '60px' }}>
           <div className="spinner"></div>
        </div>
      ) : (
        <>
          {/* Summary Cards */}
          <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))', gap: '16px', marginBottom: '32px' }}>
            <div className="glass-card animate-slide-up" style={{ display: 'flex', flexDirection: 'column', gap: '8px' }}>
              <div style={{ display: 'flex', alignItems: 'center', gap: '8px', color: 'var(--text-secondary)' }}>
                <Clock size={16} /> <span style={{ fontSize: '14px' }}>Total Sessions</span>
              </div>
              <div style={{ fontSize: '32px', fontWeight: 'bold' }}>{totalSessions}</div>
            </div>
            <div className="glass-card animate-slide-up" style={{ display: 'flex', flexDirection: 'column', gap: '8px', animationDelay: '0.1s' }}>
              <div style={{ display: 'flex', alignItems: 'center', gap: '8px', color: 'var(--text-secondary)' }}>
                <Activity size={16} /> <span style={{ fontSize: '14px' }}>Avg Score</span>
              </div>
              <div style={{ fontSize: '32px', fontWeight: 'bold' }}>{avgScore}</div>
            </div>
            <div className="glass-card animate-slide-up" style={{ display: 'flex', flexDirection: 'column', gap: '8px', animationDelay: '0.2s' }}>
              <div style={{ display: 'flex', alignItems: 'center', gap: '8px', color: 'var(--text-secondary)' }}>
                <TrendingUp size={16} /> <span style={{ fontSize: '14px' }}>Improved</span>
              </div>
              <div style={{ fontSize: '32px', fontWeight: 'bold', color: improvement >= 0 ? '#10B981' : '#EF4444' }}>
                {improvement >= 0 ? `+${improvement}` : improvement}
              </div>
            </div>
          </div>

          {/* Session List */}
          <h2 style={{ fontSize: '18px', marginBottom: '16px' }}>Past Recordings</h2>
          <div style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
            {sessions.map((session, i) => (
              <div key={session.id || i} className="glass-card animate-slide-up" style={{ animationDelay: `${0.1 * i}s`, display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                <div>
                  <div style={{ fontWeight: '600', marginBottom: '4px' }}>{session.created_at ? new Date(session.created_at).toLocaleDateString('en-US', { month: 'short', day: 'numeric', year: 'numeric' }) : 'Recent Session'}</div>
                  <div style={{ fontSize: '14px', color: 'var(--text-secondary)' }}>{session.fillers_count} Fillers • {session.stretching_level} Pace</div>
                </div>
                <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'flex-end' }}>
                  <div style={{ fontSize: '24px', fontWeight: 'bold', color: 'var(--accent-secondary)' }}>{session.score}</div>
                  <div style={{ fontSize: '12px', color: 'var(--text-secondary)' }}>Score</div>
                </div>
              </div>
            ))}
            {sessions.length === 0 && (
              <div style={{ textAlign: 'center', color: 'var(--text-secondary)', padding: '40px' }}>
                No recordings found. Open the dashboard to start!
              </div>
            )}
          </div>
        </>
      )}
    </div>
  );
}
