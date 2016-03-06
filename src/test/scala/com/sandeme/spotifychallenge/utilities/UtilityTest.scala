package com.sandeme.spotifychallenge.utilities

import com.sandeme.spotifychallenge.utilities.Utility.{DblVector}
import org.scalatest.FunSuite

/**
  * Created by sandeme on 3/6/16.
  */
class UtilityTest extends FunSuite {
  val vector: DblVector = Array(15.0, 13.0, 15.5, 18.8)
  val vectorStats = new Stats(vector)

  test("Testing Vector and Stats Class") {
    assert(vectorStats.min == 13.0)
    assert((vectorStats.stdDev - 2.406).abs < 0.001 )
  }

  test("Verify that the data can be normalized. Normalized array is useful for probablities. May not need it for this analysis.") {
    val normalized = vectorStats.normalize
    assert(normalized.max == 1.0)
    assert(normalized.min == 0.0)
  }
}
