import {Timer} from "@/utils/Timer.ts";

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

type RecordChunkConsumer = (chunk: Blob, start: number) => Promise<void>;

export class SpeechRecorder {
    private static readonly COLLECTING_CHUNKS_INTERVAL = 3000;
    private static readonly PROCESSING_CHUNKS_INTERVAL = 4000;
    private static readonly JOINED_CHUNK_TARGET_DURATION = 12000;
    private static readonly BLOB_TYPE = {type: 'audio/webm;codecs=opus'};

    private recorder: MediaRecorder | null = null;
    private collectingIntervalDescriptor: number | null = null;
    private timer: Timer = new Timer(10);
    private chunks: Blob[] = [];
    private chunkDurations: number[] = [];
    private totalChunksTime = 0;
    private nChunks = 0;
    private processedChunks = 0;
    private processingIntervalDescriptor: number | null = null;
    private readonly processRecordChunk: RecordChunkConsumer;

    constructor(sendChunk: RecordChunkConsumer) {
        navigator.mediaDevices.getUserMedia(constraints).then(
            (stream: MediaStream) => this.onPermitted(stream),
            // eslint-disable-next-line @typescript-eslint/no-explicit-any
            (reason: any) => this.onDenied(reason)
        );
        this.processRecordChunk = sendChunk;
    }

    private onPermitted(stream: MediaStream) {
        this.recorder = new MediaRecorder(stream);
        this.recorder.ondataavailable = (evt: BlobEvent) => {
            console.log('got new data: ', evt.data);
            this.chunks.push(evt.data);
            const currTime = this.timer.getTime();
            this.chunkDurations.push(currTime - this.totalChunksTime);
            this.totalChunksTime = currTime;
            this.nChunks++;
        };
    }

    private async processChunks() {
        if (this.nChunks <= this.processedChunks) {
            return;
        }

        let nJoinedChunks = this.nChunks - this.processedChunks;
        if (nJoinedChunks + 1 <= this.nChunks) {
            nJoinedChunks++;
        }

        this.processedChunks = this.nChunks;

        let joinedChunkDuration = 0;
        for (let i = 0; i < nJoinedChunks; i++) {
            joinedChunkDuration += this.chunkDurations[this.nChunks - 1 - i];
        }

        while (nJoinedChunks + 1 <= this.nChunks) {
            joinedChunkDuration += this.chunkDurations[this.nChunks - 1 - nJoinedChunks];
            nJoinedChunks++;
            if (joinedChunkDuration > SpeechRecorder.JOINED_CHUNK_TARGET_DURATION) {
                break;
            }
        }

        console.log(`processing ${nJoinedChunks} chunks of ${joinedChunkDuration} ms at ${this.timer.getTime()} ms`);
        const joinedChunk = new Blob(this.chunks.slice(this.nChunks - nJoinedChunks, this.nChunks), SpeechRecorder.BLOB_TYPE);
        await this.processRecordChunk(joinedChunk, this.totalChunksTime - joinedChunkDuration);
    }

    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    private onDenied(reason: any) {
        console.error('Mic access denied: ', reason);
    }

    toggle(): boolean {
        if (this.recorder) {
            console.log('recorder state before toggling: ', this.recorder.state);
            if (this.recorder.state == 'inactive') {
                this.recorder.start();
                this.collectingIntervalDescriptor = setInterval(() => {
                    if (this.recorder) {
                            this.recorder.stop();
                        this.recorder.start();
                    }
                }, SpeechRecorder.COLLECTING_CHUNKS_INTERVAL);

                this.timer.resume();

                this.processingIntervalDescriptor ??= setInterval(() => {
                    void this.processChunks();
                }, SpeechRecorder.PROCESSING_CHUNKS_INTERVAL);

                console.log('recorder state after toggling: ', this.recorder.state);
                return true;

            } else if (this.recorder.state == 'recording') {
                this.recorder.stop();
                if (this.collectingIntervalDescriptor) {
                    clearInterval(this.collectingIntervalDescriptor);
                }

                this.timer.pause();

                console.log('recorder state after toggling: ', this.recorder.state);
                return false;
            }
        }

        return false;
    }
}
