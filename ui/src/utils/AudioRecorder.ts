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

export class AudioRecorder {
    private static readonly GATHERING_BLOBS_INTERVAL = 10000;

    private recorder: MediaRecorder | null = null;
    private readonly sendData: (blob: ArrayBuffer) => void;

    constructor(sendData: (data: ArrayBuffer) => void) {
        navigator.mediaDevices.getUserMedia(constraints).then(
            (stream: MediaStream) => this.onPermitted(stream),
            // eslint-disable-next-line @typescript-eslint/no-explicit-any
            (reason: any) => this.onDenied(reason)
        );
        this.sendData = sendData;
    }

    private onPermitted(stream: MediaStream) {
        this.recorder = new MediaRecorder(stream);
        this.recorder.ondataavailable = async (evt: BlobEvent) => {
            console.log(evt.data);
            await evt.data.arrayBuffer().then((data: ArrayBuffer) => {
                if (data.byteLength > 0) {
                    this.sendData(data);
                }
            });
        };
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
                }, AudioRecorder.GATHERING_BLOBS_INTERVAL);
                return true;
            } else if (this.recorder.state == 'paused') {
                this.recorder.resume();
                return true;
            } else if (this.recorder.state == 'recording') {
                this.recorder.pause();
                return false;
            }
        }

        return false;
    }
}
