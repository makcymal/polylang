import {Timer} from "@/utils/Timer.ts";
import type {RecordChunk} from "@/types/RecordChunk.ts";
import {encodeArrayBufferInBase64} from "@/utils/Base64Utils.ts";

const constraints: MediaStreamConstraints = {
    audio: {
        sampleRate: 8000,
        channelCount: 1,
        echoCancellation: false,
        autoGainControl: false,
        noiseSuppression: false,
        sampleSize: 8
    }
};

type RecordChunkConsumer = (chunk: RecordChunk) => void;

export class SpeechRecorder {
    private static readonly REQUESTING_BLOB_INTERVAL = 10000;
    private static readonly JOINED_CHUNK_TARGET_DURATION = 15000;
    private static readonly BLOB_TYPE = {type: 'audio/webm;codecs=opus'};

    private recorder: MediaRecorder | null = null;
    private timer: Timer = new Timer(10);
    private chunks: Blob[] = [];
    private chunksDurationPrefSum: number[] = [];
    private readonly sendChunk: RecordChunkConsumer;

    constructor(sendChunk: RecordChunkConsumer) {
        navigator.mediaDevices.getUserMedia(constraints).then(
            (stream: MediaStream) => this.onPermitted(stream),
            // eslint-disable-next-line @typescript-eslint/no-explicit-any
            (reason: any) => this.onDenied(reason)
        );
        this.sendChunk = sendChunk;
    }

    private onPermitted(stream: MediaStream) {
        this.recorder = new MediaRecorder(stream);
        this.recorder.ondataavailable = async (evt: BlobEvent) => {
            await this.collectAndSendChunk(evt.data);
        };
    }

    private async collectAndSendChunk(newChunk: Blob) {
        if (newChunk.size == 0) {
            return;
        }

        this.chunks.push(newChunk);
        this.chunksDurationPrefSum.push(this.timer.getTime());

        const j = this.chunks.length - 1;
        let i = j;
        let precedingDuration = 0;
        for (; i >= 0; i--) {
            let dur = this.chunksDurationPrefSum[j];
            if (i > 0) {
                precedingDuration = this.chunksDurationPrefSum[i - 1];
                dur -= precedingDuration;
            }
            if (dur > SpeechRecorder.JOINED_CHUNK_TARGET_DURATION) {
                break;
            }
        }

        const chunksToJoin = this.chunks.slice(i, j + 1);
        const joinedChunk = new Blob(chunksToJoin, SpeechRecorder.BLOB_TYPE);

        const buf = await joinedChunk.arrayBuffer();
        this.sendChunk({data: encodeArrayBufferInBase64(buf), start: precedingDuration})
    }

    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    private onDenied(reason: any) {
        console.error('Mic access denied: ', reason);
    }

    toggle(): boolean {
        if (this.recorder) {
            if (this.recorder.state == 'inactive') {
                this.recorder.start();
                setInterval(() => {
                    this.recorder!.requestData();
                }, SpeechRecorder.REQUESTING_BLOB_INTERVAL);
                this.timer.start();
                return true;
            } else if (this.recorder.state == 'paused') {
                this.recorder.resume();
                this.timer.resume();
                return true;
            } else if (this.recorder.state == 'recording') {
                this.recorder.pause();
                this.timer.pause();
                return false;
            }
        }

        return false;
    }
}
