#!/bin/bash

set -e

BACKUP_DIR="${BACKUP_DIR:-./backups}"
TIMESTAMP=$(date +%Y%m%d_%H%M%S)
BACKUP_NAME="lombrierp_backup_${TIMESTAMP}.sql.gz"

mkdir -p "$BACKUP_DIR"

echo "Starting backup..."

docker exec lombrierp-db pg_dump -U "${POSTGRES_USER:-postgres}" "${POSTGRES_DB:-lombrierp}" | gzip > "$BACKUP_DIR/$BACKUP_NAME"

echo "Backup saved to: $BACKUP_DIR/$BACKUP_NAME"

find "$BACKUP_DIR" -name "lombrierp_backup_*.sql.gz" -mtime +7 -delete

echo "Old backups (>7 days) cleaned up"
echo "Done!"
