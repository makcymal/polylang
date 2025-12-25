### Whisper

```
whisper audio.ogg -o output -f txt --fp16 False --model base.en input/audio.ogg

whisper --help

[-h]
[--model {tiny,tiny.en,base,base.en,small,small.en,medium,medium.en,large,turbo}] - name of the Whisper model to use
                                                                                    (default: turbo)
[--model_dir str] - the path to save model files; uses ~/.cache/whisper by default (default: None)
[--device {cpu,gpu}] - device to use for PyTorch inference (default: cpu)
[-o --output_dir str] - directory to save the outputs (default: .)
[-f --output_format {txt,vtt,srt,tsv,json,all}] - format of the output file; if not specified, all available formats
                                                  will be produced (default: all)
[--verbose bool] - whether to print out the progress and debug messages (default: True)
[--task {transcribe,translate}] - whether to perform X->X speech recognition ('transcribe') or X->English translation
                                  ('translate') (default: transcribe)
[--language {en,ru,English,Russian...}] - language spoken in the audio, specify None to perform language detection
                                          (default: None)
[--fp16 bool] - whether to perform inference in fp16; True by default (default: True)
[--threads int] - number of threads used by torch for CPU inference; supercedes MKL_NUM_THREADS/OMP_NUM_THREADS
                  (default: 0)
[--word_timestamps bool] - extract word-level timestamps and refine the results based on them (default: False)
[--clip_timestamps (float,float)] - comma-separated list start,end,start,end,... timestamps (in seconds) of clips to
                                    process, where the last end timestamp defaults to the end of the file (default: 0)
audio [audio ...]
```

```bash
whisper -o stt/transciption -f json --verbose False --language en --fp16 False \
        --word_timestamps True --clip_timestamps 0,10 \
        --model-dir whisper-models --model small.en stt/speech/sample.wav
```

### Images:

Debian-based:

- dhi.io/python:3-debian13-dev
- dhi.io/eclipse-temurin:25-jdk-debian13-dev
- dhi.io/eclipse-temurin:25 (debian)
- dhi.io/maven:3-jdk25-debian13-dev
- dhi.io/debian-base:trixie

Alpine-based:

- dhi.io/python:3.14-alpine3.22
- dhi.io/eclipse-temurin:25-jdk-alpine3.22-dev
- dhi.io/alpine-base:3.22-alpine3.22-dev
- dhi.io/postgres:18-alpine3.22
