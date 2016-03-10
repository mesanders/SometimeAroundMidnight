# Mike Sanders - Spotify Data Engineering & Machine Learning Take-home Challenge

>Usage: Shell script `./setup.sh` will download dataset from link below. It will check for any things like duplicate rows. It will output to the console the steps that it is taking.

>Usage: Shell script `./setup.sh run`  will package the scala solution and run it with 1G Memory.

>Usage: Shell script `./setup runsql` will run sqlite3 on the lite_analysis.sql file, which has multiple exploratory queries. 


# High Level Analysis:
Based on the two tables, I didn't find anything that, where I could suggest any change to the product itself. I did have a few ideas for further experimentation. Access to more data could reveal better insights into suggestions on the product itself. The main thing that the data told me was that most of the premium users are in the United States. Premium users use the service far more than other users, and there is a weak to moderate correlation between the age of the account and premium users. There is also a weak correlation between premium users and the country. It would be nice to experiment further with users to see how user experience plays into paying for a premium service based on the positive correlation between the amount users use the service. 

One of the more interesting trends I noticed was the amount of users that changed during the 14 day period was roughly 3.8% of the users that The percentage of those 3.8% that went to premium was 67% of that group that changed. Premium users listen to more music than the other types of users. They are more engaged with the product. An experiment that might be worth testing is looking at increased usage for a user after they used the product for some time. As open users increase their usage, provide some of the premium services for free, without them opting into anything for a period of time. However, only offer this to users whose account has aged passed some threshold based upon further analysis.

The top 5-10 countries by number of users had the same set of countries with premium accounts. I was trying to use differnet lenses to look at how premium users were set up because my initial reaction is to see if there was a way to correlate how premium users differentiated with the product. More heterogenous data sets would be helpful. 

Gender in the sample were fairly even at a 48/52% split. There were roughly 26 unknown users.  Playlists outnumbered everything else on the unique context. Gender and age didn't have statistically signifcant relationships among other variables. Women were more likely to repeat songs. 

US users were found to listen to more music than Non-US users. This statistic is why I chose to break down the type of users by countries. I decided to use hierarchical clustering. Despite the long run time because, I decided to do it based on the count for each track by country. This took a long time, but broke down the countries into a nice tree, which is below. Further analysis could be done amongst the groups to find out patterns. There are 4 main groups that the countries were broken down into.  

I broke down the different sessions by day and by hour to see if I can find trends among days. My definition of a session was any set of tracks that were played within a 15 minute window + the length of the previous song played. As a user, there are often times where I get up or get interrupted, but I would consider 2 hours of listening to music a 2 hour session, even if I pause. Each day the number of amount each user listen were consistent.  I plotted the top of day aggregation and top of hour aggregations. After running the code it will generate two separate files with those trends. You could see oscilating number of listeners per hour, which would correlate to the number of users in different time zones. There are opportunities to expand market share. 

## My approach to the overall task.

First things first. Hi. Thank you. I'm excited to be here. Blork. Wait, that doesn't mean anything, sorry, I'm too excited.

- First thing I did was read through the instructions 3x. While reading the instructions, I was thinking that I didn't want to just so I could run stats libraries, but show that 1) I understood the concepts and math behind the stats  2) Show that I could write testable software in a statically typed, compiled language. 

- Open up a MARKDOWN.MD, and start taking notes. I wanted to make sure I captured all my tasks into a unified document. Also I wanted to capture my thought process. Even if I didn't have time to complete them all, I wanted to at least get my brain thinking about the problem. All of this was before I actually did any kind of analysis.

- Set up my environment. Choose some tools. Roll up sleeves, get hands dirty.  

- Set up a script that will automatically download the data, do some quick removal of any full row duplicates. Also, I wanted a script that could also run the sqlite3 script and compile and run the scala library and run tests. Running tests is really important for this analysis because I implemented a lot of the Stats myself instead of using a library (I KNOW I WOULD NEVER DO THIS normally.)

- Experiment with SQLite3 on the data to get a quick way to explore the data before doing any task. Primarily summarizations, understanding where users are located, how many unique users there are. I also used SQL to quickly make sure that all the users in the SONGs table are in the USERS table. Running SQL against the data is a quick way to test that my scala code was working (Example, make sure the same number of users are returned with filters, and that avg, sum etc are bringing back what I expect).

