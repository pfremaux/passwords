# passwords
Implementation of a password manager. It allows you to encrypt in symetric and/or asymetric way. (not ready for production)

WIP don't try.

# Keystore sheet

`keytool -keystore .keystore -genkey -alias myAlias -keyalg DSA`

`keytool -list -keystore .keystore`

`jarsigner -keystore .keystore password-store-0.1.6.jar myAlias`

>The build process needs to be changed before we can sign the jar.

* test
 