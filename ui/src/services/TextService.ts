import instance from "@/services/AxiosService.ts";
import type {AxiosResponse} from "axios";

export const getRandomText = (): Promise<AxiosResponse<Text>> => {
    return instance.get<Text>('/texts/random')
}