package com.sandeme.spotifychallenge

import com.sandeme.spotifychallenge.utilities.Stats
import com.sandeme.spotifychallenge.utilities.Utility.DblVector

import scala.io.Source
import scala.collection.mutable.{ArrayBuffer}

/**
  * Created by sandeme on 3/5/16.
  * ==> end_song_sample.csv <==
  ms_played,context,track_id,product,end_timestamp,user_id

  ==> user_data_sample.csv <==
  gender,age_range,country,acct_age_weeks,user_id

  * Case class to as a holder for User data
  * @param gender Char representation either: f, m, or u (unknown)
  * @param ageRange Bucket of age ranges: 1 -> 0-17, 2-> 18-24, 3 -> 25-29, 4 -> 30-34, 5 -> 34-45, 6 -> 44-55, 7 -> 55+, 0 -> ""
  * @param country Two character representation of country
  * @param accountAgeWeeks Age of the Account in weeks
  * @param userId Anonymized UUID
  */
case class User(gender: Char, ageRange: Int, country: String, accountAgeWeeks: Int, userId: String) {
  var songs: scala.collection.mutable.ArrayBuffer[SongRecord] = new ArrayBuffer[SongRecord]

  /**
    * Default constructor that creates a blank object.
    */
  def this() {
    this('u', 0, "unkown", -1, "unknown")
  }


  /**
    * @return String representation back to the original format.
    */
  override def toString: String = {
    val gen = gender match {
      case 'f' => "female"
      case 'm' => "male"
      case 'u' => "unknown"
    }
    s"${gen}," + User.ageRangeToString(ageRange) + s",${country},${accountAgeWeeks},${userId}"
  }

  def addSongRecord(song: SongRecord): Unit = {
    if (song.userId.equals(userId)) {
      songs += song
    }
  }
}

/**
  * Companion Object for User that includes "static" methods that can be used without instantiating the class.
  * This includes loading a file and transforming various parts of the data
  */
object User {
  /**
    * Takes in a file and a function to parse the user.
    * Why a function, well I wasn't sure if there was going to be another function I wanted to use later.
    *
    * @param file Location of the file to read in line by line
    * @param loadFunction
    * @return
    */
  def loadFromFile(file: String, loadFunction: (String) => User = convertCsvUser) : Map[String, User] = {
    val userBuffer =  scala.collection.mutable.Map[String, User]()
    val reader = Source.fromFile(file)
    try {
      for (line <- reader.getLines()) {
          val user = loadFunction(line)
          userBuffer += (user.userId -> user)
      }
    } catch {
      case ex: Exception => { ex.printStackTrace() }
    } finally {
      reader.close()
    }
    userBuffer.toMap
  }

  /**
    * This function will convert Age Range into an identifier... There is definitely a better way to do this
    * however, naming variables for enums caused me to turn down the path of least resistant
    * @param identifier : Integer representation of Age Bucket
    * @return String representation as stored from the original file
    */
  def ageRangeToString(identifier: Int): String = {
    identifier match {
      case 0 => ""
      case 1 => "0 - 17"
      case 2 => "18 - 24"
      case 3 => "25 - 29"
      case 4 => "30 - 34"
      case 5 => "35 - 44"
      case 6 => "45 - 54"
      case 7 => "55+"
      case _ => ""
    }

  }

  /**
    * As above This function will convert Age Range into an identifier... There is definitely a better way to do this
    * however, naming variables for enums caused me to turn down the path of least resistant
    * @param identifier: String representation of age bucket
    * @return Integer encode for the Age, probably should be  a short
    */
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

  /**
    * Takes in a CSV representation of what a User Object looks like. This format is based on
    * the format in the user_data_sample.csv provided.
    *
    * @param input String in, that is CSV in the following format:
    *              gender, age bucket, country, account age in weeks, Anonymized UUID
    * @return @see com.sandeme.spotifychallenge.User
    */
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

  /**
    * Place every Record into the user object.
    * @param records Array of @see com.sandeme.spotifychallenge.SongRecord
    * @param users Map of Users where the key is the user ID
    * @return @see scala.collection.immutable.Map
    */
  def addSongRecordsToUsers(records: Array[SongRecord], users: scala.collection.immutable.Map[String, User]): scala.collection.immutable.Map[String, User] = {
    for(record <- records) {
      if (users.contains(record.userId)) users(record.userId).addSongRecord(record)
    }
     users.toMap
  }

  /**
    *
    * Take the inverse of the distinct tracks, versus the number of tracks they listen to. I take the inverse, because it will
    * correlate a higher number of distinct songs. I didn't find this formula anywhere but experimented with a spreadsheet,
    * the min number will be 1, the max number will be the total number of songs, assuming they will listen to each song once
    * @param gender male or female
    * @param users Mapping of Users
    * @return
    */
  def getStatsAvgRepeatListens(gender: Char, users: Map[String, User]): Stats[Double] = {
    val repeatListensVector: DblVector = users.filter(_._2.gender == gender)
      .map{user => val songs = user._2.songs.toArray; 1.0 / (songs.map(_.trackId).distinct.size.toDouble / songs.size)}.toArray
    new Stats(repeatListensVector)
  }

  def getStatsAvgRepeatListens(users: Array[User]): DblVector = {
    users.map{user => val songs = user.songs.toArray; 1.0 / (songs.map(_.trackId).distinct.size.toDouble / songs.size)}.toArray
  }

  def getStatsForTrackCountsByGender(gender: Char, users: scala.collection.immutable.Map[String, User]): Stats[Double] = {
    val countVector: DblVector = users.filter(_._2.gender == gender).map(_._2.songs.size.toDouble).toArray
    new Stats(countVector)
  }

  def getStatsForAvgListenByGender(gender: Char, users: scala.collection.immutable.Map[String, User]): Stats[Double] = {
    val avgListenVector: DblVector = users.filter(_._2.gender == gender)
      .map{userD => val songs = userD._2.songs; songs.map(_.msPlayedTime).sum.toDouble / songs.size}.toArray
    new Stats(avgListenVector)
  }

  def getAvgListenStatsTuple(users: Map[String, User]): (Stats[Double], Stats[Double]) = {
    val statsByUS: DblVector = users.filter(_._2.country.equals("US")).map(_._2.songs.size.toDouble).toArray
    val statsByNonUS: DblVector = users.filter(!_._2.country.equals("US")).map(_._2.songs.size.toDouble).toArray
    (new Stats(statsByUS), new Stats(statsByNonUS))
  }

  def generateSessions(users: Map[String, User]): Map[String, User] = {
    // arbitrary amount of time to test the length - let's do IDK 15 minutes
    val MIN_LENGTH= 900.0
    var previousTimestamp = 0.0
    var previousSessionId = generateUUID

      users.foreach(user => user._2.songs.sortBy(_.endTimestamp)
      .map{song =>
        song.sessionId = if (song.endTimestamp - previousTimestamp  <= 0) previousSessionId else generateUUID
        previousSessionId = song.sessionId
        previousTimestamp = song.endTimestamp + (song.msPlayedTime.toDouble / 1000) + MIN_LENGTH
      })
    users
  }

  def generateUUID = java.util.UUID.randomUUID.toString
}