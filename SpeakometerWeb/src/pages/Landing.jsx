import React, { useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { Mic, Brain, Sparkles, Shield, Zap, ChevronRight } from 'lucide-react';

export default function Landing() {
  const navigate = useNavigate();

  useEffect(() => {
    const userId = localStorage.getItem('USER_ID');
    if (userId) {
      navigate('/dashboard');
    }
  }, [navigate]);

  return (
    <div style={{ background: 'var(--bg-primary)', minHeight: '100vh', color: 'white', overflowX: 'hidden' }}>
      {/* Hero Section */}
      <section style={{ 
        padding: '120px 24px 80px 24px', 
        textAlign: 'center', 
        maxWidth: '1200px', 
        margin: '0 auto',
        position: 'relative'
      }}>
        <div style={{ 
          position: 'absolute', 
          top: '0', 
          left: '50%', 
          transform: 'translateX(-50%)', 
          width: '600px', 
          height: '600px', 
          background: 'radial-gradient(circle, rgba(168, 85, 247, 0.15) 0%, rgba(15, 11, 31, 0) 70%)',
          zIndex: 0
        }}></div>

        <div className="animate-slide-up" style={{ position: 'relative', zIndex: 1 }}>
          <div style={{ 
            background: 'linear-gradient(135deg, var(--accent-primary), #6366F1)', 
            width: '64px', 
            height: '64px', 
            borderRadius: '16px', 
            display: 'flex', 
            alignItems: 'center', 
            justifyContent: 'center',
            margin: '0 auto 32px auto',
            boxShadow: '0 0 30px rgba(168, 85, 247, 0.4)'
          }}>
            <Mic size={32} color="white" />
          </div>
          <h1 style={{ fontSize: '64px', fontWeight: '800', marginBottom: '24px', lineHeight: '1.1', background: 'linear-gradient(to bottom, #FFFFFF, #9CA3AF)', WebkitBackgroundClip: 'text', WebkitTextFillColor: 'transparent' }}>
            Speak with Confidence.<br />Master the Art of Voice.
          </h1>
          <p style={{ fontSize: '20px', color: 'var(--text-secondary)', maxWidth: '600px', margin: '0 auto 48px auto', lineHeight: '1.6' }}>
            Get real-time AI feedback on your pace, filler words, and vocal tonality. Join thousands of speakers improving every day.
          </p>
          
          <div style={{ display: 'flex', gap: '16px', justifyContent: 'center' }}>
            <button 
              onClick={() => navigate('/signup')}
              className="btn-primary" 
              style={{ width: 'auto', padding: '16px 32px', display: 'flex', alignItems: 'center', gap: '8px' }}
            >
              Get Started for Free <ChevronRight size={20} />
            </button>
            <button 
              onClick={() => navigate('/login')}
              style={{ 
                background: 'rgba(255,255,255,0.05)', 
                border: '1px solid var(--glass-border)', 
                color: 'white', 
                padding: '16px 32px', 
                borderRadius: '12px', 
                fontWeight: '600', 
                cursor: 'pointer',
                transition: 'background 0.2s'
              }}
              onMouseOver={(e) => e.currentTarget.style.background = 'rgba(255,255,255,0.1)'}
              onMouseOut={(e) => e.currentTarget.style.background = 'rgba(255,255,255,0.05)'}
            >
              Sign In
            </button>
          </div>
        </div>
      </section>

      {/* Features Section */}
      <section style={{ padding: '80px 24px', maxWidth: '1200px', margin: '0 auto' }}>
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(300px, 1fr))', gap: '24px' }}>
          <div className="glass-card" style={{ padding: '32px' }}>
            <div style={{ color: 'var(--accent-primary)', marginBottom: '16px' }}><Sparkles size={32} /></div>
            <h3 style={{ fontSize: '20px', marginBottom: '12px' }}>AI-Powered Analysis</h3>
            <p style={{ color: 'var(--text-secondary)', lineHeight: '1.6' }}>Get near-instant feedback on your speech patterns with our state-of-the-art AI model.</p>
          </div>
          <div className="glass-card" style={{ padding: '32px' }}>
            <div style={{ color: 'var(--accent-secondary)', marginBottom: '16px' }}><Zap size={32} /></div>
            <h3 style={{ fontSize: '20px', marginBottom: '12px' }}>Personalized Practice</h3>
            <p style={{ color: 'var(--text-secondary)', lineHeight: '1.6' }}>Custom drills designed to target your specific weaknesses, from filler words to pacing.</p>
          </div>
          <div className="glass-card" style={{ padding: '32px' }}>
            <div style={{ color: '#F59E0B', marginBottom: '16px' }}><Shield size={32} /></div>
            <h3 style={{ fontSize: '20px', marginBottom: '12px' }}>Secure & Private</h3>
            <p style={{ color: 'var(--text-secondary)', lineHeight: '1.6' }}>Your recordings are your own. We use enterprise-grade encryption to keep your data safe.</p>
          </div>
        </div>
      </section>

      {/* Footer */}
      <footer style={{ padding: '64px 24px', textAlign: 'center', borderTop: '1px solid var(--glass-border)', color: 'var(--text-secondary)', fontSize: '14px' }}>
        <div style={{ marginBottom: '16px', display: 'flex', alignItems: 'center', justifyContent: 'center', gap: '8px' }}>
          <Brain size={18} /> <strong>Speakometer</strong>
        </div>
        &copy; 2026 Speakometer. All rights reserved.
      </footer>
    </div>
  );
}
