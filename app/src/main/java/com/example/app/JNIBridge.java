package com.example.app;
import java.nio.ByteBuffer;

public class JNIBridge {
    static {
        // This matches the library name in CMakeLists.txt
        System.loadLibrary("native-lib");
    }

    /**
     * Initializes the native processor.
     * @return A 'long' (C++ pointer) to the native processor object.
     */
    public static native long createProcessor();

    /**
     * Cleans up the native processor.
     * @param nativeProcessorAddress The pointer from createProcessor().
     */
    public static native void destroyProcessor(long nativeProcessorAddress);

    /**
     * Processes the YUV frame.
     * @param nativeProcessorAddress The pointer from createProcessor().
     * @param width Width of the frame.
     * @param height Height of the frame.
     * @param yBuffer The Y-plane (grayscale) data.
     * @param uBuffer The U-plane data.
     * @param vBuffer The V-plane data.
     * @param yStride Row stride for the Y-plane.
     * @param uvStride Row stride for the U/V-planes.
     * @param outputBuffer The direct ByteBuffer to write the Canny output to.
     */
    public static native void processFrame(
            long nativeProcessorAddress,
            int width, int height,
            ByteBuffer yBuffer, ByteBuffer uBuffer, ByteBuffer vBuffer,
            int yStride, int uvStride,
            ByteBuffer outputBuffer
    );
}
