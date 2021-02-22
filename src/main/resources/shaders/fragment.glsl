#version 300 es
precision mediump float;

struct Light { vec3 position; vec3 color; int on; };
struct Colors { vec3 ambient; vec3 diffuse; vec3 specular; };
struct Textures { sampler2D ambient; sampler2D diffuse; sampler2D specular; };
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

vec3 gammaCorrect(vec3 rgb) {
    return pow(rgb, GAMMA);
}

void main() {
    vec3 diffuseColor = u_colors.diffuse * vec3(texture(u_textures.diffuse, v_world.uv));
    vec3 N = normalize(v_world.normal);
    vec3 V = normalize(u_camera_position - v_world.position);

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
    fragColor = vec4(gammaCorrect(ambient + diffuseSum + specularSum), 1.0);
}

