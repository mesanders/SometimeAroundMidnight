package com.sandeme.spotifychallenge

import com.sandeme.spotifychallenge.utilities.{StudentTTest, Utility, Stats}
import com.sandeme.spotifychallenge.utilities.Utility._

import scala.collection.mutable.ArrayBuffer

/**
  * Created by sandeme on 3/8/16.
  */
object UserSongAnalysis {

  def statsByUsersProductChange(users: Map[String, User]): Unit = {

    val usersWhoChanged = users.filter(_._2.songs.map(_.product).distinct.size > 1)
    val newPremium = usersWhoChanged.filter{user => val songs = user._2.songs.sortBy(_.endTimestamp);
      songs.last.product.equals("premium")
    }
    val quitPremium = usersWhoChanged.filter{user => val songs = user._2.songs.sortBy(_.endTimestamp);
      !songs.last.product.equals("premium")
    }

    println(f"\n\nChanged and Premium at the end of the session %%${(newPremium.size.toDouble / usersWhoChanged.size) * 100}%.4f")
    println(f"Changed and Not premium at the end of the session %%${(quitPremium.size.toDouble / usersWhoChanged.size) * 100}%.4f")

    /* The following are commented out because there was nothing of statistical significance between the users who changed
    println("\n\nDemographics for new Users")
    statsByDemographics(newPremium)
    println("\n\nDemographic stats for users who left premium")
    statsByDemographics(quitPremium)
    */
  }


  def avgSessionLength(songs: ArrayBuffer[SongRecord]): Double = {
    val songList = songs.sortBy(- _.endTimestamp)
    var topOfSession = songList(0)
    var lastSessionId = songList(0).sessionId
    var numSessions = 1
    var previousSessionTime = topOfSession.endTimestamp
    var sum = 0.0
    for (i <- 1 to songList.size - 1) {
      if (!lastSessionId.equals(songList(i))) {
        sum += topOfSession.endTimestamp + (topOfSession.msPlayedTime / 1000.0) - previousSessionTime
        numSessions += 1
        topOfSession = songList(i)
        lastSessionId = songList(i).sessionId
      }
      previousSessionTime = songList(i).endTimestamp
    }
    sum += topOfSession.endTimestamp + (topOfSession.msPlayedTime / 1000.0) - previousSessionTime
    numSessions += 1
    sum / numSessions
  }

  /**
    * Work horse method to run pearson correlation on userdata, and then make it pretty. Creates a Matrix and then runs over each piece.
    * @param records
    */
  def pearsonCorrelationUserData(records: (Array[SongRecord], Array[User])): Unit = {
    // These two variables will be used to map Countries to a categorical Number.
    val countries = records._2.map(_.country).distinct
    val products = records._2.map(_.songs.last.product).distinct
    val labels = Array("avgRepeatListens", "sumTrackListens", "avgTrackListens", "accountAges", "accountType", "countries", "ageRanges", "gender", "isPremium", "avgSessionTime")
    lazy val avgSessionTime: DblVector = records._2.map(in => avgSessionLength(in.songs))
    lazy val avgRepeatListens: DblVector = User.getStatsAvgRepeatListens(records._2)
    lazy val sumTrackListens: DblVector = records._2.map(_.songs.size.toDouble)
    lazy val avgTrackListens:DblVector = records._2.map(in => in.songs.map(_.msPlayedTime).sum.toDouble / in.songs.size)
    lazy val accountAges: DblVector = records._2.map(_.accountAgeWeeks.toDouble)
    lazy val accountType: DblVector = records._2.map(in => products.indexOf(in.songs.last.product).toDouble)
    lazy val byCountry: DblVector = records._2.map(in => countries.indexOf(in.country).toDouble)
    lazy val ageRanges: DblVector = records._2.map(_.ageRange.toDouble)
    lazy val genders: DblVector = records._2.map(_.gender.toDouble)
    lazy val isPremium: DblVector = records._2.map{user => if (user.songs.last.product.equals("premium")) 1.0 else 0.0}

    println(s"\n\nStatistics on the sum of the tracks listened for users:\n" + new Stats(sumTrackListens))
    println(s"\nStatistics on the average time listening to tracks for users:\n" + new Stats(avgTrackListens))
    println(s"\nStatistics on the account ages in weeks for users:\n" + new Stats(accountAges) + "\n")
    println(s"\nStatistics on the average session time in seconds for users:\n" + new Stats(avgSessionTime) + "\n")
    println(s"\nStatistics on the Average Repeat Listens users:\n" + new Stats(avgRepeatListens) + "\n\n")

    // 0 = sumTrackListens | 1 = avgTrackListens | 2 = accountAges | 3 = accountType | 4 = countries | 5 = ageRanges | 6 = genders | 7 = isPremium | 8 = avgSessionLength
    val matrix = Array(avgRepeatListens,sumTrackListens, avgTrackListens, accountAges, accountType, byCountry, ageRanges, genders, isPremium, avgSessionTime)
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
    println("\nSignificance of the relationship between the two variables for r Values for each of the correlations\n" +
      "Hypothesis of no relationship comparing the two populations - Null Hypthoesis: r = 0" +
      "\nIn the following table 'TRUE' means that the relationship between the two was found significant, and 'FALSE' means" +
      "\nthat we do not reject the null hypothesis and the relationship is not statistically significant. Also, a strong" +
      "\npositive relationship can be associated as not significant and a weak correlation can have a statistically significant relationship")
    print("".padTo(20, ' '))
    labels.foreach(in => print(f"\t${in}%20s"))
    println()
    for(i <- 0 to matrix.size - 1) {
      print(labels(i).padTo(20, ' '))
      for (j <- 0 to matrix.size - 1) {
        val correlation = Utility.correlation(matrix(i), matrix(j))
        val significance = StudentTTest.tTestForPearsonCorrelation(correlation, matrix(i).size)
        print(f"${!significance.reject}%20s\t")
      }
      println
    }
  }

