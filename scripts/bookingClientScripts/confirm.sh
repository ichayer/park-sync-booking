#!/bin/bash

source ../utils.sh

check_and_extract

./book-cli -DserverAddress=localhost:50051 -Daction=confirm -Dday=200 -Dattraction=SplashMountain -Dslot=09:00 -Dvisitor=a7a90a76-bc4d-418d-8cbf-5eaf41e69bea