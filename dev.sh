# Dev build

PROJECT=esb-hl7

if [ ! -f ./setenv.sh ]; then
    echo "setenv.sh File not found! resorting to system Java"
else
    . ./setenv.sh
fi

# following helps the esb-parent script
rm -fr target/${PROJECT}*

mvn -Dmaven.wagon.http.ssl.insecure=true clean install -Dmaven.test.skip 

rc=$?
if [[ $rc -ne 0 ]] ; then
  echo 'exiting with mvn errors'; exit $rc
fi

