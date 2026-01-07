import { LIGHT, type Theme, type ThemeConsumer } from '@/types/Theme.ts';
import '@/widgets/Header.css';
import {useState} from "react";
import Profile from "@/widgets/Profile.tsx";
import {useTranslation} from "react-i18next";

interface HeaderProps {
    theme: Theme;
    switchTheme: ThemeConsumer;
}

export const Header: React.FC<HeaderProps> = ({ theme, switchTheme }: HeaderProps) => {
    const {t} = useTranslation();

    const [isProfileVisible, setIsProfileVisible] = useState(false);

    const showProfile = () => {
        setIsProfileVisible(true);
    };

    return (
        <div>
            <header className="header">
                <div className="header-logo">
                    <img src="/polylang.svg" alt="logo" className="logo-icon" />
                    <span className="logo-text">polylang</span>
                </div>
                <div className="header-menu">
                    <div className="button-with-hint">
                        <button
                            className="header-menu-button"
                            onClick={() => switchTheme(theme)}
                            type="button"
                            aria-label="Switch theme"
                        >
                            <img
                                alt={theme === LIGHT ? 'Switch to dark theme' : 'Switch to light theme'}
                                src={theme === LIGHT ? '/theme.black.png' : '/theme.white.png'}
                                className="switch-theme-icon"
                            />
                        </button>
                        <p className="hint">{t('switchTheme')}</p>
                    </div>
                    <div className="button-with-hint">
                        <button
                            className="header-menu-button"
                            onClick={showProfile}
                            type="button"
                            aria-label="Show profile"
                        >
                            <img
                                alt={'Show profile'}
                                src={theme === LIGHT ? '/user.black.png' : '/user.white.png'}
                                className="show-profile-icon"
                            />
                        </button>
                        <p className="hint">{t('showProfile')}</p>
                    </div>
                </div>
            </header>
            <Profile isVisible={isProfileVisible} setIsVisible={setIsProfileVisible} />
        </div>
    );
};

export default Header;
