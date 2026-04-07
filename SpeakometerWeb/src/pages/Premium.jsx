import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import Navbar from '../components/Navbar';
import { Crown, Check, Zap, BarChart, Shield, Play, X, CreditCard, Calendar, Lock, AlertCircle, CheckCircle } from 'lucide-react';
import { getApiUrl } from '../config';

export default function Premium() {
  const navigate = useNavigate();

  const benefits = [
    { title: 'Unlimited Sessions', icon: Zap },
    { title: 'Detailed Reports', icon: BarChart },
    { title: 'Expert Drill-down Tips', icon: Play },
    { title: 'Ad-free Experience', icon: Shield },
  ];

  const plans = [
    { title: 'Monthly', price: '₹199', desc: 'Billed monthly' },
    { title: 'Yearly', price: '₹1199', desc: 'Billed annually', popular: true },
  ];

  const [showCheckout, setShowCheckout] = useState(false);
  const [selectedPlan, setSelectedPlan] = useState(null);
  const [cardDetails, setCardDetails] = useState({ number: '', expiry: '', cvc: '' });
  const [isProcessing, setIsProcessing] = useState(false);
  const [status, setStatus] = useState('idle'); // idle, processing, success, error
  const [errorHeader, setErrorHeader] = useState('');
  const [errorMessage, setErrorMessage] = useState('');

  const formatCardNumber = (value) => {
    const v = value.replace(/\s+/g, '').replace(/[^0-9]/gi, '');
    const matches = v.match(/\d{4,16}/g);
    const match = matches && matches[0] || '';
    const parts = [];
    for (let i = 0, len = match.length; i < len; i += 4) {
      parts.push(match.substring(i, i + 4));
    }
    if (parts.length) {
      return parts.join(' ');
    } else {
      return v;
    }
  };

  const handleCardChange = (e) => {
    const { name, value } = e.target;
    if (name === 'number') {
      setCardDetails({ ...cardDetails, number: formatCardNumber(value) });
    } else if (name === 'expiry') {
      let v = value.replace(/[^0-9/]/g, '');
      if (v.length === 2 && !v.includes('/') && cardDetails.expiry.length < 2) v += '/';
      if (v.length <= 5) setCardDetails({ ...cardDetails, expiry: v });
    } else if (name === 'cvc') {
      const v = value.replace(/[^0-9]/g, '');
      if (v.length <= 3) setCardDetails({ ...cardDetails, cvc: v });
    }
  };

  const mockBankResponse = (number, cvc) => {
    const cleanNumber = number.replace(/\s/g, '');
    if (cvc === '000') return "Security code (CVC) check failed.";
    if (cleanNumber.endsWith('0000')) return "Insufficient funds in bank account.";
    if (cleanNumber.startsWith('5')) return "This card has expired.";
    if (cleanNumber.startsWith('4') || cleanNumber.startsWith('2')) return "SUCCESS";
    return "Transaction declined by issuing bank.";
  };

  const handleSubscribe = (plan) => {
    setSelectedPlan(plan);
    setShowCheckout(true);
    setStatus('idle');
  };

  const processPayment = async () => {
    const { number, expiry, cvc } = cardDetails;
    const cleanNumber = number.replace(/\s/g, '');

    // Validation
    if (cleanNumber.length < 16) {
      setErrorHeader('Invalid Card');
      setErrorMessage('Please enter a valid 16-digit card number.');
      setStatus('error');
      return;
    }
    if (!expiry.includes('/') || expiry.length < 5) {
      setErrorHeader('Invalid Expiry');
      setErrorMessage('Please use the MM/YY format.');
      setStatus('error');
      return;
    }
    if (cvc.length < 3) {
      setErrorHeader('Invalid CVC');
      setErrorMessage('Please enter a valid 3-digit security code.');
      setStatus('error');
      return;
    }

    setStatus('processing');
    setIsProcessing(true);

    // Simulate bank latency
    setTimeout(async () => {
      const bankResult = mockBankResponse(cleanNumber, cvc);

      if (bankResult === 'SUCCESS') {
        try {
          const userId = localStorage.getItem('USER_ID');
          const res = await axios.post(getApiUrl('upgrade-premium'), {
            user_id: parseInt(userId, 10),
            plan_type: selectedPlan.title.toLowerCase(),
            is_free_trial: false
          });

          if (res.data.status === 'success') {
            localStorage.setItem('IS_PREMIUM', 'true');
            localStorage.setItem('PREMIUM_PLAN', selectedPlan.title);
            setStatus('success');
          } else {
            setErrorHeader('System Error');
            setErrorMessage(res.data.message || 'Failed to update account.');
            setStatus('error');
          }
        } catch (err) {
          setErrorHeader('Network Error');
          setErrorMessage('Could not connect to the server.');
          setStatus('error');
        }
      } else {
        setErrorHeader('Payment Failed');
        setErrorMessage(bankResult);
        setStatus('error');
      }
      setIsProcessing(false);
    }, 2000);
  };

  return (
    <div style={{ display: 'flex', minHeight: '100vh', background: '#0F0B1F' }}>
      <Navbar />

      <main style={{ flex: 1, marginLeft: '260px', padding: '40px 60px', maxWidth: '1000px', margin: '0 auto' }}>
        <div style={{ textAlign: 'center', marginBottom: '60px' }}>
          <div style={{
            background: 'rgba(236, 72, 153, 0.1)',
            width: '64px',
            height: '64px',
            borderRadius: '20px',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            margin: '0 auto 24px auto',
            border: '1px solid rgba(236, 72, 153, 0.2)'
          }}>
            <Crown size={32} color="#EC4899" />
          </div>
          <h1 style={{ fontSize: '40px', color: 'white', fontWeight: '800', marginBottom: '16px' }}>Go Premium</h1>
          <p style={{ color: '#9CA3AF', fontSize: '18px', maxWidth: '600px', margin: '0 auto' }}>
            Unlock your full potential with unlimited AI analysis and personalized coaching.
          </p>
        </div>

        {/* Benefits Grid */}
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(2, 1fr)', gap: '20px', marginBottom: '60px' }}>
          {benefits.map((benefit, i) => (
            <div key={i} className="glass-card" style={{ padding: '24px', display: 'flex', alignItems: 'center', gap: '20px' }}>
              <div style={{ background: '#06B6D415', padding: '12px', borderRadius: '12px' }}>
                <benefit.icon size={24} color="#06B6D4" />
              </div>
              <span style={{ color: 'white', fontSize: '18px', fontWeight: '600' }}>{benefit.title}</span>
            </div>
          ))}
        </div>

        {/* Plans Grid */}
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(2, 1fr)', gap: '24px', maxWidth: '700px', margin: '0 auto' }}>
          {plans.map((plan, i) => (
            <div
              key={i}
              className="glass-card"
              style={{
                padding: '40px 24px',
                textAlign: 'center',
                border: plan.popular ? '2px solid #EC4899' : '1px solid rgba(255,255,255,0.1)',
                position: 'relative'
              }}
            >
              {plan.popular && (
                <div style={{
                  position: 'absolute',
                  top: '-12px',
                  left: '50%',
                  transform: 'translateX(-50%)',
                  background: '#EC4899',
                  color: 'white',
                  fontSize: '12px',
                  fontWeight: '800',
                  padding: '4px 12px',
                  borderRadius: '12px',
                  textTransform: 'uppercase'
                }}>
                  Best Value
                </div>
              )}
              <h3 style={{ color: 'white', fontSize: '20px', fontWeight: '700', marginBottom: '8px' }}>{plan.title}</h3>
              <div style={{ fontSize: '36px', fontWeight: '800', color: 'white', marginBottom: '4px' }}>{plan.price}</div>
              <p style={{ color: '#9CA3AF', fontSize: '14px', marginBottom: '32px' }}>{plan.desc}</p>

              <button
                onClick={() => handleSubscribe(plan)}
                style={{
                  width: '100%',
                  padding: '14px',
                  borderRadius: '12px',
                  border: 'none',
                  background: plan.popular ? '#EC4899' : 'rgba(255,255,255,0.05)',
                  color: 'white',
                  fontWeight: 'bold',
                  cursor: 'pointer',
                  transition: 'background 0.2s'
                }}
              >
                Select Plan
              </button>
            </div>
          ))}
        </div>

        <div style={{ textAlign: 'center', marginTop: '48px' }}>
          <button style={{ background: 'transparent', border: 'none', color: '#9CA3AF', textDecoration: 'underline', cursor: 'pointer', fontSize: '14px' }}>
            Start 7-day Free Trial
          </button>
        </div>

        {/* Checkout Modal */}
        {showCheckout && (
          <div style={{
            position: 'fixed', top: 0, left: 0, right: 0, bottom: 0,
            background: 'rgba(0,0,0,0.85)', display: 'flex', alignItems: 'center', justifyContent: 'center',
            zIndex: 1000, backdropFilter: 'blur(10px)'
          }}>
            <div className="glass-card animate-slide-up" style={{ width: '100%', maxWidth: '420px', padding: '0', overflow: 'hidden' }}>

              {/* Header */}
              <div style={{ padding: '24px', position: 'relative', borderBottom: '1px solid rgba(255,255,255,0.05)' }}>
                <button
                  onClick={() => !isProcessing && setShowCheckout(false)}
                  style={{ position: 'absolute', right: '20px', top: '24px', background: 'transparent', border: 'none', color: '#9CA3AF', cursor: 'pointer' }}
                >
                  <X size={20} />
                </button>
                <h3 style={{ color: 'white', fontSize: '20px', fontWeight: '800' }}>Secure Checkout</h3>
                <p style={{ color: '#9CA3AF', fontSize: '14px' }}>Complete your {selectedPlan?.title} subscription</p>
              </div>

              <div style={{ padding: '32px' }}>
                {status === 'success' ? (
                  <div style={{ textAlign: 'center', padding: '20px 0' }}>
                    <div style={{ background: '#10B98115', width: '64px', height: '64px', borderRadius: '50%', display: 'flex', alignItems: 'center', justifyContent: 'center', margin: '0 auto 24px auto' }}>
                      <CheckCircle size={32} color="#10B981" />
                    </div>
                    <h2 style={{ color: 'white', fontSize: '24px', fontWeight: '800', marginBottom: '12px' }}>Payment Successful!</h2>
                    <p style={{ color: '#9CA3AF', marginBottom: '32px' }}>Welcome to the elite club. Your premium features are now active.</p>
                    <button onClick={() => window.location.href = '/dashboard'} className="btn-primary" style={{ width: '100%' }}>Go to Dashboard</button>
                  </div>
                ) : (
                  <>
                    <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '32px', background: 'rgba(255,255,255,0.03)', padding: '16px', borderRadius: '12px' }}>
                      <div style={{ color: '#9CA3AF' }}>Total Due:</div>
                      <div style={{ color: 'white', fontWeight: 'bold', fontSize: '18px' }}>{selectedPlan?.price}</div>
                    </div>

                    {status === 'processing' ? (
                      <div style={{ textAlign: 'center', padding: '40px 0' }}>
                        <div className="spinner" style={{ margin: '0 auto 24px auto' }}></div>
                        <p style={{ color: 'white', fontWeight: 'bold' }}>Connecting to secure gateway...</p>
                        <p style={{ color: '#9CA3AF', fontSize: '13px', marginTop: '8px' }}>Please do not refresh the page.</p>
                      </div>
                    ) : (
                      <div style={{ display: 'flex', flexDirection: 'column', gap: '20px' }}>
                        <div>
                          <label style={{ display: 'block', color: '#9CA3AF', fontSize: '12px', fontWeight: 'bold', textTransform: 'uppercase', marginBottom: '8px' }}>Card Number</label>
                          <div style={{ position: 'relative' }}>
                            <CreditCard size={18} color="#4B5563" style={{ position: 'absolute', left: '14px', top: '15px' }} />
                            <input
                              type="text"
                              name="number"
                              placeholder="0000 0000 0000 0000"
                              value={cardDetails.number}
                              onChange={handleCardChange}
                              style={{ width: '100%', padding: '14px 14px 14px 44px', background: 'rgba(255,255,255,0.05)', border: '1px solid rgba(255,255,255,0.1)', borderRadius: '12px', color: 'white', fontSize: '16px' }}
                            />
                          </div>
                        </div>

                        <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '16px' }}>
                          <div>
                            <label style={{ display: 'block', color: '#9CA3AF', fontSize: '12px', fontWeight: 'bold', textTransform: 'uppercase', marginBottom: '8px' }}>Expiry</label>
                            <div style={{ position: 'relative' }}>
                              <Calendar size={18} color="#4B5563" style={{ position: 'absolute', left: '14px', top: '15px' }} />
                              <input
                                type="text"
                                name="expiry"
                                placeholder="MM/YY"
                                value={cardDetails.expiry}
                                onChange={handleCardChange}
                                style={{ width: '100%', padding: '14px 14px 14px 44px', background: 'rgba(255,255,255,0.05)', border: '1px solid rgba(255,255,255,0.1)', borderRadius: '12px', color: 'white', fontSize: '16px' }}
                              />
                            </div>
                          </div>
                          <div>
                            <label style={{ display: 'block', color: '#9CA3AF', fontSize: '12px', fontWeight: 'bold', textTransform: 'uppercase', marginBottom: '8px' }}>CVC</label>
                            <div style={{ position: 'relative' }}>
                              <Lock size={18} color="#4B5563" style={{ position: 'absolute', left: '14px', top: '15px' }} />
                              <input
                                type="password"
                                name="cvc"
                                placeholder="123"
                                value={cardDetails.cvc}
                                onChange={handleCardChange}
                                style={{ width: '100%', padding: '14px 14px 14px 44px', background: 'rgba(255,255,255,0.05)', border: '1px solid rgba(255,255,255,0.1)', borderRadius: '12px', color: 'white', fontSize: '16px' }}
                              />
                            </div>
                          </div>
                        </div>

                        {status === 'error' && (
                          <div style={{ background: 'rgba(239, 68, 68, 0.1)', padding: '16px', borderRadius: '12px', display: 'flex', gap: '12px', border: '1px solid rgba(239, 68, 68, 0.2)' }}>
                            <AlertCircle size={20} color="#EF4444" style={{ flexShrink: 0 }} />
                            <div>
                              <div style={{ color: '#EF4444', fontWeight: 'bold', fontSize: '14px' }}>{errorHeader}</div>
                              <div style={{ color: 'rgba(239, 68, 68, 0.8)', fontSize: '13px' }}>{errorMessage}</div>
                            </div>
                          </div>
                        )}

                        <button
                          onClick={processPayment}
                          className="btn-primary"
                          style={{ width: '100%', height: '54px', marginTop: '12px' }}
                        >
                          Pay {selectedPlan?.price}
                        </button>
                      </div>
                    )}
                  </>
                )}
              </div>

              <div style={{ padding: '20px', background: 'rgba(255,255,255,0.02)', textAlign: 'center', borderTop: '1px solid rgba(255,255,255,0.05)' }}>
                <p style={{ color: '#4B5563', fontSize: '12px', display: 'flex', alignItems: 'center', justifyContent: 'center', gap: '6px' }}>
                  <Shield size={12} /> Encrypted & Secure Payment
                </p>
              </div>
            </div>
          </div>
        )}
      </main>
    </div>
  );
}