- Set up pom.xml for Maven. Next make sure I could compile and run by script and not just in the IDE. Decide whether or not to use libraries. This was a tough decision, I decided to use only scalatest, anything else I would write myself. Everything that was written would be checked with excel or an online calculator.  


1. Create two classes with companion objects that will load the two major data structures based on the file format. 
2. Make sure that each user can be associated to a list of Songs (Build the framework to do any Student T Tests against user by demographic, and also sessionizing the data). 2) Create a Stats class that would hold vector and possibly matricies for a lot of the calculations. 
3. Create Utility class that would hold a lot of the statstical analysis that will use the Stats class.  
4) Create a launcher program that will run specific analysis


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


## Exploratory Analysis from sqlite3

**Number of total users in the user table.**
9565

**Number of users that are female: **
4560

**Number of users that are male: **
4979

**Number of unknown users**
26

**Number of Song Samples in endsong table.**
1342629

**Number of Users in endsong table that are not in the users table**
0

**List of distinct contexts and the number of times each one was played:**
playlist,672637
artist,198970
album,173970
collection,161551
unknown,56360
app,35973
search,32182
me,10986

**List the distinct product types(account type) and the number of times each one occurs:**
open,924200
premium,391898
free,21278
basic-desktop,5253

**Top 20 Users who listen to music**
8d758d1cf1ae4a6aac3ada5576b4fc25,male,US,9,4409
d10c8673c24048edb5ab72cfdc803b34,male,US,51,3869
01d7789847e9489aaca7230b8cf7f145,female,BE,20,3471
cd21473103fc4fe987008baa2060fea0,male,ES,12,3374
289d885bb40f4d84b8499e879fa080a1,male,GB,70,3308
5787f75ea024493c84a61da6159fab07,female,US,116,3264
197b5d086def4705b230ef32f25ee059,female,NL,181,3203
0b9ae83efc55496687b3dec4338d936d,female,US,16,2870
13d4388a9af44c94903fcb291c5800a9,male,AT,6,2835
362dc7bdabc94fab8a79fe8a1bce9c5d,female,US,40,2794
463df178c11c41699277b0081caeca70,female,US,44,2733
4842939aa8b24462b79d0118e4b02845,female,US,10,2647
d99d7505cae64e22a00221a194b157c5,male,US,92,2571
d6270a3272fa4fdc8829578f30339a4d,male,US,10,2401
ffebaaa5399f402aa768dac777013dc9,female,US,42,2353
093e05bf250f47a6a664541f679b11c7,male,EC,2,2312
3988756b686a477e87a1c4bb89bbfdba,female,MX,92,2274
3b4478b896634468984355f29a977a29,male,US,155,2190
4c0c572f29354df4be85ae1c255e0191,female,US,82,2102
971ca0bd7a2c44bd8fe1036e70d765a2,female,US,21,2094

**Distinct Gender in the Users Table:**
female
male
unknown

**Disctint Age Ranges in the Users Table:**
"0 - 17"
"18 - 24"
"25 - 29"
"30 - 34"
"35 - 44"
"45 - 54"
55+
""

**Number of Distinct Countries in the Users Table:**
69

**Number of Users by Country, top 15 countries.**
US,3147
GB,713
DE,562
MX,519
ES,504
BR,431
IT,341
SE,271
FR,256
NL,255
AR,227
AU,216
CA,196
PH,174
CL,139

**Number of distinct premium accounts By country, Top 20 countries**
US|587
GB|137
SE|117
DE|81
BR|66
NL|64
MX|61
AU|53
NO|53
ES|47
FR|43
AR|41
IT|36
CA|34
FI|31
DK|30
TR|25
NZ|22
PH|17
BE|15

**Total Number of Users not in the US:** 
6418

**Total Number of Users in the US: **
3147

**MIN, MAX, and AVERAGE account ages and then the number of accounts at the min weeks.**
-1,99,74.0940930475693
44

**Check the min and max time in milliseconds that user listened to a single track**
0,99997

