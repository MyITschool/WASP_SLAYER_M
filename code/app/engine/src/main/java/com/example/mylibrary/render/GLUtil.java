package com.example.mylibrary.render;

import static android.opengl.GLES20.GL_TEXTURE0;
import static android.opengl.GLES20.glActiveTexture;
import static android.opengl.GLES32.GL_COMPILE_STATUS;
import static android.opengl.GLES32.glCompileShader;
import static android.opengl.GLES32.glShaderSource;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLES32;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public final class GLUtil
{
    public static int[] createFrameBuffer(int width, int height, int lastTextureIndex) {

        glActiveTexture(GL_TEXTURE0+lastTextureIndex);

        int[] values = new int[1];
        GLES20.glGenTextures(1, values, 0);
        int mOffscreenTexture = values[0];   // expected > 0
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mOffscreenTexture);

        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, width, height, 0,
                GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, null);

        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER,
                GLES20.GL_NEAREST);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER,
                GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S,
                GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T,
                GLES20.GL_CLAMP_TO_EDGE);

        GLES20.glGenFramebuffers(1, values, 0);
        int mFramebuffer = values[0];    // expected > 0
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, mFramebuffer);

        GLES20.glGenRenderbuffers(1, values, 0);
        int mDepthBuffer = values[0];    // expected > 0
        GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, mDepthBuffer);

        GLES20.glRenderbufferStorage(GLES20.GL_RENDERBUFFER, GLES20.GL_DEPTH_COMPONENT16,
                width, height);

        GLES20.glFramebufferRenderbuffer(GLES20.GL_FRAMEBUFFER, GLES20.GL_DEPTH_ATTACHMENT,
                GLES20.GL_RENDERBUFFER, mDepthBuffer);
        GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0,
                GLES20.GL_TEXTURE_2D, mOffscreenTexture, 0);

        int status = GLES20.glCheckFramebufferStatus(GLES20.GL_FRAMEBUFFER);
        if (status != GLES20.GL_FRAMEBUFFER_COMPLETE) {
            throw new RuntimeException("status: "+status);
        }
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);

        return new int[]{mOffscreenTexture, mFramebuffer, mDepthBuffer};
    }

    static String readShader(Context context, String fileName)
    {
        String shaderSource = null;

        if ( fileName == null )
        {
            return shaderSource;
        }

        InputStream is = null;
        byte [] buffer;

        try
        {
            is =  context.getAssets().open ( fileName );

            buffer = new byte[is.available()];

            is.read ( buffer );

            ByteArrayOutputStream os = new ByteArrayOutputStream();

            os.write ( buffer );

            os.close();
            is.close();

            shaderSource = os.toString();
        }
        catch ( IOException ioe )
        {
            is = null;
        }

        if ( is == null )
        {
            return shaderSource;
        }

        return shaderSource;
    }


    public static int loadShader ( int type, String shaderSrc )
    {
        int shader;
        int[] compiled = new int[1];

        shader = GLES32.glCreateShader ( type );

        if ( shader == 0 )
        {
            return 0;
        }

        glShaderSource ( shader, shaderSrc );

        glCompileShader ( shader );

        GLES32.glGetShaderiv ( shader, GL_COMPILE_STATUS, compiled, 0 );

        if ( compiled[0] == 0 )
        {
            Log.e ( "ESShader", GLES32.glGetShaderInfoLog ( shader ) );
            GLES32.glDeleteShader ( shader );
            return 0;
        }

        return shader;
    }

    public static int loadProgram ( String vertShaderSrc, String fragShaderSrc)
    {
        int vertexShader;
        int fragmentShader;
        int programObject;
        int[] linked = new int[1];

        vertexShader = loadShader ( GLES32.GL_VERTEX_SHADER, vertShaderSrc );

        if ( vertexShader == 0 )
        {
            return 0;
        }

        fragmentShader = loadShader ( GLES32.GL_FRAGMENT_SHADER, fragShaderSrc );

        if ( fragmentShader == 0 )
        {
            GLES32.glDeleteShader ( vertexShader );
            return 0;
        }


        programObject = GLES32.glCreateProgram();

        if ( programObject == 0 )
        {
            return 0;
        }

        GLES32.glAttachShader ( programObject, vertexShader );
        GLES32.glAttachShader ( programObject, fragmentShader );

        GLES32.glLinkProgram ( programObject );

        GLES32.glGetProgramiv ( programObject, GLES32.GL_LINK_STATUS, linked, 0 );

        if ( linked[0] == 0 )
        {
            Log.e ( "ESShader", "Error linking program:" );
            Log.e ( "ESShader", GLES32.glGetProgramInfoLog ( programObject ) );
            GLES32.glDeleteProgram ( programObject );
            return 0;
        }

        GLES32.glDeleteShader ( vertexShader );
        GLES32.glDeleteShader ( fragmentShader );

        return programObject;
    }
}
