package com.sandeme.spotifychallenge.utilities

import com.sandeme.spotifychallenge.utilities.Utility.{DblVector, DVector}

/**
  * Created by sandeme on 3/5/16.
  */
class Stats[T <% Double](private var values: DVector[T]) {
  class _Stats(var minValue: Double, var maxValue: Double, var sum: Double, var sumSqr: Double)

  val stats = {
    val _stats = new _Stats(Double.MaxValue, Double.MinValue, 0.0, 0.0)

    values.foreach(x => {
      if (x < _stats.minValue) x else _stats.minValue
      if (x > _stats.maxValue) x else _stats.maxValue
      _stats.sum + x
      _stats.sumSqr + x*x
    })

    _stats
  }

  /**
    * Inverted Square root of 2 * PI is used as the densitiy function of
    * the Gaussian Distribution formula. 1/sqrt(2*PI) in general is used as a
    * density function of the normal distribution
    */
  lazy val INV_SQRT_2PI = 1 / (Math.sqrt(2 * Math.PI))
  lazy val mean = stats.sum/values.size
  lazy val variance = (stats.sumSqr - mean * mean * values.size)/ (values.size - 1)
  lazy val stdDev = if(variance < 0) 0 else Math.sqrt(variance)
  lazy val min = stats.minValue
  lazy val max = stats.maxValue

  /**
    * Statistics are usually used to normalize data into probability value [0, 1] as required by
    * most classification or clustering algorithms. It is logical to add up the normalization method
    * to the State clas, as we have already extracted the min and max values
    */
  def normalize: DblVector = {
    val range = max - min
    values.map(x => (x - min)/range)
  }

  /**
    * The same approach is used to compute the multivariate normal distribution
    * @see <a href="http://mathworld.wolfram.com/GaussianFunction.html">http://mathworld.wolfram.com/GaussianFunction.html</a>
    */
  def gauss: DblVector = {
    values.map(x => {
      val y  = x - mean
      INV_SQRT_2PI / stdDev * Math.exp(-0.5 * y * y / stdDev)
    })
  }
}

