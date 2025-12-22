import {useRef, useState, useEffect} from 'react'
import TextPanel from "@/components/TextPanel.tsx";
import "./Gym.css"
import {type Theme, LIGHT} from "@/types/Theme.ts";

const textToTranslateTitle = 'Текст для перевода'
const textToTranslateExample =
    'Он задумался о том, как быстрее заговорить на английском без страха, и решил практиковать устный перевод вслух'

const transcribedTranslationTitle = "Ваш перевод"
const transcribedTranslationPlaceholder = "—"
const transcribedTranslationHint = "Текст можно редактировать"

const translationAnalysisPlaceholder = "Здесь будет анализ вашего перевода"

interface GymProps {
    theme: Theme
}

export const Gym = ({theme}: GymProps) => {

    const [textToTranslate] = useState<string>(textToTranslateExample)
    const [transcribedTranslation, setTranscribedTranslation] = useState<string>('')
    const [isTranslationRecording, setIsTranslationRecording] = useState(false)
    const [recordingSeconds, setRecordingSeconds] = useState(0)
    const recordingSecondsInterval = useRef<number>(0)
    const [translationAnalysis, setTranslationAnalysis] = useState<string>('')

    const exploreTextsToTranslate = () => { /* empty */
    }

    const toggleTranslationRecording = () => {
        setIsTranslationRecording((recording) => !recording)
    }

    const getRecordingTimer = (): string => {
        const mins = Math.floor(recordingSeconds / 60);
        const secs = recordingSeconds % 60;

        return `${mins.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`;
    };

    useEffect(() => {
        if (isTranslationRecording) {
            recordingSecondsInterval.current = setInterval(() => {
                setRecordingSeconds(prevTime => prevTime + 1);
            }, 1000);
        } else {
            clearInterval(recordingSecondsInterval.current);
        }

        return () => {
            if (recordingSecondsInterval.current) {
                clearInterval(recordingSecondsInterval.current);
            }
        };
    }, [isTranslationRecording]);

    const analyzeTranslation = () => {
        setTranslationAnalysis('Пока это заглушка. После отправки перевода здесь появится анализ и подсказки')
    }

    return (
        <main className="gym">
            <div className="translation-controls">
                <div className="button-with-hint">
                    <button className="cta translation-button" onClick={exploreTextsToTranslate} type="button">
                        <img
                            alt={'Выбрать текст для перевода'}
                            src={theme === LIGHT ? '/compass.black.png' : '/compass.white.png'}
                            className="explore-texts-icon"
                        />
                    </button>
                    <p className="hint">Выбрать текст для перевода</p>
                </div>

                <div className="button-with-hint">
                    <button className="cta translation-button" onClick={toggleTranslationRecording} type="button">
                        <img
                            alt={'Начать переводить'}
                            src={
                                theme === LIGHT
                                    ? (isTranslationRecording ? '/micro.red-on-white.png' : '/micro.black-on-white.png')
                                    : (isTranslationRecording ? '/micro.red-on-black.png' : '/micro.yellow-on-black.png')
                            }
                            className="toggle-recording-icon"
                        />
                        <p className={isTranslationRecording ? "recording-timer blink" : "recording-timer"}>{getRecordingTimer()}</p>
                    </button>
                    <p className="hint">{isTranslationRecording ? 'Приостановить перевод' : 'Начать переводить'}</p>
                </div>
            </div>

            <div className="translation-contents">
                <div className="text-to-translate-wr">
                    <TextPanel
                        title={textToTranslateTitle}
                        text={textToTranslate}
                        isEditable={false}
                    />
                </div>
                <div className="transcribed-translation-wr">
                    <TextPanel
                        title={transcribedTranslationTitle}
                        text={transcribedTranslation}
                        isEditable={true}
                        onTextEdit={setTranscribedTranslation}
                        placeholder={transcribedTranslationPlaceholder}
                        hint={transcribedTranslationHint}
                    />
                </div>
            </div>

            <div className="analysis">
                <div className="analysis-controls">
                    <button className="cta analyze-btn" type="button" onClick={analyzeTranslation}>
                        Анализировать перевод
                    </button>
                </div>
                <TextPanel
                    text={translationAnalysis}
                    placeholder={translationAnalysisPlaceholder}
                />

            </div>
        </main>
    )
}

export default Gym
