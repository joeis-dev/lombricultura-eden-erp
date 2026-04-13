import { useState } from 'react'

interface ConfirmDialogProps {
  isOpen: boolean
  title: string
  message: string
  onConfirm: () => void
  onCancel: () => void
}

export default function ConfirmDialog({ isOpen, title, message, onConfirm, onCancel }: ConfirmDialogProps) {
  if (!isOpen) return null

  return (
    <div className="modal-overlay" onClick={onCancel}>
      <div className="modal" onClick={(e) => e.stopPropagation()} style={{ maxWidth: '400px' }}>
        <div className="modal-header">
          <h3 className="modal-title">{title}</h3>
          <button className="modal-close" onClick={onCancel}>×</button>
        </div>
        <div style={{ padding: '1.5rem' }}>
          <p style={{ marginBottom: '1.5rem', color: 'var(--color-text-light)' }}>{message}</p>
          <div style={{ display: 'flex', gap: '1rem', justifyContent: 'flex-end' }}>
            <button className="btn btn-secondary" onClick={onCancel}>
              Cancelar
            </button>
            <button className="btn btn-danger" onClick={onConfirm}>
              Eliminar
            </button>
          </div>
        </div>
      </div>
    </div>
  )
}

interface useConfirmReturn {
  confirm: (title: string, message: string) => Promise<boolean>
  ConfirmDialog: React.FC
}

export function useConfirm(): useConfirmReturn {
  const [dialogState, setDialogState] = useState<{
    isOpen: boolean
    title: string
    message: string
    resolve: (value: boolean) => void
  }>({
    isOpen: false,
    title: '',
    message: '',
    resolve: () => {},
  })

  const confirm = (title: string, message: string): Promise<boolean> => {
    return new Promise((resolve) => {
      setDialogState({ isOpen: true, title, message, resolve })
    })
  }

  const handleConfirm = () => {
    dialogState.resolve(true)
    setDialogState({ ...dialogState, isOpen: false })
  }

  const handleCancel = () => {
    dialogState.resolve(false)
    setDialogState({ ...dialogState, isOpen: false })
  }

  const DialogComponent: React.FC = () => (
    <ConfirmDialog
      isOpen={dialogState.isOpen}
      title={dialogState.title}
      message={dialogState.message}
      onConfirm={handleConfirm}
      onCancel={handleCancel}
    />
  )

  return { confirm, ConfirmDialog: DialogComponent }
}
