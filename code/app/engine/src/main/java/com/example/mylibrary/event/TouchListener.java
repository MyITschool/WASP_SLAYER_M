package com.example.mylibrary.event;

import android.view.MotionEvent;
import android.view.View;

import com.example.mylibrary.math.Vector2;

public final class TouchListener implements View.OnTouchListener{

    private float[] touch = new float[20];
    private int pointerCount = 0;
    private boolean isTouch = false;

    private int width;
    private int height;

    public TouchListener(View view){
        view.setOnTouchListener(this);
        touchArr(pointerCount);
    }

    public void setResolution(int width, int height){
        this.width=width;
        this.height=height;
    }

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
                case MotionEvent.ACTION_POINTER_DOWN: // последующие касания
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
                case MotionEvent.ACTION_POINTER_UP: // прерывания касаний
                    pointerCount = event.getPointerCount();
                    for (int i = 0; i < pointerCount; i++){
                        touch[i*2]=event.getX(i);
                        touch[i*2+1]=event.getY(i);
                    }
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

    private void touchArr(int pointerCount){
        for (int i = pointerCount; i < 10; i++){
            touch[i*2]=-1;
            touch[i*2+1]=-1;
        }
    }

    public Vector2 getTouchDown(Vector2 pos, Vector2 size){
        pos.x=(pos.x-size.x+1)/2*width;
        pos.y=(-pos.y-size.y+1)/2*height;

        size.x*=width;
        size.y*=height;

        if (isTouch){
            for (int i = 0; i < pointerCount;i++){
                float tx = touch[i*2];
                float ty = touch[i*2+1];
                if(tx>=pos.x&&tx<pos.x+size.x&& ty>=pos.y&&ty<=pos.y+size.y){
                    return new Vector2(tx, ty);
                }
            }
        }

        return new Vector2(-1);
    }
}
