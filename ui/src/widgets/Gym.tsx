import {useEffect, useRef, useState} from 'react';
import {LIGHT, type Theme} from '@/types/Theme.ts';
import {SpeechRecorder} from '@/utils/SpeechRecorder.ts';
import {getRandomText} from '@/api/TextsService.ts';
import TextPanel from '@/components/TextPanel.tsx';
import '@/widgets/Gym.css';
import {getNewTalkId, sendRecordChunk} from "@/api/TalksService.ts";
import type {RecordChunk} from "@/types/RecordChunk.ts";

const textToTranslateTitle = 'Текст для перевода';
const textToTranslateExample =
    'Он задумался о том, как быстрее заговорить на английском без страха, и решил практиковать устный перевод вслух';

const transcribedTranslationTitle = 'Ваш перевод';
const transcribedTranslationPlaceholder = '—';
const transcribedTranslationHint = 'Текст можно редактировать';

const translationAnalysisPlaceholder = 'Здесь будет анализ вашего перевода';

interface Props {
    theme: Theme;
}

export const Gym = ({theme}: Props) => {
    const [textToTranslate, setTextToTranslate] = useState(textToTranslateExample);

    const talkId = useRef<string | null>(null);

    const sendRecord = (chunk: RecordChunk) => {
        if (!talkId.current) {
            getNewTalkId()
                .then(id => {
                    talkId.current = id
                })
                .catch(err => {
                    console.error(err)
                });
        }
        if (talkId.current) {
            void sendRecordChunk(talkId.current, chunk);
        }
    }

    // eslint-disable-next-line react-hooks/refs
    const recorder = useRef(new SpeechRecorder(sendRecord));
    const [isRecording, setIsRecording] = useState(false);

    const timerDescriptor = useRef<number | null>(null);
    const timerSeconds = useRef(0);
    const [timerFormatted, setTimerFormatted] = useState('00:00');

    const [transcribedRecord, setTranscribedRecord] = useState('');
    const [translationAnalysis, setTranslationAnalysis] = useState('');


    const exploreTextsToTranslate = () => {
        /* empty */
    };

    const getTextToTranslate = () => {
        void getRandomText()
            .then(text => {
                setTextToTranslate(text.content);
            })
            .catch(err => {
                console.error(err);
            });
    };

    const toggleRecording = () => {
        const _is_recording = recorder.current.toggle();

        setIsRecording(_is_recording);

        if (_is_recording) {
            timerDescriptor.current = setInterval(() => {
                timerSeconds.current += 1;
                setTimerFormatted(getTimerFormatted());
            }, 1000);
        } else {
            if (timerDescriptor.current) {
                clearInterval(timerDescriptor.current);
                timerDescriptor.current = null;
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
        getTextToTranslate();
    }, []);

    const analyzeTranslation = () => {
        setTranslationAnalysis('Пока это заглушка. После отправки перевода здесь появится анализ и подсказки');
    };

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
                    <button className="cta translation-button" onClick={toggleRecording} type="button">
                        <img
                            alt={'Начать переводить'}
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
                    <p className="hint">{isRecording ? 'Приостановить перевод' : 'Начать переводить'}</p>
                </div>
            </div>

            <div className="translation-contents">
                <div className="text-to-translate-wr">
                    <TextPanel title={textToTranslateTitle} text={textToTranslate} isEditable={false}/>
                </div>
                <div className="transcribed-translation-wr">
                    <TextPanel
                        title={transcribedTranslationTitle}
                        text={transcribedRecord}
                        isEditable={true}
                        onTextEdit={setTranscribedRecord}
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
                <TextPanel text={translationAnalysis} placeholder={translationAnalysisPlaceholder}/>
            </div>
        </main>
    );
};

export default Gym;
