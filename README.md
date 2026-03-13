# Lombricultura Eden ERP

Sistema ERP para la gestión de un negocio de lombricultura, incluyendo:
- Registro de lotes de cosecha
- Control de producción de humus líquido (con parámetros EC, pH, PPM, temperatura)
- Gestión de inventario
- Control de ventas y clientes
- Dashboard con estadísticas y gráficos

## Tecnologías

| Componente | Tecnología |
|------------|------------|
| Backend | Spring Boot 3.2 + Java 21 |
| Base de Datos | PostgreSQL 15 |
| Frontend | React 18 + Vite + TypeScript |
| Documentación | Swagger/OpenAPI |
| Contenedores | Docker + Docker Compose |
| CI/CD | GitHub Actions |

## Estructura del Proyecto

```
eden-erp/
├── backend/               # API REST con Spring Boot
├── frontend/              # Aplicación React
├── docker-compose.yml      # Orquestación de contenedores
├── .env                   # Variables de entorno
├── .env.example           # Plantilla de variables
├── SPEC.md               # Especificación técnica
└── .github/workflows/    # Pipelines CI/CD
```

## Configuración

1. Clonar el repositorio
2. Copiar `.env.example` a `.env` y configurar las variables
3. Instalar Docker y Docker Compose

## Ejecución con Docker Compose

```bash
# Iniciar todos los servicios
docker compose up -d

# Ver logs
docker compose logs -f

# Detener servicios
docker compose down
```

### Servicios

- **Backend**: http://localhost:8080
- **Frontend**: http://localhost:3000
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **PostgreSQL**: localhost:5432

## Desarrollo Local

### Backend

```bash
cd backend
./mvnw spring-boot:run
```

### Frontend

```bash
cd frontend
npm install
npm run dev
```

## API Endpoints

### Prefijo: `/api/v1`

| Recurso | Métodos |
|---------|---------|
| `/dashboard` | GET |
| `/harvest-batches` | GET, POST |
| `/harvest-batches/{id}` | GET, PUT, DELETE |
| `/humus-productions` | GET, POST |
| `/humus-productions/{id}` | GET, PUT, DELETE |
| `/products` | GET, POST |
| `/products/low-stock` | GET |
| `/products/{id}` | GET, PUT, DELETE |
| `/inventory-movements` | GET, POST |
| `/customers` | GET, POST |
| `/customers/{id}` | GET, PUT, DELETE |
| `/sales` | GET, POST |
| `/sales/{id}` | GET |

## Testing

```bash
# Backend tests
cd backend
mvn test

# Frontend tests (si se configuran)
cd frontend
npm test
```

## Licencia

MIT
