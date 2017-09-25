if [ ! -f ./setenv.sh ]; then
    echo "setenv.sh File not found! resorting to system Java"
else
    . ./setenv.sh
fi

echo ------------------------------------------------------------------------
echo Set the version from version.property
echo ------------------------------------------------------------------------

cd ~Dev/git/intamerge-github/esb-hl7
ant -Dgit_work_tree=/home/mwicks/Dev/git/intamerge-github/esb-hl7 -Dproject_loc=/home/mwicks/Dev/git/intamerge-github/esb-hl7 version

echo ------------------------------------------------------------------------
echo Set the license
echo ------------------------------------------------------------------------

ant -Dgit_work_tree=/home/mwicks/Dev/git/intamerge-github/esb-hl7 -Dproject_loc=/home/mwicks/Dev/git/intamerge-github/esb-hl7 headers
mvn license:format

echo ------------------------------------------------------------------------
echo Build
echo ------------------------------------------------------------------------

rm -fr target/esb-hl7*

# note -P community is the prod version for community release
mvn -Dmaven.wagon.http.ssl.insecure=true clean install -Dmaven.test.skip 


rc=$?
if [[ $rc -ne 0 ]] ; then
  echo 'exiting with mvn errors'; exit $rc
fi


