# ERP Lombricultura - Especificación Técnica

## 1. Información del Proyecto

- **Nombre**: LombriERP
- **Tipo**: Sistema ERP para negocio de lombricultura
- **Descripción**: ERP para gestionar cosechas de lombrices, producción de humus líquido, inventario y dashboard analítico
- **Usuario Objetivo**: Productores de lombriz california y derivados (humus sólido y líquido)

---

## 2. Arquitectura Técnica

### Stack Tecnológico
| Capa | Tecnología | Versión |
|------|------------|---------|
| Backend | Spring Boot | 3.2.x |
| Lenguaje | Java | 21 |
| Base de Datos | PostgreSQL | 15+ |
| Frontend | React + Vite | 18.x |
| Documentación | SpringDoc OpenAPI | 2.x |
| Testing | JUnit 5 + Testcontainers | - |
| Contenedores | Docker + Docker Compose | - |
| CI/CD | GitHub Actions | - |

### Estructura del Proyecto
```
eden-erp/
├── backend/                 # Spring Boot API
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/lombrierp/
│   │   │   └── resources/
│   │   └── test/
│   ├── Dockerfile
│   └── pom.xml
├── frontend/                # React App
│   ├── src/
│   ├── Dockerfile
│   └── package.json
├── docker-compose.yml
├── .env.example
└── README.md
```

---

## 3. Modelo de Datos

### Entidades Core

#### 3.1 LoteCosecha
| Campo | Tipo | Restricciones |
|-------|------|---------------|
| id | UUID | PK, auto |
| fechaCosecha | LocalDate | NOT NULL |
| cantidadKg | BigDecimal | NOT NULL, > 0 |
| pesoPromedio | BigDecimal | > 0 |
| estado | Enum | VIGENTE, AGOTADO, REPRODUCCION |
| observaciones | String | nullable |
| createdAt | Timestamp | auto |
| updatedAt | Timestamp | auto |

#### 3.2 ProduccionHumus
| Campo | Tipo | Restricciones |
|-------|------|---------------|
| id | UUID | PK, auto |
| fechaProduccion | LocalDate | NOT NULL |
| loteCosechaId | UUID | FK -> LoteCosecha |
| cantidadLitros | BigDecimal | NOT NULL, > 0 |
| ec | BigDecimal | 0-10 dS/m |
| ph | BigDecimal | 0-14 |
| ppm | BigDecimal | > 0 |
| temperatura | BigDecimal | nullable |
| densidad | BigDecimal | nullable |
| observaciones | String | nullable |
| createdAt | Timestamp | auto |
| updatedAt | Timestamp | auto |

#### 3.3 Producto (Inventario)
| Campo | Tipo | Restricciones |
|-------|------|---------------|
| id | UUID | PK, auto |
| nombre | String | NOT NULL, unique |
| tipo | Enum | HUMUS_LIQUIDO, HUMUS_SOLIDO, LOMBRIZ, INSUMO |
| descripcion | String | nullable |
| unidadMedida | String | L, KG, UN |
| stockActual | BigDecimal | >= 0 |
| stockMinimo | BigDecimal | >= 0 |
| precioUnitario | BigDecimal | >= 0 |
| costoUnitario | BigDecimal | >= 0 |
| createdAt | Timestamp | auto |
| updatedAt | Timestamp | auto |

#### 3.4 MovimientoInventario
| Campo | Tipo | Restricciones |
|-------|------|---------------|
| id | UUID | PK, auto |
| productoId | UUID | FK -> Producto |
| tipoMovimiento | Enum | ENTRADA, SALIDA, AJUSTE |
| cantidad | BigDecimal | NOT NULL |
| motivo | String | nullable |
| fechaMovimiento | LocalDateTime | NOT NULL |
| createdAt | Timestamp | auto |

#### 3.5 Cliente
| Campo | Tipo | Restricciones |
|-------|------|---------------|
| id | UUID | PK, auto |
| nombre | String | NOT NULL |
| documento | String | unique |
| tipoDocumento | Enum | CI, RUC, PASAPORTE |
| email | String | nullable |
| telefono | String | nullable |
| direccion | String | nullable |
| createdAt | Timestamp | auto |
| updatedAt | Timestamp | auto |

#### 3.6 Venta
| Campo | Tipo | Restricciones |
|-------|------|---------------|
| id | UUID | PK, auto |
| clienteId | UUID | FK -> Cliente |
| fechaVenta | LocalDateTime | NOT NULL |
| total | BigDecimal | NOT NULL |
| estado | Enum | PENDIENTE, PAGADA, CANCELADA |
| observaciones | String | nullable |
| createdAt | Timestamp | auto |
| updatedAt | Timestamp | auto |

