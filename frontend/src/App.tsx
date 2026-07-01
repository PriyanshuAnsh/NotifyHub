import { Route, Routes } from 'react-router-dom'
import { NavRail } from './components/NavRail'
import { ConsolePage } from './pages/ConsolePage'
import { OverviewPage } from './pages/OverviewPage'

export default function App() {
  return (
    <div className="flex min-h-screen">
      <NavRail />
      <div className="mx-auto flex w-full max-w-6xl flex-col gap-6 px-4 py-8 sm:px-8">
        <Routes>
          <Route path="/" element={<ConsolePage />} />
          <Route path="/overview" element={<OverviewPage />} />
        </Routes>
      </div>
    </div>
  )
}
