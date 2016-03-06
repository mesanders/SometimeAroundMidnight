# Mike Sanders - Spotify Data Engineering & Machine Learning Take-home Challenge

>Usage: Shell script ./setup.sh will download dataset from link below. It will check for any things like duplicate rows. It will output to the console the steps that it is taking.

## My approach to the overall task.

First things first. Hi. Thank you. I'm excited to be here. Blork. Wait, that doesn't mean anything, sorry, I'm too excited.

- First thing I did was read through the instructions 3x.
- Open up a MARKDOWN.MD, and start taking notes. I wanted to make sure I captured all my tasks into a unified document. Also I wanted to capture key points or thoughts. Even if I didn't have time to complete them all, I wanted to at least get my brain thinking about the problem. All of this was before I actually did any kind of analysis.
- Set up my environment. Choose some tools. Roll up sleeves, get hands dirty.  
- First thing, set up a script that will automatically download the data, do some quick removal of any full row duplicates.
- Experiment with SQLite3 on the data to get a quick way to explore the data before doing any task. Primarily summarizations, understanding where users are located, how many unique users there are. I also used SQL to quickly make sure that all the users in the SONGs table are in the USERS table. 


## Some other information I decided to unstrategically put right here.

**System Specs**
Everything was done on my Samsung Series 9 Laptop Running Ubuntu 15.10. 
*MEMORY*: 8GB Ram 
*CPU*: 4 Core Intel i5-3317U @ 1.70GHz

**Tools of Choice**: Scala, Bash, sqlite3, Intellij, Maven

>Why did you choose Scala and not R or Python?

Good Question! Glad you asked. I wanted to make this as painful as possible. I primarily do Java development and I have been trying to use Scala more in general. Even if Spotify chooses that I am not worthy, I wanted to use this as a chance to get better. But geegolly pandas, dataframes and numpy would have made my life easier. 

>Why did you use almost no libraries in your Scala code?

Sure, I could have used Apache Math Commons, or breeze.io, and I probably should have. In most production situations, I would definitely use a library that is tried and true; however, using a library doesn't allow me to showcase my ability to be lightweight, or understand some very broad concepts. 

While doing most of the tasks over the weekend of 3/5/2016 and 3/6/2016, I spent most of the time on the couch in my basement while my son was jumping on my back while my wife was blaring songs (on Spotify, I might add). Needless to say, the overall quality could be better :)

## Task: 
> Please download the dataset from: https://storage.googleapis.com/ml_take_home/data_sample.tgz, which contains a sample of anonymized user listening data in Spotify. We'd like you to use this data first to do a warm-up exercise, then to perform an open-ended exploration

> Warm-up (Please do this first):
> 1. Determine whether male and fele listeners are significantly different in their overall listening (in terms of the count of track listens, or in terms of the total time spent listening)

### Data Set Identification 
	1.user_data_sample.csv
		- gender -- (male or female)
		- age_range -- bucket age of the user
		- country -- Counter where user is registered
		- acct_age_weeks -- the age of the user's account in weeks
		- user_id -- anonymous UUID 
	2. end_song_sample.csv
		- ms_played -- the amount of time the user listened to the track, in ms
		- context -- the UI context the track was played from (e.g. playlist,artist)
		- track_id -- the random UUID for the track
		- product -- the product status (FREE/PREMIUM)
		- end_timestamp -- time in EpOCH
		- user_id -- anonymous UUID of the user_data_sample


	  case "0 - 17" => 1
      case "18 - 24" => 2
      case "25 - 29" => 3
      case "30 - 34" => 4
      case "35 - 44" => 5
      case "45 - 54" => 6
      case "55+" => 7
      case _ => 0

### Preliminary list of Questions in my mind (* indicates suggestions from Spotify)

#### Some tasks that I feel that I might need to do:
1. Identify the different services spotify already offers
2. Identify different types of algorithms that they might use ( Extrapolate based on the services ). 
3. Identify possible suggestions to improve their services
4. Go through each data set and verify that the data is clean, identify that categorical data adds up with those two categories. Find rows that 	might be missing or have incomplete data (most likely data is clean, but make sure you identify this step).
5. We need to create Sessions…. Will have to do some kind of sessioning of the data, this will have to go through a data transformation, and 	create a new field for session ID, this needs to be generated, most likely by the amount of time between each track. Note: This should be done 	before answering any questions
6. Create session buckets based on clustering.
7. Create Top of Date Calculated field. This can be calculated via (Epoch Timestamps - (Timestamp % 86400).
8. Identify any other possibly calculated fields
9. Identify weights for the popularity of songs. The weights back be built by creating a network graph and find out how many unique relations there are to that song. 

#### Questions to Ask the Data:
1. **Determine whether male and female listeners are significantly different in their overall listening (in terms of the count of tracklistens, 	or in terms of the total time spent listening)
2. **Break the user listening into sessions (exactly what is a listening session is up to you to define)
3. **Look for correlations between user demographic features (or their behavior) and their overall listening, or their average sessionlengths
4. **Find a clustering of user categories that delineates some interesting or useful behavior traits (or show that no clustering makes sense)
5. Is there a correlation between Premium and Open accounts and the amount of tracks, or the types of tracks that users listen to
6. Identify the number of premium vs open by country and and see if there is a coorelation
7. Identify top songs per day and see if there are any trends. 
8. Identify how many times individual users listen repeat listen to songs. Do this for two separate things, within a Session, and Across Sessions.
9. Identify whether or not users change from playlist to artist etc, within one session. Identify frequency across users, separate by type of 	accounts and by gender, break this into multiple slices to view it.
10. Create clustered sessions, and create buckets for each of those session lengths
11. Identify the most popular tracks by Country. 
12. Identify the most popular tracks by Gender
13. Breakdown Countries and number of Premium vs Open Account Types
14. Possible Regression to identify estimated time of listening sessions: Possible Model:  Age Bucket, Gender, Account Type, Account Age, 
15. Identify Number of sessions per day, slice and dice by different Demographics
16. Identify the number of sessions and average session lenght, Also Average total listening length per day. Split this up by Open VSPremium. 	Find some sort of threshold in the Open users…. If Open Users Listen past that threshold, maybe they have some kind of opportunityto get a 	trial on premium. The Suggestion could be something like: “Based on how much you listen to music, you could listen to X moresongs, based on the 	number of ads you get.”
17. Identify Tracks that are commonly listened to by users. This is going to require some in depth coding, ( Possible examples to build fromare 	algorithms used for recommendation engines), create weights so that we can identify songs that are commonly listened together. See if users who 	listen to x song haven’t listened to y song, and use that as a user suggestion. Possibly suggest playlists that other userswho are similar to 	them (based on gender and/or location).
18. Logistic Regression possibility, we can create a model to see if a user is likely to listen to a song, this would include demographic data 	such as where the user lives.
19. For each song, calculate the average length listened to that song, the max and min, and the std deviation…. It might be interesting, do this 	for every song except the song at the end of each session. The reason, we don’t want to include the end session because that song could be 	terminated because they were done with the session	