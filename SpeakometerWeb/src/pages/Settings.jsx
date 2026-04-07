import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import Navbar from '../components/Navbar';
import { ArrowLeft, Bell, Moon, Sun, Shield, LogOut, ChevronRight } from 'lucide-react';

export default function Settings() {
  const navigate = useNavigate();
  const [notifications, setNotifications] = useState(true);
  const [darkMode, setDarkMode] = useState(true);

  const handleLogout = () => {
    localStorage.clear();
    navigate('/login');
  };

  return (
    <div style={{ display: 'flex', minHeight: '100vh', background: '#0F0B1F' }}>
      <Navbar />
      
      <main style={{ flex: 1, marginLeft: '260px', padding: '40px 60px', maxWidth: '800px', margin: '0 auto' }}>
        <header style={{ display: 'flex', alignItems: 'center', gap: '16px', marginBottom: '40px' }}>
          <button 
            onClick={() => navigate('/profile')} 
            style={{ 
              background: 'var(--glass-bg)', 
              border: '1px solid var(--glass-border)', 
              borderRadius: '50%', 
              width: '40px', 
              height: '40px', 
              display: 'flex', 
              alignItems: 'center', 
              justifyContent: 'center', 
              color: 'white', 
              cursor: 'pointer' 
            }}
          >
            <ArrowLeft size={20} />
          </button>
          <div>
            <h1 style={{ fontSize: '24px', color: 'white', fontWeight: '800' }}>Settings</h1>
            <p style={{ color: '#9CA3AF', fontSize: '14px' }}>Customize your app experience</p>
          </div>
        </header>

        <div style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
          
          {/* Notifications */}
          <div className="glass-card" style={{ padding: '20px 24px', display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
            <div style={{ display: 'flex', alignItems: 'center', gap: '16px' }}>
              <div style={{ background: '#A855F715', padding: '10px', borderRadius: '12px' }}>
                <Bell size={20} color="#A855F7" />
              </div>
              <div>
                <div style={{ color: 'white', fontWeight: 'bold' }}>Notifications</div>
                <div style={{ color: '#9CA3AF', fontSize: '13px' }}>Manage alerts and reminders</div>
              </div>
            </div>
            <div 
              onClick={() => setNotifications(!notifications)}
              style={{ 
                width: '44px', height: '24px', borderRadius: '12px', 
                background: notifications ? '#A855F7' : '#374151', 
                position: 'relative', cursor: 'pointer', transition: 'background 0.3s' 
              }}
            >
              <div style={{ 
                width: '18px', height: '18px', borderRadius: '50%', background: 'white', 
                position: 'absolute', top: '3px', left: notifications ? '23px' : '3px', 
                transition: 'left 0.3s' 
              }}></div>
            </div>
          </div>

          {/* Dark Mode */}
          <div className="glass-card" style={{ padding: '20px 24px', display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
            <div style={{ display: 'flex', alignItems: 'center', gap: '16px' }}>
              <div style={{ background: '#06B6D415', padding: '10px', borderRadius: '12px' }}>
                {darkMode ? <Moon size={20} color="#06B6D4" /> : <Sun size={20} color="#06B6D4" />}
              </div>
              <div>
                <div style={{ color: 'white', fontWeight: 'bold' }}>Dark Mode</div>
                <div style={{ color: '#9CA3AF', fontSize: '13px' }}>Switch between dark and light themes</div>
              </div>
            </div>
            <div 
              onClick={() => setDarkMode(!darkMode)}
              style={{ 
                width: '44px', height: '24px', borderRadius: '12px', 
                background: darkMode ? '#06B6D4' : '#374151', 
                position: 'relative', cursor: 'pointer', transition: 'background 0.3s' 
              }}
            >
              <div style={{ 
                width: '18px', height: '18px', borderRadius: '50%', background: 'white', 
                position: 'absolute', top: '3px', left: darkMode ? '23px' : '3px', 
                transition: 'left 0.3s' 
              }}></div>
            </div>
          </div>

          {/* Privacy Policy */}
          <div className="glass-card" style={{ padding: '20px 24px', display: 'flex', alignItems: 'center', justifyContent: 'space-between', cursor: 'pointer' }}>
            <div style={{ display: 'flex', alignItems: 'center', gap: '16px' }}>
              <div style={{ background: '#10B98115', padding: '10px', borderRadius: '12px' }}>
                <Shield size={20} color="#10B981" />
              </div>
              <div>
                <div style={{ color: 'white', fontWeight: 'bold' }}>Privacy Policy</div>
                <div style={{ color: '#9CA3AF', fontSize: '13px' }}>How we handle your data</div>
              </div>
            </div>
            <ChevronRight size={20} color="#4B5563" />
          </div>

          {/* Logout */}
          <div 
            onClick={handleLogout}
            className="glass-card" 
            style={{ 
              padding: '20px 24px', display: 'flex', alignItems: 'center', justifyContent: 'space-between', cursor: 'pointer',
              border: '1px solid rgba(239, 68, 68, 0.2)'
            }}
          >
            <div style={{ display: 'flex', alignItems: 'center', gap: '16px' }}>
              <div style={{ background: 'rgba(239, 68, 68, 0.1)', padding: '10px', borderRadius: '12px' }}>
                <LogOut size={20} color="#EF4444" />
              </div>
              <div>
                <div style={{ color: '#EF4444', fontWeight: 'bold' }}>Log Out</div>
                <div style={{ color: '#9CA3AF', fontSize: '13px' }}>Sign out of your account</div>
              </div>
            </div>
          </div>

        </div>

        <div style={{ textAlign: 'center', marginTop: '60px', opacity: 0.5 }}>
          <p style={{ color: '#9CA3AF', fontSize: '12px' }}>Speakometer v1.2.0 • Made with ❤️</p>
        </div>
      </main>
    </div>
  );
}
