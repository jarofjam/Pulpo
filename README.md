## API
### api/[user|role]
* Http method GET: to get all records from DB
* Http method POST: to create new record
### api/[user|role]/{id}
* Http method GET: to get a record with certain id from DB
* Http method PUT: to update a record with certain id in DB
* Http method DELETE: to remove a record with certain id from DB
 ## Database
### Contains three types of entities - user, role and request
#### For now there are no connection inside the DB
 ## UI
### Allows to manipulate with the database in a simple way
* By default shows a list of all[user|role] DB records
* Provides an input form to create a new [user|role]
* Provides ***edit*** button that does not work
* Provides ***delete*** button to delete certain [user|role] from DB
### Does not allow to update or get a certain [user|role]
## Mappings
* / - main page
* /login - custom login page
* /admin - represents admin features
* /request - represents client features
* /registration - temporary mapping; necessary for the application to work on a database without users
 ## A lesson of trickery  
Although the UI does not provide any ability to update or retrieve a specific [user|role], these operations are still available.
* To update a certain user use:
```
fetch(
  '/api/user/{id}', 
  { 
    method: 'PUT', 
    headers: { 'Content-Type': 'application/json' }, 
    body: JSON.stringify({ username: 'username', password: 'password', realName: 'Real Name', department: 'department' })
  }
).then(result => result.json().then(console.log));
```
* To update a certain role use:
```
fetch(
  '/api/role/{id}', 
  { 
    method: 'PUT', 
    headers: { 'Content-Type': 'application/json' }, 
    body: JSON.stringify({ name: 'name', description: 'description' })
  }
).then(result => result.json().then(console.log));
```
* To get a certain [user|role] use:
```
fetch('/api/[user|role]/{id}').then(response => response.json().then(console.log))
```
