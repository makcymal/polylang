export class Timer {

    private time: number;
    private timestep: number;
    private descriptor: number | null;

    constructor(timestep: number) {
        this.time = 0;
        this.timestep = timestep;
        this.descriptor = null;
    }

    public resume() {
        this.descriptor ??= setInterval(() => this.time += this.timestep, this.timestep);
    }

    public pause() {
        if (this.descriptor != null) {
            clearInterval(this.descriptor);
        }
    }

    public getTime(): number {
        return this.time;
    }

}