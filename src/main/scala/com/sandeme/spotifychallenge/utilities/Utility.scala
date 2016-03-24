package com.sandeme.spotifychallenge.utilities

/**
  * Created by sandeme on 3/5/16.
  * The idea was taken from a blog a while ago. The idea is to store Vectors. Really these are
  * Vectors, but the Stats class will use the DblVector and DblMatrix to do some calculations on them.
  */
object Utility {
  type XY = (Double, Double)
  type XYTSeries = Array[(Double, Double)]
  type DMatrix[T] = Array [Array[T]]
  type DVector[T] = Array [T]
  type DblMatrix = DMatrix[Double]
  type DblVector = DVector[Double]

  // Implicit conversions to convert primitives into types above
  implicit def int2Double(n: Int): Double = n.toDouble
  implicit def vector2DblVector[T <% Double] (vector: DVector[T]): DblVector = { vector.map(_.toDouble) }
  implicit def double2DblVector(x: Double): DblVector = { Array[Double] (x) }
  implicit def dblPair2DblVector(x: (Double, Double)): DblVector = { Array[Double](x._1, x._2) }


  def Op[T <% Double](v: DVector[T], w: DblVector, op: (T, Double) => Double): DblVector = {v.zipWithIndex.map(x => op(x._1, w(x._2))) }

  // Take a vector or matricies and divide them by a const value, the matrix is only one column
  implicit def /(v: DblVector, n:Int): DblVector = v.map(x => x / n)
  implicit def /(m: DblMatrix, col: Int, z: Double): DblMatrix = {
    (0 until m(col).size).foreach(i => m(col)(i) /= z )
    m
  }

  /**
    * @see <a link="https://en.wikipedia.org/wiki/Correlation_and_dependence"Correlation and Dependence</a>
    *      Lightweight implementation to view coorelation. Of Course another place is just to use the Apache commons again,
    *      hosever going over this helps understnad it better.
    * @param vector1
    * @param vector2
    * @return
    */
  def correlation(vector1: DblVector, vector2: DblVector): Double = {
    if (vector1.size != vector1.size) throw new Exception("ERROR Tried to compare two Vectors of different sizes")
    val stats1 = new Stats(vector1)
    val stats2 = new Stats(vector2)
    var residualProduct = 0.0
    for (i <- 0 to vector1.size - 1) {
      residualProduct += stats1.residuals(i) * stats2.residuals(i)
    }
    residualProduct / Math.sqrt(stats1.sumDeviationSquared * stats2.sumDeviationSquared)
  }

  def coefficientDetermination(vector1: DblVector, vector2: DblVector): Double = {
    Math.pow(correlation(vector1, vector2), 2)
  }

  def xysum(x: DblVector, y: DblVector): Double = {
    var xysumRet = 0.0
    for (i <- 0 to x.size - 1) {
      xysumRet += (x(i) * y(i))
    }
    xysumRet
  }
}