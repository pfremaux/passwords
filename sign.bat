cd build/libs
jarsigner -keystore .keystore password-store-SNAPSHOT-0.2.0.jar aliasdetest
move password-store-SNAPSHOT-0.2.0.jar ../../signed-password-store-SNAPSHOT-0.2.0.jar
cd ../..
