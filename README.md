# Cortest

Cortest is an Android application to assist in the assessment of epileptic seizures.  
It has been developed in the context of an engineering academic project, you can learn more about this project and its context on the [official documentation website](https://nolanbeaujault.github.io/cortest-docs/).

## Installation

Use the following command line to clone the repository :

```bash
git clone https://gitlab.com/a.baudry/cortest.git
```

## Download APK

You can download the latest debug version of the app directly from our CI builds:

👉 [Download the latest APK](https://gitlab.com/a.baudry/cortest/-/jobs/artifacts/main/raw/app/build/outputs/apk/debug/app-debug.apk?job=build)

> This APK is generated automatically by our CI/CD pipeline on every push to the `main` branch.
> It is intended for testing and demonstration purposes only.


### Prerequisites

Make sure the following is also installed in your development environment:

- A recent version of **[Android Studio](https://developer.android.com/studio?hl=fr)** (Koala 2024.1.2 or Ladybug 2024.2.1 are recommended).  
  This application has been developed with Android Studio Koala (2024.1.2) and Android Studio Ladybug (2024.2.1) versions. Installation for these versions can be found [here](https://developer.android.com/studio/archive).

> **Android Studio** will automatically handle SDK and tool installations when you open the project.  
> For reference, this project uses:
> - `compileSdk` = 34
> - `targetSdk` = 34
> - `minSdk` = 26
> - JDK 8 or higher


## Usage

Open the project with Android Studio.

To use the application, you should first build the project in the IDE.  
Please note that the AGP (Android Gradle Plugin) version must be compatible with your version of Android Studio before building.  
If it is not the case, you can directly change the AGP version in the file `cortest/gradle/libs.versions.toml`, here is an example of what you may find in it :


```toml title="cortest/gradle/libs.versions.toml"
# ...

[versions]
activityCompose = "1.9.3"
activityComposeVersion = "1.7.2"
agp = "8.6.0" # --> you can change the AGP version here, for reference 8.6.0 is the version compatible with Android Studio Koala
androidxCameraCameraCore3 = "1.5.0"
androidxCoreKtx = "1.12.0"
androidxJunit = "1.1.5"
androidxLifecycleRuntimeKtx = "2.6.1"
androidxMaterial = "1.5.2"

# ...
```

If the build was successful, you should be able to run the application on a physical device connected via USB cable or over WiFi or on a virtual device directly on Android Studio.

Running the project will install the app on your device. You will then be able to launch the app through its icon or the widget.

## Authors and acknowledgment

**Project members:**
- Maïwen Mille
- Nolan Beaujault
- Apolline Baudry
- Berkay Oztas

**Client:**
- Dr. Lucas Gauer - University Hospital of Strasbourg


## Contributing

We welcome contributions!  
If you want to contribute to the development of this application, you can ask our original members for permission.

## License

This project is licensed under the GNU General Public License v3.0 or later.
