# Logo Detection System

## Installing (Ubuntu)

1. Fork/clone the repository from GitHub.

2. Install opencv libraries:

    `$ sudo apt-get install "libopencv-*"`

3. Install sbt:

    `$ sudo echo "deb https://dl.bintray.com/sbt/debian /" | sudo tee -a
    /etc/apt/sources.list.d/sbt.list`  
    `$ sudo apt-get update`  
    `$ sudo apt-get install sbt`

4. Go to `/usr/share/OpenCV/java/` folder and copy `opencv-248.jar` into `lib`
subfolder of the project:

    `$ cd $PATH_TO_LOGO_DETECTION_REPO`  
    `$ mkdir -p lib`  
    `$ cp /usr/share/OpenCV/java/opencv-248.jar lib/`

5. Run sbt compile to ensure that everything is ok:

    `$ sbt compile`

6. Create a `twitter.config` into `config` subfolder with your Twitter account data:

    `$ mkdir -p config`  
    `$ touch config/twitter.config`

    The file should look like this (don't use `"`, `<` or `>`, just copy paste
the keys and secrets):

    `consumerKey=<your consumer key>`  
    `consumerSecret=<your consumer secret>`  
    `accessToken=<your access token>`  
    `accessTokenSecret=<your access token secret>`  

7. Run the whole thing:

    `$ sbt run`

## Setting up an Eclipse project

1. Add sbteclipse plugin to your sbt plugin file:

    `$ echo addSbtPlugin("com.typesafe.sbteclipse" % "sbteclipse-plugin" %
     "3.0.0") >> ~/.sbt/0.13/plugins/plugins.sbt`

2. Build the Eclipse project:

     `$ cd $PATH_TO_LOGO_DETECTION_REPO`  
     `$ sbt eclipse`

3. Import the project in Eclipse

## Notes

  - Didn't manage to get in running on OS X :(

