# DbChecko #

DbChecko is a tool that enables you to connect to the database and execute sql commands from command line. Typical usage is verify if the database is reachable from the client which is on server that has no GUI. Another one is to print simple data from some table.

## Build

Download or clone the repo. To build the application you need **JDK 8** and **Maven 3.6**. In the project's root directory run:

```shell script
mvn clean package
```

## Usage

```
Usage: dbchecko [-hV] [COMMAND]
  -h, --help      Show this help message and exit.
  -V, --version   Print version information and exit.
Commands:
  check   Checks if it is possible to connect to the database.
  select  Allows you to run a select sql command.
  update  Allows you to run an update sql command.
```

### Check command

Verifies if the database is reachable.

Example Windows:

```batch
java -cp target\db-checko.jar;drivers\* DbCheckoApp check -p database.properties
```

Example Linux:

```shell script
java -cp target/db-checko.jar:drivers/* DbCheckoApp check -p database.properties
```

Because **check** command is the most often usage, there are helper scripts for this command `dbchecko.bat`/`dbchecko.sh`.

### Select command

Prints simple data from a table.

Example Windows:

```batch
java -cp target\db-checko.jar;drivers\* DbCheckoApp select -p database.properties -s "select userid, screenname from user_"
```

Example Linux:

```shell script
java -cp target/db-checko.jar:drivers/* DbCheckoApp select -p database.properties -s "select userid, screenname from user_"
```

## JDBC drivers

There are **postgresql** and **mssql** JDBC drivers prepared by default. If you need to use other JDBC driver, create `drivers` directory (if doesn't exist) in the project's root directory and copy your driver here.

## Configuration

You can specify connection configuration to your database by one of these options:

### Properties file

Use `-p` option and specify path to properties file. It has to have these properties:

* `jdbc.default.driverClassName` - driver's class name
* `jdbc.default.url` - a database connection URL
* `jdbc.default.username` - a username used to login
* `jdbc.default.password` - a password used to login

### Tomcat context file

Use `-c` option and specify path to tomcat context file. It has to have Resource with name `jdbc/LiferayPool`.

## Distributable package

To create a distributable zip package which you can upload wherever you want, use `dist` profile:

```shell script
mvn clean package -P dist
```
