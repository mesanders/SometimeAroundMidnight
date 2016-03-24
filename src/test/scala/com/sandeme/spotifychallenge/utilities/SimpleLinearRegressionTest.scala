package com.sandeme.spotifychallenge.utilities

import com.sandeme.spotifychallenge.utilities.Utility.DblVector
import org.scalatest.FunSuite

/**
  * Created by sandeme on 3/22/16.
  */
class SimpleLinearRegressionTest extends FunSuite{
  val x: DblVector = Array(4.1,	6.5,	12.6,	25.5,	29.8,	38.6,	46,	52.8,	59.6,	66.3,	74.7)
  val y: DblVector = Array(2.2, 4.5,	10.4,	23.1,	27.9,	36.8,	44.3,	50.7,	57.5,	64.1,	72.6)
  val statsX = new Stats(x)
  val statsY = new Stats(y)
  val mval = .99999
  val yInterceptVal  = -2.0069
  val rSquareval = 0.99999

  test("Testing Simple Linear Regression. Testing common ") {
    val xsum = 417.5
    val ysum = 395.1
    val xysum = 20826
    val xxsum = 21679
    val yysum = 20019
    val n = 11


    assert(Math.abs(statsY.sum) - Math.abs(ysum) <= ysum)
    assert(Math.abs(Utility.xysum(x, y)) - Math.abs(xysum) <= 0.0)
    assert(Math.abs(statsX.sumSqr) - Math.abs(xxsum) <= 0.0)
    assert(Math.abs(statsY.sumSqr) - Math.abs(yysum) <= 0.0)
  }

  test("SimpleLinearRegression.getSlope verify the m value is calculated correctly") {
    val m = SimpleLinearRegression.getSlope(y, x)
    assert(Math.abs(m) - Math.abs(mval ) <=0.0)
  }

  test("SimpleLinearRegression.getIntercept - verify that yIntercept is calculated correctly.") {
    val yIntercept = SimpleLinearRegression.getYIntercept(y, x)
    assert(Math.abs(yIntercept) - Math.abs(yInterceptVal) <= 0.0)
  }

  test("SimpleLinearRegression.rSquare. Verify that the rsquare is calculated correctly") {
    assert(Math.abs(SimpleLinearRegression.rSquare(x, y)) - Math.abs(rSquareval) <= 0.0)
  }

  test("SimpleLinearRegression, verify regression returns the result correctly") {
    val result = SimpleLinearRegression.apply(y, x)
    assert(Math.abs(result.m) - Math.abs(rSquareval) <= 0.0)
    assert(Math.abs(result.yIntercept) - Math.abs(yInterceptVal) <= 0.0)
    assert(Math.abs(result.rSquare) - Math.abs(rSquareval) <= 0.0)
  }
}
