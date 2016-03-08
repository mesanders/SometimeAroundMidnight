package com.sandeme.spotifychallenge.utilities

import com.sandeme.spotifychallenge.utilities.Utility.{DblVector}
import org.scalatest.FunSuite

/**
  * Created by sandeme on 3/6/16.
  */
class UtilityTest extends FunSuite {
  val vector: DblVector = Array(15.0, 13.0, 15.5, 18.8, 15.5,33.2,21.8,44.6,11.4,13.4)
  val vector2: DblVector = Array(29, 20, 28, 34, 22.5, 33.5, 22.5, 12.4, 55.4, 33.0)
  val vector3: DblVector = Array(0,0,1,1,0,1,1,1,0,0)
  val vectorStats = new Stats(vector)

  test("Testing Vector and Stats Class") {
    assert(vectorStats.min == 11.4)
    assert((vectorStats.stdDev - 10.6229).abs < 0.001 )
  }

  test("Verify that the data can be normalized. Normalized array is useful for probablities. May not need it for this analysis.") {
    val normalized = vectorStats.normalize
    assert(normalized.max == 1.0)
    assert(normalized.min == 0.0)
  }

  test("See if two vectors are correlated: verified the results on http://www.socscistatistics.com/tests/pearson/Default2.aspx" ) {
    val rValueVectorVector3 = Utility.correlation(vector, vector3)
    assert(Utility.correlation(vector, vector2).abs - 0.474072 >= 0)
    assert(rValueVectorVector3.abs - 0.6509371712829453 >= 0.0)
    assert(Utility.coefficientDetermination(vector, vector3).abs - 0.42 >= 0.0)
    assert(StudentTTest.getTForPearson(rValueVectorVector3, vector.size).abs - 2.425 >= 0.0)
    assert(StudentTTest.tTestForPearsonCorrelation(rValueVectorVector3, vector.size, 0.050).reject == true)
  }
}
