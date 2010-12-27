#!/bin/sh

usage () {
    echo "usage: $0 <launcher|menu|dialog> <filename-without-ext>" >&2
    exit 0
}

if [ -z "$1" -o -z "$2" ]; then
    usage
fi
TYPE=$1
FILE=$2

INKSCAPE=inkscape
OUTPATH=./res/drawable-

case $TYPE in
    launcher)
	DIMS="ldpi=36 mdpi=48 hdpi=72"
	;;

    menu)
	DIMS="ldpi=36 mdpi=48 hdpi=72"
	;;

    dialog)
	DIMS="ldpi=24 mdpi=32 hdpi=48"
	;;

    *)
	usage
	;;
esac

for DIM in $DIMS; do
    SUFFIX=`expr "$DIM" : '\(.*\)=.*'`
    SIZE=`expr "$DIM" : '.*=\(.*\)'`

    echo "exporting to $OUTPATH$SUFFIX/$FILE.png (${SIZE}x${SIZE})..."
    $INKSCAPE --export-png="$OUTPATH$SUFFIX/$FILE.png" --export-area-page \
	--export-width=$SIZE --export-height=$SIZE "./imgsrc/$FILE.svg"
done

