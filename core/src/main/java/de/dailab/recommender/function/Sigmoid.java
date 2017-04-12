package de.dailab.recommender.function;

/**
 * A sigmoid function.
 * <p>
 * Characteristics: (i) odd function (ii) nondecreasing (iii) lim (-inf) = -1, lim (+inf) = +1 * d/dx f(x) = 1 for x =
 * 0.
 * <p>
 * Implementations make sure they are scaled accordingly.
 * 
 * @author kunegis
 */
public interface Sigmoid
    extends Function
{}
