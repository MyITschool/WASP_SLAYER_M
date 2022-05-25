attribute vec4 vPosition;
attribute vec2 vTextureCoord;

uniform mat4 uModelMatrix;

varying vec2 fTextureCoord;

void main() {
    fTextureCoord=vTextureCoord;

    gl_Position = uModelMatrix*vPosition;
    gl_Position.z=0.;
}
