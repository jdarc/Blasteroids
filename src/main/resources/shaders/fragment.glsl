#version 300 es
precision mediump float;

struct Light { vec3 position; vec3 color; int on; };
struct Colors { vec3 ambient; vec3 diffuse; vec3 specular; };
struct Textures { sampler2D ambient; sampler2D diffuse; sampler2D specular; sampler2D normal; };
struct Vertex { vec3 position; vec3 normal; vec2 uv; };

const vec3 GAMMA = vec3(1.0 / 2.2);

uniform Colors u_colors;
uniform Textures u_textures;
uniform Light u_lights[8];

uniform float u_ambientIntensity;
uniform float u_shininess;
uniform vec3 u_camera_position;

in Vertex v_world;
out vec4 fragColor;

const float EXPOSURE = 1.5;

mat3 cotangentFrame(vec3 N, vec3 p, vec2 uv) {
    vec3 dp1 = dFdx(p);
    vec3 dp2 = dFdy(p);
    vec2 duv1 = dFdx(uv);
    vec2 duv2 = dFdy(uv);
    vec3 dp2perp = cross(dp2, N);
    vec3 dp1perp = cross(N, dp1);
    vec3 T = dp2perp * duv1.x + dp1perp * duv2.x;
    vec3 B = dp2perp * duv1.y + dp1perp * duv2.y;
    float invmax = inversesqrt(max(dot(T, T), dot(B, B)));
    return mat3(T * invmax, B * invmax, N);
}

vec3 perturbNormal(vec3 N, vec3 V, vec2 texcoord) {
    vec3 map = (texture(u_textures.normal, texcoord).xyz * 255.0 - 128.0) / 127.0;
    return normalize(cotangentFrame(N, -V, texcoord) * map);
}

// https://hackmd.io/@jgilbert/sRGB-WebGL
vec3 linearToSrgb(vec3 rgb) {
    return clamp(mix(1.055 * pow(rgb, vec3(1.0 / 2.4)) - 0.055, 12.92 * rgb, step(rgb, vec3(0.0031308))), 0.0, 1.0);
}

// https://knarkowicz.wordpress.com/2016/01/06/aces-filmic-tone-mapping-curve/
vec3 ACESFilm(vec3 x) {
    float a = 2.51;
    float b = 0.03;
    float c = 2.43;
    float d = 0.59;
    float e = 0.14;
    return clamp((x * (a * x + b)) / (x * (c * x + d) + e), 0.0, 1.0);
}

vec3 gammaCorrect(vec3 rgb) {
    return pow(rgb, GAMMA);
}

void main() {
    vec3 diffuseColor = u_colors.diffuse * texture(u_textures.diffuse, v_world.uv).rgb;
    vec3 V = normalize(u_camera_position - v_world.position);
    vec3 N = perturbNormal(normalize(v_world.normal), V, v_world.uv);

    vec3 diffuseSum = vec3(0.0);
    vec3 specularSum = vec3(0.0);

    for (int i = 0; i < 8; ++i) {
        Light light = u_lights[i];
        if (bool(light.on)) {
            vec3 L = normalize(light.position - v_world.position);
            float lambertian = max(dot(N, L), 0.0);
            if (lambertian > 0.0) {
                diffuseSum += lambertian * diffuseColor * light.color;
                vec3 R = reflect(-L, N);
                float specAngle = max(dot(R, V), 0.0);
                specularSum += pow(specAngle, u_shininess) * u_colors.specular * vec3(texture(u_textures.specular, v_world.uv));
            }
        }
    }

    vec3 ambient = u_ambientIntensity * u_colors.ambient * vec3(texture(u_textures.ambient, v_world.uv)) * diffuseColor;
    fragColor = vec4(linearToSrgb(ACESFilm((ambient + diffuseSum + specularSum) * EXPOSURE)), 1.0);
}

