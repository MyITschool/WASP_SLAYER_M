precision lowp float;

uniform sampler2D uTexture;
uniform vec4 color;

varying vec2 fTextureCoord;

void main() {
    vec4 color = texture2D(uTexture, fTextureCoord)*color;

    gl_FragColor = color;
}
