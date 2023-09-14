#!/bin/bash

./scripts/query-cli -DserverAddress=localhost:50051 -Daction=capacity -Dday=dayOfYear -DoutPath="$PWD/output.txt"