#!/bin/bash

source ../utils.sh

check_and_extract

./book-cli -DserverAddress=localhost:50051 -Daction=book -Dday=day -Dride=ride -Dslot=slot -Dvisitor=visitor
