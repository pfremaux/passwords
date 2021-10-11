#!/bin/bash
gradle build uberJar -Pdev && java -DhintIsInIde=false -jar ./build/libs/password-store-SNAPSHOT-0.2.0.jar --kdir . --sdir . --cli false
