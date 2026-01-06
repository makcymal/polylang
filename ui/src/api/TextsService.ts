import {type Text} from '@/types/Text';
import {apiClient} from '@/api/ApiConfig.ts';

export const getRandomText = async (): Promise<Text> => {
    return await apiClient.get<Text>('/texts/random').then((resp) => resp.data);
};
