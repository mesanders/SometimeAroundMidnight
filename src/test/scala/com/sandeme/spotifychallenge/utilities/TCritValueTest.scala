package com.sandeme.spotifychallenge.utilities

import org.scalatest.{FunSuite, Suite}
/**
  * Created by sandeme on 3/5/16.
  */

class TestTCritValueSuite  extends FunSuite  {

  test("TCritValues have 101 entries for degrees of freedom") {
    assert(TCritValue.alpha100.size == 101)
    assert(TCritValue.alpha005.size == 101)
    assert(TCritValue.alpha010.size == 101)
    assert(TCritValue.alpha025.size == 101)
    assert(TCritValue.alpha050.size == 101)
    assert(TCritValue.alpha001.size == 101)
  }

  test("TCritValues test based on table found: https://en.wikipedia.org/wiki/Student%27s_t-distribution") {
    assert(TCritValue.getTCritValue(.1, 10) == 1.372)
    assert(TCritValue.getTCritValue(.1, 10) != 5.372)
    assert(TCritValue.getTCritValue(0.025, 28) == 2.048)
    assert(TCritValue.getTCritValue(.001, 38) == 3.319)
    assert(TCritValue.getTCritValue(.05, 150) == 1.645)
  }
}
