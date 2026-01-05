import { type Language, type LanguageLevel } from '@/types/Language';

export interface Text {
    id: string;
    content: string;
    language: Language;
    intendedLevel: LanguageLevel;
}
