Automation Project - Selenium 
==================================================

This project requires Java 1.8 JDK and Maven (3.3 or higher) to be installed in your environment.

[Maven 3.3.3](http://maven.apache.org/download.cgi)



## Project setup

### Environment
1. JAVA_HOME must be setted.
2. ANDROID_HOME pointing to Android sdk.
3. Android platform tools, Java commands and Maven must be in PATH.
4. NodeJS and NPM must be installed.

Another way for checking this requirements is to install Appium-doctor which runs an analysis for the local environment. See Appium installation.


### Compilation
After cloning the repo, run the next commands from the top of the directory tree.

```
mvn dependency:resolve
mvn eclipse:eclipse
mvn clean compile 
```
If dependency plugin failed due a missing SQL Server library, that one must be installed manually from command line.

### Install JDBC Driver for Microsoft SQL Server manually

1. Go to `src/etc`.
2. Install the driver in your maven's local repository executing the following command:
```
mvn install:install-file -Dfile=sqljdbc4.jar -Dpackaging=jar -DgroupId=com.microsoft.sqlserver -DartifactId=sqljdbc4 -Dversion=4.0
```

## Appium

Requirements

1. iOS: Apple’s UIAutomation
2. Android 4.2+: Google’s UiAutomator
3. Android 2.3+: Google’s Instrumentation. (Instrumentation support is provided by bundling a separate project, Selendroid)

> **Important**: Webviews are only supported through Appium's native backend in 4.4. In lower version you need to set the 
> 'automationName' capability to 'Selendroid' for it to work.

Install Appium

```
sudo npm install appium -g 
sudo npm install appium-doctor -g
```
> Run `appium-doctor` for environment checking. If everything goes fine, Appium is ready to be executed.


## Parameters

|Parameter Name|Description|
|:--------------|:-------------|
|`selenium.browser`|One of `firefox`, `chrome` , `safari` or `grid`|
|`selenium.proofsDir`|Screenshot (and other kind of proofs) output directory|
|`selenium.config`|XML path for test's input configuration|
|`selenium.gridUrl`|Url of Selenium HUB. Only if `grid` browser was selected|
|`selenium.threadsPool`|Number of threads available for running test in parallel|
|`selenium.suiteFile`|Name of the suite xml to run|


## Running tests from Command line

```
mvn test
```
Optionally, a browser can be selected explicitly (default: Firefox)

```
mvn test -Dselenium.browser=chrome
```
The screnshots directory can be selected explicitly
```
mvn test -Dselenium.proofsDir=/home/user/test
```


## Running tests in Eclipse

1. Go to Debug Configurations 
2. New TestNG test 
3. / Arguments / VM Arguments add the following code
```
-Dselenium.config=src/test/resources/com/ideasquefluyen/selenium/xctrl/selenium/configuration.xml -Dselenium.screenshotDir=target/screenshots -Dselenium.browser=firefox
```
4. Click apply
5. Run


## Appium testing 
The following parameters only affects the appium automation script. 

|Parameter Name|Description|
|:--------------|:-------------|
|`selenium.browser`|`hybrid`|
|`selenium.hybridApk`|Apk filename from /com/ideasquefluyen/selenium/xctrl/android|
|`selenium.hybridHost`|Appium server host|
|`selenium.hybridPort`|Appium server port|
|`selenium.hybridBootstrapPort`|(Android-only) port to use on device to talk to Appium|
|`selenium.hybridChromedriverPort`|Port upon which ChromeDriver will run|
|`selenium.hybridPackage`|Package name of the android application|
|`selenium.hybridDeviceName`|Device id|
|`selenium.hybridEngine`|one of `Appium` or `Selendroid`|
|`selenium.hybridApk`|name of the APK file|
|`selenium.hybridPackage`|asss|
|`selenium.hybridAndroidVersion`|Android version installed in the device|

Example:

```
mvn clean test -Dselenium.proofsDir=target/proofs -Dselenium.config="." -Dselenium.browser=hybrid -Dselenium.hybridHost=localhost -Dselenium.hybridPort=4721 -Dselenium.hybridBootstrapPort=2251 -Dselenium.hybridChromedriverPort=9515  -Dselenium.hybridDeviceName=9599544c -Dselenium.hybridApk=xctrl.apk -Dselenium.hybridPackage=com.ideasquefluyen.Xctrl -Dselenium.hybridEngine=Appium -Dselenium.hybridAndroidVersion=5.0 -Dselenium.suiteFile=happyPath.xml
```

### Appium wake-up profile (buggy)

Adding this profile will unlock the device before running automated script. Depends on `selenium.hybridDeviceName`

```
-Pandroid-wakeup-device
```

### Output directory for parallel running
This profile will override the default `target` maven's build directory. Used in conjunction with `buildDirectory` property.

```
-PotherOutputDir -DbuildDirectory=otherNameReplacingTargetDir
```

### Snapshot when test fails

Adding `snapshotOnFail` as a test parameter, the framework will takes a snapshot as proof of the failure storing it within the `selenium.proofsDir` directory.  

```
<parameter name="snapshotOnFail" value="true"></parameter>
```




