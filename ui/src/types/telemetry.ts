export type ConfigShape = Readonly<{
  active: string
  interval: number
  id: string
}>

export type WorkerPayloadShape = Readonly<{
  id: string
  lastUpdated: string
}>
