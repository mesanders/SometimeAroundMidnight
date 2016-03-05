package com.sandeme.spotifychallenge

/**
  * Created by sandeme on 3/5/16.
  * ==> end_song_sample.csv <==
  ms_played,context,track_id,product,end_timestamp,user_id

  ==> user_data_sample.csv <==
  gender,age_range,country,acct_age_weeks,user_id

  */
class User(gender: Char, ageRange: String, country: String, accountAgeWeeks: Int, userId: String) {

  class SongRecord(msPlayedTime: Double, context: String, trackId: String, product: String, endTimestamp: Int, userId: String) {
    
  }

}
