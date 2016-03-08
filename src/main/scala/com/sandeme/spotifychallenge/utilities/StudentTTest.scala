package com.sandeme.spotifychallenge.utilities

/**
  * Created by sandeme on 3/5/16.
  * @see <a link="https://en.wikipedia.org/wiki/Student%27s_t-distribution">https://en.wikipedia.org/wiki/Student%27s_t-distribution</a>
  *     Ther table above is an example of where the results came from. Could have tried to implement the formula above; however,
  *     Time constraints and all, also my brain would be mush afterwards. This is a straightforward implementation
  */
object StudentTTest {

  /**
    * The following Arrays are used to store the T Critical value based on the various degrees of freedom
    * A critical value is used in significance testing. It is the value that a test statistic must exceed
    * in order for the the null hypothesis to be rejected.
    */
  def alpha100 = Array(3.078, 1.886, 1.638, 1.533, 1.476, 1.440, 1.415, 1.397, 1.383, 1.372, 1.363, 1.356, 1.350, 1.345, 1.341, 1.337, 1.333, 1.330, 1.328, 1.325, 1.323, 1.321, 1.319, 1.318, 1.316, 1.315, 1.314, 1.313, 1.311, 1.310, 1.309, 1.309, 1.308, 1.307, 1.306, 1.306, 1.305, 1.304, 1.304, 1.303, 1.303, 1.302, 1.302, 1.301, 1.301, 1.300, 1.300, 1.299, 1.299, 1.299, 1.298, 1.298, 1.298, 1.297, 1.297, 1.297, 1.297, 1.296, 1.296, 1.296, 1.296, 1.295, 1.295, 1.295, 1.295, 1.295, 1.294, 1.294, 1.294, 1.294, 1.294, 1.293, 1.293, 1.293, 1.293, 1.293, 1.293, 1.292, 1.292, 1.292, 1.292, 1.292, 1.292, 1.292, 1.292, 1.291, 1.291, 1.291, 1.291, 1.291, 1.291, 1.291, 1.291, 1.291, 1.291, 1.290, 1.290, 1.290, 1.290, 1.290, 1.282)
  def alpha050 = Array(6.314, 2.920, 2.353, 2.132, 2.015, 1.943, 1.895, 1.860, 1.833, 1.812, 1.796, 1.782, 1.771, 1.761, 1.753, 1.746, 1.740, 1.734, 1.729, 1.725, 1.721, 1.717, 1.714, 1.711, 1.708, 1.706, 1.703, 1.701, 1.699, 1.697, 1.696, 1.694, 1.692, 1.691, 1.690, 1.688, 1.687, 1.686, 1.685, 1.684, 1.683, 1.682, 1.681, 1.680, 1.679, 1.679, 1.678, 1.677, 1.677, 1.676, 1.675, 1.675, 1.674, 1.674, 1.673, 1.673, 1.672, 1.672, 1.671, 1.671, 1.670, 1.670, 1.669, 1.669, 1.669, 1.668, 1.668, 1.668, 1.667, 1.667, 1.667, 1.666, 1.666, 1.666, 1.665, 1.665, 1.665, 1.665, 1.664, 1.664, 1.664, 1.664, 1.663, 1.663, 1.663, 1.663, 1.663, 1.662, 1.662, 1.662, 1.662, 1.662, 1.661, 1.661, 1.661, 1.661, 1.661, 1.661, 1.660, 1.660, 1.645)
  def alpha025 = Array(12.706, 4.303, 3.182, 2.776, 2.571, 2.447, 2.365, 2.306, 2.262, 2.228, 2.201, 2.179, 2.160, 2.145, 2.131, 2.120, 2.110, 2.101, 2.093, 2.086, 2.080, 2.074, 2.069, 2.064, 2.060, 2.056, 2.052, 2.048, 2.045, 2.042, 2.040, 2.037, 2.035, 2.032, 2.030, 2.028, 2.026, 2.024, 2.023, 2.021, 2.020, 2.018, 2.017, 2.015, 2.014, 2.013, 2.012, 2.011, 2.010, 2.009, 2.008, 2.007, 2.006, 2.005, 2.004, 2.003, 2.002, 2.002, 2.001, 2.000, 2.000, 1.999, 1.998, 1.998, 1.997, 1.997, 1.996, 1.995, 1.995, 1.994, 1.994, 1.993, 1.993, 1.993, 1.992, 1.992, 1.991, 1.991, 1.990, 1.990, 1.990, 1.989, 1.989, 1.989, 1.988, 1.988, 1.988, 1.987, 1.987, 1.987, 1.986, 1.986, 1.986, 1.986, 1.985, 1.985, 1.985, 1.984, 1.984, 1.984, 1.960)
  def alpha010 = Array(31.821, 6.965, 4.541, 3.747, 3.365, 3.143, 2.998, 2.896, 2.821, 2.764, 2.718, 2.681, 2.650, 2.624, 2.602, 2.583, 2.567, 2.552, 2.539, 2.528, 2.518, 2.508, 2.500, 2.492, 2.485, 2.479, 2.473, 2.467, 2.462, 2.457, 2.453, 2.449, 2.445, 2.441, 2.438, 2.434, 2.431, 2.429, 2.426, 2.423, 2.421, 2.418, 2.416, 2.414, 2.412, 2.410, 2.408, 2.407, 2.405, 2.403, 2.402, 2.400, 2.399, 2.397, 2.396, 2.395, 2.394, 2.392, 2.391, 2.390, 2.389, 2.388, 2.387, 2.386, 2.385, 2.384, 2.383, 2.382, 2.382, 2.381, 2.380, 2.379, 2.379, 2.378, 2.377, 2.376, 2.376, 2.375, 2.374, 2.374, 2.373, 2.373, 2.372, 2.372, 2.371, 2.370, 2.370, 2.369, 2.369, 2.368, 2.368, 2.368, 2.367, 2.367, 2.366, 2.366, 2.365, 2.365, 2.365, 2.364, 2.326)
  def alpha005 = Array(63.657, 9.925, 5.841, 4.604, 4.032, 3.707, 3.499, 3.355, 3.250, 3.169, 3.106, 3.055, 3.012, 2.977, 2.947, 2.921, 2.898, 2.878, 2.861, 2.845, 2.831, 2.819, 2.807, 2.797, 2.787, 2.779, 2.771, 2.763, 2.756, 2.750, 2.744, 2.738, 2.733, 2.728, 2.724, 2.719, 2.715, 2.712, 2.708, 2.704, 2.701, 2.698, 2.695, 2.692, 2.690, 2.687, 2.685, 2.682, 2.680, 2.678, 2.676, 2.674, 2.672, 2.670, 2.668, 2.667, 2.665, 2.663, 2.662, 2.660, 2.659, 2.657, 2.656, 2.655, 2.654, 2.652, 2.651, 2.650, 2.649, 2.648, 2.647, 2.646, 2.645, 2.644, 2.643, 2.642, 2.641, 2.640, 2.640, 2.639, 2.638, 2.637, 2.636, 2.636, 2.635, 2.634, 2.634, 2.633, 2.632, 2.632, 2.631, 2.630, 2.630, 2.629, 2.629, 2.628, 2.627, 2.627, 2.626, 2.626, 2.576)
  def alpha001 = Array(318.313, 22.327, 10.215, 7.173, 5.893, 5.208, 4.782, 4.499, 4.296, 4.143, 4.024, 3.929, 3.852, 3.787, 3.733, 3.686, 3.646, 3.610, 3.579, 3.552, 3.527, 3.505, 3.485, 3.467, 3.450, 3.435, 3.421, 3.408, 3.396, 3.385, 3.375, 3.365, 3.356, 3.348, 3.340, 3.333, 3.326, 3.319, 3.313, 3.307, 3.301, 3.296, 3.291, 3.286, 3.281, 3.277, 3.273, 3.269, 3.265, 3.261, 3.258, 3.255, 3.251, 3.248, 3.245, 3.242, 3.239, 3.237, 3.234, 3.232, 3.229, 3.227, 3.225, 3.223, 3.220, 3.218, 3.216, 3.214, 3.213, 3.211, 3.209, 3.207, 3.206, 3.204, 3.202, 3.201, 3.199, 3.198, 3.197, 3.195, 3.194, 3.193, 3.191, 3.190, 3.189, 3.188, 3.187, 3.185, 3.184, 3.183, 3.182, 3.181, 3.180, 3.179, 3.178, 3.177, 3.176, 3.175, 3.175, 3.174, 3.090)

