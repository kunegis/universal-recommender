package de.dailab.recommender.function;

/**
 * The arcustangent sigmoid.
 * 
 * @author kunegis
 */
public class SigmoidAtan
    implements Sigmoid
{
	@Override
	public double apply(double x)
	{
		return Math.atan(x * Math.PI / 2) / (Math.PI / 2);
	}

	@Override
	public String toString()
	{
		return "SigmoidAtan";
	}
}
