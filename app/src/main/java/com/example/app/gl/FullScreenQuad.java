package com.example.app.gl;

import android.opengl.GLES20;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class FullScreenQuad {

    // GLSL Shaders [cite: 15]
    // The Canny output is 1-channel, so we read from the 'r' component
    // and use it for all R, G, and B output channels.
    private final String VERTEX_SHADER =
            "attribute vec4 a_Position;\n" +
                    "attribute vec2 a_TexCoord;\n" +
                    "varying vec2 v_TexCoord;\n" +
                    "void main() {\n" +
                    "  gl_Position = a_Position;\n" +
                    "  v_TexCoord = vec2(a_TexCoord.x, 1.0 - a_TexCoord.y);\n" + // Flip Y
                    "}\n";

    private final String FRAGMENT_SHADER =
            "precision mediump float;\n" +
                    "uniform sampler2D u_Texture;\n" +
                    "varying vec2 v_TexCoord;\n" +
                    "void main() {\n" +
                    "  float color = texture2D(u_Texture, v_TexCoord).r;\n" +
                    "  gl_FragColor = vec4(color, color, color, 1.0);\n" +
                    "}\n";

    // Vertex data for a full-screen quad
    private final float[] QUAD_VERTICES = {
            // X, Y, Z,  U, V
            -1.0f, -1.0f, 0, 0, 0,
            1.0f, -1.0f, 0, 1, 0,
            -1.0f,  1.0f, 0, 0, 1,
            1.0f,  1.0f, 0, 1, 1,
    };

    private final FloatBuffer vertexBuffer;
    private final int program;
    private final int positionHandle;
    private final int texCoordHandle;
    private final int textureHandle;

    public FullScreenQuad() {
        vertexBuffer = ByteBuffer.allocateDirect(QUAD_VERTICES.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        vertexBuffer.put(QUAD_VERTICES).position(0);

        program = ShaderUtils.createProgram(VERTEX_SHADER, FRAGMENT_SHADER);
        positionHandle = GLES20.glGetAttribLocation(program, "a_Position");
        texCoordHandle = GLES20.glGetAttribLocation(program, "a_TexCoord");
        textureHandle = GLES20.glGetUniformLocation(program, "u_Texture");
    }

    public void draw(int textureId) {
        GLES20.glUseProgram(program);

        vertexBuffer.position(0);
        GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT, false, 20, vertexBuffer);
        GLES20.glEnableVertexAttribArray(positionHandle);

        vertexBuffer.position(3);
        GLES20.glVertexAttribPointer(texCoordHandle, 2, GLES20.GL_FLOAT, false, 20, vertexBuffer);
        GLES20.glEnableVertexAttribArray(texCoordHandle);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
        GLES20.glUniform1i(textureHandle, 0);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);

        GLES20.glDisableVertexAttribArray(positionHandle);
        GLES20.glDisableVertexAttribArray(texCoordHandle);
    }
}