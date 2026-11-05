"""Synthesizes every sound in the mod (mono 44.1 kHz Ogg Vorbis via ffmpeg).

All audio is generated from scratch here. The record is an original music-box
arrangement of "Jingle Bells" (J. L. Pierpont, 1857, public domain).
"""
import os
import subprocess
import tempfile
import wave
import zlib

import numpy as np

SR = 44100


def rng(seed):
    return np.random.default_rng(zlib.crc32(seed.encode()) if isinstance(seed, str) else seed)


def env_exp(n, tau):
    t = np.arange(n) / SR
    return np.exp(-t / tau)


def bell(freq, dur, tau=0.4, partials=((1, 1), (2.76, .5), (5.4, .25), (8.93, .12)), amp=1.0):
    n = int(dur * SR)
    t = np.arange(n) / SR
    out = np.zeros(n)
    for ratio, a in partials:
        out += a * np.sin(2 * np.pi * freq * ratio * t) * np.exp(-t / (tau / (ratio ** 0.6)))
    attack = np.minimum(1, t / 0.002)
    return out * attack * amp


def noise(n, seed):
    return rng(seed).standard_normal(n)


def lowpass(x, cutoff):
    a = np.exp(-2 * np.pi * cutoff / SR)
    y = np.zeros_like(x)
    acc = 0.0
    for i, v in enumerate(x):
        acc = (1 - a) * v + a * acc
        y[i] = acc
    return y


def highpass(x, cutoff):
    return x - lowpass(x, cutoff)


def bandpass(x, lo, hi):
    return lowpass(highpass(x, lo), hi)


def mixin(dst, src, at):
    i = int(at * SR)
    end = min(len(dst), i + len(src))
    dst[i:end] += src[:end - i]


def normalize(x, peak=0.9):
    m = np.max(np.abs(x)) or 1
    return x / m * peak


def fade(x, ms=8):
    n = int(SR * ms / 1000)
    x = x.copy()
    x[:n] *= np.linspace(0, 1, n)
    x[-n:] *= np.linspace(1, 0, n)
    return x


# ------------------------------------------------------------------ effects
def sleigh_bells(seed):
    r = rng(seed)
    out = np.zeros(int(1.4 * SR))
    for shake, t0 in enumerate((0.0, 0.42, 0.78)):
        for _ in range(16):
            t = t0 + abs(r.normal(0.06, 0.05))
            f = r.uniform(2300, 4600)
            mixin(out, bell(f, 0.35, tau=r.uniform(.05, .12), amp=r.uniform(.3, 1.0)), t)
        nz = bandpass(noise(int(.2 * SR), seed + str(shake)), 4000, 9000) * env_exp(int(.2 * SR), .04) * .15
        mixin(out, nz, t0)
    return fade(normalize(out, .8))


def chime_arpeggio(notes, step=0.09, tau=0.5, amp=.6):
    out = np.zeros(int((len(notes) * step + 1.2) * SR))
    for i, f in enumerate(notes):
        mixin(out, bell(f, 1.2, tau=tau, partials=((1, 1), (2, .3), (3, .12), (4.2, .05))) * amp, i * step)
    return out


def paper(dur, seed, density=180):
    r = rng(seed)
    n = int(dur * SR)
    base = bandpass(noise(n, seed), 900, 7000)
    crackle = np.zeros(n)
    for _ in range(int(density * dur)):
        i = r.integers(0, n - 300)
        crackle[i:i + 300] += r.uniform(.3, 1) * env_exp(300, .002)
    shape = np.interp(np.arange(n), [0, n * .1, n * .6, n], [0, 1, .7, 0])
    return base * (0.25 + crackle) * shape


def present_unwrap(seed):
    out = np.zeros(int(1.6 * SR))
    mixin(out, paper(0.55, seed) * .8, 0)
    mixin(out, chime_arpeggio([1046.5, 1318.5, 1568.0, 2093.0], step=.07), .38)
    return fade(normalize(out, .85))


def present_wrap(seed):
    return fade(normalize(paper(0.45, seed, density=90), .6))


def advent_open(seed):
    out = np.zeros(int(1.3 * SR))
    click = bandpass(noise(int(.03 * SR), seed), 600, 3000) * env_exp(int(.03 * SR), .006)
    mixin(out, click * .6, 0)
    mixin(out, chime_arpeggio([783.99, 987.77, 1174.66, 1567.98], step=.08, amp=.5), .05)
    return fade(normalize(out, .8))


def giggle(seed):
    r = rng(seed)
    out = np.zeros(int(0.55 * SR))
    for i in range(3):
        n = int(.075 * SR)
        t = np.arange(n) / SR
        f0 = r.uniform(1100, 1250) * (1.0 + .08 * (2 - i))
        f = f0 * (1 + .25 * np.sin(np.pi * t / t[-1]))
        ph = 2 * np.pi * np.cumsum(f) / SR
        syl = (np.sin(ph) + .35 * np.sin(2 * ph) + .15 * np.sin(3 * ph)) * np.sin(np.pi * t / t[-1]) ** 1.5
        h = bandpass(noise(int(.02 * SR), seed + str(i)), 2000, 6000) * env_exp(int(.02 * SR), .006) * .3
        mixin(out, h, i * .13)
        mixin(out, syl * .5, i * .13 + .01)
    return fade(normalize(out, .55))


