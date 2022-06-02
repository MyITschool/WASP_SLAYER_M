attribute vec4 vPosition;

uniform mat4 uVPMatrix;
uniform mat4 uModelMatrix;
uniform float far;

varying float z;

void main(){
    vec4 position = uVPMatrix*uModelMatrix*vPosition;
    z = position.z/far;

    gl_Position = position;
}