package com.example.mylibrary.event;

import android.view.MotionEvent;
import android.view.View;

import com.example.mylibrary.math.Func;
import com.example.mylibrary.math.Vector2;

import java.util.Arrays;

public final class TouchListener implements View.OnTouchListener{

    // касания
    private float[] touch = new float[20];
    // количество касаний
    private int pointerCount = 0;
    // есть ли нажатия
    private boolean isTouch = false;
    // разрешение экрана
    private Vector2 res = new Vector2(0);

    public TouchListener(View view){
        view.setOnTouchListener(this);
        Arrays.fill(touch, -1);
    }
    // установка разрешения
    public void setResolution(Vector2 res){
        this.res=res;
    }

    // событие нажатия
    @Override
    public boolean onTouch(View view, MotionEvent event) {

        synchronized (this){
            int actionMask = event.getActionMasked();
            pointerCount = event.getPointerCount();
            switch (actionMask & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN: // первое касание
                    isTouch = true;
                    pointerCount = event.getPointerCount();
                    for (int i = 0; i < pointerCount; i++){
                        touch[i*2]=event.getX(i);
                        touch[i*2+1]=event.getY(i);
                    }
                    break;
                case MotionEvent.ACTION_POINTER_DOWN: // последующие касания
                case MotionEvent.ACTION_POINTER_UP: // прерывания касаний
                    pointerCount = event.getPointerCount();
                    for (int i = 0; i < pointerCount; i++){
                        touch[i*2]=event.getX(i);
                        touch[i*2+1]=event.getY(i);
                    }
                    break;

                case MotionEvent.ACTION_UP: // прерывание последнего касания
                    pointerCount = event.getPointerCount();
                    for (int i = 0; i < pointerCount; i++){
                        touch[i*2]=event.getX(i);
                        touch[i*2+1]=event.getY(i);
                    }
                    isTouch = false;
                    break;
                case MotionEvent.ACTION_MOVE:
                    for (int i = 0; i < pointerCount; i++){
                        touch[i*2]=event.getX(i);
                        touch[i*2+1]=event.getY(i);
                    }
                    break;

            }

            return true;
        }
    }

    // проверка нажатия
    public boolean getTouchDown(Vector2 pos, Vector2 size, Vector2 out){
        pos.x=(pos.x-size.x+1)/2*res.x;
        pos.y=(-pos.y-size.y+1)/2* res.y;

        size.x*= res.x;
        size.y*= res.y;

        if (isTouch){
            for (int i = 0; i < pointerCount;i++){
                float tx = touch[i*2];
                float ty = touch[i*2+1];
                if(tx>=pos.x&&tx<pos.x+size.x&& ty>=pos.y&&ty<=pos.y+size.y){
                    Vector2 t = Func.canvasToGlCoord(new Vector2(tx,ty), res);
                    out.setXY(t.x, t.y);
                    return true;
                }
            }
        }

        return false;
    }
}
