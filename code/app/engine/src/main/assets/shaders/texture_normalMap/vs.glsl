attribute vec4 vPosition;
attribute vec4 vNormal;
attribute vec2 vTexture;
attribute vec2 vNormalTextureCoord;
attribute vec3 vTangent;

uniform mat4 uVPMatrix;
uniform mat4 uModelMatrix;
uniform mat4 uRotMatrix;
uniform float far;
uniform mat4 depthMVP;

varying vec3 fFragPos;
varying float z;
varying vec2 fTextureCoord;
varying vec2 fNormalTextureCoord;
varying mat3 fTBN;
varying vec4 fShadowCoord;
varying float far_f;

void main(){
    fFragPos = (uModelMatrix*vPosition).xyz;
    fTextureCoord = vTexture;
    fNormalTextureCoord = vNormalTextureCoord;

    z = (uVPMatrix*uModelMatrix*vPosition).z/far;

    vec3 T = normalize(vec3(uModelMatrix * vec4(vTangent,   0.0)));
    vec3 N = -normalize(uRotMatrix*vNormal).xyz;
    vec3 B = normalize(vec3(uModelMatrix * vec4(cross(N, T), 0.0)));
    mat3 TBN = mat3(T, B, N);
    fTBN = TBN;

    fShadowCoord = depthMVP*uModelMatrix*vPosition;
    far_f=far;

    gl_Position = uVPMatrix*uModelMatrix*vPosition;
}