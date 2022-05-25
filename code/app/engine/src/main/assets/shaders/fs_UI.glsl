precision highp float;

uniform float material[1];
uniform sampler2D uTexture;

uniform vec4 uColor;

varying vec2 fTextureCoord;

void main() {
    vec4 color;
    if(material[0]>-1.){
        color = texture2D(uTexture, fTextureCoord);
    }else{
        color = uColor;
    }
    gl_FragColor = color;
}
