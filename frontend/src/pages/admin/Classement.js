import React, { useEffect, useState } from 'react';
import toast from 'react-hot-toast';
import { Trophy, Clock } from 'lucide-react';

const formatDuration = (seconds) => {
  const s = Math.max(0, Number(seconds) || 0);
  const mins = Math.floor(s / 60);
  const secs = s % 60;
  return `${mins}m ${secs}s`;
};

const Classement = () => {
  const [loading, setLoading] = useState(true);
  const [rows, setRows] = useState([]);
  const [modeTest, setModeTest] = useState('');

  useEffect(() => {
    const fetchClassement = async () => {
      setLoading(true);
      try {
        const qs = new URLSearchParams();
        if (modeTest) {
          qs.set('modeTest', modeTest);
        }
        const url = qs.toString()
          ? `/api/admin/resultats/classement?${qs.toString()}`
          : '/api/admin/resultats/classement';
        const resp = await fetch(url);
        const contentType = resp.headers.get('content-type') || '';
        const data = contentType.includes('application/json')
          ? await resp.json().catch(() => ({}))
          : await resp.text().then((t) => ({ error: t })).catch(() => ({}));
        if (!resp.ok) {
          toast.error(data?.error || 'Erreur lors du chargement du classement');
          setRows([]);
          return;
        }
        setRows(Array.isArray(data?.classement) ? data.classement : []);
      } catch (e) {
        console.error('Admin classement fetch error:', e);
        toast.error('Erreur de connexion au serveur');
      } finally {
        setLoading(false);
      }
    };

    fetchClassement();
  }, [modeTest]);

  return (
    <div className="p-6">
      <div className="mb-8">
        <h1 className="text-3xl font-bold text-gray-900">Classement</h1>
        <p className="text-gray-600 mt-2">Classement global (score puis temps)</p>
      </div>

      <div className="bg-white rounded-lg shadow p-4 mb-6 flex items-center justify-end">
        <div className="flex items-center gap-3">
          <label className="text-sm font-medium text-gray-700">Filtrer</label>
          <select
            className="input"
            value={modeTest}
            onChange={(e) => setModeTest(e.target.value)}
          >
            <option value="">Tous</option>
            <option value="GENERAL">Culture générale</option>
            <option value="INFORMATIQUE">Informatique</option>
          </select>
        </div>
      </div>

      {loading ? (
        <div className="flex items-center justify-center h-64">
          <div className="loading" />
        </div>
      ) : (
        <div className="bg-white rounded-lg shadow overflow-hidden">
          <div className="overflow-x-auto">
            <table className="min-w-full">
              <thead className="bg-gray-50">
                <tr>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Rang</th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Candidat</th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Email</th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">École</th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Type</th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Spécialité</th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Score</th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Temps</th>
                </tr>
              </thead>
              <tbody className="bg-white divide-y divide-gray-200">
                {rows.map((r) => {
                  const rowKey = [
                    r?.candidatId,
                    r?.modeTest || '',
                    r?.specialite || '',
                    r?.sessionId || r?.rang || ''
                  ].join('-');
                  return (
                  <tr key={rowKey}>
                    <td className="px-6 py-4 whitespace-nowrap font-medium text-gray-900">{r?.rang}</td>
                    <td className="px-6 py-4 whitespace-nowrap text-gray-900">
                      <div className="flex items-center">
                        <Trophy className="h-4 w-4 text-yellow-500 mr-2" />
                        <span className="font-medium">{r?.prenom} {r?.nom}</span>
                      </div>
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-gray-700">{r?.email || '-'}</td>
                    <td className="px-6 py-4 whitespace-nowrap text-gray-700">{r?.ecole || '-'}</td>
                    <td className="px-6 py-4 whitespace-nowrap text-gray-700">{r?.modeTest || '-'}</td>
                    <td className="px-6 py-4 whitespace-nowrap text-gray-700">{r?.specialite || '-'}</td>
                    <td className="px-6 py-4 whitespace-nowrap text-gray-900 font-semibold">
                      {r?.scoreTotal}/{r?.scoreMax}
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-gray-700">
                      <div className="flex items-center">
                        <Clock className="h-4 w-4 mr-2 text-gray-500" />
                        {formatDuration(r?.dureeSecondes)}
                      </div>
                    </td>
                  </tr>
                  );
                })}

                {rows.length === 0 && (
                  <tr>
                    <td colSpan={8} className="px-6 py-10 text-center text-gray-600">
                      Aucun classement disponible pour le moment.
                    </td>
                  </tr>
                )}
              </tbody>
            </table>
          </div>
        </div>
      )}
    </div>
  );
};

export default Classement;
