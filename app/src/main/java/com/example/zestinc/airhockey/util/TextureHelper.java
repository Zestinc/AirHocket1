package com.example.zestinc.airhockey.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLUtils;
import android.util.Log;

import static android.opengl.GLES20.GL_LINEAR;
import static android.opengl.GLES20.GL_LINEAR_MIPMAP_LINEAR;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.GL_TEXTURE_MAG_FILTER;
import static android.opengl.GLES20.GL_TEXTURE_MIN_FILTER;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glDeleteTextures;
import static android.opengl.GLES20.glGenTextures;
import static android.opengl.GLES20.glGenerateMipmap;
import static android.opengl.GLES20.glTexParameteri;

public class TextureHelper {
    private static final String TAG = TextureHelper.class.getSimpleName();
    // Get the Id of the loaded OpenGL texture and draw a image onto it.
    public static int loadTexture(Context context, int resourceId) {
        final int[] textureObjectIds = new int[1];
        glGenTextures(1, textureObjectIds, 0);

        if (textureObjectIds[0] == 0) {
            if (LoggerConfig.ON) {
                Log.w(TAG, "Could not generate a new OpenGL texture object.");
            }
            return 0;
        }

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;

        final Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resourceId, options);

        if (bitmap == null) {
            if (LoggerConfig.ON) {
                Log.w(TAG, "Resource Id" + resourceId + " could not be decoded.");
            }

            glDeleteTextures(1, textureObjectIds, 0);
            return 0;
        }

        // GL_TEXTURE_2D: tells OpenGL that this should be treated as a two-dimensional texture
        glBindTexture(GL_TEXTURE_2D, textureObjectIds[0]);
        // Tell OpenGL use trilinear filtering for minification
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
        // Tell OpenGl use bilinear filtering for mini magnification
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

        GLUtils.texImage2D(GL_TEXTURE_2D, 0, bitmap, 0);
        bitmap.recycle();
        glGenerateMipmap(GL_TEXTURE_2D);

        //unbind texture
        glBindTexture(GL_TEXTURE_2D, 0);

        return textureObjectIds[0];
    }
}
