Launch MongoDB Replica Set
==========================

For this lab, you will need a Replica Set with the following configuration:

- Replica Set Name: ``rs0``
- Members: [ localhost:30000, localhost:30001, localhost:30002 ]

You can use ``mlaunch`` to simplify the setup.

```shell
mlaunch --replicaset --name rs0 --port 30000 --dir db
```

Import the item dataset for MongoMart
=====================================

From the console shell, import the item dataset using `mongoimport`:

```shell
$ mongoimport --host rs0/localhost:30000,localhost:30001,localhost:30002 -d mongomart -c item items.json
```

Then from the mongoshell, create a text index on the `item` collection:

```javascript
> use mongomart
> db.item.createIndex( { "title" : "text", "slogan" : "text", "description" : "text" } )
```

Import the store dataset for MongoMart
=====================================

N.B. The store locations dataset is based on Best Buy locations.

From the console shell, import the store dataset using `mongoimport`:

```shell
$ mongoimport --host rs0/localhost:30000,localhost:30001,localhost:30002 -d mongomart -c store stores.json
```

Then from the mongoshell, create some indexes on the `store` collection.

```javascript
> use mongomart
> db.store.createIndex( { "storeId" : 1 }, { "unique": true } );
> db.store.createIndex( { "zip": 1 } );
> db.store.createIndex( { "city": 1 } );
> db.store.createIndex( { "coords": "2dsphere" } );
```

Import the zip dataset for MongoMart
=====================================

From the console shell, import the zip dataset using `mongoimport`:

```shell
$ mongoimport --host rs0/localhost:30000,localhost:30001,localhost:30002 -d mongomart -c zip zips.json
```
