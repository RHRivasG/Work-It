export interface TrainingVideo {
  ext: string
  length: number
  name: string
  video?: string
}

export interface Training {
    id: string,
    trainerId: string,
    name: string,
    description: string,
    categories: string[],
    video: TrainingVideo
}
