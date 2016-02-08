# --- !Ups

create  table "Users" ("id" SERIAL NOT NULL PRIMARY KEY,"firstName" VARCHAR NOT NULL,"lastName" VARCHAR NOT NULL,"secondName" VARCHAR NOT NULL,"login" VARCHAR NOT NULL,"password" VARCHAR NOT NULL,"gender" INTEGER DEFAULT 0 NOT NULL,"bDay" TIMESTAMP, "roles" VARCHAR NOT NULL);

insert into "Users" ("firstName","lastName", "secondName", "login", "password", "roles") values ('Bunyod', 'Bobojonov', 'Raximbayevich', 'bunyodreal@gmail.com', 'password123', 'ADMIN');

# --- !Downs

drop table "Users";
