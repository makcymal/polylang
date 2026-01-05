import type {Language, LanguageLevel} from "@/types/Language.ts";

export default interface StudiedLanguage {
    id: number;
    userId: number;
    language: Language;
    declaredLevel: LanguageLevel;
    estimatedLevel: LanguageLevel;
}