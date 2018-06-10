package com.example.zestinc.airhockey.programs;

import android.content.Context;

import com.example.zestinc.airhockey.R;

import static android.opengl.GLES20.GL_TEXTURE0;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.glActiveTexture;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniform1i;
import static android.opengl.GLES20.glUniformMatrix4fv;

/**
 * Created by zestinc on 6/10/18.
 */

public class TextureShaderProgram extends ShaderProgram{
    // Uniform locations
    private final int uMatrixLocation;
    // TODO What's this?
    private final int uTextureUnitLocation;

    // Attribute locations
    private final int aPositionLocation;
    private final int aTextureCoordinatesLocation;

    public TextureShaderProgram(Context context) {
        super(context, R.raw.texture_vertex_shader, R.raw.texture_fragment_shader);

        // Retrieve uniform locations for the shader program
        uMatrixLocation = glGetUniformLocation(program, U_MATRIX);
        uTextureUnitLocation = glGetUniformLocation(program, U_TEXTURE_UNIT);

        // Retrieve attribute locations for the shader program.
        aPositionLocation = glGetAttribLocation(program, A_POSITION);
        aTextureCoordinatesLocation = glGetAttribLocation(program, A_TEXTURE_COORDINATES);
    }

    public void setUniforms(float[] matrix, int textureId) {
        // Pass the matrix into the shader program.
        glUniformMatrix4fv(uMatrixLocation, 1, false, matrix, 0);

        /**
         * When we draw using textures in OpenGL, we don’t pass the texture directly in to the shader. Instead, we use a texture unit to hold the texture. We do this because a GPU can only draw so many textures at the same time. It uses these texture units to represent the active textures currently being drawn. We can swap textures in and out of texture units if we need to switch textures, though this may slow down rendering if we do it too often. We can also use several texture units to draw more than one texture at the same time.
         We start out this part by setting the active texture unit to texture unit 0 with a call to glActiveTexture(), and then we bind our texture to this unit with a call to glBindTexture(). We then pass in the selected texture unit to u_TextureUnit in the fragment shader by calling glUniform1i(uTextureUnitLocation, 0).
         */
        // Set the active texture unit to texture unit 0.
        glActiveTexture(GL_TEXTURE0);

        // Bind the texture to this unit.
        glBindTexture(GL_TEXTURE_2D, textureId);

        // Tell the texture uniform sampler to use this texture in the shader by telling it to read from texture unit 0.
        glUniform1i(uTextureUnitLocation, 0);
    }

    public int getPositionAttributeLocation() {
        return aPositionLocation;
    }

    public int getTextureCoordinatesAttributeLocation() {
        return aTextureCoordinatesLocation;
    }
}
