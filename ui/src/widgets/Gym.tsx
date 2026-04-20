import {useEffect, useRef, useState} from 'react';
import {LIGHT, type Theme} from '@/types/Theme.ts';
import {SpeechRecorder} from '@/utils/SpeechRecorder.ts';
import {getRandomText} from '@/api/TextsService.ts';
import TextPanel from '@/components/TextPanel.tsx';
import '@/widgets/Gym.css';
import {getNewTalkId, getTalkTranscription, appendRecordChunk} from "@/api/TalksService.ts";
import type {Text} from "@/types/Text.ts"
import {useTranslation} from "react-i18next";

interface Props {
    theme: Theme;
}

export const Gym = ({theme}: Props) => {
    const {t} = useTranslation();

    const [textToTranslate, setTextToTranslate] = useState<Text>({
        id: '',
        content: '',
        language: 'RUSSIAN',
        intendedLevel: 'A1'
    });

    const textId = useRef<string | null>(null);
    const talkId = useRef<string | null>(null);

    const processRecordChunk = async (chunk: Blob, start: number): Promise<void> => {
        console.log(chunk, start);
        if (!talkId.current) {
            if (textId.current) {
                await getNewTalkId(textId.current)
                    .then(id => talkId.current = id)
                    .catch(e => console.error(e));
            }
        }
        if (talkId.current) {
            void appendRecordChunk(talkId.current, start, chunk);
        }
    }

    const recorder = useRef(new SpeechRecorder(processRecordChunk));
    const [isRecording, setIsRecording] = useState(false);

    const timerInterval = useRef<number | null>(null);
    const timerSeconds = useRef(0);
    const [timerFormatted, setTimerFormatted] = useState('00:00');

    const transcribeInterval = useRef<number | null>(null);
    const [transcribedRecord, setTranscribedRecord] = useState('');
    const [translationAnalysis, setTranslationAnalysis] = useState('');


    const toggleRecording = () => {
        const _is_recording = recorder.current.toggle();

        setIsRecording(_is_recording);

        if (_is_recording) {
            timerInterval.current = setInterval(() => {
                timerSeconds.current += 1;
                setTimerFormatted(getTimerFormatted());
            }, 1000);
            transcribeInterval.current ??= setInterval(async () => {
                if (talkId.current) {
                    const transcription = await getTalkTranscription(talkId.current);
                    setTranscribedRecord(transcription);
                }
            }, 2000);
        } else {
            if (timerInterval.current) {
                clearInterval(timerInterval.current);
                timerInterval.current = null;
            }
        }
    };

    const getTimerFormatted = () => {
        const minutes = Math.floor(timerSeconds.current / 60)
            .toString()
            .padStart(2, '0');
        const seconds = (timerSeconds.current % 60).toString().padStart(2, '0');
        return `${minutes}:${seconds}`;
    };

    useEffect(() => {
        const fetchTextToTranslate = async () => {
            const text = await getRandomText();
            setTextToTranslate(text);
        }
        void fetchTextToTranslate();
    }, []);

    useEffect(() => {
        textId.current = textToTranslate.id
    }, [textToTranslate]);

    const analyzeTranslation = () => {
        setTranslationAnalysis('Пока это заглушка. После отправки перевода здесь появится анализ и подсказки');
    };

    return (
        <main className="gym">
            <div className="translation-controls">
                <div className="button-with-hint">
                    <button className="cta translation-button" type="button">
                        <img
                            alt={t('selectText')}
                            src={theme === LIGHT ? '/compass.black.png' : '/compass.white.png'}
                            className="explore-texts-icon"
                        />
                    </button>
                    <p className="hint">{t('selectText')}</p>
                </div>

                <div className="button-with-hint">
                    <button className="cta translation-button" onClick={toggleRecording} type="button">
                        <img
                            alt={t('startTranslation')}
                            src={
                                theme === LIGHT
                                    ? isRecording
                                        ? '/micro.red-on-white.png'
                                        : '/micro.black-on-white.png'
                                    : isRecording
                                        ? '/micro.red-on-black.png'
                                        : '/micro.yellow-on-black.png'
                            }
                            className="toggle-recording-icon"
                        />
                        <p className={isRecording ? 'recording-timer blink' : 'recording-timer'}>{timerFormatted}</p>
                    </button>
                    <p className="hint">
                        {
                            isRecording
                                ? t('pauseTranslating')
                                : (
                                    timerFormatted == '00:00'
                                        ? t('startTranslating')
                                        : t('resumeTranslating')
                                )
                        }
                    </p>
                </div>
            </div>

            <div className="translation-contents">
                <div className="text-to-translate-wr">
                    <TextPanel title={t('textToTranslate')} text={textToTranslate.content} isEditable={false}/>
                </div>
                <div className="transcribed-translation-wr">
                    <TextPanel
                        title={t('yourTranslation')}
                        text={transcribedRecord}
                        isEditable={true}
                        onTextEdit={setTranscribedRecord}
                        placeholder='—'
                        hint={t('textIsEditable')}
                    />
                </div>
            </div>

            <div className="analysis">
                <div className="analysis-controls">
                    <button className="cta analyze-btn" type="button" onClick={analyzeTranslation}>
                        {t('analyzeTranslation')}
                    </button>
                </div>
                <TextPanel text={translationAnalysis} placeholder={t('hereWillBeAnalysis')}/>
            </div>
        </main>
    );
};

export default Gym;
