package au.edu.jcu.cp3406_cp5307_utilityappstartertemplate.ui.theme

import org.intellij.lang.annotations.Language

@Language("AGSL")
val BACKGROUND_SHADER_SRC = """
// very important --- do not leave out
uniform float2 iResolution;
uniform float  iTime;
uniform float iDuration;

const int NUM_OCTAVES =  5;

float rand(float2 co) {
    return fract(sin(dot(co, float2(12.9898, 78.233))) * 43758.5453);
}

float circle(in float2 _st, in float _radius){
    float2 dist = _st - float2(0.5);
    return 1.0 - smoothstep(_radius - (_radius * 0.01),
                            _radius + (_radius * 0.01),
                            dot(dist, dist) * 4.0);
}

float star(float2 st, float radius) {
    float d = length(st - float2(0.5));
    return smoothstep(radius, 0.0, d);
}

float noise(float2 st) {
    float2 i = floor(st);
    float2 f = fract(st);
    
    float a = rand(i);
    float b = rand(i + float2(1.0, 0.0));
    float c = rand(i + float2(0.0, 1.0));
    float d = rand(i + float2(1.0, 1.0));
    
    // interpolation
    float2 u = f * f * (3.0 - 2.0 * f);
    
    // mix 4 corners based on curve
    return mix(a, b, u.x) + (c - a) * u.y * (1.0 - u.x) + (d - b) * u.x * u.y;
}
    
// fractional brownian motion
float fbm(in float2 _st) {
    float v = 0.0;
    float a = 0.5;
    float2 shift = float2(100.0);
    mat2 rot = mat2(cos(0.5), sin(0.5),
                    -sin(0.5), cos(0.50));
                    
    for (int i = 0; i < NUM_OCTAVES; ++i) {
        v += a * noise(_st);
        _st = rot * _st * 2.0 + shift;
        a *= 0.5;
    }
    return v;
}

half4 main(float2 fragCoord) {
    float2 uv = (fragCoord * 2.0 - iResolution.xy) / iResolution.xy;
    uv.y = -uv.y; 
    uv.x *= iResolution.x / iResolution.y;
    
    float2 scaledUv = uv * 5.0;
    float2 cellId = floor(scaledUv);
    float2 localUv = fract(scaledUv);
    
    float cellHash = rand(cellId);
    float twinkle = sin(iTime * 3.0 + cellHash * 6.28) * 0.5 + 0.5;
    
    float3 starCol = float3(0.0);
    
    // nebula 
    float2 drift = float2(cos(iTime * 0.1) * 0.15, sin(iTime * 0.1) * 0.15);
    float2 nebulaUv = uv * 2.5 + 2.5 + drift;
    
    float cloudDensity = fbm(nebulaUv + fbm(nebulaUv + iTime * 0.02));
    cloudDensity = smoothstep(0.2, 0.8, cloudDensity);
    
    float3 nebulaColor1 = float3(0.4, 0.5, 0.6); 
    float3 nebulaColor2 = float3(1.6, 0.6, 0.5); 
   
    float3 finalNebula = mix(nebulaColor2, nebulaColor1, cloudDensity) * cloudDensity * 0.4;
    
    // each cell has % chance 
    if (cellHash > 0.7) {
        float baseRadius = cellHash * 0.15;
        float currentRadius = baseRadius * (0.3 + 0.7 * twinkle);
        
        float starCore = star(localUv, currentRadius);
        
        // adds diffraction spikes 
        float2 distFromCenter = abs(localUv - float2(0.5));
        float spikes = 0.001 / (distFromCenter.x * distFromCenter.y);
        
        // stops spikes bleeding past boundaries
        spikes *= smoothstep(0.4, 0.1, length(localUv - float2(0.5)));
        starCol = float3(starCore + spikes * currentRadius);
    }
    
    float3 starColor = float3(0.8) + 0.2 * cos(cellHash * 6.28 + float3(0.0, 1.0, 2.0));
    float3 finalStars = starCol * starColor;
    
    float3 finalColor = finalNebula + finalStars;
    
    return half4(finalColor, 1.0);
}
""".trimIndent()