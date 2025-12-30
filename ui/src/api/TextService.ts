import type {AxiosResponse} from 'axios';
import {type Text} from '@/types/Text';
import {http} from '@/api/ApiConfig.ts';

export const getRandomText = (): Promise<AxiosResponse<Text>> => {
    return http().get<Text>('/texts/random');
};