**Check Min and Max of epoch timestamps, this is used to verify that the dates are in the correct range, most likely are, but yah  **
1443657600.04,1444867196.61

**Checking distinct user_id, product type combinations. **
9938


## Various Statistical Output From the Analysis 


Aggregating number of tracks listened by UTC day. Saving to output file top_of_day_aggregation.csv
Statistics for number of tracks listened by day:
 Min: 86457.0	Max: 102163.0	 Mean: 95902.0714	 Std_Dev: 4486.0336	 Size: 14	Sum: 1342629.0	 SumSqr: 129022520731.0000	Variance: 20124497.7637

Aggregating number of tracks listened by UTC hour. Saving to output file. default = top_of_hour_aggregation.csv
Statistics for number of tracks listened by hour:
 Min: 1841.0	Max: 6743.0	 Mean: 3995.9196	 Std_Dev: 1291.2882	 Size: 336	Sum: 1342629.0	 SumSqr: 5923625037.0000	Variance: 1667425.2025


#### Gender percentages. Unknown were left out
Percentages:		Female: %47.67		Male: %52.05


#### Difference between male and female listeners based on number of tracks listened
Comparing the difference between male and female listeners based on Number of Tracks they listen to. X1 = male mean number of tracks, and X2 = female mean number of tracks
Null Hypothesis:	 X1 - X2 = 0
Alternative Hypothesis:	 X1 - X2 != 0
Female Listening Stats:	Min: 1.0	Max: 3471.0	 Mean: 142.3325	 Std_Dev: 250.2974	 Size: 4560	Sum: 649036.0	 SumSqr: 377994664.0000	Variance: 62648.7774
Male Listening Stats:	Min: 1.0	Max: 4409.0	 Mean: 138.8550	 Std_Dev: 253.2248	 Size: 4979	Sum: 691359.0	 SumSqr: 415201913.0000	Variance: 64122.7934

Do not Reject the null Hypothesis the difference between the two means are not statistically significant. t value: -0.6740309581639912	T Crit Value: 1.96 and -1.96

#### Difference between male and female listeners on the average amount of time they listen to tracks

Comparing the difference between male and female listeners based on the average amount of time that they listen to tracks. X1 = male mean average time listening, and X2 = female mean average Time listening
Null Hypothesis:	 X1 - X2 = 0
Alternative Hypothesis:	 X1 - X2 != 0
Female Listening Stats:	Min: 0.0	Max: 1053345.3333333333	 Mean: 118156.3289	 Std_Dev: 72654.8335	 Size: 4560	Sum: 5.387928596407552E8	 SumSqr: 87727492819644.9000	Variance: 5278724830.8589
Male Listening Stats:	Min: 0.0	Max: 565211.0	 Mean: 115474.9487	 Std_Dev: 75616.3517	 Size: 4979	Sum: 5.749497696740905E8	 SumSqr: 94855666103467.3400	Variance: 5717832650.4817

Do not Reject the null Hypothesis the difference between the two means are not statistically significant. t value: -1.7657462012411484	T Crit Value: 1.96 and -1.96

#### Difference in mean between male and female users based on the number of times that they repeat listen

Comparing the difference between male and female listeners based on the average number of times that they repeat listens.
 X1 = male mean average repeat listens, and X2 = female mean average repeatListens
Null Hypothesis:	 X1 - X2 = 0
Alternative Hypothesis:	 X1 - X2 != 0
Female Listening Stats:	Min: 1.0	Max: 362.0	 Mean: 2.1592	 Std_Dev: 6.9577	 Size: 4560	Sum: 9846.125321341415	 SumSqr: 241957.8142	Variance: 48.4092
Male Listening Stats:	Min: 1.0	Max: 132.0	 Mean: 2.0305	 Std_Dev: 3.0960	 Size: 4979	Sum: 10109.662900445339	 SumSqr: 68242.2559	Variance: 9.5852

Do not Reject the null Hypothesis the difference between the two means are not statistically significant. t value: -1.1499283541713141	T Crit Value: 1.96 and -1.96

#### Difference between US Listeners and Non listeners on the amount of tracks listened

Percentages:		US: %32.90		Non-US: %67.10


