import { useEffect, useState } from 'react'
import { Plus, Edit, Trash2, AlertTriangle } from 'lucide-react'
import { productApi, inventoryMovementApi } from '../services/api'

interface Product {
  id: string
  name: string
  type: string
  description: string
  unitOfMeasure: string
  currentStock: number
  minimumStock: number
  unitPrice: number
  unitCost: number
  volumePerUnit: number
  lowStock: boolean
}

export default function Inventory() {
  const [products, setProducts] = useState<Product[]>([])
  const [loading, setLoading] = useState(true)
  const [showModal, setShowModal] = useState(false)
  const [showMovementModal, setShowMovementModal] = useState(false)
  const [editingProduct, setEditingProduct] = useState<Product | null>(null)
  const [selectedProduct, setSelectedProduct] = useState<Product | null>(null)
  const [formData, setFormData] = useState({
    name: '',
    type: 'HUMUS_LIQUID',
    description: '',
    unitOfMeasure: 'L',
    currentStock: '',
    minimumStock: '',
    unitPrice: '',
    unitCost: '',
    volumePerUnit: '1',
  })
  const [movementData, setMovementData] = useState({
    movementType: 'IN',
    quantity: '',
    reason: '',
  })

  useEffect(() => {
    loadProducts()
  }, [])

  const loadProducts = async () => {
    try {
      const response = await productApi.getAll()
      setProducts(response.data)
    } catch (err) {
      console.error('Error loading products:', err)
    } finally {
      setLoading(false)
    }
  }

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    try {
      const data = {
        name: formData.name,
        type: formData.type,
        description: formData.description || null,
        unitOfMeasure: formData.unitOfMeasure,
        currentStock: formData.currentStock ? parseFloat(formData.currentStock) : 0,
        minimumStock: formData.minimumStock ? parseFloat(formData.minimumStock) : 0,
        unitPrice: formData.unitPrice ? parseFloat(formData.unitPrice) : 0,
        unitCost: formData.unitCost ? parseFloat(formData.unitCost) : 0,
        volumePerUnit: formData.volumePerUnit ? parseFloat(formData.volumePerUnit) : 1,
      }

      if (editingProduct) {
        await productApi.update(editingProduct.id, data)
      } else {
        await productApi.create(data)
      }
      
      setShowModal(false)
      setEditingProduct(null)
      resetForm()
      loadProducts()
    } catch (err) {
      console.error('Error saving product:', err)
    }
  }

  const handleMovement = async (e: React.FormEvent) => {
    e.preventDefault()
    if (!selectedProduct) return
    try {
      await inventoryMovementApi.create({
        productId: selectedProduct.id,
        movementType: movementData.movementType,
        quantity: parseFloat(movementData.quantity),
        reason: movementData.reason || null,
      })
      setShowMovementModal(false)
      setSelectedProduct(null)
      setMovementData({ movementType: 'IN', quantity: '', reason: '' })
      loadProducts()
    } catch (err) {
      console.error('Error creating movement:', err)
    }
  }

  const handleEdit = (product: Product) => {
    setEditingProduct(product)
    setFormData({
      name: product.name,
      type: product.type,
      description: product.description || '',
      unitOfMeasure: product.unitOfMeasure,
      currentStock: product.currentStock.toString(),
      minimumStock: product.minimumStock.toString(),
      unitPrice: product.unitPrice.toString(),
      unitCost: product.unitCost.toString(),
      volumePerUnit: product.volumePerUnit?.toString() || '1',
    })
    setShowModal(true)
  }

  const handleDelete = async (id: string) => {
    if (confirm('¿Estás seguro de eliminar este producto?')) {
      try {
        await productApi.delete(id)
        loadProducts()
      } catch (err) {
        console.error('Error deleting product:', err)
      }
    }
  }

  const resetForm = () => {
    setFormData({
      name: '',
      type: 'HUMUS_LIQUID',
      description: '',
      unitOfMeasure: 'L',
      currentStock: '',
      minimumStock: '',
      unitPrice: '',
      unitCost: '',
      volumePerUnit: '1',
    })
  }

  const openMovementModal = (product: Product) => {
    setSelectedProduct(product)
    setMovementData({ movementType: 'IN', quantity: '', reason: '' })
    setShowMovementModal(true)
  }

  const getTypeLabel = (type: string) => {
    const labels: Record<string, string> = {
      HUMUS_LIQUID: 'Humus Líquido',
      HUMUS_SOLID: 'Humus Sólido',
      WORM: 'Lombriz',
      SUPPLY: 'Insumo',
    }
    return labels[type] || type
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
          <h3 className="card-title">Inventario de Productos</h3>
          <button className="btn btn-primary" onClick={() => { setShowModal(true); setEditingProduct(null); resetForm() }}>
            <Plus size={18} /> Nuevo Producto
          </button>
        </div>

        {products.length === 0 ? (
          <div className="empty-state">
            <h3>No hay productos registrados</h3>
            <p>Registra tu primer producto</p>
          </div>
        ) : (
          <div className="table-container">
            <table>
              <thead>
                <tr>
                  <th>Nombre</th>
                  <th>Tipo</th>
                  <th>Stock</th>
                  <th>Stock Mín.</th>
                  <th>Precio</th>
                  <th>Estado</th>
                  <th>Acciones</th>
                </tr>
              </thead>
              <tbody>
                {products.map((product) => (
                  <tr key={product.id}>
                    <td>{product.name}</td>
                    <td>{getTypeLabel(product.type)}</td>
                    <td>{product.currentStock} {product.unitOfMeasure}</td>
                    <td>{product.minimumStock}</td>
                    <td>${product.unitPrice.toFixed(2)}</td>
                    <td>
                      {product.lowStock ? (
                        <span className="badge badge-warning">
                          <AlertTriangle size={12} /> Bajo Stock
                        </span>
                      ) : (
                        <span className="badge badge-success">Normal</span>
                      )}
                    </td>
                    <td>
                      <div className="actions">
                        <button className="btn btn-secondary" onClick={() => openMovementModal(product)} title="Movimiento">
                          ↔
                        </button>
                        <button className="btn btn-secondary" onClick={() => handleEdit(product)}>
                          <Edit size={16} />
                        </button>
                        <button className="btn btn-danger" onClick={() => handleDelete(product.id)}>
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
              <h3 className="modal-title">{editingProduct ? 'Editar Producto' : 'Nuevo Producto'}</h3>
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
                  <label className="form-label">Tipo</label>
                  <select
                    className="form-select"
                    value={formData.type}
                    onChange={(e) => setFormData({ ...formData, type: e.target.value })}
                  >
                    <option value="HUMUS_LIQUID">Humus Líquido</option>
                    <option value="HUMUS_SOLID">Humus Sólido</option>
                    <option value="WORM">Lombriz</option>
                    <option value="SUPPLY">Insumo</option>
                  </select>
                </div>
                <div className="form-group">
                  <label className="form-label">Unidad de Medida</label>
                  <select
                    className="form-select"
                    value={formData.unitOfMeasure}
                    onChange={(e) => setFormData({ ...formData, unitOfMeasure: e.target.value })}
                  >
                    <option value="L">Litros (L)</option>
                    <option value="KG">Kilogramos (KG)</option>
                    <option value="UN">Unidades (UN)</option>
                  </select>
                </div>
              </div>
              <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '1rem' }}>
                <div className="form-group">
                  <label className="form-label">Volumen/Unidad</label>
                  <input
                    type="number"
                    step="0.01"
                    className="form-input"
                    value={formData.volumePerUnit}
                    onChange={(e) => setFormData({ ...formData, volumePerUnit: e.target.value })}
                    placeholder="ej: 20"
                  />
                </div>
                <div className="form-group">
                  <label className="form-label">Stock Actual</label>
                  <input
                    type="number"
                    step="0.01"
                    className="form-input"
                    value={formData.currentStock}
                    onChange={(e) => setFormData({ ...formData, currentStock: e.target.value })}
                  />
                </div>
                <div className="form-group">
                  <label className="form-label">Stock Mínimo</label>
                  <input
                    type="number"
                    step="0.01"
                    className="form-input"
                    value={formData.minimumStock}
                    onChange={(e) => setFormData({ ...formData, minimumStock: e.target.value })}
                  />
                </div>
                <div className="form-group">
                  <label className="form-label">Precio Unit.</label>
                  <input
                    type="number"
                    step="0.01"
                    className="form-input"
                    value={formData.unitPrice}
                    onChange={(e) => setFormData({ ...formData, unitPrice: e.target.value })}
                  />
                </div>
              </div>
              <div className="form-group">
                <label className="form-label">Descripción</label>
                <textarea
                  className="form-input"
                  rows={2}
                  value={formData.description}
                  onChange={(e) => setFormData({ ...formData, description: e.target.value })}
                />
              </div>
              <div style={{ display: 'flex', gap: '1rem', justifyContent: 'flex-end' }}>
                <button type="button" className="btn btn-secondary" onClick={() => setShowModal(false)}>
                  Cancelar
                </button>
                <button type="submit" className="btn btn-primary">
                  {editingProduct ? 'Actualizar' : 'Crear'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {showMovementModal && selectedProduct && (
        <div className="modal-overlay" onClick={() => setShowMovementModal(false)}>
          <div className="modal" onClick={(e) => e.stopPropagation()}>
            <div className="modal-header">
              <h3 className="modal-title">Movimiento de Inventario</h3>
              <button className="modal-close" onClick={() => setShowMovementModal(false)}>×</button>
            </div>
            <p style={{ marginBottom: '1rem', color: 'var(--color-text-light)' }}>
              Producto: <strong>{selectedProduct.name}</strong> (Stock actual: {selectedProduct.currentStock})
            </p>
            <form onSubmit={handleMovement}>
              <div className="form-group">
                <label className="form-label">Tipo de Movimiento</label>
                <select
                  className="form-select"
                  value={movementData.movementType}
                  onChange={(e) => setMovementData({ ...movementData, movementType: e.target.value })}
                >
                  <option value="IN">Entrada</option>
                  <option value="OUT">Salida</option>
                  <option value="ADJUSTMENT">Ajuste</option>
                </select>
              </div>
              <div className="form-group">
                <label className="form-label">Cantidad</label>
                <input
                  type="number"
                  step="0.01"
                  className="form-input"
                  value={movementData.quantity}
                  onChange={(e) => setMovementData({ ...movementData, quantity: e.target.value })}
                  required
                />
              </div>
              <div className="form-group">
                <label className="form-label">Motivo</label>
                <input
                  type="text"
                  className="form-input"
                  value={movementData.reason}
                  onChange={(e) => setMovementData({ ...movementData, reason: e.target.value })}
                />
              </div>
              <div style={{ display: 'flex', gap: '1rem', justifyContent: 'flex-end' }}>
                <button type="button" className="btn btn-secondary" onClick={() => setShowMovementModal(false)}>
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
