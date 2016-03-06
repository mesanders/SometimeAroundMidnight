package com.sandeme.spotifychallenge

import org.scalatest.FunSuite

/**
  * Created by sandeme on 3/5/16.
  */
class UsersTest  extends FunSuite  {

  test("Verify that conversion from CSV Line into a User object is successful.") {
    val user = User.convertCsvUser("female,0 - 17,AR,0,61f88d6fd67a448daf5871b97bac0b10")
    assert(user.gender == 'f')
    assert(user.ageRange == 1)
    assert(user.country == "AR")
    assert(user.accountAgeWeeks == 0)
    assert(user.userId == "61f88d6fd67a448daf5871b97bac0b10")
    assert(user.toString == "female,0 - 17,AR,0,61f88d6fd67a448daf5871b97bac0b10")
  }
}
