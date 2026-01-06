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
    private static readonly COLLECTING_CHUNKS_INTERVAL = 5000;
    private static readonly PROCESSING_CHUNKS_INTERVAL = 6000;
    private static readonly JOINED_CHUNK_TARGET_DURATION = 12000;
    private static readonly BLOB_TYPE = {type: 'audio/webm;codecs=opus'};

    private recorder: MediaRecorder | null = null;
    private collectingIntervalDescriptor: number | null = null;
    private timer: Timer = new Timer(100);
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

    private async collectAndSendChunk(newChunk: Blob) {
        // if (newChunk.size == 0) {
        //     return;
        // }
        //
        // console.log('new chunk ', newChunk);
        // // const blob = this.chunks.length == 0 ? newChunk : new Blob([this.chunks[this.chunks.length - 1], newChunk], SpeechRecorder.BLOB_TYPE);
        // // this.chunks.push(newChunk);
        // await this.processRecordChunk(newChunk, this.timer.getTime());

        // this.chunks.push(newChunk);
        // this.chunksDurationPrefSum.push(this.timer.getTime());
        //
        // const j = this.chunks.length - 1;
        // let i = j;
        // let precedingDuration = 0;
        // for (; i >= 0; i--) {
        //     let dur = this.chunksDurationPrefSum[j];
        //     if (i > 0) {
        //         precedingDuration = this.chunksDurationPrefSum[i - 1];
        //         dur -= precedingDuration;
        //     }
        //     if (dur > SpeechRecorder.JOINED_CHUNK_TARGET_DURATION) {
        //         break;
        //     }
        // }
        //
        // const chunksToJoin = this.chunks.slice(i, j + 1);
        // const joinedChunk = new Blob(chunksToJoin, SpeechRecorder.BLOB_TYPE);
        // await this.processRecordChunk(joinedChunk, precedingDuration);
    }

    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    private onDenied(reason: any) {
        console.error('Mic access denied: ', reason);
    }

    toggle(): boolean {
        if (this.recorder) {
            if (this.recorder.state == 'inactive') {
                this.recorder.start();
                this.collectingIntervalDescriptor = setInterval(() => {
                    if (this.recorder) {
                        this.recorder.stop();
                        this.recorder.start();
                    }
                }, SpeechRecorder.COLLECTING_CHUNKS_INTERVAL);

                this.timer.resume();

                if (!this.processingIntervalDescriptor) {
                    this.collectingIntervalDescriptor = setInterval(() => {
                        void this.processChunks();
                    }, SpeechRecorder.PROCESSING_CHUNKS_INTERVAL);
                }

                return true;

            } else if (this.recorder.state == 'recording') {
                this.recorder.stop();
                if (this.collectingIntervalDescriptor) {
                    clearInterval(this.collectingIntervalDescriptor);
                }

                this.timer.pause();

                return false;
            }
        }

        return false;
    }
}
