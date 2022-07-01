package com.example.mylibrary.render;

import static android.opengl.GLES20.GL_TRIANGLES;
import static android.opengl.GLES20.glDrawArrays;
import com.example.mylibrary.core.Core;
import com.example.mylibrary.math.Vector3;
import com.example.mylibrary.math.Vector4;
import com.example.mylibrary.model.Model;
import com.example.mylibrary.model.UIModel;

public class RebdererText extends RenderImg{
    // текст
    public String text;
    // сдвиг в текстуре
    protected float charSize = 1/16.0f;
    // установить сдвиг
    public void setCharSize(int fontRes){
        this.charSize=1.0f/fontRes;
    }

    // модель
    protected final UIModel uiModel;

    public RebdererText(String textureKey, String text, UIModel uiModel) {
        super(textureKey, uiModel);
        this.uiModel = uiModel;
        this.text = text;
    }
    //////////////////////////////////////////////////////////
    public RebdererText(String textureKey, UIModel uiModel) {
        super(textureKey, uiModel);
        this.uiModel = uiModel;
    }

    // отрисовка
    public void draw(){
        if(!activity)return;

        Vector3 isc = getScale().clone();
        Vector3 ip = getPosition().clone();
        float scaleX = isc.x*2/text.length();
        float posX = ip.x-scaleX*text.length()/4;

        setScale(new Vector3(scaleX, isc.y, 1));

        for (int i = 0; i < text.length(); i++){
            drawChar(i, scaleX, posX);
            setUniforms();

            glDrawArrays(GL_TRIANGLES, 0, model.getNumberPolygons());
        }

        setPosition(ip);
        setScale(isc);

        uiModel.setVertexesTexture(new float[]{
                0,1,
                1,1,
                1,0,

                0,1,
                1,0,
                0,0
        });
        model.putShaderVariables();
    }
    protected void drawChar(int i, float scaleX, float posX){
        byte c = (byte)text.charAt(i);
        int x = c & 0b1111;
        int y = c >> 4;
        Vector4 rct = new Vector4(x*charSize);
        rct.y = rct.x+charSize;
        rct.z = y*charSize;
        rct.w = rct.z+charSize;

        float[] mTextureCoordsData = new float[]{
                rct.x, rct.w,
                rct.y, rct.w,
                rct.y, rct.z,

                rct.x, rct.w,
                rct.y, rct.z,
                rct.x, rct.z
        };

        Vector3 p = getPosition();

        setPosition(new Vector3(posX+scaleX*i, p.y, p.z));

        uiModel.setVertexesTexture(mTextureCoordsData);
        model.putShaderVariables();
    }
}
