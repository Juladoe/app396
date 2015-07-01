create table if not exists new (
    id integer primary key AutoIncrement,
    title varchar(100),
    content varchar(255),
    createTime integer,
    imgUrl varchar(255),
    unread integer,
    type integer,
    detailId integer,
    isTop integer
);

create table if not exists chat (
    id integer primary key AutoIncrement,
    newId integer,
    fromId integer,
    nickname varchar(100),
    headimgurl varchar(255),
    content varchar(255),
    type integer
);

create table if not exists type (
    id integer primary key AutoIncrement,
    typeName varchar(20)
);

