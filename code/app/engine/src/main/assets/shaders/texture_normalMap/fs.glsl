precision lowp float;

uniform vec4 color;
uniform vec3 global_light_dir;
uniform vec3 global_light_color;
uniform vec4 uLight[252];
uniform vec3 uViewPos;
uniform float ambient;
uniform vec4 fog_color;
uniform vec2 specular;
uniform sampler2D uTexture;
uniform sampler2D uNormalTexture;
uniform sampler2D shadowMap;
uniform int softShadow;
uniform float bias;

varying vec3 fFragPos;
varying float z;
varying vec2 fTextureCoord;
varying vec2 fNormalTextureCoord;
varying mat3 fTBN;
varying float far_f;
varying vec4 fShadowCoord;

vec4 getSumLigth(vec3 normal){
    vec3 sumCol = vec3(0.0);
    for(int i = 0; i < 252; i++){
        if(uLight[i+1]==vec4(0.0)){
            break;
        }
        vec4 l_pos = uLight[i];
        vec4 l_dir = vec4(0.0);
        vec4 l_col = vec4(1.0);
        if(l_pos.w>0.0){
            i++;
            l_dir = uLight[i];
            i++;
            l_col = uLight[i];

            vec3 l_dir_p = normalize(l_pos.xyz - fFragPos);

            float theta = dot(-l_dir_p, normalize(l_dir.xyz));

            if(theta>l_pos.w){
                float distance = length(l_pos.xyz - fFragPos);

                float diffuse = max(dot(normal, l_dir_p), 0.0);

                vec3 lightDir = normalize(l_pos.xyz - fFragPos);

                vec3 viewDir = normalize(uViewPos - fFragPos);
                vec3 reflectDir = reflect(normal, -lightDir);

                float spec = pow(max(dot(viewDir, reflectDir), 0.0), specular.y);
                float specular = specular.x * spec;


                float intensity = theta-l_pos.w;
                if(intensity < 0.0){ intensity = 0.0; }

                vec3 col = diffuse*l_col.xyz+sumCol*l_col.xyz;

                sumCol+= col*intensity*((l_col.w)*1.0/(distance*distance));

            }
        }else{
            i++;
            l_col = uLight[i];

            float distance = length(l_pos.xyz - fFragPos);

            vec3 l_dir_p = -normalize(fFragPos - l_pos.xyz);

            float diffuse = max(dot(normal, l_dir_p), 0.0);

            vec3 viewDir = normalize(uViewPos - fFragPos);
            vec3 reflectDir = reflect(normal, -l_dir_p);

            float spec = pow(max(dot(viewDir, reflectDir), 0.0), specular.y);
            float specular = specular.x * spec;

            float sumSL = specular+diffuse;

            sumCol+=sumSL*l_col.xyz*l_col.w*1.0/(distance*distance);

        }

    }
    return vec4(sumCol, 1.0);
}

void main(){
    vec3 normal = normalize(fTBN * normalize(texture2D(uNormalTexture, fNormalTextureCoord).rgb * 2.0 - 1.0));

    float diff = max(dot(normal, global_light_dir), 0.0);

    vec3 viewDir = normalize(uViewPos - fFragPos);
    vec3 reflectDir = reflect(normal, -global_light_dir);

    float spec = pow(max(dot(viewDir, reflectDir), 0.0), specular.y);
    float specular = specular.x * spec;

    vec4 col = color*((diff+specular)*vec4(global_light_color, 1.0)+ambient+getSumLigth(normal));

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

    gl_FragColor = col*texture2D(uTexture, fTextureCoord)*vec4(vec3(1.0-z), 1.0)+fog_color*vec4(vec3(z), 1.0);
}
