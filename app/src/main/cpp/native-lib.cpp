#include <jni.h>
#include <string>
#include <opencv2/core.hpp>
#include <opencv2/imgproc.hpp>
#include <android/log.h>

#define LOG_TAG "NativeLib"
#define ALOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

// We create a C++ class to hold our persistent Mat objects
// This avoids reallocating memory on every frame
class EdgeProcessor {
public:
    cv::Mat yMat;
    cv::Mat cannyOutput;
};

extern "C"
JNIEXPORT jlong JNICALL
Java_com_example_edgedetector_JNIBridge_createProcessor(JNIEnv *env, jclass clazz) {
    // Allocate the processor on the heap and return its pointer as a long
    EdgeProcessor *processor = new EdgeProcessor();
    return (jlong) processor;
}

extern "C"
JNIEXPORT void JNICALL
Java_com_example_edgedetector_JNIBridge_destroyProcessor(JNIEnv *env, jclass clazz, jlong native_processor_address) {
    // Cast the long back to a pointer and delete it
    EdgeProcessor *processor = (EdgeProcessor *) native_processor_address;
    delete processor;
}

extern "C"
JNIEXPORT void JNICALL
Java_com_example_edgedetector_JNIBridge_processFrame(JNIEnv *env, jclass clazz,
                                                     jlong native_processor_address,
                                                     jint width, jint height,
                                                     jobject y_buffer, jobject u_buffer, jobject v_buffer,
                                                     jint y_stride, jint uv_stride,
                                                     jobject output_buffer) {
    // Get the processor object
    EdgeProcessor *processor = (EdgeProcessor *) native_processor_address;
    if (processor == nullptr) {
        ALOGE("Processor is null");
        return;
    }

    // Get direct buffer access
    uint8_t *yPixels = (uint8_t *) env->GetDirectBufferAddress(y_buffer);
    uint8_t *outputPixels = (uint8_t *) env->GetDirectBufferAddress(output_buffer);

    if (yPixels == nullptr || outputPixels == nullptr) {
        ALOGE("Buffer is null");
        return;
    }

    // 1. Create a cv::Mat wrapper around the Y-plane (grayscale) data
    //    The Y-plane is our grayscale image. We only need this for Canny.
    processor->yMat = cv::Mat(height, width, CV_8UC1, yPixels, y_stride);

    // 2. Apply Canny Edge Detection [cite: 28]
    //    We use the yMat as input and processor->cannyOutput as the result
    cv::Canny(processor->yMat, processor->cannyOutput, 80, 150);

    // 3. Copy the processed data to the output buffer
    //    cannyOutput is CV_8UC1 (1-channel), matching our output buffer
    int outputSize = width * height;
    memcpy(outputPixels, processor->cannyOutput.data, outputSize);
}