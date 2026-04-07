import React from 'react';
import { useNavigate } from 'react-router-dom';
import Navbar from '../components/Navbar';
import { Brain, Star, Clock, Trophy, Lock, ChevronRight, PlayCircle, CheckCircle } from 'lucide-react';

export default function Practice() {
  const navigate = useNavigate();
  const isPremium = localStorage.getItem('IS_PREMIUM') === 'true';
  const suggestedTopic = localStorage.getItem('SUGGESTED_TOPIC');
  
  const exercises = [
    { title: 'Filler Words', desc: 'Reduce "um", "ah", and other crutch words.', icon: PlayCircle, color: '#A855F7', free: true },
    { title: 'Hesitation', desc: 'Work on confidence and fewer mid-sentence pauses.', icon: Trophy, color: '#A855F7', free: true },
    { title: 'Pace Variation', desc: 'Master the speed and rhythm of your speech.', icon: Clock, color: '#A855F7', free: true },
    { title: 'Art & Storytelling', desc: 'Learn to engage listeners with narrative flow.', icon: Brain, color: '#9CA3AF', free: false },
    { title: 'Presentation Skills', desc: 'Perfect your formal and public speaking tone.', icon: Brain, color: '#9CA3AF', free: false },
  ];

  return (
    <div style={{ display: 'flex', minHeight: '100vh', background: '#0F0B1F' }}>
      <Navbar />
      
      <main style={{ flex: 1, marginLeft: '260px', padding: '40px 60px', maxWidth: '1000px' }}>
        <header style={{ marginBottom: '48px' }}>
          <h1 style={{ fontSize: '32px', color: 'white', marginBottom: '8px', fontWeight: '800' }}>Practice Zone</h1>
          <p style={{ color: '#9CA3AF', fontSize: '18px' }}>Master your communication skills with guided drills.</p>
        </header>

        <div style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
          {exercises.map((ex, i) => {
            const locked = !ex.free && !isPremium;
            const completed = localStorage.getItem(`COMPLETED_PRACTICE_${ex.title}`) === 'true';
            const isRecommended = ex.title === suggestedTopic;

            return (
              <div 
                key={i} 
                className={`glass-card animate-slide-up ${isRecommended ? 'recommended-pulse' : ''}`}
                style={{ 
                  padding: '20px 24px', 
                  display: 'flex', 
                  alignItems: 'center', 
                  justifyContent: 'space-between',
                  opacity: locked ? 0.6 : 1,
                  cursor: locked ? 'default' : 'pointer',
                  border: isRecommended ? '1px solid var(--accent-primary)' : (locked ? '1px solid rgba(255,255,255,0.05)' : '1px solid rgba(255,255,255,0.1)'),
                  animationDelay: `${i * 0.05}s`,
                  position: 'relative',
                  overflow: 'visible'
                }}
                onClick={() => !locked && navigate(`/practice/session?topic=${encodeURIComponent(ex.title)}`)}
              >
                {isRecommended && (
                  <div style={{ 
                    position: 'absolute', 
                    top: '-10px', 
                    right: '20px', 
                    background: 'var(--accent-primary)', 
                    color: 'white', 
                    fontSize: '11px', 
                    fontWeight: 'bold', 
                    padding: '2px 8px', 
                    borderRadius: '4px',
                    textTransform: 'uppercase'
                  }}>
                    Recommended
                  </div>
                )}

                <div style={{ display: 'flex', alignItems: 'center', gap: '20px' }}>
                  <div style={{ 
                    background: locked ? '#374151' : `${ex.color}20`, 
                    width: '56px', 
                    height: '56px', 
                    borderRadius: '16px', 
                    display: 'flex', 
                    alignItems: 'center', 
                    justifyContent: 'center'
                  }}>
                    {locked ? <Lock size={24} color="#9CA3AF" /> : (completed ? <CheckCircle size={24} color="#10B981" /> : <ex.icon size={24} color={ex.color} />)}
                  </div>
                  <div>
                    <h3 style={{ color: 'white', fontSize: '18px', marginBottom: '4px', fontWeight: '700' }}>{ex.title}</h3>
                    <p style={{ color: '#9CA3AF', fontSize: '14px' }}>{ex.desc}</p>
                  </div>
                </div>
                {!locked && <ChevronRight size={20} color="#6B7280" />}
              </div>
            );
          })}
        </div>

        {!isPremium && (
          <div className="glass-card animate-slide-up" style={{ marginTop: '48px', padding: '32px', textAlign: 'center', background: 'rgba(236, 72, 153, 0.05)', border: '1px solid rgba(236, 72, 153, 0.2)' }}>
            <div style={{ background: 'rgba(236, 72, 153, 0.1)', width: '48px', height: '48px', borderRadius: '50%', display: 'flex', alignItems: 'center', justifyCenter: 'center', margin: '0 auto 16px auto', display: 'flex', justifyContent: 'center', alignItems: 'center' }}>
               <Lock size={24} color="#EC4899" />
            </div>
            <h3 style={{ color: 'white', fontSize: '22px', marginBottom: '8px', fontWeight: '800' }}>Unlock All Exercises</h3>
            <p style={{ color: '#9CA3AF', marginBottom: '24px', fontSize: '15px' }}>Get access to advanced storytelling, professional presentation skills, and more with Premium.</p>
            <button 
              onClick={() => navigate('/premium')}
              style={{ background: 'transparent', border: 'none', color: '#EC4899', fontWeight: 'bold', fontSize: '16px', cursor: 'pointer', display: 'flex', alignItems: 'center', gap: '8px', margin: '0 auto' }}
            >
              UPGRADE NOW <ChevronRight size={18} />
            </button>
          </div>
        )}
      </main>
    </div>
  );
}
