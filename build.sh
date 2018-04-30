# Orignal build script from: https://medium.com/@authmane512/how-to-build-an-apk-from-command-line-without-ide-7260e1e22676
# Generate a key with: keytool -genkeypair -validity 365 -keystore key.keystore -keyalg RSA -keysize 2048
# Logcat: ../android-sdk/platform-tools/adb logcat jn:V AndroidRuntime:E *:S

PATH=$PATH:../android-sdk/build-tools/27.0.3:../android-sdk/platform-tools
PLATFORM=../android-sdk/platforms/android-19/android.jar

echo "Compiling..."
mkdir classes
javac -Xlint -d classes -bootclasspath $PLATFORM app/src/com/example/jsonnative/*.java
dx.bat --dex --output=classes.dex classes
rm -r classes

echo "Packing..."
aapt package -F jsonnative.unaligned.apk -M app/AndroidManifest.xml -I $PLATFORM
aapt add jsonnative.unaligned.apk classes.dex
zipalign -f 4 jsonnative.unaligned.apk jsonnative.apk
apksigner.bat sign --ks key.keystore --ks-pass pass:jsonnative --ks-pass pass:jsonnative jsonnative.apk
rm classes.dex jsonnative.unaligned.apk

echo "Running..."
adb install -r jsonnative.apk
adb shell am start -n com.example.jsonnative/.MainActivity