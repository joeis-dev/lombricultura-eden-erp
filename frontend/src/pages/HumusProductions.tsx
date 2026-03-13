import { useEffect, useState } from 'react'
import { Plus, Edit, Trash2 } from 'lucide-react'
import { humusProductionApi, harvestBatchApi } from '../services/api'

interface HumusProduction {
  id: string
  productionDate: string
  harvestBatchId: string | null
  harvestBatchInfo: string | null
  quantityLiters: number
  ec: number | null
  ph: number | null
  ppm: number | null
  temperature: number | null
  density: number | null
  observations: string
}

interface HarvestBatch {
  id: string
  harvestDate: string
  quantityKg: number
}

export default function HumusProductions() {
  const [productions, setProductions] = useState<HumusProduction[]>([])
  const [batches, setBatches] = useState<HarvestBatch[]>([])
  const [loading, setLoading] = useState(true)
  const [showModal, setShowModal] = useState(false)
  const [editingProduction, setEditingProduction] = useState<HumusProduction | null>(null)
  const [formData, setFormData] = useState({
    productionDate: '',
    harvestBatchId: '',
    quantityLiters: '',
    ec: '',
    ph: '',
    ppm: '',
    temperature: '',
    density: '',
    observations: '',
  })

  useEffect(() => {
    loadData()
  }, [])

  const loadData = async () => {
    try {
      const [productionsRes, batchesRes] = await Promise.all([
        humusProductionApi.getAll(),
        harvestBatchApi.getAll(),
      ])
      setProductions(productionsRes.data)
      setBatches(batchesRes.data)
    } catch (err) {
      console.error('Error loading data:', err)
    } finally {
      setLoading(false)
    }
  }

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    try {
      const data = {
        productionDate: formData.productionDate,
        harvestBatchId: formData.harvestBatchId || null,
        quantityLiters: parseFloat(formData.quantityLiters),
        ec: formData.ec ? parseFloat(formData.ec) : null,
        ph: formData.ph ? parseFloat(formData.ph) : null,
        ppm: formData.ppm ? parseFloat(formData.ppm) : null,
        temperature: formData.temperature ? parseFloat(formData.temperature) : null,
        density: formData.density ? parseFloat(formData.density) : null,
        observations: formData.observations || null,
      }

      if (editingProduction) {
        await humusProductionApi.update(editingProduction.id, data)
      } else {
        await humusProductionApi.create(data)
      }
      
      setShowModal(false)
      setEditingProduction(null)
      resetForm()
      loadData()
    } catch (err) {
      console.error('Error saving production:', err)
    }
  }

  const handleEdit = (production: HumusProduction) => {
    setEditingProduction(production)
    setFormData({
      productionDate: production.productionDate,
      harvestBatchId: production.harvestBatchId || '',
      quantityLiters: production.quantityLiters.toString(),
      ec: production.ec?.toString() || '',
      ph: production.ph?.toString() || '',
      ppm: production.ppm?.toString() || '',
      temperature: production.temperature?.toString() || '',
      density: production.density?.toString() || '',
      observations: production.observations || '',
    })
    setShowModal(true)
  }

  const handleDelete = async (id: string) => {
    if (confirm('¿Estás seguro de eliminar esta producción?')) {
      try {
        await humusProductionApi.delete(id)
        loadData()
      } catch (err) {
        console.error('Error deleting production:', err)
      }
    }
  }

  const resetForm = () => {
    setFormData({
      productionDate: '',
      harvestBatchId: '',
      quantityLiters: '',
      ec: '',
      ph: '',
      ppm: '',
      temperature: '',
      density: '',
      observations: '',
    })
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
          <h3 className="card-title">Producción de Humus Líquido</h3>
          <button className="btn btn-primary" onClick={() => { setShowModal(true); setEditingProduction(null); resetForm() }}>
            <Plus size={18} /> Nueva Producción
          </button>
        </div>

        {productions.length === 0 ? (
          <div className="empty-state">
            <h3>No hay producciones registradas</h3>
            <p>Registra tu primera producción de humus líquido</p>
          </div>
        ) : (
          <div className="table-container">
            <table>
              <thead>
                <tr>
                  <th>Fecha</th>
                  <th>Lote</th>
                  <th>Cantidad (L)</th>
                  <th>EC</th>
                  <th>pH</th>
                  <th>PPM</th>
                  <th>Temp</th>
                  <th>Acciones</th>
                </tr>
              </thead>
              <tbody>
                {productions.map((p) => (
                  <tr key={p.id}>
                    <td>{new Date(p.productionDate).toLocaleDateString('es-ES')}</td>
                    <td>{p.harvestBatchInfo || '-'}</td>
                    <td>{p.quantityLiters}</td>
                    <td>{p.ec || '-'}</td>
                    <td>{p.ph || '-'}</td>
                    <td>{p.ppm || '-'}</td>
                    <td>{p.temperature ? `${p.temperature}°C` : '-'}</td>
                    <td>
                      <div className="actions">
                        <button className="btn btn-secondary" onClick={() => handleEdit(p)}>
                          <Edit size={16} />
                        </button>
                        <button className="btn btn-danger" onClick={() => handleDelete(p.id)}>
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
          <div className="modal" onClick={(e) => e.stopPropagation()} style={{ maxWidth: '600px' }}>
            <div className="modal-header">
              <h3 className="modal-title">{editingProduction ? 'Editar Producción' : 'Nueva Producción'}</h3>
              <button className="modal-close" onClick={() => setShowModal(false)}>×</button>
            </div>
            <form onSubmit={handleSubmit}>
              <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '1rem' }}>
                <div className="form-group">
                  <label className="form-label">Fecha de Producción</label>
                  <input
                    type="date"
                    className="form-input"
                    value={formData.productionDate}
                    onChange={(e) => setFormData({ ...formData, productionDate: e.target.value })}
                    required
                  />
                </div>
                <div className="form-group">
                  <label className="form-label">Lote de Cosecha</label>
                  <select
                    className="form-select"
                    value={formData.harvestBatchId}
                    onChange={(e) => setFormData({ ...formData, harvestBatchId: e.target.value })}
                  >
                    <option value="">Sin lote</option>
                    {batches.map((b) => (
                      <option key={b.id} value={b.id}>
                        {new Date(b.harvestDate).toLocaleDateString('es-ES')} - {b.quantityKg}kg
                      </option>
                    ))}
                  </select>
                </div>
              </div>
              <div className="form-group">
                <label className="form-label">Cantidad (Litros)</label>
                <input
                  type="number"
                  step="0.01"
                  className="form-input"
                  value={formData.quantityLiters}
                  onChange={(e) => setFormData({ ...formData, quantityLiters: e.target.value })}
                  required
                />
              </div>
              <div style={{ display: 'grid', gridTemplateColumns: 'repeat(3, 1fr)', gap: '1rem' }}>
                <div className="form-group">
                  <label className="form-label">EC (dS/m)</label>
                  <input
                    type="number"
                    step="0.01"
                    className="form-input"
                    value={formData.ec}
                    onChange={(e) => setFormData({ ...formData, ec: e.target.value })}
                  />
                </div>
                <div className="form-group">
                  <label className="form-label">pH</label>
                  <input
                    type="number"
                    step="0.01"
                    className="form-input"
                    value={formData.ph}
                    onChange={(e) => setFormData({ ...formData, ph: e.target.value })}
                  />
                </div>
                <div className="form-group">
                  <label className="form-label">PPM</label>
                  <input
                    type="number"
                    step="0.01"
                    className="form-input"
                    value={formData.ppm}
                    onChange={(e) => setFormData({ ...formData, ppm: e.target.value })}
                  />
                </div>
              </div>
              <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '1rem' }}>
                <div className="form-group">
                  <label className="form-label">Temperatura (°C)</label>
                  <input
                    type="number"
                    step="0.1"
                    className="form-input"
                    value={formData.temperature}
                    onChange={(e) => setFormData({ ...formData, temperature: e.target.value })}
                  />
                </div>
                <div className="form-group">
                  <label className="form-label">Densidad</label>
                  <input
                    type="number"
                    step="0.001"
                    className="form-input"
                    value={formData.density}
                    onChange={(e) => setFormData({ ...formData, density: e.target.value })}
                  />
                </div>
              </div>
              <div className="form-group">
                <label className="form-label">Observaciones</label>
                <textarea
                  className="form-input"
                  rows={2}
                  value={formData.observations}
                  onChange={(e) => setFormData({ ...formData, observations: e.target.value })}
                />
              </div>
              <div style={{ display: 'flex', gap: '1rem', justifyContent: 'flex-end' }}>
                <button type="button" className="btn btn-secondary" onClick={() => setShowModal(false)}>
                  Cancelar
                </button>
                <button type="submit" className="btn btn-primary">
                  {editingProduction ? 'Actualizar' : 'Crear'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  )
}
