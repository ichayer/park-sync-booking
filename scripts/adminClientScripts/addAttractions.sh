#!/bin/bash

source ../utils.sh

export CURRENT_DIR=`pwd`
check_and_extract

./admin-cli -DserverAddress=localhost:50051 -Daction=rides -DinPath=$CURRENT_DIR/attractions.csv