create table if not exists new (
    id integer primary key AutoIncrement,
    fromId integer,
    title varchar(100),
    content varchar(255),
    createdTime integer,
    imgUrl varchar(255),
    unread integer,
    type varchar,
    belongId integer,
    isTop integer
);

create table if not exists chat (
    chatId integer primary key AutoIncrement,
    id integer ,
    fromId integer,
    toId integer,
    nickname varchar(100),
    headImgUrl varchar(255),
    content varchar(255),
    type varchar,
    delivery integer,
    createdTime integer
);

create table if not exists bulletin(
    id integer,
    content varchar(255),
    schoolDomain varchar(100),
    createdTime integer
);

create table if not exists news_course(
    id integer,
    courseId integer,
    objectId integer,
    title varchar(100),
    content varchar(255),
    fromType  varchar(50),
    bodyType varchar(50),
    lessonType varchar(15),
    userId integer,
    createdTime integer,
    lessonId integer,
    homeworkResultId integer,
    questionId integer,
    learnStartTime integer,
    learnFinishTime integer
);

create table if not exists sp_msg(
    id integer,
    sp_id integer,
    title varchar(100),
    content varchar(255),
    type integer,
    body text,
    createdTime integer,
    toId integer
);

create table if not exists classroom_discuss(
    discussId integer primary key AutoIncrement,
    id integer,
    classroomId integer,
    fromId integer,
    nickname varchar(100),
    headImgUrl varchar(255),
    content varchar(255),
    belongId integer,
    type varchar,
    delivery integer,
    createdTime integer
);

create table if not exists course_discuss(
    discussId integer primary key AutoIncrement,
    id integer,
    courseId integer,
    fromId integer,
    nickname varchar(100),
    headImgUrl varchar(255),
    content varchar(255),
    belongId integer,
    type varchar,
    delivery integer,
    createdTime integer
);

