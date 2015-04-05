Deploy HowTo
============

Database
========
0. Setup

    CREATE USER kid WITH SUPERUSER PASSWORD 'q1';
    CREATE DATABASE kinodb WITH OWNER = kid;

1. Db access

    pg_ctl -D /usr/local/var/postgres -l /usr/local/var/postgres/server.log start
    psql -d kinodb -U kid

2. Db create

    sbt "run-main kino.db.postgres.LiquiBase create"

3. Db drop create

    sbt "run-main kino.db.postgres.LiquiBase drop create"

4. Db update

    sbt "run-main kino.db.postgres.LiquiBase drop update"
    
Test requests
=============
    curl http://localhost:8080/films
    
    curl -v http://localhost:8080/film/64
    
    * add new film:
        curl -X POST http://localhost:8080/films -H "Content-Type: application/json" -d '{"title": "film37", "year": 1900}'
        
    * find film:
        curl -X GET http://localhost:8080/film -H "Content-Type: application/json" -d '{"title": "film37", "year": 1900}'

    curl -X POST http://localhost:8080/subscriptions -H "Content-Type: application/json" -d '{"name": "sub4", "watchlists": ["w1", "w2"], "searchResources": ["Rutracker", "Kickass"], "emails": ["koj@g.com"], "enabled": true, "scheduleIntervalSeconds": 10}'