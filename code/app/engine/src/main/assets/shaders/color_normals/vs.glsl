attribute vec4 vPosition;
attribute vec4 vNormal;

uniform mat4 uVPMatrix;
uniform mat4 uModelMatrix;
uniform float far;

varying vec3 normal;
varying vec3 fFragPos;
varying float z;

void main(){
    normal = normalize(uModelMatrix*vNormal).xyz;
    fFragPos = (uModelMatrix*vPosition).xyz;

    vec4 position = uVPMatrix*uModelMatrix*vPosition;
    z = position.z/far;

    gl_Position = position;
}