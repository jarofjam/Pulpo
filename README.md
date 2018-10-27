## API
### api/user
* Http method GET: to get all records from DB
* Http method POST: to create new record
### api/user/{id}
* Http method GET: to get a record with certain id from DB
* Http method PUT: to update a record with certain id in DB
* Http method DELETE: to remove a record with certain id from DB
 ## Database
### Contains three types of entities - user, role and request
 ## UI
### Allows to manipulate with database in a simple way
* By default shows a list of all DB records
* Provides an input form to create a new user
* Provides ***edit*** button that does not work
* Provides ***delete*** button to delete certain user from DB
### Does not allow to update or get a certain user
 ## In addition
### Three types of Json view for user entity are supported, but not used
### Services are used to separate controllers and DB
 ## A lesson of trickery  
Although the UI does not provide any ability to update or retrieve a specific user, these operations are still available.
* To update a certain user use:
```
fetch(
  '/api/user/{id}', 
  { 
    method: 'PUT', 
    headers: { 'Content-Type': 'application/json' }, 
    body: JSON.stringify({ login: 'login', password: 'password', email: 'email', realName: 'Real Name' })
  }
).then(result => result.json().then(console.log));
```
* To get a certain user use:
```
fetch('/api/user/{id}').then(response => response.json().then(console.log))
```
