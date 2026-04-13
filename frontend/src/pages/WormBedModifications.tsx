import { useEffect, useState } from 'react'
import { Plus, Trash2, ArrowUp, ArrowDown } from 'lucide-react'
import { wormBedApi, wormBedModificationApi } from '../services/api'
import { useConfirm } from '../components/ConfirmDialog'

interface WormBed {
  id: string
  name: string
  canModify: boolean
  daysUntilNextModification: number
}

interface WormBedModification {
  id: string
  wormBedId: string
  wormBedName: string
  modificationDate: string
  modificationType: string
  quantity: number | null
  description: string | null
}

export default function WormBedModifications() {
  const [beds, setBeds] = useState<WormBed[]>([])
  const [modifications, setModifications] = useState<WormBedModification[]>([])
  const [loading, setLoading] = useState(true)
  const [showModal, setShowModal] = useState(false)
  const [formData, setFormData] = useState({
    wormBedId: '',
    modificationDate: new Date().toISOString().split('T')[0],
    modificationType: 'SUBSTRACTION',
    quantity: '',
    description: '',
  })
  const { confirm, ConfirmDialog } = useConfirm()

  useEffect(() => {
    loadData()
  }, [])

  const loadData = async () => {
    try {
      const [bedsRes, modsRes] = await Promise.all([
        wormBedApi.getAll(),
        wormBedModificationApi.getAll()
      ])
      setBeds(bedsRes.data)
      setModifications(modsRes.data)
    } catch (err) {
      console.error('Error loading data:', err)
    } finally {
      setLoading(false)
    }
  }

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    try {
      const selectedBed = beds.find(b => b.id === formData.wormBedId)
      
      if (formData.modificationType === 'SUBSTRACTION' && selectedBed && !selectedBed.canModify) {
        alert(`Este lecho aún está en periodo de espera. Debes esperar ${selectedBed.daysUntilNextModification} días.`)
        return
      }
      
      const data = {
        wormBedId: formData.wormBedId,
        modificationDate: formData.modificationDate,
        modificationType: formData.modificationType,
        quantity: formData.quantity ? parseFloat(formData.quantity) : null,
        description: formData.description || null,
      }

      await wormBedModificationApi.create(data)
      
      setShowModal(false)
      setFormData({
        wormBedId: '',
        modificationDate: new Date().toISOString().split('T')[0],
        modificationType: 'SUBSTRACTION',
        quantity: '',
        description: '',
      })
      loadData()
    } catch (err) {
      console.error('Error saving modification:', err)
      alert('Error al registrar la modificación.')
    }
  }

  const handleDelete = async (id: string) => {
    const confirmed = await confirm('Eliminar Registro', '¿Estás seguro de eliminar este registro de modificación?')
    if (confirmed) {
      try {
        await wormBedModificationApi.delete(id)
        loadData()
      } catch (err) {
        console.error('Error deleting modification:', err)
      }
    }
  }

  const getTypeIcon = (type: string) => {
    if (type === 'ADDITION') {
      return <span className="badge badge-success"><ArrowUp size={14} /> Adición</span>
    }
    return <span className="badge badge-danger"><ArrowDown size={14} /> Resta</span>
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
          <h3 className="card-title">Registro de Modificaciones</h3>
          <button className="btn btn-primary" onClick={() => setShowModal(true)}>
            <Plus size={18} /> Nueva Modificación
          </button>
        </div>

        <div style={{ padding: '1rem', backgroundColor: 'var(--color-bg-secondary)', borderRadius: '8px', marginBottom: '1rem' }}>
          <p style={{ margin: 0, color: 'var(--color-text-light)', fontSize: '0.875rem' }}>
            Registra aquí las adiciones o restas de gusanos en los lechos. Las restas activarán el periodo de espera 
            configurado para cada lecho antes de poder realizar otra resta.
          </p>
        </div>

        {modifications.length === 0 ? (
          <div className="empty-state">
            <h3>No hay modificaciones registradas</h3>
            <p>Registra tu primera adición o resta de gusanos</p>
          </div>
        ) : (
          <div className="modifications-list">
            {modifications.map(mod => (
              <div key={mod.id} className="modification-item">
                <div className="modification-item-header">
                  <div className="modification-item-info">
                    <strong>{mod.wormBedName}</strong>
                    {getTypeIcon(mod.modificationType)}
                  </div>
                  <div className="modification-item-actions">
                    <button className="btn btn-danger btn-sm" onClick={() => handleDelete(mod.id)}>
                      <Trash2 size={14} />
                    </button>
                  </div>
                </div>
                <div className="modification-item-details">
                  <div className="modification-detail">
                    <span className="modification-detail-label">Fecha:</span>
                    <span>{new Date(mod.modificationDate).toLocaleDateString('es-ES')}</span>
                  </div>
                  {mod.quantity && (
                    <div className="modification-detail">
                      <span className="modification-detail-label">Cantidad:</span>
                      <span>{mod.quantity.toLocaleString('es-ES')}</span>
                    </div>
                  )}
                </div>
                {mod.description && (
                  <div className="modification-item-description">
                    {mod.description}
                  </div>
                )}
              </div>
            ))}
          </div>
        )}
      </div>

      {showModal && (
        <div className="modal-overlay" onClick={() => setShowModal(false)}>
          <div className="modal" onClick={(e) => e.stopPropagation()}>
            <div className="modal-header">
              <h3 className="modal-title">Nueva Modificación</h3>
              <button className="modal-close" onClick={() => setShowModal(false)}>×</button>
            </div>
            <form onSubmit={handleSubmit}>
              <div className="form-group">
                <label className="form-label">Lecho de Gusanos *</label>
                <select
                  className="form-input"
                  value={formData.wormBedId}
                  onChange={(e) => setFormData({ ...formData, wormBedId: e.target.value })}
                  required
                >
                  <option value="">Selecciona un lecho</option>
                  {beds.map(bed => (
                    <option key={bed.id} value={bed.id}>
                      {bed.name}
                      {!bed.canModify && ` (Espera ${bed.daysUntilNextModification} días)`}
                    </option>
                  ))}
                </select>
              </div>
              
              <div className="form-group">
                <label className="form-label">Tipo de Modificación *</label>
                <div className="radio-group">
                  <label className={`radio-option ${formData.modificationType === 'SUBSTRACTION' ? 'selected' : ''}`}>
                    <input
                      type="radio"
                      name="modificationType"
                      value="SUBSTRACTION"
                      checked={formData.modificationType === 'SUBSTRACTION'}
                      onChange={(e) => setFormData({ ...formData, modificationType: e.target.value })}
                    />
                    <ArrowDown size={16} />
                    <span>Resta (extracción de gusanos)</span>
                  </label>
                  <label className={`radio-option ${formData.modificationType === 'ADDITION' ? 'selected' : ''}`}>
                    <input
                      type="radio"
                      name="modificationType"
                      value="ADDITION"
                      checked={formData.modificationType === 'ADDITION'}
                      onChange={(e) => setFormData({ ...formData, modificationType: e.target.value })}
                    />
                    <ArrowUp size={16} />
                    <span>Adición (incorporación de gusanos)</span>
                  </label>
                </div>
              </div>
              
              <div className="form-group">
                <label className="form-label">Fecha de Modificación *</label>
                <input
                  type="date"
                  className="form-input"
                  value={formData.modificationDate}
                  onChange={(e) => setFormData({ ...formData, modificationDate: e.target.value })}
                  required
                />
              </div>
              
              <div className="form-group">
                <label className="form-label">Cantidad de Gusanos</label>
                <input
                  type="number"
                  className="form-input"
                  value={formData.quantity}
                  onChange={(e) => setFormData({ ...formData, quantity: e.target.value })}
                  placeholder="Cantidad afectada"
                />
              </div>
              
              <div className="form-group">
                <label className="form-label">Descripción</label>
                <textarea
                  className="form-input"
                  rows={3}
                  value={formData.description}
                  onChange={(e) => setFormData({ ...formData, description: e.target.value })}
                  placeholder="Ej: Cosecha para venta, Incorporación de nueva camada, etc."
                />
              </div>
              
              {formData.wormBedId && formData.modificationType === 'SUBSTRACTION' && (
                (() => {
                  const selectedBed = beds.find(b => b.id === formData.wormBedId)
                  if (selectedBed && !selectedBed.canModify) {
                    return (
                      <div className="alert alert-warning" style={{ marginBottom: '1rem' }}>
                        <strong>Advertencia:</strong> Este lecho está en periodo de espera. 
                        Podrás realizar otra resta en {selectedBed.daysUntilNextModification} días.
                      </div>
                    )
                  }
                  return null
                })()
              )}
              
              <div style={{ display: 'flex', gap: '1rem', justifyContent: 'flex-end' }}>
                <button type="button" className="btn btn-secondary" onClick={() => setShowModal(false)}>
                  Cancelar
                </button>
                <button type="submit" className="btn btn-primary">
                  Registrar
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  )
}
