package com.sandeme.spotifychallenge

import org.scalatest.FunSuite

import scala.collection.mutable

/**
  * Created by sandeme on 3/5/16.
  */
class UsersTest  extends FunSuite  {
  test("Convert Age Bucket to Int and back") {
    assert(User.ageStringToInt("18 - 24") == 2)
    assert(User.ageStringToInt("55+") == 7)
    assert(User.ageRangeToString(6) == "45 - 55")
    assert(User.ageRangeToString(-1) == "")
    assert(User.ageStringToInt("HELLO Mike, I see you there, looking at me") == 0)
  }

  test("Verify that conversion from CSV Line into a User object is successful.") {
    val user = User.convertCsvUser("female,0 - 17,AR,0,61f88d6fd67a448daf5871b97bac0b10")
    assert(user.gender == 'f')
    assert(user.ageRange == 1)
    assert(user.country == "AR")
    assert(user.accountAgeWeeks == 0)
    assert(user.userId == "61f88d6fd67a448daf5871b97bac0b10")
    assert(user.toString == "female,0 - 17,AR,0,61f88d6fd67a448daf5871b97bac0b10")
  }

  test("Verify integration test to load the test file that was provided.") {
    val users: mutable.Map[String, User] = User.loadFromFile("data/user_data_sample.csv", User.convertCsvUser)
    assert(users.size == 9565)
    assert(users.filter(_._2.gender == 'f').size == 4560)
  }
}
