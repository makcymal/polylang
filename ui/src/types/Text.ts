import {type Language, type LanguageLevel} from '@/types/Language'

export interface Text {
    id: number;
    content: string;
    language: Language;
    intendedLevel: LanguageLevel;
}