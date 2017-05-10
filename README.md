I made this for a job interview. I was rejected based on the utterly abysmal quality of my effort.

So I named the game "Rejection".

Anyhow, I decided to make it fun for *me* to play. I started by making a TTY/command-line version, but java is not real good at that and there was too much annoying screen-flashing when playing high-complexity layouts; so I made a Swing version that prefers to play full screen (only tested on MS Windows, because I'm one of those jerks who like MS Windows).

You can play pretty easily if you have an Oracle JDK installed and a decent unix-like shell. Type:

    ./build.sh
    ./play.sh --help

to get options, or leave off the `--help` to play some with some semi-decent defaults.
Note that for the `--config` option, there are sample configurations in the `sample-configs` directory.


