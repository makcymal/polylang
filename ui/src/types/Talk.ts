export interface Talk {
    id: string;
    userId: string;
    textId: string;
    transcription: string;
    analysisId: number;
    score: number;
    created_at: string;
}