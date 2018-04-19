package com.example.zestinc.airhockey;

import android.content.Context;
import android.graphics.Shader;
import android.opengl.GLSurfaceView;

import com.example.zestinc.airhockey.util.LoggerConfig;
import com.example.zestinc.airhockey.util.ShaderHelper;
import com.example.zestinc.airhockey.util.TextResourceReader;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.*;

public class AirHockeyRenderer implements GLSurfaceView.Renderer{
    private static final String U_COLOR = "u_Color";
    private static final String A_POSITION = "a_Position";
    private static final int POSITION_COMPONENT_COUNT = 2;
    private static final int BYTES_PER_FLOAT = 4;

    private final Context context;
    private final FloatBuffer vertexData;

    private int program;
    private int uColorLocation;
    private int aPositionLocation;

    public AirHockeyRenderer(Context context) {
        this.context = context;
        float[] tableVerticesWithTriangles = {
                // First triangle
                -0.5f, -0.5f,
                0.5f, 0.5f,
                -0.5f, 0.5f,
                // Second triangle
                -0.5f, -0.5f,
                0.5f, -0.5f,
                0.5f, 0.5f,
                // Middle Line
                -0.5f, 0f,
                0.5f, 0f,
                // Mallets
                0f, -0.25f,
                0f, 0.25f
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
        uColorLocation = glGetUniformLocation(program, U_COLOR);
        aPositionLocation = glGetAttribLocation(program, A_POSITION);
        // Ensure OPEN_GL read data from very beginning
        vertexData.position(0);
        glVertexAttribPointer(aPositionLocation, POSITION_COMPONENT_COUNT, GL_FLOAT, false, 0, vertexData);
        glEnableVertexAttribArray(aPositionLocation);
    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int width, int height) {
        glViewport(0, 0, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl10) {
        glClear(GL_COLOR_BUFFER_BIT);
        // Draw table
        glUniform4f(uColorLocation, 1.0f, 1.0f, 1.0f, 1.0f);
        glDrawArrays(GL_TRIANGLE_FAN, 0, 6);

        // Draw dividing line
        glUniform4f(uColorLocation, 1.0f, 0.0f, 0.0f, 1.0f);
        glDrawArrays(GL_LINES, 6, 2);

        // Draw First Mallets
        glUniform4f(uColorLocation, 0.0f, 0.0f, 1.0f, 1.0f);
        glDrawArrays(GL_POINTS, 8, 1);

        // Draw Second Mallets
        glUniform4f(uColorLocation, 1.0f, 0.0f, 0.0f, 1.0f);
        glDrawArrays(GL_POINTS, 9, 1);
    }
}
