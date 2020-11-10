#! /bin/sh

ROOT=`dirname $0`

DEPS=""
for I in $ROOT/fitlayout*.jar; do
  DEPS=$I
done
for I in $ROOT/lib/*.jar; do
  DEPS=$DEPS:$I
done

java -p $DEPS -m cz.vutbr.fit.layout.ide/cz.vutbr.fit.layout.ide.Browser $1 $2 $3 $4 