Comparing the difference between US listeners and non US listeners based on the average Amount of tracks they listen to
during exploratory analysis US made up roughly 1/3rd of Users. X1 = US mean number of tracks, and X2 = Non US mean number of tracks
Null Hypothesis:	 X1 - X2 = 0
Alternative Hypothesis:	 X1 - X2 != 0
US Listening Stats:	Min: 1.0	Max: 4409.0	 Mean: 163.3988	 Std_Dev: 292.2997	 Size: 3147	Sum: 514216.0	 SumSqr: 352813778.0000	Variance: 85439.1305
Non-US Listening Stats:	Min: 1.0	Max: 3471.0	 Mean: 129.0765	 Std_Dev: 228.1474	 Size: 6418	Sum: 828413.0	 SumSqr: 440941559.0000	Variance: 52051.2553

Reject the null hypothesis that the the difference between the two means are statistically significant. t value: 5.780130202724316	T Crit Value: 1.96 and -1.96

#### Various statics over the entire sample size from 10/1 - 10/14

Statistics on the sum of the tracks listened for users:
Min: 1.0	Max: 4409.0	 Mean: 140.3689	 Std_Dev: 251.5722	 Size: 9565	Sum: 1342629.0	 SumSqr: 793755337.0000	Variance: 63288.5733

Statistics on the average time listening to tracks for users:
Min: 0.0	Max: 1053345.3333333333	 Mean: 116791.0050	 Std_Dev: 74234.0204	 Size: 9565	Sum: 1.117105963065837E9	 SumSqr: 183172165228950.7500	Variance: 5510689782.8180

Statistics on the account ages in weeks for users:
Min: -1.0	Max: 363.0	 Mean: 74.0941	 Std_Dev: 76.8109	 Size: 9565	Sum: 708710.0	 SumSqr: 108937964.0000	Variance: 5899.9100


Statistics on the average session time in seconds for users:
Min: 0.0	Max: 790.0090000033379	 Mean: 112.2612	 Std_Dev: 71.6116	 Size: 9565	Sum: 1073778.138480881	 SumSqr: 169589877.1994	Variance: 5128.2185


Statistics on the Average Repeat Listens users:
Min: 1.0	Max: 362.0	 Mean: 2.0901	 Std_Dev: 5.2982	 Size: 9565	Sum: 19991.72259263511	 SumSqr: 310253.6186	Variance: 28.0708


#### R Values for Pearson Coorelation:

**ROW/COL**|avgRepeatListens|sumTrackListens|avgTrackListens|accountAges|accountType|countries|ageRanges|gender|isPremium|avgSessionTime
------------|---------------|---------------|---------------|-----------|-----------|---------|---------|------|---------|--------------
avgRepeatListens | 1.0000| 0.0591|-0.1079|-0.0342|-0.0161|-0.0023|-0.0045|-0.0131|-0.0124|-0.1022	
sumTrackListens | 0.0591| 1.0000| 0.0898| 0.0382| 0.1371|-0.0437|-0.0762|-0.0087|0.1892|0.1222	
avgTrackListens |-0.1079| 0.0898| 1.0000| 0.1274| 0.0356|-0.0398| 0.1112|-0.0163| 0.0059|0.9885	
accountAges  |-0.0342| 0.0382| 0.1274|1.0000|0.4191|-0.0156	|0.1523| 0.0396|0.1653| 0.1329	
accountType |-0.0161| 0.1371| 0.0356|0.4191|1.0000|0.0166|0.1737| 0.0636|0.7285	| 0.0435	
countries |-0.0023|-0.0437	|-0.0398|-0.0156|0.0166|1.0000	|0.0124	| 0.0138|0.0234	|-0.0429	
ageRanges|-0.0045|-0.0762| 0.1112|0.1523|0.1737|0.0124|1.0000| 0.0854|0.1341| 0.1062	
gender  |-0.0131|-0.0087|-0.0163|0.0396|0.0636|0.0138|0.0854| 1.0000|0.0410|-0.0181	
isPremium |-0.0124| 0.1892| 0.0059|0.1653|0.7285|0.0234|0.1341	|0.0410| 1.0000	|0.0175	
avgSessionTime |-0.1022| 0.1222| 0.9885|0.1329|0.0435|-0.0429|0.1062|-0.0181|0.0175	| 1.0000	

