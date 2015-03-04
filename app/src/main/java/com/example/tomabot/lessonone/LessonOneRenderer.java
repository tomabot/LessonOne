package com.example.tomabot.lessonone;

import android.opengl.EGLConfig;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView.Renderer;
import android.opengl.Matrix;
import android.os.SystemClock;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

/**
 * Created by tomabot on 3/4/15.
 */

public class LessonOneRenderer implements Renderer {
    // some graphics to render
    public final FloatBuffer mTriangle1Vertices;
    //private final FloatBuffer mTriangle2Vertices;
    //private final FloatBuffer mTriangle3Vertices;

    // number of bytes per float
    private final int mBytesPerFloat = 4;

    // initialize mode data
    public LessonOneRenderer()
    {
        // this triangle is red, green, and blue
        final float[] triangle1VerticesData = {
                -0.5f, -0.25f, 0.0f,        // X, Y, Z
                1.0f, 0.0f, 0.0f, 1.0f,     // R, g, b, A

                0.5f, -0.25f, 0.0f,         // X, Y, Z
                0.0f, 0.0f, 1.0f, 1.0f,     // r, g, B, A

                0.0f, 0.559016994f, 0.0f,   // X, Y, Z
                0.0f, 1.0f, 0.0f, 1.0f      // r, G, b, A
        };

        // initialize the buffers that hold the vertice data
        mTriangle1Vertices = ByteBuffer
                .allocateDirect(triangle1VerticesData.length * mBytesPerFloat)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();

        mTriangle1Vertices.put(triangle1VerticesData).position(0);
    }

    private float[] mViewMatrix = new float[16];

    // store the model matrix. this matrix is used to move models from object space (where each
    // model can be thought of as being located at the center of the universe) to world space
    private float[] mModelMatrix = new float[16];

    @Override
    public void onDrawFrame(GL10 unused) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        // Do a complete rotation every 10 seconds.
        long time = SystemClock.uptimeMillis() % 10000L;
        float angleInDegrees = (360.0f / 10000.0f) * ((int) time);

