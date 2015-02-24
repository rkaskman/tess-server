create database financeManager;
create user faust with password 'faustf';

CREATE SEQUENCE store_id ;

CREATE TABLE store
( store_id numeric(10,0) NOT NULL DEFAULT nextval('store_id'),
  store_name varchar(200),
  total_sum_tag varchar(100),
  CONSTRAINT store_pk PRIMARY KEY (store_id)
) ;

alter table store add column status char(1);

CREATE SEQUENCE store_receipt_tag_id;

CREATE TABLE store_receipt_tag
( store_receipt_tag_id numeric(10,0) NOT NULL DEFAULT nextval('store_receipt_tag_id'),
  tag varchar(200) not null,
  store_id numeric(10, 0) not null,
  status char(1) not null,
  CONSTRAINT store_receipt_tag PRIMARY KEY (store_receipt_tag_id)
) ;


insert into store (store_name, total_sum_tag, status) values ('Rimi', 'kokku eur', 'A');
insert into store (store_name, total_sum_tag, status) values ('Selver', 'vahesumma', 'A');

insert into store_receipt_tag (tag, store_id, status) values ('pin', 1, 'A');
insert into store_receipt_tag (tag, store_id, status) values ('kontrollitud', 1, 'A');
insert into store_receipt_tag (tag, store_id, status) values ('säilitage', 1, 'A');
insert into store_receipt_tag (tag, store_id, status) values ('väljavõtte', 1, 'A');
insert into store_receipt_tag (tag, store_id, status) values ('kontrolliks', 1, 'A');
insert into store_receipt_tag (tag, store_id, status) values ('teenitud', 1, 'A');
insert into store_receipt_tag (tag, store_id, status) values ('sinu', 1, 'A');
insert into store_receipt_tag (tag, store_id, status) values ('rimi', 1, 'A');
insert into store_receipt_tag (tag, store_id, status) values ('raha', 1, 'A');
insert into store_receipt_tag (tag, store_id, status) values ('saldo', 1, 'A');
insert into store_receipt_tag (tag, store_id, status) values ('kokku', 1, 'A');

insert into store_receipt_tag (tag, store_id, status) values ('vahesumma', 2, 'A');
insert into store_receipt_tag (tag, store_id, status) values ('maksekaart', 2, 'A');
insert into store_receipt_tag (tag, store_id, status) values ('km%', 2, 'A');
insert into store_receipt_tag (tag, store_id, status) values ('neto', 2, 'A');
insert into store_receipt_tag (tag, store_id, status) values ('bruto', 2, 'A');
insert into store_receipt_tag (tag, store_id, status) values ('boonuspunktide', 2, 'A');
insert into store_receipt_tag (tag, store_id, status) values ('jääk', 2, 'A');
insert into store_receipt_tag (tag, store_id, status) values ('tšekk', 2, 'A');
insert into store_receipt_tag (tag, store_id, status) values ('kpv', 2, 'A');
insert into store_receipt_tag (tag, store_id, status) values ('aeg', 2, 'A');
insert into store_receipt_tag (tag, store_id, status) values ('oper', 2, 'A');
insert into store_receipt_tag (tag, store_id, status) values ('kassa', 2, 'A');
