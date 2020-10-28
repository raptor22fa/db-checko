@echo off

if "%~1"=="" (
    echo Usage: %0 properties/resource
    goto :eof
)

set FILE_PATH=%1
if [%FILE_PATH:~-10%] == [properties] (
    java -cp target\db-checko.jar;drivers\* cz.raptor22fa.dbchecko.cmd.DbCheckoApp check -p %1
)
