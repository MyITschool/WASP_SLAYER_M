attribute vec4 vPosition;
attribute vec2 vTexture;

uniform mat4 uVPMatrix;
uniform mat4 uModelMatrix;
uniform float far;

varying float z;
varying vec2 fTextureCoord;

void main(){
    vec4 position = uVPMatrix*uModelMatrix*vPosition;
    z = position.z/far;
    fTextureCoord = vTexture;

    gl_Position = position;
}
