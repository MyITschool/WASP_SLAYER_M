attribute vec4 vPosition;

uniform mat4 uVPMatrix;
uniform mat4 uModelMatrix;

varying float fz;

void main(){
    fz = vec4(uVPMatrix*uModelMatrix*vPosition).z;
    gl_Position = uVPMatrix*uModelMatrix*vPosition;
}