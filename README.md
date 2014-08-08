toy-mongo
=========

### API

| Method  | Path                              | Action              |
| ------- |-----------------------------------|---------------------|
| POST    | /databases                        | create a database   |
| GET     | /databases                        | list the databases  |
| GET     | /databases/{database}             | get the database    |
| POST    | /databases/{database}/collections | create a collection |
| GET     | /databases/{database}/collections | list collections in the specified database |
| GET     | /databases/{database}/collections/{collection} | get the collection in the specified database |
| POST    | /databases/{database}/collections/{collection}/documents/insertAll | insert multiple documents at a time |
