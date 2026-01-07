import i18n from 'i18next';
import {initReactI18next} from 'react-i18next';

const resources = {
    ru: {
        translation: {
            switchTheme: 'Сменить оформление',
            showProfile: 'Показать профиль',

            selectText: 'Выбрать текст для перевода',
            startTranslating: 'Начать переводить',
            pauseTranslating: 'Приостановить перевод',
            resumeTranslating: 'Продолжить переводить',
            textToTranslate: 'Текст для перевода',
            yourTranslation: 'Ваш перевод',
            textIsEditable: 'Текст можно редактировать',

            analyzeTranslation: 'Анализировать перевод',
            hereWillBeAnalysis: 'Здесь будет анализ вашего перевода',

            login: 'Войти',
            logout: 'Выйти',
            register: 'Зарегистрироваться',
            loginOrRegister: 'Войти или зарегистрироваться',

            email: 'Электронная почта',
            username: 'Имя пользователя',
            emailOrUsername: 'Электронная почта или имя пользователя',

            password: 'Пароль',
            incorrectPassword: 'Неверный пароль',

            confirmationCode: 'Код подтверждения',
            emailConfirmation: 'Подтверждение почты',

            hi: 'Привет',
            confirm: 'Подтвердить',
            continue: 'Продолжить',
        }
    },
    en: {
        translation: {
            switchTheme: 'Switch theme',
            showProfile: 'Show profile',

            selectText: 'Select text for translation',
            startTranslating: 'Start translating',
            pauseTranslating: 'Pause translating',
            resumeTranslating: 'Resume translating',
            textToTranslate: 'Text to translate',
            yourTranslation: 'Your translation',
            textIsEditable: 'The text can be edited',

            analyzeTranslation: 'Analyze translation',
            hereWillBeAnalysis: 'Your translation will be analyzed here',

            login: 'Login',
            logout: 'Logout',
            register: 'Register',
            loginOrRegister: 'Login or register',

            email: 'Email',
            username: 'Username',
            emailOrUsername: 'Email or username',

            password: 'Password',
            incorrectPassword: 'Incorrect password',

            confirmationCode: 'Confirmation code',
            emailConfirmation: 'Email confirmation',

            hi: 'Hi',
            confirm: 'Confirm',
            continue: 'Continue',
        }
    }
};

await i18n.use(initReactI18next).init({
    resources,
    lng: 'ru',
    fallbackLng: 'en',
    interpolation: {
        escapeValue: false
    }
});

export default i18n;
