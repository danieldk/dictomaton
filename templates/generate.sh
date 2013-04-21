#!/bin/bash

BASEDIR="../src/main/java/eu/danieldk/dictomaton/collections"

generate()
{
  TEMPLATE=$1
  UNBOXED=$2
  BOXED=$3
  NAME=$4
  OUT=$5

  cat $1 | sed "s/##UNBOXED_TYPE##/$UNBOXED/g" | \
    sed "s/##BOXED_TYPE##/$BOXED/g" | \
    sed "s/##TYPE_NAME##/$NAME/g" > "$BASEDIR/$OUT"
}


generate 'ImmutableStringTYPEMap.t' 'boolean' 'Boolean' 'Boolean' 'ImmutableStringBooleanMap.java'
generate 'ImmutableStringTYPEMap.t' 'byte' 'Byte' 'Byte' 'ImmutableStringByteMap.java'
generate 'ImmutableStringTYPEMap.t' 'char' 'Character' 'Char' 'ImmutableStringCharMap.java'
generate 'ImmutableStringTYPEMap.t' 'int' 'Integer' 'Int' 'ImmutableStringIntMap.java'
generate 'ImmutableStringTYPEMap.t' 'long' 'Long' 'Long' 'ImmutableStringLongMap.java'
generate 'ImmutableStringTYPEMap.t' 'short' 'Short' 'Short' 'ImmutableStringShortMap.java'
generate 'ImmutableStringTYPEMap.t' 'float' 'Float' 'Float' 'ImmutableStringFloatMap.java'
generate 'ImmutableStringTYPEMap.t' 'double' 'Double' 'Double' 'ImmutableStringDoubleMap.java'
