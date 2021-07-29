#!/bin/bash
gradle build uberJar -Pdev && java -DhintIsInIde=false -jar ./build/libs/password-store-SNAPSHOT-0.0.0.jar --kdir . --sdir . --cli false
