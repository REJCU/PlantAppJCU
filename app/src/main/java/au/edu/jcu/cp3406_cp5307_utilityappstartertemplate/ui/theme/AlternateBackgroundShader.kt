package au.edu.jcu.cp3406_cp5307_utilityappstartertemplate.ui.theme

import org.intellij.lang.annotations.Language

@Language("AGSL")
val ALTERNATE_SHADER_SRC = """
// very important --- do not leave out
uniform float2 iResolution;
uniform float  iTime;
uniform float iDuration;
    

    
    return half4(finalColor, 1.0);
}
""".trimIndent()