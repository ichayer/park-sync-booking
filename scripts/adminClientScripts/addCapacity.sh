#!/bin/bash

source ../utils.sh

check_and_extract

./admin-cli -DserverAddress=localhost:50051 -Daction=slots -Dride=ride -Dday=day -Dcapacity=capacity