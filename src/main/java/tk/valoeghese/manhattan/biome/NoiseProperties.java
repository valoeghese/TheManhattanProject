package tk.valoeghese.manhattan.biome;

public class NoiseProperties {
	public NoiseProperties(float depth, float scale, float thicknessVariation) {
		this.depth = depth;
		this.scale = scale;
		this.thicknessVariation = thicknessVariation;
	}

	public final float depth;
	public final float scale;
	public final float thicknessVariation;
}
