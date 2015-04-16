CREATE USER faust WITH PASSWORD 'faustf';
CREATE DATABASE financemanager;
GRANT ALL PRIVILEGES ON DATABASE financemanager TO faust;


create SEQUENCE receipt_picture_data_seq;

create TABLE receipt_picture_data
(
  id NUMERIC(10, 0) not null DEFAULT nextval('receipt_picture_data_seq'),
  user_id text NOT NULL ,
  registration_id text NOT NULL ,
  regNumber_picture text NOT NULL,
  reg_number_picture_extension text  NOT NULL,
  total_cost_picture text NOT NULL,
  total_cost_picture_extension text NOT NULL ,
  state VARCHAR(1) NOT NULL ,
  inserted_at timestamp NOT NULL
);

create SEQUENCE processed_image_result_seq;

create TABLE processed_image_result
(
  id NUMERIC(10, 0) not null DEFAULT nextval('processed_image_result_seq'),
  user_id text NOT NULL ,
  registration_id text NOT NULL ,
  company_reg_number text NOT NULL,
  company_name text  NOT NULL,
  total_cost NUMERIC(10, 2) NOT NULL ,
  state VARCHAR(1) NOT NULL ,
  inserted_at timestamp NOT NULL
);



insert into processed_image_result (id, company_name, company_reg_number, inserted_at, registration_id, state, total_cost, user_id)
values (nextval('processed_image_result_seq'), 'Google', '6627121', to_timestamp('16-05-2014 15:36:38', 'dd-mm-yyyy hh24:mi:ss'), '123', 'A', 11.22, '117808689210678639760');

insert into processed_image_result (id, company_name, company_reg_number, inserted_at, registration_id, state, total_cost, user_id)
values (nextval('processed_image_result_seq'), 'Konsum', '6627122', to_timestamp('17-05-2014 15:36:38', 'dd-mm-yyyy hh24:mi:ss'), '123', 'A', 11.22, '117808689210678639760');

insert into processed_image_result (id, company_name, company_reg_number, inserted_at, registration_id, state, total_cost, user_id)
values (nextval('processed_image_result_seq'), 'R-kiosk', '6627123', to_timestamp('19-05-2014 15:36:38', 'dd-mm-yyyy hh24:mi:ss'), '123', 'A', 11.22, '117808689210678639760');

insert into processed_image_result (id, company_name, company_reg_number, inserted_at, registration_id, state, total_cost, user_id)
values (nextval('processed_image_result_seq'), 'Selver', '6627124', to_timestamp('22-07-2014 15:36:38', 'dd-mm-yyyy hh24:mi:ss'), '123', 'A', 11.22, '117808689210678639760');

insert into processed_image_result (id, company_name, company_reg_number, inserted_at, registration_id, state, total_cost, user_id)
values (nextval('processed_image_result_seq'), 'Apple', '6627125', to_timestamp('13-08-2014 15:36:38', 'dd-mm-yyyy hh24:mi:ss'), '123', 'A', 11.22, '117808689210678639760');

insert into processed_image_result (id, company_name, company_reg_number, inserted_at, registration_id, state, total_cost, user_id)
values (nextval('processed_image_result_seq'), 'IBM', '6627126', to_timestamp('16-08-2014 15:31:38', 'dd-mm-yyyy hh24:mi:ss'), '123', 'A', 11.22, '117808689210678639760');

insert into processed_image_result (id, company_name, company_reg_number, inserted_at, registration_id, state, total_cost, user_id)
values (nextval('processed_image_result_seq'), 'beats', '6627127', to_timestamp('16-10-2014 18:36:38', 'dd-mm-yyyy hh24:mi:ss'), '123', 'A', 11.22, '117808689210678639760');

insert into processed_image_result (id, company_name, company_reg_number, inserted_at, registration_id, state, total_cost, user_id)
values (nextval('processed_image_result_seq'), 'oü õäö', '6627128', to_timestamp('12-12-2014 15:36:38', 'dd-mm-yyyy hh24:mi:ss'), '123', 'A', 11.22, '117808689210678639760');

insert into processed_image_result (id, company_name, company_reg_number, inserted_at, registration_id, state, total_cost, user_id)
values (nextval('processed_image_result_seq'), 'someCompany', '6627129', to_timestamp('02-02-2015 15:36:38', 'dd-mm-yyyy hh24:mi:ss'), '123', 'A', 11.22, '117808689210678639760');

insert into processed_image_result (id, company_name, company_reg_number, inserted_at, registration_id, state, total_cost, user_id)
values (nextval('processed_image_result_seq'), 'Google', '6627121', to_timestamp('03-03-2015 15:36:38', 'dd-mm-yyyy hh24:mi:ss'), '123', 'A', 11.22, '117808689210678639760');

insert into processed_image_result (id, company_name, company_reg_number, inserted_at, registration_id, state, total_cost, user_id)
values (nextval('processed_image_result_seq'), 'Google', '6627121', to_timestamp('15-04-2015 15:36:38', 'dd-mm-yyyy hh24:mi:ss'), '123', 'A', 11.22, '117808689210678639760');