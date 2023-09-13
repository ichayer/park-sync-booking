#!/bin/bash

cd ./tmp/tpe1-g4-client-2023.1Q/ && ./query-cli -DserverAddress=localhost:50051 -Daction=capacity -Dday=dayOfYear -DoutPath=./../output.txt