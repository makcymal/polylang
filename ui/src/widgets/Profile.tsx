import '@/widgets/Profile.css'
import {type ChangeEvent, useEffect, useRef, useState} from "react";
import {type User} from "@/types/users/User.ts";
import {readUserFromCookie} from "@/utils/ReadCookies.ts";
import type {LoginRequest} from "@/types/users/LoginRequest.ts";
import type {RegisterRequest} from "@/types/users/RegisterRequest.ts";
import type {ConfirmEmailRequest} from "@/types/users/ConfirmEmailRequest.ts";
import {checkUserExists, confirmEmail, doLogin, doLogout, registerUser} from "@/api/UsersService.ts";
import type {AxiosError} from "axios";
import {useTranslation} from "react-i18next";

interface Props {
    isVisible: boolean;
    setIsVisible: (value: boolean) => void;
}

type Stage = 'start' | 'register' | 'confirm' | 'login' | 'view';

export const Profile = ({isVisible, setIsVisible}: Props) => {
    const {t} = useTranslation();

    const user = useRef<User | null>(null);
    const emailOrUsername = useRef<string | null>(null);
    const [email, setEmail] = useState('');
    const [username, setUsername] = useState('');
    const [login, setLogin] = useState('');
    const confirmEmailRequest = useRef<ConfirmEmailRequest>({'email': '', 'code': ''});
    const registerRequest = useRef<RegisterRequest>({'email': '', 'username': '', 'password': ''});
    const loginRequest = useRef<LoginRequest>({'emailOrUsername': '', 'password': ''});
    const [passwordIncorrect, setPasswordIncorrect] = useState<boolean>(false);
    const [stage, setStage] = useState<Stage>('start');

    const cleanUp = () => {
        user.current = null;
        emailOrUsername.current = null;
        setEmail('');
        setUsername('');
        confirmEmailRequest.current = {'email': '', 'code': ''};
        registerRequest.current = {'email': '', 'username': '', 'password': ''};
        loginRequest.current = {'emailOrUsername': '', 'password': ''};
        setPasswordIncorrect(false);
    }

    useEffect(() => {
        user.current = readUserFromCookie();
        if (user.current == null) {
            // eslint-disable-next-line react-hooks/set-state-in-effect
            setStage('start');
        } else {
            setEmail(user.current.email);
            setUsername(user.current.username);
            if (!user.current.emailConfirmed) {
                confirmEmailRequest.current.email = user.current.email;
                setStage('confirm');
            } else {
                setStage('view');
            }
        }
    }, []);

    const stageStartOnEmailOrUsernameChanged = (evt: ChangeEvent<HTMLInputElement>) => {
        emailOrUsername.current = evt.target.value;
    }

    const stageStartOnButtonClicked = async () => {
        if (emailOrUsername.current) {
            const checkUserExistsResponse = await checkUserExists(emailOrUsername.current);
            if (!checkUserExistsResponse.exists) {
                if (checkUserExistsResponse.email) {
                    registerRequest.current.email = emailOrUsername.current;
                    setEmail(emailOrUsername.current);
                } else {
                    registerRequest.current.username = emailOrUsername.current;
                    setUsername(emailOrUsername.current);
                }
                setStage('register');
            } else {
                loginRequest.current.emailOrUsername = emailOrUsername.current;
                setLogin(emailOrUsername.current);
                setStage('login');
            }
        }
    }

    const stageRegisterOnEmailChanged = (evt: ChangeEvent<HTMLInputElement>) => {
        registerRequest.current.email = evt.target.value;
        setEmail(evt.target.value);
    }

    const stageRegisterOnUsernameChanged = (evt: ChangeEvent<HTMLInputElement>) => {
        registerRequest.current.username = evt.target.value;
        setUsername(evt.target.value);
    }

    const stageRegisterOnPasswordChanged = (evt: ChangeEvent<HTMLInputElement>) => {
        registerRequest.current.password = evt.target.value;
    }

    const stageRegisterOnButtonClicked = async () => {
        await registerUser(registerRequest.current);
        user.current = readUserFromCookie();
        if (user.current == null) {
            setStage('start');
        } else {
            confirmEmailRequest.current.email = registerRequest.current.email;
            setStage('confirm');
        }
    }

    const stageConfirmOnCodeChanged = (evt: ChangeEvent<HTMLInputElement>) => {
        confirmEmailRequest.current.code = evt.target.value;
    }

    const stageConfirmOnButtonClicked = async () => {
        await confirmEmail(confirmEmailRequest.current);
        user.current = readUserFromCookie();
        if (user.current == null) {
            setStage('start');
        } else {
            if (user.current.emailConfirmed) {
                setStage('view');
            } else {
                setStage('confirm');
            }
        }
    }

    const stageLoginOnLoginChanged = (evt: ChangeEvent<HTMLInputElement>) => {
        loginRequest.current.emailOrUsername = evt.target.value;
        setLogin(evt.target.value);
    }

    const stageLoginOnPasswordChanged = (evt: ChangeEvent<HTMLInputElement>) => {
        loginRequest.current.password = evt.target.value;
    }

    const stageLoginOnButtonClicked = async () => {
        await doLogin(loginRequest.current)
            .then(() => {
                user.current = readUserFromCookie();
                if (user.current == null) {
                    setStage('start');
                } else {
                    setEmail(user.current.email);
                    setUsername(user.current.username);
                    if (user.current.emailConfirmed) {
                        setStage('view');
                    } else {
                        confirmEmailRequest.current.email = email;
                        setStage('confirm');
                    }
                }
            })
            .catch((e: AxiosError) => {
                if (e.status == 401) {
                    setPasswordIncorrect(() => true);
                    setStage('login');
                }
            });
    }

    const stageViewOnButtonClicked = async () => {
        await doLogout();
        cleanUp();
        setStage('start');
    }

    return (
        <div>
            {
                isVisible &&
                <div className="blur" onClick={() => setIsVisible(false)}>
                    <div
                        className="container"
                        onClick={(e) => e.stopPropagation()}
                    >
                        {
                            stage == 'start' &&
                            <div className="form">
                                <p className="form-title form-width">{t('loginOrRegister')}</p>
                                <div className="form-item form-width">
                                    <p className="form-item-name">{t('emailOrUsername')}</p>
                                    <input className="form-input" placeholder='user@mail.com'
                                           onChange={stageStartOnEmailOrUsernameChanged}/>
                                </div>
                                <button className="cta form-button"
                                        onClick={() => void stageStartOnButtonClicked()}>
                                    {t('continue')}
                                </button>
                            </div>
                        }
                        {
                            stage == 'register' &&
                            <div className="form">
                                <p className="form-title form-width">{t('register')}</p>
                                <div className="form-item form-width">
                                    <p className="form-item-name">{t('email')}</p>
                                    <input className="form-input" placeholder='user@mail.com'
                                           value={email}
                                           onChange={stageRegisterOnEmailChanged}/>
                                </div>
                                <div className="form-item form-width">
                                    <p className="form-item-name">{t('username')}</p>
                                    <input className="form-input" placeholder='username'
                                           value={username}
                                           onChange={stageRegisterOnUsernameChanged}/>
                                </div>
                                <div className="form-item form-width">
                                    <p className="form-item-name">{t('password')}</p>
                                    <input className="form-input" placeholder='P4ssw0rd!' type='password'
                                           onChange={stageRegisterOnPasswordChanged}/>
                                </div>
                                <button className="cta form-button"
                                        onClick={() => void stageRegisterOnButtonClicked()}>
                                    {t('continue')}
                                </button>
                            </div>
                        }
                        {
                            stage == 'confirm' &&
                            <div className="form">
                                <p className="form-title form-width">{t('emailConfirmation')}</p>
                                <div className="form-item form-width">
                                    <p className="form-item-name">{t('email')}</p>
                                    <input className="form-input" value={email}
                                           readOnly={true}/>
                                </div>
                                <div className="form-item form-width">
                                    <p className="form-item-name">{t('confirmationCode')}</p>
                                    <input className="form-input" placeholder='123456'
                                           onChange={stageConfirmOnCodeChanged}/>
                                </div>
                                <button className="cta form-button"
                                        onClick={() => void stageConfirmOnButtonClicked()}>
                                    {t('confirm')}
                                </button>
                            </div>
                        }
                        {
                            stage == 'login' &&
                            <div className="form">
                                <p className="form-title form-width">{t('login')}</p>
                                <div className="form-item form-width">
                                    <p className="form-item-name">{t('emailOrUsername')}</p>
                                    <input className="form-input" value={login}
                                           onChange={stageLoginOnLoginChanged}/>
                                </div>
                                <div className="form-item form-width">
                                    <p className="form-item-name">{t('password')}</p>
                                    <input className="form-input" placeholder='P4ssw0rd!' type='password'
                                           onChange={stageLoginOnPasswordChanged}/>
                                </div>
                                <button className="cta form-button"
                                        onClick={() => void stageLoginOnButtonClicked()}>
                                    {t('continue')}
                                </button>
                                {
                                    passwordIncorrect &&
                                    <div className="form-item form-width">
                                        <p className="form-warn">{t('incorrectPassword')}</p>
                                    </div>
                                }
                            </div>
                        }
                        {
                            stage == 'view' &&
                            <div className="form">
                                <p className="form-title form-width">{t('hi')}, %{username}%!</p>
                                <button className="cta form-button"
                                        onClick={() => void stageViewOnButtonClicked()}>
                                    {t('logout')}
                                </button>
                            </div>
                        }
                    </div>
                </div>
            }
        </div>
    )
}

export default Profile;