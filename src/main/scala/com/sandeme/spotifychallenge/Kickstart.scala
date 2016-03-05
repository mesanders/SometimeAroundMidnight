package com.sandeme.spotifychallenge

/**
  * Created by sandeme on 3/5/16.
  */
object KickstartMyHeart {
  def main(args: Array[String]) = {
    println("Kickstart my heart");
    User.loadFromFile("/home/sandeme/spotify/data/user_data_sample.csv", println)
    println()
  }
}
