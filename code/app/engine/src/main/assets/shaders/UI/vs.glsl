attribute vec2 vPosition;
attribute vec2 vTexture;

uniform mat4 uModelMatrix;

varying vec2 fTextureCoord;

void main() {
    fTextureCoord=vTexture;

    gl_Position = uModelMatrix*vec4(vPosition, 0.0, 1.0);
}
