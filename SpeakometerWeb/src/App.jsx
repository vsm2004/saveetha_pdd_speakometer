import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import Login from './pages/Login';
import Signup from './pages/Signup';
import Dashboard from './pages/Dashboard';
import History from './pages/History';
import Profile from './pages/Profile';
import Practice from './pages/Practice';
import Premium from './pages/Premium';
import PracticeExercise from './pages/PracticeExercise';
import Settings from './pages/Settings';

import Landing from './pages/Landing';

export default function App() {
  return (
    <Router>
      <Routes>
        <Route path="/" element={<Landing />} />
        <Route path="/login" element={<Login />} />
        <Route path="/signup" element={<Signup />} />
        <Route path="/dashboard" element={<Dashboard />} />
        <Route path="/history" element={<History />} />
        <Route path="/profile" element={<Profile />} />
        <Route path="/practice" element={<Practice />} />
        <Route path="/practice/session" element={<PracticeExercise />} />
        <Route path="/premium" element={<Premium />} />
        <Route path="/settings" element={<Settings />} />
      </Routes>
    </Router>
  );
}
