import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import Navbar from '../components/Navbar';
import { User, Mail, Save, AlertCircle, CheckCircle, ChevronRight, BarChart2, Award, Settings, LogOut } from 'lucide-react';
import { getApiUrl } from '../config';

export default function Profile() {
  const navigate = useNavigate();
  const userId = localStorage.getItem('USER_ID');
  const userName = localStorage.getItem('USER_NAME') || 'User';
  const email = localStorage.getItem('USER_EMAIL') || 'user@example.com';
  const initial = userName.charAt(0).toUpperCase();
  const isPremium = localStorage.getItem('IS_PREMIUM') === 'true';

  const [stats, setStats] = useState({ totalSessions: 0, avgScore: 0 });
  const [loading, setLoading] = useState(false);
  
  const [isEditing, setIsEditing] = useState(false);
  const [tempName, setTempName] = useState(userName);
  const [tempAccent, setTempAccent] = useState(localStorage.getItem('ACCENT_PREF') || 'Indian');

  const accentOptions = [
    { key: 'Indian', label: '🇮🇳 Indian English' },
    { key: 'American', label: '🇺🇸 American English' },
    { key: 'British', label: '🇬🇧 British English' }
  ];

  useEffect(() => {
    if (!userId) {
      navigate('/login');
    } else {
      fetchStats();
    }
  }, [userId, navigate]);

  const handleSave = () => {
    localStorage.setItem('USER_NAME', tempName);
    localStorage.setItem('ACCENT_PREF', tempAccent);
    setIsEditing(false);
    window.location.reload(); // Refresh to update all references
  };

  const handleMenuClick = (title) => {
    if (title === 'Settings') {
      navigate('/settings');
    } else if (title === 'Progress Stats' || title === 'Achievements') {
      if (isPremium) {
        alert(title + " feature is coming soon to the web!");
      } else {
        navigate('/premium');
      }
    }
  };

  const fetchStats = async () => {
    try {
      const res = await axios.get(getApiUrl(`sessions/${userId}`));
      if (res.data.status === 'success') {
        const sessions = res.data.sessions;
        const total = res.data.total_sessions;
        const avg = sessions.length > 0 ? Math.round(sessions.reduce((acc, s) => acc + s.score, 0) / sessions.length) : 0;
        setStats({ totalSessions: total, avgScore: avg });
      }
    } catch (err) {
      console.error("Error fetching stats:", err);
    }
  };

  const menuItems = [
    { title: 'Progress Stats', desc: 'Detailed analytics and trends', icon: BarChart2, color: '#06B6D4' },
    { title: 'Achievements', desc: 'Badges and milestones', icon: Award, color: '#A855F7' },
    { title: 'Settings', desc: 'App preferences and notifications', icon: Settings, color: '#06B6D4' },
  ];

  return (
    <div style={{ display: 'flex', minHeight: '100vh', background: '#0F0B1F' }}>
      <Navbar />
      
      <main style={{ flex: 1, marginLeft: '260px', padding: '40px 60px', maxWidth: '800px', margin: '0 auto' }}>
        <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center' }}>
          
          {/* Avatar Area */}
          <div style={{ 
            width: '100px', 
            height: '100px', 
            borderRadius: '50%', 
            background: 'linear-gradient(135deg, #06B6D4, #0891B2)',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            color: 'white',
            fontSize: '40px',
            fontWeight: 'bold',
            marginBottom: '16px',
            boxShadow: '0 12px 24px rgba(6, 182, 212, 0.2)'
          }}>
            {initial}
          </div>

          <h1 style={{ color: 'white', fontSize: '24px', fontWeight: '800', marginBottom: '8px' }}>{userName}</h1>
          <p style={{ color: '#9CA3AF', fontSize: '16px', marginBottom: '16px' }}>{email}</p>
          
          <button 
            onClick={() => setIsEditing(true)}
            style={{ 
              background: 'transparent', 
              border: 'none', 
              color: '#06B6D4', 
              fontSize: '14px', 
              fontWeight: 'bold', 
              display: 'flex', 
              alignItems: 'center', 
              gap: '8px',
              cursor: 'pointer'
            }}
          >
            <User size={16} /> Edit Profile
          </button>

          {/* Stats Triple Card */}
          <div className="glass-card" style={{ width: '100%', marginTop: '32px', padding: '24px', display: 'grid', gridTemplateColumns: 'repeat(3, 1fr)', gap: '1px', background: 'rgba(255,255,255,0.05)' }}>
            <div style={{ textAlign: 'center' }}>
              <div style={{ fontSize: '24px', fontWeight: '800', color: 'white' }}>{stats.totalSessions}</div>
              <div style={{ fontSize: '12px', color: '#9CA3AF', textTransform: 'uppercase', marginTop: '4px' }}>Sessions</div>
            </div>
            <div style={{ textAlign: 'center', borderLeft: '1px solid rgba(255,255,255,0.1)', borderRight: '1px solid rgba(255,255,255,0.1)' }}>
              <div style={{ fontSize: '24px', fontWeight: '800', color: '#06B6D4' }}>{stats.avgScore}</div>
              <div style={{ fontSize: '12px', color: '#9CA3AF', textTransform: 'uppercase', marginTop: '4px' }}>Avg Score</div>
            </div>
            <div style={{ textAlign: 'center' }}>
              <div style={{ fontSize: '24px', fontWeight: '800', color: '#10B981' }}>+12%</div>
              <div style={{ fontSize: '12px', color: '#9CA3AF', textTransform: 'uppercase', marginTop: '4px' }}>Improved</div>
            </div>
          </div>

          {/* Menu Items */}
          <div style={{ width: '100%', marginTop: '24px', display: 'flex', flexDirection: 'column', gap: '16px' }}>
            {menuItems.map((item, i) => (
              <div 
                key={i} 
                className="glass-card" 
                style={{ padding: '16px 20px', display: 'flex', alignItems: 'center', justifyContent: 'space-between', cursor: 'pointer' }}
                onClick={() => handleMenuClick(item.title)}
              >
                <div style={{ display: 'flex', alignItems: 'center', gap: '16px' }}>
                  <div style={{ background: `${item.color}15`, padding: '10px', borderRadius: '12px' }}>
                    <item.icon size={20} color={item.color} />
                  </div>
                  <div>
                    <div style={{ color: 'white', fontWeight: 'bold', fontSize: '15px' }}>{item.title}</div>
                    <div style={{ color: '#9CA3AF', fontSize: '13px' }}>{item.desc}</div>
                  </div>
                </div>
                <ChevronRight size={18} color="#6B7280" />
              </div>
            ))}
          </div>

          {/* Premium Banner */}
          {!isPremium && (
            <div className="glass-card" style={{ width: '100%', marginTop: '32px', padding: '24px', background: 'rgba(236, 72, 153, 0.05)', border: '1px solid rgba(236, 72, 153, 0.2)', textAlign: 'center' }}>
              <h3 style={{ color: 'white', fontSize: '18px', fontWeight: '800', marginBottom: '8px' }}>Free Plan</h3>
              <p style={{ color: '#9CA3AF', fontSize: '14px', marginBottom: '20px' }}>Upgrade to unlock advanced analytics and expert training units.</p>
              <button 
                onClick={() => navigate('/premium')}
                style={{ 
                  background: 'transparent', 
                  border: 'none', 
                  color: '#EC4899', 
                  fontWeight: 'bold', 
                  fontSize: '15px', 
                  display: 'flex', 
                  alignItems: 'center', 
                  gap: '4px',
                  margin: '0 auto',
                  cursor: 'pointer'
                }}
              >
                SEE PREMIUM PLANS <ChevronRight size={18} />
              </button>
            </div>
          )}

          {/* Logout */}
          <button 
            onClick={() => { localStorage.clear(); navigate('/login'); }}
            style={{ 
              width: '100%', 
              marginTop: '16px', 
              marginBottom: '48px',
              padding: '16px 20px', 
              background: 'transparent', 
              border: '1px solid rgba(239, 68, 68, 0.2)', 
              borderRadius: '16px',
              display: 'flex', 
              alignItems: 'center', 
              gap: '16px',
              cursor: 'pointer'
            }}
          >
            <div style={{ background: 'rgba(239, 68, 68, 0.1)', padding: '10px', borderRadius: '12px' }}>
              <LogOut size={20} color="#EF4444" />
            </div>
            <div style={{ textAlign: 'left' }}>
              <div style={{ color: '#EF4444', fontWeight: 'bold', fontSize: '15px' }}>Log Out</div>
              <div style={{ color: '#9CA3AF', fontSize: '13px' }}>Sign out of your account</div>
            </div>
          </button>

        </div>

        {/* Edit Profile Modal */}
        {isEditing && (
          <div style={{ 
            position: 'fixed', 
            top: 0, left: 0, right: 0, bottom: 0, 
            background: 'rgba(0,0,0,0.8)', 
            display: 'flex', 
            alignItems: 'center', 
            justifyContent: 'center',
            zIndex: 1000,
            backdropFilter: 'blur(8px)'
          }}>
            <div className="glass-card animate-slide-up" style={{ width: '100%', maxWidth: '450px', padding: '40px' }}>
              <h2 style={{ fontSize: '24px', color: 'white', marginBottom: '8px' }}>Edit Profile</h2>
              <p style={{ color: '#9CA3AF', fontSize: '14px', marginBottom: '32px' }}>Update your personal details and accent preference.</p>
              
              <div style={{ marginBottom: '24px' }}>
                <label style={{ display: 'block', color: 'white', fontSize: '14px', marginBottom: '8px' }}>Full Name</label>
                <input 
                  type="text" 
                  value={tempName} 
                  onChange={(e) => setTempName(e.target.value)}
                  style={{ width: '100%', padding: '14px', borderRadius: '12px', background: 'rgba(255,255,255,0.05)', border: '1px solid rgba(255,255,255,0.1)', color: 'white', fontSize: '16px' }} 
                />
              </div>

              <div style={{ marginBottom: '32px' }}>
                <label style={{ display: 'block', color: 'white', fontSize: '14px', marginBottom: '8px' }}>Accent Preference</label>
                <select 
                  value={tempAccent} 
                  onChange={(e) => setTempAccent(e.target.value)}
                  style={{ width: '100%', padding: '14px', borderRadius: '12px', background: 'rgba(255,255,255,0.05)', border: '1px solid rgba(255,255,255,0.1)', color: 'white', fontSize: '16px', appearance: 'none' }}
                >
                  {accentOptions.map(opt => (
                    <option key={opt.key} value={opt.key}>{opt.label}</option>
                  ))}
                </select>
              </div>

              <div style={{ display: 'flex', gap: '16px' }}>
                <button 
                  onClick={handleSave}
                  className="btn-primary" 
                  style={{ flex: 1, height: '52px' }}
                >
                  Save Changes
                </button>
                <button 
                  onClick={() => setIsEditing(false)}
                  style={{ flex: 1, height: '52px', background: 'transparent', border: '1px solid rgba(255,255,255,0.1)', color: 'white', borderRadius: '12px', cursor: 'pointer', fontWeight: 'bold' }}
                >
                  Cancel
                </button>
              </div>
            </div>
          </div>
        )}
      </main>
    </div>
  );
}
