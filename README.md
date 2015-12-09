# wiki-talk-parser

* Parse wikipedia dump files (xml) to wiki-talk networks.
* "Shrink" the resulted network, so it is an unweighted directed network w/o loops, 
like in the [SNAP wiki-Talk dataset](https://snap.stanford.edu/data/wiki-Talk.html).

## Installation

Download the latest [jar files](https://github.com/yfiua/wiki-talk-parser/releases).

## Usage
### Parse

    $ java -jar parser.jar *input-file* *lang* > *output-file*

### Shrink

    $ java -jar shrinker.jar *input-file* > *output-file*

## Compilation

    $ lein with-profile parser:shrinker uberjar

## License

Copyright © 2015 Yfiua

Distributed under the Eclipse Public License either version 1.0 or any later version.
