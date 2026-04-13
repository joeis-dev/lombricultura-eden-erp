import axios from 'axios'

const API_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080/api/v1'

const api = axios.create({
  baseURL: API_URL,
  headers: {
    'Content-Type': 'application/json',
  },
})

export const dashboardApi = {
  getStats: () => api.get('/dashboard'),
}

export const harvestBatchApi = {
  getAll: () => api.get('/harvest-batches'),
  getById: (id: string) => api.get(`/harvest-batches/${id}`),
  create: (data: any) => api.post('/harvest-batches', data),
  update: (id: string, data: any) => api.put(`/harvest-batches/${id}`, data),
  delete: (id: string) => api.delete(`/harvest-batches/${id}`),
}

export const humusProductionApi = {
  getAll: () => api.get('/humus-productions'),
  getById: (id: string) => api.get(`/humus-productions/${id}`),
  getByBatch: (batchId: string) => api.get(`/humus-productions/batch/${batchId}`),
  create: (data: any) => api.post('/humus-productions', data),
  update: (id: string, data: any) => api.put(`/humus-productions/${id}`, data),
  delete: (id: string) => api.delete(`/humus-productions/${id}`),
}

export const productApi = {
  getAll: () => api.get('/products'),
  getById: (id: string) => api.get(`/products/${id}`),
  getLowStock: () => api.get('/products/low-stock'),
  create: (data: any) => api.post('/products', data),
  update: (id: string, data: any) => api.put(`/products/${id}`, data),
  delete: (id: string) => api.delete(`/products/${id}`),
}

export const inventoryMovementApi = {
  getAll: () => api.get('/inventory-movements'),
  getByProduct: (productId: string) => api.get(`/inventory-movements/product/${productId}`),
  create: (data: any) => api.post('/inventory-movements', data),
}

export const customerApi = {
  getAll: () => api.get('/customers'),
  getById: (id: string) => api.get(`/customers/${id}`),
  create: (data: any) => api.post('/customers', data),
  update: (id: string, data: any) => api.put(`/customers/${id}`, data),
  delete: (id: string) => api.delete(`/customers/${id}`),
}

export const saleApi = {
  getAll: () => api.get('/sales'),
  getById: (id: string) => api.get(`/sales/${id}`),
  create: (data: any) => api.post('/sales', data),
  updateStatus: (id: string, status: string) => api.patch(`/sales/${id}/status?status=${status}`),
}

export const wormBedApi = {
  getAll: () => api.get('/worm-beds'),
  getById: (id: string) => api.get(`/worm-beds/${id}`),
  create: (data: any) => api.post('/worm-beds', data),
  update: (id: string, data: any) => api.put(`/worm-beds/${id}`, data),
  delete: (id: string) => api.delete(`/worm-beds/${id}`),
  updateSubtractionDate: (id: string, date: string) => api.patch(`/worm-beds/${id}/subtraction-date?date=${date}`),
  updateApproximateCount: (id: string, count: number) => api.patch(`/worm-beds/${id}/approximate-count?count=${count}`),
}

export const wormBedModificationApi = {
  getAll: () => api.get('/worm-bed-modifications'),
  getById: (id: string) => api.get(`/worm-bed-modifications/${id}`),
  getByWormBedId: (wormBedId: string) => api.get(`/worm-bed-modifications/worm-bed/${wormBedId}`),
  create: (data: any) => api.post('/worm-bed-modifications', data),
  delete: (id: string) => api.delete(`/worm-bed-modifications/${id}`),
}

export default api
