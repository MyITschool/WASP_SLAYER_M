precision highp float;

uniform float material[6];

uniform float ambient;

uniform vec3 global_light_dir;
uniform vec3 global_light_color;

//          vec4().w = type  0-noDir, 1-p
uniform vec4 uLight[256];

uniform sampler2D uTexture;
uniform sampler2D uNormalTexture;
uniform sampler2D shadowMap;
uniform samplerCube skyBox;
uniform int usSkyBox;

uniform float random_seed;

uniform vec2 utexturesize;

uniform vec3 uViewPos;

uniform vec3 uColor;

uniform int ultroSoftShadow;

uniform float softShadow;
uniform int usShadowMap;
uniform float bias;

uniform float usRandL;

varying vec3 fNormal;
varying vec2 fTextureCoord;
varying vec2 fNormalTextureCoord;
varying mat3 fTBN;
varying vec3 fFragPos;
varying vec4 fShadowCoord;

varying float z;
varying float far_f;

uniform vec4 fog_color;

vec2 r;
float random(){
    r+=random_seed;
    return 0.0;//fract(sin(dot(r, vec2(12.9898, 78.233))) * 43758.5453);
}

vec3 getColor(){
    if(material[1]==-1.){
        return uColor;
    }else{
        return texture2D(uTexture, fTextureCoord).xyz;
    }
}
vec3 getNormal(){
    if(material[2]>-1.){
        return -normalize(fTBN * normalize(texture2D(uNormalTexture, fNormalTextureCoord).rgb * 2.0 - 1.0));
    }else{
        return normalize(fNormal);
    }
}
vec3 getSumLigth(vec3 normal){
    vec3 sumCol = vec3(0.0);
    for(int i = 0; i < 256; i++){
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

                /*float specularStrength = material[3];
                vec3 viewDir = normalize(uViewPos - fFragPos);
                vec3 reflectDir = reflect(-l_dir_p, normal);

                vec3 halfwayDir = normalize(l_dir_p + viewDir);

                float spec = pow(max(dot(normal, halfwayDir), 0.0), material[4]);
                float specular = spec * specularStrength;*/

                vec3 lightDir = normalize(l_pos.xyz - fFragPos);

                vec3 viewDir = normalize(uViewPos - fFragPos);
                vec3 reflectDir = reflect(normal, -lightDir);

                float spec = pow(max(dot(viewDir, reflectDir), 0.0), material[4]);
                float specular = material[3] * spec;


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

//            float diffuse = max(dot(normal, l_dir_p), 0.0);
            float diffuse = max(dot(normal, l_dir_p), 0.0);

            /*float specularStrength = material[3];
            vec3 viewDir = normalize(uViewPos - fFragPos);
            vec3 reflectDir = reflect(-l_dir_p, normal);

            vec3 halfwayDir = normalize(l_dir_p + viewDir);

            float spec = pow(max(dot(normal, halfwayDir), 0.0), material[4]);
            float specular = spec * specularStrength;*/

            vec3 viewDir = normalize(uViewPos - fFragPos);
            vec3 reflectDir = reflect(normal, -l_dir_p);

            float spec = pow(max(dot(viewDir, reflectDir), 0.0), material[4]);
            float specular = material[3] * spec;

            float sumSL = specular+diffuse;
            if(usRandL!=0.){
                sumSL+=random()*usRandL;
            }

            sumCol+=sumSL*l_col.xyz*l_col.w*1.0/(distance*distance);//(diffuse+specular)*l_col.xyz*((l_col.w)*1.0/(distance*distance));

        }

    }
    return sumCol;
}
float getShadow(){
    vec2 projCoords = fShadowCoord.xy / fShadowCoord.w;
    projCoords = projCoords.xy * 0.5 + 0.5;

    float sc = texture2D(shadowMap, projCoords.xy).x;

    float currentDepth = fShadowCoord.z/far_f;

    float s = currentDepth - bias < sc ? 1.0 : 0.0;

    if(s==0.0||ultroSoftShadow==1){

        vec2 tex_offset = 1.0 / utexturesize * sc*softShadow;

        float shadow = 0.;//currentDepth - bias < sc ? 1.0 : 0.0;

        int i = 0;
        for(int x = -10; x <= 10; ++x)
        {
            for(int y = -10; y <= 10; ++y)
            {
                float pcfDepth = texture2D(shadowMap, projCoords.xy + vec2(float(x), float(y)) * tex_offset).x;
                shadow += currentDepth - bias < pcfDepth ? 1.0 : currentDepth;

                i++;
            }
        }
        shadow/=float(i);
        return shadow;
    }
    return 1.0;
}

void main(){
    vec3 color = getColor();

    vec2 u_res = vec2(800,600);
    vec2 uv = (gl_FragCoord.xy - u_res/2.0)/u_res/2.0;
    r=random_seed+uv;

    if(material[0]==1.){
        vec3 lightDir = normalize(global_light_dir - fFragPos);
        vec3 normal = getNormal();

        float diff = max(dot(normal, lightDir), 0.0);
        //vec3 ambientColor = ambient*global_light_color;

        vec3 viewDir = normalize(uViewPos - fFragPos);
        vec3 reflectDir = reflect(normal, -lightDir);

        float spec = pow(max(dot(viewDir, reflectDir), 0.0), material[4]);
        float specular = material[3] * spec;


        /*vec3 halfwayDir = normalize(lightDir + viewDir);
        vec3 specular = material[3] * pow(max(dot(normal, halfwayDir), 0.0), material[4]) * global_light_color;
        */
//        float spec = pow(max(dot(viewDir, reflectDir), 0.0), 32.);
//        //vec3 specular = material[3] * spec * global_light_color;
//
//        vec3 halfwayDir = normalize(lightDir + viewDir);
//        float specular = spec + pow(max(dot(normal, halfwayDir), 0.0), 8.0)*2.;

        color *= (diff+specular+random()/2.)*global_light_color+getSumLigth(normal)+ambient;

        if(usShadowMap == 1){
            color*=getShadow();
        }

        color = pow(color, vec3(1.0/2.2));
    }


    //gl_FragColor = vec4(color, 1.0);
    gl_FragColor = vec4(color, 1.0)*(1.0-z)+fog_color*z;

    //gl_FragColor = vec4(vec3(random()), 1.0);
}