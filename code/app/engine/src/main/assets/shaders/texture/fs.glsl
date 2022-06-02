precision lowp float;

uniform vec4 color;
uniform vec4 fog_color;
uniform sampler2D uTexture;

varying float z;
varying vec2 fTextureCoord;

void main(){
    gl_FragColor = color*vec4(vec3(1.0-z), 1.0)*texture2D(uTexture, fTextureCoord)*vec4(vec3(1.0-z), 1.0)+fog_color*vec4(vec3(z), 1.0);
}