  def getTForPearson(rValue: Double, sampleSize: Int):  Double =  {
    rValue / Math.sqrt((1 - Math.pow(rValue, 2)) / (sampleSize - 2))
  }

  /**
    * Takes in rValue, sample size and alpha value and returns a TTestResult Object
    * @param rValue - rValue Result from a Pearson Coorelation
    * @param sampleSize - size of the sample
    * @param alpha - alpha value. tThe default is .05 /2 for a two tailed test
    * @return @see TTestResult
    */
  def tTestForPearsonCorrelation(rValue: Double, sampleSize: Int, alpha: Double = 0.050 / 2): TTestResult = {
    val tValue = getTForPearson(rValue, sampleSize)
    val critValue = getTCritValue(alpha, sampleSize - 2)
    val rSquared = Math.pow(rValue, 2)
    val reject = if (tValue >= critValue || tValue <= (-1 * critValue)) true else false
    var message = if (reject) "Reject the null hypothesis. There is a statistically significant correlation between the two variables."
    else "Do not Reject the null Hypothesis that there is not a statistically significant correlation between two variables"
      message = message + s". t value: ${tValue}\tT Crit Value: ${critValue} and " + (critValue * -1)
    if (rValue >= 0.0 && rValue >= 0.5) message = message + "\n There is a strong correlation between the two variables. When one increases the other increases."
    else if (rValue >= 0.0) message = message + "\nThere is a weak correlation between the two variables. When one increases the other increases."
    else if (rValue <= 0.0 && rValue <= -0.5) message = message + "\nThere is a weak negative correlation between the two variables. When one increases the other decreases"
    else {message = message + s"There is a Strong Negative correlation. When one increases the other decreases."}
    message = message + s"\nr: ${rValue} \t r\u00B2: ${rSquared}."
    return new TTestResult(message, tValue, (critValue, -1 * critValue), reject)
  }

