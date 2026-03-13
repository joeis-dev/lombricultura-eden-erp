import { useEffect, useState } from 'react'
import { 
  BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer, LineChart, Line 
} from 'recharts'
import { Wheat, Droplets, Package, DollarSign, AlertTriangle } from 'lucide-react'
import { dashboardApi } from '../services/api'

interface DashboardData {
  totalHarvestBatches: number
  totalActiveBatches: number
  totalHumusProduction: number
  totalLitersProduced: number
  totalSales: number
  totalRevenue: number
  totalProducts: number
  lowStockProducts: number
  monthlyProductions: { month: string; quantity: number }[]
  monthlySales: { month: string; total: number }[]
  lowStockAlerts: any[]
}

export default function Dashboard() {
  const [data, setData] = useState<DashboardData | null>(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  useEffect(() => {
    loadDashboard()
  }, [])

  const loadDashboard = async () => {
    try {
      const response = await dashboardApi.getStats()
      setData(response.data)
    } catch (err) {
      setError('Error loading dashboard data')
    } finally {
      setLoading(false)
    }
  }

  if (loading) {
    return (
      <div className="loading">
        <div className="spinner"></div>
      </div>
    )
  }

  if (error) {
    return (
      <div className="alert alert-error">
        {error}. Make sure the backend is running.
      </div>
    )
  }

  return (
    <div>
      <div className="stats-grid">
        <div className="stat-card">
          <div className="stat-card-header">
            <span className="stat-card-title">Lotes de Cosecha</span>
            <Wheat size={24} color="var(--color-primary)" />
          </div>
          <div className="stat-card-value">{data?.totalHarvestBatches || 0}</div>
          <div className="stat-card-change positive">
            {data?.totalActiveBatches || 0} activos
          </div>
        </div>

        <div className="stat-card">
          <div className="stat-card-header">
            <span className="stat-card-title">Producción Humus</span>
            <Droplets size={24} color="#8b5a2b" />
          </div>
          <div className="stat-card-value">{data?.totalLitersProduced?.toFixed(1) || 0} L</div>
          <div className="stat-card-change">
            {data?.totalHumusProduction || 0} producciones
          </div>
        </div>

        <div className="stat-card">
          <div className="stat-card-header">
            <span className="stat-card-title">Productos</span>
            <Package size={24} color="var(--color-secondary)" />
          </div>
          <div className="stat-card-value">{data?.totalProducts || 0}</div>
          <div className="stat-card-change">
            {data?.lowStockProducts || 0} bajo stock
          </div>
        </div>

        <div className="stat-card">
          <div className="stat-card-header">
            <span className="stat-card-title">Ingresos del Año</span>
            <DollarSign size={24} color="var(--color-success)" />
          </div>
          <div className="stat-card-value">${data?.totalRevenue?.toFixed(2) || '0.00'}</div>
          <div className="stat-card-change">
            {data?.totalSales || 0} ventas
          </div>
        </div>
      </div>

      {data?.lowStockAlerts && data.lowStockAlerts.length > 0 && (
        <div className="card" style={{ marginBottom: '2rem' }}>
          <div className="card-header">
            <h3 className="card-title" style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
              <AlertTriangle size={20} color="var(--color-warning)" />
              Alertas de Stock Bajo
            </h3>
          </div>
          <div style={{ display: 'flex', flexWrap: 'wrap', gap: '1rem' }}>
            {data.lowStockAlerts.map((product: any) => (
              <div key={product.id} className="alert alert-warning" style={{ flex: '1 1 200px' }}>
                <strong>{product.name}</strong>: {product.currentStock} {product.unitOfMeasure} (mín: {product.minimumStock})
              </div>
            ))}
          </div>
        </div>
      )}

      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(400px, 1fr))', gap: '1.5rem' }}>
        <div className="card">
          <div className="card-header">
            <h3 className="card-title">Producción Mensual</h3>
          </div>
          <ResponsiveContainer width="100%" height={250}>
            <BarChart data={data?.monthlyProductions || []}>
              <CartesianGrid strokeDasharray="3 3" />
              <XAxis dataKey="month" />
              <YAxis />
              <Tooltip />
              <Bar dataKey="quantity" fill="var(--color-primary)" />
            </BarChart>
          </ResponsiveContainer>
        </div>

        <div className="card">
          <div className="card-header">
            <h3 className="card-title">Ventas Mensuales</h3>
          </div>
          <ResponsiveContainer width="100%" height={250}>
            <LineChart data={data?.monthlySales || []}>
              <CartesianGrid strokeDasharray="3 3" />
              <XAxis dataKey="month" />
              <YAxis />
              <Tooltip />
              <Line type="monotone" dataKey="total" stroke="var(--color-success)" strokeWidth={2} />
            </LineChart>
          </ResponsiveContainer>
        </div>
      </div>
    </div>
  )
}
