# --- !Ups

create  table "Users" ("id" SERIAL NOT NULL PRIMARY KEY,"firstName" VARCHAR NOT NULL,"lastName" VARCHAR NOT NULL,"secondName" VARCHAR NOT NULL,"login" VARCHAR NOT NULL,"password" VARCHAR NOT NULL,"gender" INTEGER DEFAULT 0 NOT NULL,"bDay" TIMESTAMP, "roles" VARCHAR NOT NULL);

insert into "Users" ("firstName","lastName", "secondName", "login", "password", "roles") values ('Bunyod', 'Bobojonov', 'Raximbayevich', 'bunyodreal@gmail.com', 'password123', 'ADMIN');

create table "Questions" ("id" SERIAL NOT NULL PRIMARY KEY, "question" VARCHAR, "ansA" VARCHAR, "ansB" VARCHAR, "ansC" VARCHAR, "ansD" VARCHAR)

# --- !Downs

drop table "Users";
drop table "Questions";
