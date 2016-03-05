#/bin/bash

function log {
	echo "$(date "+%D %T") INFO: $1"  
}

log "Downloading files"
wget https://storage.googleapis.com/ml_take_home/data_sample.tgz

log "Creating data directory and moving data sample to new directory."
mkdir data
mkdir data/withheader
mv data_sample.tgz data

log "Extracting data_sample into the data driectory"
tar -xzf data/data_sample.tgz -C data

log "Removing top line of each file: "
head -1 data/end_song_sample.csv > data/withheader/end_song_sample.csv
head -1 data/user_data_sample.csv > data/withheader/user_data_sample.csv 
sed -i '1d' data/end_song_sample.csv
sed -i '1d' data/user_data_sample.csv

log "Sorting each file and removing any duplicates."
sort data/end_song_sample.csv -o data/end_song_sample.csv
uniq data/end_song_sample.csv > data/end_song_sample_uniq.csv
sort data/user_data_sample.csv -o data/user_data_sample.csv 
uniq data/user_data_sample.csv > data/user_data_sample_uniq.csv

log "Number of duplicates in end_song_sample: $(comm data/end_song_sample.csv data/end_song_sample_uniq.csv -23 | wc -l)"
log "Number of duplicates in user_data_sample: $(comm data/user_data_sample.csv data/user_data_sample_uniq.csv -23 | wc -l)" 
log "Moving the original files to data/withheaders and adding the headers back now that they are sorted."
rm data/user_data_sample.csv data/end_song_sample.csv

mv data/user_data_sample_uniq.csv data/user_data_sample.csv
mv data/end_song_sample_uniq.csv data/end_song_sample.csv
cat data/user_data_sample.csv >> data/withheader/user_data_sample.csv
cat data/end_song_sample.csv >> data/withheader/end_song_sample.csv
