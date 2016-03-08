.print "Importing data/withheader/user_data_sample.csv into users."
.print "Importing data/withheader/end_song_sample.csv into endsong."
.mode csv tab1
.import data/withheader/user_data_sample.csv users

.mode csv tab2
.import data/withheader/end_song_sample.csv endsong

.print
.print "Describe the User Table."
PRAGMA table_info( users);

.print
.print "Describe the End Song Table." 
PRAGMA table_info(endsong);

.print
.print "Number of total users in the user table."
SELECT COUNT(*) FROM users;

.print
.print "Number of users that are female: " 
SELECT COUNT(*) FROM users WHERE gender = 'female';

.print
.print "Number of users that are male: "
SELECT COUNT(*) FROM users WHERE gender = 'male';

.print
.print "Number of unknown users"
SELECT COUNT(*) FROM users WHERE gender NOT IN ('male', 'female');

.print
.print "Number of Song Samples in endsong table."
SELECT COUNT(*) FROM endsong;

.print 
.print "Number of Users in endsong table that are not in the users table"
SELECT COUNT(DISTINCT user_id) FROM endsong WHERE user_id NOT IN (SELECT user_ID FROM users);


.print
.print "List of distinct contexts and the number of times each one was played:"
SELECT DISTINCT context, COUNT(*) as COUNT FROM endsong GROUP BY context ORDER BY COUNT DESC; 

.print
.print "Top 20 Users who listen to music"
SELECT endsong.user_id, gender, country, acct_age_weeks, COUNT(endsong.user_id) as DUPS FROM endsong JOIN users ON endsong.user_id = users.user_id GROUP BY endsong.user_id ORDER BY DUPS DESC LIMIT 20;


.print
.print "Distinct Gender in the Users Table:"
SELECT DISTINCT gender FROM users;

.print
.print "Disctint Age Ranges in the Users Table:" 
SELECT DISTINCT age_range FROM users;

.print
.print "Number of Distinct Countries in the Users Table:"
SELECT COUNT(DISTINCT country) FROM users;

.print
.print "Number of Users by Country, top 15 countries."
SELECT country, COUNT(*) as NUM_USERS FROM users GROUP BY country ORDER BY NUM_USERS DESC LIMIT 15;

.print
.print "Total Number of Users not in the US: "
SELECT COUNT(*) NUM_USERS FROM users WHERE country != 'US';

.print
.print "Total Number of Users in the US: " 
SELECT COUNT(*) AS NUM_USERS FROM users WHERE country = 'US';

.print 
.print "MIN, MAX, and AVERAGE account ages and then the number of accounts at the min weeks."
SELECT MIN(acct_age_weeks), MAX(acct_age_weeks), AVG(acct_age_weeks) FROM USERS;
SELECT COUNT(*) FROM users WHERE acct_age_weeks IN (SELECT MIN(acct_age_weeks) FROM users);

.print
.print "Check the min and max time in milliseconds that user listened to a single track"
SELECT MIN(ms_played), MAX(ms_played) FROM endsong;

.print
.print "Check Min and Max of epoch timestamps, this is used to verify that the dates are in the correct range, most likely are, but yah"
SELECT MIN(end_timestamp), MAX(end_timestamp) FROM endsong;

.print
.print "Checking distinct user_id, product type combinations."
SELECT COUNT(*) FROM (SELECT DISTINCT users.user_id, product FROM users JOIN endsong ON users.user_id = endsong.user_id ORDER BY users.user_id);

.print
.print "Top 10 Tracks and the number of listens by female, and then by male"
SELECT track_id, COUNT(track_id) as LISTENS FROM endsong JOIN users ON users.user_id = endsong.user_id WHERE gender ='female' GROUP BY track_id ORDER BY LISTENS DESC LIMIT 10;

SELECT track_id, COUNT(track_id) as LISTENS FROM endsong JOIN users ON users.user_id = endsong.user_id WHERE gender ='male' GROUP BY track_id ORDER BY LISTENS DESC LIMIT 10;
