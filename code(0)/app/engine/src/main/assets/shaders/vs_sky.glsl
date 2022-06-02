attribute vec4 vPosition;

uniform mat4 uVPMatrix;

varying vec3 fFragPos;

void main() {
    fFragPos=vPosition.xyz;
    fFragPos.x=-vPosition.x;

    vec4 out_pos=(uVPMatrix * vPosition).xyww;
   // out_pos.zw = vec2(0.0,1.0);
    gl_Position = out_pos;

}
