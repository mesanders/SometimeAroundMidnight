package com.sandeme.spotifychallenge

import scala.io.Source

/**
  * Created by sandeme on 3/5/16.
  * ==> end_song_sample.csv <==
  ms_played,context,track_id,product,end_timestamp,user_id

  ==> user_data_sample.csv <==
  gender,age_range,country,acct_age_weeks,user_id

  */
class User(gender: Char, ageRange: String, country: String, accountAgeWeeks: Int, userId: String) {
  def this() {
    this('u', "45-55", "US", 35, "hellouser")
  }

  class SongRecord(msPlayedTime: Double, context: String, trackId: String, product: String, endTimestamp: Int, userId: String) {

  }

}

/**
  * Companion Object that can load users from file. and then load their SongRecords, we need to identify a
  * Way to sessionize data
  */
object User {
  def loadFromFile(file: String, loadFunction: (String) => Unit) : Array[User] = {
    val reader = Source.fromFile(file)
    try {
      for (line <- reader.getLines()) {
        loadFunction(line)
      }
    } catch {
      case ex: Exception => { ex.printStackTrace() }
    } finally {
      reader.close()
    }
    Array(new User('M', "45-50", "FR", 444, "HELLO"))
  }

  def convertCsvUser(line: String): User = {
    new User();
  }

  def loadSongRecords(file: String, users: Array[User]): Array[User] = {
    Array(new User('M', "45-50", "FR", 444, "HELLO"))
  }
}