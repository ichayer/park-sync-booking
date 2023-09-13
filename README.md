
<div>
  <div align="right">
    <img src="https://github.com/ichayer/park-sync-booking/actions/workflows/maven.yml/badge.svg" alt="Java CI with Maven">
  </div>

  <!-- Las otras tres badges a la izquierda -->
  <div>
    <img src="https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white">
    <img src="https://img.shields.io/badge/Apache%20Maven-C71A36?style=for-the-badge&logo=Apache%20Maven&logoColor=white">
    <img src="https://img.shields.io/badge/github-%23121011.svg?style=for-the-badge&logo=github&logoColor=white">
  </div>
</div>


# Authors
- [Camila Di Toro](https://github.com/camilaDiToro)
- [Iván Chayer](https://github.com/ichayer)
- [Thomas Mizrahi](https://github.com/ThomasMiz)

# Instalation
Run this command to compile the project with `Maven`:
```bash
mvn clean install
```
Extract `tar.gz` from `target` folders:
```bash
mkdir -p tmp/csv && find . -name '*tar.gz' -exec tar -C tmp -xzf {} \;
find . -path './tmp/tpe1-g4-*/*' -exec chmod u+x {} \;
```
This will extract the generated `.tar.gz` files, storing them in a temporary directory `./tmp`, and grant execution permissions to both the client and server `.sh` files. It will also create a `csv` folder to place csv's in it.

## Run server
```bash
cd ./tmp/tpe1-g4-server-2023.1Q/ && ./run-server.sh
```
## Run admin client
```bash
cd ./tmp/tpe1-g4-client-2023.1Q/ && ./admin-cli -DserverAddress=xx.xx.xx.xx:yyyy -Daction=actionName [ -DinPath=filename | -Dride=rideName | -Dday=dayOfYear | -Dcapacity=amount ]
```
## Run booking client
```bash
cd ./tmp/tpe1-g4-client-2023.1Q/ && ./book-cli -DserverAddress=xx.xx.xx.xx:yyyy -Daction=actionName [ -Dday=dayOfYear -Dride=rideName -Dvisitor=visitorId -Dslot=bookingSlot -DslotTo=bookingSlotTo ]
```
## Run notification client
```bash
cd ./tmp/tpe1-g4-client-2023.1Q/ && ./notif-cli -DserverAddress=xx.xx.xx.xx:yyyy -Daction=actionName -Dday=dayOfYear -Dride=rideName -Dvisitor=visitorId
```
## Run query client
```bash
cd ./tmp/tpe1-g4-client-2023.1Q/ && ./query-cli -DserverAddress=xx.xx.xx.xx:yyyy -Daction=actionName -Dday=dayOfYear -DoutPath=output.txt
```

## Examples
Sample scripts are provided in the `scripts` folder. Update script flags values to match your environment.

> Note: this scripts must be executed from the root of the project.

Add executions permissions:
```bash
find . -path './scripts/*' -exec chmod u+x {} \;
```

Run server:
```bash
./scripts/run-server.sh
```

Add attractions:
```bash
cd ./scripts/adminClientScripts/addAttractions.sh
```

Add capacity for attraction:
```bash
cd ./scripts/adminClientScripts/addCapacity.sh
 ```

Add tickets:
```bash
cd ./scripts/adminClientScripts/addTickets.sh
```

Get loaded attractions info:
```bash
cd ./scripts/bookingClientScripts/attractionsInfo.sh
```

Get attraction(s) availability(ies):
```bash
cd ./scripts/bookingClientScripts/availability.sh
```

Book:
```bash
cd ./scripts/bookingClientScripts/book.sh
```

Confirm reservation:
```bash
cd ./scripts/bookingClientScripts/confirm.sh
```

Cancel reservation:
```bash
cd ./scripts/bookingClientScripts/cancel.sh
```

Receive notifications from modifications in attraction bookings:
```bash
cd ./scripts/notificationsClientScripts/suscribe.sh
```

Unscribe from notifications:
```bash
cd ./scripts/notificationsClientScripts/unsuscribe.sh
```

Query suggested capacity:
```bash
cd ./scripts/queryClientScripts/suggestedCapacity.sh
```

Query confirmed reservations:
```bash
cd ./scripts/queryClientScripts/confirmedReservations.sh
```

# Project description
This project focuses on the development of thread-safe system for managing and booking attractions in an theme park. It includes remote services for park management, attraction reservations, and receiving notifications about changes in reservations. It also provides the ability to make queries about suggested capacity and confirmed reservations.

## Park Administration Service
  - Add an attraction to the park using its name, opening and closing hours, and the duration of slots for reservations. Fails if an attraction with that name already exists, if the provided time values are invalid, if the minutes are not positive, or if there's no possible slot with the given values.
  - Add an attraction pass sold based on the visitor's ID (in UUID format), the pass type, and the day of the year for its validity. Visitors must have a valid pass for the specified day of the year to reserve an attraction. There are three types of passes: `UNLIMITED`, `THREE`, and `HALF_DAY`.
  - Load the capacity of slots for an attraction based on the attraction's name, a day of the year, and a capacity. Fails if the attraction doesn't exist, if the day is invalid, if the capacity is negative, or if the capacity has already been loaded for that attraction and day. The capacity applies to all slots for the specified day and attraction.

## Attractions Reservation Service
  - Query attractions in the park, providing the name, opening time, and closing time for each attraction.
  - Check the availability of attractions, indicating the number of reservations (pending and confirmed) and the capacity of slots for the attraction (if loaded), based on the following criteria:
    - One slot for an attraction, using the day of the year, the attraction name, and the slot time (in HH:MM format).
    - A range of slots for an attraction, using the day of the year, the attraction name, and the two slot values defining the range (both in HH:MM format).
    - A range of slots for all attractions in the park, using the day of the year, and the two slot values defining the range (both in HH:MM format).
- Reserve an attraction based on the attraction name, day of the year, reservation slot (in HH:MM format), and visitor ID (in UUID format). Fails if the reservation already exists, if the attraction can't be reserved according to pass type restrictions, if the attraction doesn't exist, if the day is invalid, if the slot is invalid, or if the visitor doesn't have a valid pass for that day.
- Confirm a pending reservation for an attraction based on the attraction name, day of the year, reservation slot (in HH:MM format), and visitor ID (in UUID format). Fails if the slot capacity for the attraction on that day hasn't been loaded, if the reservation is already confirmed, if there's no reservation made for the attraction with that pass, if the attraction doesn't exist, if the day is invalid, if the slot is invalid, or if the visitor doesn't have a valid pass for that day.
- Cancel a reservation for an attraction based on the attraction name, day of the year, reservation slot (in HH:MM format), and visitor ID (in UUID format). Both pending and confirmed reservations can be canceled. Fails if there's no reservation made for the attraction with that pass, if the attraction doesn't exist, if the day is invalid, if the slot is invalid, or if the visitor doesn't have a valid pass for that day.

## Attraction Notifications Service
  - Register a visitor to be notified of events related to a reservation, based on the attraction name, visitor ID (in UUID format), and day of the year for the pass. Fails if the attraction doesn't exist, if the day is invalid, if the visitor doesn't have a valid pass for that day, or if the visitor was already registered to be notified for that attraction on that day.
  - Unregister a visitor to stop receiving notifications for the attraction, based on the attraction name, visitor ID (in UUID format), and day of the year for the pass. Fails if the visitor is not registered to receive notifications for an attraction, if the attraction doesn't exist, if the day is invalid, or if the visitor doesn't have a valid pass for that day.

## Query Service
  - Query the suggested capacity, based on pending reservations, for all attractions in the park for a day. For each attraction, provide the attraction name, suggested capacity (maximum number of pending reservations for all slots of the attraction), and the corresponding slot (in HH:MM format), in descending order by suggested capacity, based on the day of the year. Fails if the day is invalid. If the attraction already has a loaded capacity, it should not be listed in the query.
  - Query confirmed reservations for all attractions in the park for a day. For each reservation, provide the attraction name, visitor ID (in UUID format), and slot (in HH:MM format), in order of reservation confirmation, based on the day of the year. Fails if the day is invalid.

## Clients
The project includes four client programs, each corresponding to a remote service.
- `Park Management Client:` Allows executing actions related to park management, such as adding attractions, passes, or loading slot capacity.
- `Reservation Client:` Allows making queries and reservations for attractions, as well as confirming and canceling reservations.
- `Notification Client:` Allows registering and canceling registration to receive notifications about events related to a reservation.
- `Query Client:` Generates TXT files with results of queries about suggested capacity and confirmed reservations for attractions.

## Considerations
- There's no persistence of data between executions.
- It is assumed that the format and content of the input files are correct and do not need validation.
- The number of days in the year is set at 365.