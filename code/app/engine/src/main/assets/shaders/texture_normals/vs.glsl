attribute vec4 vPosition;
attribute vec4 vNormal;
attribute vec2 vTexture;

uniform mat4 uVPMatrix;
uniform mat4 uModelMatrix;
uniform mat4 uRotMatrix;
uniform float far;
uniform mat4 depthMVP;

varying vec3 normal;
varying vec3 fFragPos;
varying float z;
varying vec2 fTextureCoord;
varying vec4 fShadowCoord;
varying float far_f;

void main(){
    normal = normalize(uRotMatrix*vNormal).xyz;
    fFragPos = (uModelMatrix*vPosition).xyz;
    fTextureCoord = vTexture;

    vec4 position = uVPMatrix*uModelMatrix*vPosition;
    z = position.z/far;

    fShadowCoord = depthMVP*uModelMatrix*vPosition;
    far_f=far;

    gl_Position = position;
}