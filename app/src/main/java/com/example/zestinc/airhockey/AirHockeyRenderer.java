package com.example.zestinc.airhockey;

import android.content.Context;
import android.graphics.Shader;
import android.opengl.GLSurfaceView;

import com.example.zestinc.airhockey.util.LoggerConfig;
import com.example.zestinc.airhockey.util.MatrixHelper;
import com.example.zestinc.airhockey.util.ShaderHelper;
import com.example.zestinc.airhockey.util.TextResourceReader;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.*;
import static android.opengl.Matrix.multiplyMM;
import static android.opengl.Matrix.orthoM;
import static android.opengl.Matrix.setIdentityM;
import static android.opengl.Matrix.translateM;

public class AirHockeyRenderer implements GLSurfaceView.Renderer{
    private static final String U_MATRIX = "u_Matrix";
    private static final String A_POSITION = "a_Position";
    private static final int POSITION_COMPONENT_COUNT = 4;
    private static final int BYTES_PER_FLOAT = 4;
    private static final String A_COLOR = "a_Color";
    private static final int COLOR_COMPONENT_COUNT = 3;
    private static final int STRIDE = (POSITION_COMPONENT_COUNT + COLOR_COMPONENT_COUNT) * BYTES_PER_FLOAT;

    private final Context context;
    private final FloatBuffer vertexData;
    private final float[] projectionMatrix = new float[16];
    private final float[] modelMatrix = new float[16];

    private int program;
    private int aPositionLocation;
    private int aColorLocation;
    private int uMatrixLocation;

    public AirHockeyRenderer(Context context) {
        this.context = context;
        float[] tableVerticesWithTriangles = {
                // Order of coordinates: X, Y, Z, W, R, G, B
                // Triangle Fan
                0f,      0f,    0f,     1.5f,   1f,     1f,     1f,
                -0.5f,  -0.8f,  0f,     1f,     0.7f,   0.7f,   0.7f,
                0.5f,   -0.8f,  0f,     1f,     0.7f,   0.7f,   0.7f,
                0.5f,   0.8f,   0f,     2f,     0.7f,   0.7f,   0.7f,
                -0.5f,  0.8f,   0f,     2f,     0.7f,   0.7f,   0.7f,
                -0.5f,  -0.8f,  0f,     1f,     0.7f,   0.7f,   0.7f,
                // Middle Line
                -0.5f,  0f,     0f,     1.5f,   1f,     0f,     0f,
                0.5f,   0f,     0f,     1.5f,   1f,     0f,     0f,
                // Mallets
                0f,     -0.4f,  0f,     1.25f,  0f,     0f,     1f,
                0f,     0.4f,   0f,     1.75f,  1f,     0f,     0f,
        };
        vertexData = ByteBuffer
                .allocateDirect(tableVerticesWithTriangles.length * BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();

        vertexData.put(tableVerticesWithTriangles);
    }

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        glClearColor(0.0f, 0.0f,  0.0f, 0.0f);
        String vertexShaderSource = TextResourceReader.readTextFileFromResourde(context, R.raw.simple_vertex_shader);
        String fragmentShaderSource = TextResourceReader.readTextFileFromResourde(context, R.raw.simple_fragment_shader);
        int vertexShader = ShaderHelper.compileVertexShader(vertexShaderSource);
        int fragmentShader = ShaderHelper.compileFragmentShader(fragmentShaderSource);
        program = ShaderHelper.linkProgram(vertexShader, fragmentShader);
        if (LoggerConfig.ON) {
            ShaderHelper.validateProgram(program);
        }
        glUseProgram(program);
        aColorLocation = glGetAttribLocation(program, A_COLOR);
        aPositionLocation = glGetAttribLocation(program, A_POSITION);
        // Ensure OPEN_GL read data from very beginning
        vertexData.position(0);
        glVertexAttribPointer(aPositionLocation, POSITION_COMPONENT_COUNT, GL_FLOAT, false, STRIDE, vertexData);
        glEnableVertexAttribArray(aPositionLocation);

        vertexData.position(POSITION_COMPONENT_COUNT);
        glVertexAttribPointer(aColorLocation, COLOR_COMPONENT_COUNT, GL_FLOAT, false, STRIDE, vertexData);
        glEnableVertexAttribArray(aColorLocation);

        uMatrixLocation = glGetUniformLocation(program, U_MATRIX);
        setIdentityM(modelMatrix, 0);
        translateM(modelMatrix, 0, 0f, 0f, -2f);

        final float[] temp = new float[16];
        multiplyMM(temp, 0, projectionMatrix, 0, modelMatrix, 0);
        System.arraycopy(temp, 0, projectionMatrix, 0, temp.length);
    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int width, int height) {
        glViewport(0, 0, width, height);
        MatrixHelper.perspectiveM(projectionMatrix, 45, (float)width/(float)height, 1f, 10f);
    }

    @Override
    public void onDrawFrame(GL10 gl10) {
        glClear(GL_COLOR_BUFFER_BIT);
        glUniformMatrix4fv(uMatrixLocation, 1, false, projectionMatrix, 0);
        // Draw table
        glDrawArrays(GL_TRIANGLE_FAN, 0, 6);

        // Draw dividing line
        glDrawArrays(GL_LINES, 6, 2);

        // Draw First Mallets
        glDrawArrays(GL_POINTS, 8, 1);

        // Draw Second Mallets
        glDrawArrays(GL_POINTS, 9, 1);
    }
}
