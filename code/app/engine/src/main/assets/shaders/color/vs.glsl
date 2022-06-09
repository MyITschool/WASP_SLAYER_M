attribute vec4 vPosition;

uniform mat4 uVPMatrix;
uniform mat4 uModelMatrix;
uniform float far;
uniform mat4 depthMVP;

varying float z;
varying vec4 fShadowCoord;
varying float far_f;

void main(){
    vec4 position = uVPMatrix*uModelMatrix*vPosition;
    z = position.z/far;
    fShadowCoord = depthMVP*uModelMatrix*vPosition;
    far_f=far;

    gl_Position = position;
}