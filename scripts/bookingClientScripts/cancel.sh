#!/bin/bash

source ../utils.sh

check_and_extract

./book-cli -DserverAddress=localhost:50051 -Daction=cancel -Dday=day -Dattraction=attraction -Dslot=slot -Dvisitor=visitor