### Docker container

```
docker run -it ubuntu --name polylang /bin/bash
docker restart polylang
docker exec -it polylang /bin/bash

apt update && apt upgrade && apt install ffmpeg python3 python3.12-venv
mkdir /app
cd /app
python -m venv venv
source venv/bin/activate
pip3 install -U openai-whisper
```

### Whisper

```
whisper audio.ogg -o output -f txt --fp16 False --model base.en input/audio.ogg

whisper --help

[-h]
[--model {turbo,}]
[--model_dir MODEL_DIR] - the path to save model files; uses ~/.cache/whisper by default (default: None)
[--device DEVICE] - device to use for PyTorch inference (default: cpu)
[-o --output_dir OUTPUT_DIR] - directory to save the outputs (default: .)
[-f --output_format {txt,vtt,srt,tsv,json,all}] - format of the output file; if not specified, all available formats
                                                  will be produced (default: all)
[--verbose VERBOSE] - whether to print out the progress and debug messages (default: True)
[--task {transcribe,translate}]
[--language {...en...ru...English...Russian...] - language spoken in the audio, specify None to perform language
                                                  detection (default: None)
[--temperature TEMPERATURE] - temperature to use for sampling (default: 0)
[--best_of BEST_OF] - number of candidates when sampling with non-zero temperature (default: 5)
[--beam_size BEAM_SIZE] - number of beams in beam search, only applicable when temperature is zero (default: 5)
[--patience PATIENCE] - optional patience value to use in beam decoding, as in https://arxiv.org/abs/2204.05424, the
                        default (1.0) is equivalent to conventional beam search (default: None)
[--length_penalty LENGTH_PENALTY] - optional patience value to use in beam decoding, as in
                                    https://arxiv.org/abs/2204.05424, the default (1.0) is equivalent to conventional
                                    beam search (default: None)
[--suppress_tokens SUPPRESS_TOKENS] - comma-separated list of token ids to suppress during sampling; '-1' will suppress
                                      most special characters except common punctuations (default: -1)
[--initial_prompt INITIAL_PROMPT] - optional text to provide as a prompt for the first window. (default: None)
[--carry_initial_prompt CARRY_INITIAL_PROMPT] - if True, prepend initial_prompt to every internal decode() call. May
                                                reduce the effectiveness of condition_on_previous_text (default: False)
[--condition_on_previous_text CONDITION_ON_PREVIOUS_TEXT] - if True, provide the previous output of the model as a
                                                            prompt for the next window; disabling may make the text
                                                            inconsistent across windows, but the model becomes less
                                                            prone to getting stuck in a failure loop (default: True)
[--fp16 FP16] - whether to perform inference in fp16; True by default (default: True)
[--temperature_increment_on_fallback TEMPERATURE_INCREMENT_ON_FALLBACK] - temperature to increase when falling back when
                                                                          the decoding fails to meet either of the
                                                                          thresholds below (default: 0.2)
[--compression_ratio_threshold COMPRESSION_RATIO_THRESHOLD] - if the gzip compression ratio is higher than this value,
                                                              treat the decoding as failed (default: 2.4)
[--logprob_threshold LOGPROB_THRESHOLD] - if the average log probability is lower than this value, treat the decoding as
                                          failed (default: -1.0)
[--no_speech_threshold NO_SPEECH_THRESHOLD] - if the probability of the <|nospeech|> token is higher than this value AND
                                              the decoding has failed due to `logprob_threshold`, consider the segment
                                              as silence (default: 0.6)
[--threads THREADS] - number of threads used by torch for CPU inference; supercedes MKL_NUM_THREADS/OMP_NUM_THREADS
                      (default: 0)
[--clip_timestamps CLIP_TIMESTAMPS] - comma-separated list start,end,start,end,... timestamps (in seconds) of clips to
                                      process, where the last end timestamp defaults to the end of the file (default: 0)
audio [audio ...]x
```
