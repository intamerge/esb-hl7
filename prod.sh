if [ ! -f ./setenv.sh ]; then
    echo "setenv.sh File not found! resorting to system Java"
else
    . ./setenv.sh
fi

rm -fr target/esb-hl7*

# note -P community is the prod version for community release
mvn -Dmaven.wagon.http.ssl.insecure=true clean install -Dmaven.test.skip 


rc=$?
if [[ $rc -ne 0 ]] ; then
  echo 'exiting with mvn errors'; exit $rc
fi


