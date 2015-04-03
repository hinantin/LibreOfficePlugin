SQUOIA Project Spellcheckers
============================

**Version**: 0.3-beta.3

**Last update date**: 2015/04/01

Authors: 
  * Annette Rios, e-mail: rios at cl.uzh.ch 
  * Richard Castro, e-mail: rcastro at hinantin.com

### Compiling/installing 

1. (Optional) Download LibreOffice SDK from https://www.libreoffice.org/, we do not provide the sdk as part of our project as it may be subject to changes over time.

    `$ sudo apt-get install alien dpkg-dev debhelper build-essential`

    `$ sudo alien packagename.rpm`

    `$ sudo dpkg -i packagename.deb`

2. Install `ant` and `zip`:

    `$ sudo apt-get -u install ant`

    `$ sudo apt-get install zip`

3. Go to the `qhichwa` folder

    `$ cd qhichwa`

4. Run the following commands:

    `$ chmod +x compile.sh`

    `$ ./compile.sh -xcjo`

5. If everything went well a `qhichwav%.%%.oxt` should appear.


