package de.dailab.recommender.matrix.template;

import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Set;

import de.dailab.recommender.matrix.FullEntry;
import de.dailab.recommender.matrix.Matrix;
import de.dailab.recommender.matrix.Vector;

/**
 * Factory for creating sparse memory-held matrices of a given size and format. Sparse matrices can only be created
 * through this interface.
 * <p>
 * The raw memory requirement for a m by n matrix with r entries of index types i and j and value type v is {@code r *
 * (i + j + 2 * v) + (m + n) * (3 * P + 3 * O + I)}, where O is the pointer size in the JVM, I is the size of int and O
 * the overhead for an object on the heap, which is at least 8 bytes.
 * 
 * @author kunegis
 */
public class MatrixFactory
{
	/**
	 * Create an asymmetric sparse memory matrix of given size with minimal index sizes. The matrix is initially empty.
	 * 
	 * @param m row count
	 * @param n column count
	 * @param valueType type of values in the matrix. The name of the primitive type, e.g. "byte", "float". May be NULL
	 *        to denote the default type (float). May be "boolean" to denote a binary matrix.
	 * 
	 * @return an empty sparse memory-held matrix of the given size.
	 */
	public static Matrix newMemoryMatrix(int m, int n, String valueType)
	{
		assert m > 0;
		assert n > 0;

		if (valueType == null) valueType = float.class.getSimpleName();
		assert VALUE_TYPES.contains(valueType);

		final String indexA = getIndexType(m);
		final String indexB = getIndexType(n);

		final String packageName = MatrixFactory.class.getPackage().getName();

		final String className = packageName
		    + "."
		    + (valueType.equals(boolean.class.getSimpleName()) ? "MemoryBinaryMatrix" + upcase(indexA) + upcase(indexB)
		        : "MemoryMatrix" + upcase(indexA) + upcase(indexB) + upcase(valueType));

		try
		{
			final Class <?> matrixClass = Class.forName(className);

			final Object obj = matrixClass.getConstructor(int.class, int.class).newInstance(Integer.valueOf(m),
			    Integer.valueOf(n));

			assert obj instanceof Matrix;

			return (Matrix) obj;
		}
		catch (final ClassNotFoundException classNotFoundException)
		{
			assert false;
			throw new RuntimeException(classNotFoundException);
		}
		catch (final NoSuchMethodException noSuchMethodException)
		{
			assert false;
			throw new RuntimeException(noSuchMethodException);
		}
		catch (final InvocationTargetException invocationTargetException)
		{
			assert false;
			throw new RuntimeException(invocationTargetException);
		}
		catch (final IllegalAccessException illegalAccessException)
		{
			assert false;
			throw new RuntimeException(illegalAccessException);
		}
		catch (final InstantiationException instantiationException)
		{
			assert false;
			throw new RuntimeException(instantiationException);
		}
	}

	/**
	 * Create a sparse memory vector of given size with minimal index size. The vector is initially empty.
	 * 
	 * @param n entry count
	 * @param valueType type of values in the vector. The name of the primitive type, e.g. "byte", "float". May be NULL
	 *        to denote the default type (float). May be "boolean" to denote a binary matrix.
	 * 
	 * @return an empty sparse memory-held matrix of the given size.
	 */
	public static Vector newMemoryVector(int n, String valueType)
	{
		assert n >= 0;

		if (valueType == null) valueType = float.class.getSimpleName();
		assert VALUE_TYPES.contains(valueType);

		final String indexType = getIndexType(n);

		return newMemoryVector(indexType, valueType);
	}

	/**
	 * Create a sparse memory-held vector of the given index and value type.
	 * 
	 * @param indexType Type of indexes
	 * @param valueType Type of values
	 * @return An empty vector
	 */
	public static Vector newMemoryVector(String indexType, String valueType)
	{
		final String packageName = MatrixFactory.class.getPackage().getName();

		final String className = packageName
		    + "."
		    + (valueType.equals(boolean.class.getSimpleName()) ? "MemoryBinaryVector" + upcase(indexType)
		        : "MemoryVector" + upcase(indexType) + upcase(valueType));

		try
		{
			final Class <?> vectorClass = Class.forName(className);

			/*
			 * Don't pass N as parameter because that would be the capacity, not the size.
			 */
			final Object obj = vectorClass.getConstructor().newInstance();

			assert obj instanceof Vector;

			return (Vector) obj;
		}
		catch (final ClassNotFoundException classNotFoundException)
		{
			assert false;
			throw new RuntimeException(classNotFoundException);
		}
		catch (final NoSuchMethodException noSuchMethodException)
		{
			assert false;
			throw new RuntimeException(noSuchMethodException);
		}
		catch (final InvocationTargetException invocationTargetException)
		{
			assert false;
			throw new RuntimeException(invocationTargetException);
		}
		catch (final IllegalAccessException illegalAccessException)
		{
			assert false;
			throw new RuntimeException(illegalAccessException);
		}
		catch (final InstantiationException instantiationException)
		{
			assert false;
			throw new RuntimeException(instantiationException);
		}
	}

	/**
	 * A sparse memory-held vector of the default value type, which is float. The index type is determined from the
	 * given size.
	 * 
	 * @param n Size of the vector
	 * @return An empty vector
	 */
	static public Vector newMemoryVector(int n)
	{
		return newMemoryVector(n, float.class.getSimpleName());
	}

