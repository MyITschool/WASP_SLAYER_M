precision mediump float;

varying float fz;

void main(){
    gl_FragColor = vec4(vec3(fz/100.), 1.0);
}