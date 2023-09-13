#!/bin/bash

source ../utils.sh

export CURRENT_DIR=`pwd`
check_and_extract

./admin-cli -DserverAddress=localhost:50051 -Daction=tickets -DinPath=$CURRENT_DIR/passes.csv