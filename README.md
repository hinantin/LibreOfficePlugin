SQUOIA PROJECT SPELLCHECKERS
============================

1. Download LibreOffice SDK from https://www.libreoffice.org/, we do not provide the sdk as part of our project as it may be subject to changes over time.

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

5. If everything went well a qhichwav%.%%.oxt should appear.


