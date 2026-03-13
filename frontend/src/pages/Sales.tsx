import { useEffect, useState } from 'react'
import { Plus } from 'lucide-react'
import { saleApi, productApi, customerApi } from '../services/api'

interface Sale {
  id: string
  customerId: string | null
  customerName: string | null
  saleDate: string
  total: number
  status: string
  observations: string
}

interface Product {
  id: string
  name: string
  currentStock: number
  unitPrice: number
  unitOfMeasure: string
}

interface Customer {
  id: string
  name: string
}

export default function Sales() {
  const [sales, setSales] = useState<Sale[]>([])
  const [products, setProducts] = useState<Product[]>([])
  const [customers, setCustomers] = useState<Customer[]>([])
  const [loading, setLoading] = useState(true)
  const [showModal, setShowModal] = useState(false)
  const [formData, setFormData] = useState({
    customerId: '',
    saleDate: new Date().toISOString().split('T')[0],
    status: 'PENDING',
    observations: '',
    details: [{ productId: '', quantity: '', unitPrice: '' }],
  })

  useEffect(() => {
    loadData()
  }, [])

  const loadData = async () => {
    try {
      const [salesRes, productsRes, customersRes] = await Promise.all([
        saleApi.getAll(),
        productApi.getAll(),
        customerApi.getAll(),
      ])
      setSales(salesRes.data)
      setProducts(productsRes.data)
      setCustomers(customersRes.data)
    } catch (err) {
      console.error('Error loading data:', err)
    } finally {
      setLoading(false)
    }
  }

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    try {
      const details = formData.details
        .filter(d => d.productId && d.quantity && d.unitPrice)
        .map(d => ({
          productId: d.productId,
          quantity: parseFloat(d.quantity),
          unitPrice: parseFloat(d.unitPrice),
        }))

      const data = {
        customerId: formData.customerId || null,
        saleDate: new Date(formData.saleDate).toISOString(),
        status: formData.status,
        observations: formData.observations || null,
        details,
      }

      await saleApi.create(data)
      setShowModal(false)
      resetForm()
      loadData()
    } catch (err) {
      console.error('Error creating sale:', err)
    }
  }

  const resetForm = () => {
    setFormData({
      customerId: '',
      saleDate: new Date().toISOString().split('T')[0],
      status: 'PENDING',
      observations: '',
      details: [{ productId: '', quantity: '', unitPrice: '' }],
    })
  }

  const addDetail = () => {
    setFormData({
      ...formData,
      details: [...formData.details, { productId: '', quantity: '', unitPrice: '' }],
    })
  }

  const removeDetail = (index: number) => {
    if (formData.details.length > 1) {
      const newDetails = formData.details.filter((_, i) => i !== index)
      setFormData({ ...formData, details: newDetails })
    }
  }

  const updateDetail = (index: number, field: string, value: string) => {
    const newDetails = [...formData.details]
    newDetails[index] = { ...newDetails[index], [field]: value }
    
    if (field === 'productId') {
      const product = products.find(p => p.id === value)
      if (product) {
        newDetails[index].unitPrice = product.unitPrice.toString()
      }
    }
    
    setFormData({ ...formData, details: newDetails })
  }

  const getStatusBadge = (status: string) => {
    const classes: Record<string, string> = {
      PENDING: 'badge-warning',
      PAID: 'badge-success',
      CANCELLED: 'badge-error',
    }
    const labels: Record<string, string> = {
      PENDING: 'Pendiente',
      PAID: 'Pagada',
      CANCELLED: 'Cancelada',
    }
    return <span className={`badge ${classes[status]}`}>{labels[status]}</span>
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
          <h3 className="card-title">Ventas</h3>
          <button className="btn btn-primary" onClick={() => setShowModal(true)}>
            <Plus size={18} /> Nueva Venta
          </button>
        </div>

        {sales.length === 0 ? (
          <div className="empty-state">
            <h3>No hay ventas registradas</h3>
            <p>Registra tu primera venta</p>
          </div>
        ) : (
          <div className="table-container">
            <table>
              <thead>
                <tr>
                  <th>Fecha</th>
                  <th>Cliente</th>
                  <th>Total</th>
                  <th>Estado</th>
                  <th>Observaciones</th>
                </tr>
              </thead>
              <tbody>
                {sales.map((sale) => (
                  <tr key={sale.id}>
                    <td>{new Date(sale.saleDate).toLocaleDateString('es-ES')}</td>
                    <td>{sale.customerName || 'Sin cliente'}</td>
                    <td>${sale.total.toFixed(2)}</td>
                    <td>{getStatusBadge(sale.status)}</td>
                    <td>{sale.observations || '-'}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>

      {showModal && (
        <div className="modal-overlay" onClick={() => setShowModal(false)}>
          <div className="modal" onClick={(e) => e.stopPropagation()} style={{ maxWidth: '700px' }}>
            <div className="modal-header">
              <h3 className="modal-title">Nueva Venta</h3>
              <button className="modal-close" onClick={() => setShowModal(false)}>×</button>
            </div>
            <form onSubmit={handleSubmit}>
              <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '1rem' }}>
                <div className="form-group">
                  <label className="form-label">Cliente</label>
                  <select
                    className="form-select"
                    value={formData.customerId}
                    onChange={(e) => setFormData({ ...formData, customerId: e.target.value })}
                  >
                    <option value="">Sin cliente</option>
                    {customers.map((c) => (
                      <option key={c.id} value={c.id}>{c.name}</option>
                    ))}
                  </select>
                </div>
                <div className="form-group">
                  <label className="form-label">Fecha</label>
                  <input
                    type="date"
                    className="form-input"
                    value={formData.saleDate}
                    onChange={(e) => setFormData({ ...formData, saleDate: e.target.value })}
                    required
                  />
                </div>
              </div>
              
              <div className="form-group">
                <label className="form-label">Productos</label>
                {formData.details.map((detail, index) => (
                  <div key={index} style={{ display: 'grid', gridTemplateColumns: '2fr 1fr 1fr auto', gap: '0.5rem', marginBottom: '0.5rem' }}>
                    <select
                      className="form-select"
                      value={detail.productId}
                      onChange={(e) => updateDetail(index, 'productId', e.target.value)}
                      required
                    >
                      <option value="">Seleccionar</option>
                      {products.map((p) => (
                        <option key={p.id} value={p.id}>{p.name} ({p.currentStock} {p.unitOfMeasure})</option>
                      ))}
                    </select>
                    <input
                      type="number"
                      step="0.01"
                      className="form-input"
                      placeholder="Cantidad"
                      value={detail.quantity}
                      onChange={(e) => updateDetail(index, 'quantity', e.target.value)}
                      required
                    />
                    <input
                      type="number"
                      step="0.01"
                      className="form-input"
                      placeholder="Precio"
                      value={detail.unitPrice}
                      onChange={(e) => updateDetail(index, 'unitPrice', e.target.value)}
                      required
                    />
                    <button type="button" className="btn btn-danger" onClick={() => removeDetail(index)}>×</button>
                  </div>
                ))}
                <button type="button" className="btn btn-secondary" onClick={addDetail}>
                  + Agregar Producto
                </button>
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
                  Crear Venta
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  )
}
