## Mappings
* / - main page
* /admin - represents admin features
* /request - a page to create new requests
* /client - represents client features
* /performer - represents performer features
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
### Client(allows to process only requests valid for the client)
#### api/client/request
* Http method GET: to get all requests created by a logged in client
* Http method POST: to create a new request with logged in user as a client
#### api/client/request/{id}
* Http method PUT: to update request description field
### Moderator(allows to process only requests valid for the moderator)
#### api/moderator/request
* Http method GET: to get all requests
#### api/moderator/request/{id}
* Http method DELETE: to delete a request
### Performer(allows to process only requests valid for the performer)
#### api/department/request
* Http method GET: to get all requests addressed to the department of the currently logged in performer
#### api/performer/request
* Http method GET: to get all requests currently logged in performer is assigned for
#### api/performer/request/{id}
* Http method PUT: to update request status, performer and comment fields
 ## Database
### Contains three types of entities - user, role and request
#### For now there are no connection inside the DB
 ## UI
### /admin mapping
* By default shows lists of all users and roles
* Provides input forms to create  new users and roles
* Provides buttons to edit or delete a certain user or role
### /performer mapping
* Provides a list of all requests for the department of the currently logged in performer
* Allows to get assigned for any free request
* Providesa a list of all requests for which the currently logged in performer is assigned
* Allows to change status, performer or comment of any request currently logged in performer is assigned for
### /client mapping
* Provides a list of all requests, the author of which is the currently logged in client
* Allows to change description of any request, the author of which is the currently logged in client
### /request mapping
* Allows to choose a department from hardcoded list
* Allows to choose a template request for chosen department or create a new request
* Provides an input form to insert your request details
