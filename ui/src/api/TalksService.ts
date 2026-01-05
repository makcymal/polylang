import {http} from '@/api/ApiConfig.ts';
import type {RecordChunk} from "@/types/RecordChunk.ts";

export const getNewTalkId = async (): Promise<string> => {
    return await http().post<string>('/talks').then((resp) => resp.data);
}

export const sendRecordChunk = async (talkId: string, chunk: RecordChunk) => {
    await http().put(`/talks/record/${talkId}`, chunk)
}

export const getTalkTranscription = async (talkId: string) => {
    return await http().get(`/talks/transcription/${talkId}`);
}