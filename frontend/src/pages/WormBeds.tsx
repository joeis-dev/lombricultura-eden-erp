import { useEffect, useState } from 'react'
import { Plus, Edit, Trash2, AlertCircle, CheckCircle, Clock, Users } from 'lucide-react'
import { wormBedApi } from '../services/api'
import { useConfirm } from '../components/ConfirmDialog'

interface WormBed {
  id: string
  name: string
  approximateCount: number | null
  lengthMeters: number | null
  widthMeters: number | null
  heightMeters: number | null
  volumeCubicMeters: number | null
  estimatedPopulation: number | null
  populationEstimateBasis: string | null
  lastFeed: string | null
  lastSubtraction: string | null
  modificationCooldownDays: number
  observations: string | null
  status: string
  canModify: boolean
  daysUntilNextModification: number
}

export default function WormBeds() {
  const [beds, setBeds] = useState<WormBed[]>([])
  const [loading, setLoading] = useState(true)
  const [showModal, setShowModal] = useState(false)
  const [editingBed, setEditingBed] = useState<WormBed | null>(null)
  const [formData, setFormData] = useState({
    name: '',
    approximateCount: '',
    lengthMeters: '',
    widthMeters: '',
    heightMeters: '',
    lastFeed: '',
    observations: '',
  })
  const { confirm, ConfirmDialog } = useConfirm()

  useEffect(() => {
    loadBeds()
  }, [])

  const loadBeds = async () => {
    try {
      const response = await wormBedApi.getAll()
      setBeds(response.data)
    } catch (err) {
      console.error('Error loading beds:', err)
    } finally {
      setLoading(false)
    }
  }

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    try {
      const data = {
        name: formData.name,
        approximateCount: formData.approximateCount ? parseFloat(formData.approximateCount) : null,
        lengthMeters: formData.lengthMeters ? parseFloat(formData.lengthMeters) : null,
        widthMeters: formData.widthMeters ? parseFloat(formData.widthMeters) : null,
        heightMeters: formData.heightMeters ? parseFloat(formData.heightMeters) : null,
        lastFeed: formData.lastFeed || null,
        observations: formData.observations || null,
      }

      if (editingBed) {
        await wormBedApi.update(editingBed.id, data)
      } else {
        await wormBedApi.create(data)
      }
      
      setShowModal(false)
      setEditingBed(null)
      setFormData({ name: '', approximateCount: '', lengthMeters: '', widthMeters: '', heightMeters: '', lastFeed: '', observations: '' })
      loadBeds()
    } catch (err) {
      console.error('Error saving bed:', err)
      alert('Error al guardar el lecho. Puede que el nombre ya exista.')
    }
  }

  const handleEdit = (bed: WormBed) => {
    setEditingBed(bed)
    setFormData({
      name: bed.name,
      approximateCount: bed.approximateCount?.toString() || '',
      lengthMeters: bed.lengthMeters?.toString() || '',
      widthMeters: bed.widthMeters?.toString() || '',
      heightMeters: bed.heightMeters?.toString() || '',
      lastFeed: bed.lastFeed || '',
      observations: bed.observations || '',
    })
    setShowModal(true)
  }

  const handleDelete = async (id: string) => {
    const confirmed = await confirm('Eliminar Lecho', '¿Estás seguro de eliminar este lecho de gusanos?')
    if (confirmed) {
      try {
        await wormBedApi.delete(id)
        loadBeds()
      } catch (err) {
        console.error('Error deleting bed:', err)
      }
    }
  }

  const getStatusBadge = (bed: WormBed) => {
    if (!bed.lastSubtraction) {
      return (
        <span className="badge badge-success">
          <CheckCircle size={14} /> Sin restas
        </span>
      )
    }
    
    if (bed.canModify) {
      return (
        <span className="badge badge-success">
          <CheckCircle size={14} /> Listo para modificar
        </span>
      )
    }
    
    return (
      <span className="badge badge-warning">
        <Clock size={14} /> {bed.daysUntilNextModification} días para modificar
      </span>
    )
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
      <ConfirmDialog />
      <div className="card">
        <div className="card-header">
          <h3 className="card-title">Lechos de Gusanos</h3>
          <button className="btn btn-primary" onClick={() => { 
            setShowModal(true); 
            setEditingBed(null); 
            setFormData({ name: '', approximateCount: '', lengthMeters: '', widthMeters: '', heightMeters: '', lastFeed: '', observations: '' }) 
          }}>
            <Plus size={18} /> Nuevo Lecho
          </button>
        </div>

        {beds.length === 0 ? (
          <div className="empty-state">
            <h3>No hay lechos registrados</h3>
            <p>Comienza registrando tu primer lecho de gusanos</p>
          </div>
        ) : (
          <div className="hero-cards-grid">
            {beds.map(bed => (
              <div key={bed.id} className={`hero-card ${!bed.canModify && bed.lastSubtraction ? 'hero-card-warning' : ''}`}>
                <div className="hero-card-header">
                  <h4 className="hero-card-title">{bed.name}</h4>
                  {getStatusBadge(bed)}
                </div>
                
                <div className="hero-card-body">
                  {bed.estimatedPopulation !== null && (
                    <div className="hero-card-stat hero-card-stat-highlight">
                      <span className="hero-card-stat-label">Población Estimada</span>
                      <span className="hero-card-stat-value">
                        <Users size={16} style={{ marginRight: '4px' }} />
                        {bed.estimatedPopulation.toLocaleString('es-ES')} gusanos
                      </span>
                    </div>
                  )}
                  
                  <div className="hero-card-stat">
                    <span className="hero-card-stat-label">Cantidad aprox.</span>
                    <span className="hero-card-stat-value">
                      {bed.approximateCount !== null ? `${bed.approximateCount.toLocaleString('es-ES')} gusanos` : 'No registrado'}
                    </span>
                  </div>
                  
                  {bed.volumeCubicMeters !== null && (
                    <div className="hero-card-stat">
                      <span className="hero-card-stat-label">Volumen</span>
                      <span className="hero-card-stat-value">
                        {bed.volumeCubicMeters.toLocaleString('es-ES')} m³ ({bed.lengthMeters}m × {bed.widthMeters}m × {bed.heightMeters}m)
                      </span>
                    </div>
                  )}
                  
                  <div className="hero-card-stat">
                    <span className="hero-card-stat-label">Última alimentación</span>
                    <span className="hero-card-stat-value">
                      {bed.lastFeed ? new Date(bed.lastFeed).toLocaleDateString('es-ES') : 'No registrada'}
                    </span>
                  </div>
                  
                  <div className="hero-card-stat">
                    <span className="hero-card-stat-label">Última resta</span>
                    <span className="hero-card-stat-value">
                      {bed.lastSubtraction ? new Date(bed.lastSubtraction).toLocaleDateString('es-ES') : 'Ninguna'}
                    </span>
                  </div>
                  
                  {!bed.canModify && bed.lastSubtraction && (
                    <div className="hero-card-alert">
                      <AlertCircle size={16} />
                      <span>Espera {bed.daysUntilNextModification} días para realizar otra resta</span>
                    </div>
                  )}
                  
                  {bed.populationEstimateBasis && (
                    <div className="hero-card-basis" title={bed.populationEstimateBasis}>
                      <small>Base de estimación</small>
                    </div>
                  )}
                  
                  {bed.observations && (
                    <div className="hero-card-observations">
                      <span className="hero-card-stat-label">Observaciones</span>
                      <p>{bed.observations}</p>
                    </div>
                  )}
                </div>
                
                <div className="hero-card-footer">
                  <button className="btn btn-secondary" onClick={() => handleEdit(bed)}>
                    <Edit size={16} /> Editar
                  </button>
                  <button className="btn btn-danger" onClick={() => handleDelete(bed.id)}>
                    <Trash2 size={16} />
                  </button>
                </div>
              </div>
            ))}
          </div>
        )}
      </div>

      {showModal && (
        <div className="modal-overlay" onClick={() => setShowModal(false)}>
          <div className="modal" onClick={(e) => e.stopPropagation()}>
            <div className="modal-header">
              <h3 className="modal-title">{editingBed ? 'Editar Lecho' : 'Nuevo Lecho'}</h3>
              <button className="modal-close" onClick={() => setShowModal(false)}>×</button>
            </div>
            <form onSubmit={handleSubmit}>
              <div className="form-group">
                <label className="form-label">Nombre del Lecho *</label>
                <input
                  type="text"
                  className="form-input"
                  value={formData.name}
                  onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                  placeholder="Ej: Lecho A, Lecho Principal, etc."
                  required
                />
              </div>
              <div className="form-group">
                <label className="form-label">Cantidad Aproximada de Gusanos</label>
                <input
                  type="number"
                  className="form-input"
                  value={formData.approximateCount}
                  onChange={(e) => setFormData({ ...formData, approximateCount: e.target.value })}
                  placeholder="Cantidad aproximada"
                />
              </div>
              <div className="form-group">
                <label className="form-label">Dimensiones (metros)</label>
                <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr 1fr', gap: '0.5rem' }}>
                  <div>
                    <input
                      type="number"
                      step="0.01"
                      className="form-input"
                      value={formData.lengthMeters}
                      onChange={(e) => setFormData({ ...formData, lengthMeters: e.target.value })}
                      placeholder="Largo"
                    />
                    <small style={{ color: 'var(--color-text-light)' }}>Largo (m)</small>
                  </div>
                  <div>
                    <input
                      type="number"
                      step="0.01"
                      className="form-input"
                      value={formData.widthMeters}
                      onChange={(e) => setFormData({ ...formData, widthMeters: e.target.value })}
                      placeholder="Ancho"
                    />
                    <small style={{ color: 'var(--color-text-light)' }}>Ancho (m)</small>
                  </div>
                  <div>
                    <input
                      type="number"
                      step="0.01"
                      className="form-input"
                      value={formData.heightMeters}
                      onChange={(e) => setFormData({ ...formData, heightMeters: e.target.value })}
                      placeholder="Alto"
                    />
                    <small style={{ color: 'var(--color-text-light)' }}>Alto (m)</small>
                  </div>
                </div>
                <small style={{ color: 'var(--color-text-light)', marginTop: '0.25rem', display: 'block' }}>
                  Para calcular población estimada
                </small>
              </div>
              <div className="form-group">
                <label className="form-label">Última Alimentación</label>
                <input
                  type="date"
                  className="form-input"
                  value={formData.lastFeed}
                  onChange={(e) => setFormData({ ...formData, lastFeed: e.target.value })}
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
                  {editingBed ? 'Actualizar' : 'Crear'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  )
}
