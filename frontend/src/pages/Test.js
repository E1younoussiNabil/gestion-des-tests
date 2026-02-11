import React, { useState, useEffect, useCallback } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';
import toast from 'react-hot-toast';
import { 
  Clock, 
  CheckCircle,
  Play
} from 'lucide-react';

const Test = () => {
  const { user } = useAuth();
  const navigate = useNavigate();
  const [session, setSession] = useState(null);
  const [questions, setQuestions] = useState([]);
  const [currentQuestionIndex, setCurrentQuestionIndex] = useState(0);
  const [answers, setAnswers] = useState({});
  const [validatedQuestions, setValidatedQuestions] = useState({});
  const [questionTimers, setQuestionTimers] = useState({});
  const [questionStartAt, setQuestionStartAt] = useState(null);
  const [timeLeft, setTimeLeft] = useState(0);
  const [isStarted, setIsStarted] = useState(false);
  const [isFinished, setIsFinished] = useState(false);
  const [loading, setLoading] = useState(false);
  const [submitting, setSubmitting] = useState(false);
  const [creneauInfo, setCreneauInfo] = useState(null);
  const [secondsUntilStart, setSecondsUntilStart] = useState(null);
  const [canStartScheduled, setCanStartScheduled] = useState(false);
  const [isAfterEndScheduled, setIsAfterEndScheduled] = useState(false);

  const checkExistingSession = useCallback(async () => {
    try {
      const response = await fetch('/api/tests/session-active', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ codeSession: user.codeSession }),
      });

      if (response.ok) {
        const data = await response.json();
        if (data.session) {
          setSession(data.session);
          setQuestions(data.questions);
          setCurrentQuestionIndex(data.currentQuestionIndex || 0);
          const restoredAnswers = data.answers || {};
          setAnswers(restoredAnswers);
          const restoredValidated = {};
          (data.questions || []).forEach((q) => {
            const question = q.question || q;
            const qid = question?.id;
            if (qid == null) {
              return;
            }
            const val = restoredAnswers[qid];
            const hasAnswer = Array.isArray(val)
              ? val.length > 0
              : (typeof val === 'string'
                  ? val.trim().length > 0
                  : val !== undefined && val !== null);
            if (hasAnswer) {
              restoredValidated[qid] = true;
            }
          });
          setValidatedQuestions(restoredValidated);
          setTimeLeft(data.timeLeft || 0);
          setIsStarted(true);
        }
      }
    } catch (error) {
      console.error('Error checking session:', error);
    }
  }, [user]);

  const handleSubmitTest = useCallback(async (responseTimesOverride = null) => {
    setSubmitting(true);
    try {
      const response = await fetch('/api/tests/soumettre', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          sessionId: session.id,
          answers,
          responseTimes: responseTimesOverride || questionTimers
        }),
      });

      let data;
      const contentType = response.headers.get('content-type');
      if (contentType && contentType.includes('application/json')) {
        data = await response.json();
      } else {
        const text = await response.text();
        console.error('Non-JSON response:', text);
        data = { error: text };
      }

      if (response.ok) {
        setIsFinished(true);
        toast.success('Test terminé !');
        
        // Attendre un peu pour s'assurer que les données sont bien enregistrées
        setTimeout(() => {
          // Déclencher un événement pour rafraîchir les résultats
          window.dispatchEvent(new CustomEvent('refreshResults'));
          
          setTimeout(() => {
            navigate('/resultats');
          }, 500);
        }, 1000);
      } else {
        toast.error(data.error || 'Erreur lors de la soumission');
      }
    } catch (error) {
      console.error('Error submitting test:', error);
      toast.error('Erreur de connexion au serveur');
    } finally {
      setSubmitting(false);
    }
  }, [session, navigate, answers, questionTimers]);

  useEffect(() => {
    if (!user) {
      navigate('/connexion');
      return;
    }
    checkExistingSession();
  }, [user, navigate, checkExistingSession]);

  useEffect(() => {
    if (!user?.codeSession || String(user.codeSession).trim() === '') {
      return;
    }

    let interval;
    let serverOffsetMs = 0;
    let startAt = null;
    let endAt = null;

    const tick = () => {
      if (!startAt) {
        return;
      }
      const nowServer = new Date(Date.now() + serverOffsetMs);
      const diffStart = Math.max(0, Math.floor((startAt.getTime() - nowServer.getTime()) / 1000));
      setSecondsUntilStart(diffStart);
      const allowed = nowServer.getTime() >= startAt.getTime() && (!endAt || nowServer.getTime() <= endAt.getTime());
      setCanStartScheduled(allowed);
      setIsAfterEndScheduled(Boolean(endAt && nowServer.getTime() > endAt.getTime()));
    };

    const fetchCreneauInfo = async () => {
      try {
        const response = await fetch(`/api/candidats/creneau-info/${encodeURIComponent(user.codeSession)}`);
        if (!response.ok) {
          return;
        }
        const data = await response.json();
        setCreneauInfo(data);

        const serverMs = Number.isFinite(Number(data.serverTimeMs)) ? Number(data.serverTimeMs) : null;
        const startMs = Number.isFinite(Number(data.debutCreneauMs)) ? Number(data.debutCreneauMs) : null;
        const endMs = Number.isFinite(Number(data.finCreneauMs)) ? Number(data.finCreneauMs) : null;

        const serverTime = serverMs != null ? new Date(serverMs) : (data.serverTime ? new Date(data.serverTime) : null);
        const start = startMs != null ? new Date(startMs) : (data.debutCreneau ? new Date(data.debutCreneau) : null);
        const end = endMs != null ? new Date(endMs) : (data.finCreneau ? new Date(data.finCreneau) : null);

        if (start) {
          serverOffsetMs = serverTime ? (serverTime.getTime() - Date.now()) : 0;
          startAt = start;
          endAt = end;
          tick();
          clearInterval(interval);
          interval = setInterval(tick, 1000);
        }
      } catch (e) {
        console.error('Error fetching créneau info:', e);
      }
    };

    fetchCreneauInfo();

    return () => {
      if (interval) {
        clearInterval(interval);
      }
    };
  }, [user?.codeSession]);

  useEffect(() => {
    let timer;
    if (isStarted && !isFinished && timeLeft > 0) {
      timer = setTimeout(() => {
        setTimeLeft(timeLeft - 1);
      }, 1000);
    } else if (timeLeft === 0 && isStarted && !isFinished) {
      handleSubmitTest();
    }
    return () => clearTimeout(timer);
  }, [timeLeft, isStarted, isFinished, handleSubmitTest]);

  const startTest = async () => {
    if (!canStartScheduled || isAfterEndScheduled || (secondsUntilStart != null && secondsUntilStart > 0)) {
      return;
    }
    setLoading(true);
    try {
      const response = await fetch('/api/tests/demarrer', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          codeSession: user.codeSession,
        }),
      });

      const data = await response.json();

      if (response.ok) {
        setSession(data.session);
        setQuestions(data.questions);
        setCurrentQuestionIndex(0);
        setAnswers({});
        setValidatedQuestions({});
        setQuestionTimers({});
        setQuestionStartAt(Date.now());
        setTimeLeft(20 * 60);
        setIsStarted(true);
        toast.success('Test démarré !');
      } else {
        toast.error(data.error || 'Erreur lors du démarrage du test');
      }
    } catch (error) {
      console.error('Error starting test:', error);
      toast.error('Erreur de connexion au serveur');
    } finally {
      setLoading(false);
    }
  };

  const handleAnswer = (questionId, answer) => {
    setAnswers(prev => ({
      ...prev,
      [questionId]: answer
    }));
  };

  const isAnswerProvided = (question, answerValue) => {
    const typeNom = question?.typeQuestion?.nom;
    if (typeNom === 'MULTIPLE') {
      return Array.isArray(answerValue) && answerValue.length > 0;
    }
    if (typeNom === 'TEXTE') {
      return typeof answerValue === 'string' && answerValue.trim().length > 0;
    }
    return answerValue !== undefined && answerValue !== null;
  };

  const validateCurrentAnswer = () => {
    const question = questions[currentQuestionIndex]?.question || questions[currentQuestionIndex];
    if (!question) {
      return;
    }
    const qid = question.id;
    const answerValue = answers[qid];
    if (!isAnswerProvided(question, answerValue)) {
      toast.error('Vous devez répondre à la question avant de continuer.');
      return;
    }

    if (!validatedQuestions[qid] && questionStartAt) {
      const seconds = Math.max(0, Math.round((Date.now() - questionStartAt) / 1000));
      setQuestionTimers(prev => ({
        ...prev,
        [qid]: seconds
      }));
    }

    setValidatedQuestions(prev => ({
      ...prev,
      [qid]: true
    }));
  };

  const goToNextQuestion = () => {
    const question = questions[currentQuestionIndex]?.question || questions[currentQuestionIndex];
    if (!question) {
      return;
    }
    const qid = question.id;
    if (!validatedQuestions[qid]) {
      validateCurrentAnswer();
      if (!isAnswerProvided(question, answers[qid])) {
        return;
      }
    }

    if (currentQuestionIndex < questions.length - 1) {
      setCurrentQuestionIndex(currentQuestionIndex + 1);
      setQuestionStartAt(Date.now());
    }
  };

  const formatTime = (seconds) => {
    const mins = Math.floor(seconds / 60);
    const secs = seconds % 60;
    return `${mins.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`;
  };

  const formatDuration = (seconds) => {
    const s = Math.max(0, seconds || 0);
    const hours = Math.floor(s / 3600);
    const mins = Math.floor((s % 3600) / 60);
    const secs = s % 60;
    if (hours > 0) {
      return `${hours.toString().padStart(2, '0')}:${mins.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`;
    }
    return `${mins.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`;
  };

  const currentQuestion = questions[currentQuestionIndex]?.question || questions[currentQuestionIndex];
  const currentQuestionId = currentQuestion?.id;
  const isCurrentValidated = currentQuestionId != null ? !!validatedQuestions[currentQuestionId] : false;
  const canValidateCurrent = currentQuestionId != null
    ? isAnswerProvided(currentQuestion, answers[currentQuestionId])
    : false;

  if (!user) {
    return null;
  }

  if (isFinished) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <div className="text-center">
          <CheckCircle className="h-16 w-16 text-green-600 mx-auto mb-4" />
          <h2 className="text-2xl font-bold text-gray-900 mb-2">
            Test terminé !
          </h2>
          <p className="text-gray-600 mb-4">
            Redirection vers vos résultats...
          </p>
        </div>
      </div>
    );
  }

  if (!isStarted && (!user?.codeSession || String(user.codeSession).trim() === '')) {
    navigate('/connexion?step=portal');
    return null;
  }

  if (!isStarted) {
    const waitingForStart = secondsUntilStart !== null && secondsUntilStart > 0;
    const startDisabled = loading || waitingForStart || !canStartScheduled || isAfterEndScheduled;
    const status = creneauInfo?.status;
    const showTooEarly = secondsUntilStart !== null && secondsUntilStart > 0;
    const showTooLate = status === 'AFTER' || isAfterEndScheduled;

    return (
      <div className="min-h-screen flex items-center justify-center py-12">
        <div className="max-w-md w-full">
          <div className="text-center">
            <div className="mx-auto h-12 w-12 flex items-center justify-center rounded-full bg-blue-100 mb-4">
              <Play className="h-6 w-6 text-blue-600" />
            </div>
            <h2 className="text-3xl font-bold text-gray-900 mb-4">
              Prêt à commencer le test ?
            </h2>
            <p className="text-gray-600 mb-8">
              Vous aurez {questions.length || 30} questions, avec 20 minutes au total.
            </p>

            <div className="bg-white rounded-lg shadow-lg p-8">
              {showTooEarly && (
                <div className="mb-4 bg-yellow-50 border border-yellow-200 rounded-lg p-4 text-sm text-yellow-800">
                  <div className="font-medium mb-1">Vous êtes en avance.</div>
                  <div>
                    Le test pourra commencer dans:
                    <span className="font-mono font-bold"> {formatDuration(secondsUntilStart)}</span>
                  </div>
                </div>
              )}

              {showTooLate && (
                <div className="mb-4 bg-red-50 border border-red-200 rounded-lg p-4 text-sm text-red-800">
                  <div className="font-medium mb-1">Créneau expiré.</div>
                  <div>Vous ne pouvez plus démarrer le test pour ce créneau.</div>
                </div>
              )}

              <button
                onClick={startTest}
                disabled={startDisabled}
                className="btn btn-primary"
              >
                {loading ? 'Démarrage...' : 'Démarrer le test'}
              </button>
            </div>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-50">
      <div className="max-w-4xl mx-auto py-8 px-4">
        {/* Header */}
        <div className="bg-white rounded-lg shadow-lg p-6 mb-6">
          <div className="flex justify-between items-center">
            <div>
              <h1 className="text-2xl font-bold text-gray-900">
                Test en cours
              </h1>
              <p className="text-gray-600">
                Question {currentQuestionIndex + 1} sur {questions.length}
              </p>
            </div>
            <div className={`flex items-center space-x-2 px-4 py-2 rounded-lg ${
              timeLeft < 60 ? 'bg-red-100 text-red-800' : 'bg-blue-100 text-blue-800'
            }`}>
              <Clock className="h-5 w-5" />
              <span className="font-mono font-bold">
                {formatTime(timeLeft)}
              </span>
            </div>
          </div>
          
          {/* Progress bar */}
          <div className="mt-4">
            <div className="w-full bg-gray-200 rounded-full h-2">
              <div 
                className="bg-blue-600 h-2 rounded-full transition-all duration-300"
                style={{ width: `${((currentQuestionIndex + 1) / questions.length) * 100}%` }}
              ></div>
            </div>
          </div>
        </div>

        {/* Question */}
        {currentQuestion && (
          <div className="bg-white rounded-lg shadow-lg p-8 mb-6">
            <div className="mb-6">
              <div className="flex items-center justify-between mb-4">
                <span className="bg-blue-100 text-blue-800 text-sm font-medium px-3 py-1 rounded-full">
                  Question {currentQuestionIndex + 1}
                </span>
                <span className="bg-gray-100 text-gray-800 text-sm font-medium px-3 py-1 rounded-full">
                  {currentQuestion.typeQuestion?.nom}
                </span>
              </div>
              <h2 className="text-xl font-semibold text-gray-900 mb-4">
                {currentQuestion.libelle}
              </h2>
            </div>

            {/* Réponses */}
            <div className="space-y-3">
              {currentQuestion.reponsesPossibles?.map((reponse) => (
                <label
                  key={reponse.id}
                  className={`flex items-center p-4 border rounded-lg transition-all ${
                    answers[currentQuestion.id] === reponse.id
                      ? 'border-blue-500 bg-blue-50'
                      : 'border-gray-200'
                  }`}
                >
                  <input
                    type={
                      currentQuestion.typeQuestion?.nom === 'MULTIPLE'
                        ? 'checkbox'
                        : 'radio'
                    }
                    name={`question-${currentQuestion.id}`}
                    disabled={isCurrentValidated}
                    checked={
                      currentQuestion.typeQuestion?.nom === 'MULTIPLE'
                        ? Array.isArray(answers[currentQuestion.id]) && 
                          answers[currentQuestion.id].includes(reponse.id)
                        : answers[currentQuestion.id] === reponse.id
                    }
                    onChange={(e) => {
                      if (isCurrentValidated) {
                        return;
                      }
                      if (currentQuestion.typeQuestion?.nom === 'MULTIPLE') {
                        if (e.target.checked) {
                          setAnswers(prev => {
                            const existing = Array.isArray(prev[currentQuestion.id]) ? prev[currentQuestion.id] : [];
                            return {
                              ...prev,
                              [currentQuestion.id]: [...existing, reponse.id]
                            };
                          });
                        } else {
                          setAnswers(prev => {
                            const existing = Array.isArray(prev[currentQuestion.id]) ? prev[currentQuestion.id] : [];
                            return {
                              ...prev,
                              [currentQuestion.id]: existing.filter(id => id !== reponse.id)
                            };
                          });
                        }
                      } else {
                        setAnswers(prev => ({
                          ...prev,
                          [currentQuestion.id]: reponse.id
                        }));
                      }
                    }}
                    className="mr-3"
                  />
                  <span className="ml-3 text-gray-700">{reponse.libelle}</span>
                </label>
              ))}
            </div>

            {currentQuestion.typeQuestion?.nom === 'TEXTE' && (
              <textarea
                className="input mt-4"
                rows={4}
                placeholder="Tapez votre réponse ici..."
                value={answers[currentQuestion.id] || ''}
                disabled={isCurrentValidated}
                onChange={(e) => {
                  if (isCurrentValidated) {
                    return;
                  }
                  handleAnswer(currentQuestion.id, e.target.value);
                }}
              />
            )}
          </div>
        )}

        {/* Navigation */}
        <div className="flex justify-end items-center">
          {currentQuestionIndex === questions.length - 1 ? (
            <button
              onClick={() => {
                if (!isCurrentValidated) {
                  if (!canValidateCurrent) {
                    return;
                  }
                  const qid = currentQuestion.id;
                  const seconds = questionStartAt
                    ? Math.max(0, Math.round((Date.now() - questionStartAt) / 1000))
                    : null;
                  const nextTimes = seconds != null
                    ? { ...questionTimers, [qid]: seconds }
                    : { ...questionTimers };
                  validateCurrentAnswer();
                  handleSubmitTest(nextTimes);
                  return;
                }

                handleSubmitTest();
              }}
              disabled={submitting || (!isCurrentValidated && !canValidateCurrent)}
              className="btn btn-success disabled:opacity-50"
            >
              {submitting ? 'Soumission...' : 'Terminer le test'}
            </button>
          ) : (
            <button
              onClick={goToNextQuestion}
              disabled={!canValidateCurrent && !isCurrentValidated}
              className="btn btn-primary disabled:opacity-50"
            >
              Go to next question
            </button>
          )}
        </div>
      </div>
    </div>
  );
};

export default Test;
