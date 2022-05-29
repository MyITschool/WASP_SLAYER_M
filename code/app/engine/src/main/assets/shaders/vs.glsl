attribute vec4 vPosition;
attribute vec3 vNormal;
attribute vec2 vTextureCoord;
attribute vec2 vNormalTextureCoord;
attribute vec3 vTangent;

uniform mat4 uVPMatrix;
uniform mat4 uModelMatrix;
uniform mat4 depthMVP;
uniform mat4 rotateMatrix;

uniform float material[6];

varying vec3 fNormal;
varying vec2 fTextureCoord;
varying vec2 fNormalTextureCoord;
varying mat3 fTBN;
varying vec3 fFragPos;
varying vec4 fShadowCoord;
varying float z;

uniform float far;
varying float far_f;

void main(){

   // if(material[5]>-1.){
      //  fFragPos=vPosition.xyz;
      //  fFragPos.z=-vPosition.z;

//        gl_Position = uVPMatrix * vPosition;
//        gl_Position = gl_Position.xyww;
      //  outPos = (uVPMatrix * vPosition).xyww;

        //return;
   // }else{
        fNormal = (rotateMatrix*vec4(vNormal,1.0)).xyz;
        fTextureCoord = vTextureCoord;
        fNormalTextureCoord = vNormalTextureCoord;
        fFragPos = (uModelMatrix*vPosition).xyz;

        vec3 T = normalize(vec3(uModelMatrix * vec4(vTangent,   0.0)));
        vec3 N = normalize(vec3(rotateMatrix * vec4(vNormal,    0.0)));
        vec3 B = normalize(vec3(uModelMatrix * vec4(cross(N, T), 0.0)));
        mat3 TBN = mat3(T, B, N);

        fTBN = TBN;

        fShadowCoord = depthMVP*uModelMatrix*vPosition;

   // }
        far_f=far;
        z=(uVPMatrix*uModelMatrix*vPosition).z/far;
    gl_Position = uVPMatrix*uModelMatrix*vPosition;
}