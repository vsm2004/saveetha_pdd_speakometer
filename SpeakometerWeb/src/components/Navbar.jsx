import React from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import { Home, History, Brain, User, LogOut, Crown } from 'lucide-react';

export default function Navbar() {
  const navigate = useNavigate();
  const location = useLocation();
  const isPremium = localStorage.getItem('IS_PREMIUM') === 'true';
  
  const navItems = [
    { id: 'dashboard', label: 'Home', icon: Home, path: '/dashboard' },
    { id: 'history', label: 'History', icon: History, path: '/history' },
    { id: 'practice', label: 'Practice', icon: Brain, path: '/practice' },
    { id: 'profile', label: 'Profile', icon: User, path: '/profile' },
  ];

  const logout = () => {
    localStorage.clear();
    navigate('/login');
  };

  return (
    <nav className="glass-card" style={{
      width: '260px',
      height: '100vh',
      position: 'fixed',
      left: 0,
      top: 0,
      borderRadius: 0,
      borderLeft: 'none',
      borderTop: 'none',
      borderBottom: 'none',
      display: 'flex',
      flexDirection: 'column',
      padding: '32px 16px',
      zIndex: 100,
      background: '#121017' // Match Android Bottom Nav exactly
    }}>
      <div style={{ marginBottom: '48px', padding: '0 16px', display: 'flex', alignItems: 'center', gap: '12px' }}>
        <div style={{ 
          background: 'linear-gradient(135deg, #A855F7, #EC4899)', 
          width: '32px', 
          height: '32px', 
          borderRadius: '8px',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center'
        }}>
          <Brain size={20} color="white" />
        </div>
        <h1 style={{ 
          fontSize: '20px', 
          fontWeight: 'bold',
          color: 'white',
          letterSpacing: '0.5px'
        }}>
          Speakometer
        </h1>
      </div>

      <div style={{ display: 'flex', flexDirection: 'column', gap: '4px', flex: 1 }}>
        {navItems.map((item) => {
          const isActive = location.pathname === item.path;
          const Icon = item.icon;
          
          return (
            <button
              key={item.id}
              onClick={() => navigate(item.path)}
              style={{
                display: 'flex',
                alignItems: 'center',
                gap: '12px',
                padding: '14px 16px',
                borderRadius: '12px',
                border: 'none',
                background: isActive ? 'rgba(6, 182, 212, 0.1)' : 'transparent',
                color: isActive ? '#06B6D4' : '#9CA3AF', // Cyan for active, Grey for inactive
                cursor: 'pointer',
                transition: 'all 0.2s ease',
                textAlign: 'left',
                fontWeight: isActive ? '700' : '500',
                fontSize: '15px'
              }}
            >
              <Icon size={20} strokeWidth={isActive ? 2.5 : 2} />
              {item.label}
            </button>
          );
        })}
        
        {!isPremium && (
          <button
            onClick={() => navigate('/premium')}
            style={{
              display: 'flex',
              alignItems: 'center',
              gap: '12px',
              padding: '14px 16px',
              borderRadius: '12px',
              border: 'none',
              background: 'linear-gradient(135deg, rgba(236, 72, 153, 0.1), rgba(168, 85, 247, 0.1))',
              color: '#EC4899',
              cursor: 'pointer',
              marginTop: '8px',
              fontWeight: '700',
              fontSize: '15px'
            }}
          >
            <Crown size={20} />
            Go Premium
          </button>
        )}
      </div>

      <button
        onClick={logout}
        style={{
          display: 'flex',
          alignItems: 'center',
          gap: '12px',
          padding: '14px 16px',
          borderRadius: '12px',
          border: 'none',
          background: 'transparent',
          color: '#ef4444',
          cursor: 'pointer',
          marginTop: 'auto',
          fontSize: '15px',
          fontWeight: '600'
        }}
      >
        <LogOut size={20} />
        Logout
      </button>
    </nav>
  );
}
