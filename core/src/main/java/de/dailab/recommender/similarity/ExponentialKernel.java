package de.dailab.recommender.similarity;

import de.dailab.recommender.function.Function;

/**
 * The exponential kernel as a spectral transformation.
 * <p>
 * This kernel is defined as f(x) = &beta; exp{&alpha; x}.
 * <p>
 * The parameter &beta; amounts to a constant factor applied to prediction values and is irrelevant for recommendation.
 * The parameter &alpha; is relevant in all scenarios and should be estimated. The default value for &alpha; available
 * in this class is a estimate for datasets of common sizes, and should only be used with normalization (which is
 * enabled by default).
 * <p>
 * If the normalized parameter is set, the kernel used is f(x) = &beta; exp{(&alpha; / |&lambda;<sub>0</sub>|) x}, where
 * |&lambda;<sub>0</sub>| is the absolute largest eigenvalue, i.e. usually the spectral radius of the network.
 * 
 * @author kunegis
 */
public class ExponentialKernel
    extends SpectralTransformation
{
	/**
	 * The exponential kernel using the given parameters. The kernel corresponds to beta * exp(alpha * x).
	 * 
	 * @param alpha The inner multiplicative parameter
	 * @param beta The outer multiplicative parameter
	 * @param normalized Whether to normalized eigenvalues such that the largest has absolute value one
	 */
	public ExponentialKernel(final double alpha, final double beta, boolean normalized)
	{
		super(getFunction(alpha, beta), normalized);

		this.alpha = alpha;
		this.beta = beta;
	}

	/**
	 * The exponential kernel with default value of the "normalized" parameter.
	 * 
	 * @param alpha The alpha parameter
	 * @param beta The beta parameter
	 */
	public ExponentialKernel(double alpha, double beta)
	{
		super(getFunction(alpha, beta));

		this.alpha = alpha;
		this.beta = beta;
	}

	/**
	 * The exponential kernel using beta = 1.
	 * 
	 * @param alpha The inner multiplicative parameter
	 */
	public ExponentialKernel(final double alpha)
	{
		this(alpha, 1);
	}

	/**
	 * The exponential kernel with all default parameters.
	 */
	public ExponentialKernel()
	{
		this(ALPHA_DEFAULT, 1);
	}

	private final double alpha, beta;

	/**
	 * Default value of the alpha parameter.
	 */
	public static final double ALPHA_DEFAULT = 1;

	@Override
	public String toString()
	{
		if (beta == 1.)
		{
			return String.format("Exp(%g)", alpha);
		}
		else
		{
			return String.format("Exp(%g, %g)", alpha, beta);
		}
	}

	private static Function getFunction(final double alpha, final double beta)
	{
		return new Function()
		{
			@Override
			public double apply(double x)
			{
				return beta * Math.exp(alpha * x);
			}
		};
	}
}
