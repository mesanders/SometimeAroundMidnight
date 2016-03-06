package com.sandeme.spotifychallenge

import scala.io.Source

/**
  * Created by sandeme on 3/5/16.
  * ==> end_song_sample.csv <==
  ms_played,context,track_id,product,end_timestamp,user_id

  ==> user_data_sample.csv <==
  gender,age_range,country,acct_age_weeks,user_id

  */
case class User(gender: Char, ageRange: Int, country: String, accountAgeWeeks: Int, userId: String) {
  def this() {
    this('u', 0, "unkown", -1, "unknown")
  }

  override def toString: String = {
    val gen = gender match {
      case 'f' => "female"
      case 'm' => "male"
      case 'u' => "unknown"
    }
    s"${gen}," + User.ageRangeToString(ageRange) + s",${country},${accountAgeWeeks},${userId}"
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
    Array(new User('M', 3, "FR", 444, "HELLO"))
  }

  def ageRangeToString(identifier: Int): String = {
    identifier match {
      case 0 => ""
      case 1 => "0 - 17"
      case 2 => "18 - 24"
      case 3 => "25 - 29"
      case 4 => "30 - 34"
      case 5 => "35 - 44"
      case 6 => "45 - 54"
      case 7 => "55"
    }
  }

  def ageStringToInt(identifier: String): Int = {
    identifier match {
      case "0 - 17" => 1
      case "18 - 24" => 2
      case "25 - 29" => 3
      case "30 - 34" => 4
      case "35 - 44" => 5
      case "45 - 54" => 6
      case "55+" => 7
      case _ => 0
    }
  }

  def convertCsvUser(input: String): User = {
    val line = input.split(",")
    val gender: Char = line(0) match {
      case "female" => 'f'
      case "male" => 'm'
      case _ => 'u'
    }
    val ageRange = ageStringToInt(line(1))
    val country = line(2)
    val acctAgeWeeks = line(3).toInt
    val userID = line(4)


    new User(gender, ageRange, country, acctAgeWeeks, userID );
  }

  def loadSongRecords(file: String, users: Array[User]): Array[User] = {
    Array(new User('M', 0, "FR", 444, "HELLO"))
  }
}