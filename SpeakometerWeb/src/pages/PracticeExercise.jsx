import React, { useState, useEffect, useRef } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import axios from 'axios';
import { ArrowLeft, Mic, Square, Play, CheckCircle, AlertCircle, Info } from 'lucide-react';
import { getApiUrl } from '../config';

const PRACTICE_DATA = {
  'Filler Words': {
    title: 'Reduce Filler Words',
    focus: 'Eliminate "um", "ah", "like", and "you know".',
    prompts: {
      'American': 'Describe your ideal weekend in New York City. Include details about places you would visit and how you would spend your time.',
      'British': 'Describe your favourite British television programme and explain why it appeals to you.',
      'Default': 'Introduce yourself and describe your professional background and goals in 2 minutes.'
    },
    tip: 'Silent pauses are better than filler words. When you feel a filler coming, just stop for a second.'
  },
  'Hesitation': {
    title: 'Avoid Hesitation',
    focus: 'Maintain a steady flow without unnecessary mid-sentence pauses.',
    prompts: {
      'American': 'Answer the question: "What is your biggest professional strength and how has it helped you in your career?"',
      'British': 'Introduce yourself professionally as if you were beginning a keynote speech at a major conference.',
      'Default': 'Answer the following question: "What is the most important lesson you have learned in your life?"'
    },
    tip: 'Connect your thoughts before you start speaking the next sentence to avoid "searching" mid-way.'
  },
  'Pace Variation': {
    title: 'Master Pace Variation',
    focus: 'Avoid speaking too fast or too slow. Aim for a conversational 130-150 WPM.',
    prompts: {
      'American': 'Give a 2-minute mock TED-style talk on a topic you are passionate about.',
      'British': 'Deliver a 2-minute formal argument either for or against the use of social media in schools.',
      'Default': 'Read a passage from your favourite book or an article, trying to maintain a steady, engaging pace.'
    },
    tip: 'Use your hands to pace your speech. Slower movements usually lead to a more measured pace.'
  }
};

