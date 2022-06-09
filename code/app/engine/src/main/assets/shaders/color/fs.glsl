precision lowp float;

uniform vec4 color;
uniform vec4 fog_color;
uniform sampler2D shadowMap;
uniform int softShadow;
uniform float bias;

varying float z;
varying vec4 fShadowCoord;
varying float far_f;

void main(){
    vec4 col = color;

    if(softShadow>0){
        vec2 projCoords = fShadowCoord.xy / fShadowCoord.w;
        projCoords = projCoords.xy * 0.5 + 0.5;

        float sc = texture2D(shadowMap, projCoords.xy).x;

        float currentDepth = fShadowCoord.z/far_f;

        if(currentDepth - bias >= sc){
            col.xyz*=0.5;
        }
    }

    col = pow(col, vec4(vec3(1.0/2.2), 1.0));

    gl_FragColor = col*vec4(vec3(1.0-z), 1.0)+fog_color*vec4(vec3(z), 1.0);
}