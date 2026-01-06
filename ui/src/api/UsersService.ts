import type {CheckUserExistsResponse} from "@/types/users/CheckUserExistsResponse.ts";
import {apiClient} from "@/api/ApiConfig.ts";
import type {RegisterRequest} from "@/types/users/RegisterRequest.ts";
import type {ConfirmEmailRequest} from "@/types/users/ConfirmEmailRequest.ts";
import type {LoginRequest} from "@/types/users/LoginRequest.ts";

export const checkUserExists = async (emailOrUsername: string): Promise<CheckUserExistsResponse> => {
    return await apiClient.get<CheckUserExistsResponse>(`/users/check-if-exists/${emailOrUsername}`).then(resp => resp.data);
}

export const registerUser = async (request: RegisterRequest): Promise<void> => {
    await apiClient.post<void>('/users/register', request);
}

export const confirmEmail = async (request: ConfirmEmailRequest): Promise<void> => {
    await apiClient.put<void>('/users/confirm', request);
}

export const doLogin = async (request: LoginRequest): Promise<void> => {
    await apiClient.post<void>('/users/login', request);
}

export const doLogout = async (): Promise<void> => {
    await apiClient.post<void>('/users/logout');
}
