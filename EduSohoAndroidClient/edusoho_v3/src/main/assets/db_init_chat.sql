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
    headimgurl varchar(255),
    content varchar(255),
    type varchar,
    delivery integer,
    createdTime integer
);

create table if not exists bulletin(
    id integer,
    content varchar(255),
    createdTime integer
);

