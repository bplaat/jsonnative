# keytool -genkey -validity 10000 -keystore key.keystore -keyalg RSA -keysize 2048 -storepass jsonnative -keypass jsonnative
PATH=$PATH:../android-sdk/build-tools/28.0.2:../android-sdk/platform-tools
PLATFORM=../android-sdk/platforms/android-28/android.jar
if aapt package -m -J src -M AndroidManifest.xml -S res -I $PLATFORM; then
    mkdir build/classes
    if javac -Xlint -cp $PLATFORM -d build/classes src/com/example/jsonnative/*.java; then
        dx.bat --dex --output=build/classes.dex build/classes
        rm -r build/classes src/com/example/jsonnative/R.java build/jsonnative.apk
        aapt package -F build/jsonnative.apk -M AndroidManifest.xml -S res -I $PLATFORM build
        rm build/classes.dex
        apksigner.bat sign --ks key.keystore --ks-pass pass:jsonnative --ks-pass pass:jsonnative build/jsonnative.apk
        adb install -r build/jsonnative.apk
        adb shell am start -n com.example.jsonnative/.MainActivity
    else
        rm -r build/classes src/com/example/jsonnative/R.java
    fi
fi