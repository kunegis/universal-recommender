// package de.dailab.recommender.genericmatrix;
//
// import java.util.Collections;
// import java.util.Iterator;
// import java.util.NoSuchElementException;
//
// import de.dailab.recommender.matrix.FullEntry;
// import de.dailab.recommender.matrix.Matrix;
//
// /**
// * A sparse, asymmetric memory-held matrix of generic primitive types, with fast indexing from both dimensions.
// *
// * @param <Index> Index type. One of: Byte, Short, Character, Integer.
// * @param <Value> Value type. Any type corresponding to a primitive type except boolean.
// *
// * @author kunegis
// */
// public final class GenericMemoryMatrix <Index extends Number & Comparable <Index>, Value extends Number>
// implements Matrix
// {
// /**
// * New memory-held sparse asymmetric float matrix of the given size. The constructed matrix is zero.
// *
// * @param m Row count
// * @param n Column count
// */
// @SuppressWarnings("unchecked")
// public GenericMemoryMatrix(int m, int n, Class <?> indexComponentType, Class <?> valueComponentType)
// {
// rows = new GenericMemoryVector[m];
// cols = new GenericMemoryVector[n];
// this.indexComponentType = indexComponentType;
// this.valueComponentType = valueComponentType;
// }
//
// @Override
// public int rows()
// {
// return rows.length;
// }
//
// @Override
// public int cols()
// {
// return cols.length;
// }
//
// @Override
// public void set(int i, int j, double value)
// {
// assert i >= 0 && i < rows.length && j >= 0 && j < cols.length;
//
// if (rows[i] == null) rows[i] = new GenericMemoryVector <Index, Value>(indexComponentType, valueComponentType);
// rows[i].set(j, value);
//
// if (cols[j_int] == null)
// cols[j_int] = new GenericMemoryVector <Index, Value>(indexComponentType, valueComponentType);
// cols[j_int].set(i, value);
// }
//
// /**
// * {@inheritDoc}
// *
// * Iterate in row-by-row order
// */
// @Override
// public Iterable <FullEntry> all()
// {
// return new Iterable <FullEntry>()
// {
// @Override
// public Iterator <FullEntry> iterator()
// {
// return new Iterator <FullEntry>()
// {
// @Override
// public boolean hasNext()
// {
// if (iterator != null && iterator.hasNext()) return true;
//
// if (iterator != null) ++i;
//
// while (i < rows.length && (rows[i] == null || !(iterator = rows[i].iterator()).hasNext()))
// ++i;
// return iterator.hasNext();
// }
//
// @Override
// public FullEntry next()
// {
// if (!hasNext()) throw new NoSuchElementException();
// final Entry entry = iterator.next();
// return new FullEntry(i, entry.index, entry.value);
// }
//
// @Override
// public void remove()
// {
// throw new UnsupportedOperationException();
// }
//
// private int i = 0;
// private Iterator <Entry> iterator = null;
// };
// }
// };
// }
//
// @Override
// public Iterable <Entry> col(int j)
// {
// if (cols[j] == null) return Collections.emptyList();
// return cols[j];
// }
//
// @Override
// public Iterable <Entry> row(int i)
// {
// if (rows[i] == null) return Collections.emptyList();
// return rows[i];
// }
//
// @Override
// public double get(int i, int j)
// {
// if (rows.length < cols.length)
// {
// if (cols[j] == null) return 0.;
// return cols[j].get(i);
// }
// else
// {
// if (rows[i] == null) return 0.;
// return rows[i].get(j);
// }
// }
//
// @Override
// public boolean isSymmetric()
// {
// return false;
// }
//
// @Override
// public double[] mult(double[] v, double[] ret, double weight)
// {
// assert v.length == cols.length;
//
// if (ret == null) ret = new double[rows.length];
//
// assert ret.length == rows.length;
//
// for (int i = 0; i < rows.length; ++i)
// {
// if (rows[i] == null) continue;
// ret[i] += weight * rows[i].mult(v);
// }
//
// return ret;
// }
//
// @Override
// public double[] multT(double[] v, double[] ret, double weight)
// {
// assert v.length == rows.length;
//
// if (ret == null) ret = new double[cols.length];
//
// assert ret.length == cols.length;
//
// for (int j = 0; j < cols.length; ++j)
// {
// if (cols[j] == null) continue;
// ret[j] += weight * cols[j].mult(v);
// }
//
// return ret;
// }
//
// /*
// * Empty rows and columns may be represented by NULL, but don't have to.
// */
// private final GenericMemoryVector <Index, Value> rows[];
// private final GenericMemoryVector <Index, Value> cols[];
//
// private final Class <?> indexComponentType;
// private final Class <?> valueComponentType;
// }
