SQUOIA Project Spellcheckers
============================

### Institute of Computational Linguistics, University of Zurich

**Version**: 0.3-beta.3

**Last updated**: 2015/04/01

#### Authors: 
  * Annette Rios, e-mail: rios at cl.uzh.ch 
  * Richard Castro, e-mail: richard.castro.mamani at gmail.com

#### Compatibility

The plugins are developed and tested on a Ubuntu Linux system.
The source code should be reasonably portable to win 32 and Mac OSX.

### Compiling OXT Plugin 

1. (Optional) Download LibreOffice SDK from https://www.libreoffice.org/, we do not provide the sdk as part of our project as it may be subject to changes over time.

    ```bash
    $ sudo apt-get install alien dpkg-dev debhelper build-essential

    $ sudo alien packagename.rpm

    $ sudo dpkg -i packagename.deb
    ```

2. Install `ant` and `zip`:

    ```bash
    $ sudo apt-get -u install ant

    $ sudo apt-get install zip
    ```

3. Go to the `qhichwa` folder

    `$ cd qhichwa`

4. Run the following commands:

    ```bash
    $ chmod +x compile.sh

    $ ./compile.sh -xcjo
    ```

5. If everything went well a `qhichwav%.%%.oxt` should appear.

### Installing the plugin

#### Prerequisites

  * At least 4GB of RAM memory
  * LibreOffice 4.3 (x86)
  * Oracle Java 7 (x86, you need the 32bit version even if you have a 64bit machine)

### Acknowledgement

This research is funded by the **Swiss National Science Foundation** under grant *100015_132219/1*.
