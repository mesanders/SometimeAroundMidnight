#/bin/bash

function log {
	echo "$(date "+%D %T") INFO: $1"  
}

function run_scala_package() {
	mvn --version >/dev/null 2>&1
	if [ $? -eq 0 ]; then
		log "Compiling package"
		mvn package
		log "Running main class:"
		JAVA_OPTS="-Xmx1G" scala target/spotifychallenge-1.0-SNAPSHOT.jar 
	fi
}

case $1 in 
	run)
		run_scala_package
		exit 0
		;;
	*)
		;;
esac

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
DATADIR=${DIR}/data
HEADERDIR=${DATADIR}/withheader
ZIPDATA=data_sample.tgz
USERDATA=user_data_sample.csv
SONGDATA=end_song_sample.csv

log "Downloading files"
wget https://storage.googleapis.com/ml_take_home/data_sample.tgz

log "Creating data directory and moving data sample to new directory."
mkdir -p $HEADERDIR
mv ${ZIPDATA} ${DATADIR}

log "Extracting data_sample into the data driectory"
tar -xzf ${DATADIR}/${ZIPDATA} -C ${DATADIR}

log "Removing top line of each file: "
head -1 ${DATADIR}/${SONGDATA} > ${HEADERDIR}/${SONGDATA}
head -1 ${DATADIR}/${USERDATA} > ${HEADERDIR}/${USERDATA} 
sed -i '1d' ${DATADIR}/${USERDATA}
sed -i '1d' ${DATADIR}/${SONGDATA}

log "Sorting each file and removing any duplicates."
sort ${DATADIR}/${SONGDATA} -o ${DATADIR}/${SONGDATA}
uniq ${DATADIR}/${SONGDATA} > ${DATADIR}/end_song_sample_uniq.csv
sort ${DATADIR}/${USERDATA} -o ${DATADIR}/${USERDATA} 
uniq ${DATADIR}/${USERDATA} > ${DATADIR}/user_data_sample_uniq.csv

log "Number of duplicates in end_song_sample: $(comm ${DATADIR}/${SONGDATA} data/end_song_sample_uniq.csv -23 | wc -l)"
log "Number of duplicates in user_data_sample: $(comm ${DATADIR}/${USERDATA} data/user_data_sample_uniq.csv -23 | wc -l)" 
log "Moving the original files to data/withheaders and adding the headers back now that they are sorted."
rm ${DATADIR}/${USERDATA} ${DATADIR}/${SONGDATA}

mv ${DATADIR}/user_data_sample_uniq.csv ${DATADIR}/${USERDATA}
mv ${DATADIR}/end_song_sample_uniq.csv ${DATADIR}/${SONGDATA}
cat ${DATADIR}/${USERDATA} >> ${HEADERDIR}/${USERDATA}
cat ${DATADIR}/${SONGDATA} >> ${HEADERDIR}/${SONGDATA}

sqlite3 --version >/dev/null 2>&1
if [ $? -eq 0 ]; then
	log "Launching sqlite3 script" 
	sqlite3 < lite_analysis.sql
fi

run_scala_package()
