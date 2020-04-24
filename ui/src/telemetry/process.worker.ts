import { API, Table } from "./constants"

import type { WorkerPayloadShape } from "types"
import { fetchApi, QuestDB } from "utils"

const quest = new QuestDB({ port: BACKEND_PORT })

const start = async (payload: WorkerPayloadShape) => {
  const telemetryQuery = await quest.queryRaw(`
    SELECT *
      FROM ${Table.MAIN}
      WHERE created > to_timestamp('${payload.lastUpdated}', 'yyyy-MM-ddTHH:mm:ss.SSSZ');
  `)

  if (telemetryQuery.count > 0) {
    const response = await fetchApi<void>(`${API}/add`, {
      method: "POST",
      body: JSON.stringify({
        columns: telemetryQuery.columns,
        dataset: telemetryQuery.dataset,
        id: payload.id,
      }),
    })

    if (!response.error) {
      const timestamp = telemetryQuery.dataset[
        telemetryQuery.count - 1
      ][0] as string
      postMessage(new Date(timestamp).toISOString())
    }
  }
}

addEventListener("message", (message: { data: WorkerPayloadShape }) => {
  void start(message.data)
})
