toy-mongo
=========

### API

#### Database

| Method  | Path                  | Action             |
| ------- | --------------------- | ------------------ |
| POST    | /databases            | create a database  |
| GET     | /databases            | list the databases |
| GET     | /databases/{database} | get the database   |

#### Collection

| Method  | Path                                           | Action              |
| ------- | ---------------------------------------------- | ------------------- |
| POST    | /databases/{database}/collections              | create a collection |
| GET     | /databases/{database}/collections              | list collections in the specified database |
| GET     | /databases/{database}/collections/{collection} | get the collection in the specified database |

#### Document

| Method  | Path                                                               | Action              |
| ------- | ------------------------------------------------------------------ | ------------------- |
| POST    | /databases/{database}/collections/{collection}/documents/insertAll | insert multiple documents at a time |

#### Index

| Method  | Path                                                           | Action          |
| ------- | -------------------------------------------------------------- | --------------- |
| POST    | /databases/{database}/collections/{collection}/indexes         | create an index |
| GET     | /databases/{database}/collections/{collection}/indexes         | list indexes    |
| GET     | /databases/{database}/collections/{collection}/indexes/{index} | get the index   |
