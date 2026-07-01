import { DeliveryLane } from '../components/DeliveryLane'
import { PageHeader } from '../components/PageHeader'
import { useNotifications } from '../hooks/useNotifications'

export function OverviewPage() {
  const notifications = useNotifications()
  const data = notifications.data ?? []

  return (
    <>
      <PageHeader
        title="Overview"
        subtitle="Aggregate view of every notification by delivery stage."
      />
      <DeliveryLane notifications={data} pulseKey={data.length} />
    </>
  )
}