        // Draw the triangle facing straight on.
        Matrix.setIdentityM(mModelMatrix, 0);
        Matrix.rotateM(mModelMatrix, 0, angleInDegrees, 0.0f, 0.0f, 1.0f);
        drawTriangle(mTriangle1Vertices);

    }

    // allocate storage for the final combined matrix. this will be passed into the shader program
    private float[] mMVPMatrix = new float[16];

    // number of elements per vertex
    private final int mStrideBytes = 7 * mBytesPerFloat;

    // offset of the position data
    private final int mPositionOffset = 0;

    // size of the position data in elements
    private final int mPositionDataSize = 3;

    // offset of the color data
    private final int mColorOffset = 3;

    // size of the color data in elements
    private final int mColorDataSize = 4;

    /**
     * draw a triangle from the given vertex data
     *
     * @param aTriangleBuffer the buffer containing the vertex data
     */
    private void drawTriangle(final FloatBuffer aTriangleBuffer) {
        // Pass in the position information
        aTriangleBuffer.position(mPositionOffset);
        GLES20.glVertexAttribPointer(mPositionHandle, mPositionDataSize, GLES20.GL_FLOAT, false,
                mStrideBytes, aTriangleBuffer);

        GLES20.glEnableVertexAttribArray(mPositionHandle);

        // Pass in the color information
        aTriangleBuffer.position(mColorOffset);
        GLES20.glVertexAttribPointer(mColorHandle, mColorDataSize, GLES20.GL_FLOAT, false,
                mStrideBytes, aTriangleBuffer);

        GLES20.glEnableVertexAttribArray(mColorHandle);

        // This multiplies the view matrix by the model matrix, and stores the result in the MVP matrix
        // (which currently contains model * view).
        Matrix.multiplyMM(mMVPMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);

        // This multiplies the modelview matrix by the projection matrix, and stores the result in the MVP matrix
        // (which now contains model * view * projection).
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mMVPMatrix, 0);

        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 3);
    }

    // used to pass in the transformation matrix
    private int mMVPMatrixHandle;

    // used to pass in model position information
    private int mPositionHandle;

    //used to pass inmodel color information
    private int mColorHandle;

    @Override
    public void onSurfaceCreated(GL10 gl, javax.microedition.khronos.egl.EGLConfig config) {
        // set the background clear color to gray
        GLES20.glClearColor(0.5f, 0.5f, 0.5f, 0.5f);

        // position the eye behind the origin
        final float eyeX = 0.0f;
        final float eyeY = 0.0f;
        final float eyeZ = 1.5f;

        // we are looking toward the distance
        final float lookX = 0.0f;
        final float lookY = 0.0f;
        final float lookZ = -5.0f;

        final float upX = 0.0f;
        final float upY = 1.0f;
        final float upZ = 0.0f;

        // set the view matrix. this matrix represents the camera position
        Matrix.setLookAtM(mViewMatrix, 0, eyeX, eyeY, eyeZ, lookX, lookY, lookZ, upX, upY, upZ);

        final String vertexShader =
              "uniform mat4 u_MVPMatrix;        \n"     // constant representing a combined model/view/projection matrix
            + "attribute vec4 a_Position;       \n"     // per-vertex position info to be passed in
            + "attribute vec4 a_Color;          \n"     // per-vertex color info to be passed in
            + "varying vec4 v_Color;            \n"     // this will be passed to the fragment shader
                                                        // it will be interpolated across the triangle
            + "void main() {                    \n"     // entry point for the vertex shader
            + "    v_Color = a_Color;           \n"     // pass the color thru to the fragment shader
            + "    gl_Position = u_MVPMatrix    \n"     // gl_position is a special variable used to store the final position
            + "                * a_Position;    \n"     // multiply the vertex by the matrix to xform the point
            + "}                                \n";    // screen coordinates

        // create the vertex shader
        int vertexShaderHandle= GLES20.glCreateShader(GLES20.GL_VERTEX_SHADER);
        if (vertexShaderHandle != 0) {
            // pass in the shader source
            GLES20.glShaderSource(vertexShaderHandle, vertexShader);

            // compile the shader
            GLES20.glCompileShader(vertexShaderHandle);

            // get the compilation status
            final int[] compileStatus = new int[1];
            GLES20.glGetShaderiv(vertexShaderHandle, GLES20.GL_COMPILE_STATUS, compileStatus, 0);

            // if the compilation failed, delete the shader
            if(compileStatus[0] == 0) {
                GLES20.glDeleteShader(vertexShaderHandle);
                vertexShaderHandle = 0;

            }
        }

        if (vertexShaderHandle == 0) {
            throw new RuntimeException("Error creating vertex shader");
        }

        final String fragmentShader =
              "precision mediump float;     \n"     // Set the default precision to medium. We don't need as high of a
                                                    // precision in the fragment shader.
            + "varying vec4 v_Color;        \n"     // This is the color from the vertex shader interpolated across the
                                                    // triangle per fragment.
            + "void main()                  \n"     // The entry point for our fragment shader.
            + "{                            \n"
            + "   gl_FragColor = v_Color;   \n"     // Pass the color directly through the pipeline.
            + "}                            \n";

        // create the fragment shader
        int fragmentShaderHandle= GLES20.glCreateShader(GLES20.GL_VERTEX_SHADER);
        if (fragmentShaderHandle != 0) {
            // pass in the shader source
            GLES20.glShaderSource(vertexShaderHandle, fragmentShader);

            // compile the shader
            GLES20.glCompileShader(fragmentShaderHandle);

            // get the compilation status
            final int[] compileStatus = new int[1];
            GLES20.glGetShaderiv(fragmentShaderHandle, GLES20.GL_COMPILE_STATUS, compileStatus, 0);

            // if the compilation failed, delete the shader
            if(compileStatus[0] == 0) {
                GLES20.glDeleteShader(fragmentShaderHandle);
                fragmentShaderHandle = 0;

            }
        }

        if (fragmentShaderHandle == 0) {
            throw new RuntimeException("Error creating fragment shader");
        }


        // Create a program object and store the handle to it.
        int programHandle = GLES20.glCreateProgram();
        if (programHandle != 0) {
            // Bind the vertex shader to the program.
            GLES20.glAttachShader(programHandle, vertexShaderHandle);

            // Bind the fragment shader to the program.
            GLES20.glAttachShader(programHandle, fragmentShaderHandle);

            // Bind attributes
            GLES20.glBindAttribLocation(programHandle, 0, "a_Position");
            GLES20.glBindAttribLocation(programHandle, 1, "a_Color");

            // Link the two shaders together into a program.
            GLES20.glLinkProgram(programHandle);

            // Get the link status.
            final int[] linkStatus = new int[1];
            GLES20.glGetProgramiv(programHandle, GLES20.GL_LINK_STATUS, linkStatus, 0);

            // If the link failed, delete the program.
            if (linkStatus[0] == 0)
            {
                GLES20.glDeleteProgram(programHandle);
                programHandle = 0;
            }
        }

        if (programHandle == 0) {
            throw new RuntimeException("Error creating program.");
        }

        // Set program handles. These will later be used to pass in values to the program.
        mMVPMatrixHandle = GLES20.glGetUniformLocation(programHandle, "u_MVPMatrix");
        mPositionHandle = GLES20.glGetAttribLocation(programHandle, "a_Position");
        mColorHandle = GLES20.glGetAttribLocation(programHandle, "a_Color");

        // Tell OpenGL to use this program when rendering.
        GLES20.glUseProgram(programHandle);
    }

    // store the projection matrix. this is used to project the scene onto a 2D viewport
    private float[] mProjectionMatrix = new float[16];

    @Override
    public void onSurfaceChanged(GL10 unused, int width, int height) {
        // set the OpenGL viewport to the same size as the surface
        GLES20.glViewport(0, 0, width, height);

        // create a new perspective projection matrix. the height will stay the same
        // while the width will vary as per aspect ration
        final float ratio = (float) width / height;
        final float left = -ratio;
        final float right = ratio;
        final float bottom = -1.0f;
        final float top = 1.0f;
        final float near = 1.0f;
        final float far = 10.0f;

        Matrix.frustumM(mProjectionMatrix, 0, left, right, bottom, top, near, far);

    }
}
