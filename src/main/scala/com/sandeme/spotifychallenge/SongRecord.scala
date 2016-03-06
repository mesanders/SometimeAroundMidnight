package com.sandeme.spotifychallenge

/**
  * Created by sandeme on 3/5/16.
  */

case class SongRecord(msPlayedTime: Double, context: String, trackId: String, product: String, endTimestamp: Int, userId: String) {

}

object SongRecord {
  def loadSongRecords(file: String): Array[User] = {
    Array(new User('M', 0, "FR", 444, "HELLO"))
  }
}