  /**
    * Takes in an alpha value that is either .1, .05, .025, .01, .005, or .001 and and does a lookup based on the number
    * of degrees of freedom. Could have implemented the complicated Math function to calculate the Critical value, but
    * That would take more time than was available.
    * @param alpha Value to compute
    * @param degreesFreedom The number in the sample minus 1.
    * @return A double of the t-critical value
    */
  def getTCritValue(alpha:Double, degreesFreedom: Int): Double = {
    if (degreesFreedom < 0 ) { throw new Exception("Error degrees of freedom should be greater than 0") }

    val alphaArray = alpha match {
      case.10 => alpha100
      case.05 => alpha050
      case.025 => alpha025
      case.010 => alpha010
      case.005 => alpha005
      case.001 => alpha001
      case _ => throw new Exception("Error, alpha value not in TCritValue class table.")
    }


    if (degreesFreedom < 100) {
      return alphaArray(degreesFreedom - 1)
    } else {
      alphaArray(100)
    }
  }

  /**
    * Conducts Two Tailed T Test The formula is based on formula from the link below.
    * @see <a link="https://en.wikipedia.org/wiki/Student%27s_t-distribution">https://en.wikipedia.org/wiki/Student%27s_t-distribution</a>
    * @param stats1 @see com.sandeme.spotifychallenge.utlities.Stats
    * @param stats2 @see com.sandeme.spotifychallenge.utlities.Stats
    * @param alpha : This value will be cut in half, so if it's .05, it will be .025 for a two tailed test
    * @return TTestResult, a case class with the result from the test
    */
  def studentTwoTailedTTest(stats1: Stats[Double], stats2: Stats[Double], alpha: Double = 0.050): TTestResult = {
    if (alpha < .025   || alpha > .10) throw new Exception("Exception thrown, the alpha value was either too low or too high: Between .001 and .1")
    val meanDifferences = stats1.mean - stats2.mean
    val pooledVariance = Math.sqrt((stats1.variance / stats1.size) + (stats2.variance / stats2.size))
    val tValue = meanDifferences / pooledVariance
    val df = getDegreesFreedom(stats1, stats2)
    val critValue = getTCritValue(alpha /2.0 , df)
    val reject = if (tValue >= critValue || tValue <= (-1 * critValue)) true else false
    var message = if (reject) "Reject the null hypothesis that the the difference between the two means are statistically significant"
      else "Do not Reject the null Hypothesis the difference between the two means are not statistically significant"
    message = message + s". t value: ${tValue}\tT Crit Value: ${critValue} and " + (critValue * -1)
    return new TTestResult(message, tValue, (critValue, -1 * critValue), reject)
  }

  /**
    * Calculates the degrees of freedom for two means of independent samples. Need to do some fancy Math instead
    * of just taking the average like I originally though.
    * @param stats1
    * @param stats2
    * @return
    */
  def getDegreesFreedom(stats1: Stats[Double], stats2: Stats[Double]): Int = {
    val num = Math.pow(stats1.varOverSampleSize + stats2.varOverSampleSize ,2)
    val demLeft = stats1.inverseSize * Math.pow(stats1.varOverSampleSize,2)
    val demRight = stats2.inverseSize * Math.pow(stats2.varOverSampleSize ,2)
    val den = demLeft + demRight
    (num / den).toInt
  }

  case class TTestResult(var message: String, var tvalue: Double, var criticalValues:(Double, Double), var reject: Boolean)
}
