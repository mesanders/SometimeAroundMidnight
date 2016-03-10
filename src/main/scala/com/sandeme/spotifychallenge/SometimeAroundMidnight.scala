package com.sandeme.spotifychallenge

import java.io.File
import java.util.logging.Logger

import com.sandeme.spotifychallenge.utilities.Utility.DblVector
import com.sandeme.spotifychallenge.utilities.{Clustering, Stats, Utility, StudentTTest}

import scala.collection.mutable.ArrayBuffer

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

    println("Aggregating number of tracks listened by UTC day. Saving to output file top_of_day_aggregation.csv")
    outputTopOfDayCounts(songUsersTuple._1)
    println("Aggregating number of tracks listened by UTC hour. Saving to output file. default = top_of_hour_aggregation.csv")
    outputTopOfHourCounts(songUsersTuple._1)

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

    // Run clustering Algorithm on the User data versus tracks. See if there is a clustering of Countries
    println("Run clustering Algorithm:")
    runClusteringAlgorithm(songUsersTuple._1, songUsersTuple._2)
    System.exit(0)
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
    * prints the top of counts aggregation to a file
    * @param songs
    * @param fileOut
    */
  def outputTopOfDayCounts(songs: Array[SongRecord], fileOut: String = "top_of_day_aggregation.csv") = {
    val writer = new java.io.PrintWriter(new java.io.File(fileOut))
    try {
      SongRecord.groupByTopOfDay(songs).toSeq.sortBy(_._1).foreach(v => writer.write(v._1 +","+ v._2 +"\n"))
      val statsVector: DblVector = SongRecord.groupByTopOfDay(songs).toSeq.map(_._2.toDouble).toArray
      println("Statistics for number of tracks listened by day:\n " + new Stats(statsVector) + "\n")
    } finally {
      writer.close
    }
  }

  /**
    * prints top of hour aggregation to a file
    * @param songs
    * @param fileOut
    */
  def outputTopOfHourCounts(songs: Array[SongRecord], fileOut: String = "top_of_hour_aggregation.csv") = {
    val writer = new java.io.PrintWriter(new java.io.File(fileOut))
    try {
      SongRecord.groupByTopOfHour(songs).toSeq.sortBy(_._1).foreach(v => writer.write(v._1 +","+ v._2 +"\n"))
      val statsVector: DblVector = SongRecord.groupByTopOfHour(songs).toSeq.map(_._2.toDouble).toArray
      println("Statistics for number of tracks listened by hour:\n " + new Stats(statsVector) + "\n")
    } finally {
      writer.close
    }
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


  /**
    * Terrible idea to run this if it's over every track. It will create a 136000 x 69 matrix
    * @param songs
    * @param users
    */
  def runClusteringAlgorithm(songs: Array[SongRecord], users: Map[String, User]) = {


    println("Be aware it took around 2 hours to run this... The bottleneck is mapping the correct counts to the correct" +
      "part of the array. mSetting country for every song record loop through each record and go ahead and add each one there: " + System.currentTimeMillis())
    songs.foreach(song => song.countryId = users(song.userId).country)
    val labels = users.map(user => user._2.country).toArray.distinct.sorted.toIndexedSeq
    val distinctSongs = songs.map(_.trackId).distinct.sorted.toIndexedSeq
    val sortedTrackTuple = songs.map(track => (track.trackId, track.countryId)).sorted
    val matrix = Array.ofDim[Double](labels.size, distinctSongs.size)
    var currentCountry = 0
    var currentTrack = 0
    var tracksUpdated = 0
    for (i <- 0 to sortedTrackTuple.size - 1) {
      currentTrack = distinctSongs.indexOf(sortedTrackTuple(i)._1)
      currentCountry = labels.indexOf(sortedTrackTuple(i)._2)
      matrix(currentCountry)(currentTrack) += 1.0
      tracksUpdated += 1
      if (tracksUpdated % 10000 == 0) {
        println(s"Tracks updated:\t${tracksUpdated} + ${System.currentTimeMillis}")
      }
    }

    println("Going over the clustering Algorithm: " + System.currentTimeMillis())
    println("Label: " + matrix.size)
    Clustering.printCluster(Clustering.hCluster(matrix)(0), labels = Some(labels))
  }
}
