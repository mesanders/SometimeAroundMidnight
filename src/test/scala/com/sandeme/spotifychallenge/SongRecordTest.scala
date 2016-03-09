package com.sandeme.spotifychallenge

import org.scalatest.FunSuite

/**
  * Created by sandeme on 3/6/16.
  */
class SongRecordTest extends FunSuite{

  test("Verify SongRecordConversions") {
    val line = "0,album,00101320699d493697c7bdc3b5977792,open,1444582057.75,aea18678537d40fca61a70d8cd3329b4"
    val songRecord = SongRecord.convertCsvToSongRecord(line)
    assert(songRecord.msPlayedTime == 0)
    assert(songRecord.context == "album")
    assert(songRecord.userId == "aea18678537d40fca61a70d8cd3329b4")
  }

  test("Verify that a SongRecord Object is successfully converted back to CSV") {
    val line = "0,album,00101320699d493697c7bdc3b5977792,open,1444582057.75,aea18678537d40fca61a70d8cd3329b4"
    val songRecord = SongRecord.convertCsvToSongRecord(line)
    assert(songRecord.toString.equals(line))
  }

  /* Integration test will take too long, so commented out.
  test("Verify integration test to load the test file that was provided.") {
    val endsongs: Array[SongRecord] = SongRecord.loadSongRecords("data/end_song_sample.csv")
    assert(endsongs.size == 1342629)
  }
  */
  test("Test topOfRecordTimestamp") {
    val line = "0,album,00101320699d493697c7bdc3b5977792,open,1444582057.75,aea18678537d40fca61a70d8cd3329b4"
    val songRecord = SongRecord.convertCsvToSongRecord(line)
    assert(songRecord.getTopOfDay == 1444521600)
    assert(songRecord.getTopOfHour == 1444579200)
  }
}
