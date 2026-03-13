import { useEffect, useState } from 'react'
import { Plus, Edit, Trash2 } from 'lucide-react'
import { customerApi } from '../services/api'

interface Customer {
  id: string
  name: string
  document: string | null
  documentType: string | null
  email: string | null
  phone: string | null
  address: string | null
}

export default function Customers() {
  const [customers, setCustomers] = useState<Customer[]>([])
  const [loading, setLoading] = useState(true)
  const [showModal, setShowModal] = useState(false)
  const [editingCustomer, setEditingCustomer] = useState<Customer | null>(null)
  const [formData, setFormData] = useState({
    name: '',
    document: '',
    documentType: 'CI',
    email: '',
    phone: '',
    address: '',
  })

  useEffect(() => {
    loadCustomers()
  }, [])

  const loadCustomers = async () => {
    try {
      const response = await customerApi.getAll()
      setCustomers(response.data)
    } catch (err) {
      console.error('Error loading customers:', err)
    } finally {
      setLoading(false)
    }
  }

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    try {
      const data = {
        name: formData.name,
        document: formData.document || null,
        documentType: formData.documentType || null,
        email: formData.email || null,
        phone: formData.phone || null,
        address: formData.address || null,
      }

      if (editingCustomer) {
        await customerApi.update(editingCustomer.id, data)
      } else {
        await customerApi.create(data)
      }
      
      setShowModal(false)
      setEditingCustomer(null)
      resetForm()
      loadCustomers()
    } catch (err) {
      console.error('Error saving customer:', err)
    }
  }

  const handleEdit = (customer: Customer) => {
    setEditingCustomer(customer)
    setFormData({
      name: customer.name,
      document: customer.document || '',
      documentType: customer.documentType || 'CI',
      email: customer.email || '',
      phone: customer.phone || '',
      address: customer.address || '',
    })
    setShowModal(true)
  }

  const handleDelete = async (id: string) => {
    if (confirm('¿Estás seguro de eliminar este cliente?')) {
      try {
        await customerApi.delete(id)
        loadCustomers()
      } catch (err) {
        console.error('Error deleting customer:', err)
      }
    }
  }

  const resetForm = () => {
    setFormData({
      name: '',
      document: '',
      documentType: 'CI',
      email: '',
      phone: '',
      address: '',
    })
  }

  const getDocTypeLabel = (type: string | null) => {
    const labels: Record<string, string> = {
      CI: 'Cédula',
      RUC: 'RUC',
      PASSPORT: 'Pasaporte',
    }
    return type ? labels[type] : '-'
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
          <h3 className="card-title">Clientes</h3>
          <button className="btn btn-primary" onClick={() => { setShowModal(true); setEditingCustomer(null); resetForm() }}>
            <Plus size={18} /> Nuevo Cliente
          </button>
        </div>

        {customers.length === 0 ? (
          <div className="empty-state">
            <h3>No hay clientes registrados</h3>
            <p>Registra tu primer cliente</p>
          </div>
        ) : (
          <div className="table-container">
            <table>
              <thead>
                <tr>
                  <th>Nombre</th>
                  <th>Documento</th>
                  <th>Email</th>
                  <th>Teléfono</th>
                  <th>Dirección</th>
                  <th>Acciones</th>
                </tr>
              </thead>
              <tbody>
                {customers.map((customer) => (
                  <tr key={customer.id}>
                    <td>{customer.name}</td>
                    <td>{customer.document ? `${getDocTypeLabel(customer.documentType)}: ${customer.document}` : '-'}</td>
                    <td>{customer.email || '-'}</td>
                    <td>{customer.phone || '-'}</td>
                    <td>{customer.address || '-'}</td>
                    <td>
                      <div className="actions">
                        <button className="btn btn-secondary" onClick={() => handleEdit(customer)}>
                          <Edit size={16} />
                        </button>
                        <button className="btn btn-danger" onClick={() => handleDelete(customer.id)}>
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
              <h3 className="modal-title">{editingCustomer ? 'Editar Cliente' : 'Nuevo Cliente'}</h3>
              <button className="modal-close" onClick={() => setShowModal(false)}>×</button>
            </div>
            <form onSubmit={handleSubmit}>
              <div className="form-group">
                <label className="form-label">Nombre</label>
                <input
                  type="text"
                  className="form-input"
                  value={formData.name}
                  onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                  required
                />
              </div>
              <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '1rem' }}>
                <div className="form-group">
                  <label className="form-label">Tipo de Documento</label>
                  <select
                    className="form-select"
                    value={formData.documentType}
                    onChange={(e) => setFormData({ ...formData, documentType: e.target.value })}
                  >
                    <option value="CI">Cédula de Identidad</option>
                    <option value="RUC">RUC</option>
                    <option value="PASSPORT">Pasaporte</option>
                  </select>
                </div>
                <div className="form-group">
                  <label className="form-label">Número de Documento</label>
                  <input
                    type="text"
                    className="form-input"
                    value={formData.document}
                    onChange={(e) => setFormData({ ...formData, document: e.target.value })}
                  />
                </div>
              </div>
              <div className="form-group">
                <label className="form-label">Email</label>
                <input
                  type="email"
                  className="form-input"
                  value={formData.email}
                  onChange={(e) => setFormData({ ...formData, email: e.target.value })}
                />
              </div>
              <div className="form-group">
                <label className="form-label">Teléfono</label>
                <input
                  type="tel"
                  className="form-input"
                  value={formData.phone}
                  onChange={(e) => setFormData({ ...formData, phone: e.target.value })}
                />
              </div>
              <div className="form-group">
                <label className="form-label">Dirección</label>
                <textarea
                  className="form-input"
                  rows={2}
                  value={formData.address}
                  onChange={(e) => setFormData({ ...formData, address: e.target.value })}
                />
              </div>
              <div style={{ display: 'flex', gap: '1rem', justifyContent: 'flex-end' }}>
                <button type="button" className="btn btn-secondary" onClick={() => setShowModal(false)}>
                  Cancelar
                </button>
                <button type="submit" className="btn btn-primary">
                  {editingCustomer ? 'Actualizar' : 'Crear'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  )
}
