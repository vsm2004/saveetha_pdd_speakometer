import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import axios from 'axios';
import { Mic } from 'lucide-react';
import { getApiUrl } from '../utils/config';

export default function Signup() {
  const [name, setName] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const navigate = useNavigate();

  const handleSignup = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');

    try {
      const response = await axios.post(getApiUrl('signup'), {
        name, email, password
      });
      
      const data = response.data;
      if (data.status === 'success') {
         // Auto-login or redirect to login
         navigate('/login');
      } else {
         setError(data.message || 'Signup failed');
      }
    } catch (err) {
       setError(err.response?.data?.detail || err.message);
    } finally {
       setLoading(false);
    }
  };

  return (
    <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center', minHeight: '100vh', padding: '24px' }}>
      <div className="glass-card animate-slide-up" style={{ width: '100%', maxWidth: '400px', display: 'flex', flexDirection: 'column', alignItems: 'center' }}>
        <div style={{ background: 'var(--accent-primary)', padding: '16px', borderRadius: '50%', marginBottom: '24px', boxShadow: '0 0 20px rgba(168, 85, 247, 0.4)' }}>
          <Mic size={32} color="white" />
        </div>
        <h1 style={{ marginBottom: '8px', fontSize: '28px', textAlign: 'center' }}>Create Account</h1>
        <p style={{ color: 'var(--text-secondary)', marginBottom: '32px', textAlign: 'center' }}>Join Speakometer Web today</p>
        
        {error && <div style={{ color: '#ef4444', background: 'rgba(239, 68, 68, 0.1)', padding: '12px', borderRadius: '8px', marginBottom: '16px', fontSize: '14px', width: '100%', textAlign: 'center' }}>{error}</div>}

        <form onSubmit={handleSignup} style={{ width: '100%' }}>
          <div style={{ marginBottom: '16px' }}>
            <label style={{ fontSize: '14px', color: 'var(--text-secondary)' }}>Full Name</label>
            <input 
              type="text" 
              className="input-dark" 
              placeholder="Enter your name" 
              name="name"
              autoComplete="name"
              value={name}
              onChange={(e) => setName(e.target.value)}
            />
          </div>
          <div style={{ marginBottom: '16px' }}>
            <label style={{ fontSize: '14px', color: 'var(--text-secondary)' }}>Email</label>
            <input 
              type="email" 
              className="input-dark" 
              placeholder="Enter your email" 
              name="email"
              autoComplete="username"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              required 
            />
          </div>
          <div style={{ marginBottom: '32px' }}>
            <label style={{ fontSize: '14px', color: 'var(--text-secondary)' }}>Password</label>
            <input 
              type="password" 
              className="input-dark" 
              placeholder="Create a password" 
              name="password"
              autoComplete="new-password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required 
              minLength="6"
            />
          </div>
          <button type="submit" className="btn-primary" disabled={loading} style={{ marginBottom: '16px' }}>
            {loading ? 'Registering...' : 'Sign Up'}
          </button>
          
          <div style={{ textAlign: 'center', fontSize: '14px', color: 'var(--text-secondary)' }}>
            Already have an account? <Link to="/login" style={{ color: 'var(--accent-secondary)', textDecoration: 'none' }}>Log in</Link>
          </div>
        </form>
      </div>
    </div>
  );
}