	/**
	 * Create a sparse memory-held binary vector. The index type is inferred from the max size.
	 * 
	 * @param n Max size that can be used in the vector; this is not the size as vectors do not have a fixed size
	 * @return Empty vector
	 */
	static public Vector newMemoryVectorUnweighted(int n)
	{
		return newMemoryVector(n, boolean.class.getSimpleName());
	}

	/**
	 * Create a sparse binary memory-held vector of the given index type.
	 * 
	 * @param indexType The index type, e.g. "short" or "boolean"
	 * @return Empty vector
	 */
	static public Vector newMemoryVectorUnweighted(String indexType)
	{
		return newMemoryVector(indexType, boolean.class.getSimpleName());
	}

	/**
	 * Create an empty sparse matrix of the given size using the default value type.
	 * <p>
	 * The default value type is float.
	 * 
	 * @param m row count
	 * @param n column count
	 * @return an empty sparse matrix of the given size
	 */
	public static Matrix newMemoryMatrix(int m, int n)
	{
		return newMemoryMatrix(m, n, float.class.getSimpleName());
	}

	/**
	 * A square empty sparse memory-held matrix of size n by n of the default value type, which is float.
	 * 
	 * @param n Number of rows and columns
	 * @return The created matrix
	 */
	public static Matrix newMemoryMatrix(int n)
	{
		return newMemoryMatrix(n, n, float.class.getSimpleName());
	}

	/**
	 * A square empty sparse memory-held matrix of size n by n of the given value type.
	 * 
	 * @param n Number of rows and columns
	 * @param valueType The type of entries
	 * @return The created matrix
	 */
	public static Matrix newMemoryMatrix(int n, String valueType)
	{
		return newMemoryMatrix(n, n, valueType);
	}

	/**
	 * An unweighted matrix of the given size.
	 * 
	 * @param m Row count
	 * @param n Column count
	 * @return The built matrix
	 */
	public static Matrix newMemoryMatrixUnweighted(int m, int n)
	{
		return newMemoryMatrix(m, n, boolean.class.getSimpleName());
	}

	/**
	 * An unweighted square matrix of the given size.
	 * 
	 * @param n Row and column count
	 * @return The built matrix of size n by n
	 */
	public static Matrix newMemoryMatrixUnweighted(int n)
	{
		return newMemoryMatrix(n, n, boolean.class.getSimpleName());
	}

	/**
	 * Create an empty sparse matrix of the same size and value type as a given matrix.
	 * 
	 * @param matrix A matrix
	 * @return A new empty sparse matrix of the correct size and value type
	 */
	public static Matrix newMemoryMatrix(Matrix matrix)
	{
		try
		{
			final Class <? extends Matrix> matrixClass = matrix.getClass();

			final Object obj = matrixClass.getConstructor(int.class, int.class).newInstance(
			    Integer.valueOf(matrix.rows()), Integer.valueOf(matrix.cols()));

			return (Matrix) obj;
		}
		catch (final IllegalArgumentException exception)
		{
			assert false;
			throw new RuntimeException(exception);
		}
		catch (final SecurityException exception)
		{
			assert false;
			throw new RuntimeException(exception);
		}
		catch (final InstantiationException exception)
		{
			assert false;
			throw new RuntimeException(exception);
		}
		catch (final IllegalAccessException exception)
		{
			assert false;
			throw new RuntimeException(exception);
		}
		catch (final InvocationTargetException exception)
		{
			assert false;
			throw new RuntimeException(exception);
		}
		catch (final NoSuchMethodException exception)
		{
			assert false;
			throw new RuntimeException(exception);
		}
	}

	/**
	 * Make a memory-held copy of a matrix.
	 * 
	 * @param matrix The matrix to copy
	 * @return The copied memory-held matrix
	 */
	public static Matrix copyMatrix(Matrix matrix)
	{
		final Matrix ret = newMemoryMatrix(matrix);
		for (final FullEntry fullEntry: matrix.all())
		{
			ret.set(fullEntry.rowIndex, fullEntry.colIndex, fullEntry.value);
		}
		return ret;
	}

	/**
	 * Return the index type necessary to represent indexes that are smaller than n.
	 * 
	 * @return the name of the primitive type.
	 */
	private static String getIndexType(int n)
	{
		if (n <= 1 + Byte.MAX_VALUE) return "byte";
		if (n <= 1 + Character.MAX_VALUE) return "char";
		return "int";
	}

	/**
	 * Convert the first character to uppercase.
	 * 
	 * @param name type name
	 * @return type name with first character in upper case.
	 */
	@SuppressWarnings("cast")
	private static String upcase(String name)
	{
		return "" + (char) ((int) name.charAt(0) + (int) ('A' - 'a')) + name.substring(1);
	}

	private final static Set <String> VALUE_TYPES = new HashSet <String>();

	static
	{
		VALUE_TYPES.add(boolean.class.getSimpleName());
		VALUE_TYPES.add(byte.class.getSimpleName());
		VALUE_TYPES.add(short.class.getSimpleName());
		VALUE_TYPES.add(char.class.getSimpleName());
		VALUE_TYPES.add(int.class.getSimpleName());
		VALUE_TYPES.add(long.class.getSimpleName());
		VALUE_TYPES.add(float.class.getSimpleName());
		VALUE_TYPES.add(double.class.getSimpleName());
	}
}
