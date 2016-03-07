package com.sandeme.spotifychallenge.utilities

import com.sandeme.spotifychallenge.utilities.Utility._
import org.scalatest.{FunSuite, Suite}
/**
  * Created by sandeme on 3/5/16.
  */

class StudentTTestTest  extends FunSuite  {

  test("TCritValues have 101 entries for degrees of freedom") {
    assert(StudentTTest.alpha100.size == 101)
    assert(StudentTTest.alpha005.size == 101)
    assert(StudentTTest.alpha010.size == 101)
    assert(StudentTTest.alpha025.size == 101)
    assert(StudentTTest.alpha050.size == 101)
    assert(StudentTTest.alpha001.size == 101)
  }

  test("TCritValues test based on table found: https://en.wikipedia.org/wiki/Student%27s_t-distribution") {
    assert(StudentTTest.getTCritValue(.1, 10) == 1.372)
    assert(StudentTTest.getTCritValue(.1, 10) != 5.372)
    assert(StudentTTest.getTCritValue(0.025, 28) == 2.048)
    assert(StudentTTest.getTCritValue(.001, 38) == 3.319)
    assert(StudentTTest.getTCritValue(.05, 150) == 1.645)
  }

  test("TCritValues verify get degrees of freedom formula works correctly:") {
    val vector1: DblVector = Array(15.0, 13.0, 15.5, 18.8, 25.3, 32.2, 22.1, 18.5, 14.5, 13.2,11.0)
    val vector2: DblVector = Array(8.0, 12.0, 9.5, 12.8, 22.3, 17.2, 12.1, 38.5, 44.5, 23.2,21.0)
    val stats1 = new Stats(vector1)
    val stats2 = new Stats(vector2)
    // It's really 15.25, but we round so it's 15, verified on an online calculator
    assert(StudentTTest.getDegreesFreedom(stats1, stats2) == 15)
    assert(StudentTTest.studentTwoTailedTTest(stats1, stats2, .050).criticalValues == (2.131, -2.131))
  }
}
