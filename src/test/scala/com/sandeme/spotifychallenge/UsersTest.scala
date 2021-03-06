package com.sandeme.spotifychallenge

import java.text.SimpleDateFormat
import java.util.Date

import com.sandeme.spotifychallenge.utilities.Stats
import org.scalatest.FunSuite

import scala.collection.mutable
import com.sandeme.spotifychallenge.utilities.Utility.DblVector
/**
  * Created by sandeme on 3/5/16.
  */
class UsersTest  extends FunSuite  {
  val users:  Map[String, User] = Map("47cba57eef554e7fa85442464e2c2512" -> User.convertCsvUser("female,0 - 17,AR,0,47cba57eef554e7fa85442464e2c2512"),
    "61f88d6fd67a448daf5871b97bac0b10" -> User.convertCsvUser("female,0 - 17,AR,0,61f88d6fd67a448daf5871b97bac0b10"),
    "93ddb63355ca46f1aa051e4ea91469ff" -> User.convertCsvUser("female,0 - 17,AR,0,93ddb63355ca46f1aa051e4ea91469ff"),
    "c48fc748b2164b23a96eeff365b0bcab" -> User.convertCsvUser("female,0 - 17,AR,0,c48fc748b2164b23a96eeff365b0bcab"))

  val songs: Array[SongRecord] = Array(SongRecord.convertCsvToSongRecord("0,album,02e4bba99bf04ac585f2287106ce5af5,premium,1444843436.29,47cba57eef554e7fa85442464e2c2512"),
    SongRecord.convertCsvToSongRecord("0,playlist,429b93942afc467486a25b74e7306e28,premium,1444352515.89,61f88d6fd67a448daf5871b97bac0b10"),
    SongRecord.convertCsvToSongRecord("10774,search,4d1702f2d96742de9553c01bb0da1a09,premium,1444624922.33,93ddb63355ca46f1aa051e4ea91469ff"),
    SongRecord.convertCsvToSongRecord("0,collection,a4863e7e1c8b44d69f275284361ea2d6,open,1444566760.65,c48fc748b2164b23a96eeff365b0bcab"),
    SongRecord.convertCsvToSongRecord("5712,collection,b40c241ebbb245d4b1e5058e350f5cc4,open,1444567277.71,c48fc748b2164b23a96eeff365b0bcab"))


/*
  test("TESTING")  {
    val users = User.addSongRecordsToUsers(
      SongRecord.loadSongRecords("data/end_song_sample.csv"), User.loadFromFile("data/user_data_sample.csv"))
    User.generateSessions(users)
    val writer = new java.io.PrintWriter(new java.io.File("test.txt" ))
    writer.write("ms_played,context,track_id,product,end_timestamp,user_id,session_id\n")
    users.foreach(_._2.songs.foreach(song => writer.write(song.toString + "\n")))
    writer.close
  }
*/

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
    val users: Map[String, User] = User.loadFromFile("data/user_data_sample.csv", User.convertCsvUser)
    assert(users.size == 9565)
    assert(users.filter(_._2.gender == 'f').size == 4560)
  }

  test("Verify that songs get mapped to the correct Users") {
    val mappedUsers: Map[String, User] = User.addSongRecordsToUsers(songs, users)
    assert(mappedUsers("c48fc748b2164b23a96eeff365b0bcab").songs.size == 2)
    assert(mappedUsers("61f88d6fd67a448daf5871b97bac0b10").songs.size == 1)
    assert(mappedUsers("47cba57eef554e7fa85442464e2c2512").songs.size == 1)
    val femaleNumListensStats = User.getStatsForTrackCountsByGender('f', mappedUsers)
    assert(femaleNumListensStats.mean == 1.25)
    assert(femaleNumListensStats.size == 4)
  }

  test("User.getStatsForAvgListen test") {
    val mutableMap = scala.collection.immutable.Map() ++ users
    val stats = User.getStatsForAvgListenByGender('f', mutableMap)
    assert(stats.max == 10774.0)
    assert(stats.min == 0.0)
  }

  // Uses over 300 SongRecords to test the class. Needed to get a bunch of different timestmaps in order to test a good sessionID generator.
  test("Test generating Sessions asserting that there are approx 31 sessions in this group based on the setting of 15 minute max between times") {
    val songs = Array(SongRecord.convertCsvToSongRecord("0,album,02e4bba99bf04ac585f2287106ce5af5,premium,1444843436.29,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("0,album,21e026c340144d569aa1cfb39c0f66c9,premium,1444843315.46,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("0,album,2e685d0633204bbea0b17e874840c960,premium,1444606298.02,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("0,album,8df985a0435640958897d4bed6b0a349,premium,1444843315.45,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("0,album,cb77dea36ef64492ad5e53fe9aef2028,premium,1444710240.52,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("0,collection,1513df35bdbd47f2b3fd219c610ee6c3,premium,1444415527.45,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("0,collection,35709095f4f54abab5a55f8f882ac546,premium,1444785431.1,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("0,collection,6974b5b57f4d4dc08ebf40973616110c,premium,1444416561.79,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("0,collection,e66edc515c3343889e253ebb2f788610,premium,1444845645.85,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("0,collection,e66edc515c3343889e253ebb2f788610,premium,1444845646.51,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("0,collection,e66edc515c3343889e253ebb2f788610,premium,1444845646.52,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("0,collection,e66edc515c3343889e253ebb2f788610,premium,1444845646.6,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("0,collection,e66edc515c3343889e253ebb2f788610,premium,1444845646.84,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("0,collection,e66edc515c3343889e253ebb2f788610,premium,1444845646.85,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("0,collection,f18f1ebe913a4c2fbd424193ad5cf05e,premium,1444417061.78,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("10063,album,c9a5e4cb097446a082aa44426a02ac46,premium,1444844191.56,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("10515,artist,6974b5b57f4d4dc08ebf40973616110c,premium,1444504556.74,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("10541,collection,f18f1ebe913a4c2fbd424193ad5cf05e,premium,1444415493.28,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("1068,album,ba90fe61b1624ecfb7924a96fe0e33d7,premium,1444843967.06,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("10913,album,2e685d0633204bbea0b17e874840c960,premium,1444842714.49,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("1114,collection,f18f1ebe913a4c2fbd424193ad5cf05e,premium,1444417060.71,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("11238,album,d2b1905ac7f845449f833140eaf3315f,premium,1444606293.97,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("11568,album,18649198a57b47ef828b1143da015208,premium,1444758213.41,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("1160,collection,f18f1ebe913a4c2fbd424193ad5cf05e,premium,1444417511.58,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("116192,album,a2b98a935eff49eda6bbb345466d52ab,premium,1444784759.8,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("119500,album,a2b98a935eff49eda6bbb345466d52ab,premium,1444757387.52,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("120211,album,f1220451b3f44c1e8f8e9a629f0f3009,premium,1444594442.16,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("12027,collection,f18f1ebe913a4c2fbd424193ad5cf05e,premium,1444415517.75,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("12167,collection,f18f1ebe913a4c2fbd424193ad5cf05e,premium,1444435348.68,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("124830,album,a2b98a935eff49eda6bbb345466d52ab,premium,1444708384.89,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("12538,collection,4ba37c5adbe34a2989804fbfc618dd50,premium,1444785342.95,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("12538,collection,f18f1ebe913a4c2fbd424193ad5cf05e,premium,1444490578.28,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("125712,album,a2b98a935eff49eda6bbb345466d52ab,premium,1444624885.03,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("1300,album,2e685d0633204bbea0b17e874840c960,premium,1444708564.2,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("13281,album,2130d7fd86304434b2ad13d0d7b6b3e4,premium,1444625072.94,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("1343,album,d79857251f1547488a8b6f4906201f7b,premium,1444630322.71,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("1346,collection,73b58682ddf64c5d91f3c08406f53a20,premium,1444528479.92,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("13560,collection,f18f1ebe913a4c2fbd424193ad5cf05e,premium,1444416981.43,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("13600,album,e2f05c0bc2c04b69beebc4802dcc1d9f,premium,1444505248.1,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("14071,collection,f18f1ebe913a4c2fbd424193ad5cf05e,premium,1444491975.68,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("141223,album,0906459e5a1f42deaf51db0a77592001,premium,1444757751.41,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("14201,album,4a87eec7127f403db70f53b44bb146ae,premium,1444582814.61,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("14212,album,889d4c79ba334ba3894fe82a2313ffcf,premium,1444582781.0,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("1442,album,f18f1ebe913a4c2fbd424193ad5cf05e,premium,1444415305.73,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("14517,album,b34b80ed93634621b8407022416e5ef1,premium,1444582795.03,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("145914,collection,1513df35bdbd47f2b3fd219c610ee6c3,premium,1444415712.92,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("1513,album,ee800c3250db494e8b33967e3355aa7b,premium,1444595335.87,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("1517,album,80f577926e9641e3a46809565a6fd04a,premium,1444607069.88,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("1532,album,0a3e70ec4c054581a2397cde2f8f2c52,premium,1444675111.54,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("1532,album,2acef701d6d24587b2553dff7d5d5497,premium,1444592398.36,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("1532,album,2e685d0633204bbea0b17e874840c960,premium,1444806135.99,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("1532,album,ae2583d829ae4fe1a64736aa259364f6,premium,1444773301.62,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("1532,album,b21807e523a74a4fa1d4befd3a61861a,premium,1444624760.53,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("1532,album,c9a5e4cb097446a082aa44426a02ac46,premium,1444794159.4,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("1532,album,cb77dea36ef64492ad5e53fe9aef2028,premium,1444718135.99,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("1532,album,cb77dea36ef64492ad5e53fe9aef2028,premium,1444757145.88,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("1532,album,d6305108fadb470191982bc34334e2f0,premium,1444606574.78,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("1532,album,f18f1ebe913a4c2fbd424193ad5cf05e,premium,1444415227.62,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("1532,album,f18f1ebe913a4c2fbd424193ad5cf05e,premium,1444605712.18,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("1532,album,f18f1ebe913a4c2fbd424193ad5cf05e,premium,1444697906.62,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("1532,album,f18f1ebe913a4c2fbd424193ad5cf05e,premium,1444708261.16,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("1532,collection,1513df35bdbd47f2b3fd219c610ee6c3,premium,1444415526.67,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("1532,collection,1513df35bdbd47f2b3fd219c610ee6c3,premium,1444415527.2,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("1532,collection,1513df35bdbd47f2b3fd219c610ee6c3,premium,1444415528.81,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("1532,collection,1513df35bdbd47f2b3fd219c610ee6c3,premium,1444491343.42,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("1532,collection,1513df35bdbd47f2b3fd219c610ee6c3,premium,1444491348.61,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("1532,collection,1513df35bdbd47f2b3fd219c610ee6c3,premium,1444491350.34,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("1532,collection,1513df35bdbd47f2b3fd219c610ee6c3,premium,1444491351.04,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("1532,collection,1513df35bdbd47f2b3fd219c610ee6c3,premium,1444491353.45,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("1532,collection,1513df35bdbd47f2b3fd219c610ee6c3,premium,1444491354.86,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("1532,collection,73b58682ddf64c5d91f3c08406f53a20,premium,1444507838.23,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("1532,collection,73b58682ddf64c5d91f3c08406f53a20,premium,1444545059.93,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("1532,collection,73b58682ddf64c5d91f3c08406f53a20,premium,1444580614.36,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("1532,collection,9056b1ef8cb34d13b5d0b2734f46f097,premium,1444507611.69,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("1532,collection,f18f1ebe913a4c2fbd424193ad5cf05e,premium,1444416984.47,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("1532,collection,f18f1ebe913a4c2fbd424193ad5cf05e,premium,1444416985.42,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("1532,collection,f18f1ebe913a4c2fbd424193ad5cf05e,premium,1444416985.74,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("1532,collection,f18f1ebe913a4c2fbd424193ad5cf05e,premium,1444416986.38,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("1532,collection,f18f1ebe913a4c2fbd424193ad5cf05e,premium,1444417060.59,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("1532,collection,f18f1ebe913a4c2fbd424193ad5cf05e,premium,1444417089.96,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("1532,collection,f18f1ebe913a4c2fbd424193ad5cf05e,premium,1444417090.54,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("1532,collection,f18f1ebe913a4c2fbd424193ad5cf05e,premium,1444417485.67,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("1532,collection,f18f1ebe913a4c2fbd424193ad5cf05e,premium,1444417486.54,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("1532,collection,f18f1ebe913a4c2fbd424193ad5cf05e,premium,1444417486.92,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("1532,collection,f18f1ebe913a4c2fbd424193ad5cf05e,premium,1444417487.89,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("1532,collection,f18f1ebe913a4c2fbd424193ad5cf05e,premium,1444417497.95,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("1532,collection,f18f1ebe913a4c2fbd424193ad5cf05e,premium,1444417498.53,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("1532,collection,f18f1ebe913a4c2fbd424193ad5cf05e,premium,1444417499.03,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("1532,collection,f18f1ebe913a4c2fbd424193ad5cf05e,premium,1444417499.51,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("1532,collection,f18f1ebe913a4c2fbd424193ad5cf05e,premium,1444417500.08,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("1532,collection,f18f1ebe913a4c2fbd424193ad5cf05e,premium,1444417509.27,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("1532,collection,f18f1ebe913a4c2fbd424193ad5cf05e,premium,1444417510.43,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("1532,collection,f18f1ebe913a4c2fbd424193ad5cf05e,premium,1444417511.57,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("1532,collection,f18f1ebe913a4c2fbd424193ad5cf05e,premium,1444458644.75,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("1532,collection,f18f1ebe913a4c2fbd424193ad5cf05e,premium,1444460448.63,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("1532,collection,f18f1ebe913a4c2fbd424193ad5cf05e,premium,1444462253.59,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("1532,collection,f18f1ebe913a4c2fbd424193ad5cf05e,premium,1444464051.35,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("1532,collection,f18f1ebe913a4c2fbd424193ad5cf05e,premium,1444490580.98,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("1532,collection,f18f1ebe913a4c2fbd424193ad5cf05e,premium,1444490591.31,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("1532,collection,f18f1ebe913a4c2fbd424193ad5cf05e,premium,1444491352.16,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("1532,collection,f18f1ebe913a4c2fbd424193ad5cf05e,premium,1444491353.98,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("1532,collection,f18f1ebe913a4c2fbd424193ad5cf05e,premium,1444491355.45,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("1532,collection,f18f1ebe913a4c2fbd424193ad5cf05e,premium,1444492986.1,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("15748,album,b21807e523a74a4fa1d4befd3a61861a,premium,1444505169.95,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("1578,collection,f18f1ebe913a4c2fbd424193ad5cf05e,premium,1444416987.11,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("1578,collection,f18f1ebe913a4c2fbd424193ad5cf05e,premium,1444417485.2,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("1578,collection,f18f1ebe913a4c2fbd424193ad5cf05e,premium,1444417503.56,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("1578,collection,f18f1ebe913a4c2fbd424193ad5cf05e,premium,1444417512.7,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("1578,collection,f18f1ebe913a4c2fbd424193ad5cf05e,premium,1444417516.73,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("1578,collection,f18f1ebe913a4c2fbd424193ad5cf05e,premium,1444491352.56,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("160306,album,d56e3aa7536f41759219847d032cae7a,premium,1444593820.85,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("160306,album,d56e3aa7536f41759219847d032cae7a,premium,1444709161.92,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("160306,album,d56e3aa7536f41759219847d032cae7a,premium,1444785777.25,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("160306,album,d56e3aa7536f41759219847d032cae7a,premium,1444843953.34,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("160560,album,a2b98a935eff49eda6bbb345466d52ab,premium,1444606121.95,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("160560,album,a2b98a935eff49eda6bbb345466d52ab,premium,1444606283.05,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("160560,album,a2b98a935eff49eda6bbb345466d52ab,premium,1444606739.13,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("160560,album,a2b98a935eff49eda6bbb345466d52ab,premium,1444630116.05,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("160954,collection,e66edc515c3343889e253ebb2f788610,premium,1444844447.87,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("1671,album,c9a5e4cb097446a082aa44426a02ac46,premium,1444709420.28,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("1671,album,d79857251f1547488a8b6f4906201f7b,premium,1444606579.01,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("1671,collection,f18f1ebe913a4c2fbd424193ad5cf05e,premium,1444417512.06,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("1674,album,1b3db7cbf6144df09155041f06bcdd32,premium,1444606565.55,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("168746,album,e2f05c0bc2c04b69beebc4802dcc1d9f,premium,1444607760.6,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("17138,playlist,9976500fac414a1989c6019c9701703d,open,1444415055.3,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("172853,album,11c807ba6ccf4905b5c7706a94d5edd9,premium,1444592571.37,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("172853,album,11c807ba6ccf4905b5c7706a94d5edd9,premium,1444593474.47,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("172853,album,11c807ba6ccf4905b5c7706a94d5edd9,premium,1444630494.8,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("172853,album,11c807ba6ccf4905b5c7706a94d5edd9,premium,1444708815.0,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("172853,album,11c807ba6ccf4905b5c7706a94d5edd9,premium,1444757926.57,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("172853,album,11c807ba6ccf4905b5c7706a94d5edd9,premium,1444785132.98,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("175573,album,3e39dba9b1ff47e3b4fdd51b4f089cf4,premium,1444605913.51,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("175573,album,3e39dba9b1ff47e3b4fdd51b4f089cf4,premium,1444625068.37,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("175573,album,3e39dba9b1ff47e3b4fdd51b4f089cf4,premium,1444675286.85,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("176976,album,c383e2d2379e430da3784dcd145e1ade,premium,1444504941.22,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("177026,album,c383e2d2379e430da3784dcd145e1ade,premium,1444630321.5,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("178973,album,b6a4a2c8b9914200908791545d29f505,premium,1444606559.09,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("178973,album,b6a4a2c8b9914200908791545d29f505,premium,1444708563.11,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("178973,album,b6a4a2c8b9914200908791545d29f505,premium,1444794410.27,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("183809,album,99edf248849f422590c1b59074b0f2dd,premium,1444785317.58,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("185213,album,ac434a451d5e4fd4aeb1b698102bb975,premium,1444594322.11,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("185295,album,99edf248849f422590c1b59074b0f2dd,premium,1444843790.6,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("185341,album,99edf248849f422590c1b59074b0f2dd,premium,1444758111.53,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("185806,album,99edf248849f422590c1b59074b0f2dd,premium,1444630680.55,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("18601,album,263da181343147deb3e72a0133326de9,premium,1444605962.19,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("186453,album,99edf248849f422590c1b59074b0f2dd,premium,1444592757.9,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("186453,album,99edf248849f422590c1b59074b0f2dd,premium,1444593660.98,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("186453,album,99edf248849f422590c1b59074b0f2dd,premium,1444709001.85,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("186453,album,99edf248849f422590c1b59074b0f2dd,premium,1444785616.57,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("1904,album,d2b1905ac7f845449f833140eaf3315f,premium,1444606123.28,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("192106,artist,e66edc515c3343889e253ebb2f788610,premium,1444773494.91,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("196886,album,f18f1ebe913a4c2fbd424193ad5cf05e,premium,1444625872.62,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("196937,album,f18f1ebe913a4c2fbd424193ad5cf05e,premium,1444504753.58,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("196937,album,f18f1ebe913a4c2fbd424193ad5cf05e,premium,1444595533.02,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("196937,collection,f18f1ebe913a4c2fbd424193ad5cf05e,premium,1444415911.64,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("196937,collection,f18f1ebe913a4c2fbd424193ad5cf05e,premium,1444490790.91,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("196937,collection,f18f1ebe913a4c2fbd424193ad5cf05e,premium,1444508035.34,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("196937,collection,f18f1ebe913a4c2fbd424193ad5cf05e,premium,1444526349.71,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("198106,album,d7017384eb2041bfbd1164fd479dde38,premium,1444593301.21,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("201469,album,0906459e5a1f42deaf51db0a77592001,premium,1444710240.51,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("201520,album,0906459e5a1f42deaf51db0a77592001,premium,1444757589.53,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("201520,album,0906459e5a1f42deaf51db0a77592001,premium,1444784960.33,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("20387,album,ba90fe61b1624ecfb7924a96fe0e33d7,premium,1444785797.38,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("2043,album,b6a4a2c8b9914200908791545d29f505,premium,1444606296.81,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("2043,album,c9a5e4cb097446a082aa44426a02ac46,premium,1444630687.29,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("2043,collection,f18f1ebe913a4c2fbd424193ad5cf05e,premium,1444416983.74,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("2043,collection,f18f1ebe913a4c2fbd424193ad5cf05e,premium,1444417088.35,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("2043,collection,f18f1ebe913a4c2fbd424193ad5cf05e,premium,1444417089.26,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("2043,collection,f18f1ebe913a4c2fbd424193ad5cf05e,premium,1444417100.21,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("2043,collection,f18f1ebe913a4c2fbd424193ad5cf05e,premium,1444417502.88,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("2043,collection,f18f1ebe913a4c2fbd424193ad5cf05e,premium,1444417510.43,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("2043,collection,f18f1ebe913a4c2fbd424193ad5cf05e,premium,1444417515.97,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("2043,collection,f18f1ebe913a4c2fbd424193ad5cf05e,premium,1444417517.8,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("207280,album,0a3e70ec4c054581a2397cde2f8f2c52,premium,1444593078.29,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("207280,album,0a3e70ec4c054581a2397cde2f8f2c52,premium,1444594088.11,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("207280,album,0a3e70ec4c054581a2397cde2f8f2c52,premium,1444709419.28,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("207280,album,0a3e70ec4c054581a2397cde2f8f2c52,premium,1444786003.54,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("207280,album,0a3e70ec4c054581a2397cde2f8f2c52,premium,1444844189.71,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("2089,collection,1513df35bdbd47f2b3fd219c610ee6c3,premium,1444415527.99,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("2089,collection,1513df35bdbd47f2b3fd219c610ee6c3,premium,1444491347.34,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("2089,collection,f18f1ebe913a4c2fbd424193ad5cf05e,premium,1444417061.62,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("2089,collection,f18f1ebe913a4c2fbd424193ad5cf05e,premium,1444417062.11,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("2089,collection,f18f1ebe913a4c2fbd424193ad5cf05e,premium,1444417097.72,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("211962,collection,1513df35bdbd47f2b3fd219c610ee6c3,premium,1444508849.53,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("212013,album,1513df35bdbd47f2b3fd219c610ee6c3,premium,1444607280.91,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("212013,album,1513df35bdbd47f2b3fd219c610ee6c3,premium,1444842133.4,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("212013,collection,1513df35bdbd47f2b3fd219c610ee6c3,premium,1444416124.36,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("212013,collection,1513df35bdbd47f2b3fd219c610ee6c3,premium,1444491312.33,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("212013,collection,1513df35bdbd47f2b3fd219c610ee6c3,premium,1444507824.44,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("212013,collection,1513df35bdbd47f2b3fd219c610ee6c3,premium,1444526562.41,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("212013,search,1513df35bdbd47f2b3fd219c610ee6c3,premium,1444491010.97,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("2135,album,cd52627c4f7d4b05b87af44a350b4a7c,premium,1444606567.85,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("218360,album,d45016d0347d4011bbd1540629d34804,premium,1444607592.31,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("218360,album,d45016d0347d4011bbd1540629d34804,premium,1444842357.71,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("22717,collection,d9d8f0dfa67f45dd8f96e21587b85ca1,premium,1444785396.93,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("22946,album,d56e3aa7536f41759219847d032cae7a,premium,1444592787.99,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("2368,album,0a3e70ec4c054581a2397cde2f8f2c52,premium,1444844191.72,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("25263,album,263da181343147deb3e72a0133326de9,premium,1444843381.96,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("25402,album,d2b1905ac7f845449f833140eaf3315f,premium,1444606764.47,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("2554,album,62628ba0beaf4fe0b836f38d342efe1e,premium,1444843390.84,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("2554,album,ba72fd84ba61440499e359ccf1904c59,premium,1444606571.26,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("2554,album,ba90fe61b1624ecfb7924a96fe0e33d7,premium,1444630683.92,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("2554,album,d2b1905ac7f845449f833140eaf3315f,premium,1444794232.13,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("2554,album,f1220451b3f44c1e8f8e9a629f0f3009,premium,1444843388.55,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("2554,collection,1513df35bdbd47f2b3fd219c610ee6c3,premium,1444415446.13,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("2554,collection,1513df35bdbd47f2b3fd219c610ee6c3,premium,1444491343.08,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("2554,collection,f18f1ebe913a4c2fbd424193ad5cf05e,premium,1444417060.16,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("2554,collection,f18f1ebe913a4c2fbd424193ad5cf05e,premium,1444417483.14,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("2554,collection,f18f1ebe913a4c2fbd424193ad5cf05e,premium,1444417484.62,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("2554,collection,f18f1ebe913a4c2fbd424193ad5cf05e,premium,1444417497.34,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("2600,album,09546630c4d24473b92c30f420c1e94d,premium,1444606569.58,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("2600,album,d56e3aa7536f41759219847d032cae7a,premium,1444630682.37,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("2600,collection,f18f1ebe913a4c2fbd424193ad5cf05e,premium,1444416982.94,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("2600,collection,f18f1ebe913a4c2fbd424193ad5cf05e,premium,1444417502.08,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("2663,collection,1513df35bdbd47f2b3fd219c610ee6c3,premium,1444415483.82,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("27074,collection,f18f1ebe913a4c2fbd424193ad5cf05e,premium,1444415481.15,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("2739,collection,f18f1ebe913a4c2fbd424193ad5cf05e,premium,1444490794.23,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("2750,album,19a0e4c2681a4d08abbaa21b5bf34aea,premium,1444843392.53,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("278,artist,f8bb3b2f82f84ff2acbde8e06bc286f8,premium,1444784641.75,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("280973,album,19ec73f5f05c4f229b77b0c312bbc3d6,premium,1444607046.53,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("2832,album,e2f05c0bc2c04b69beebc4802dcc1d9f,premium,1444842421.9,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("3018,collection,f18f1ebe913a4c2fbd424193ad5cf05e,premium,1444417069.86,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("3018,collection,f18f1ebe913a4c2fbd424193ad5cf05e,premium,1444417096.54,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("3018,collection,f18f1ebe913a4c2fbd424193ad5cf05e,premium,1444417515.02,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("3065,album,0a3e70ec4c054581a2397cde2f8f2c52,premium,1444630686.21,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("3065,album,18649198a57b47ef828b1143da015208,premium,1444843384.12,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("3065,album,b88a3a2d527142efb5fa7a2bfe517f13,premium,1444758118.88,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("3073,album,155ac3cd3223462b9a50c86c5143c942,premium,1444582797.25,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("3250,album,ac434a451d5e4fd4aeb1b698102bb975,premium,1444843385.97,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("3529,album,c383e2d2379e430da3784dcd145e1ade,premium,1444504947.35,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("3529,collection,1513df35bdbd47f2b3fd219c610ee6c3,premium,1444415454.88,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("3529,collection,1513df35bdbd47f2b3fd219c610ee6c3,premium,1444415519.09,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("3529,collection,1513df35bdbd47f2b3fd219c610ee6c3,premium,1444415526.13,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("3575,album,e191f5081ed7459dba1bb5493b1fc71b,premium,1444582820.49,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("3578,album,2f91d30a7d18476fac7d9e9c73287deb,premium,1444582817.86,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("3604,album,2acef701d6d24587b2553dff7d5d5497,premium,1444582823.38,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("38730,album,b34b80ed93634621b8407022416e5ef1,premium,1444843347.65,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("3993,collection,e66edc515c3343889e253ebb2f788610,premium,1444845646.51,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("4040,album,99edf248849f422590c1b59074b0f2dd,premium,1444757754.53,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("4086,album,c383e2d2379e430da3784dcd145e1ade,premium,1444606577.68,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("4167,album,d79857251f1547488a8b6f4906201f7b,premium,1444504947.64,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("417,collection,f18f1ebe913a4c2fbd424193ad5cf05e,premium,1444417061.09,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("417,collection,f18f1ebe913a4c2fbd424193ad5cf05e,premium,1444417511.8,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("417,collection,f18f1ebe913a4c2fbd424193ad5cf05e,premium,1444417512.2,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("4551,album,ee800c3250db494e8b33967e3355aa7b,premium,1444843396.43,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("4551,collection,1513df35bdbd47f2b3fd219c610ee6c3,premium,1444415505.51,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("4551,collection,f18f1ebe913a4c2fbd424193ad5cf05e,premium,1444417087.32,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("48143,album,e8a80f156562444a81ff96ab88d0f28e,premium,1444582870.36,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("48994,album,ba90fe61b1624ecfb7924a96fe0e33d7,premium,1444709212.24,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("4969,collection,f18f1ebe913a4c2fbd424193ad5cf05e,premium,1444416559.56,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("49969,album,ba90fe61b1624ecfb7924a96fe0e33d7,premium,1444593881.64,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("5040,album,12e01d4e852144a7ab874c97a6eb5efe,premium,1444582801.23,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("5061,collection,1513df35bdbd47f2b3fd219c610ee6c3,premium,1444491356.38,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("5061,collection,f18f1ebe913a4c2fbd424193ad5cf05e,premium,1444415501.63,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("5061,collection,f18f1ebe913a4c2fbd424193ad5cf05e,premium,1444417094.59,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("52366,album,f18f1ebe913a4c2fbd424193ad5cf05e,premium,1444415161.16,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("5247,album,d6305108fadb470191982bc34334e2f0,premium,1444630145.58,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("5294,album,2e685d0633204bbea0b17e874840c960,premium,1444606564.21,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("5387,album,2130d7fd86304434b2ad13d0d7b6b3e4,premium,1444676062.41,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("54195,album,f18f1ebe913a4c2fbd424193ad5cf05e,premium,1444415225.49,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("5526,collection,f18f1ebe913a4c2fbd424193ad5cf05e,premium,1444415497.44,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("5526,collection,f18f1ebe913a4c2fbd424193ad5cf05e,premium,1444417507.95,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("5572,collection,1513df35bdbd47f2b3fd219c610ee6c3,premium,1444415523.45,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("557,album,f18f1ebe913a4c2fbd424193ad5cf05e,premium,1444504753.9,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("557,album,f18f1ebe913a4c2fbd424193ad5cf05e,premium,1444697324.61,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("557,collection,1513df35bdbd47f2b3fd219c610ee6c3,premium,1444491344.52,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("557,collection,f18f1ebe913a4c2fbd424193ad5cf05e,premium,1444417486.16,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("557,collection,f18f1ebe913a4c2fbd424193ad5cf05e,premium,1444417518.02,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("5586,album,ba90fe61b1624ecfb7924a96fe0e33d7,premium,1444593093.11,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("58700,album,ba90fe61b1624ecfb7924a96fe0e33d7,premium,1444844256.62,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("5897,collection,f18f1ebe913a4c2fbd424193ad5cf05e,premium,1444491341.34,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("5955,collection,adc51d5948904a21b0bafa87acddba84,premium,1444785430.72,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("603,collection,1513df35bdbd47f2b3fd219c610ee6c3,premium,1444491348.1,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("6084,album,d8b6e9734d294ba58b655ed4b0700263,premium,1444593098.32,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("6283,album,cb77dea36ef64492ad5e53fe9aef2028,premium,1444757598.05,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("6315,collection,6974b5b57f4d4dc08ebf40973616110c,premium,1444416555.64,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("65248,collection,f18f1ebe913a4c2fbd424193ad5cf05e,premium,1444434642.58,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("6548,album,ae2583d829ae4fe1a64736aa259364f6,premium,1444758217.79,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("6548,collection,f18f1ebe913a4c2fbd424193ad5cf05e,premium,1444417067.98,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("6594,collection,f18f1ebe913a4c2fbd424193ad5cf05e,premium,1444417481.76,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("6640,album,09546630c4d24473b92c30f420c1e94d,premium,1444505048.62,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("69692,search,1513df35bdbd47f2b3fd219c610ee6c3,premium,1444415444.59,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("7058,collection,f18f1ebe913a4c2fbd424193ad5cf05e,premium,1444415452.23,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("712,collection,1513df35bdbd47f2b3fd219c610ee6c3,premium,1444582692.8,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("72121,collection,f18f1ebe913a4c2fbd424193ad5cf05e,premium,1444417058.38,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("72167,album,a2b98a935eff49eda6bbb345466d52ab,premium,1444794230.64,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("74164,album,3e39dba9b1ff47e3b4fdd51b4f089cf4,premium,1444708643.03,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("7476,album,d2b1905ac7f845449f833140eaf3315f,premium,1444630138.93,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("75240,album,ae2583d829ae4fe1a64736aa259364f6,premium,1444582767.74,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("76207,album,3e39dba9b1ff47e3b4fdd51b4f089cf4,premium,1444843058.35,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("78158,album,3e39dba9b1ff47e3b4fdd51b4f089cf4,premium,1444758201.4,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("789,album,cb77dea36ef64492ad5e53fe9aef2028,premium,1444784960.67,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("8132,album,ba72fd84ba61440499e359ccf1904c59,premium,1444505154.62,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("83777,collection,1513df35bdbd47f2b3fd219c610ee6c3,premium,1444504536.01,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("861,collection,1513df35bdbd47f2b3fd219c610ee6c3,premium,1444512558.65,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("8792,search,5933c895c12f43c98ec5ae1c91c86271,premium,1444580687.51,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("8863,album,2130d7fd86304434b2ad13d0d7b6b3e4,premium,1444605921.73,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("9055,collection,f18f1ebe913a4c2fbd424193ad5cf05e,premium,1444417495.68,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("9102,search,b2eb0c4793744bc5bd8754a90b604701,premium,1444491102.15,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("91480,album,a2b98a935eff49eda6bbb345466d52ab,premium,1444505038.02,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("9659,album,c9a5e4cb097446a082aa44426a02ac46,premium,1444594137.44,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("9845,album,c9a5e4cb097446a082aa44426a02ac46,premium,1444593087.72,47cba57eef554e7fa85442464e2c2512"),
      SongRecord.convertCsvToSongRecord("9952,album,d6305108fadb470191982bc34334e2f0,premium,1444504763.75,47cba57eef554e7fa85442464e2c2512"))

    var users = Map("47cba57eef554e7fa85442464e2c2512" -> User.convertCsvUser("female,0 - 17,AR,0,47cba57eef554e7fa85442464e2c2512"))
    users = User.addSongRecordsToUsers(songs, users)
    User.generateSessions(users)
    assert(users.last._2.songs.map(_.sessionId).distinct.size == 31)
    assert(SongRecord.groupByTopOfDay(songs).size == 6)
  }

}
