package de.dailab.recommender.similarity;

import de.dailab.recommender.function.Function;

/**
 * The Von Neumann graph kernel f(x) = &beta; / (1 &minus; &alpha; x).
 * <p>
 * &beta; is a multiplicative parameter that is irrelevant for recommendation. &alpha; is relevant and must be less than
 * one.
 * 
 * @author kunegis
 */
public class VonNeumannKernel
    extends SpectralTransformation
{
	/**
	 * The Von Neumann graph kernel with the given parameters.
	 * 
	 * @param alpha Inner multiplicative parameter; must be positive and less than one
	 * @param beta Outer multiplicative parameter; must be positive
	 */
	public VonNeumannKernel(final double alpha, final double beta)
	{
		super(new Function()
		{
			@Override
			public double apply(double x)
			{
				final double alpha_x = alpha * x;
				if (!(alpha_x < 1))
				    throw new IllegalArgumentException(String.format(
				        "The parameter must be smaller than the eigenvalue %s", x));

				return beta / (1 - alpha_x);
			}
		}, true);

		if (alpha <= 0) throw new IllegalArgumentException("The parameter alpha must be positive");
		if (alpha >= 1) throw new IllegalArgumentException("The parameter alpha must be less than one");
		if (beta <= 0) throw new IllegalArgumentException("The parameter beta must be positive");

		this.alpha = alpha;
		this.beta = beta;
	}

	/**
	 * The Von Neumann graph kernel with given &alpha; parameter and &beta; = 1.
	 * 
	 * @param alpha Inner multiplicative parameter; must be positive and less than one
	 */
	public VonNeumannKernel(final double alpha)
	{
		this(alpha, 1);
	}

	/**
	 * The Von Neumann graph kernel with default parameters.
	 */
	public VonNeumannKernel()
	{
		this(ALPHA_DEFAULT);
	}

	private final double alpha, beta;

	/**
	 * Default value of the alpha parameter, a half.
	 */
	public static final double ALPHA_DEFAULT = .5;

	@Override
	public String toString()
	{
		if (beta == 1.)
		{
			return String.format("VonNeumann(%g)", alpha);
		}
		else
		{
			return String.format("VonNeumann(%g, %g)", alpha, beta);
		}
	}
}
