import type {Language} from "@/types/Language.ts";
import type StudiedLanguage from "@/types/StudiedLanguage.ts";

export interface User {
    id: string;
    email: string;
    emailConfirmed: boolean;
    username: string;
    nativeLanguage: Language;
    studiedLanguages: StudiedLanguage[];
}
