precision highp float;

uniform samplerCube skyBox;

varying vec3 fFragPos;

void main() {
    gl_FragColor = textureCube(skyBox, fFragPos);
}
