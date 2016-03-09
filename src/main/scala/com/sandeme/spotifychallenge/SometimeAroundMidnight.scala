package com.sandeme.spotifychallenge

import java.io.File
import java.util.logging.Logger

import com.sandeme.spotifychallenge.utilities.Utility.DblVector
import com.sandeme.spotifychallenge.utilities.{Stats, Utility, StudentTTest}

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

    val songUsersTuple = loadSongAndUserFiles(userFile, songFile)

    println("Generating sessions for users: ")
    User.generateSessions(songUsersTuple._2)

    // Does some tests against demographic data.
    UserSongAnalysis.statsByDemographics(songUsersTuple._2)
    // Lists some preliminary stats on Vectors of user data nad then a correlation table
    UserSongAnalysis.pearsonCorrelationUserData(songUsersTuple._1, songUsersTuple._2.map(_._2).toArray)
    // Stats by product Type
    UserSongAnalysis.statsByProductType(songUsersTuple._2)
    // Users who changed status from or to premium during the session
    UserSongAnalysis.statsByUsersProductChange(songUsersTuple._2)
  }



  /**
    * Takes in User file location and song file location and returns a tuple of Objects Created by the data
    * @param userFile user file with the fields that were specified.
    * @param songFile song file with records.
    * @return Tuple of SongRecords array and a Map of Users
    */
  def loadSongAndUserFiles(userFile: String, songFile: String): (Array[SongRecord], Map[String, User]) = {
    println("Loading Song Files...")
    val songs = SongRecord.loadSongRecords(songFile)
    println("Loading User FilesFiles and adding loading the tracks each user played...\n")
    val users = User.addSongRecordsToUsers(songs, User.loadFromFile(userFile))

    (songs, users)
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
