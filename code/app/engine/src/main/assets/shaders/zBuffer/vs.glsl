attribute vec4 vPosition;

uniform mat4 uVPMatrix;
uniform mat4 uModelMatrix;
uniform float far;

varying float fz;

void main(){
    fz = vec4(uVPMatrix*uModelMatrix*vPosition).z/far;
    gl_Position = uVPMatrix*uModelMatrix*vPosition;
}