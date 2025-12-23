import type { AxiosResponse } from 'axios';
import {type Text} from '@/types/Text'
import instance from '@/services/AxiosService.ts';

export const getRandomText = (): Promise<AxiosResponse<Text>> => {
    return instance.get<Text>('/texts/random');
};
