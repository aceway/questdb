import { API, Table } from "./constants"
import ProcessWorker from "./process.worker.ts"

import type { ConfigShape, WorkerPayloadShape } from "types"
import { fetchApi, QuestDB } from "utils"

type LastUpdatedResponse = Readonly<{ lastUpdated?: string }>

const quest = new QuestDB({ port: BACKEND_PORT })

const start = async () => {
  const weekOffset = 24 * 60 * 60 * 1000 * 7
  const configQuery = await quest.query<ConfigShape>(`
    SELECT * FROM ${Table.CONFIG};
  `)
  let lastUpdated: string | undefined

  // If the user enabled telemetry then we start the webworker
  if (configQuery.data.length && configQuery.data[0].active) {
    const response = await fetchApi<LastUpdatedResponse>(
      `${API}/last-updated`,
      {
        method: "POST",
        body: JSON.stringify({ id: configQuery.data[0].id }),
      },
    )

    if (response.error || !response.data) {
      return
    }

    lastUpdated = response.data.lastUpdated

    const sendTelemetry = () => {
      const worker: Worker = new ProcessWorker()
      const payload: WorkerPayloadShape = {
        id: configQuery.data[0].id,
        lastUpdated:
          lastUpdated ||
          new Date(new Date().getTime() - weekOffset).toISOString(),
      }

      worker.postMessage(payload)
      worker.onmessage = (event: { data?: string }) => {
        lastUpdated = event.data
      }
    }

    sendTelemetry()
    setInterval(sendTelemetry, configQuery.data[0].interval)
  }
}

export default start
