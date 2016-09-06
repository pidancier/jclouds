#!/bin/bash
# Scrpt to deploy all the jcloud jars into gael's third party repository
count=0
for i in `find .. -name pom.xml `
do
   a=`dirname "$i"`;  
   for snap in $a/target/*SNAPSHOT.jar
   do
      if [ -f $snap ]
      then
         echo Installation of $snap...
         # mvn deploy:deploy-file -DpomFile=$i -Dfile=$snap -DrepositoryId=gael-third-party -Durl=http://repository.gael.fr:8081/nexus/content/repositories/thirdparty
         # third party repository only contains releases, snapshots are not supported
         # use gael-snapshot instead
         mvn deploy:deploy-file -DpomFile=$i -Dfile=$snap -DrepositoryId=gael-snapshots -Durl=http://repository.gael.fr:8081/nexus/content/repositories/snapshots
         echo Installation of $snap done.
         count=$((count+1))
      fi
   done
done
echo $count Files deployed onto Gael third party...