*Significance of the relationship between the two variables for r Values for each of the correlations
Hypothesis of no relationship comparing the two populations - Null Hypthoesis: r = 0
In the following table 'TRUE' means that the relationship between the two was found significant, and 'FALSE' means
that we do not reject the null hypothesis and the relationship is not statistically significant. Also, a strong
positive relationship can be associated as not significant and a weak correlation can have a statistically significant relationship*


#### Statistical Significant correlation between variables

ROW/COL|avgRepeatListens|sumTrackListens|avgTrackListens|accountAges|accountType|countries|ageRanges|gender|isPremium|avgSessionTime
----------------------------|----------------|---------------|---------------|-----------|-----------|---------|---------|------|---------|---------------
**avgRepeatListens**|false|false|false|false|true|true|true|true|true|false	
**sumTrackListens**||false|false|false|false|false|false|false|true|false|false
**avgTrackListens**|false|false|false|false|false|false|false|true|true|false
**accountAges**|false|false|false|false|false|true|false|false|false|false	
**accountType**|true|false|false|false|false|true|false|false|false|false	
**countries**|true|false|false|true|true|false|true|true|false|false	
**ageRanges**|true|false|false|false|false|true|false|false|false|false	
**gender**|true|true|true|false|false|true|false|false|false|true	
**isPremium**|true|false|true|false|false|false|false|false|false|true	
**avgSessionTime**|false|false|false|false|false|false|false|true|true|false	


#### Statistics on the sample of users based on their last listen
Percentages		Premium: %16.65 	Open: %80.87 	Free: %2.24 	Desktop: %0.24

Account Type | Min | Max | Mean |Std_Dev | Size | Sum | SumSqr | Variance
----------------------------------------------------------------------------
Premium| 0.0 | 8.7933082E8	| 28535665.1808| 55645890.1416	| 1593	| 4.5457314633E10	| 6226727133101526000.0	|3096465089645960.5
Free|1021.0	| 2.41972367E8|15677332.0981| 30615246.8187	 |214| 3.354949069E9 | 252240131672358848.0	| 937293337771506.9 
Open | 0.0	| 7.16991435E8	 | 15911016.4034 | Std_Dev: 29718009.8284	| 7735	| 1.2307171188E11 | 8788556303019612200.0| 883160108159706.6
----------------------------------------------------------------------------------------------------------
#### Comparing the different product services to one another based on the average listening Time
Premium to free comparing the difference of means avg listening time between premium users as X and Free Users as Y
Reject the null hypothesis that the the difference between the two means are statistically significant. 
t value: 5.113285777590281 |  T Crit Value: 1.96 and -1.96


Premium to open comparing the difference means of avg listening time between premium and open users: Premium X and Open Y
Reject the null hypothesis that the the difference between the two means are statistically significant. 
t value: 8.80034266626886  | T Crit Value: 1.96 and -1.96

Premium to Desktop comparing avg listening time between premium and desktop users: Premium X and Desktop Y
Do not Reject the null Hypothesis the difference between the two means are not statistically significant. 
t value: -1.0750958015491177 | T Crit Value: 2.074 and -2.074

Free to Open comparing avg listening. Free: X and Open: Y
 Do not Reject the null Hypothesis the difference between the two means are not statistically significant. 
 t value: -0.11023270695190775 | T Crit Value: 1.96 and -1.96

#### Percentages of changes from a nonpremium service to a premium service from the beginning of October to middle of October
Changed and Premium at the end of the session %62.4665
Changed and Not premium at the end of the session %37.5335

