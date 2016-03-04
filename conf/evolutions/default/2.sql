# --- !Ups

create  table "Subjects" ("id" SERIAL NOT NULL PRIMARY KEY,"name" VARCHAR NOT NULL);

create table "Themes" ("id" SERIAL NOT NULL PRIMARY KEY,
  "subjectId" INTEGER NOT NULL
    CONSTRAINT "themesFkSubjectId"
    REFERENCES "Subjects" ON UPDATE CASCADE ON DELETE CASCADE,
  "name" VARCHAR
  )

# --- !Downs

drop table "Themes";
drop table "Subjects";
