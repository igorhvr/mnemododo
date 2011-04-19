#
# Other useful Android tools:
#	ddms
#	hierarchyviewer
#	traceview / dmtracedump
#		
#		... (to /sdcard/output.trace) ...
#
# Lock screen	F7
# Rotate screen Ctrl-F11 / Ctrl-F12
#

MNEMOGOGO=$(HOME)/.mnemosyne/plugins/mnemogogo
EXPORTDIR=libs/export
TMPDIR=$(HOME)/tmp

ANT=/usr/bin/ant
RM=rm
LN=ln -s
ETAGS=etags

ANDROIDSDK=/opt/android-sdk-linux_x86

EMULATOR=$(ANDROIDSDK)/tools/emulator
EMU_OPTIONS= -no-boot-anim -no-skin

ADB=$(ANDROIDSDK)/platform-tools/adb
LOGCAT_LEVEL=d
MKSDCARD=$(ANDROIDSDK)/tools/mksdcard
SDCARD=libs/sdcard.iso

# run: android list
AVD?=basic1.6

.PHONY: clean cleanall listkeys sdcard debug release

debug: setup
	$(ANT) debug
	@if [ `$(ADB) devices | egrep -v 'List of devices attached|^$$' | wc -l` -eq 1 ]; then \
	    echo "Installing Mnemododo-debug.apk"; \
	    $(ADB) install -r bin/Mnemododo-debug.apk; \
	fi

release: setup
	$(ANT) release

emulator: sdcard
	$(EMULATOR) -help-keys
	$(EMULATOR) -avd $(AVD) -sdcard $(SDCARD) \
	    -logcat $(LOGCAT_LEVEL) $(EMU_OPTIONS)

push:
	$(ADB) push $(EXPORTDIR) /sdcard/cards

pull:
	$(ADB) pull /sdcard/cards $(EXPORTDIR)

analyze:
	@echo "---layoutopt"
	@$(ANDROIDSDK)/tools/layoutopt res/layout

traceview:
	@echo "Inside app:"
	@echo "\tandroid.os.Debug.startMethodTracing(\"mnemododo\");"
	@echo "\t..."
	@echo "\tandroid.os.Debug.stopMethodTracing();"
	$(ADB) pull /sdcard/mnemododo.trace $(TMPDIR)
	$(ANDROIDSDK)/tools/traceview $(TMPDIR)/mnemododo

jdb:
	@echo "Start the app..."
	sleep 5
	adb forward tcp:8000 jdwp:`adb jdwp | tail -1`
	jdb -attach localhost:8000

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

setup: libs/mnemogogo-android.jar icons

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
	$(LN) `ls $(MNEMOGOGO)/mnemogogo-android-?.?.?.jar | tail -1` libs/mnemogogo-android.jar

listkeys:
	keytool -list -keystore libs/mnemododo.keystore

tags:
	$(ETAGS) -f tags \
	    src/org/tbrk/mnemododo/*.java \
	    $(MNEMOGOGO)/mobile/hexcsv/*.java

# Icons

icons:	res/drawable-mdpi/icon_categories.png \
	res/drawable-mdpi/icon_schedule.png \
	res/drawable-mdpi/icon_skip.png \
	res/drawable-mdpi/icon_stats.png \
	res/drawable-mdpi/icon_dia_bright.png \
	res/drawable-mdpi/icon_dia_dark.png

res/drawable-mdpi/icon_categories.png: imgsrc/icon_categories.svg
	./imgsrc/makeicons.sh menu icon_categories

res/drawable-mdpi/icon_schedule.png: imgsrc/icon_schedule.svg
	./imgsrc/makeicons.sh menu icon_schedule

res/drawable-mdpi/icon_skip.png: imgsrc/icon_skip.svg
	./imgsrc/makeicons.sh menu icon_skip

res/drawable-mdpi/icon_stats.png: imgsrc/icon_stats.svg
	./imgsrc/makeicons.sh menu icon_stats

res/drawable-mdpi/icon_dia_bright.png: imgsrc/icon_dia_bright.svg
	./imgsrc/makeicons.sh dialog icon_dia_bright

res/drawable-mdpi/icon_dia_dark.png: imgsrc/icon_dia_dark.svg
	./imgsrc/makeicons.sh dialog icon_dia_dark

# Translations

set-cn:
	$(ADB) -e shell \
	    'setprop persist.sys.language zh;setprop persist.sys.country CN;stop;sleep 5;start'
set-da:
	$(ADB) -e shell \
	    'setprop persist.sys.language da;setprop persist.sys.country DK;stop;sleep 5;start'
set-de:
	$(ADB) -e shell \
	    'setprop persist.sys.language de;setprop persist.sys.country DE;stop;sleep 5;start'
set-en:
	$(ADB) -e shell \
	    'setprop persist.sys.language en;setprop persist.sys.country US;stop;sleep 5;start'

set-es:
	$(ADB) -e shell \
	    'setprop persist.sys.language es;setprop persist.sys.country ES;stop;sleep 5;start'

set-fr:
	$(ADB) -e shell \
	    'setprop persist.sys.language fr;setprop persist.sys.country FR;stop;sleep 5;start'
set-hr:
	$(ADB) -e shell \
	    'setprop persist.sys.language hr;setprop persist.sys.country HR;stop;sleep 5;start'
set-nl:
	$(ADB) -e shell \
	    'setprop persist.sys.language nl;setprop persist.sys.country NL;stop;sleep 5;start'
set-pl:
	$(ADB) -e shell \
	    'setprop persist.sys.language pl;setprop persist.sys.country PL;stop;sleep 5;start'

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
	-@$(RM) gen/org/tbrk/mnemododo/R.java

cleanall: clean
	-@$(RM) libs/mnemogogo-android.jar
	-@$(RM) bin/mnemododo.apk
	-@$(RM) bin/Mnemododo-debug.apk
	-@$(RM) bin/Mnemododo-release.apk
	-@$(RM) bin/Mnemododo-unsigned.apk

