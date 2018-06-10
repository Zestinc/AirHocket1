package com.example.zestinc.airhockey;

import android.content.Context;
import android.graphics.Shader;
import android.opengl.GLSurfaceView;
import android.util.Log;

import com.example.zestinc.airhockey.objects.Mallet;
import com.example.zestinc.airhockey.objects.Table;
import com.example.zestinc.airhockey.programs.ColorShaderProgram;
import com.example.zestinc.airhockey.programs.TextureShaderProgram;
import com.example.zestinc.airhockey.util.LoggerConfig;
import com.example.zestinc.airhockey.util.MatrixHelper;
import com.example.zestinc.airhockey.util.ShaderHelper;
import com.example.zestinc.airhockey.util.TextResourceReader;
import com.example.zestinc.airhockey.util.TextureHelper;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.*;
import static android.opengl.Matrix.multiplyMM;
import static android.opengl.Matrix.orthoM;
import static android.opengl.Matrix.rotateM;
import static android.opengl.Matrix.setIdentityM;
import static android.opengl.Matrix.translateM;
import static com.example.zestinc.airhockey.Constants.BYTES_PER_FLOAT;

public class AirHockeyRenderer implements GLSurfaceView.Renderer{
    private final Context context;

    private final float[] projectionMatrix = new float[16];
    private final float[] modelMatrix = new float[16];

    private Table table;
    private Mallet mallet;

    private TextureShaderProgram textureProgram;
    private ColorShaderProgram colorProgram;

    private int texture;

    public AirHockeyRenderer(Context context) {
        this.context = context;
    }

    @Override
    public void onSurfaceCreated(GL10 glUnused, EGLConfig config) {
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        table = new Table();
        mallet = new Mallet();

        textureProgram = new TextureShaderProgram(context);
        colorProgram = new ColorShaderProgram(context);

        texture = TextureHelper.loadTexture(context, R.drawable.air_hockey_surface);
    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int width, int height) {
        glViewport(0, 0, width, height);
        MatrixHelper.perspectiveM(projectionMatrix, 45, (float)width/(float)height, 1f, 10f);
        final float[] temp = new float[16];
        multiplyMM(temp, 0, projectionMatrix, 0, modelMatrix, 0);
        System.arraycopy(temp, 0, projectionMatrix, 0, temp.length);
    }

    @Override
    public void onDrawFrame(GL10 glUnused) {
        // Clear the rendering surface.
        glClear(GL_COLOR_BUFFER_BIT);

        // Draw the table.
        textureProgram.useProgram();
        textureProgram.setUniforms(projectionMatrix, texture);
        table.bindData(textureProgram);
        table.draw();

        // Draw the mallets.
        colorProgram.useProgram();;
        colorProgram.setUniforms(projectionMatrix);
        mallet.bindData(colorProgram);
        mallet.draw();
    }
}