#### Hierarchal Clustering Results on Country for Track ID:
```
  -
    -
      -
        PH
        -
          AR
          IE
      -
        DE
        -
          NL
          -
            BR
            SI
-----------------------------------------------------------------  
    -
      -
        -
          -
            -
              PL
              -
                -
                  -
                    UY
                    -
                      LV
                      MC
                  -
                    DO
                    -
                      -
                        BG
                        -
                          PY
                          RU
                      -
                        IL
                        -
                          LI
                          -
                            IS
                            PR
                -
                  CR
                  -
                    GR
                    -
                      MT
                      -
                        JP
                        -
                          GT
                          NI
            -
              -
                CA
                PA
              -
                SG
                -
                  AT
                  PE
          -
            -
              CW
              -
                NZ
                -
                  -
                    LT
                    RO
                  -
                    -
                      SV
                      ZW
                    -
                      BO
                      EE
            -
              ES
              -
                FI
                -
                  HN
                  HU
        -
          -
            BE
            GB
          -
            -
              CO
              TW
            -
              FR
              -
                NO
                -
                  CY
                  -
                    CL
                    ZZ
---------------------------------------
      -
        -
          -
            SE
            -
              PT
              -
                CZ
                -
                  MW
                  -
                    AP
                    US
          -
            AU
            -
              EC
              SK
        -
          -
            DK
            -
              HK
              MX
          -
            -
              A1
              IT
            -
              MY
              -
                CH
                -
                  IN
                  TR
```

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
		- context -- the UI context the track was played from (e.g. playlist,artist, album, app, collection, search, unknown )
		- track_id -- the random UUID for the track
		- product -- the product status (FREE/PREMIUM)
		- end_timestamp -- time in EpOCH
		- user_id -- anonymous UUID of the 

	
### Preliminary list of Questions in my mind (* indicates suggestions from Spotify)

#### Some tasks that I feel that I might need to do:
* Identify the different services spotify already offers
* Identify different types of algorithms that they might use ( Extrapolate based on the services ). 
* Identify possible suggestions to improve their services
* Go through each data set and verify that the data is clean, identify that categorical data adds up with those two categories. Find rows that 	might be missing or have incomplete data (most likely data is clean, but make sure you identify this step).
* We need to create Sessions…. Will have to do some kind of sessioning of the data, this will have to go through a data transformation, and 	create a new field for session ID, this needs to be generated, most likely by the amount of time between each track. Note: This should be done 	before answering any questions
* Create session buckets based on clustering.
* Create Top of Date Calculated field. This can be calculated via (Epoch Timestamps - (Timestamp % 86400).
* Identify any other possibly calculated fields

#### Questions to Ask the Data:
[x]**Determine whether male and female listeners are significantly different in their overall listening (in terms of the count of tracklistens, or in terms of the total time spent listening)
[x]**Break the user listening into sessions (exactly what is a listening session is up to you to define)
[x]**Look for correlations between user demographic features (or their behavior) and their overall listening, or their average sessionlengths
[x]**Find a clustering of user categories that delineates some interesting or useful behavior traits (or show that no clustering makes sense)
[x]Is there a correlation between Premium and Open accounts and the amount of tracks, or the types of tracks that users listen to
[ ]Identify the number of premium vs open by country and and see if there is a coorelation
[ ]Identify top songs per day and see if there are any trends. 
[ ]Identify how many times individual users listen repeat listen to songs. Do this for two separate things, within a Session, and Across Sessions.
[ ]Identify whether or not users change from playlist to artist etc, within one session. Identify frequency across users, separate by type of 	accounts and by gender, break this into multiple slices to view it.
[x] Create clustered sessions, and create buckets for each of those session lengths
[x] Breakdown Countries and number of Premium vs Open Account Types
[ ] Possible Regression to identify estimated time of listening sessions: Possible Model:  Age Bucket, Gender, Account Type, Account Age, 
[x] Identify Number of sessions per day, slice and dice by different Demographics
[x] Identify the number of sessions and average session lenght, Also Average total listening length per day. Split this up by Open VSPremium. 	Find some sort of threshold in the Open users…. If Open Users Listen past that threshold, maybe they have some kind of opportunityto get a 	trial on premium. The Suggestion could be something like: “Based on how much you listen to music, you could listen to X moresongs, based on the 	number of ads you get.”
[x] Create a Matrix by Country and tracks listened to. Run Hierarchal clustering analysis to see what groups emerge by Country. Could then further analyze the subgroups for song recommendation based on clumped countries
[x] For each song, calculate the average length listened to that song, the max and min, and the std deviation…. It might be interesting, do this 	for every song except the song at the end of each session. The reason, we don’t want to include the end session because that song could be 	terminated because they were done with the session	
