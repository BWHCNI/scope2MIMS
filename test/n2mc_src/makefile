#
#
#

CFLAGS=-g

all : n2mc n2m2 n3mc n3m3

libmims.so : mims.o mims.h
	ld -G -o libmims.so mims.o 

mims.o : mims.c mims.h
	$(CC) $(CFLAGS) -c -o mims.o mims.c

tr : tr.c sims.h
	$(CC) $(CFLAGS)  -o tr tr.c

cmp : cmp.c sims.h
	$(CC) $(CFLAGS)  -o cmp cmp.c

esims : esims.c sims.h
	$(CC) $(CFLAGS)  -o esims esims.c -lm

nik2mims.o : nik2mims.c mims.h
	$(CC) $(CFLAGS)  -c -o nik2mims.o nik2mims.c 

nik2mims : nik2mims.o mims.h
	$(CC) $(CFLAGS)  -o nik2mims nik2mims.o mims.o calc.o -lm

n2mc.o : n2mc.c mims.h
	$(CC) $(CFLAGS)  -c -o n2mc.o n2mc.c

n2m2.o : n2m2.c mims.h
	$(CC) $(CFLAGS)  -c -o n2m2.o n2m2.c 

n3m3.o : n3m3.c mims.h
	$(CC) $(CFLAGS)  -c -o n3m3.o n3m3.c 

vpts2mims.o : vpts2mims.c mims.h
	$(CC) $(CFLAGS)  -c -o vpts2mims.o vpts2mims.c 

calc.o : calc.c mims.h
	$(CC) $(CFLAGS)  -c -o calc.o calc.c 

n2m2 : n2m2.o mims.o calc.o
	$(CC) $(CFLAGS)  -o n2m2 n2m2.o calc.o mims.o -lm

n2mc : n2mc.o mims.o calc.o
	$(CC) $(CFLAGS)  -o n2mc n2mc.o calc.o mims.o -lm

n3mc : n3mc.o mims.o calc.o
	$(CC) $(CFLAGS)  -o n3mc n3mc.o calc.o mims.o -lm

n3m3 : n3m3.o mims.o calc.o
	$(CC) $(CFLAGS)  -o n3m3 n3m3.o calc.o mims.o -lm

vpts2mims : vpts2mims.o mims.o calc.o
	$(CC) $(CFLAGS)  -o vpts2mims vpts2mims.o calc.o mims.o -lm


