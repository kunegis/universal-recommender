
COMBINATIONS_VECTOR := $(foreach I, Byte Char Int, $(I)Byte $(I)Short $(I)Char $(I)Int $(I)Long $(I)Float $(I)Double)

COMBINATIONS_BINARY_VECTOR := Byte Char Int

COMBINATIONS_MATRIX := $(foreach I, ByteByte ByteChar ByteInt CharByte CharChar CharInt IntByte IntChar IntInt, $(I)Byte $(I)Short $(I)Char $(I)Int $(I)Long $(I)Float $(I)Double)

COMBINATIONS_BINARY_MATRIX := ByteByte ByteChar ByteInt CharByte CharChar CharInt IntByte IntChar IntInt

all: \
  $(foreach I, $(COMBINATIONS_VECTOR), MemoryVector$(I).java) \
  $(foreach I, $(COMBINATIONS_BINARY_VECTOR), MemoryBinaryVector$(I).java) \
  $(foreach I, $(COMBINATIONS_MATRIX), MemoryMatrix$(I).java) \
  $(foreach I, $(COMBINATIONS_BINARY_MATRIX), MemoryBinaryMatrix$(I).java) 


MemoryVector%.java:  MemoryVector.java mk
	VALUES=$* NAMES=IndexValue ./mk <MemoryVector.java >MemoryVector$*.java

MemoryBinaryVector%.java:  MemoryBinaryVector.java mk
	VALUES=$* NAMES=Index ./mk <MemoryBinaryVector.java >MemoryBinaryVector$*.java

MemoryMatrix%.java:  MemoryMatrix.java mk
	VALUES=$* NAMES=IndexaIndexbValue ./mk <MemoryMatrix.java >MemoryMatrix$*.java

MemoryBinaryMatrix%.java:  MemoryBinaryMatrix.java mk
	VALUES=$* NAMES=IndexaIndexb ./mk <MemoryBinaryMatrix.java >MemoryBinaryMatrix$*.java

clean: ; rm -rf MemoryVector?*.java MemoryBinaryVector?*.java MemoryMatrix?*.java MemoryBinaryMatrix?*.java
.PHONY:  clean