export default function PracticeExercise() {
  const navigate = useNavigate();
  const location = useLocation();
  const query = new URLSearchParams(location.search);
  const topic = query.get('topic') || 'Filler Words';
  const data = PRACTICE_DATA[topic] || PRACTICE_DATA['Filler Words'];
  
  const userId = localStorage.getItem('USER_ID');
  const userAccent = localStorage.getItem('ACCENT_PREF') || 'Indian';
  const prompt = data.prompts[userAccent] || data.prompts['Default'] || data.prompts['Indian'];

  const [recording, setRecording] = useState(false);
  const [analyzing, setAnalyzing] = useState(false);
  const [results, setResults] = useState(null);
  const mediaRecorderRef = useRef(null);
  const audioChunksRef = useRef([]);

  const startRecording = async () => {
    try {
      const stream = await navigator.mediaDevices.getUserMedia({ audio: true });
      mediaRecorderRef.current = new MediaRecorder(stream);
      audioChunksRef.current = [];
      
      mediaRecorderRef.current.ondataavailable = (event) => {
        if (event.data.size > 0) audioChunksRef.current.push(event.data);
      };

      mediaRecorderRef.current.onstop = async () => {
        const audioBlob = new Blob(audioChunksRef.current, { type: 'audio/mp3' });
        await analyzeExercise(audioBlob);
        stream.getTracks().forEach(track => track.stop());
      };

      mediaRecorderRef.current.start();
      setRecording(true);
    } catch (err) {
      alert("Microphone access denied.");
    }
  };

  const stopRecording = () => {
    if (mediaRecorderRef.current) {
      mediaRecorderRef.current.stop();
      setRecording(false);
    }
  };

  const analyzeExercise = async (blob) => {
    setAnalyzing(true);
    const formData = new FormData();
    formData.append("file", blob, "exercise_recording.mp3");

    try {
      const res = await axios.post(getApiUrl("analyze"), formData);
      const resultData = res.data;

      if (resultData.status === 'success') {
        const score = Math.round((resultData.accent_score + resultData.confidence_score) / 2);
        setResults({ ...resultData, score });
        
        // Save completion status
        localStorage.setItem(`COMPLETED_PRACTICE_${topic}`, 'true');
        
        // Save session link
        const sessionPayload = {
          user_id: parseInt(userId, 10),
          score: score,
          fillers_count: resultData.filler_count || 0,
          stretching_level: resultData.wpm < 110 ? "high" : "none",
          confidence: resultData.confidence_score || 0
        };
        await axios.post(getApiUrl("save-session"), sessionPayload);
      }
    } catch (err) {
      alert("Analysis failed.");
    } finally {
      setAnalyzing(false);
    }
  };

  return (
    <div style={{ minHeight: '100vh', background: '#0F0B1F', color: 'white', padding: '40px 24px' }}>
      <header style={{ maxWidth: '800px', margin: '0 auto 40px auto', display: 'flex', alignItems: 'center', gap: '16px' }}>
        <button onClick={() => navigate('/practice')} style={{ background: 'var(--glass-bg)', border: '1px solid var(--glass-border)', borderRadius: '50%', width: '40px', height: '40px', display: 'flex', alignItems: 'center', justifyContent: 'center', color: 'white', cursor: 'pointer' }}>
          <ArrowLeft size={20} />
        </button>
        <h1 style={{ fontSize: '24px' }}>Practice Session</h1>
      </header>

      <main style={{ maxWidth: '800px', margin: '0 auto' }}>
        {!results && !analyzing && (
          <div className="glass-card animate-slide-up" style={{ padding: '40px' }}>
            <h2 style={{ fontSize: '28px', marginBottom: '8px' }}>{data.title}</h2>
            <p style={{ color: 'var(--accent-primary)', fontWeight: 'bold', marginBottom: '32px' }}>{data.focus}</p>
            
            <div style={{ background: 'rgba(255,255,255,0.03)', padding: '32px', borderRadius: '20px', border: '1px solid var(--glass-border)', marginBottom: '32px' }}>
              <div style={{ display: 'flex', alignItems: 'center', gap: '8px', color: '#9CA3AF', marginBottom: '16px', fontSize: '14px', textTransform: 'uppercase', letterSpacing: '1px' }}>
                <Play size={14} /> Your Prompt
              </div>
              <p style={{ fontSize: '18px', lineHeight: '1.6', color: '#E5E7EB' }}>{prompt}</p>
            </div>

            <div style={{ background: 'rgba(168, 85, 247, 0.05)', padding: '24px', borderRadius: '16px', border: '1px solid rgba(168, 85, 247, 0.2)', marginBottom: '40px', display: 'flex', gap: '16px' }}>
               <Info color="var(--accent-primary)" size={24} style={{ flexShrink: 0 }} />
               <p style={{ fontSize: '14px', color: '#9CA3AF', lineHeight: '1.5' }}><strong>Pro-Tip:</strong> {data.tip}</p>
            </div>

            <button 
              onClick={recording ? stopRecording : startRecording}
              className="btn-primary" 
              style={{ height: '64px', fontSize: '18px', background: recording ? '#EF4444' : 'var(--accent-primary)' }}
            >
              {recording ? 'Stop and Analyze' : 'Start Recording'}
            </button>
            {recording && <p style={{ textAlign: 'center', marginTop: '16px', color: '#EF4444' }} className="pulse">Recording in progress...</p>}
          </div>
        )}

        {analyzing && (
          <div className="glass-card animate-slide-up" style={{ textAlign: 'center', padding: '80px 24px' }}>
            <div className="spinner" style={{ margin: '0 auto 24px auto' }}></div>
            <h2>Analyzing your performance...</h2>
          </div>
        )}

        {results && (
          <div className="glass-card animate-slide-up" style={{ padding: '48px', textAlign: 'center' }}>
            <div style={{ background: '#10B98115', width: '80px', height: '80px', borderRadius: '50%', display: 'flex', alignItems: 'center', justifyContent: 'center', margin: '0 auto 24px auto' }}>
              <CheckCircle size={40} color="#10B981" />
            </div>
            <h2 style={{ fontSize: '32px', marginBottom: '8px' }}>Exercise Complete!</h2>
            <p style={{ color: '#9CA3AF', marginBottom: '40px' }}>You successfully completed the {topic} drill.</p>
            
            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '20px', marginBottom: '40px' }}>
              <div className="glass-card" style={{ padding: '24px' }}>
                <div style={{ fontSize: '32px', fontWeight: 'bold', color: 'var(--accent-secondary)' }}>{results.score}</div>
                <div style={{ fontSize: '14px', color: '#9CA3AF' }}>Overall Score</div>
              </div>
              <div className="glass-card" style={{ padding: '24px' }}>
                <div style={{ fontSize: '32px', fontWeight: 'bold', color: results.filler_count > 2 ? '#EF4444' : '#10B981' }}>{results.filler_count}</div>
                <div style={{ fontSize: '14px', color: '#9CA3AF' }}>Fillers Found</div>
              </div>
            </div>

            <div style={{ display: 'flex', gap: '16px' }}>
               <button className="btn-primary" onClick={() => setResults(null)}>Practice Again</button>
               <button className="btn-primary" style={{ background: 'transparent', border: '1px solid var(--glass-border)' }} onClick={() => navigate('/practice')}>Back to Hub</button>
            </div>
          </div>
        )}
      </main>
    </div>
  );
}
