## Mappings
* / - main page
* /admin - represents admin features
* /request - a page to create new requests
* /login - custom login page
* /registration - temporary mapping; necessary for the application to work on a database without users
 ## API
 ### User
#### api/user
* Http method GET: to get all records from DB
* Http method POST: to create new record
#### api/user/{id}
* Http method GET: to get a record with certain id from DB
* Http method PUT: to update a record with certain id in DB
* Http method DELETE: to remove a record with certain id from DB
### Role
#### api/role
* Http method GET: to get all records from DB
* Http method POST: to create new record
#### api/role/{id}
* Http method GET: to get a record with certain id from DB
* Http method PUT: to update a record with certain id in DB
* Http method DELETE: to remove a record with certain id from DB
### Request
#### api/request
* Http method GET: to get all records from DB
* Http method POST: to create new record
 ## Database
### Contains three types of entities - user, role and request
#### For now there are no connection inside the DB
 ## UI
### /admin mapping
* By default shows lists of all users and roles
* Provides input forms to create  new users and roles
* Provides buttons to edit or delete a certain user or role
### /request mapping
* Allows to choose a department from hardcoded list
* Allows to choose a template request for chosen department or create a new request
* Provides an input form to insert your request details
