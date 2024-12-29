package zmaster587.libVulpes.render;

public class TextPart {
	public final String text;
	public final double size;
	public final int colorRGBA;
	public double offsetX;
	public double offsetY;
	
	public TextPart(String txt, double sze, int color) {
		text = txt;
		size = sze;
		colorRGBA = color;
	}
}
