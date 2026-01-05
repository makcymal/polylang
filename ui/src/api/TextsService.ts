import {type Text} from '@/types/Text';
import {http} from '@/api/ApiConfig.ts';

export const getRandomText = async (): Promise<Text> => {
    return await http().get<Text>('/texts/random').then((resp) => resp.data);
};
