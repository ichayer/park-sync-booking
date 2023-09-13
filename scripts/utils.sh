#!/bin/bash

export CLIENTS_DIR="$HOME/park-sync-booking/client/target/tpe1-g4-client-2023.1Q"
export TARGET_DIR="$HOME/park-sync-booking/client/target"
export TAR_FILE="$HOME/park-sync-booking/client/target/tpe1-g4-client-2023.1Q-bin.tar.gz"

function check_and_extract() {

  if [ ! -d "$TARGET_DIR" ]; then
      echo "Directory $TARGET_DIR does not exist. Please run mvn clean install first."
      exit 1
  fi

  if [ ! -d "$CLIENTS_DIR" ]; then
      echo "Directory $CLIENTS_DIR does not exist. Extracting tar file..."
      tar -xzf "$TAR_FILE" -C "$HOME/park-sync-booking/client/target/" || exit
      echo "Tar file extracted successfully."
  else
      echo -e "Clients jar already extracted. Executing script...\n"
  fi

  cd "$CLIENTS_DIR" || exit
}