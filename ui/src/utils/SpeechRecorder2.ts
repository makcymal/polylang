export type SpeechChunkCallback = (
  chunk: Blob,
  start: number
) => Promise<void> | void;

export interface SpeechRecorderOptions {
  tickMs?: number;
  windowMs?: number;
  mimeType?: string;
  deviceId?: string;
};

type State = "idle" | "recording" | "paused" | "stopping";

interface Slice {
  blob: Blob;
  startMs: number;
  endMs: number;
};

export class SpeechRecorder {
  private readonly onChunk: SpeechChunkCallback;
  private readonly tickMs: number;
  private readonly windowMs: number;
  private readonly mimeType?: string;
  private readonly deviceId?: string;

  private stream: MediaStream | null = null;
  private recorder: MediaRecorder | null = null;
  private state: State = "idle";

  private slices: Slice[] = [];
  private tickTimer: number | null = null;
  private maxWindowTimer: number | null = null;

  private sessionStartPerfMs = 0;
  private currentChunkStartMs = 0;
  private lastEmittedEndMs = 0;
  private flushPending = false;

  constructor(onChunk: SpeechChunkCallback, options: SpeechRecorderOptions = {}) {
    this.onChunk = onChunk;
    this.tickMs = options.tickMs ?? 3000;
    this.windowMs = options.windowMs ?? 12000;
    this.mimeType = options.mimeType;
    this.deviceId = options.deviceId;
  }

  getStatus(): State {
    return this.state;
  }

  toggle(): boolean {
    if (this.state === "idle") {
      void this.start();
      return true;
    }

    if (this.state === "recording") {
      void this.pause();
      return false;
    }

    if (this.state === "paused") {
      void this.resume();
      return true;
    }

    return this.state === "recording";
  }

  async start(): Promise<void> {
    if (this.state !== "idle") return;

    this.stream = await navigator.mediaDevices.getUserMedia({
      audio: this.deviceId ? { deviceId: { exact: this.deviceId } } : true,
    });

    const recorderOptions: MediaRecorderOptions = {};
    if (this.mimeType && MediaRecorder.isTypeSupported(this.mimeType)) {
      recorderOptions.mimeType = this.mimeType;
    }

    this.recorder = new MediaRecorder(this.stream, recorderOptions);
    this.bindRecorderEvents();

    this.slices = [];
    this.sessionStartPerfMs = performance.now();
    this.currentChunkStartMs = 0;
    this.lastEmittedEndMs = 0;
    this.flushPending = false;

    this.recorder.start(this.tickMs);
    this.state = "recording";
    this.startTickTimer();
    this.startMaxWindowTimer();
  }

  async pause(): Promise<void> {
    if (this.state !== "recording" || !this.recorder) return;

    this.flush();
    this.clearTickTimer();
    this.clearMaxWindowTimer();
    this.recorder.pause();
    this.state = "paused";
  }

  async resume(): Promise<void> {
    if (this.state !== "paused" || !this.recorder) return;

    this.state = "recording";
    this.startTickTimer();
    this.startMaxWindowTimer();
    this.recorder.resume();
  }

  async stop(): Promise<void> {
    if (this.state === "idle") return;

    this.state = "stopping";
    this.clearTickTimer();
    this.clearMaxWindowTimer();

    if (this.recorder?.state === "recording") {
      this.flush();
    }

    if (this.recorder?.state !== "inactive") {
      this.recorder?.stop();
    }

    this.stream?.getTracks().forEach((t) => t.stop());
    this.cleanup();
  }

  private bindRecorderEvents(): void {
    if (!this.recorder) return;

    this.recorder.ondataavailable = async (event: BlobEvent) => {
      const blob = event.data;
      if (!blob || blob.size === 0) {
        this.flushPending = false;
        return;
      }

      const endMs = performance.now() - this.sessionStartPerfMs;
      const startMs = this.currentChunkStartMs;

      this.currentChunkStartMs = endMs;
      this.lastEmittedEndMs = endMs;

      this.slices.push({ blob, startMs, endMs });
      this.trimSlices(endMs);

      if (this.flushPending) {
        this.flushPending = false;
        await this.emitWindow();
      }
    };

    this.recorder.onpause = () => {
      this.clearTickTimer();
      this.clearMaxWindowTimer();
    };

    this.recorder.onresume = () => {
      if (this.state === "recording") {
        this.startTickTimer();
        this.startMaxWindowTimer();
      }
    };

    this.recorder.onerror = () => {
      void this.stop();
    };
  }

  private startTickTimer(): void {
    this.clearTickTimer();
    this.tickTimer = window.setInterval(() => {
      if (this.state !== "recording" || !this.recorder || this.recorder.state !== "recording") return;
      this.flush();
    }, this.tickMs);
  }

  private startMaxWindowTimer(): void {
    this.clearMaxWindowTimer();
    this.maxWindowTimer = window.setTimeout(() => {
      if (this.state === "recording") this.flush();
    }, this.windowMs);
  }

  private flush(): void {
    if (!this.recorder || this.recorder.state !== "recording") return;
    this.flushPending = true;
    this.recorder.requestData();
  }

  private async emitWindow(): Promise<void> {
    const endMs = this.lastEmittedEndMs;
    const startMs = Math.max(0, endMs - this.windowMs);

    const selected = this.slices.filter((s) => s.endMs > startMs && s.startMs < endMs);
    if (!selected.length) return;

    const chunk = new Blob(selected.map((s) => s.blob), {
      type: selected[0].blob.type || this.mimeType || "audio/webm",
    });

    await this.onChunk(chunk, startMs);
  }

  private trimSlices(currentEndMs: number): void {
    const keepFrom = Math.max(0, currentEndMs - this.windowMs - 2000);
    while (this.slices.length && this.slices[0].endMs < keepFrom) {
      this.slices.shift();
    }
  }

  private clearTickTimer(): void {
    if (this.tickTimer !== null) {
      clearInterval(this.tickTimer);
      this.tickTimer = null;
    }
  }

  private clearMaxWindowTimer(): void {
    if (this.maxWindowTimer !== null) {
      clearTimeout(this.maxWindowTimer);
      this.maxWindowTimer = null;
    }
  }

  private cleanup(): void {
    this.clearTickTimer();
    this.clearMaxWindowTimer();
    this.slices = [];
    this.recorder = null;
    this.stream = null;
    this.flushPending = false;
    this.state = "idle";
  }
}
