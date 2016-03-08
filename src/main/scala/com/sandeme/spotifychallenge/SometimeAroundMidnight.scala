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
    // Does some tests against demographic data.
    statsByDemographics(songUsersTuple._2)
    // Lists some preliminary stats on Vectors of user data nad then a correlation table
    pearsonCorrelationUserData(songUsersTuple._1, songUsersTuple._2.map(_._2).toArray)
    // Stats by product Type
    statsByProductType(songUsersTuple._2)
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

  def pearsonCorrelationUserData(records: (Array[SongRecord], Array[User])): Unit = {
    // These two variables will be used to map Countries to a categorical Number.
    val countries = records._2.map(_.country).distinct
    val products = records._2.map(_.songs.last.product).distinct
    val labels = Array("avgRepeatListens", "sumTrackListens", "avgTrackListens", "accountAges", "accountType", "countries", "ageRanges", "gender", "isPremium")
    val avgRepeatListens: DblVector = User.getStatsAvgRepeatListens(records._2)
    val sumTrackListens: DblVector = records._2.map(_.songs.size.toDouble)
    val avgTrackListens:DblVector = records._2.map(in => in.songs.map(_.msPlayedTime).sum.toDouble / in.songs.size)
    val accountAges: DblVector = records._2.map(_.accountAgeWeeks.toDouble)
    val accountType: DblVector = records._2.map(in => products.indexOf(in.songs.last.product).toDouble)
    val byCountry: DblVector = records._2.map(in => countries.indexOf(in.country).toDouble)
    val ageRanges: DblVector = records._2.map(_.ageRange.toDouble)
    val genders: DblVector = records._2.map(_.gender.toDouble)
    val isPremium: DblVector = records._2.map{user => if (user.songs.last.product.equals("premium")) 1.0 else 0.0}

    println(s"\n\n\nStatistics on the sum of the tracks listened for users:\n" + new Stats(sumTrackListens))
    println(s"\n\nStatistics on the average time listening to tracks for users:\n" + new Stats(avgTrackListens))
    println(s"\n\nStatistics on the account ages in weeks for users:\n" + new Stats(accountAges) + "\n")
    println(s"\n\nStatistics on the Average Repeat Listens users:\n" + new Stats(avgRepeatListens) + "\n\n")

    // 0 = sumTrackListens | 1 = avgTrackListens | 2 = accountAges | 3 = accountType | 4 = countries | 5 = ageRanges | 6 = genders
    val matrix = Array(avgRepeatListens,sumTrackListens, avgTrackListens, accountAges, accountType, byCountry, ageRanges, genders, isPremium)
    println("R Values for Pearson Coorelation:")
    print("".padTo(20, ' '))
    labels.foreach(in => print(f"${in}%20s"))
    println()
    for(i <- 0 to matrix.size - 1) {
      print(labels(i).padTo(20, ' '))
      for (j <- 0 to matrix.size - 1) {
        val correlation = Utility.correlation(matrix(i), matrix(j))
        print(f"${correlation}%16.4f\t")
      }
      println
    }
  }

  def statsByDemographics(users: Map[String, User]): Unit = {
    // Might want o
    val femaleStats =  User.getStatsForTrackCountsByGender('f', users)
    val maleStats = User.getStatsForTrackCountsByGender('m', users)

    println("Comparing the difference between male and female listeners based on Number of Tracks they listen to. X1 = male mean number of tracks, and X2 = female mean number of tracks")
    println("Null Hypothesis:\t X1 - X2 = 0\nAlternative Hypothesis:\t X1 - X2 != 0")
    println(s"Female Listening Stats:\t${femaleStats}")
    println(s"Male Listening Stats:\t${maleStats}\n")
    println(StudentTTest.studentTwoTailedTTest(maleStats, femaleStats, .05).message)

    println("\nAnd the walls spin and you're paper-thin from the haze of the smoke and the mescaline\n")
    val femaleStatsAvgTime = User.getStatsForAvgListenByGender('f', users)
    val maleStatsAvgTime = User.getStatsForAvgListenByGender('m', users)

    println("Comparing the difference between male and female listeners based on the average amount of time that they listen to tracks. X1 = male mean average time listening, and X2 = female mean average Time listening")
    println("Null Hypothesis:\t X1 - X2 = 0\nAlternative Hypothesis:\t X1 - X2 != 0")
    println(s"Female Listening Stats:\t${femaleStatsAvgTime}")
    println(s"Male Listening Stats:\t${maleStatsAvgTime}\n")
    println(StudentTTest.studentTwoTailedTTest(maleStatsAvgTime, femaleStatsAvgTime, .05).message)

    println("\nBut ya make everything alright, when ya hold and you sqeeze me tight.")

    val femaleSongsAvgRepeatListens = User.getStatsAvgRepeatListens('f', users)
    val maleStatsAvgRepeatListens = User.getStatsAvgRepeatListens('m', users)

    println("Comparing the difference between male and female listeners based on the average number of times that they repeat listens." +
      "\n X1 = male mean average repeat listens, and X2 = female mean average repeatListens")
    println("Null Hypothesis:\t X1 - X2 = 0\nAlternative Hypothesis:\t X1 - X2 != 0")
    println(s"Female Listening Stats:\t${femaleSongsAvgRepeatListens}")
    println(s"Male Listening Stats:\t${maleStatsAvgRepeatListens}\n")
    println(StudentTTest.studentTwoTailedTTest(maleStatsAvgRepeatListens, femaleSongsAvgRepeatListens, .05).message)

    println("\nBut ya make everything alright, when ya hold and you sqeeze me tight.")

    val statsByCountryGroupTuple = User.getAvgListenStatsTuple(users)
    println("\n\nComparing the difference between US listeners and non US listeners based on the average Amount of tracks they listen to\n" +
      "during exploratory analysis US made up roughly 1/3rd of Users. X1 = US mean number of tracks, and X2 = Non US mean number of tracks")
    println("Null Hypothesis:\t X1 - X2 = 0\nAlternative Hypothesis:\t X1 - X2 != 0")
    println(s"US Listening Stats:\t${statsByCountryGroupTuple._1}")
    println(s"Non-US Listening Stats:\t${statsByCountryGroupTuple._2}\n")
    val testResults = StudentTTest.studentTwoTailedTTest(statsByCountryGroupTuple._1, statsByCountryGroupTuple._2, .05)
    println(testResults.message)
  }

  def statsByProductType(users: Map[String, User]): Unit = {
    val premiumUsers: DblVector = users.filter(_._2.songs.last.product.equals("premium")).map(_._2.songs.map(_.msPlayedTime).sum.toDouble).toArray
    val freeUsers: DblVector = users.filter(_._2.songs.last.product.equals("free")).map(_._2.songs.map(_.msPlayedTime).sum.toDouble).toArray
    val openUsers: DblVector = users.filter(_._2.songs.last.product.equals("open")).map(_._2.songs.map(_.msPlayedTime).sum.toDouble).toArray
    val basicDesktop: DblVector = users.filter(_._2.songs.last.product.equals("basic-desktop")).map(_._2.songs.map(_.msPlayedTime).sum.toDouble).toArray

    val percentagePremiumUsers : Double = premiumUsers.size.toDouble / users.size
    val percentageFreeUsers : Double = freeUsers.size.toDouble / users.size
    val percentageOpenUsers : Double = openUsers.size.toDouble / users.size
    val percentageBasicDesktop : Double = basicDesktop.size.toDouble / users.size

    println(f"\n\nPercentages\t\tPremium: ${percentagePremiumUsers}%.2f \tOpen: ${percentageOpenUsers}%.2f \tFree: ${percentageFreeUsers}%.2f \tDesktop: ${percentageBasicDesktop}%.2f")
    val premiumToFree = StudentTTest.studentTwoTailedTTest(new Stats(premiumUsers), new Stats( freeUsers))
    val premiumToOpen = StudentTTest.studentTwoTailedTTest(new Stats(premiumUsers), new Stats(openUsers))
    val premiumToDesktop = StudentTTest.studentTwoTailedTTest(new Stats(premiumUsers), new Stats(basicDesktop))
    val freeToOpen = StudentTTest.studentTwoTailedTTest(new Stats(freeUsers), new Stats(openUsers))
    println(s"\nStats for premium: " + new Stats(premiumUsers) + "\nStats for free: " + new Stats(freeUsers) + "\nStats for Open Users: " + new Stats(openUsers))
    println(s"\n\nPremium to free comparing the difference of means avg listening time between premium users as X and Free Users as Y\n${premiumToFree.message}\n")
    println(s"\nPremium to open comparing the difference means of avg listening time between premium and open users: Premium X and Open Y\n${premiumToOpen.message}")
    println(s"\nPremium to Desktop comparing avg listening time between premium and desktop users: Premium X and Desktop Y\n${premiumToDesktop.message}")
    println(s"\nFree to Open comparing avg listening. Free: X and Open: Y\n ${freeToOpen.message}")
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
