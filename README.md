# Real-Time Edge Detection Viewer (R&D Intern Assignment)

This project is an Android app that captures the camera feed, performs real-time Canny edge detection in C++ (via JNI), and renders the output using OpenGL ES 2.0. It also includes a minimal TypeScript-based web viewer.

---

## 📸 Demo

![App Screenshot](screenshot.png) ---


<img width="1539" height="945" alt="Screenshot 2025-11-14 021421" src="https://github.com/user-attachments/assets/97b68862-f439-4924-a336-74a243fa6a9e" />

<img width="1888" height="977" alt="Screenshot 2025-11-14 022333" src="https://github.com/user-attachments/assets/9338cfb3-7cd3-41d8-aab9-3092d42b6e70" />


## Features Implemented 

* **Android App:**
    * Real-time camera feed using `CameraX` (`ImageAnalysis`).
    * JNI bridge passing `YUV_420_888` frame data to native C++.
    * Native C++ processing using OpenCV 4.x.
    * Efficiently uses the Y-plane as a grayscale source.
    * [cite_start]Applies Canny Edge Detection (`cv::Canny`) in C++[cite: 28].
    * [cite_start]Renders the 1-channel Canny output using OpenGL ES 2.0 (as a `GL_LUMINANCE` texture)[cite: 31].
    * [cite_start]Maintains real-time performance (tested > 15 FPS)[cite: 32].
* **Web Viewer:**
    * [cite_start]Minimal TypeScript + HTML page[cite: 34].
    * [cite_start]Displays a static sample of the processed frame[cite: 38].
    * [cite_start]Shows mock frame stats (resolution, FPS)[cite: 39].
    * [cite_start]Configured to build via `tsc`.

---

## [cite_start]🏛️ Architecture & Frame Flow 

1.  **`MainActivity.java`**: Sets up `CameraX` and a `GLSurfaceView`.
2.  **`ImageAnalysis`**: Provides camera frames as `YUV_420_888` objects on a background thread.
3.  **`MainRenderer.java`**: Receives the Y, U, and V `ByteBuffers` from the analyzer.
4.  **`JNIBridge.java`**: `MainRenderer` passes the buffers and frame metadata to the `processFrame` native method.
5.  **`native-lib.cpp`**:
    * Wraps the `yBuffer` (grayscale plane) in a `cv::Mat` without copying data.
    * Applies `cv::Canny` to this `Mat`, storing the result in a persistent `cannyOutput` `Mat`.
    * `memcpy`s the `cannyOutput.data` into the `outputBuffer` (a direct ByteBuffer) provided by Java.
6.  **`MainRenderer.java`**: On the GL thread (`onDrawFrame`), this class takes the now-filled `outputBuffer` and uploads it to a `GL_LUMINANCE` texture using `glTexImage2D`.
7.  **`FullScreenQuad.java`**: Draws a quad to the screen, sampling from the texture. The GLSL fragment shader maps the 1-channel texture to an `RGB` color.

---

## [cite_start]⚙️ Setup Instructions 

1.  **Clone:** `git clone [Your-Repo-URL]`
2.  **OpenCV:**
    * Download the "OpenCV for Android" SDK.
    * In Android Studio, import `OpenCV-android-sdk/sdk/java` as a module (`:opencv`).
    * Go to **File > Project Structure** and add `:opencv` as a dependency for the `:app` module.
3.  **NDK (C++):**
    * Copy the `OpenCV-android-sdk/sdk/native/jni` folder into `app/src/main/cpp/`.
    * Rename this new folder to `opencv`.
    * The `app/src/main/cpp/CMakeLists.txt` is configured to find OpenCV at this relative path.
4.  **Build (Android):**
    * Ensure you have the NDK installed via the SDK Manager.
    * Sync Gradle and run the `app` on a physical device.
5.  **Build (Web):**
    * `cd web`
    * Run `tsc` (requires `npm install -g typescript`).
    * Open `web/index.html` in a browser.
  