def crunch(seed, dur=0.28, hits=8):
    r = rng(seed)
    out = np.zeros(int((dur + .1) * SR))
    for k in range(hits):
        n = int(r.uniform(.004, .016) * SR)
        burst = highpass(noise(n, seed + str(k)), 1200) * env_exp(n, .004) * r.uniform(.4, 1)
        mixin(out, burst, r.uniform(0, dur))
    thump = np.sin(2 * np.pi * 140 * np.arange(int(.08 * SR)) / SR) * env_exp(int(.08 * SR), .02) * .5
    mixin(out, thump, 0)
    return fade(normalize(out, .8))


def crumble(seed):
    out = crunch(seed, dur=.6, hits=26)
    sprinkle = bandpass(noise(len(out), seed + 's'), 3000, 9000) * np.interp(np.arange(len(out)), [0, len(out) * .3, len(out)], [0, .12, 0])
    return fade(normalize(out + sprinkle, .85))


CHURCH = ((0.5, .6), (1.0, 1.0), (1.19, .45), (1.5, .35), (2.0, .4), (2.51, .2), (2.66, .15), (3.01, .1))


def krampus_toll(seed, base=98.0):
    out = np.zeros(int(3.2 * SR))
    mixin(out, bell(base, 3.0, tau=1.4, partials=CHURCH), 0)
    n = len(out)
    growl = lowpass(noise(n, seed), 260) * (1 + .6 * np.sin(2 * np.pi * 28 * np.arange(n) / SR))
    growl *= np.interp(np.arange(n), [0, n * .05, n * .5, n], [0, .4, .15, 0])
    return fade(normalize(out + growl * 1.2, .85))


def growl(seed, dur=.4, f0=95, f1=70):
    n = int(dur * SR)
    t = np.arange(n) / SR
    f = np.linspace(f0, f1, n) * (1 + .03 * rng(seed).standard_normal(n).cumsum() / np.sqrt(n))
    ph = 2 * np.pi * np.cumsum(f) / SR
    saw = 2 * (ph / (2 * np.pi) % 1) - 1
    rough = lowpass(noise(n, seed), 900) * .6
    sig = lowpass(saw + rough, 1100) * (1 + .5 * np.sin(2 * np.pi * 31 * t))
    shape = np.interp(np.arange(n), [0, n * .08, n * .6, n], [0, 1, .7, 0])
    return sig * shape


def krampus_hurt(seed):
    return fade(normalize(growl(seed, .38, 120, 80), .85))


def chains(seed, dur=.6, hits=14):
    r = rng(seed)
    out = np.zeros(int((dur + .3) * SR))
    for k in range(hits):
        f = r.uniform(1500, 3800)
        clink = bell(f, .25, tau=r.uniform(.03, .08), partials=((1, 1), (2.32, .6), (4.1, .4), (6.6, .2)))
        mixin(out, clink * r.uniform(.3, 1), r.uniform(0, dur))
    return fade(normalize(out, .75))


def krampus_death(seed):
    out = np.zeros(int(3.0 * SR))
    mixin(out, growl(seed, 1.0, 110, 55) * 1.0, 0)
    mixin(out, chains(seed + 'c', .5, 18) * .7, .6)
    mixin(out, bell(73.4, 2.0, tau=1.0, partials=CHURCH) * .6, .9)
    return fade(normalize(out, .9))


# ------------------------------------------------------------------ the record
def note_freq(name):
    names = {'C': -9, 'D': -7, 'E': -5, 'F': -4, 'G': -2, 'A': 0, 'B': 2}
    semis = names[name[0]] + (1 if '#' in name else 0) + 12 * (int(name[-1]) - 4)
    return 440.0 * 2 ** (semis / 12)


def music_box(freq, dur, amp=.5):
    n = int(dur * SR)
    t = np.arange(n) / SR
    tine = (np.sin(2 * np.pi * freq * t) + .22 * np.sin(2 * np.pi * freq * 2 * t) * np.exp(-t / .15)
            + .08 * np.sin(2 * np.pi * freq * 5.93 * t) * np.exp(-t / .04))
    return tine * np.exp(-t / .9) * np.minimum(1, t / .0015) * amp


