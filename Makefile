
MNEMOGOGO=$(HOME)/.mnemosyne/plugins/mnemogogo
EXPORTDIR=libs/export

ANT=/usr/bin/ant
RM=rm
LN=ln -s
ETAGS=etags

ANDROIDSDK=/opt/android-sdk-linux_x86

EMULATOR=$(ANDROIDSDK)/tools/emulator
EMU_OPTIONS=#-noskin

ADB=$(ANDROIDSDK)/tools/adb
LOGCAT_LEVEL=d
MKSDCARD=$(ANDROIDSDK)/tools/mksdcard
SDCARD=libs/sdcard.iso

# run: android list
AVD?=blah1.5

.PHONY: clean cleanall listkeys sdcard debug release

debug: setup
	$(ANT) debug
	@if [ `$(ADB) devices | egrep -v 'List of devices|^$$' | wc -l` -eq 1 ]; then \
	    echo "Installing Mnemododo-debug.apk"; \
	    $(ADB) install -r bin/Mnemododo-debug.apk; \
	fi

release: setup
	$(ANT) release

emulator: sdcard
	$(EMULATOR) -avd $(AVD) -sdcard $(SDCARD) \
	    -no-boot-anim -logcat $(LOGCAT_LEVEL) $(EMU_OPTIONS)

push:
	$(ADB) push $(EXPORTDIR) /sdcard/cards

pull:
	$(ADB) pull /sdcard/cards $(EXPORTDIR)

install-release: bin/Mnemododo-release.apk
	$(ADB) install bin/Mnemododo-release.apk

install-debug: bin/Mnemododo-debug.apk
	$(ADB) install bin/Mnemododo-debug.apk

uninstall:
	$(ADB) uninstall org.tbrk.mnemododo

bin/Mnemododo-debug.apk: setup
	$(ANT) debug

bin/Mnemododo-release.apk: setup
	$(ANT) release

setup: libs/mnemogogo-android.jar

mountsdcard: sdcard
	mkdir -p libs/sdcard
	sudo mount -t vfat -o loop,shortname=mixed $(SDCARD) libs/sdcard

umountsdcard:
	sudo umount libs/sdcard
	rmdir libs/sdcard

sdcard:
	if [ ! -e $(SDCARD) ]; then \
	    $(MKSDCARD) 512MB $(SDCARD); \
	fi

libs/mnemogogo-android.jar:
	$(LN) $(MNEMOGOGO)/mnemogogo-android-?.?.?.jar libs/mnemogogo-android.jar

listkeys:
	keytool -list -keystore libs/mnemododo.keystore

tags:
	$(ETAGS) -f tags \
	    src/org/tbrk/mnemododo/*.java \
	    $(MNEMOGOGO)/mobile/hexcsv/*.java

# Translations

set-cn:
	$(ADB) -e shell \
	    'setprop persist.sys.language zh;setprop persist.sys.country CN;stop;sleep 5;start'
set-de:
	$(ADB) -e shell \
	    'setprop persist.sys.language de;setprop persist.sys.country DE;stop;sleep 5;start'
set-en:
	$(ADB) -e shell \
	    'setprop persist.sys.language en;setprop persist.sys.country US;stop;sleep 5;start'
set-fr:
	$(ADB) -e shell \
	    'setprop persist.sys.language fr;setprop persist.sys.country FR;stop;sleep 5;start'
set-hr:
	$(ADB) -e shell \
	    'setprop persist.sys.language hr;setprop persist.sys.country HR;stop;sleep 5;start'
set-nl:
	$(ADB) -e shell \
	    'setprop persist.sys.language nl;setprop persist.sys.country NL;stop;sleep 5;start'
set-tw:
	$(ADB) -e shell \
	    'setprop persist.sys.language zh;setprop persist.sys.country TW;stop;sleep 5;start'

# Clean

clean:
	-@$(RM) bin/Mnemododo.ap_
	-@$(RM) bin/Mnemododo-debug-unaligned.apk
	-@$(RM) bin/Mnemododo-unaligned.apk
	-@$(RM) bin/resources.ap_
	-@$(RM) bin/classes.dex
	-@$(RM) bin/classes/org/tbrk/mnemododo/*.class

cleanall: clean
	-@$(RM) libs/mnemogogo-android.jar
	-@$(RM) bin/mnemododo.apk
	-@$(RM) bin/Mnemododo-debug.apk
	-@$(RM) bin/Mnemododo-release.apk
	-@$(RM) bin/Mnemododo-unsigned.apk

