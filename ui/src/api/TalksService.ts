import {apiClient} from '@/api/ApiConfig.ts';

export const getNewTalkId = async (textId: string): Promise<string> => {
    return await apiClient.post<string>(`/talks/${textId}`).then(resp => resp.data);
}

export const appendRecordChunk = async (talkId: string, start: number, chunk: Blob) => {
    await apiClient.put(`/talks/record/${talkId}/${start}`, chunk, {headers: {"Content-Type": "audio/webm"},});
}

export const getTalkTranscription = async (talkId: string): Promise<string> => {
    return await apiClient.get<string>(`/talks/transcription/${talkId}`).then(resp => resp.data);
}

export const analyzeTalk = async (talkId: string): Promise<string> => {
    return await apiClient.get<string>(`/talks/analyze/${talkId}`).then(resp => resp.data);
}
