package project.android.imageprocessing.filter;

import android.opengl.GLES20;

/**
 * A multi-pixel filter extension of the BasicFilter.  This class passes the texel width and height
 * information to the shaders so that neighbouring pixel locations can be calculated in the shader.
 * @author Chris Batt
 */
public abstract class MultiPixelRenderer extends BasicFilter {
	protected static final String UNIFORM_TEXELWIDTH = "u_TexelWidth";
	protected static final String UNIFORM_TEXELHEIGHT = "u_TexelHeight";
	
	protected float texelWidth;
	protected float texelHeight;
	private int texelWidthHandle;
	private int texelHeightHandle;
	
	/**
	 * Creates a MultiPixelRender that passes the texel width and height information to the shaders.
	 */
	public MultiPixelRenderer() {
		super();
	}
	
	@Override
	protected void handleSizeChange() {
		super.handleSizeChange();
		texelWidth = 1.0f / (float)getWidth();
		texelHeight = 1.0f / (float)getHeight();
	}
	
	@Override
	protected void initShaderHandles() {
		super.initShaderHandles();
		texelWidthHandle = GLES20.glGetUniformLocation(programHandle, UNIFORM_TEXELWIDTH);
		texelHeightHandle = GLES20.glGetUniformLocation(programHandle, UNIFORM_TEXELHEIGHT);
	}
	
	@Override
	protected void passShaderValues() {
		super.passShaderValues();
		GLES20.glUniform1f(texelWidthHandle, texelWidth);
		GLES20.glUniform1f(texelHeightHandle, texelHeight);
	}
}