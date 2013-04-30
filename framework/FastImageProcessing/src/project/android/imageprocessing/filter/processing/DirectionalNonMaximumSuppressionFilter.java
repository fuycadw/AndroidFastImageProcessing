package project.android.imageprocessing.filter.processing;

import project.android.imageprocessing.filter.MultiPixelRenderer;
import android.opengl.GLES20;

public class DirectionalNonMaximumSuppressionFilter extends MultiPixelRenderer {
	private static final String UNIFORM_UPPER_THRESHOLD = "u_UpperThreshold";
	private static final String UNIFORM_LOWER_THRESHOLD = "u_LowerThreshold";
	
	private float upperThreshold;
	private float lowerThreshold;
	private int upperThresholdHandle;
	private int lowerThresholdHandle;
	
	/**
	 * Creates a ImageGammaFilter with the given gamma adjustment value.
	 * @param gamma
	 * The gamma adjustment value.
	 */
	public DirectionalNonMaximumSuppressionFilter(float upperThreshold, float lowerThreshold) {
		this.upperThreshold = upperThreshold;
		this.lowerThreshold = lowerThreshold;
	}
	
	@Override
	protected void initShaderHandles() {
		super.initShaderHandles();
		upperThresholdHandle = GLES20.glGetUniformLocation(programHandle, UNIFORM_UPPER_THRESHOLD);
		lowerThresholdHandle = GLES20.glGetUniformLocation(programHandle, UNIFORM_LOWER_THRESHOLD);
	}
	
	@Override
	protected void passShaderValues() {
		super.passShaderValues();
		GLES20.glUniform1f(upperThresholdHandle, upperThreshold);
		GLES20.glUniform1f(lowerThresholdHandle, lowerThreshold);
	} 
	
	@Override
	protected String getFragmentShader() {
		return 
				 "precision mediump float;\n" 
				+"uniform sampler2D "+UNIFORM_TEXTURE0+";\n"  
				+"varying vec2 "+VARYING_TEXCOORD+";\n"	
				+"uniform float "+UNIFORM_TEXELWIDTH+";\n"
				+"uniform float "+UNIFORM_TEXELHEIGHT+";\n"
				+"uniform float "+UNIFORM_UPPER_THRESHOLD+";\n"
				+"uniform float "+UNIFORM_LOWER_THRESHOLD+";\n"
				
		  		+"void main(){\n"
		  		+"   vec3 currentGradientAndDirection = texture2D("+UNIFORM_TEXTURE0+","+VARYING_TEXCOORD+").rgb;\n"
		  		+"   vec2 gradientDirection = ((currentGradientAndDirection.gb * 2.0) - 1.0) * vec2("+UNIFORM_TEXELWIDTH+", "+UNIFORM_TEXELHEIGHT+");\n"
		  		+"   float firstSampledGradientMagnitude = texture2D("+UNIFORM_TEXTURE0+","+VARYING_TEXCOORD+" + gradientDirection).r;\n"
		  		+"   float secondSampledGradientMagnitude = texture2D("+UNIFORM_TEXTURE0+","+VARYING_TEXCOORD+" - gradientDirection).r;\n"
		  		+"   float multiplier = step(firstSampledGradientMagnitude, currentGradientAndDirection.r);\n"
		  		+"   multiplier = multiplier * step(secondSampledGradientMagnitude, currentGradientAndDirection.r);\n"
	     
		  		+"   float thresholdCompliance = smoothstep("+UNIFORM_LOWER_THRESHOLD+", "+UNIFORM_UPPER_THRESHOLD+", currentGradientAndDirection.r);\n"
		  		+"   multiplier = multiplier * thresholdCompliance;\n"
	     
		  		+"   gl_FragColor = vec4(vec3(multiplier), 1.0);\n"
				+"}\n";
	}
}