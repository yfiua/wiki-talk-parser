# wiki-talk-parser
This little program can:

* Parse wikipedia dump files (xml) to wiki-talk networks. Original wikipedia UIDs are remained.
* "Shrink" the resulting network, so it is an unweighted directed network w/o loops, 
like in the [SNAP wiki-Talk dataset](https://snap.stanford.edu/data/wiki-Talk.html).
* Group users according to their [roles](https://en.wikipedia.org/wiki/Wikipedia:User_access_levels).

## Usage with stu
Use [stu](https://github.com/kunegis/stu) for easy lives. The only file you need is [main.stu](https://raw.githubusercontent.com/yfiua/wiki-talk-parser/master/main.stu). Simply type in `stu` or:

    $ nohup stu -k -j 3 &

Stu will automatically start downloading this program and the datasets, then parsing.
The parameter `-j` defines the number of jobs that will run in parallel.
For downloading, more than 3 is not recommended.

## Usage without stu
### Installation

Manually download the [latest jar files](https://github.com/yfiua/wiki-talk-parser/releases/latest).

### Parse

    $ java -jar parser.jar *input-file* *lang* > *output-file*

### Shrink

    $ java -jar shrinker.jar *input-file* > *output-file*
    
### Group users

    $ java -jar grouper.jar *input-file* > *output-file*

### Compilation

    $ lein with-profile parser:shrinker:grouper uberjar

## License

Copyright © 2023 Yfiua

Distributed under the Eclipse Public License either version 1.0 or any later version.
