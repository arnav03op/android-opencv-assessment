package com.example.app.gl;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.Log;

import com.example.app.JNIBridge;

import java.nio.ByteBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MainRenderer implements GLSurfaceView.Renderer {

    private static final String TAG = "MainRenderer";
    private final GLSurfaceView glSurfaceView;
    private FullScreenQuad quad;

    private int textureId;
    private int frameWidth, frameHeight;
    private ByteBuffer processedFrameBuffer;
    private boolean frameReady = false;
    private final Object frameSync = new Object();

    private long nativeProcessorAddress;

    public MainRenderer(GLSurfaceView view) {
        this.glSurfaceView = view;
        nativeProcessorAddress = JNIBridge.createProcessor();
    }

    public void cleanup() {
        JNIBridge.destroyProcessor(nativeProcessorAddress);
        nativeProcessorAddress = 0;
    }

    /**
     * Called from the CameraX analyzer thread.
     */
    public void processFrame(int width, int height, ByteBuffer y, ByteBuffer u, ByteBuffer v, int yStride, int uvStride) {
        synchronized (frameSync) {
            if (frameWidth != width || frameHeight != height) {
                frameWidth = width;
                frameHeight = height;

                processedFrameBuffer = ByteBuffer.allocateDirect(width * height);
            }


            JNIBridge.processFrame(
                    nativeProcessorAddress,
                    width, height,
                    y, u, v,
                    yStride, uvStride,
                    processedFrameBuffer
            );

            frameReady = true;
            glSurfaceView.requestRender();
        }
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        quad = new FullScreenQuad();
        setupTexture();
    }

    private void setupTexture() {
        int[] textures = new int[1];
        GLES20.glGenTextures(1, textures, 0);
        textureId = textures[0];

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        synchronized (frameSync) {
            if (frameReady && processedFrameBuffer != null) {
                frameReady = false;
                processedFrameBuffer.position(0);

                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);


                GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_LUMINANCE,
                        frameWidth, frameHeight, 0, GLES20.GL_LUMINANCE,
                        GLES20.GL_UNSIGNED_BYTE, processedFrameBuffer);
            }
        }


        if (frameWidth > 0) {
            quad.draw(textureId);
        }
    }
}