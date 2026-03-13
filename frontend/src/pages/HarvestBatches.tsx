import { useEffect, useState } from 'react'
import { Plus, Edit, Trash2 } from 'lucide-react'
import { harvestBatchApi } from '../services/api'

interface HarvestBatch {
  id: string
  harvestDate: string
  quantityKg: number
  averageWeight: number
  observations: string
}

export default function HarvestBatches() {
  const [batches, setBatches] = useState<HarvestBatch[]>([])
  const [loading, setLoading] = useState(true)
  const [showModal, setShowModal] = useState(false)
  const [editingBatch, setEditingBatch] = useState<HarvestBatch | null>(null)
  const [formData, setFormData] = useState({
    harvestDate: '',
    quantityKg: '',
    averageWeight: '',
    observations: '',
  })

  useEffect(() => {
    loadBatches()
  }, [])

  const loadBatches = async () => {
    try {
      const response = await harvestBatchApi.getAll()
      setBatches(response.data)
    } catch (err) {
      console.error('Error loading batches:', err)
    } finally {
      setLoading(false)
    }
  }

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    try {
      const data = {
        harvestDate: formData.harvestDate,
        quantityKg: parseFloat(formData.quantityKg),
        averageWeight: formData.averageWeight ? parseFloat(formData.averageWeight) : null,
        observations: formData.observations || null,
      }

      if (editingBatch) {
        await harvestBatchApi.update(editingBatch.id, data)
      } else {
        await harvestBatchApi.create(data)
      }
      
      setShowModal(false)
      setEditingBatch(null)
      setFormData({ harvestDate: '', quantityKg: '', averageWeight: '', observations: '' })
      loadBatches()
    } catch (err) {
      console.error('Error saving batch:', err)
    }
  }

  const handleEdit = (batch: HarvestBatch) => {
    setEditingBatch(batch)
    setFormData({
      harvestDate: batch.harvestDate,
      quantityKg: batch.quantityKg.toString(),
      averageWeight: batch.averageWeight?.toString() || '',
      observations: batch.observations || '',
    })
    setShowModal(true)
  }

  const handleDelete = async (id: string) => {
    if (confirm('¿Estás seguro de eliminar este lote?')) {
      try {
        await harvestBatchApi.delete(id)
        loadBatches()
      } catch (err) {
        console.error('Error deleting batch:', err)
      }
    }
  }

  if (loading) {
    return (
      <div className="loading">
        <div className="spinner"></div>
      </div>
    )
  }

  return (
    <div>
      <div className="card">
        <div className="card-header">
          <h3 className="card-title">Lotes de Cosecha</h3>
          <button className="btn btn-primary" onClick={() => { setShowModal(true); setEditingBatch(null); setFormData({ harvestDate: '', quantityKg: '', averageWeight: '', observations: '' }) }}>
            <Plus size={18} /> Nuevo Lote
          </button>
        </div>

        {batches.length === 0 ? (
          <div className="empty-state">
            <h3>No hay lotes registrados</h3>
            <p>Comienza registrando tu primer lote de cosecha</p>
          </div>
        ) : (
          <div className="table-container">
            <table>
              <thead>
                <tr>
                  <th>Fecha</th>
                  <th>Cantidad (kg)</th>
                  <th>Peso Promedio</th>
                  <th>Observaciones</th>
                  <th>Acciones</th>
                </tr>
              </thead>
              <tbody>
                {batches.map((batch) => (
                  <tr key={batch.id}>
                    <td>{new Date(batch.harvestDate).toLocaleDateString('es-ES')}</td>
                    <td>{batch.quantityKg}</td>
                    <td>{batch.averageWeight || '-'}</td>
                    <td>{batch.observations || '-'}</td>
                    <td>
                      <div className="actions">
                        <button className="btn btn-secondary" onClick={() => handleEdit(batch)}>
                          <Edit size={16} />
                        </button>
                        <button className="btn btn-danger" onClick={() => handleDelete(batch.id)}>
                          <Trash2 size={16} />
                        </button>
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>

      {showModal && (
        <div className="modal-overlay" onClick={() => setShowModal(false)}>
          <div className="modal" onClick={(e) => e.stopPropagation()}>
            <div className="modal-header">
              <h3 className="modal-title">{editingBatch ? 'Editar Lote' : 'Nuevo Lote'}</h3>
              <button className="modal-close" onClick={() => setShowModal(false)}>×</button>
            </div>
            <form onSubmit={handleSubmit}>
              <div className="form-group">
                <label className="form-label">Fecha de Cosecha</label>
                <input
                  type="date"
                  className="form-input"
                  value={formData.harvestDate}
                  onChange={(e) => setFormData({ ...formData, harvestDate: e.target.value })}
                  required
                />
              </div>
              <div className="form-group">
                <label className="form-label">Cantidad (kg)</label>
                <input
                  type="number"
                  step="0.01"
                  className="form-input"
                  value={formData.quantityKg}
                  onChange={(e) => setFormData({ ...formData, quantityKg: e.target.value })}
                  required
                />
              </div>
              <div className="form-group">
                <label className="form-label">Peso Promedio</label>
                <input
                  type="number"
                  step="0.01"
                  className="form-input"
                  value={formData.averageWeight}
                  onChange={(e) => setFormData({ ...formData, averageWeight: e.target.value })}
                />
              </div>
              <div className="form-group">
                <label className="form-label">Observaciones</label>
                <textarea
                  className="form-input"
                  rows={3}
                  value={formData.observations}
                  onChange={(e) => setFormData({ ...formData, observations: e.target.value })}
                />
              </div>
              <div style={{ display: 'flex', gap: '1rem', justifyContent: 'flex-end' }}>
                <button type="button" className="btn btn-secondary" onClick={() => setShowModal(false)}>
                  Cancelar
                </button>
                <button type="submit" className="btn btn-primary">
                  {editingBatch ? 'Actualizar' : 'Crear'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  )
}
