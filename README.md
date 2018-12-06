## Pages
* / - main page
* /admin - represents admin features
* /moderator - represents moderator features
* /performer - represents performer features
* /applicant - represents client features
* /request - a page to create new requests
* /login - custom login page
* /registration - temporary mapping; necessary for the application to work on a database without users
 ## API
 ### Department
#### api/department
* Http method GET: to get all departments
* Http method POST: to create a new department
#### api/department/{id}
* Http method GET: to get a department by id
* Http method PUT: to update a department
* Http method DELETE: to delete a department
 ### User
#### api/user
* Http method GET: to get all users
* Http method POST: to create a new user
#### api/user/{id}
* Http method GET: to get a user by id
* Http method PUT: to update a user
* Http method DELETE: to delete a user
### Role
#### api/role
* Http method GET: to get all roles
### Status
#### api/status
* Http method GET: to get all statuses
* Http method POST: to create a new status
#### api/status/{id}
* Http method GET: to get a status by id
* Http method PUT: to update a status
* Http method DELETE: to delete a status
### Request by applicant
#### api/applicant/request[?status={status}&department={department}]
* Http method GET: to get all requests(or filter them by status and(or) department) which were created by a logged in user 
* Http method POST: to create a new request with logged in user as an applicant
#### api/applicant/request/{id}
* Http method PUT: to update a request(cancel it or change description)
### Request by performer
#### api/performer/department/request
* Http method GET: to get all requests addressed to the department of the currently logged in user
#### api/performer/request[?status={status}]
* Http method GET: to get all requests currently logged in user is assigned for as a performer(or filter them by status)
#### api/performer/request/{id}
* Http method PUT: to update a request(change status, sigh up for it or change comment) 
### Request by Moderator
#### api/moderator/request[?status={status}&department={department}]
* Http method GET: to get all requests(or filter them by status and(or) department) 
#### api/moderator/request/{id}
* Http method PUT: to change a request(change status or update topic, description, comment or dealine)
* Http method DELETE: to delete a request

 ## Database
### Database schema
![Database schema](images/DB_schema_postgre.png)
 ## UI
 ### In Progress - does not work at all
### /admin page
* By default shows lists of all users and roles
* Provides input forms to create  new users and roles
* Provides buttons to edit or delete a certain user or role
### /performer page
* Provides a list of all requests for the department of the currently logged in performer
* Allows to get assigned for any free request
* Providesa a list of all requests for which the currently logged in performer is assigned
* Allows to change status, performer or comment of any request currently logged in performer is assigned for
### /client page
* Provides a list of all requests, the author of which is the currently logged in client
* Provides a button for each status available in DB to extract requests with defined status created by this user
* Allows to change description of any request, the author of which is the currently logged in client
* Allows to cancel any request, the author of which is the currently logged in client
### /request page
* Allows to choose a department from hardcoded list
* Allows to choose a template request for chosen department or create a new request
* Provides an input form to insert your request details
