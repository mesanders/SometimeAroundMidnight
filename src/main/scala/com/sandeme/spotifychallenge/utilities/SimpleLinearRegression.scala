package com.sandeme.spotifychallenge.utilities

import com.sandeme.spotifychallenge.utilities.Utility.DblVector
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.XmlVisitor.TextPredictor

/**
  * Created by sandeme on 3/22/16.
  *
  * Least Squares estimator of a linear regression model with a single explanatory variable. Suppose there are n data points, the functions
  * describes x and y is yi = alpha + Bxi + ei. Where the goal
 *
  * @see <a link="https://en.wikipedia.org/wiki/Simple_linear_regression">https://en.wikipedia.org/wiki/Simple_linear_regression</a>
  */
object SimpleLinearRegression {
  // The best form of our line is slope-intercept form, which looks like
  // y = mx + b; Therefore it is only necessary to compute m and b to determine the bes fit line.
  // m = ((N * xysum) - (xSum*ySum)) /  ((N * xxsum) - (xsum*xsum))
  // b = ((xxsum*ysum)-(xsum*xysum))/((N*xxsum)-(xsum*xsum))

  case class RegressionResult(val m: Double, val yIntercept: Double, val rSquare: Double, val message: String)
  /**
    * Take in two vectors, the Response and the Predictor. This is also known as m. The response is the
    * Y vector. Prector is the X vector. We are interested in getting the intercept from the two vectors
    * @param response is the y vector
    * @param predictor is the variable we are trying to predict future Y  or infer values on
    * @return the intercept of the slope
    */
  def getSlope(response: DblVector, predictor:DblVector): Double = {
    val statsResponse = new Stats(response)
    val statsPreditor = new Stats(predictor)

    val n = statsPreditor.size
    val xysum =  Utility.xysum(response, predictor)
    val xsumysum = statsPreditor.sum * statsResponse.sum
    val num = (n * xysum) - (statsResponse.sum * statsPreditor.sum)
    val den = (n * statsPreditor.sumSqr) - (statsPreditor.sum * statsPreditor.sum)
    num / den
  }

  /**
    * Take in two vectors, the Response and the Predictor. The response is the
    * Y vector. Prector is the X vector. We are interested in getting the slope for X from the two vectors
    * @param response is the y vector
    * @param predictor is the variable we are trying to predict future Y  or infer values on
    * @return slope for the X value
    */
  def getYIntercept(response: DblVector, predictor: DblVector): Double = {
    val statsResponse = new Stats(response)
    val statsPreditor = new Stats(predictor)
    val xsumysum = Utility.xysum(response, predictor)
    val n = statsPreditor.size

    val num: Double = (statsPreditor.sumSqr * statsResponse.sum) - (statsPreditor.sum * xsumysum)
    val den: Double = (n * statsPreditor.sumSqr) - (statsPreditor.sum * statsPreditor.sum)
    num / den
  }

  /**
    * Returning rSqure from the two vectors to see if there is a correlation
    * @param response
    * @param predictor
    * @return
    */
  def rSquare(response: DblVector, predictor:DblVector): Double = {
    Utility.coefficientDetermination(predictor, response)
  }

  def apply(response: DblVector, predictor: DblVector): RegressionResult = {
    val m = getSlope(response, predictor)
    val yIntercept = getYIntercept(response, predictor)
    val rsquare = rSquare(response, predictor)
    val message = s"y = ${m}X\u2081 + ${yIntercept}"
    new RegressionResult(m, yIntercept, rsquare, message)
  }
}
