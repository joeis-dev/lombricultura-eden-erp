import { Routes, Route, NavLink, useLocation } from 'react-router-dom'
import { 
  LayoutDashboard, 
  Wheat, 
  Droplets, 
  Package, 
  ShoppingCart, 
  Users,
  Activity,
  Grid3X3,
  History
} from 'lucide-react'
import Dashboard from './pages/Dashboard'
import HarvestBatches from './pages/HarvestBatches'
import HumusProductions from './pages/HumusProductions'
import Inventory from './pages/Inventory'
import Sales from './pages/Sales'
import Customers from './pages/Customers'
import WormBeds from './pages/WormBeds'
import WormBedModifications from './pages/WormBedModifications'

const navItems = [
  { path: '/', icon: LayoutDashboard, label: 'Dashboard' },
  { path: '/harvest-batches', icon: Wheat, label: 'Cosechas' },
  { path: '/humus-productions', icon: Droplets, label: 'Producción Humus' },
  { path: '/worm-beds', icon: Grid3X3, label: 'Lechos' },
  { path: '/worm-bed-modifications', icon: History, label: 'Modificaciones' },
  { path: '/inventory', icon: Package, label: 'Inventario' },
  { path: '/sales', icon: ShoppingCart, label: 'Ventas' },
  { path: '/customers', icon: Users, label: 'Clientes' },
]

function App() {
  const location = useLocation()
  const getPageTitle = () => {
    const item = navItems.find(i => i.path === location.pathname)
    return item?.label || 'Dashboard'
  }

  return (
    <div className="app-layout">
      <aside className="sidebar">
        <div className="sidebar-logo">
          <Activity size={28} />
          <h1>Lombricultura Eden</h1>
        </div>
        <nav className="sidebar-nav">
          {navItems.map(item => (
            <NavLink
              key={item.path}
              to={item.path}
              className={({ isActive }) => `nav-link ${isActive ? 'active' : ''}`}
              end={item.path === '/'}
            >
              <item.icon size={20} />
              {item.label}
            </NavLink>
          ))}
        </nav>
      </aside>
      <main className="main-content">
        <header className="header">
          <h2>{getPageTitle()}</h2>
          <div>
            <span style={{ color: 'var(--color-text-light)' }}>ERP v1.0.0</span>
          </div>
        </header>
        <div className="page-content">
          <Routes>
            <Route path="/" element={<Dashboard />} />
            <Route path="/harvest-batches" element={<HarvestBatches />} />
            <Route path="/humus-productions" element={<HumusProductions />} />
            <Route path="/worm-beds" element={<WormBeds />} />
            <Route path="/worm-bed-modifications" element={<WormBedModifications />} />
            <Route path="/inventory" element={<Inventory />} />
            <Route path="/sales" element={<Sales />} />
            <Route path="/customers" element={<Customers />} />
          </Routes>
        </div>
      </main>
    </div>
  )
}

export default App
