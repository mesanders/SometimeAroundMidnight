package com.sandeme.spotifychallenge

import java.io.File
import java.util.logging.Logger

/**
  * Created by sandeme on 3/5/16.
  */
object SometimeAroundMidnight {
  val defaultUsersFile = "data/user_data_sample.csv"
  val defaultSongFile = "data/end_song_sample.csv"

  def main(args: Array[String]) = {
    val parsedArgs = parseArgs(args)
    println("And the band plays some song about forgetting yourself for a while.\n");
    val userFile = if (parsedArgs.contains("usercsv")) parsedArgs("usercsv") else defaultUsersFile
    val songFile = if (parsedArgs.contains("songcsv")) parsedArgs("songcsv") else defaultSongFile

    if (!new File(userFile).exists || !new File(songFile).exists) {
      println(s"ERROR: ${userFile} or ${songFile} does not exist! Please check to make sure they are on the file system.")
      System.exit(1)
    }

    genderStatistics(userFile , songFile)
  }

  def genderStatistics(userFile: String, songFile :String): Unit = {
    // Might want o
    val songs = SongRecord.loadSongRecords(songFile)
    val users = User.addSongRecordsToUsers(songs, User.loadFromFile(userFile))
    val femaleStats =  User.getStatsForTrackCountsByGender('f', users)
    val maleStats = User.getStatsForTrackCountsByGender('m', users)

    println(s"Female Listening Stats:\t${femaleStats}")
    println(s"Male Listening Stats:\t${maleStats}")
  }

  /**
    * Very lightweight command line parser, assumes key value pair separated by =
    * @param args each arg should be K,V separated by '='
    * @return Mapping of K,V args
    */
  def parseArgs(args: Array[String]): Map[String, String] = {
    if (args.contains("help")) {
      println("Usage: [usercsv=filelocation] [endsongcsv=filelocation]")
      println(s"Default file locations are: User${defaultUsersFile}\tSongs:${defaultSongFile}")
    }

    args.filter(_.contains("=")).map(in => in.split("=")(0) -> in.split("=")(1))(collection.breakOut): Map[String, String]
  }
}