def jingle_bells():
    bpm = 126
    beat = 60 / bpm
    chorus = [
        ('E5', 1), ('E5', 1), ('E5', 2), ('E5', 1), ('E5', 1), ('E5', 2),
        ('E5', 1), ('G5', 1), ('C5', 1.5), ('D5', .5), ('E5', 4),
        ('F5', 1), ('F5', 1), ('F5', 1.5), ('F5', .5), ('F5', 1), ('E5', 1), ('E5', 1), ('E5', .5), ('E5', .5),
        ('E5', 1), ('D5', 1), ('D5', 1), ('E5', 1), ('D5', 2), ('G5', 2),
        ('E5', 1), ('E5', 1), ('E5', 2), ('E5', 1), ('E5', 1), ('E5', 2),
        ('E5', 1), ('G5', 1), ('C5', 1.5), ('D5', .5), ('E5', 4),
        ('F5', 1), ('F5', 1), ('F5', 1.5), ('F5', .5), ('F5', 1), ('E5', 1), ('E5', 1), ('E5', .5), ('E5', .5),
        ('G5', 1), ('G5', 1), ('F5', 1), ('D5', 1), ('C5', 4),
    ]
    chords = ['C', 'C', 'C', 'C', 'F', 'C', 'D', 'G', 'C', 'C', 'C', 'C', 'F', 'C', 'G', 'C']
    triads = {'C': ('C3', 'G3', 'E4', 'G4'), 'F': ('F3', 'C4', 'A4', 'C5'), 'G': ('G3', 'D4', 'B4', 'D5'),
              'D': ('D3', 'A3', 'F#4', 'C5')}
    passes = 2
    total_beats = 64 * passes + 4
    out = np.zeros(int((total_beats * beat + 3) * SR))
    rng_ = rng(1857)
    for p in range(passes):
        t0 = p * 64 * beat
        t = t0
        for name, d in chorus:
            f = note_freq(name) * (2 if p == 1 else 1)
            mixin(out, music_box(f, d * beat + 1.2, .55), t)
            if p == 1:
                mixin(out, music_box(note_freq(name), d * beat + 1.2, .25), t)   # octave doubling
            t += d * beat
        for bar, ch in enumerate(chords):
            bass, fifth, mid, top = triads[ch]
            bt = t0 + bar * 4 * beat
            mixin(out, music_box(note_freq(bass), 2.4, .32), bt)
            mixin(out, music_box(note_freq(fifth), 2.4, .22), bt + 2 * beat)
            mixin(out, music_box(note_freq(mid), 1.6, .14), bt + 1 * beat)
            mixin(out, music_box(note_freq(top), 1.6, .12), bt + 3 * beat)
            if p == 1:
                for b in range(4):
                    for _ in range(5):
                        mixin(out, bell(rng_.uniform(3000, 5200), .2, tau=.05) * .05, bt + b * beat + abs(rng_.normal(0, .02)))
    end = passes * 64 * beat
    for i, n in enumerate(('C5', 'E5', 'G5', 'C6', 'E6', 'G6', 'C7')):
        mixin(out, music_box(note_freq(n), 2.5, .4), end + i * .09)
    mixin(out, music_box(note_freq('C3'), 3, .35), end)
    out = out[:int((end + 3.2) * SR)]
    return fade(normalize(out, .85), 30), (end + 3.2)


EFFECTS = {
    'sleigh_bells1': lambda: sleigh_bells('sb1'),
    'sleigh_bells2': lambda: sleigh_bells('sb2'),
    'present_unwrap1': lambda: present_unwrap('pu1'),
    'present_unwrap2': lambda: present_unwrap('pu2'),
    'present_wrap': lambda: present_wrap('pw'),
    'advent_open': lambda: advent_open('ao'),
    'gingerbread_giggle1': lambda: giggle('gg1'),
    'gingerbread_giggle2': lambda: giggle('gg2'),
    'gingerbread_crunch1': lambda: crunch('gc1'),
    'gingerbread_crunch2': lambda: crunch('gc2'),
    'gingerbread_crumble': lambda: crumble('gcr'),
    'krampus_toll1': lambda: krampus_toll('kt1', 98.0),
    'krampus_toll2': lambda: krampus_toll('kt2', 87.3),
    'krampus_hurt1': lambda: krampus_hurt('kh1'),
    'krampus_hurt2': lambda: krampus_hurt('kh2'),
    'krampus_death': lambda: krampus_death('kd'),
    'krampus_chains1': lambda: chains('kc1'),
    'krampus_chains2': lambda: chains('kc2', .45, 10),
}


def write_ogg(samples, path, quality=4):
    os.makedirs(os.path.dirname(path), exist_ok=True)
    pcm = (np.clip(samples, -1, 1) * 32767).astype(np.int16)
    with tempfile.NamedTemporaryFile(suffix='.wav', delete=False) as tmp:
        wav_path = tmp.name
    with wave.open(wav_path, 'wb') as w:
        w.setnchannels(1)
        w.setsampwidth(2)
        w.setframerate(SR)
        w.writeframes(pcm.tobytes())
    subprocess.run(['ffmpeg', '-loglevel', 'error', '-y', '-i', wav_path, '-c:a', 'libvorbis', '-q:a', str(quality),
                    '-ac', '1', '-map_metadata', '-1', '-fflags', '+bitexact', path], check=True)
    os.unlink(wav_path)


def generate(sound_dir):
    for name, fn in EFFECTS.items():
        write_ogg(fn(), os.path.join(sound_dir, f'{name}.ogg'))
    record, seconds = jingle_bells()
    write_ogg(record, os.path.join(sound_dir, 'records', 'jingle_bells.ogg'), quality=5)
    return len(EFFECTS) + 1, seconds
