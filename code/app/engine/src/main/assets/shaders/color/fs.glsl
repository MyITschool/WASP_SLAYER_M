precision lowp float;

uniform vec4 color;
uniform vec4 fog_color;

varying float z;

void main(){
    gl_FragColor = color*(1.0-z)+fog_color*z;
}