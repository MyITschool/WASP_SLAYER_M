package com.example.engine.render;

import android.opengl.GLES20;

import com.example.engine.math.Vector2;
import com.example.engine.math.Vector3;
import com.example.engine.math.Vector4;

public class RenderText extends RenderImg{

    private String text;
    private float charSize = 1/16.0f;

    public RenderText(int[] shader_vars, int[] mVBOIds, int[] uUIMaterial) {
        super(shader_vars, mVBOIds, uUIMaterial);
    }

    public void setCharSize(int charSize){
        this.charSize=charSize;
    }

    public void setText(String text){
        this.text=text;
    }
    public String getText(){return text;}

    public void draw(){
        Vector2 isc = getScale().clone();
        Vector3 ip = getPosition().clone();
        float scaleX = isc.x*2/text.length();
        float posX = ip.x-scaleX*text.length()/4;

        setScale(new Vector2(scaleX, isc.y));

        if(!actively)
            return;

//        if (objUpd){
//            objUpd=false;
//            genModelMatrix();
//        }
        if (material[0]>-1){
            GLES20.glUniform1i(shader_vars[31], (int) material[0]);
        }

        for (int i = 0; i < uUIMaterial.length; i++){
            GLES20.glUniform1f(uUIMaterial[i], material[i]);
        }

        putBuffers();
        for (int i = 0; i < text.length(); i++){
            drawChar(i, scaleX, posX);

            setBuffers();


            GLES20.glUniformMatrix4fv(shader_vars[30], 1, false, modelMatrix, 0);
            GLES20.glDrawElements(GLES20.GL_TRIANGLES, mIndicesData.length, GLES20.GL_UNSIGNED_SHORT, 0);
        }

        GLES20.glDisableVertexAttribArray(shader_vars[29]);

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);

        setPosition(ip);
        setScale(isc);
    }

    private void drawChar(int i, float scaleX, float posX){
        char c = text.charAt(i);
        int x = c & 0b1111;
        int y = c >> 4;
        Vector4 rct = new Vector4(x*charSize);
        rct.y = rct.x+charSize;
        rct.z = y*charSize;
        rct.w = rct.z+charSize;

        mTextureCoordsData[0] = mTextureCoordsData[6] = rct.x;
        mTextureCoordsData[2] = mTextureCoordsData[4] = rct.y;
        mTextureCoordsData[1] = mTextureCoordsData[3] = rct.w;
        mTextureCoordsData[5] = mTextureCoordsData[7] = rct.z;

        Vector3 p = getPosition();

        setPosition(new Vector3(posX+scaleX*i, p.y, p.z));

        setTextureCoords(mTextureCoordsData);

//        if (objUpd){
//            objUpd=false;
//            genModelMatrix();
//        }
    }
}
