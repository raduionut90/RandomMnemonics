#!/bin/sh
Server_Dir=$(cd "`dirname $0`/.." && pwd)
java -cp $Server_Dir/lib/mnemonic-1.0-SNAPSHOT.jar:"$Server_Dir/lib/*":"$Server_Dir/config" org.example.CheckMnemonic