# --- !Ups

ALTER TABLE "Questions"
  ADD COLUMN "subjectId" INTEGER
    CONSTRAINT "questionsFkSubjectId"
    REFERENCES "Subjects" ON UPDATE CASCADE ON DELETE SET NULL,
  ADD COLUMN "themeId" INTEGER
    CONSTRAINT "questionsFkThemeId"
    REFERENCES "Themes" ON UPDATE CASCADE ON DELETE SET NULL;

# --- !Downs

ALTER TABLE "Questions"
  DROP COLUMN "subjectId",
  DROP COLUMN "themeId";