  /**
    * Mostly printlns outputing various stats by demographics, using a lot of the methods that were created to support this stuff.
    * @param users
    */
  def statsByDemographics(users: Map[String, User]): Unit = {
    // Might want o
    val femaleStats =  User.getStatsForTrackCountsByGender('f', users)
    val maleStats = User.getStatsForTrackCountsByGender('m', users)
    val percentageFemale = (femaleStats.size.toDouble / users.size) * 100
    val percentageMale = (maleStats.size.toDouble / users.size) * 100
    println(f"Percentages:\t\tFemale: %%${percentageFemale}%.2f\t\tMale: %%${percentageMale}%.2f\n")
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
    val USPercentage = (statsByCountryGroupTuple._1.size.toDouble / users.size) * 100
    val nonUSPercentage = (statsByCountryGroupTuple._2.size.toDouble / users.size) * 100
    println(f"\nPercentages:\t\tUS: %%${USPercentage}%.2f\t\tNon-US: %%${nonUSPercentage}%.2f")
    println("\n\nComparing the difference between US listeners and non US listeners based on the average Amount of tracks they listen to\n" +
      "during exploratory analysis US made up roughly 1/3rd of Users. X1 = US mean number of tracks, and X2 = Non US mean number of tracks")
    println("Null Hypothesis:\t X1 - X2 = 0\nAlternative Hypothesis:\t X1 - X2 != 0")
    println(s"US Listening Stats:\t${statsByCountryGroupTuple._1}")
    println(s"Non-US Listening Stats:\t${statsByCountryGroupTuple._2}\n")
    val testResults = StudentTTest.studentTwoTailedTTest(statsByCountryGroupTuple._1, statsByCountryGroupTuple._2, .05)
    println(testResults.message)
  }

  /**
    * I wanted to print out stats by product type, a lot of this was exploratory in nature. But yeah
    * @param users
    */
  def statsByProductType(users: Map[String, User]): Unit = {
    println("Someone with the chemicals to believe.")
    val premiumUsers: DblVector = users.filter(_._2.songs.last.product.equals("premium")).map(_._2.songs.map(_.msPlayedTime).sum.toDouble).toArray
    val freeUsers: DblVector = users.filter(_._2.songs.last.product.equals("free")).map(_._2.songs.map(_.msPlayedTime).sum.toDouble).toArray
    val openUsers: DblVector = users.filter(_._2.songs.last.product.equals("open")).map(_._2.songs.map(_.msPlayedTime).sum.toDouble).toArray
    val basicDesktop: DblVector = users.filter(_._2.songs.last.product.equals("basic-desktop")).map(_._2.songs.map(_.msPlayedTime).sum.toDouble).toArray

    val percentagePremiumUsers : Double = (premiumUsers.size.toDouble / users.size) * 100
    val percentageFreeUsers : Double = (freeUsers.size.toDouble / users.size) * 100
    val percentageOpenUsers : Double = (openUsers.size.toDouble / users.size) * 100
    val percentageBasicDesktop : Double = (basicDesktop.size.toDouble / users.size) * 100

    println(f"\n\nPercentages\t\tPremium: %%${percentagePremiumUsers}%.2f \tOpen: %%${percentageOpenUsers}%.2f \tFree: %%${percentageFreeUsers}%.2f \tDesktop: %%${percentageBasicDesktop}%.2f")
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
}
