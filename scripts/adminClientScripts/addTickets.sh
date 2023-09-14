#!/bin/bash

./scripts/admin-cli -DserverAddress=localhost:50051 -Daction=tickets -DinPath="$PWD/scripts/adminClientScripts/passes.csv"