#### 3.7 VentaDetalle
| Campo | Tipo | Restricciones |
|-------|------|---------------|
| id | UUID | PK, auto |
| ventaId | UUID | FK -> Venta |
| productoId | UUID | FK -> Producto |
| cantidad | BigDecimal | > 0 |
| precioUnitario | BigDecimal | > 0 |
| subtotal | BigDecimal | > 0 |

---

## 4. API REST Endpoints

### Prefix: `/api/v1`

#### 4.1 Cosechas
- `GET /cosechas` - Listar todas
- `GET /cosechas/{id}` - Obtener por ID
- `POST /cosechas` - Crear
- `PUT /cosechas/{id}` - Actualizar
- `DELETE /cosechas/{id}` - Eliminar

#### 4.2 Producción Humus
- `GET /producciones` - Listar todas
- `GET /producciones/{id}` - Obtener por ID
- `POST /producciones` - Crear
- `PUT /producciones/{id}` - Actualizar
- `DELETE /producciones/{id}` - Eliminar
- `GET /producciones/lote/{loteId}` - Por lote

#### 4.3 Inventario
- `GET /productos` - Listar productos
- `GET /productos/{id}` - Obtener por ID
- `POST /productos` - Crear producto
- `PUT /productos/{id}` - Actualizar
- `DELETE /productos/{id}` - Eliminar
- `GET /productos/bajo-stock` - Productos bajo stock

#### 4.4 Movimientos Inventario
- `GET /movimientos` - Listar movimientos
- `POST /movimientos` - Registrar movimiento

#### 4.5 Ventas
- `GET /ventas` - Listar ventas
- `GET /ventas/{id}` - Obtener por ID
- `POST /ventas` - Crear venta
- `PUT /ventas/{id}` - Actualizar estado

#### 4.6 Dashboard
- `GET /dashboard/estadisticas` - KPIs principales
- `GET /dashboard/produccion-mensual` - Producción por mes
- `GET /dashboard/ventas-mensual` - Ventas por mes

#### 4.7 Clientes
- `GET /clientes` - Listar clientes
- `POST /clientes` - Crear cliente
- `PUT /clientes/{id}` - Actualizar

---

## 5. Frontend - Estructura de Vistas

### 5.1 Layout Principal
- **Sidebar**: Navegación lateral (280px)
- **Header**: Usuario, notificaciones
- **Content**: Área principal

### 5.2 Vistas
1. **Dashboard** (`/`)
   - Cards con KPIs
   - Gráfico de producción mensual
   - Gráfico de ventas mensual
   - Alertas de stock bajo

2. **Cosechas** (`/cosechas`)
   - Tabla de lotes
   - Formulario modal para crear/editar
   - Filtros por estado

3. **Producción Humus** (`/producciones`)
   - Tabla de producciones
   - Formulario con campos: fecha, lote, cantidad, EC, pH, PPM
   - Filtros por fecha

4. **Inventario** (`/inventario`)
   - Listado de productos
   - Stock actual con indicadores visuales
   - Movimientos rápidos

5. **Ventas** (`/ventas`)
   - Registro de ventas
   - Estados de pago

6. **Clientes** (`/clientes`)
   - CRM básico

---

## 6. Contenedores y DevOps

### Docker Compose Services
```yaml
services:
  postgres:
    image: postgres:15
    env_file: .env
    volumes: postgres_data
    
  backend:
    build: ./backend
    ports: "8080:8080"
    env_file: .env
    depends_on: postgres
    
  frontend:
    build: ./frontend
    ports: "3000:3000"
    depends_on: backend
```

### Variables de Entorno (.env)
```
# Database
POSTGRES_DB=lombrierp
POSTGRES_USER=postgres
POSTGRES_PASSWORD=password
POSTGRES_HOST=localhost
POSTGRES_PORT=5432

# Backend
SERVER_PORT=8080
SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/lombrierp

# Frontend
VITE_API_URL=http://localhost:8080/api/v1
```

---

## 7. Testing

### Unit Tests
- Servicios con Mockito
- Controladores con MockMvc

### Integración
- Testcontainers para PostgreSQL
- Tests de API REST

### Cobertura Objetivo
- Mínimo 70% en dominio

---

## 8. Documentación

### Swagger UI
- URL: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

---

## 9. CI/CD

### GitHub Actions
- Build: Compilar Java + Node
- Test: Unit + Integración
- Docker: Build & Push imágenes

---

## 10. Requisitos No Funcionales

- Respuesta API < 200ms
- UI responsiva (mobile-friendly)
- Manejo de errores con mensajes claros
- Validación de datos en frontend y backend
- Logs estructurados
