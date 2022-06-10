precision lowp float;

uniform vec4 color;
uniform vec4 fog_color;
uniform sampler2D shadowMap;
uniform int softShadow;
uniform float bias;
uniform float ambient;

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
            //col.xyz*=0.5;
            float shadow = 0.0;
            for(int x = -softShadow; x < softShadow; x++)
            {
                for(int y = -softShadow; y < softShadow; y++)
                {
                    float pcfDepth = texture2D(shadowMap, projCoords.xy + vec2(float(x), float(y)) * 0.001).x;
                    shadow += currentDepth - bias < pcfDepth ? 1.0 : ambient;
                }
            }
            col.xyz*=shadow/float(softShadow*softShadow*4);
        }
    }

    col = pow(col, vec4(vec3(0.4545), 1.0));

    gl_FragColor = col*vec4(vec3(1.0-z), 1.0)+fog_color*vec4(vec3(z), 1.0);
}