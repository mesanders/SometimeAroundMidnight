package com.sandeme.spotifychallenge


import scala.io.Source
/**
  * Created by sandeme on 3/5/16.
  */

case class SongRecord(msPlayedTime: Int, context: String, trackId: String, product: String, endTimestamp: Double, userId: String, var sessionId: String) {
  override def toString(): String = {
    val formattedTimestamp: String =  String.format("%.2f", endTimestamp: java.lang.Double)
    val retVal = s"${msPlayedTime},${context},${trackId},${product},${formattedTimestamp},${userId}"
    if (sessionId.isEmpty) retVal else retVal + s",${sessionId}"
  }
}

object SongRecord {
  /**
    * Creates an Array of SongRecords, that follows the same
    * format that is inside the file:
    * @param file input file where each row is a SongRecord that has the
    *             ms_played, context, track_id, product, end_timestamp, and user_id
    * @return an Array of SongRecords
    */
  def loadSongRecords(file: String): Array[SongRecord] = {

      val songBuffer =  new scala.collection.mutable.ArrayBuffer[SongRecord]
      val reader = Source.fromFile(file)
      try {
        for (line <- reader.getLines()) {
          val songRecord = convertCsvToSongRecord(line)
          songBuffer += (songRecord)
        }
      } catch {
        case ex: Exception => { ex.printStackTrace() }
      } finally {
        reader.close()
      }
      songBuffer.toArray
  }

  /**
    * Converts a line from the end_song_sample into a record
    * @param line contains the following columns: ms_played, context, track_id, product, end_timestamp, user_id
    * @return SongRecord object
    */
  def convertCsvToSongRecord(line: String): SongRecord = {
    val splits = line.split(",")
    val ms_played = splits(0).toInt
    val context = splits(1)
    val track_id = splits(2)
    val product = splits(3)
    val end_timestamp = splits(4).toDouble
    val user_id = splits(5)

    new SongRecord(ms_played, context, track_id, product, end_timestamp, user_id, "")
  }
}