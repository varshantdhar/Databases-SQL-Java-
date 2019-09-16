# DB DDL, Indexes, and Bulk Loading

In this homework you are creating a database schema, bulk loading data, and creating indexes for the data.
The data for this HW is pothole data from the City of Chicago (note this is a trimmed version of the dataset).

You will be reading and writing from a private database hosted on a private managed VM.
Please note that you will need to use your CNET ID and password to access the VM.
**Note, you will only be able to access the VMs from the campus network.**
This means to ssh into the VM you will need to physically be on campus (and on at least
uchicago-secure if wireless), first ssh into a machine on campus and ssh from there (e.g. ssh into csil machine first),
or use the UChicago VPN.  You will be provided with your VM hostname and ssh with
`ssh yourcnetid@vm-XX-XXX-XX-XXX`
This VM will mount your CSIL home directory, so you can easily move files in and out.

Once in the VM, you will primarily interact with the database via psql. You should simply be able to
run `psql` and you will be connected to a database that has the same name as your ucnetid.
You can run sql in psql by creating a file (eg myquery.sql) with the SQL statement and then
passing -f to psql (`psql -f myquery.sql`).


### Dataset
Get the dataset from
```
http://people.cs.uchicago.edu/~aelmore/class/311_Service_Requests_-_Pot_Holes_Reported.tsv
```
You can easily download this on the vm using wget:
```
wget http://people.cs.uchicago.edu/~aelmore/class/311_Service_Requests_-_Pot_Holes_Reported.tsv
```


## CREATE and DROP a Table (30%)

You will need to write SQL statements for creating and dropping a table called `potholes`. In order to determine the right
datatypes for the table, examine the TSV (tab separated value file) and look at postgresql's data types https://www.postgresql.org/docs/10/datatype.html. Add your SQL statements to createPotholeTable.sql and dropPotholeTable.sql  files. This requires you to create a SQL statement for both tasks.
This table is the only table involved for the homework. Use the same attribute names from the TSV header.
Your schema will likely change after you try to load the data. Use varchar / char only when necessary


## Load data via copy (30%)
Implement loadPotholes.sql that loads TSV file into your table.  Use the COPY command to do this
https://www.postgresql.org/docs/10/sql-copy.html  
Some protips here: wrap the file path in single quotes, E'\t' is a single byte character for tab and
you will need to use `\copy` and not `copy`.

## Add Indexes (20%)
Implement createPotholeIndexes.sql to add *at least three* indexes to your table that you imagine would
speed up the following query predicates that are uniformly selected.
Modify the existing table here (i.e. assume you are adding indexes to an existing table, not specifying the
  indexes on table creation, look at alter table).

The universe of possible predicates (p1,p2) and their operations (comparison) is bound to:
 - p1 ZIP =
 - p1 LAT > , p2 LAT <
 - p1 LONG >, p2 LONG <
 - p1 ZIP =, p2 NUM POTHOLES >
 - p1 MOST RECENT ACTION =  

Note the second predicate may be null/ not present.

## Measure the loading and indexing time latency (20%)
To measure the time a linux command takes, prefix it with time.  So if I wanted to test the time to load
the pothole data I would run the command:
`time psql -f loadPotholes.sql`

This shows the output and wall clock run time as real:
```
COPY 560478

real    0m2.958s
user    0m0.149s
sys     0m0.091s
```
You should measure the following three things, and report the mean wallclock (real) time of at least 3 runs. Do not forget to drop the table in between each run.

 - Cost of loading data with no indexes
 - Cost of loading data with indexes
 - Cost of adding indexes after data is loaded

 This process can be very easily put into a bash script or run manually.  

## To submit
Please add a very short write up in mywriteup.txt that includes what indexes you added and why, and mean times as described in the prior section.


### Asking Questions
*Many* problems for this homework will be solved with a quick Google/stack overflow /stack exchange search. If the problem is not postgresql related, when you post a question please list at least one search term that you looked for the answer using.
