import { useState, useEffect, useRef, ReactNode } from 'react'

interface Column {
  key: string
  label: string
  width?: number
  visible?: boolean
}

interface ResizableTableProps {
  columns: Column[]
  data: any[]
  storageKey: string
  renderCell: (row: any, key: string) => ReactNode
  onRowClick?: (row: any) => void
}

export default function ResizableTable({ columns, data, storageKey, renderCell, onRowClick }: ResizableTableProps) {
  const [columnWidths, setColumnWidths] = useState<Record<string, number>>(() => {
    const saved = localStorage.getItem(`${storageKey}_widths`)
    return saved ? JSON.parse(saved) : {}
  })
  const [columnOrder, setColumnOrder] = useState<string[]>(() => {
    const saved = localStorage.getItem(`${storageKey}_order`)
    return saved ? JSON.parse(saved) : columns.map((c: any) => c.key || c)
  })
  const tableRef = useRef<HTMLTableElement>(null)
  const resizingRef = useRef<{ col: HTMLElement, startX: number, startWidth: number, key: string } | null>(null)

  useEffect(() => {
    localStorage.setItem(`${storageKey}_widths`, JSON.stringify(columnWidths))
  }, [columnWidths, storageKey])

  useEffect(() => {
    localStorage.setItem(`${storageKey}_order`, JSON.stringify(columnOrder))
  }, [columnOrder, storageKey])

  const handleMouseDown = (e: React.MouseEvent, key: string) => {
    e.preventDefault()
    const col = (e.target as HTMLElement).closest('th') as HTMLElement
    resizingRef.current = {
      col,
      startX: e.pageX,
      startWidth: col.offsetWidth,
      key
    }
    document.addEventListener('mousemove', handleMouseMove)
    document.addEventListener('mouseup', handleMouseUp)
  }

  const handleMouseMove = (e: MouseEvent) => {
    if (!resizingRef.current) return
    const { startX, startWidth, key } = resizingRef.current
    const diff = e.pageX - startX
    setColumnWidths(prev => ({ ...prev, [key]: Math.max(50, startWidth + diff) }))
  }

  const handleMouseUp = () => {
    resizingRef.current = null
    document.removeEventListener('mousemove', handleMouseMove)
    document.removeEventListener('mouseup', handleMouseUp)
  }

  const handleDragStart = (e: React.DragEvent, key: string) => {
    e.dataTransfer.setData('text/plain', key)
    e.dataTransfer.effectAllowed = 'move'
  }

  const handleDrop = (e: React.DragEvent, targetKey: string) => {
    e.preventDefault()
    const draggedKey = e.dataTransfer.getData('text/plain')
    if (draggedKey === targetKey) return

    setColumnOrder(prev => {
      const newOrder = [...prev]
      const draggedIndex = newOrder.indexOf(draggedKey)
      const targetIndex = newOrder.indexOf(targetKey)
      newOrder.splice(draggedIndex, 1)
      newOrder.splice(targetIndex, 0, draggedKey)
      return newOrder
    })
  }

  const handleDragOver = (e: React.DragEvent) => {
    e.preventDefault()
    e.dataTransfer.dropEffect = 'move'
  }

  const visibleColumns = columnOrder.length > 0 
    ? columnOrder.map(key => columns.find(c => c.key === key)).filter(Boolean)
    : columns

  return (
    <div className="table-container">
      <table ref={tableRef}>
        <thead>
          <tr>
            {visibleColumns.map(col => (
              <th
                key={col!.key}
                draggable
                onDragStart={(e) => handleDragStart(e, col!.key)}
                onDragOver={handleDragOver}
                onDrop={(e) => handleDrop(e, col!.key)}
                style={{ 
                  width: columnWidths[col!.key] || col!.width || 'auto',
                  cursor: 'move',
                  userSelect: 'none'
                }}
              >
                <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
                  <span>{col!.label}</span>
                  <div
                    className="resize-handle"
                    onMouseDown={(e) => handleMouseDown(e, col!.key)}
                    onClick={(e) => e.stopPropagation()}
                  />
                </div>
              </th>
            ))}
          </tr>
        </thead>
        <tbody>
          {data.map((row, idx) => (
            <tr key={row.id || idx} onClick={() => onRowClick?.(row)}>
              {visibleColumns.map(col => (
                <td key={col!.key}>
                  {renderCell(row, col!.key)}
                </td>
              ))}
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  )
}
