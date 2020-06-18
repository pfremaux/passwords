#!/bin/bash
gradle :commons-lib:build :passwords-app:build :makeBuild -Pdev && java -jar ./build/libs/password-store-dev.jar --kdir . --sdir . --cli true
