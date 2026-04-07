import React, { useState, useEffect, useRef } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import { Mic, Upload, Square, Activity, FileCheck, Brain, TrendingUp, Calendar, ChevronRight } from 'lucide-react';
import Navbar from '../components/Navbar';
import { getApiUrl } from '../config';

export default function Dashboard() {
  const navigate = useNavigate();
  const userId = localStorage.getItem('USER_ID');
  const userName = localStorage.getItem('USER_NAME') || 'User';
  const initial = userName.charAt(0).toUpperCase();
  
  const [recording, setRecording] = useState(false);
  const [analyzing, setAnalyzing] = useState(false);
  const [results, setResults] = useState(null);
  const [stats, setStats] = useState({ totalSessions: 0, avgScore: 0, lastScore: 0 });
  const [suggestedTopic, setSuggestedTopic] = useState('');
  
  const mediaRecorderRef = useRef(null);
  const audioChunksRef = useRef([]);

  useEffect(() => {
    if (!userId) {
      navigate('/login');
    } else {
      fetchStats();
    }
  }, [userId, navigate]);

  const fetchStats = async () => {
    try {
      const res = await axios.get(getApiUrl(`sessions/${userId}`));
      if (res.data.status === 'success') {
        const sessions = res.data.sessions;
        const total = res.data.total_sessions;
        const avg = sessions.length > 0 ? Math.round(sessions.reduce((acc, s) => acc + s.score, 0) / sessions.length) : 0;
        const last = sessions.length > 0 ? sessions[0].score : 0;
        setStats({ totalSessions: total, avgScore: avg, lastScore: last });
        
        if (sessions.length > 0) {
           const latest = sessions[0];
           let topic = 'Hesitation';
           if (latest.fillers_count > 3) topic = 'Filler Words';
           else if (latest.score < 60) topic = 'Pace Variation';
           
           setSuggestedTopic(topic);
           localStorage.setItem('SUGGESTED_TOPIC', topic);
        }
      }
    } catch (err) {
      console.error("Error fetching stats:", err);
    }
  };

  const getGreeting = () => {
    const hour = new Date().getHours();
    if (hour < 12) return "Good Morning";
    if (hour < 17) return "Good Afternoon";
    if (hour < 21) return "Good Evening";
    return "Good Night";
  };

  const startRecording = async () => {
    try {
      setResults(null);
      const stream = await navigator.mediaDevices.getUserMedia({ audio: true });
      mediaRecorderRef.current = new MediaRecorder(stream);
      audioChunksRef.current = [];
      
      mediaRecorderRef.current.ondataavailable = (event) => {
        if (event.data.size > 0) {
          audioChunksRef.current.push(event.data);
        }
      };

      mediaRecorderRef.current.onstop = async () => {
        const audioBlob = new Blob(audioChunksRef.current, { type: 'audio/mp3' });
        await processAudioFile(audioBlob, 'web_recording.mp3');
        stream.getTracks().forEach(track => track.stop());
      };

      mediaRecorderRef.current.start();
      setRecording(true);
    } catch (err) {
      alert("Microphone access denied or unavailable.");
    }
  };

  const stopRecording = () => {
    if (mediaRecorderRef.current) {
      mediaRecorderRef.current.stop();
      setRecording(false);
    }
  };

  const handleFileUpload = async (e) => {
    const file = e.target.files[0];
    if (file) {
      setResults(null);
      await processAudioFile(file, file.name);
    }
  };

  const processAudioFile = async (blob, filename) => {
    setAnalyzing(true);
    const formData = new FormData();
    formData.append("file", blob, filename);

    try {
      const res = await axios.post(getApiUrl("analyze"), formData, {
        headers: { "Content-Type": "multipart/form-data" },
        timeout: 60000 // 60 seconds timeout for AI analysis
      });
      const data = res.data;

      if (data.status === 'success') {
         const finalScore = Math.round((data.accent_score + data.confidence_score) / 2);
         setResults({ ...data, finalScore });

         const sessionPayload = {
           user_id: parseInt(userId, 10),
           score: finalScore,
           fillers_count: data.filler_count || 0,
           stretching_level: data.wpm < 110 ? "high" : "none",
           confidence: data.confidence_score || 0
         };
         await axios.post(getApiUrl("save-session"), sessionPayload);
         fetchStats();
      } else {
         alert("Analysis Failed: " + data.message);
      }
    } catch (err) {
      console.error("AI Analysis Error Details:", err);
      let errorDetail = "Error contacting the AI analysis engine.";
      
      if (err.code === 'ECONNABORTED') {
          errorDetail = "Analysis timed out. Try a shorter recording.";
      } else if (err.response) {
          errorDetail = `Server Error (${err.response.status}): ${err.response.data.detail || 'Internal Error'}`;
      } else if (err.request) {
          errorDetail = "Connection Refused. Please ensure the Python backend is running and the Dev Tunnel is active.";
      }
      
      alert(errorDetail);
    } finally {
      setAnalyzing(false);
    }
  };

  return (
    <div style={{ display: 'flex', minHeight: '100vh', background: '#0F0B1F' }}>
      <Navbar />
      
      <main style={{ flex: 1, marginLeft: '260px', padding: '40px 60px', maxWidth: '1200px' }}>
        <header style={{ marginBottom: '40px', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
          <div>
            <h1 style={{ fontSize: '32px', color: 'white', marginBottom: '8px', fontWeight: '800' }}>
              {getGreeting()}, {userName.split(' ')[0]}!
            </h1>
            <p style={{ color: '#9CA3AF', fontSize: '18px' }}>Ready to conquer your goals today?</p>
          </div>
          
          <div style={{ 
            width: '56px', 
            height: '56px', 
            borderRadius: '50%', 
            background: 'linear-gradient(135deg, #06B6D4, #0891B2)',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            color: 'white',
            fontSize: '24px',
            fontWeight: 'bold',
            boxShadow: '0 8px 16px rgba(6, 182, 212, 0.2)'
          }}>
            {initial}
          </div>
        </header>

        {!results && !analyzing && (
          <div className="animate-slide-up">
            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '24px', marginBottom: '32px' }}>
              <div className="glass-card" style={{ padding: '32px', position: 'relative' }}>
                <div style={{ display: 'flex', alignItems: 'flex-start', justifyContent: 'space-between', marginBottom: '24px' }}>
                  <div style={{ background: '#06B6D415', padding: '16px', borderRadius: '16px' }}>
                    <Mic size={32} color="#06B6D4" />
                  </div>
                  {recording && <div style={{ color: '#EF4444', display: 'flex', alignItems: 'center', gap: '6px', fontSize: '14px', fontWeight: 'bold' }}>
                    <div style={{ width: '8px', height: '8px', borderRadius: '50%', background: '#EF4444' }} className="pulse"></div>
                    REC
                  </div>}
                </div>
                <h3 style={{ color: 'white', fontSize: '24px', marginBottom: '8px', fontWeight: '700' }}>Live Recording</h3>
                <p style={{ color: '#9CA3AF', marginBottom: '32px', fontSize: '15px' }}>Start speaking directly to analyze your tone and pace in real-time.</p>
                <button 
                  onClick={recording ? stopRecording : startRecording} 
                  className="btn-primary"
                  style={{ background: recording ? '#EF4444' : '#06B6D4', width: '100%', height: '54px' }}
                >
                  {recording ? 'Stop Recording' : 'Start Session'}
                </button>
              </div>

              <div className="glass-card" style={{ padding: '32px', position: 'relative' }}>
                <input type="file" accept="audio/*" onChange={handleFileUpload} style={{ position: 'absolute', inset: 0, opacity: 0, cursor: 'pointer', zIndex: 10 }} disabled={recording} />
                <div style={{ display: 'flex', alignItems: 'flex-start', marginBottom: '24px' }}>
                  <div style={{ background: '#A855F715', padding: '16px', borderRadius: '16px' }}>
                    <Upload size={32} color="#A855F7" />
                  </div>
                </div>
                <h3 style={{ color: 'white', fontSize: '24px', marginBottom: '8px', fontWeight: '700' }}>Upload Audio</h3>
                <p style={{ color: '#9CA3AF', marginBottom: '32px', fontSize: '15px' }}>Upload an existing recording to get a full AI-powered drill down report.</p>
                <button className="btn-primary" style={{ background: '#A855F7', width: '100%', height: '54px' }}>
                  Browse Files
                </button>
              </div>
            </div>

            {suggestedTopic && (
              <div className="glass-card" style={{ padding: '24px 32px', display: 'flex', alignItems: 'center', justifyContent: 'space-between', background: 'rgba(6, 182, 212, 0.05)', border: '1px solid rgba(6, 182, 212, 0.1)' }}>
                <div style={{ display: 'flex', alignItems: 'center', gap: '20px' }}>
                  <div style={{ background: '#06B6D4', padding: '12px', borderRadius: '12px' }}>
                    <Brain size={24} color="white" />
                  </div>
                  <div>
                    <h4 style={{ color: 'white', fontSize: '18px', marginBottom: '4px' }}>Recommended for you</h4>
                    <p style={{ color: '#06B6D4', fontSize: '14px', fontWeight: '600' }}>Practice: {suggestedTopic}</p>
                  </div>
                </div>
                <button onClick={() => navigate('/practice')} style={{ background: 'transparent', border: 'none', color: '#06B6D4', cursor: 'pointer', display: 'flex', alignItems: 'center', gap: '4px', fontWeight: 'bold' }}>
                  Open Practice <ChevronRight size={20} />
                </button>
              </div>
            )}
            
            <div style={{ display: 'grid', gridTemplateColumns: 'repeat(3, 1fr)', gap: '24px', marginTop: '32px' }}>
               <div className="glass-card" style={{ padding: '24px', textAlign: 'center' }}>
                  <div style={{ color: '#9CA3AF', marginBottom: '8px', fontSize: '13px', fontWeight: 'bold', textTransform: 'uppercase' }}>Sessions</div>
                  <div style={{ fontSize: '32px', fontWeight: '800', color: 'white' }}>{stats.totalSessions}</div>
               </div>
               <div className="glass-card" style={{ padding: '24px', textAlign: 'center' }}>
                  <div style={{ color: '#9CA3AF', marginBottom: '8px', fontSize: '13px', fontWeight: 'bold', textTransform: 'uppercase' }}>Avg Score</div>
                  <div style={{ fontSize: '32px', fontWeight: '800', color: '#06B6D4' }}>{stats.avgScore}</div>
               </div>
               <div className="glass-card" style={{ padding: '24px', textAlign: 'center' }}>
                  <div style={{ color: '#9CA3AF', marginBottom: '8px', fontSize: '13px', fontWeight: 'bold', textTransform: 'uppercase' }}>Improved</div>
                  <div style={{ fontSize: '32px', fontWeight: '800', color: (stats.avgScore > 0 && stats.lastScore > stats.avgScore) ? '#10B981' : (stats.avgScore > 0 && stats.lastScore < stats.avgScore ? '#EF4444' : '#F59E0B') }}>
                    {stats.avgScore > 0 ? (stats.lastScore > stats.avgScore ? `+${Math.round((stats.lastScore - stats.avgScore)/stats.avgScore * 100)}%` : `${Math.round((stats.lastScore - stats.avgScore)/stats.avgScore * 100)}%`) : '0%'}
                  </div>
               </div>
            </div>
          </div>
        )}

        {analyzing && (
          <div className="glass-card animate-slide-up" style={{ textAlign: 'center', padding: '100px 24px' }}>
            <div className="spinner" style={{ margin: '0 auto 24px auto', width: '64px', height: '64px' }}></div>
            <h2 style={{ fontSize: '24px', color: 'white' }}>Analyzing Performance...</h2>
            <p style={{ color: '#9CA3AF', marginTop: '12px' }}>Evaluating pace, filler words, and vocal tonality.</p>
          </div>
        )}

        {results && !analyzing && (
          <div className="glass-card animate-slide-up" style={{ padding: '48px' }}>
            <div style={{ textAlign: 'center', marginBottom: '48px' }}>
              <div style={{ fontSize: '14px', color: '#06B6D4', fontWeight: 'bold', textTransform: 'uppercase', letterSpacing: '2px', marginBottom: '8px' }}>Session Result</div>
              <h2 style={{ fontSize: '32px', color: 'white', fontWeight: '800' }}>Analysis Complete</h2>
            </div>
            
            <div style={{ display: 'grid', gridTemplateColumns: '1.2fr 1fr', gap: '32px', marginBottom: '40px' }}>
              <div style={{ background: 'rgba(255,255,255,0.03)', padding: '40px', borderRadius: '24px', border: '1px solid var(--glass-border)', textAlign: 'center', display: 'flex', flexDirection: 'column', justifyContent: 'center' }}>
                <div style={{ fontSize: '80px', fontWeight: 'bold', color: '#06B6D4', lineHeight: 1 }}>{results.finalScore}<span style={{ fontSize: '24px', color: 'rgba(255,255,255,0.3)' }}>/100</span></div>
                <div style={{ color: 'white', fontSize: '18px', marginTop: '16px', fontWeight: '600' }}>Overall Score</div>
              </div>
              
              <div style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
                 <div style={{ background: 'rgba(255,255,255,0.03)', padding: '24px', borderRadius: '20px', display: 'flex', alignItems: 'center', gap: '20px', border: '1px solid var(--glass-border)' }}>
                    <Activity color="#06B6D4" size={24} />
                    <div>
                      <div style={{ fontWeight: 'bold', color: 'white', fontSize: '20px' }}>{results.wpm} <span style={{ fontSize: '14px', fontWeight: 'normal', color: '#9CA3AF' }}>WPM</span></div>
                      <div style={{ fontSize: '14px', color: '#9CA3AF' }}>Speaking Pace</div>
                    </div>
                 </div>
                 
                 <div style={{ background: 'rgba(255,255,255,0.03)', padding: '24px', borderRadius: '20px', display: 'flex', alignItems: 'center', gap: '20px', border: '1px solid var(--glass-border)' }}>
                    <Brain color="#F59E0B" size={24} />
                    <div>
                      <div style={{ fontWeight: 'bold', color: results.filler_count > 3 ? '#EF4444' : '#10B981', fontSize: '20px' }}>{results.filler_count} <span style={{ fontSize: '14px', fontWeight: 'normal', color: '#9CA3AF' }}>Instances</span></div>
                      <div style={{ fontSize: '14px', color: '#9CA3AF' }}>Filler Words</div>
                    </div>
                 </div>
              </div>
            </div>

            <div style={{ background: 'rgba(255,255,255,0.03)', padding: '32px', borderRadius: '24px', marginBottom: '40px', border: '1px solid var(--glass-border)' }}>
              <div style={{ display: 'flex', alignItems: 'center', gap: '12px', marginBottom: '16px' }}>
                <FileCheck color="#06B6D4" size={24} />
                <h3 style={{ margin: 0, color: 'white' }}>Transcription</h3>
              </div>
              <p style={{ lineHeight: '1.8', color: '#E5E7EB', fontSize: '16px', opacity: 0.8 }}>{results.transcription || "No transcription available."}</p>
            </div>

            <div style={{ display: 'flex', gap: '16px' }}>
              <button className="btn-primary" onClick={() => setResults(null)}>Start New Session</button>
              <button className="btn-primary" style={{ background: 'transparent', border: '1px solid var(--glass-border)' }} onClick={() => navigate('/history')}>View History</button>
            </div>
          </div>
        )}
      </main>
    </div>
  );
}
