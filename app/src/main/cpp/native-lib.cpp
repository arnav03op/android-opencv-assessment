#include <jni.h>
#include <string>
#include <opencv2/core.hpp>
#include <opencv2/imgproc.hpp>
#include <android/log.h>

#define LOG_TAG "NativeLib"
#define ALOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)


class EdgeProcessor {
public:
    cv::Mat yMat;
    cv::Mat cannyOutput;
};

extern "C"
JNIEXPORT jlong JNICALL
Java_com_example_edgedetector_JNIBridge_createProcessor(JNIEnv *env, jclass clazz) {

    EdgeProcessor *processor = new EdgeProcessor();
    return (jlong) processor;
}

extern "C"
JNIEXPORT void JNICALL
Java_com_example_edgedetector_JNIBridge_destroyProcessor(JNIEnv *env, jclass clazz, jlong native_processor_address) {

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


    processor->yMat = cv::Mat(height, width, CV_8UC1, yPixels, y_stride);


    cv::Canny(processor->yMat, processor->cannyOutput, 80, 150);


    int outputSize = width * height;
    memcpy(outputPixels, processor->cannyOutput.data, outputSize);
}