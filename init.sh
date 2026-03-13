#!/bin/bash

set -e

COMPOSE_FILE="docker-compose.yml"
COMPOSE_DEV_FILE="docker-compose.dev.yml"

usage() {
    echo "Usage: $0 <command>"
    echo ""
    echo "Commands:"
    echo "  dev          Start development with hot reload (frontend + backend)"
    echo "  dev-fe       Start only frontend in development mode"
    echo "  dev-be       Start only backend in development mode"
    echo "  prod         Start production mode"
    echo "  build        Build all images (no start)"
    echo "  build-dev    Build development images"
    echo "  clean        Stop and remove all containers and volumes"
    echo "  stop         Stop all containers"
    echo "  restart      Restart all containers"
    echo "  logs         Show logs (follow mode)"
    echo "  logs-fe      Show frontend logs"
    echo "  logs-be      Show backend logs"
    echo "  ps           Show running containers"
    echo "  backup       Backup database"
    echo "  shell-be     Open shell in backend container"
    echo "  shell-fe     Open shell in frontend container"
    echo "  shell-db     Open PostgreSQL shell"
    echo "  health       Check services health"
    echo ""
    echo "Examples:"
    echo "  $0 dev                    # Start dev mode"
    echo "  $0 dev -f                # Start dev mode and follow logs"
    echo "  $0 clean                 # Clean everything"
    exit 1
}

start_dev() {
    echo "Starting development mode with hot reload..."
    docker compose -f "${COMPOSE_FILE}" -f "${COMPOSE_DEV_FILE}" up --build -d
    echo ""
    echo "Services started:"
    echo "  Frontend: http://localhost:3001 (Vite HMR)"
    echo "  Frontend (nginx): http://localhost:3000"
    echo "  Backend:  http://localhost:8080"
    echo "  API Docs: http://localhost:8080/swagger-ui.html"
}

start_dev_fe() {
    echo "Starting frontend development mode..."
    docker compose -f "${COMPOSE_FILE}" -f "${COMPOSE_DEV_FILE}" up -d postgres
    docker compose -f "${COMPOSE_FILE}" -f "${COMPOSE_DEV_FILE}" up --build frontend
    echo ""
    echo "Frontend: http://localhost:3000"
}

start_dev_be() {
    echo "Starting backend development mode..."
    docker compose -f "${COMPOSE_FILE}" up -d postgres
    docker compose -f "${COMPOSE_FILE}" -f "${COMPOSE_DEV_FILE}" up --build backend
    echo ""
    echo "Backend: http://localhost:8080"
}

start_prod() {
    echo "Starting production mode..."
    docker compose -f "${COMPOSE_FILE}" up --build -d
    echo ""
    echo "Services started:"
    echo "  Frontend: http://localhost:3000"
    echo "  Backend:  http://localhost:8080"
}

build() {
    echo "Building production images..."
    docker compose -f "${COMPOSE_FILE}" build --no-cache
}

build_dev() {
    echo "Building development images..."
    docker compose -f "${COMPOSE_FILE}" -f "${COMPOSE_DEV_FILE}" build --no-cache
}

clean() {
    echo "Stopping and removing all containers and volumes..."
    docker compose -f "${COMPOSE_FILE}" down -v
    docker compose -f "${COMPOSE_FILE}" -f "${COMPOSE_DEV_FILE}" down -v 2>/dev/null || true
    echo "Clean complete!"
}

stop() {
    echo "Stopping all containers..."
    docker compose -f "${COMPOSE_FILE}" down
    docker compose -f "${COMPOSE_FILE}" -f "${COMPOSE_DEV_FILE}" down 2>/dev/null || true
}

restart() {
    stop
    start_prod
}

logs() {
    docker compose -f "${COMPOSE_FILE}" logs -f
}

logs_fe() {
    docker compose -f "${COMPOSE_FILE}" logs -f frontend
}

logs_be() {
    docker compose -f "${COMPOSE_FILE}" logs -f backend
}

ps() {
    docker compose -f "${COMPOSE_FILE}" ps
}

backup_db() {
    BACKUP_DIR="${BACKUP_DIR:-./backups}"
    mkdir -p "$BACKUP_DIR"
    TIMESTAMP=$(date +%Y%m%d_%H%M%S)
    BACKUP_NAME="lombrierp_backup_${TIMESTAMP}.sql.gz"
    
    docker exec lombrierp-db pg_dump -U "${POSTGRES_USER:-postgres}" "${POSTGRES_DB:-lombrierp}" | gzip > "$BACKUP_DIR/$BACKUP_NAME"
    echo "Backup saved to: $BACKUP_DIR/$BACKUP_NAME"
}

shell_be() {
    docker exec -it lombrierp-backend sh
}

shell_fe() {
    docker exec -it lombrierp-frontend sh
}

shell_db() {
    docker exec -it lombrierp-db psql -U "${POSTGRES_USER:-postgres}" -d "${POSTGRES_DB:-lombrierp}"
}

health() {
    echo "Checking services health..."
    echo ""
    
    echo -n "Postgres: "
    docker exec lombrierp-db pg_isready -U postgres > /dev/null 2>&1 && echo "OK" || echo "DOWN"
    
    echo -n "Backend:  "
    curl -s http://localhost:8080/actuator/health > /dev/null 2>&1 && echo "OK" || echo "DOWN"
    
    echo -n "Frontend: "
    curl -s http://localhost:80 > /dev/null 2>&1 && echo "OK" || echo "DOWN"
}

COMMAND="${1:-}"

case "$COMMAND" in
    dev)           start_dev ;;
    dev-fe)        start_dev_fe ;;
    dev-be)        start_dev_be ;;
    prod)          start_prod ;;
    build)         build ;;
    build-dev)     build_dev ;;
    clean)         clean ;;
    stop)          stop ;;
    restart)       restart ;;
    logs)          logs ;;
    logs-fe)       logs_fe ;;
    logs-be)       logs_be ;;
    ps)            ps ;;
    backup)        backup_db ;;
    shell-be)      shell_be ;;
    shell-fe)      shell_fe ;;
    shell-db)      shell_db ;;
    health)        health ;;
    *)             usage ;;
esac
