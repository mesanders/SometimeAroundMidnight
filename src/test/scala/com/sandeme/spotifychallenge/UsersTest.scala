package com.sandeme.spotifychallenge

import com.sandeme.spotifychallenge.utilities.Stats
import org.scalatest.FunSuite

import scala.collection.mutable
import com.sandeme.spotifychallenge.utilities.Utility.DblVector
/**
  * Created by sandeme on 3/5/16.
  */
class UsersTest  extends FunSuite  {
  test("Convert Age Bucket to Int and back") {
    assert(User.ageStringToInt("18 - 24") == 2)
    assert(User.ageStringToInt("55+") == 7)
    assert(User.ageRangeToString(6).equals("45 - 54"))
    assert(User.ageRangeToString(-1).equals(""))
    assert(User.ageRangeToString(1).equals("0 - 17"))
    assert(User.ageStringToInt("25 - 29") == 3)
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

  test("Verify that songs get mapped to the correct Users") {
    val users: mutable.Map[String, User] = mutable.Map("47cba57eef554e7fa85442464e2c2512" -> User.convertCsvUser("female,0 - 17,AR,0,47cba57eef554e7fa85442464e2c2512"),
      "61f88d6fd67a448daf5871b97bac0b10" -> User.convertCsvUser("female,0 - 17,AR,0,61f88d6fd67a448daf5871b97bac0b10"),
      "93ddb63355ca46f1aa051e4ea91469ff" -> User.convertCsvUser("female,0 - 17,AR,0,93ddb63355ca46f1aa051e4ea91469ff"),
      "c48fc748b2164b23a96eeff365b0bcab" -> User.convertCsvUser("female,0 - 17,AR,0,c48fc748b2164b23a96eeff365b0bcab"))

    val songs: Array[SongRecord] = Array(SongRecord.convertCsvToSongRecord("0,album,02e4bba99bf04ac585f2287106ce5af5,premium,1444843436.29,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("0,playlist,429b93942afc467486a25b74e7306e28,premium,1444352515.89,61f88d6fd67a448daf5871b97bac0b10"),
      SongRecord.convertCsvToSongRecord("10774,search,4d1702f2d96742de9553c01bb0da1a09,premium,1444624922.33,93ddb63355ca46f1aa051e4ea91469ff"),
      SongRecord.convertCsvToSongRecord("0,collection,a4863e7e1c8b44d69f275284361ea2d6,open,1444566760.65,c48fc748b2164b23a96eeff365b0bcab"),
      SongRecord.convertCsvToSongRecord("5712,collection,b40c241ebbb245d4b1e5058e350f5cc4,open,1444567277.71,c48fc748b2164b23a96eeff365b0bcab"))

    val mappedUsers: Map[String, User] = User.addSongRecordsToUsers(songs, users)
    assert(mappedUsers("c48fc748b2164b23a96eeff365b0bcab").songs.size == 2)
    assert(mappedUsers("61f88d6fd67a448daf5871b97bac0b10").songs.size == 1)
    assert(mappedUsers("47cba57eef554e7fa85442464e2c2512").songs.size == 1)
    val femaleNumListensStats = User.getStatsForTrackCountsByGender('f', mappedUsers)
    assert(femaleNumListensStats.mean == 1.25)
    assert(femaleNumListensStats.size == 4)
  }
}
